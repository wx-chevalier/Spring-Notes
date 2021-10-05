# ScopedProxyMode

首先查看下 @Scope 注解的定义信息，其共有三个方法，分别为 value、scopeName、proxyMode，其中 value 和 scopeName 利用了 Spring 的显性覆盖，这两个方法的作用是一样的，只不过 scopeName 要比 value 更具有语义性。重点是 proxyMode 方法，其默认值为 ScopedProxyMode.DEFAULT。

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {

	@AliasFor("scopeName")
	String value() default "";

	@AliasFor("value")
	String scopeName() default "";

	ScopedProxyMode proxyMode() default ScopedProxyMode.DEFAULT;

}
```

ScopedProxyMode 是一个枚举类，该类共定义了四个枚举值，分别为 NO、DEFAULT、INTERFACE、TARGET_CLASS，其中 DEFAULT 和 NO 的作用是一样的。INTERFACES 代表要使用 JDK 的动态代理来创建代理对象，TARGET_CLASS 代表要使用 CGLIB 来创建代理对象。

```java
public enum ScopedProxyMode {
	DEFAULT,
	NO,
	INTERFACES,
	TARGET_CLASS
}
```

首先我们通过代码案例来看来不同取值的差异：

```java
public class MyBean {
    @Autowired
    private ScopeProxyBean proxyBean;

    public void test(){
        proxyBean.code();
    }

}

@Scope(value = DefaultListableBeanFactory.SCOPE_PROTOTYPE/*,proxyMode = ScopedProxyMode.TARGET_CLASS*/)
public class ScopeProxyBean {

    public void code() {
        System.out.println(this.hashCode());

    }
}

// 测试用例
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestSpring.class)
public class TestSpring {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void test1() throws UnknownHostException {
       AnnotationConfigApplicationContext applicationContext =
        new AnnotationConfigApplicationContext();
        applicationContext.register(MyBean.class);
        applicationContext.register(ScopeProxyBean.class);
        applicationContext.refresh();
        MyBean bean = applicationContext.getBean(MyBean.class);
        for (int i = 0; i < 10; i++) {
            bean.test(); // 这里生成的所有 Bean 的 hashCode 都是一致的
        }
    }
}
```

下面调整 @Scope 注解中的 proxyMode 属性为 ScopedProxyMode.TARGET_CLASS，能看到会得到不同的 hashCode 的值。

# 源码解析

在 ClassPathBeanDefinitionScanner 的 doScan 方法中，对于 findCandidateComponents 方法返回值进行遍历时，会首先调用 ScopeMetadataResolver 的 resolveScopeMetadata 方法，传入 BeanDefinition 对象。该方法会返回一个 ScopeMetadata 对象，然后将该对象设置到 BeanDefinition 中去，通过 BeanDefinition 的 setScope 方法。接下来便是通过 BeanNameGenerator 的 generatedBeanName 方法来生成 BeanName，判断 BeanDefinition 对象(以下简称为 candidate)是否是 AbstractBeanDefinition，如果判断成立，则调用 postProcessBeanDefinition 方法(该方法主要用来设置 BeanDefinition 的一些默认值)，判断 candidate 是否是 AnnotatedBeanDefinition，如果判断成立则调用 AnnotationConfigUtils 的 processCommonDefinitionAn-notations 方法(通过方法名也可以看出，该方法主要用来解析一些通用的注解)。

调用 checkCandidate 方法，如果该方法返回值为 true(该方法用来判断当前(注意，不是层级查找)IoC 容器中是否指定 BeanName 的 BeanDefinition 信息，如果包含，则进行兼容性比对)。创建 BeanDefinitionHolder 实例，然后调用 AnnotationConfigUtils 的 applyScopedProxyMode 方法来根据前面解析好的 ScopeMetadata 对象来处理 BeanDefinitionHolder，注意这里传了 BeanDefinitionRegistry 实例，最后调用 registerBeanDefinition 方法将 AnnotationConfigUtils 的 applyScopedProxyMode 方法返回值注册到 BeanDefinition 到 IoC 容器中。

```java
private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
// ClassPathBeanDefinitionScanner#doScan
protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Assert.notEmpty(basePackages, "At least one base package must be specified");
		Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();
		for (String basePackage : basePackages) {
			// 根据指定包路径扫描Bean资源并加载
			Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
			for (BeanDefinition candidate : candidates) {
				// 使用AnnotationScopeMetadataResolver的resolveScopeMeatdata方法来根据Bean中@Scope(如果有)注解创建ScopeMeatdata对象
				ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
				candidate.setScope(scopeMetadata.getScopeName());
				// 调用AnnotationBeanNameGenerator的generatorBeanName方法生成beanName
				String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);
				// 如果BeanDefinition是AbstractBeanDefinition类型的，设置BeanDefinition的默认值
				if (candidate instanceof AbstractBeanDefinition) {
					postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
				}
				// 如果BeanDefinition是AnnotatedBeanDefinition类型，解析通用注解
				if (candidate instanceof AnnotatedBeanDefinition) {
					AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
				}
				// 如果BeanDefinition可以兼容
				if (checkCandidate(beanName, candidate)) {
					BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
					// 解析Bean中的@Scope注解
					definitionHolder =
							AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
					beanDefinitions.add(definitionHolder);
					registerBeanDefinition(definitionHolder, this.registry);
				}
			}
		}
		return beanDefinitions;
	}
