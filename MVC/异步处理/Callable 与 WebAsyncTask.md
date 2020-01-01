# Callable

```java
@ResponseBody
@GetMapping("/callable")
public Callable<String> helloGetCallable() throws Exception {
System.out.println(Thread.currentThread().getName() + " main thread start");

Callable<String> callable =
    () -> {
        System.out.println(Thread.currentThread().getName() + " child thread start");
        TimeUnit.SECONDS.sleep(25); // 模拟处理业务逻辑，花费了5秒钟
        System.out.println(Thread.currentThread().getName() + " child thread end");

        // 这里稍微小细节一下：最终返回的不是Callable对象，而是它里面的内容
        return "hello world";
    };

System.out.println(Thread.currentThread().getName() + " main thread end");
return callable;
}

/**
http-nio-8080-exec-1 main thread start
http-nio-8080-exec-1 main thread end
task-1 child thread start
task-1 child thread end
*/
```

# WebAsyncTask

Spring 官方推荐如果我们需要超时处理的回调或者错误处理的回调，我们可以使用 WebAsyncTask 代替 Callable。

```java
@ResponseBody
@GetMapping("/async_task")
public WebAsyncTask<String> helloGetWebAsyncTask() throws Exception {
System.out.println(Thread.currentThread().getName() + " main thread start");

Callable<String> callable =
    () -> {
        System.out.println(Thread.currentThread().getName() + " child thread start");

        if (Math.random() < 0.5) {
        throw new Exception("Exception Response");
        }

        TimeUnit.SECONDS.sleep(600); // 模拟处理业务逻辑，话费了5秒钟
        System.out.println(Thread.currentThread().getName() + " child thread end");

        return "hello world";
    };

// 采用WebAsyncTask 返回 这样可以处理超时和错误 同时也可以指定使用的Excutor名称
WebAsyncTask<String> webAsyncTask = new WebAsyncTask<>(3000, callable);
// 注意：onCompletion表示完成，不管你是否超时、是否抛出异常，这个函数都会执行的
webAsyncTask.onCompletion(() -> System.out.println("Completion"));

// 这两个返回的内容，最终都会放进response里面去===========
webAsyncTask.onTimeout(() -> "Timeout");

// 备注：这个是Spring5新增的
webAsyncTask.onError(() -> "Exception");

System.out.println(Thread.currentThread().getName() + " main thread end");
return webAsyncTask;
}
```

WebAsyncTask 的源码如下：

```java
public class WebAsyncTask<V> implements BeanFactoryAware {

	// 正常执行的函数（通过WebAsyncTask的构造函数可以传进来）
	private final Callable<V> callable;
	// 处理超时时间（ms），可通过构造函数指定，也可以不指定（不会有超时处理）
	private Long timeout;
	// 执行任务的执行器。可以构造函数设置进来，手动指定。
	private AsyncTaskExecutor executor;
	// 若设置了，会根据此名称去IoC容器里找这个Bean （和上面二选一）
	// 若传了executorName,请务必调用set方法设置beanFactory
	private String executorName;
	private BeanFactory beanFactory;

	// 超时的回调
	private Callable<V> timeoutCallback;
	// 发生错误的回调
	private Callable<V> errorCallback;
	// 完成的回调（不管超时还是错误都会执行）
	private Runnable completionCallback;

	...

	// 这是获取执行器的逻辑
	@Nullable
	public AsyncTaskExecutor getExecutor() {
		if (this.executor != null) {
			return this.executor;
		} else if (this.executorName != null) {
			Assert.state(this.beanFactory != null, "BeanFactory is required to look up an executor bean by name");
			return this.beanFactory.getBean(this.executorName, AsyncTaskExecutor.class);
		} else {
			return null;
		}
	}


	public void onTimeout(Callable<V> callback) {
		this.timeoutCallback = callback;
	}
	public void onError(Callable<V> callback) {
		this.errorCallback = callback;
	}
	public void onCompletion(Runnable callback) {
		this.completionCallback = callback;
	}

	// 最终执行超时回调、错误回调、完成回调都是通过这个拦截器实现的
	CallableProcessingInterceptor getInterceptor() {
		return new CallableProcessingInterceptor() {
			@Override
			public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
				return (timeoutCallback != null ? timeoutCallback.call() : CallableProcessingInterceptor.RESULT_NONE);
			}
			@Override
			public <T> Object handleError(NativeWebRequest request, Callable<T> task, Throwable t) throws Exception {
				return (errorCallback != null ? errorCallback.call() : CallableProcessingInterceptor.RESULT_NONE);
			}
			@Override
			public <T> void afterCompletion(NativeWebRequest request, Callable<T> task) throws Exception {
				if (completionCallback != null) {
					completionCallback.run();
				}
			}
		};
	}

}
```

WebAsyncTask 的异步编程 API，相比于 @Async 注解，WebAsyncTask 提供更加健全的 超时处理 和 异常处理 支持。但是 @Async 也有更优秀的地方，就是他不仅仅能用于 Controller 中，而是可以用在任何地方。

# WebMvcConfigurerAdapter

```java
@Configuration
public class RequestAsyncPoolConfig extends WebMvcConfigurerAdapter {

	@Resource
	private ThreadPoolTaskExecutor myThreadPoolTaskExecutor;

	@Override
	public void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
		//处理 callable超时
		configurer.setDefaultTimeout(60*1000);
		configurer.setTaskExecutor(myThreadPoolTaskExecutor);
		configurer.registerCallableInterceptors(timeoutCallableProcessingInterceptor());
	}

	@Bean
	public TimeoutCallableProcessingInterceptor timeoutCallableProcessingInterceptor() {
		return new TimeoutCallableProcessingInterceptor();
	}
}
```
