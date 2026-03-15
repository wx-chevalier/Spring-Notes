# Spring 中的事务管理

我们在开发企业应用时，通常业务人员的一个操作实际上是对数据库读写的多步操作的结合。由于数据操作在顺序执行的过程中，任何一步操作都有可能发生异常，异常会导致后续操作无法完成，此时由于业务逻辑并未正确的完成，之前成功操作的数据并不可靠，如果要让这个业务正确的执行下去，通常有实现方式：

- 记录失败的位置，问题修复之后，从上一次执行失败的位置开始继续执行后面要做的业务逻辑
- 在执行失败的时候，回退本次执行的所有过程，让操作恢复到原始状态，待问题修复之后，重新执行原来的业务逻辑

事务就是针对上述方式 2 的实现。事务，一般是指要做的或所做的事情，就是上面所说的业务人员的一个操作（比如电商系统中，一个创建订单的操作包含了创建订单、商品库存的扣减两个基本操作。如果创建订单成功，库存扣减失败，那么就会出现商品超卖的问题，所以最基本的最发就是需要为这两个操作用事务包括起来，保证这两个操作要么都成功，要么都失败）。根据数据库的基本理论我们可知事务具有以下四个特性：

- 原子性：一个事务中所有对数据库的操作是一个不可分割的操作序列，要么全做，要么全部做。
- 一致性：数据不会因为事务的执行而遭到破坏。
- 隔离性：一个事务的执行，不受其他事务(进程)的干扰。既并发执行的个事务之间互不干扰。
- 持久性：一个事务一旦提交，它对数据库的改变将是永久的。

# 编程式事务管理 & 声明式事务管理

编程式事务管理即在 Spring 出现以前，编程式事务管理对基于 POJO 的应用来说是唯一选择。用过 Hibernate 的人都知道，我们需要在代码中显式调用 beginTransaction()、commit()、rollback()等事务管理相关的方法，这就是编程式事务管理。通过 Spring 提供的事务管理 API，我们可以在代码中灵活控制事务的执行。在底层，Spring 仍然将事务操作委托给底层的持久化框架来执行。

Spring 的声明式事务管理在底层是建立在 AOP 的基础之上的。其本质是对方法前后进行拦截，然后在目标方法开始之前创建或者加入一个事务，在执行完目标方法之后根据执行情况提交或者回滚事务。声明式事务最大的优点就是不需要通过编程的方式管理事务，这样就不需要在业务逻辑代码中掺杂事务管理的代码，只需在配置文件中做相关的事务规则声明(或通过等价的基于标注的方式)，便可以将事务规则应用到业务逻辑中。因为事务管理本身就是一个典型的横切逻辑，正是 AOP 的用武之地。Spring 开发团队也意识到了这一点，为声明式事务提供了简单而强大的支持。

声明式事务管理曾经是 EJB 引以为傲的一个亮点，Spring 让 POJO 在事务管理方面也拥有了和 EJB 一样的待遇，让开发人员在 EJB 容器之外也用上了强大的声明式事务管理功能，这主要得益于 Spring 依赖注入容器和 Spring AOP 的支持。依赖注入容器为声明式事务管理提供了基础设施，使得 Bean 对于 Spring 框架而言是可管理的；而 Spring AOP 则是声明式事务管理的直接实现者。

建议在开发中使用声明式事务，不仅因为其简单，更主要是因为这样使得纯业务代码不被污染，极大方便后期的代码维护。和编程式事务相比，声明式事务唯一不足地方是，后者的最细粒度只能作用到方法级别，无法做到像编程式事务那样可以作用到代码块级别。但是即便有这样的需求，也存在很多变通的方法，比如，可以将需要进行事务管理的代码块独立为方法等等。

# 快速开始

在 Spring Boot 中，当我们使用了 spring-boot-starter-jdbc 或 spring-boot-starter-data-jpa 依赖的时候，框架会自动默认分别注入 DataSourceTransactionManager 或 JpaTransactionManager。所以我们不需要任何额外配置就可以用@Transactional 注解进行事务的使用。我们创建了 User 实体以及对 User 的数据访问对象 UserRepository，在单元测试类中实现了使用 UserRepository 进行数据读写的单元测试用例，如下：

