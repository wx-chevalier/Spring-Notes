# DeferredResult

一旦在 Servlet 容器中启用了异步请求处理功能，控制器方法就可以使用 DeferredResult 包装任何支持的控制器方法返回值。DeferredResult 使用方式与 Callable 类似，但在返回结果上不一样，它返回的时候实际结果可能没有生成，实际的结果可能会在另外的线程里面设置到 DeferredResult 中去。这个特性非常非常的重要，对后面实现复杂的功能（比如服务端推技术、订单过期时间处理、长轮询、模拟 MQ 的功能等等高级应用）

```java
@GetMapping("/quotes")
@ResponseBody
public DeferredResult<String> quotes() {
    DeferredResult<String> deferredResult = new DeferredResult<String>();
    // Save the deferredResult somewhere..
    return deferredResult;
}

// From some other thread...
deferredResult.setResult(data);
```

控制器可以从不同的线程异步生成返回值，例如，响应外部事件（JMS 消息），计划任务或其他事件等。

```java
@RequestMapping("/getAMessageFutureAsync")
public DeferredResult<Message> getAMessageFutureAsync() {
    DeferredResult<Message> deffered = new DeferredResult<>(90000);
    CompletableFuture<Message> f = this.service1.getAMessageFuture();
    f.whenComplete((res, ex) -> {
        if (ex != null) {
            deffered.setErrorResult(ex);
        } else {
            deffered.setResult(res);
        }
    });
    return deffered;
}

public CompletableFuture<Message> getAMessageFuture() {
    return CompletableFuture.supplyAsync(() -> {
        logger.info("Start: Executing slow task in Service 1");
        Util.delay(1000);
        logger.info("End: Executing slow task in Service 1");
        return new Message("data 1");
    }, futureExecutor);
}
```

# 源码分析

```java
public class DeferredResult<T> {

	private static final Object RESULT_NONE = new Object()


	// 超时时间（ms）可以不配置
	@Nullable
	private final Long timeout;
	// 相当于超时的话的，传给回调函数的值
	private final Object timeoutResult;

	// 这三种回调也都是支持的
	private Runnable timeoutCallback;
	private Consumer<Throwable> errorCallback;
	private Runnable completionCallback;


	// 这个比较强大，就是能把我们结果再交给这个自定义的函数处理了 他是个@FunctionalInterface
	private DeferredResultHandler resultHandler;

	private volatile Object result = RESULT_NONE;
	private volatile boolean expired = false;


	// 判断这个DeferredResult是否已经被set过了（被set过的对象，就可以移除了嘛）
	// 如果expired表示已经过期了你还没set，也是返回false的
	// Spring4.0之后提供的
	public final boolean isSetOrExpired() {
		return (this.result != RESULT_NONE || this.expired);
	}

	// 没有isSetOrExpired 强大，建议使用上面那个
	public boolean hasResult() {
		return (this.result != RESULT_NONE);
	}

	// 还可以获得set进去的结果
	@Nullable
	public Object getResult() {
		Object resultToCheck = this.result;
		return (resultToCheck != RESULT_NONE ? resultToCheck : null);
	}


	public void onTimeout(Runnable callback) {
		this.timeoutCallback = callback;
	}
	public void onError(Consumer<Throwable> callback) {
		this.errorCallback = callback;
	}
	public void onCompletion(Runnable callback) {
		this.completionCallback = callback;
	}


	// 如果你的result还需要处理，可以这是一个resultHandler，会对你设置进去的结果进行处理
	public final void setResultHandler(DeferredResultHandler resultHandler) {
		Assert.notNull(resultHandler, "DeferredResultHandler is required");
		// Immediate expiration check outside of the result lock
		if (this.expired) {
			return;
		}
		Object resultToHandle;
		synchronized (this) {
			// Got the lock in the meantime: double-check expiration status
			if (this.expired) {
				return;
			}
			resultToHandle = this.result;
			if (resultToHandle == RESULT_NONE) {
				// No result yet: store handler for processing once it comes in
				this.resultHandler = resultHandler;
				return;
			}
		}
		try {
			resultHandler.handleResult(resultToHandle);
		} catch (Throwable ex) {
			logger.debug("Failed to handle existing result", ex);
		}
	}

	// 我们发现，这里调用是private方法setResultInternal，我们设置进来的结果result，会经过它的处理
	// 而它的处理逻辑也很简单，如果我们提供了resultHandler，它会把这个值进一步的交给我们的resultHandler处理
	// 若我们没有提供此resultHandler，那就保存下这个result即可
	public boolean setResult(T result) {
		return setResultInternal(result);
	}

	private boolean setResultInternal(Object result) {
		// Immediate expiration check outside of the result lock
		if (isSetOrExpired()) {
			return false;
		}
		DeferredResultHandler resultHandlerToUse;
		synchronized (this) {
			// Got the lock in the meantime: double-check expiration status
			if (isSetOrExpired()) {
				return false;
			}
			// At this point, we got a new result to process
			this.result = result;
			resultHandlerToUse = this.resultHandler;
			if (resultHandlerToUse == null) {
				this.resultHandler = null;
			}
		}
		resultHandlerToUse.handleResult(result);
		return true;
	}

	// 发生错误了，也可以设置一个值。这个result会被记下来，当作result
	// 注意这个和setResult的唯一区别，这里入参是Object类型，而setResult只能set规定的指定类型
	// 定义成Obj是有原因的：因为我们一般会把Exception等异常对象放进来。。。
	public boolean setErrorResult(Object result) {
		return setResultInternal(result);
	}

	// 拦截器 注意最终finally里面，都可能会调用我们的自己的处理器resultHandler(若存在的话)
	// afterCompletion不会调用resultHandler~~~~~~~~~~~~~
	final DeferredResultProcessingInterceptor getInterceptor() {
		return new DeferredResultProcessingInterceptor() {
			@Override
			public <S> boolean handleTimeout(NativeWebRequest request, DeferredResult<S> deferredResult) {
				boolean continueProcessing = true;
				try {
					if (timeoutCallback != null) {
						timeoutCallback.run();
					}
				} finally {
					if (timeoutResult != RESULT_NONE) {
						continueProcessing = false;
						try {
							setResultInternal(timeoutResult);
						} catch (Throwable ex) {
							logger.debug("Failed to handle timeout result", ex);
						}
					}
				}
				return continueProcessing;
			}
			@Override
			public <S> boolean handleError(NativeWebRequest request, DeferredResult<S> deferredResult, Throwable t) {
				try {
					if (errorCallback != null) {
						errorCallback.accept(t);
					}
				} finally {
					try {
						setResultInternal(t);
					} catch (Throwable ex) {
						logger.debug("Failed to handle error result", ex);
					}
				}
				return false;
			}
			@Override
			public <S> void afterCompletion(NativeWebRequest request, DeferredResult<S> deferredResult) {
				expired = true;
				if (completionCallback != null) {
					completionCallback.run();
				}
			}
		};
	}

	// 内部函数式接口 DeferredResultHandler
	@FunctionalInterface
	public interface DeferredResultHandler {
		void handleResult(Object result);
	}

}
```

DeferredResult 的超时处理，采用委托机制，也就是在实例 DeferredResult 时给予一个超时时长（毫秒），同时在 onTimeout 中委托（传入）一个新的处理线程（我们可以认为是超时线程）；当超时时间到来，DeferredResult 启动超时线程，超时线程处理业务，封装返回数据，给 DeferredResult 赋值（正确返回的或错误返回的）
