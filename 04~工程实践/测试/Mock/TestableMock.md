# TestableMock

TestableMock 是基于源码和字节码增强的 Java 单元测试辅助工具，包含以下功能：

- 快速 Mock 任意调用：使被测类的任意方法调用快速替换为 Mock 方法，实现"指哪换哪"，解决传统 Mock 工具使用繁琐的问题。
- 访问被测类私有成员：使单元测试能直接调用和访问被测类的私有成员，解决私有成员初始化和私有方法测试的问题。
- 辅助测试 void 方法：利用 Mock 校验器对方法的内部逻辑进行检查，解决无返回值方法难以实施单元测试的问题。
- 快速构造参数对象：生成任意多层嵌套的对象实例，并简化其内部成员赋值方式，解决被测方法参数初始化代码冗长的问题。

# Hello World

## 依赖配置

在 `build.gradle` 文件中添加 `TestableMock` 依赖：

```groovy
dependencies {
    testImplementation('com.alibaba.testable:testable-all:0.5.2')
    testAnnotationProcessor('com.alibaba.testable:testable-processor:0.5.2')
}
```

然后在测试配置中添加 javaagent：

```groovy
test {
    jvmArgs "-javaagent:${classpath.find { it.name.contains("testable-agent") }.absolutePath}"
}
```

然后相比以往 Mock 工具以类为粒度的 Mock 方式，TestableMock 允许用户直接定义需要 Mock 的单个方法，并遵循约定优于配置的原则，按照规则自动在测试运行时替换被测方法中的指定方法调用。

- Mock 非构造方法，拷贝原方法定义到 Mock 容器类，加 @MockMethod 注解
- Mock 构造方法，拷贝原方法定义到 Mock 容器类，返回值换成构造的类型，方法名随意，加@MockContructor 注解

## 覆写任意类的方法调用

首先为测试类添加一个关联的 Mock 类型，作为承载其 Mock 方法的容器，最简单的做法是在测试类里添加一个名称为 Mock 的静态内部类。例如：

```java
public class DemoTest {

    public static class Mock {
        // 放置 Mock 方法的地方
    }

}
```

在 Mock 容器类中定义一个有@MockMethod 注解的普通方法，使它与需覆写的方法名称、参数、返回值类型完全一致，并在注解的 targetClass 参数指定该方法原本所属对象类型。此时被测类中所有对该需覆写方法的调用，将在单元测试运行时，将自动被替换为对上述自定义 Mock 方法的调用。例如，被测类中有一处"anything".substring(1, 2)调用，我们希望在运行测试的时候将它换成一个固定字符串，则只需在 Mock 容器类定义如下方法：

```java
// 原方法签名为`String substring(int, int)`
// 调用此方法的对象`"anything"`类型为`String`
@MockMethod(targetClass = String.class)
private String substring(int i, int j) {
    return "sub_string";
}
```

当遇到待覆写方法有重名时，可以将需覆写的方法名写到 @MockMethod 注解的 targetMethod 参数里，这样 Mock 方法自身就可以随意命名了。下面这个例子展示了 targetMethod 参数的用法，其效果与上述示例相同：

```java
// 使用`targetMethod`指定需Mock的方法名
// 此方法本身现在可以随意命名，但方法参数依然需要遵循相同的匹配规则
@MockMethod(targetClass = String.class, targetMethod = "substring")
private String use_any_mock_method_name(int i, int j) {
    return "sub_string";
}
```

有时，在 Mock 方法里会需要访问发起调用的原始对象中的成员变量，或是调用原始对象的其他方法。此时，可以将 @MockMethod 注解中的 targetClass 参数去除，然后在方法参数列表首位增加一个类型为该方法原本所属对象类型的参数。TestableMock 约定，当@MockMethod 注解的 targetClass 参数值为空时，Mock 方法的首位参数即为目标方法所属类型，参数名称随意。通常为了便于代码阅读，建议将此参数统一命名为 self 或 src。举例如下：

