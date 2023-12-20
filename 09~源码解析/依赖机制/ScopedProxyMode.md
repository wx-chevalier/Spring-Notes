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

可以发现，IoC 容器中的确存在 ScopedBeanDemo 的两个 BeanDefinition 数据，一个 beanName 为“scopedTarget.scopedBeanName”，另一个为“scopeBeanDemo”。如上面的分析结果，IoC 容器中存在两个相同类型的 Bean，那么当我们通过 BeanFactory 的 getBean(Class)方法来查找时，是会抛出异常呢？还是正常返回呢？如果正常返回，那么该返回那个呢？

```java
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.beans.Introspector;
import java.lang.reflect.Field;

@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ScopedBeanDemo {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ScopedBeanDemo.class);
        context.refresh();
        // 根据 ScopedBeanDemo 类型来查找
        ScopedBeanDemo byType = context.getBean(ScopedBeanDemo.class);
        // 根据 ScopedBeanDemo 在IoC容器中的BeanName来进行查找 -> 其底层也是通过 Java Beans 中的
        // Introspector#decapitalize方法来生成BeanName
        ScopedBeanDemo byName =
                (ScopedBeanDemo) context.getBean(Introspector.decapitalize("ScopedBeanDemo"));
        // 在 ScopedBeanDemo 在IoC容器中的BeanName 前面拼接上 ScopedProxyUtils#TARGET_NAME_PREFIX 字段的值
        Field field = ScopedProxyUtils.class.getDeclaredField("TARGET_NAME_PREFIX");
        field.setAccessible(true);
        Object value = field.get(null);
        ScopedBeanDemo byScopedName =
                (ScopedBeanDemo)
                        context.getBean(value + Introspector.decapitalize("ScopedBeanDemo"));
        System.out.println("根据ScopedBeanDemo类型查找到的：" + byType.getClass());
        System.out.println("根据ScopedBeanDemo名称查找到的：" + byName.getClass());
        System.out.println("根据scopedTarget.ScopedBeanDemo名称查找到的：" + byScopedName.getClass());
        // 关闭Spring 应用上下文
        context.close();
    }
}
```

可以发现无论是根据类型还是根据 beanName 来进行 IoC 容器返回的始终是是代理后的对象。只有按其拼接的规则来拼接 beanName 后(在 beanName 前拼接上“scopedTarget.”前缀)，再使用 BeanFactory 的 getBean(String)方法来查找才会返回原始对象。按照 beanName 来进行查找，IoC 容器会返回代理对象，这点可以理解，因为在 ScopedProxyUtils 的 createScopedProxy 方法偷梁换柱，将原始的 beanName 对应的 BeanDefinition 替换为代理 BeanDefinition，所以查找根据原始 beanName 查找出来的 bean 为代理 Bean 就不奇怪了，那么为什么根据类型来查找返回的依然是代理 Bean 呢？

这里先说下结论：是因为前面在 ScopedProxyUtils 的 createScopedProxy 方法中将原始的 BeanDefinit-ion(targetDefinition)的 autowireCandidate 设置为 false 导致的。

```java
// The target bean should be ignored in favor of the scoped proxy.
targetDefinition.setAutowireCandidate(false);
targetDefinition.setPrimary(false);
```

下面我们来分析下 BeanFactory 的 getBean(Class)方法。AsbtractApplicationContext 实现了该方法，在该方法中首先来对 BeanFactory 实现类实例的存活状态进行校验。之后就是调用 BeanFactory 实现类实例的 getBean 方法，传入要获取的 Class。

```java
// AbstractApplicationContext#getBean(java.lang.Class<T>)
public <T> T getBean(Class<T> requiredType) throws BeansException {
   assertBeanFactoryActive();
   return getBeanFactory().getBean(requiredType);
}

```

在 DefaultListableBeanFactory 实现的 getBean 方法中，调用 resolveBean 方法来根据类型获取，如果该方法的返回值为 null，抛出 NoSuchBeanDefinitionException 异常。可以看出的是 resolveBean 方法并不会主动抛出异常，而是 getBean 方法抛出的异常，这一点很重要，因为包括 BeanFactory 提供的安全查找 Bean 的 getBeanProvider 方法底层也是基于该方法进行实现，这里就不再展开分析了。

```java
// DefaultListableBeanFactory#getBean(java.lang.Class<T>, java.lang.Object...)
public <T> T getBean(Class<T> requiredType, @Nullable Object... args) throws BeansException {
   Assert.notNull(requiredType, "Required type must not be null");
   Object resolved = resolveBean(ResolvableType.forRawClass(requiredType), args, false);
   if (resolved == null) {
      throw new NoSuchBeanDefinitionException(requiredType);
   }
   return (T) resolved;
}
```

在 resolveBean 方法中，调用 resolveNamedBean 方法来进行查找，如果该方法返回值不为 null，则直接返回，否则获取当前 IoC 容器的父容器(如果有)，层层查找。