```java
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void test() throws Exception {

		// 创建10条记录
		userRepository.save(new User("AAA", 10));
		userRepository.save(new User("BBB", 20));
		userRepository.save(new User("CCC", 30));
		userRepository.save(new User("DDD", 40));
		userRepository.save(new User("EEE", 50));
		userRepository.save(new User("FFF", 60));
		userRepository.save(new User("GGG", 70));
		userRepository.save(new User("HHH", 80));
		userRepository.save(new User("III", 90));
		userRepository.save(new User("JJJ", 100));

		// 省略后续的一些验证操作
	}

}
```

可以看到，在这个单元测试用例中，使用 UserRepository 对象连续创建了 10 个 User 实体到数据库中，下面我们人为的来制造一些异常，看看会发生什么情况。通过@Max(50)来为 User 的 age 设置最大值为 50，这样通过创建时 User 实体的 age 属性超过 50 的时候就可以触发异常产生。

```java
@Entity
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @Max(50)
    private Integer age;

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

}

2020-07-09 11:55:29.581 ERROR 24424 --- [           main] o.h.i.ExceptionMapperStandardImpl        : HHH000346: Error during managed flush [Validation failed for classes [com.didispace.chapter310.User] during persist time for groups [javax.validation.groups.Default, ]
List of constraint violations:[
	ConstraintViolationImpl{interpolatedMessage='最大不能超过50', propertyPath=age, rootBeanClass=class com.didispace.chapter310.User, messageTemplate='{javax.validation.constraints.Max.message}'}
]]
```

可以看到，测试用例执行到一半之后因为异常中断了，前 5 条数据正确插入而后 5 条数据没有成功插入，如果这 10 条数据需要全部成功或者全部失败，那么这时候就可以使用事务来实现，做法非常简单，我们只需要在 test 函数上添加@Transactional 注解即可。

```java
@Test
@Transactional
public void test() throws Exception {

    // 省略测试内容

}
```

再来执行该测试用例，可以看到控制台中输出了回滚日志（Rolled back transaction for test context），

```java
2020-07-09 12:48:23.831  INFO 24889 --- [           main] o.s.t.c.transaction.TransactionContext   : Began transaction (1) for test context [DefaultTestContext@f6efaab testClass = Chapter310ApplicationTests, testInstance = com.didispace.chapter310.Chapter310ApplicationTests@60816371, testMethod = test@Chapter310ApplicationTests, testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@3c19aaa5 testClass = Chapter310ApplicationTests, locations = '{}', classes = '{class com.didispace.chapter310.Chapter310Application}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@34cd072c, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@528931cf, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2353b3e6, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@7ce6a65d], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.populatedRequestContextHolder' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.resetRequestContextHolder' -> true]]; transaction manager [org.springframework.orm.jpa.JpaTransactionManager@4b85edeb]; rollback [true]
2020-07-09 12:48:24.011  INFO 24889 --- [           main] o.s.t.c.transaction.TransactionContext   : Rolled back transaction for test: [DefaultTestContext@f6efaab testClass = Chapter310ApplicationTests, testInstance = com.didispace.chapter310.Chapter310ApplicationTests@60816371, testMethod = test@Chapter310ApplicationTests, testException = javax.validation.ConstraintViolationException: Validation failed for classes [com.didispace.chapter310.User] during persist time for groups [javax.validation.groups.Default, ]
List of constraint violations:[
	ConstraintViolationImpl{interpolatedMessage='最大不能超过50', propertyPath=age, rootBeanClass=class com.didispace.chapter310.User, messageTemplate='{javax.validation.constraints.Max.message}'}
], mergedContextConfiguration = [WebMergedContextConfiguration@3c19aaa5 testClass = Chapter310ApplicationTests, locations = '{}', classes = '{class com.didispace.chapter310.Chapter310Application}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@34cd072c, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@528931cf, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2353b3e6, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@7ce6a65d], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.populatedRequestContextHolder' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.resetRequestContextHolder' -> true]]
```

再看数据库中，User 表就没有 AAA 到 EEE 的用户数据了，成功实现了自动回滚。这里主要通过单元测试演示了如何使用 @Transactional 注解来声明一个函数需要被事务管理，通常我们单元测试为了保证每个测试之间的数据独立，会使用@Rollback 注解让每个单元测试都能在结束时回滚。而真正在开发业务逻辑时，我们通常在 service 层接口中使用 @Transactional 来对各个业务逻辑进行事务管理的配置，例如：

```java
public interface UserService {

    @Transactional
    User update(String name, String password);

}
```