```java
// Mock方法在参数列表首位增加一个类型为`String`的参数（名字随意）
// 此参数可用于获得当时的实际调用者的值和上下文
@MockMethod
private String substring(String self, int i, int j) {
    // 可以直接调用原方法，此时Mock方法仅用于记录调用，常见于对void方法的测试
    return self.substring(i, j);
}
```

## 覆写被测类自身的成员方法

有时候，在对某些方法进行测试时，希望将被测类自身的另外一些成员方法 Mock 掉（比如这个方法里有许多外部依赖或耗时操作）。做法与前一种情况完全相同，只需将 targetClass 参数赋值为被测类，即可实现对被测类自身（不论是公有或私有）成员方法的覆写。例如，被测类中有一个签名为 String innerFunc(String)的私有方法，我们希望在测试的时候将它替换掉，则只需在 Mock 容器类定义如下方法：

```java
// 被测类型是`DemoMock`
@MockMethod(targetClass = DemoMock.class)
private String innerFunc(String text) {
    return "mock_" + text;
}
```

同样的，上述示例中的方法如需访问发起调用的原始被测对象，也可不使用 targetClass 参数，而是在定义 Mock 方法时，在方法参数列表首位加一个类型为 DemoMock 的参数（名字随意）。

## 覆写任意类的静态方法

对于静态方法的 Mock 与普通方法相同。例如，在被测类中调用了`BlackBox`类型中的静态方法`secretBox()`，该方法签名为`BlackBox secretBox()`，则 Mock 方法如下：

```java
@MockMethod(targetClass = BlackBox.class)
private BlackBox secretBox() {
    return new BlackBox("not_secret_box");
}
```

对于静态方法的 Mock，通常不使用方法参数列表的首位加参数来表示目标类型。但这种方法也依然适用，只是实际传入的第一个参数值将始终是`null`。

## 覆写任意类的 new 操作

在 Mock 容器类里定义一个返回值类型为要被创建的对象类型，且方法参数与要 Mock 的构造函数参数完全一致的方法，名称随意，然后加上`@MockContructor`注解。此时被测类中所有用`new`创建指定类的操作（并使用了与 Mock 方法参数一致的构造函数）将被替换为对该自定义方法的调用。

例如，在被测类中有一处`new BlackBox("something")`调用，希望在测试时将它换掉（通常是换成 Mock 对象，或换成使用测试参数创建的临时对象），则只需定义如下 Mock 方法：

```java
// 要覆写的构造函数签名为`BlackBox(String)`
// Mock方法返回`BlackBox`类型对象，方法的名称随意起
@MockContructor
private BlackBox createBlackBox(String text) {
    return new BlackBox("mock_" + text);
}
```

## 在 Mock 方法中区分调用来源

在 Mock 方法中通过 TestableTool.SOURCE_METHOD 变量可以识别进入该 Mock 方法前的被测类方法名称；此外，还可以借助 TestableTool.MOCK_CONTEXT 变量为 Mock 方法注入“额外的上下文参数”，从而区分处理不同的调用场景。例如，在测试用例中验证当被 Mock 方法返回不同结果时，对被测目标方法的影响：
例如，在测试用例中验证当被 Mock 方法返回不同结果时，对被测目标方法的影响：

```java
@Test
public void testDemo() {
    MOCK_CONTEXT.put("case", "data-ready");
    assertEquals(true, demo());
    MOCK_CONTEXT.put("case", "has-error");
    assertEquals(false, demo());
}
```

在 Mock 方法中取出注入的参数，根据情况返回不同结果：

```java
@MockMethod
private Data mockDemo() {
    switch((String)MOCK_CONTEXT.get("case")) {
        case "data-ready":
            return new Data();
        case "has-error":
            throw new NetworkException();
        default:
            return null;
    }
}
```
