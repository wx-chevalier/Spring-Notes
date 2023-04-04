# SpringCloud OpenFeign 教程

OpenFeign 集成了 Ribbon 和 Hystrix，并简化了服务调用方式，使用起来非常方便。

# 开始配置

## 导入相关依赖

- SpringCloud 版本：Hoxton.SR1
- SpringBoot 版本：2.2.4.RELEASE 新版 SpringCloud 的 OpenFeign 有些变化，先用旧版本，后面搞明白了再来填坑。

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

<!--必须导入web依赖，否则无法启动-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

## 启动类注解

添加 @EnableEurekaClient 和 @EnableFeignClients 注解：

```java
@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
public class FeignConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeignConsumerApplication.class, args);
    }
}
```

## 服务提供者

服务提供者有三个接口，分别测试 Json 参数、对象参数、普通参数：

```java
@RestController
public class Controller {
    @RequestMapping("/getUser3")
    public User getUser3(@RequestBody User user) throws InterruptedException {
        return user;
    }

    @RequestMapping("/getUser2")
    public User getUser2(User user) throws InterruptedException {
        return user;
    }

    @RequestMapping("/getUsers")
    public String[] getUsers(String ids) throws InterruptedException {
        return ids.split(",");
    }
}
```

## 实现服务调用

OpenFeign 实现服务调用非常简单：

- 创建一个接口
- 添加@FeignClient("provider")注解，provider 是服务名
- 编写接口方法 接口方法跟服务提供者的 Controller 代码基本一致，但参数绑定会有些不同，具体看代码注释
- 定义请求方式
- 定义参数
- 定义返回值

```java
@FeignClient"provider")
public interface Service {
    //必须用@RequestParam注解，否则提供者接收不到
    @GetMapping("/getUsers")
    String[] get(@RequestParam("ids") String ids);

    //传递对象参数用@SpringQueryMap注解
    @GetMapping("/getUser2")
    User get2(@SpringQueryMap User user);

    //传递Json参数用@RequestBody注解
    @GetMapping("/getUser3")
    User get3(@RequestBody User user);
}
```

接口定义好以后就可以直接使用了，不需要实现类，下面 Controller 层调用它：

```java
//注入上面定义的接口
@Autowired
private Service service;

//Controller层方法
@GetMapping("/getUsers")
public String[] getUsers(String ids){
    //调用接口的方法
    return service.get(ids);
}
```

## Ribbon 配置

OpenFeign 集成了 Ribbon，Ribbon 的配置可以参考这篇文章 SpringCloud Ribbon 教程。

## Hystrix 配置

OpenFeign 集成了 Hystrix，默认是关闭的，需要先启用它：

```yaml
feign:
  hystrix:
    enabled: true
```

Hystrix 配置与这篇文章一致 SpringCloud Hystrix 教程。

## 定义服务降级逻辑

OpenFeign 的服务降级配置方式有些不同

- 创建一个类，实现上面定义的 Service 接口，这个类就是服务降级处理类
- 把这个类装配到 Spring 容器，可以用@Component 注解

```java
@Component
public class ServiceFallback implements Service{
    @Override
    public String[] get(String ids) {
        System.out.println("降级");
        return new String[0];
    }

    @Override
    public User get2(User user) {
        System.out.println("降级");
        return null;
    }

    @Override
    public User get3(User user) {
        System.out.println("降级");
        return null;
    }
}
```

- Service 接口上的注解添加 fallback = ServiceFallback.class 指定降级处理类

```java
@FeignClient(value = "provider",fallback = ServiceFallback.class)
public interface Service {
    @GetMapping("/getUsers")
    String[] get(@RequestParam("ids") String ids);

    @GetMapping("/getUser2")
    User get2(@SpringQueryMap User user);

    @GetMapping("/getUser3")
    User get3(@RequestBody User user);
}
```

# OpenFeign 配置

## 压缩

```yaml
feign:
  # 启用请求压缩和响应压缩
  compression:
    request:
      enabled: true
      # 压缩哪些类型
      mime-types: text/xml,application/xml,application/json
      # 超过此大小才压缩
      min-request-size: 2048
    response:
      enabled: true
      # GZIP压缩
      useGzipDecoder: true
```

## 日志

OpenFeign 日志记录级别，定义记录那些信息：

- NONE: 不记录任何信息。
- BASIC: 仅记录请求方法、URL 以及响应状态码和执行时间。
- HEADERS: 除了记录 BASIC 级别的信息之外，还会记录请求和响应的头信息。
- FULL: 记录所有请求与响应的明细，包括头信息、请求体、元数据等。 启动类添加日志配置

```java
@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
public class FeignConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeignConsumerApplication.class, args);
    }

    /**
     * feign日志
     * @return
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
```

配置文件中定义日志级别，这里的日志级别跟上面的不一样，Feign 日志记录仅响应 debug 级别：

```yaml
# 日志级别
logging:
  level:
    com:
      example:
        feignconsumer:
          Service: debug
```

访问接口可以看到控制台输出日志：

```sh
2021-02-03 16:29:02.817 DEBUG 17308 --- [trix-provider-1] com.example.feignconsumer.Service        : [Service#get2] <--- HTTP/1.1 200 (554ms)
2021-02-03 16:29:02.817 DEBUG 17308 --- [trix-provider-1] com.example.feignconsumer.Service        : [Service#get2] connection: keep-alive
2021-02-03 16:29:02.817 DEBUG 17308 --- [trix-provider-1] com.example.feignconsumer.Service        : [Service#get2] content-type: application/json
2021-02-03 16:29:02.817 DEBUG 17308 --- [trix-provider-1] com.example.feignconsumer.Service        : [Service#get2] date: Wed, 03 Feb 2021 08:29:02 GMT
2021-02-03 16:29:02.818 DEBUG 17308 --- [trix-provider-1] com.example.feignconsumer.Service        : [Service#get2] keep-alive: timeout=60
2021-02-03 16:29:02.818 DEBUG 17308 --- [trix-provider-1] com.example.feignconsumer.Service        : [Service#get2] transfer-encoding: chunked
2021-02-03 16:29:02.818 DEBUG 17308 --- [trix-provider-1] com.example.feignconsumer.Service        : [Service#get2]
2021-02-03 16:29:02.822 DEBUG 17308 --- [trix-provider-1] com.example.feignconsumer.Service        : [Service#get2] {"id":null,"name":"1","password":"2"}
2021-02-03 16:29:02.822 DEBUG 17308 --- [trix-provider-1] com.example.feignconsumer.Service        : [Service#get2] <--- END HTTP (37-byte body)
```
