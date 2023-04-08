# 使用 MySQLContainer 进行数据库单元测试

首先声明基础的测试类，该类中会声明一个 MySQLContainer 对象，用于启动一个 MySQL 容器，然后将容器的 URL、用户名和密码设置为系统属性，以便在测试中使用。

```java
@AutoConfigureTestDatabase(
    replace = Replace.NONE
)
public abstract class AbstractDatabaseTest {
    private static final File TEST_DATABASE_PROP_FILE = new File(System.getProperty("user.home"), ".ufc/test-database.properties");

    @Container
    public static final MySQLContainer<?> DATABASE;

    @Autowired
    private ApplicationContext context;

    public AbstractDatabaseTest() {
    }

    public static String getUrl() {
        String url = System.getProperty("spring.datasource.url");
        return url == null ? DATABASE.getJdbcUrl() : url;
    }

    public static String getUsername() {
        String url = System.getProperty("spring.datasource.url");
        return url == null ? DATABASE.getUsername() : System.getProperty("spring.datasource.username");
    }

    public static String getPassword() {
        String url = System.getProperty("spring.datasource.url");
        return url == null ? DATABASE.getPassword() : System.getProperty("spring.datasource.password");
    }

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", AbstractDatabaseTest::getUrl);
        registry.add("spring.datasource.username", AbstractDatabaseTest::getUsername);
        registry.add("spring.datasource.password", AbstractDatabaseTest::getPassword);
    }

    private static void runScript(MySQLContainer<?> mysql, String classpath) {
        ScriptUtils.runInitScript(new JdbcDatabaseDelegate(mysql, ""), classpath);
    }

    private static void runScriptInPath(MySQLContainer<?> mysql, String classpath) {
        if (!classpath.endsWith("/")) {
            classpath = classpath + "/";
        }

        try {
            InputStream in = AbstractDatabaseTest.class.getClassLoader().getResourceAsStream(classpath);
            Throwable var3 = null;

            try {
                if (in == null) {
                    return;
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                Throwable var5 = null;

                try {
                    String name;
                    try {
                        while((name = br.readLine()) != null) {
                            runScript(mysql, classpath + name);
                        }
                    } catch (Throwable var31) {
                        var5 = var31;
                        throw var31;
                    }
                } finally {
                    if (br != null) {
                        if (var5 != null) {
                            try {
                                br.close();
                            } catch (Throwable var30) {
                                var5.addSuppressed(var30);
                            }
                        } else {
                            br.close();
                        }
                    }

                }
            } catch (Throwable var33) {
                var3 = var33;
                throw var33;
            } finally {
                if (in != null) {
                    if (var3 != null) {
                        try {
                            in.close();
                        } catch (Throwable var29) {
                            var3.addSuppressed(var29);
                        }
                    } else {
                        in.close();
                    }
                }

            }

        } catch (IOException var35) {
            throw new UncheckedIOException(var35);
        }
    }

    @SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT"})
    protected void cleanupDatabase() {
        String url = getUrl();
        if (!url.contains("localhost") && !url.contains("127.0.0.1")) {
            throw new RuntimeException("无效测试库: " + url);
        } else {
            Collection<BaseMapper> mappers = this.context.getBeansOfType(BaseMapper.class).values();
            Iterator var3 = mappers.iterator();

            while(var3.hasNext()) {
                BaseMapper mapper = (BaseMapper)var3.next();
                mapper.delete(new LambdaQueryWrapper());
            }

        }
    }

    static {
        try {
            FileInputStream ins = new FileInputStream(TEST_DATABASE_PROP_FILE);
            Throwable var1 = null;

            try {
                Properties props = new Properties();
                props.load(ins);
                Set<String> propertyNames = props.stringPropertyNames();
                Iterator var4 = propertyNames.iterator();

                while(var4.hasNext()) {
                    String propertyName = (String)var4.next();
                    System.setProperty(propertyName, props.getProperty(propertyName));
                }
            } catch (Throwable var14) {
                var1 = var14;
                throw var14;
            } finally {
                if (ins != null) {
                    if (var1 != null) {
                        try {
                            ins.close();
                        } catch (Throwable var13) {
                            var1.addSuppressed(var13);
                        }
                    } else {
                        ins.close();
                    }
                }

            }
        } catch (IOException var16) {
        }

        String url = System.getProperty("spring.datasource.url");
        if (url == null) {
            DATABASE = new MySQLContainer("mysql:8");
            DATABASE.withDatabaseName("mysql");
            DATABASE.withReuse(true);
            DATABASE.withClasspathResourceMapping("db/schema", "/docker-entrypoint-initdb.d", BindMode.READ_ONLY);
            DATABASE.start();
            runScriptInPath(DATABASE, "db/schemanext");
        } else {
            DATABASE = null;
        }

    }
}

```

