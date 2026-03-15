# JUnit 5

JUnit 是 Java 中使用最广泛的测试框架，JUnit5 主要在希望能够适应 Java8 风格的编码以及相关工，这就是为什么建议在 Java 8 之后的项目中使用 JUnit5 来创建和执行测试。JUnit5 的第一个可用性版本是在 2017 年 9 月 10 日发布的。`JUnit 5 = JUnit Platform + JUnit Jupiter + JUnit Vintage`：

- JUnit Platform: 启动 Junit 测试、IDE、构建工具或插件都需要包含和扩展 Platform API，它定义了 TestEngine 在平台运行的新测试框架的 API。它还提供了一个控制台启动器，可以从命令行启动 Platform，为 Gradle 和 Maven 插件提供支持。
- JUnit Jupiter: 它用于编写测试代码的新的编程和扩展模型。它具有所有新的 Junit 注释和 TestEngine 实现来运行这些注释编写的测试。
- JUnit Vintage: 它主要的目的是支持在 JUnit5 的测试代码中运行 JUnit3 和 4 方式写的测试，它能够向前兼容之前的测试代码。

![JUnit 5 平台构成](https://ngte-superbed.oss-cn-beijing.aliyuncs.com/item/20230416204449.png)

# 快速开始

使用 JUnit 5 的时候需要在 Maven 添加对应的依赖：

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>${junit.jupiter.version}</version>
</dependency>
<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-runner</artifactId>
    <version>${junit.platform.version}</version>
    <scope>test</scope>
</dependency>
```

或者在 Gradle 中添加如下的依赖：

```groovy
testRuntime("org.junit.jupiter:junit-jupiter-engine:5.0.0-M4")
testRuntime("org.junit.platform:junit-platform-runner:1.0.0-M4")
```

> Tips: 无论是单元测试，还是集成测试，我们都必须小心地控制代码的边界，避免耦合带来的意外失败，并提高用例地运行效率。

# Links

- https://www.baeldung.com/spring-boot-testing
