# Mockito

Mockito 是用于生成模拟对象或者直接点说，就是”假对象“的工具。两者定位不同，所以一般通常的做法就是联合 JUnit 与 Mockito 来进行测试。

```java
List mock = mock(List.class);
when( mock.get(0) ).thenReturn( 1 );
assertEquals( "预期返回1", 1, mock.get( 0 ) );// mock.get(0) 返回 1
```

其中 mock 是模拟 List 的对象，拥有 List 的所有方法和属性。when(xxxx).thenReturn(yyyy); 是指定当执行了这个方法的时候，返回 thenReturn 的值，相当于是对模拟对象的配置过程，为某些条件给定一个预期的返回值。相信通过这个简单的例子你可以明白所谓 Mock 便是这么一回事。

我们看到 List 为 Java.util.List 是接口，并不是实现类，但这不妨碍我们使用它作为我们的“打桩”对象，——当然你也可以使用实现类，传入 mock(obj) 方法中。这里提到的是"打桩(Stub，也有人称其为“存根”)"的概念，是一个形象的说法，就是把所需的测试数据塞进对象中，适用于基于状态的(state-based)测试，关注的是输入和输出。Mockito 中 when(…).thenReturn(…) 这样的语法来定义对象方法和参数(输入)，然后在 thenReturn 中指定结果(输出)。此过程称为 Stub 打桩。一旦这个方法被 stub 了，就会一直返回这个 stub 的值。

