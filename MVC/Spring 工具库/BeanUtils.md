# BeanUtils

# copyProperties

以下是针对相同类型的 Bean 处理的例子：

```java
public class CopyPropsToSameType {
    public static void main (String[] args) {
        BeanWrapper bw = new BeanWrapperImpl(new TestBean());
        bw.setPropertyValue("aString", "someString");
        bw.setPropertyValue("anInt", 3);

        TestBean testBean2 = new TestBean();

        BeanUtils.copyProperties(bw.getWrappedInstance(), testBean2);

        System.out.println(testBean2);
    }
}

// TestBean{aString='someString', anInt=3, date=Mon May 01 16:08:07 CDT 2017}
```

以下是针对不同类型的 Bean 处理的例子：

```java
public class CopyPropsToDifferentType {
    public static void main (String[] args) {
        BeanWrapper bw = new BeanWrapperImpl(new TestBean());
        bw.setPropertyValue("aString", "someString");
        bw.setPropertyValue("anInt", 3);

        TestBeanDifferent testBean2 = new TestBeanDifferent();

        //only properties of same name will be copied
        BeanUtils.copyProperties(bw.getWrappedInstance(), testBean2);

        System.out.println(testBean2);
    }

    private static class TestBeanDifferent {
        private String aString;
        private int differentInt;

        public String getAString () {
            return aString;
        }

        public void setAString (String aString) {
            this.aString = aString;
        }

        public int getDifferentInt () {
            return differentInt;
        }

        public void setDifferentInt (int differentInt) {
            this.differentInt = differentInt;
        }

        @Override
        public String toString () {
            return "TestBeanDifferent{" +
                                "aString='" + aString + '\'' +
                                ", differentInt=" + differentInt +
                                '}';
        }
    }
}

// TestBeanDifferent{aString='someString', differentInt=0}
```

# getPropertyDescriptors

```java
public class PropDescriptorExample {
    public static void main (String[] args) throws IntrospectionException {
        PropertyDescriptor[] actual = Introspector.getBeanInfo(TestBean.class)
                                                  .getPropertyDescriptors();
        PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(TestBean.class);

        System.out.println(Arrays.toString(actual));
        System.out.println(Arrays.toString(descriptors));
    }
}

// [java.beans.PropertyDescriptor[name=AString; propertyType=class java.lang.String; readMethod=public java.lang.String com.logicbig.example.TestBean.getAString(); writeMethod=public void com.logicbig.example.TestBean.setAString(java.lang.String)], java.beans.PropertyDescriptor[name=anInt; propertyType=int; readMethod=public int com.logicbig.example.TestBean.getAnInt(); writeMethod=public void com.logicbig.example.TestBean.setAnInt(int)], java.beans.PropertyDescriptor[name=class; propertyType=class java.lang.Class; readMethod=public final native java.lang.Class java.lang.Object.getClass()], java.beans.PropertyDescriptor[name=date; propertyType=class java.util.Date; readMethod=public java.util.Date com.logicbig.example.TestBean.getDate()]]
```

# resolveSignature

```java
public class ResolveSignature {

    public static void main (String[] args) throws
                        InvocationTargetException, IllegalAccessException {
        Method doSomething = BeanUtils.resolveSignature("aMethod(java.lang.String, int)",
                            LocalBean.class);
        doSomething.invoke(new LocalBean(), "some string value", 100);

        doSomething = BeanUtils.resolveSignature("aMethod(java.lang.Integer)",
                            LocalBean.class);
        doSomething.invoke(new LocalBean(), 200);
    }

    private static class LocalBean {
        public void aMethod (String str, int anInt) {
            System.out.println(str);
            System.out.println(anInt);
        }

        public void aMethod (Integer anInt) {
            System.out.println(anInt);
        }
    }
}

/**
    some string value
    100
    200
*/

public class ResolveSignature2 {

    public static void main (String[] args) throws
                        InvocationTargetException, IllegalAccessException {
        Method doSomething = BeanUtils.resolveSignature("doSomething",
                            LocalBean.class);
        doSomething.invoke(new LocalBean());
    }

    private static class LocalBean {
        public void doSomething () {
            System.out.println("-- doing something -- ");

        }
    }
}

// -- doing something --
```
