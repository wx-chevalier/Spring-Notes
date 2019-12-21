# Redisson

# 配置

## 手动配置

```java
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 从外部文件创建
        // Config config = Config.fromJSON(new File("singleNodeConfig.json"));
        config.useClusterServers()
                .setScanInterval(2000)
                .addNodeAddress("redis://10.0.29.30:6379", "redis://10.0.29.95:6379")
                .addNodeAddress("redis://10.0.29.205:6379");

        RedissonClient redisson = Redisson.create(config);

        return redisson;
    }

}

// 使用的时候
@Autowired
private RedissonClient redissonClient;
```

Redisson 常用的配置如下：

```json
{
  "singleServerConfig": {
    "idleConnectionTimeout": 10000,
    "pingTimeout": 1000,
    "connectTimeout": 10000,
    "timeout": 3000,
    "retryAttempts": 3,
    "retryInterval": 1500,
    "reconnectionTimeout": 3000,
    "failedAttempts": 3,
    "password": null,
    "subscriptionsPerConnection": 5,
    "clientName": null,
    "address": "redis://127.0.0.1:6379",
    "subscriptionConnectionMinimumIdleSize": 1,
    "subscriptionConnectionPoolSize": 50,
    "connectionMinimumIdleSize": 10,
    "connectionPoolSize": 64,
    "database": 0,
    "dnsMonitoring": false,
    "dnsMonitoringInterval": 5000
  },
  "threads": 0,
  "nettyThreads": 0,
  "codec": null,
  "useLinuxNativeEpoll": false
}
```

其对应的 YML 配置如下：

```yml
singleServerConfig:
  idleConnectionTimeout: 10000
  pingTimeout: 1000
  connectTimeout: 10000
  timeout: 3000
  retryAttempts: 3
  retryInterval: 1500
  reconnectionTimeout: 3000
  failedAttempts: 3
  password: null
  subscriptionsPerConnection: 5
  clientName: null
  address: "redis://127.0.0.1:6379"
  subscriptionConnectionMinimumIdleSize: 1
  subscriptionConnectionPoolSize: 50
  connectionMinimumIdleSize: 10
  connectionPoolSize: 64
  database: 0
  dnsMonitoring: false
  dnsMonitoringInterval: 5000
threads: 0
nettyThreads: 0
codec: !<org.redisson.codec.JsonJacksonCodec> {}
useLinuxNativeEpoll: false
```

## redisson-spring-boot-starter

- 添加 `redisson-spring-boot-starter` 到依赖中:

Maven

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>3.11.6</version>
</dependency>
```

Gradle

```sh
compile 'org.redisson:redisson-spring-boot-starter:3.11.6'
```

- 添加如下配置 `application.settings`：

```yml
# common spring boot settings

spring.redis.database=
spring.redis.host=
spring.redis.port=
spring.redis.password=
spring.redis.ssl=
spring.redis.timeout=
spring.redis.cluster.nodes=
spring.redis.sentinel.master=
spring.redis.sentinel.nodes=

# Redisson settings

#path to config - redisson.yaml
spring.redis.redisson.config=classpath:redisson.yaml
```

然后我们可以注册 RedissonClient 的 Bean：

```java
 @Configuration
 public class RedissonSpringDataConfig {

    @Bean
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
        return new RedissonConnectionFactory(redisson);
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson(@Value("classpath:/redisson.yaml") Resource configFile) throws IOException {
        Config config = Config.fromYAML(configFile.getInputStream());
        return Redisson.create(config);
    }

 }
```

# 基础操作

Redisson 支持同步，异步和反应式接口。这些接口上的操作是线程安全的。RedissonClient 生成的所有实体（对象，集合，锁和服务）都具有同步和异步方法。同步方法带有异步变体。这些方法通常具有与它们的同步变体相同的方法名称，并带有“ Async”。

```java
RedissonClient client = Redisson.create();
RAtomicLong myLong = client.getAtomicLong('myLong');

RFuture<Boolean> isSet = myLong.compareAndSetAsync(6, 27);
```

方法的异步变体返回 RFuture 对象。我们可以在该对象上设置侦听器，以在结果可用时取回结果：

```java
isSet.handle((result, exception) -> {
    // handle the result or exception here.
});

RedissonReactiveClient client = Redisson.createReactive();
RAtomicLongReactive myLong = client.getAtomicLong("myLong");

Publisher<Boolean> isSetPublisher = myLong.compareAndSet(5, 28);
```