打桩需要[注意以下几点](http://qiuguo0205.iteye.com/blog/1443344)：

- 对于 static 和 final 方法， Mockito 无法对其 when(…).thenReturn(…) 操作。
- 当我们连续两次为同一个方法使用 stub 的时候，他只会只用最新的一次。

mock 对象会覆盖整个被 mock 的对象，因此没有 stub 的方法只能返回默认值。又因为，我们 mock 一个接口的时候，很多成员方法只是一个签名，并没有实现，这就要我们手动写出这些实现方法啦。典型地，我们模拟一个 request 请求对象，你被测试的代码中使用了 HttpSerevletRequest 什么方法，就要写出相应的实现方法！

```
HttpServletRequest request = mock(HttpServletRequest.class);
when(request.getParameter("foo")).thenReturn("boo");
```

这里“打桩”之后，我们执行 request.getParamter("foo") 就会返回 boo，如果不这样设定，Mockito 就会返回默认的 null，也不会报错说这个方法找不到。mock 实例默认的会给所有的方法添加基本实现：返回 null 或空集合，或者 0 等基本类型的值。这取决于方法返回类型，如 int 会返回 0，布尔值返回 false。对于其他 type 会返回 null。

```java
// 第一种方式
when(i.next()).thenReturn("Hello").thenReturn("World");
// 第二种方式
when(i.next()).thenReturn("Hello", "World");
// 第三种方式，都是等价的
when(i.next()).thenReturn("Hello");
when(i.next()).thenReturn("World");
```

第一次调用 i.next() 将返回 ”Hello”，第二次的调用会返回 ”World”。
上述我们一直在讨论被测试的方法都有返回值的，那么没有返回值的 void 方法呢？也是测试吗？答案是肯定的。——只不过 Mockito 要求你的写法上有不同，因为都没返回值了，调用 thenReturn(xxx) 肯定不行，取而代之的写法是，

```
doNothing().when(obj).notify();
// 或直接
when(obj).notify();
```

Mockito 还能对被测试的方法强行抛出异常，

```java
when(i.next()).thenThrow(new RuntimeException());
doThrow(new RuntimeException()).when(i).remove(); // void 方法的
// 迭代风格
doNothing().doThrow(new RuntimeException()).when(i).remove(); // 第一次调用 remove 方法什么都不做，第二次调用抛出 RuntimeException 异常。
```

## 模拟传入参数 argument matchers

```
when(request.getParameter("foo")).thenReturn("boo");
```

这里 getParameter("foo") 这里我们是写死参数 foo 的，但是如果我不关心输入的具体内容，可以吗？可以的，最好能像正则表达式那样，/w+ 表示任意字符串是不是很方便，不用考虑具体什么参数，只要是 字符串 型的参数，就可以打桩。如此方便的想法 Mockito 也考虑到了，提供 argument matchers 机制，例如 anyString() 匹配任何 String 参数，anyInt() 匹配任何 int 参数，anySet() 匹配任何 Set，any() 则意味着参数为任意值。例子如下，

```
when(mockedList.get(anyInt())).thenReturn("element");
System.out.println(mockedList.get(999));// 此时打印是 element
```

再进一步，自定义类型也可以，如 any(User.class)，另，参见[《学习 Mockito - 自定义参数匹配器》](http://hotdog.iteye.com/blog/911584) 和 [这里](http://blog.sina.com.cn/s/blog_6176c38201014jrk.html) 和 [这里](http://blog.csdn.net/onlyqi/article/details/6544989)。

### 获取返回的结果

一个问题，thenReturn 是返回结果是我们写死的。如果要让被测试的方法不写死，返回实际结果并让我们可以获取到的——怎么做呢？有时我们需要自定义方法执行的返回结果，Answer 接口就是满足这样的需求而存在的。
例如模拟常见的 request.getAttribute(key)，由于这本来是个接口，所以连内部实现都要自己写了。此次通过 Answer 接口获取参数内容。

```java
final Map<String, Object> hash = new HashMap<String, Object>();
Answer<String> aswser = new Answer<String>() {
    public String answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        return hash.get(args[0].toString()).toString();
    }
};

when(request.getAttribute("isRawOutput")).thenReturn(true);
when(request.getAttribute("errMsg")).thenAnswer(aswser);
when(request.getAttribute("msg")).thenAnswer(aswser);
```

利用 InvocationOnMock 提供的方法可以获取 mock 方法的调用信息。下面是它提供的方法：

- getArguments() 调用后会以 Object 数组的方式返回 mock 方法调用的参数。
- getMethod() 返回 java.lang.reflect.Method 对象
- getMock() 返回 mock 对象
- callRealMethod() 真实方法调用，如果 mock 的是接口它将会抛出异常

void 方法可以获取参数，只是写法上有区别，

```
doAnswer(new Answer<Object>() {
    public Object answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        // Object mock = invocation.getMock();
        System.out.println(args[1]);
        hash.put(args[0].toString(), args[1]);
        return "called with arguments: " + args;
    }
}).when(request).setAttribute(anyString(), anyString());


```

## 验证 Verify

前面提到的 when(……).thenReturn(……) 属于状态测试，某些时候，测试不关心返回结果，而是侧重方法有否被正确的参数调用过，这时候就应该使用 验证方法了。从概念上讲，就是和状态测试所不同的“行为测试”了。
一旦使用 mock() 对模拟对象打桩，意味着 Mockito 会记录着这个模拟对象调用了什么方法，还有调用了多少次。最后由用户决定是否需要进行验证，即 verify() 方法。
verify() 说明其作用的例子，

```

mockedList.add("one");
mockedList.add("two");
verify(mockedList).add("one"); // 如果times不传入，则默认是1


```

verify 内部跟踪了所有的方法调用和参数的调用情况，然后会返回一个结果，说明是否通过。参见另外一个详细的例子。

```
Map mock = Mockito.mock( Map.class );
when( mock.get( "city" ) ).thenReturn( "广州" );
// 关注参数有否传入
verify(mock).get( Matchers.eq( "city" ) );
// 关注调用的次数
verify(mock, times( 2 ));
```

也就是说，这是对历史记录作一种回溯校验的处理。

这里补充一个学究的问题，所谓 Mock 与 Stub 打桩，其实它们之间不能互为其表。但 Mockito 语境中则 [Stub 和 Mock 对象同时使用的](http://stamen.iteye.com/blog/1470066)。因为它既可以设置方法调用返回值，又可以验证方法的调用。有关 stub 和 mock 的详细论述请见 Martin Fowler 大叔的文章[《Mocks Aren't Stub》](http://martinfowler.com/articles/mocksArentStubs.html)。

Mockito 除了提供 times(N) 方法供我们调用外，还提供了很多可选的方法：

- never() 没有被调用，相当于 times(0)
- atLeast(N) 至少被调用 N 次
- atLeastOnce() 相当于 atLeast(1)
- atMost(N) 最多被调用 N 次

verify 也可以像 when 那样使用模拟参数，若方法中的某一个参数使用了 matcher，则所有的参数都必须使用 matcher。

```
// correct
verify(mock).someMethod(anyInt(), anyString(), eq("third argument"));
// will throw exception
verify(mock).someMethod(anyInt(), anyString(), "third argument");
```

在最后的验证时如果只输入字符串”hello”是会报错的，必须使用 Matchers 类内建的 eq 方法。

```
Map mapMock = mock(Map.class);
when(mapMock.put(anyInt(), anyString())).thenReturn("world");
mapMock.put(1, "hello");
verify(mapMock).put(anyInt(), eq("hello"));


```

其他高级用法，详见[《学习 Mockito - Mock 对象的行为验证》](http://hotdog.iteye.com/blog/908827)，主要特性如下，

- 参数验证，详见[《利用 ArgumentCaptor(参数捕获器)捕获方法参数进行验证》](http://hotdog.iteye.com/blog/916364)
- 超时验证，通过 timeout，并制定毫秒数验证超时。注意，如果被调用多次，times 还是需要的。
- 方法调用顺序 通过 InOrder 对象，验证方法的执行顺序，如上例子中，如果 mock 的 get(0) 和 get(1) 方法反过来则测试不通过。这里 mock2 其实没有被调用过。所以不需要些。
- verifyNoMoreInteractions 查询是否存在被调用，但未被验证的方法，如果存在则抛出异常。这里因为验证了 get(anyInt())，相当于所有的 get 方法被验证，所以通过。
- verifyZeroInteractions 查询对象是否未产生交互，如果传入 的 mock 对象的方法被调用过，则抛出异常。这里 mock2 的方法没有被调用过，所有通过。

参见[《用 mockito 的 verify 来验证 mock 的方法是否被调用》](http://blog.sina.com.cn/s/blog_6176c38201014lzc.html)：

> 看 mockito 的 api 时，一直都不清楚 veriry()这个方法的作用，因为如果我 mock 了某个方法，肯定是为了调用的啊。直到今天在回归接口测试用例的时候，发现有两个用例，用例 2 比用例 1 多了一个 mock 的步骤，不过最后的结果输出是一样的。由于代码做了修改，我重新 mock 后，其实用例 2 中对于的步骤是不会执行的，可测试还是通过了。仔细查看后，发现 mock 的方法没有被调用，所以用例 2 和用例 1 就变成一样的了。于是，就产生了这么个需求：单单通过结果来判断正确与否还是不够的，我还要判断是否按我指定的路径执行的用例。到这里，终于领略到了 mockito 的 verify 的强大威力，以下是示例代码：
>
> 若调用成功，则程序正常运行，反之则会报告: Wanted but not invoked:verify(mockedList).add("one"); 错误。

感觉 verify 会用的比较少。

## Spy

spy 的意思是你可以修改某个真实对象的某些方法的行为特征，而不改变他的基本行为特征，这种策略的使用跟 AOP 有点类似。下面举官方的例子来说明：

```java
List list = new LinkedList();
List spy = spy(list);

//optionally, you can stub out some methods:
when(spy.size()).thenReturn(100);

//using the spy calls <b>real</b> methods
spy.add("one");
spy.add("two");

//prints "one" - the first element of a list
System.out.println(spy.get(0));

//size() method was stubbed - 100 is printed
System.out.println(spy.size());

//optionally, you can verify
verify(spy).add("one");
verify(spy).add("two");
```

可以看到 spy 保留了 list 的大部分功能，只是将它的 size() 方法改写了。不过 spy 在使用的时候有很多地方需要注意，一不小心就会导致问题，所以不到万不得已还是不要用 spy。

```java
@Test
public void save() {
    User user = new User();
    user.setLoginName("admin");
    // 第一次调用findUserByLoginName返回user 第二次调用返回null
    when(mockUserDao.findUserByLoginName(anyString())).thenReturn(user).thenReturn(null);
    try {
        // 测试如果重名会抛出异常
        userService.save(user);
        // 如果没有抛出异常测试不通过
        failBecauseExceptionWasNotThrown(RuntimeException.class);
    } catch (ServiceException se) {
    }
    verify(mockUserDao).findUserByLoginName("admin");

    // userService.save(user);
    user.setPassword("123456");
    String userId = userService.save(user);
    // 断言返回结果
    assertThat(userId).isNotEmpty().hasSize(32);
    verify(mockUserDao, times(2)).findUserByLoginName(anyString());
    verify(mockUserDao).save(any(User.class));
}

@Test
public void save2() {
    User user = new User();
    user.setLoginName("admin");
    user.setPassword("123456");
    userService.save(user);

    // 通过ArgumentCaptor(参数捕获器) 对传入参数进行验证
    ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
    verify(mockUserDao).save(argument.capture());
    assertThat("admin").isEqualTo(argument.getValue().getLoginName());

    // stub 调用save方法时抛出异常
    doThrow(new ServiceException("测试抛出异常")).when(mockUserDao).save(any(User.class));
    try {
        userService.save(user);
        failBecauseExceptionWasNotThrown(RuntimeException.class);
    } catch (ServiceException se) {
    }
}
```

## 模拟 Servlet

[JUnit + Mockito 单元测试(三)](http://blog.csdn.net/zhangxin09/article/details/42487319)

| FEATURE                                              | JUNIT 4        | JUNIT 5        |
| ---------------------------------------------------- | -------------- | -------------- |
| Declare a test method                                | `@Test`        | `@Test`        |
| Execute before all test methods in the current class | `@BeforeClass` | `@BeforeAll`   |
| Execute after all test methods in the current class  | `@AfterClass`  | `@AfterAll`    |
| Execute before each test method                      | `@Before`      | `@BeforeEach`  |
| Execute after each test method                       | `@After`       | `@AfterEach`   |
| Disable a test method / class                        | `@Ignore`      | `@Disabled`    |
| Test factory for dynamic tests                       | NA             | `@TestFactory` |
| Nested tests                                         | NA             | `@Nested`      |
| Tagging and filtering                                | `@Category`    | `@Tag`         |
| Register custom extensions                           | NA             | `@ExtendWith`  |
