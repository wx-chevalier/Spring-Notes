# 嵌入式 Redis Server

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<dependency>
  <groupId>it.ozimov</groupId>
  <artifactId>embedded-redis</artifactId>
  <version>0.7.2</version>
  <scope>test</scope>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-test</artifactId>
  <scope>test</scope>
</dependency>
```

在添加了依赖关系之后，我们应该定义 Redis 服务器和我们的应用程序之间的连接设置。让我们先创建一个类来保存我们的属性。

```java
@Configuration
public class RedisProperties {
    private int redisPort;
    private String redisHost;

    public RedisProperties(
      @Value("${spring.redis.port}") int redisPort,
      @Value("${spring.redis.host}") String redisHost) {
        this.redisPort = redisPort;
        this.redisHost = redisHost;
    }

    // getters
}
```

接下来，我们应该创建一个配置类，定义连接并使用我们的属性：

```java
@Configuration
@EnableRedisRepositories
public class RedisConfiguration {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(
      RedisProperties redisProperties) {
        return new LettuceConnectionFactory(
          redisProperties.getRedisHost(),
          redisProperties.getRedisPort());
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
```

首先，在 src/test/resources 目录下创建 `application.properties` 文件：

```plaintext
spring.redis.host=localhost
spring.redis.port=6370
```

然后，创建 `@TestConfiguration` 注解的配置类：

```java
@TestConfiguration
public class TestRedisConfiguration {

    private RedisServer redisServer;

    public TestRedisConfiguration(RedisProperties redisProperties) {
        this.redisServer = new RedisServer(redisProperties.getRedisPort());
    }

    @PostConstruct
    public void postConstruct() {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}
```

一旦上下文启动，服务器就会启动。它将在我们的机器上以我们在属性中定义的端口启动。例如，我们现在可以在不停止实际 Redis 服务器的情况下运行测试。理想情况下，我们希望在随机可用的端口上启动它，但嵌入式 Redis 还没有这个功能。我们现在可以做的是通过 ServerSocket API 获取随机端口。另外，一旦上下文被销毁，服务器就会停止。服务器也可以提供我们自己的可执行文件。

```java
this.redisServer = new RedisServer("/path/redis", redisProperties.getRedisPort());
```

Furthermore, the executable can be defined per operating system:

```java
RedisExecProvider customProvider = RedisExecProvider.defaultProvider()
  .override(OS.UNIX, "/path/unix/redis")
  .override(OS.Windows, Architecture.x86_64, "/path/windows/redis")
  .override(OS.MAC_OS_X, Architecture.x86_64, "/path/macosx/redis")

this.redisServer = new RedisServer(customProvider, redisProperties.getRedisPort());
```

Finally, let's create a test that'll use our _TestRedisConfiguration_ class:

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestRedisConfiguration.class)
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveUser_toRedis() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "name");

        User saved = userRepository.save(user);

        assertNotNull(saved);
    }
}
```

The user has been saved to our embedded Redis server.
