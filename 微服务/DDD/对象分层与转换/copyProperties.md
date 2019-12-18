# copyProperties

copyProperties 是 Spring BeanUtils 工具箱中提供的用于 Bean 之间属性复制的方法。

```java
package com.abc.demo;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeanUtils;

public class Main {

    public static void main(String[] args) {

        List<Article> articleList = Arrays.asList(new Article(1L, "hello world"));

        Member m1 = new Member("Eddy", articleList);
        Member m2 = new Member();
        BeanUtils.copyProperties(m1, m2);

        System.out.println("m1:" + m1); // m1:name:Eddy, articleList:[id:1, content:hello world]
        System.out.println("m2:" + m2); // m2:name:Eddy, articleList:[id:1, content:hello world]

    }

}

class Member {

    private String name;
    private List<Article> articleList;

    public Member() {
    }

    public Member(String name, List<Article> articleList) {
        this.name = name;
        this.articleList = articleList;
    }

    @Override
    public String toString() {
        return "name:" + name + ", " + "articleList:" + articleList;
    }

    // getters and setters
}

class Article {
    private Long id;
    private String content;

    public Article(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    @Override
    public String toString() {
        return "id:" + id + ", content:" + content;
    }

    // getters and setters
}
```

上面可以看到经由`BeanUtils.copyProperties()`复制后`m2`的属性值确实与`m1`相同，里面的`List`物件属性也会被复制，属于深拷贝(deep copy)。下面范例则是把`m1`复制到另一个类别`User`，除了`articlelist`属性名称外几乎相同。

```java
package com.abc.demo;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeanUtils;

public class Main {

    public static void main(String[] args) {

        List<Article> articleList = Arrays.asList(new Article(1L, "hello world"));

        Member m1 = new Member("Eddy", articleList);

        User u1 = new User();
        BeanUtils.copyProperties(m1, u1);

        System.out.println("m1:" + m1); // m1:name:Eddy, articleList:[id:1, content:hello world]
        System.out.println("u1:" + u1); // u1:name:Eddy, articlelist:null

    }

}

class Member {

    private String name;
    private List<Article> articleList; // <-- small camel case

    public Member() {
    }

    public Member(String name, List<Article> articleList) {
        this.name = name;
        this.articleList = articleList;
    }

    @Override
    public String toString() {
        return "name:" + name + ", " + "articleList:" + articleList;
    }

    // getters and setters
}

class Article {
    private Long id;
    private String content;

    public Article(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    @Override
    public String toString() {
        return "id:" + id + ", content:" + content;
    }

    // getters and setters
}

class User {

    private String name;
    private String articlelist; // <-- all lowercase

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "name:" + name + ", " + "articleList:" + articlelist;
    }

    // getters and setters
}

```

此时 u1.articlelist 的内容却是 null，由此可知 BeanUtils.copyProperties()只会复制属性名称相同的属性值，若属性名称不同则被忽略。

# 内部实现

BeanUtils.copyProperties()是利用 Java 的反射 reflection 来达成，以下是原始码。

```java
public abstract class BeanUtils {
    ...
    private static void copyProperties(Object source, Object target, @Nullable Class<?> editable,
            @Nullable String... ignoreProperties) throws BeansException {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
                        "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName()); // 以目的對象(target)的屬性名稱來取得來源對象(source)的屬性
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null &&
                            ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source); // 取得來源對象的屬性值
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            writeMethod.invoke(target, value); // 把來源對象的屬性值寫入目的對象的同名屬性
                        }
                        catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }
    ...
}
```

BeanUtils.copyProperties()虽然在复制 POJO 物件时非常方便，但在属性的命名上必须统一，所以系统中各属性的命名规则必须严格规范。还有个问题就是 debug 时错误不好找，所以并不推荐使用。大型程序中最好有专人专职负责维护整个系统的命名列表，中英文对照表，名词解释等，让开发人员可以直接查找使用，如果找不到就写信去申请由维护人员建立，否则就常看到同一种东西在程序中出现多种名称，这样 BeanUtils.copyProperties()就派不上用场了，更严重的是维护上常令人困惑。

- 例如顾客编号 customerId，customerID，custId，custID，clientId，clientId；
- 商品数量 quantity, qty；
- 利息收入 interestIncome，intstIncome，intrstIncome，intstRevenue；
- 一次性费用 onetimeExpense，onceExpense，oneExpense 等。