然后声明 MyBatis 的配置：

```java

public abstract class AbstractMyBatisConfig {
    public AbstractMyBatisConfig() {
    }

    @Bean
    public GlobalConfig globalConfig() {
        DbConfig dbConfig = new DbConfig();
        dbConfig.setColumnFormat("`%s`");
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setDbConfig(dbConfig);
        return globalConfig;
    }

    @Bean
    public MybatisPlusPropertiesCustomizer customizer() {
        return (properties) -> {
            properties.getGlobalConfig().getDbConfig().setColumnFormat("`%s`");
        };
    }

    @Bean
    @Qualifier("entityIdClasses")
    public abstract EntityIdClasses entityIdClasses();

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInterceptor.setMaxLimit(100L);
        interceptor.addInnerInterceptor(paginationInterceptor);
        return interceptor;
    }

    @Bean
    public TypeHandlerRegistry typeHandlers(SqlSessionFactory sqlSessionFactory, @Qualifier("entityIdClasses") EntityIdClasses entityIdClasses) {
        TypeHandlerRegistry typeHandlerRegistry = sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
        typeHandlerRegistry.register(UUID.class, JdbcType.BINARY, new BinUUIDTypeHandler());
        typeHandlerRegistry.register(JsonNode.class, JacksonTypeHandler.class);
        typeHandlerRegistry.register(ObjectNode.class, JacksonTypeHandler.class);
        typeHandlerRegistry.register(ArrayNode.class, JacksonTypeHandler.class);
        Iterator var4 = API.List(new JdbcType[]{JdbcType.BIGINT, JdbcType.INTEGER, JdbcType.NULL}).iterator();

        while(var4.hasNext()) {
            JdbcType jdbcType = (JdbcType)var4.next();
            Iterator var6 = entityIdClasses.getLongEntityIdClasses().iterator();

            while(var6.hasNext()) {
                Class<? extends LongEntityId> clazz = (Class)var6.next();
                EntityIdTypeHandler handler = new EntityIdTypeHandler(clazz);
                typeHandlerRegistry.register(clazz, jdbcType, handler);
            }
        }

        var4 = entityIdClasses.getUuidEntityIdClasses().iterator();

        while(var4.hasNext()) {
            Class<? extends UuidEntityId> clazz = (Class)var4.next();
            BinAndEntityIdTypeHandler handler = new BinAndEntityIdTypeHandler(clazz);
            typeHandlerRegistry.register(clazz, JdbcType.BINARY, handler);
        }

        return typeHandlerRegistry;
    }
}


public abstract class AbstractDatabaseTestConfig extends AbstractMyBatisConfig {
    public AbstractDatabaseTestConfig() {
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(AbstractDatabaseTest.getUrl());
        dataSource.setUsername(AbstractDatabaseTest.getUsername());
        dataSource.setPassword(AbstractDatabaseTest.getPassword());
        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, GlobalConfig globalConfig, MybatisPlusInterceptor mybatisPlusInterceptor) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setGlobalConfig(globalConfig);
        sqlSessionFactory.setPlugins(new Interceptor[]{mybatisPlusInterceptor});
        return sqlSessionFactory.getObject();
    }
}
```

最后在具体的测试类中，继承 AbstractDatabaseTest，并注入 TestConfig 就可以使用 MyBatis 了：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = {
      OrderDatabaseConfig.class,
      CustomerServiceImpl.class,
      CustomerImportAction.class,
      CustomerMergeAction.class,
      CustomerCreateOrUpdateAction.class,
      CustomerRemoveAction.class
    },
    properties = {"logging.level.com.unionfab.cloud.alarm.infra.dmr=debug"})
class CustomerServiceImplTest extends AbstractDatabaseTest {}
```
