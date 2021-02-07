# Spring Boot

从 2002 年开始，Spring 一直在飞速的发展，如今已经成为了在 Java EE（Java Enterprise Edition）开发中真正意义上的标准，但是随着技术的发展，Java EE 使用 Spring 逐渐变得笨重起来，大量的 XML 文件存在于项目之中。繁琐的配置，整合第三方框架的配置问题，导致了开发和部署效率的降低。2012 年 10 月，Mike Youngstrom 在 Spring jira 中创建了一个功能请求，要求在 Spring 框架中支持无容器 Web 应用程序体系结构。他谈到了在主容器引导 spring 容器内配置 Web 容器服务。这是 jira 请求的摘录：

> 我认为 Spring 的 Web 应用体系结构可以大大简化，如果它提供了从上到下利用 Spring 组件和配置模型的工具和参考体系结构。在简单的 main()方法引导的 Spring 容器内嵌入和统一这些常用 Web 容器服务的配置。

这一要求促使了 2013 年初开始的 Spring Boot 项目的研发，到今天，Spring Boot 的版本已经到了 2.0.3 RELEASE。Spring Boot 并不是用来替代 Spring 的解决方案，而是和 Spring 框架紧密结合用于提升 Spring 开发者体验的工具。它集成了大量常用的第三方库配置，Spring Boot 应用中这些第三方库几乎可以是零配置的开箱即用（out-of-the-box），大部分的 Spring Boot 应用都只需要非常少量的配置代码（基于 Java 的配置），开发者能够更加专注于业务逻辑。

# Spring Boot 定位

Spring 的官方网站，可以看到下图：

![Spring: the source for modern java](https://s3.ax1x.com/2021/02/07/yt0agU.png)

我们可以看到图中官方对 Spring Boot 的定位：Build Anything， Build 任何东西。Spring Boot 旨在尽可能快地启动和运行，并且只需最少的 Spring 前期配置。 同时我们也来看一下官方对后面两个的定位：

- SpringCloud：Coordinate Anything，协调任何事情；
- SpringCloud Data Flow：Connect everything，连接任何东西。

## Spring Boot 与 SSM

SSM 是 Spring IoC、Spring MVC、Mybatis 的组合。SSM 限定死了你只能开发 Java Web 应用，而且 MVC 框架必须用 Spring MVC，持久层必须用 Mybatis。Spring Boot 没有和任何 MVC 框架绑定，没有和任何持久层框架绑定，没有和任何其他业务领域的框架绑定。开发 Web 应用可以用 Spring Boot。用 spring-boot-starter-web 就帮你配置好了 Spring MVC。你不想用 Spring MVC 了，换成 Spring WebFLux(用 spring-boot-starter-webflux)写响应式 Web 应用。数据持久层，你可以用 Spring Data 项目下的任何子项目(JPA\JDBC\MongoDB\Redis\LDAP\Cassandra\Couchbase\Noe4J\Hadoop\Elasticsearch....)，当然用非 Spring 官方支持的 Mybatis 也可以。只要用上对应技术或框架的 spring-boot-starter-xxx 就可以了。

但是必须要知道，Spring Boot 提供的只是这些 starters，这些 Starter 依赖了(maven dependence)对应的框架或技术，但不包含对应的技术或框架本身！

# Spring Boot 优点

## 良好的基因

Spring Boot 是伴随着 Spring 4.0 诞生的，从字面理解，Boot 是引导的意思，因此 Spring Boot 旨在帮助开发者快速搭建 Spring 框架。Spring Boot 继承了原有 Spring 框架的优秀基因，使 Spring 在使用中更加方便快捷。

## 简化编码

举个例子，比如我们要创建一个 web 项目，使用 Spring 的朋友都知道，在使用 Spring 的时候，需要在 pom 文件中添加多个依赖，而 Spring Boot 则会帮助开发着快速启动一个 web 容器，在 Spring Boot 中，我们只需要在 pom 文件中添加如下一个 starter-web 依赖即可。

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

我们点击进入该依赖后可以看到，Spring Boot 这个 starter-web 已经包含了多个依赖，包括之前在 Spring 工程中需要导入的依赖，我们看一下其中的一部分，如下：

```xml
<!-- .....省略其他依赖 -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>5.0.7.RELEASE</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.0.7.RELEASE</version>
    <scope>compile</scope>
</dependency>
```

## 简化配置

Spring 虽然号称 Java EE 轻量级框架，但由于其繁琐的配置，一度被人认为是“配置地狱”。各种 XML、Annotation 配置会让人眼花缭乱，而且配置多的话，如果出错了也很难找出原因。Spring Boot 更多的是采用 Java Config 的方式，对 Spring 进行配置。举个例子，我新建一个类，但是我不用 @Service 注解，也就是说，它是个普通的类，那么我们如何使它也成为一个 Bean 让 Spring 去管理呢？只需要@Configuration 和@Bean 两个注解即可，如下：

```java
public class TestService {
    public String sayHello () {
        return "Hello Spring Boot!";
    }
}

@Configuration
public class JavaConfig {
    @Bean
    public TestService getTestService() {
        return new TestService();
    }
}

```

@Configuration 表示该类是个配置类，@Bean 表示该方法返回一个 Bean。这样就把 TestService 作为 Bean 让 Spring 去管理了，在其他地方，我们如果需要使用该 Bean，和原来一样，直接使用@Resource 注解注入进来即可使用，非常方便。
