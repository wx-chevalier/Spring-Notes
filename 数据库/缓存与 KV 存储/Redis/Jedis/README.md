# Jedis

# 快速开始

Spring Boot 集成 redis 很方便，只需要导入一个 redis 的 starter 依赖即可。如下：

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<!--阿里巴巴fastjson -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.35</version>
</dependency>
```

导入了依赖之后，我们在 application.yml 文件里配置 redis：

```yaml
server:
  port: 8080
spring:
  #redis相关配置
  redis:
    database: 5
    # 配置redis的主机地址，需要修改成自己的
    host: 192.168.48.190
    port: 6379
    password: 123456
    timeout: 5000
    jedis:
      pool:
        # 连接池中的最大空闲连接，默认值也是8。
        max-idle: 500
        # 连接池中的最小空闲连接，默认值也是0。
        min-idle: 50
        # 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)
        max-active: 1000
        # 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException
        max-wait: 2000
```

注意，有两个 redis 模板：RedisTemplate 和 StringRedisTemplate。我们不使用 RedisTemplate，RedisTemplate 提供给我们操作对象，操作对象的时候，我们通常是以 json 格式存储，但在存储的时候，会使用 Redis 默认的内部序列化器；导致我们存进里面的是乱码之类的东西。当然了，我们可以自己定义序列化，但是比较麻烦，所以使用 StringRedisTemplate 模板。StringRedisTemplate 主要给我们提供字符串操作，我们可以将实体类等转成 json 字符串即可，在取出来后，也可以转成相应的对象。

```java
public class RedisService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * set redis: string类型
     * @param key key
     * @param value value
     */
    public void setString(String key, String value){
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set(key, value);
    }
    /**
     * get redis: string类型
     * @param key key
     * @return
     */
    public String getString(String key){
        return stringRedisTemplate.opsForValue().get(key);
    }

@RunWith(SpringRunner.class)
@SpringBootTest
public class Course14ApplicationTests {
    private static final Logger logger = LoggerFactory.getLogger(Course14ApplicationTests.class);
	@Resource
	private RedisService redisService;
	@Test
	public void contextLoads() {
        //测试redis的string类型
        redisService.setString("weichat","程序员私房菜");
        logger.info("我的微信公众号为：{}", redisService.getString("weichat"));
        // 如果是个实体，我们可以使用json工具转成json字符串，
        User user = new User("CSDN", "123456");
        redisService.setString("userInfo", JSON.toJSONString(user));
        logger.info("用户信息：{}", redisService.getString("userInfo"));
    }
}
```

## 常用操作

```java
public class TestRedis {
  private Jedis jedis;

  @Before
  public void setup() {
    //连接redis服务器，192.168.0.100:6379
    jedis = new Jedis("192.168.0.100", 6379);

    //权限认证
    jedis.auth("admin");
  }

  /**
   * redis存储字符串
   */
  @Test
  public void testString() {
    //-----添加数据----------
    jedis.set("name", "xinxin"); //向key-->name中放入了value-->xinxin
    System.out.println(jedis.get("name")); //执行结果：xinxin

    jedis.append("name", " is my lover"); //拼接
    System.out.println(jedis.get("name"));

    jedis.del("name"); //删除某个键
    System.out.println(jedis.get("name"));

    //设置多个键值对
    jedis.mset("name", "liuling", "age", "23", "qq", "476777XXX");
    jedis.incr("age"); //进行加1操作
    System.out.println(
      jedis.get("name") + "-" + jedis.get("age") + "-" + jedis.get("qq")
    );
  }

  /**
   * redis操作Map
   */
  @Test
  public void testMap() {
    //-----添加数据----------
    Map<String, String> map = new HashMap<String, String>();
    map.put("name", "xinxin");
    map.put("age", "22");
    map.put("qq", "123456");
    jedis.hmset("user", map);

    //取出user中的name，执行结果:[minxr]-->注意结果是一个泛型的List
    //第一个参数是存入redis中map对象的key，后面跟的是放入map中的对象的key，后面的key可以跟多个，是可变参数
    List<String> rsmap = jedis.hmget("user", "name", "age", "qq");
    System.out.println(rsmap);

    //删除map中的某个键值
    jedis.hdel("user", "age");
    System.out.println(jedis.hmget("user", "age")); //因为删除了，所以返回的是null
    System.out.println(jedis.hlen("user")); //返回key为user的键中存放的值的个数2
    System.out.println(jedis.exists("user")); //是否存在key为user的记录 返回true
    System.out.println(jedis.hkeys("user")); //返回map对象中的所有key
    System.out.println(jedis.hvals("user")); //返回map对象中的所有value

    Iterator<String> iter = jedis.hkeys("user").iterator();
    while (iter.hasNext()) {
      String key = iter.next();
      System.out.println(key + ":" + jedis.hmget("user", key));
    }
  }

  /**
   * jedis操作List
   */
  @Test
  public void testList() {
    //开始前，先移除所有的内容
    jedis.del("java framework");
    System.out.println(jedis.lrange("java framework", 0, -1));

    //先向key java framework中存放三条数据
    jedis.lpush("java framework", "spring");
    jedis.lpush("java framework", "struts");
    jedis.lpush("java framework", "hibernate");

    //再取出所有数据jedis.lrange是按范围取出，
    // 第一个是key，第二个是起始位置，第三个是结束位置，jedis.llen获取长度 -1表示取得所有
    System.out.println(jedis.lrange("java framework", 0, -1));

    jedis.del("java framework");
    jedis.rpush("java framework", "spring");
    jedis.rpush("java framework", "struts");
    jedis.rpush("java framework", "hibernate");
    System.out.println(jedis.lrange("java framework", 0, -1));
  }

  /**
   * jedis操作Set
   */
  @Test
  public void testSet() {
    //添加
    jedis.sadd("user", "liuling");
    jedis.sadd("user", "xinxin");
    jedis.sadd("user", "ling");
    jedis.sadd("user", "zhangxinxin");
    jedis.sadd("user", "who");

    //移除noname
    jedis.srem("user", "who");
    System.out.println(jedis.smembers("user")); //获取所有加入的value
    System.out.println(jedis.sismember("user", "who")); //判断 who 是否是user集合的元素
    System.out.println(jedis.srandmember("user"));
    System.out.println(jedis.scard("user")); //返回集合的元素个数
  }

  @Test
  public void test() throws InterruptedException {
    //jedis 排序
    //注意，此处的rpush和lpush是List的操作。是一个双向链表(但从表现来看的)
    jedis.del("a"); //先清除数据，再加入数据进行测试
    jedis.rpush("a", "1");
    jedis.lpush("a", "6");
    jedis.lpush("a", "3");
    jedis.lpush("a", "9");
    System.out.println(jedis.lrange("a", 0, -1)); // [9, 3, 6, 1]
    System.out.println(jedis.sort("a")); //[1, 3, 6, 9]  //输入排序后结果
    System.out.println(jedis.lrange("a", 0, -1));
  }

  @Test
  public void testRedisPool() {
    RedisUtil.getJedis().set("newname", "中文测试");
    System.out.println(RedisUtil.getJedis().get("newname"));
  }
}
```