```

首先分析下 ScopeMetadataResolver 这个接口。该接口存在两个实现类，分别为 AnnotationScopeM-etadataResolver 和 Jsr330ScopeMetadataResolver。AnnotationScopeMetadataResolver 用来处理 Spring 的@Scope 注解，而 Jsr330ScopeMetadataResolver 则用来处理 Jsr-330 规范中提出的@Scope 注解。ClassPathBeanDefinitionScanner 默认使用的是 AnnotationScopeMetadataResolver。

在 AnnotationScopeMetadataResolver 的 resolveScopeMetadata 方法中，首先创建 ScopeMetadata 实例，然后判断传入的 BeanDefinition 是否是 AnnotatedBeanDefinition 类型的。这里需要说明下通过 Cl-assPathBeanDefinitionScanner 扫描的类信息并创建的 BeanDefinition 都是 ScannedGenericBeanDefin-ition 类型的，该类型实现了 AnnotatedBeanDefinition 接口，因此这里的判断成立。

判断成立后首先将 BeanDefinition 强制转型为 AnnotatedBeanDefinition，调用 AnnotationConfigUtils 的 attributesFor 方法，传入从注解元数据(AnnotationMetadata)以及@Scope 注解的类型，返回 AnnotationAttributes 对象(以下简称 attributes)，如果返回的对象不为空，则设置 ScopeMetadata 的 scopeName(通过调用 atributes 的 getString 方法)，调用 attributes 的 getEnum 方法来获取@Scope 注解中 proxyMode 方法的返回值，如果返回的 proxyMode 等等于 ScopeProxyMode 的 DEFAULT，则将 proxyMode 重置为 ScopedProxyMode.NO(这也是前面讲到的 DEFAULT 和 NO 的作用是一样的)，将 proxyMode 设置到 metadata 中。

最后返回设置好的 metadata。

```java
public AnnotationScopeMetadataResolver() {
		this.defaultProxyMode = ScopedProxyMode.NO;
}
// AnnotationScopeMetadataResolver#resolveScopeMetadata
public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
   ScopeMetadata metadata = new ScopeMetadata();
   if (definition instanceof AnnotatedBeanDefinition) {
      AnnotatedBeanDefinition annDef = (AnnotatedBeanDefinition) definition;
      AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(
            annDef.getMetadata(), this.scopeAnnotationType);
      if (attributes != null) {
         metadata.setScopeName(attributes.getString("value"));
         ScopedProxyMode proxyMode = attributes.getEnum("proxyMode");
         if (proxyMode == ScopedProxyMode.DEFAULT) {
            proxyMode = this.defaultProxyMode;
         }
         metadata.setScopedProxyMode(proxyMode);
      }
   }
   return metadata;
}
```

在 AnnotationConfigUtils 的 applyScopedProxyMode 方法中，通过传入的 ScopeMetadata 实例的 getScopedProxyMode 方法来获取 ScopedProxyMode，如果获取到的 ScopedProxyMode 等于 ScopedProxyMode.NO，则直接原样返回。接下来则是判断获取到的 scopedProxyMode 是否等于 ScopedProxyMode.TARGET_CLASS，并将比较结果赋值给 proxyTargetClass，调用 ScopedProxyCreator 的 createScopeProxy 方法。

```java
// AnnotationConfigUtils#applyScopedProxyMode
static BeanDefinitionHolder applyScopedProxyMode(
      ScopeMetadata metadata, BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {

   ScopedProxyMode scopedProxyMode = metadata.getScopedProxyMode();
   if (scopedProxyMode.equals(ScopedProxyMode.NO)) {
      return definition;
   }
   boolean proxyTargetClass = scopedProxyMode.equals(ScopedProxyMode.TARGET_CLASS);
   return ScopedProxyCreator.createScopedProxy(definition, registry, proxyTargetClass);
}
```

在 ScopedProxyCreator 的 createScopedProxy 方法中直接委派给 ScopedProxyUtils 的 createdScopedProxy 方法实现。

```java
// ScopedProxyCreator#createScopedProxy
public static BeanDefinitionHolder createScopedProxy(
      BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry, boolean proxyTargetClass) {

   return ScopedProxyUtils.createScopedProxy(definitionHolder, registry, proxyTargetClass);
}
```

在 ScopedProxyUtils 的 createScopedProxy 方法中，调用传入的 BeanDefinitionHolder 的 getBeanName 方法获取 beanName 并赋值给 originalBeanName，调用传入的 BeanDefinitionHolder 的 getBeanDefinition 方法来获取 BeanDefinition 并赋值给 targetBeanDefinition 变量，调用 getTargetBeanName 方法来处理 originalBeanName 并赋值给 targetBeanName 变量(该方法的处理逻辑就是在传入的 beanName 前拼接上“scopedTarget.”)。

重点是接下来创建的 RootBeanDefinition-proxyDefinition，传入的 beanClass 为 ScopedProxyFactoryBean 的 Class，根据 targetBeanDefinition 以及 targetBeanName 来创建 BeanDefinitionHolder 并设置到 proxyDefinition 的 decoratedDefinition 属性中，设置 targetDefinition 到 proxyDefinition 的 originatingBeanDefinition 属性中，获取 proxyDefinition 的属性元数据(getPropertyValues 方法)，将其 targetBea-nName 的属性值设置为 targetBeanName。设置将 targetBeanDefinition 的 autowireCandidate 以及 primary 设置为 false(设置这两个属性的意义在后面会分析到)。

通过调用传入的 BeanDefinitionRegistry 的 registerBeanDefinition 方法，来注册 targetDefinition，需重点关注的是，在注册 targetDefinition 时，传递的 beanName 为 targetBeanName(即拼接上“scopedTarget.”前缀的 beanName)。最后创建 BeanDefinitionHolder，指定的 beanName 却为 originalBeanName(即未拼接上“scopedTarget.”前缀的 beanName)。返回该实例。

```java
// ScopedProxyUtils#createScopedProxy
public static BeanDefinitionHolder createScopedProxy(BeanDefinitionHolder definition,
      BeanDefinitionRegistry registry, boolean proxyTargetClass) {

   String originalBeanName = definition.getBeanName();
   BeanDefinition targetDefinition = definition.getBeanDefinition();
   String targetBeanName = getTargetBeanName(originalBeanName);

   // Create a scoped proxy definition for the original bean name,
   // "hiding" the target bean in an internal target definition.
   RootBeanDefinition proxyDefinition = new RootBeanDefinition(ScopedProxyFactoryBean.class);
   proxyDefinition.setDecoratedDefinition(new BeanDefinitionHolder(targetDefinition, targetBeanName));
   proxyDefinition.setOriginatingBeanDefinition(targetDefinition);
   proxyDefinition.setSource(definition.getSource());
   proxyDefinition.setRole(targetDefinition.getRole());

   proxyDefinition.getPropertyValues().add("targetBeanName", targetBeanName);
   if (proxyTargetClass) {
      targetDefinition.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
      // ScopedProxyFactoryBean's "proxyTargetClass" default is TRUE, so we don't need to set it explicitly here.
   } else {
      proxyDefinition.getPropertyValues().add("proxyTargetClass", Boolean.FALSE);
   }

   // Copy autowire settings from original bean definition.
   proxyDefinition.setAutowireCandidate(targetDefinition.isAutowireCandidate());
   proxyDefinition.setPrimary(targetDefinition.isPrimary());
   if (targetDefinition instanceof AbstractBeanDefinition) {
      proxyDefinition.copyQualifiersFrom((AbstractBeanDefinition) targetDefinition);
   }

   // The target bean should be ignored in favor of the scoped proxy.
   targetDefinition.setAutowireCandidate(false);
   targetDefinition.setPrimary(false);

   // Register the target bean as separate bean in the factory.
   registry.registerBeanDefinition(targetBeanName, targetDefinition);

   // Return the scoped proxy definition as primary bean definition
   // (potentially an inner bean).
   return new BeanDefinitionHolder(proxyDefinition, originalBeanName, definition.getAliases());
}

private static final String TARGET_NAME_PREFIX = "scopedTarget.";

public static String getTargetBeanName(String originalBeanName) {
		return TARGET_NAME_PREFIX + originalBeanName;
}

```

把目光回到调用入口处-ClassPathBeanDefinitionScanner 的 doScan 方法中，在该方法的最后将 Annot-ationConfigUtils 的 applyScopedProxyMode 方法返回的 BeanDefinitionHolder 注册到 BeanDefinitionRegistry 中。这意味着如果某个 Bean 添加了@Scope 注解，并且将 proxyMode 设置为非 DEFAULT、NO 时，IoC 容器中将会存在该 Bean 的两个实例，一个名为“scopedTarget.beanName”其对应的是真正的 Bean 实例，另一个为“beanName”其对应的是 ScopedProxyFactoryBean 创建出来的目标 Bean 的代理对象。

```java
BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
definitionHolder =
		AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
beanDefinitions.add(definitionHolder);
registerBeanDefinition(definitionHolder, this.registry);
```

## 代码验证

使用启动类来进行 @Scope 的 proxyMode 属性测试。

```java
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ScopedBeanDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ScopedBeanDemo.class);
        context.refresh();
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        Stream.of(beanDefinitionNames)
                .forEach(
                        beanName -> {
                            Class<?> beanType = context.getType(beanName);
                            System.out.printf("beanName : %s -----> beanType：%s \n",beanName,beanType.getName());
                        });
    }
}
```

可以发现，IoC 容器中的确存在 ScopedBeanDemo 的两个 BeanDefinition 数据，一个 beanName 为“scopedTarget.scopedBeanName”，另一个为“scopeBeanDemo”。