```java
// DefaultListableBeanFactory#resolveBean
private <T> T resolveBean(ResolvableType requiredType, @Nullable Object[] args, boolean nonUniqueAsNull) {
   NamedBeanHolder<T> namedBean = resolveNamedBean(requiredType, args, nonUniqueAsNull);
   if (namedBean != null) {
      return namedBean.getBeanInstance();
   }
   BeanFactory parent = getParentBeanFactory();
   if (parent instanceof DefaultListableBeanFactory) {
      return ((DefaultListableBeanFactory) parent).resolveBean(requiredType, args, nonUniqueAsNull);
   }
   else if (parent != null) {
      ObjectProvider<T> parentProvider = parent.getBeanProvider(requiredType);
      if (args != null) {
         return parentProvider.getObject(args);
      }
      else {
         return (nonUniqueAsNull ? parentProvider.getIfUnique() : parentProvider.getIfAvailable());
      }
   }
   return null;
}
```

在 resolveNamedBean 方法中，首先根据 getNamesForType 方法来获取指定类型的所有 beanName，该方法的返回值是一个数组。结合前面的代码可以得出这里获取到的 candidateNames 有两个，分别为：scopedTarget.scopedBeanDemo 和 scopedBeanDemo。

因此会进入第一个判断即 candidateNames 的长度大于 1，遍历 candidateNames 集合，对于遍历到的每一个 beanName，通过 containsBeanDefinition 方法来判断当前 IoC 容器中是否包含指定 beanName 的 BeanDefinition 数据(注意这里是对结果进行取反，因此判断失败)，第二个判断是根据 beanName 获取到对应的 BeanDefinition 实例后，然后调用其 isAutowireCandidate 方法，注意前面我们已经分析过在 ScopedProxyUtils 的 createScopedProxy 方法将 targetDefinition 的 autowireCandidate 属性设置为 false，因此真正的 BeanDefinition 是不会被作为候选的 BeanDefinition，反而是代理 BeanDefinition 会作为候选的 BeanDefinition。

next，判断 candidateNames 数组的长度是否等等于 1，如果判断成立，则调用 getBean 方法来根据 beanName 获取，并将方法返回结果构建为 NamedBeanHolder 返回。

```java
// DefaultListableBeanFactory#resolveNamedBean
private <T> NamedBeanHolder<T> resolveNamedBean(
      ResolvableType requiredType, @Nullable Object[] args, boolean nonUniqueAsNull) throws BeansException {

   Assert.notNull(requiredType, "Required type must not be null");
	// getBean(ScopedBeanDemo.class) -> candidateNames 中存在两个beanName，分别为
	// “scopedTarget.scopedBeanDemo”以及“scopedBeanDemo”。
   String[] candidateNames = getBeanNamesForType(requiredType);

   if (candidateNames.length > 1) {
      List<String> autowireCandidates = new ArrayList<>(candidateNames.length);
      for (String beanName : candidateNames) {
				// 由于真正的ScopedBeanDemo的BeanDefinition的autowireCandidate属性被设置为false，
				// 因此这里被保存到autowireCandidates集合中的是代理Bean的BeanDefinition
         if (!containsBeanDefinition(beanName) || getBeanDefinition(beanName).isAutowireCandidate()) {
            autowireCandidates.add(beanName);
         }
      }
      if (!autowireCandidates.isEmpty()) {
         candidateNames = StringUtils.toStringArray(autowireCandidates);
      }
   }
		// 如果candidateNames的长度为1，通过getBean方法来触发初始化或者从缓存中获取并构建为
		// NamedBeanHolder 对象返回。
   if (candidateNames.length == 1) {
      String beanName = candidateNames[0];
      return new NamedBeanHolder<>(beanName, (T) getBean(beanName, requiredType.toClass(), args));
   } else if (candidateNames.length > 1) {
      Map<String, Object> candidates = new LinkedHashMap<>(candidateNames.length);
      for (String beanName : candidateNames) {
         if (containsSingleton(beanName) && args == null) {
            Object beanInstance = getBean(beanName);
            candidates.put(beanName, (beanInstance instanceof NullBean ? null : beanInstance));
         } else {
            candidates.put(beanName, getType(beanName));
         }
      }
      String candidateName = determinePrimaryCandidate(candidates, requiredType.toClass());
      if (candidateName == null) {
         candidateName = determineHighestPriorityCandidate(candidates, requiredType.toClass());
      }
      if (candidateName != null) {
         Object beanInstance = candidates.get(candidateName);
         if (beanInstance == null || beanInstance instanceof Class) {
            beanInstance = getBean(candidateName, requiredType.toClass(), args);
         }
         return new NamedBeanHolder<>(candidateName, (T) beanInstance);
      }
      if (!nonUniqueAsNull) {
         throw new NoUniqueBeanDefinitionException(requiredType, candidates.keySet());
      }
   }

   return null;
}
```

# 总结

@Scope 注解中的 proxyMode 方法值指示了 IoC 容器要不要为 Bean 创建代理，如何创建代理，是使用 JDK 的动态代理还是使用 CGLIB？我们通过源码也了解到 ScopedProxyMode 的 DEFAULT 和 NO 作用是一样的，如果配置为 INTERFACES 或 TARGET_CLASS，在 ScopedProxyUtils 的 createScopedProxy 方法中将会为目标 Bean 创建一个 ScopedProxyFactoryBean 的 BeanDefinition，并使用目标 Bean 的 beanName 来注册这个 BeanDefinition，将目标 Bean 的 beanName 拼接上“ScopedTarget.”前缀来注册目标 Bean 的 BeanDefinition。

同时将目标 BeanDefinition 的 autowireCandidate 属性设置为 false，以此来确保 IoC 容器在查找该类型的单个 Bean 时(getBean 方法)不会返回原始 Bean 实例，而是返回经过代理后的 Bean 实例。
