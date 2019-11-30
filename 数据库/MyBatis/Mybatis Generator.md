# MyBatis Generator

# 基础使用

MyBatis Generator 可以直接通过命令行调用：

```sh
java -jar mybatis-generator-core-x.x.x.jar -configfile generatorConfig.xml
```

或者添加 Maven 插件：

```xml
<project ...>
    ...
    <build>
      ...
      <plugins>
      ...
      <plugin>
        <groupId>org.mybatis.generator</groupId>
        <artifactId>mybatis-generator-maven-plugin</artifactId>
        <version>1.3.7</version>
        <configurationFile>src/main/resources/generatorConfig.xml</configurationFile>
      </plugin>
      ...
    </plugins>
    ...
  </build>
  ...
</project>
```

然后在项目目录下执行 mvn 命令：

```sh
$ mvn mybatis-generator:generate
```

# 配置详解

generatorConfig.xml 的基础结构如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<generatorConfiguration>
    <!--导入属性配置 -->
    <properties resource="generator.properties"></properties>

    <!--指定特定数据库的jdbc驱动jar包的位置 -->
    <classPathEntry location="${jdbc.driverLocation}"/>

    <!--一或多个-->
    <context id="default" targetRuntime="MyBatis3">
     ...
    </context>
</generatorConfiguration>
```

`<context>` 元素用于指定生成一组对象的环境。例如指定要连接的数据库，要生成对象的类型和要处理的数据库中的表。运行 MBG 的时候还可以指定要运行的 `<context>`。该元素只有一个必选属性 id，用来唯一确定一个 `<context>` 元素，该 id 属性可以在运行 MBG 的使用。`<context>` 还包含了 defaultModelType 属性，用来指定 MBG 生成实体类的规则：

- conditional: 默认设置，这个模型和下面的 hierarchical 类似，除了如果那个单独的类将只包含一个字段，将不会生成一个单独的类。因此,如果一个表的主键只有一个字段,那么不会为该字段生成单独的实体类,会将该字段合并到基本实体类中。
- flat: 该模型为每一张表只生成一个实体类，这个实体类包含表中的所有字段。
- hierarchical: 如果表有主键,那么该模型会产生一个单独的主键实体类,如果表还有 BLOB 字段， 则会为表生成一个包含所有 BLOB 字段的单独的实体类,然后为所有其他的字段生成一个单独的实体类。MBG 会在所有生成的实体类之间维护一个继承关系。

## 上下文配置

MBG 配置中的其他几个元素，基本上都是 `<context>` 的子元素，这些子元素（有严格的配置顺序）包括：

- `<property>` (0 个或多个)

property 属性能够用于处理数据库表中的特殊字符，譬如 SQL 关键字的处理等。

- `<plugin>` (0 个或多个)

plugin 元素用来定义一个插件。插件用于扩展或修改通过 MyBatis Generator (MBG)代码生成器生成的代码。

- `<commentGenerator>` (0 个或 1 个)

commentGenerator 旨在创建 class 时，对注释进行控制。一般情况下由于 MBG 生成的注释信息没有任何价值，而且有时间戳的情况下每次生成的注释都不一样，使用版本控制的时候每次都会提交，因而一般情况下我们都会屏蔽注释信息，可以如下配置：

```xml
<commentGenerator>
    <property name="suppressAllComments" value="true"/>
    <property name="suppressDate" value="true"/>
</commentGenerator>
```

- `<jdbcConnection>` (1 个)

jdbcConnection 用于指定数据库连接信息，该元素必选，并且只能有一个。配置该元素只需要注意如果 JDBC 驱动不在 classpath 下，就需要通过 `<classPathEntry>` 元素引入 jar 包，这里推荐将 jar 包放到 classpath 下。

```xml
<jdbcConnection driverClass="com.mysql.jdbc.Driver"
                connectionURL="jdbc:mysql://localhost:3306/test"
                userId="root"
                password="">
</jdbcConnection>

<!--或者使用外部依赖-->
<jdbcConnection driverClass="${jdbc.driverClass}" connectionURL="${jdbc.connectionURL}" userId="${jdbc.userId}" password="${jdbc.password}">
</jdbcConnection>
```

## 实体类解析配置

- `<javaTypeResolver>` (0 个或 1 个)

这个元素的配置用来指定 JDBC 类型和 Java 类型如何转换。该元素提供了一个可选的属性 type，和`<commentGenerator>`比较类型，提供了默认的实现 DEFAULT，一般情况下使用默认即可，需要特殊处理的情况可以通过其他元素配置来解决，不建议修改该属性。可以配置的属性为 forceBigDecimals，该属性可以控制是否强制 DECIMAL 和 NUMERIC 类型的字段转换为 Java 类型的 java.math.BigDecimal,默认值为 false，一般不需要配置。

```xml
<javaTypeResolver >
    <!-- 如果精度>0或者长度>18，就会使用java.math.BigDecimal -->
    <!-- 如果精度=0并且10<=长度<=18，就会使用java.lang.Long -->
    <!-- 如果精度=0并且5<=长度<=9，就会使用java.lang.Integer -->
    <!-- 如果精度=0并且长度<5，就会使用java.lang.Short -->
    <property name="forceBigDecimals" value="false" />
</javaTypeResolver>
```

- `<javaModelGenerator>` (1 个)

Model 模型生成器,用来生成含有主键 key 的类，记录类 以及查询 Example 类；targetPackage 指定生成的 model 生成所在的包名，targetProject 指定在该项目下所在的路径。

```xml
<javaModelGenerator targetPackage="wx.model.po" targetProject="src/main/java">
    <!-- 是否对model添加 构造函数 -->
    <property name="constructorBased" value="true"/>

    <!-- 是否允许子包，即targetPackage.schemaName.tableName -->
    <property name="enableSubPackages" value="false"/>

    <!-- 建立的 Model 对象是否不可改变，即生成的Model对象不会有 setter方法，只有构造方法 -->
    <property name="immutable" value="true"/>

    <!-- 给 Model 添加一个父类 -->
    <property name="rootClass" value="wx.model.Hello"/>

    <!-- 是否对类 CHAR 类型的列的数据进行 trim 操作 -->
    <property name="trimStrings" value="true"/>
</javaModelGenerator>
```

- `<sqlMapGenerator>` (0 个或 1 个)

Mapper 映射文件生成所在的目录 为每一个数据库的表生成对应的 SqlMap 文件：

```xml
<sqlMapGenerator targetPackage="wx.map.domain" targetProject="src/main/java">
    <property name="enableSubPackages" value="false"/>
</sqlMapGenerator>
```

- `<javaClientGenerator>` (0 个或 1 个)

客户端代码，生成易于使用的针对 Model 对象和 XML 配置文件的代码：

```xml
<!--
  type="ANNOTATEDMAPPER",生成Java Model 和基于注解的Mapper对象
  type="MIXEDMAPPER",生成基于注解的Java Model 和相应的Mapper对象
  type="XMLMAPPER",生成SQLMap XML文件和独立的Mapper接口
-->
<javaClientGenerator targetPackage="wx.dao" targetProject="src/main/java" type="MIXEDMAPPER">
    <property name="enableSubPackages" value=""/>
    <!--
            定义Maper.java 源代码中的ByExample() 方法的可视性，可选的值有：
            public;
            private;
            protected;
            default
            注意：如果 targetRuntime="MyBatis3",此参数被忽略
      -->
    <property name="exampleMethodVisibility" value=""/>
    <!--
      方法名计数器
      Important note: this property is ignored if the target runtime is MyBatis3.
      -->
    <property name="methodNameCalculator" value=""/>

    <!--
        为生成的接口添加父接口
    -->
    <property name="rootInterface" value=""/>
</javaClientGenerator>
```

- `<table>` (1 个或多个)

```xml
<table tableName="lession" schema="mybatis" domainObjectName="ObjName">
    <!-- optional
         自动生成的键值(identity,或者序列值)，如果指定此元素，MBG将会生成<selectKey>元素，然后将此元素插入到SQL Map的<insert> 元素之中，sqlStatement 的语句将会返回新的值；如果是一个自增主键的话，你可以使用预定义的语句, 或者添加自定义的SQL语句. 预定义的值如下: MySql: SELECT LAST_INSERT_ID()

    -->
    <generatedKey column="id" sqlStatement="Mysql" identity="" type=""/>

    <!-- optional.
            列的命名规则：
            MBG使用 <columnRenamingRule> 元素在计算列名的对应名称之前，先对列名进行重命名，
            作用：一般需要对BUSI_CLIENT_NO 前的BUSI_进行过滤
            支持正在表达式
              searchString 表示要被换掉的字符串
              replaceString 则是要换成的字符串，默认情况下为空字符串，可选
    -->
    <columnRenamingRule searchString="" replaceString=""/>

    <!-- optional.告诉 MBG 忽略某一列
            column，需要忽略的列
            delimitedColumnName:true ,匹配column的值和数据库列的名称 大小写完全匹配，false 忽略大小写匹配
            是否限定表的列名，即固定表列在Model中的名称
    -->
    <ignoreColumn column="PLAN_ID"  delimitedColumnName="true" />

    <!--optional.覆盖MBG对 Model 的生成规则
          column: 数据库的列名
          javaType: 对应的Java数据类型的完全限定名
          在必要的时候可以覆盖由JavaTypeResolver计算得到的java数据类型. For some databases, this is necessary to handle "odd" database types (e.g. MySql's unsigned bigint type should be mapped to java.lang.Object).
          jdbcType:该列的JDBC数据类型(INTEGER, DECIMAL, NUMERIC, VARCHAR, etc.)，该列可以覆盖由JavaTypeResolver计算得到的Jdbc类型，对某些数据库而言，对于处理特定的JDBC 驱动癖好 很有必要(e.g. DB2's LONGVARCHAR type should be mapped to VARCHAR for iBATIS).
          typeHandler:

    -->
    <columnOverride column="" javaType="" jdbcType="" typeHandler=""	delimitedColumnName="" />

</table>
```

## Lombok

如果我们希望在生成的实体类中支持 Lombok，那可以参考 [mybatis-generator-lombok-plugin](https://github.com/softwareloop/mybatis-generator-lombok-plugin) 等项目。在 pom.xml 中可以添加该插件：

```xml
<plugin>
    <groupId>org.mybatis.generator</groupId>
    <artifactId>mybatis-generator-maven-plugin</artifactId>
    <version>${mybatis.generator.version}</version>
    <configuration>
        <overwrite>true</overwrite>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>com.softwareloop</groupId>
            <artifactId>mybatis-generator-lombok-plugin</artifactId>
            <version>1.0</version>
        </dependency>
    </dependencies>
</plugin>
```

然后在 Generator 的配置中添加该插件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>
    <context id="example"
             targetRuntime="MyBatis3Simple"
             defaultModelType="flat">
        <!-- include the plugin -->
        <plugin type="com.softwareloop.mybatis.generator.plugins.LombokPlugin">

             <!-- enable annotations -->
             <property name="builder" value="true"/>
             <!-- annotation's option(boolean) -->
             <property name="builder.fluent" value="true"/>
             <!-- annotation's option(String) -->
             <property name="builder.builderMethodName" value="myBuilder"/>

             <property name="accessors" value="true"/>
             <!-- annotation's option(array of String) -->
             <property name="accessors.prefix" value="m_, _"/>

             <!-- disable annotations -->
             <property name="allArgsConstructor" value="false"/>
        </plugin>

        <!-- other configurations -->

    </context>
</generatorConfiguration>
```

# 模板使用

对于 MyBatis Generator 生成的代码模板，不建议修改生成后的模板，这样在数据库发生变化时候可以直接重新生成，根据错误提示修改对应代码。MyBatis Generator 为我们自动生成了 Model, Mapper 与 Example 文件，其中 Example 能够被用于构建搜索条件，譬如：

```java
// 模糊搜索用户名
String name = "明";
UserExample ex = new UserExample();
ex.createCriteria().andNameLike('%'+name+'%');
List<User> userList = userDao.selectByExample(ex);

// 通过某个字段排序
String orderByClause = "id DESC";
UserExample ex = new UserExample();
ex.setOrderByClause(orderByClause);
List<User> userList = userDao.selectByExample(ex);

// 条件搜索，不确定条件的个数
UserExample ex = new UserExample();
Criteria criteria = ex.createCriteria();
if(StringUtils.isNotBlank(user.getAddress())){
	criteria.andAddressEqualTo(user.getAddress());
}
if(StringUtils.isNotBlank(user.getName())){
	criteria.andNameEqualTo(user.getName());
}
List<User> userList = userDao.selectByExample(ex);

// 分页搜索列表
pager.setPageNum(1);
pager.setPageSize(5);
UserExample ex = new UserExample();
ex.setPage(pager);
List<User> userList = userDao.selectByExample(ex);
```

## Mapper 类

Mapper 也就是数据库访问类 DAO：

```java
package org.dev.repo.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.dev.repo.dto.OrderDTO;
import org.dev.repo.dto.OrderDTOExample;

public interface OrderDTOMapper {
    //通过条件计数
    int countByExample(OrderDTOExample example);
    //通过条件删除
    int deleteByExample(OrderDTOExample example);
    //通过主键删除
    int deleteByPrimaryKey(Long id);
    //插入DTO
    int insert(OrderDTO record);
    //dto非空字段插入
    int insertSelective(OrderDTO record);
    //条件查询,返回结果包括text等大字段
    List<OrderDTO> selectByExampleWithBLOBs(OrderDTOExample example);
    //条件查询,返回结果不包括text等大字段
    List<OrderDTO> selectByExample(OrderDTOExample example);
    //通过主键查询
    OrderDTO selectByPrimaryKey(Long id);
    //条件更新,只更新dto中非空字段的值
    int updateByExampleSelective(@Param("record") OrderDTO record, @Param("example") OrderDTOExample example);
    //条件更新全部字段,并包含text等大字段
    int updateByExampleWithBLOBs(@Param("record") OrderDTO record, @Param("example") OrderDTOExample example);
    //条件更新dto全部字段
    int updateByExample(@Param("record") OrderDTO record, @Param("example") OrderDTOExample example);
    //通过主键更新,dto非空字段
    int updateByPrimaryKeySelective(OrderDTO record);
    //通过主键更新,dto全部字段,包含text等大字段
    int updateByPrimaryKeyWithBLOBs(OrderDTO record);
    //通过主键更新,dto全部字段,不包含text等大字段
    int updateByPrimaryKey(OrderDTO record);
}
```

- 最常用的动态查询方法 selectByExample(OrderDTOExample example),简单通过构造不同的 example 对象就可以实现.

- 后缀为 Selective 的插入和更新方法,只处理 dto 对象的非空字段,例如经常我们只想更新某些记录的部分字段,那么 updateByExampleSelective 方法最合适不过.

- 后缀为 WithBLOBs 的方法是对大字段的处理方法,与常规处理方法分离。

使用 Example 进行动态查询也是颇为便捷的：

```java
LocalDate ld=LocalDate.of(2017,1,1);
Instant instant = Instant.from(ld);
Date date = Date.from(instant);

OrderDTOExample example=new OrderDTOExample();
//设置排序字段
example.setOrderByClause(" order by create_time desc ");

//创建查询条件对象
example.createCriteria().andCustomerIdEqualTo(123456L).
andCreateTimeGreaterThan(date).
andPaymentAmtGreaterThan(300L);

//执行查询
return orderMapper.selectByExample(example);
```

# Example

Example 类中有三个内部类 Criteria，Criterion，GeneratedCriteria：

- Criteria 是查询条件类,继承 GeneratedCriteria 类,,我们构造查询条件就是使用它的方法,它由 Example 对象的 createCriteria()方法来创建。

- Criterion 是存储字段的条件,例如 id=5 这个条件就是存储在这个类对象的属性中。

- GeneratedCriteria 是 Criteria 父类,Example 对象映射表的每个字段的 12 个方法都属于这个类,它保存一组 Criterion 对象。

动态查询的 sqlmapper.xml 片段如下：

```xml
<select id="selectByExample" parameterType="org.dev.repo.dto.OrderDTOExample" resultMap="BaseResultMap">
select
<!--对应Example中的distinct属性 -->
<if test="distinct">
    distinct
</if>
<!-- sql片段,order表字段 -->
<include refid="Base_Column_List" />
from order
<if test="_parameter != null">
    <!--这里是重点,sql片段,查询条件-->
    <include refid="Example_Where_Clause" />
</if>
<!--对应Example中的orderByClause属性 -->
<if test="orderByClause != null">
    order by ${orderByClause}
</if>
</select>
```

sqlmapper.xml 是如何构造动态查询条件：

```xml
<sql id="Example_Where_Clause">
    <where>
      <!-- 遍历Example中的oredCriteria属性中的元素Criteria,多个Criteria组成or条件 -->
      <foreach collection="oredCriteria" item="criteria" separator="or">
      <!-- 如果Example.Criteria对象存在一个或多个Criterion对象那么进行遍历,多个Criterion构成and条件 -->
        <if test="criteria.valid">
          <trim prefix="(" prefixOverrides="and" suffix=")">
            <foreach collection="criteria.criteria" item="criterion">
              <choose>
                <!-- 无值条件,例如name is null -->
                <when test="criterion.noValue">
                  and ${criterion.condition}
                </when>
                <!-- 单值条件,例如 name='张三' -->
                <when test="criterion.singleValue">
                  and ${criterion.condition} #{criterion.value}
                </when>
                <!-- 双值条件,对应between -->
                <when test="criterion.betweenValue">
                  and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}
                </when>
                <!-- 多值条件,对应in -->
                <when test="criterion.listValue">
                  and ${criterion.condition}
                  <foreach close=")" collection="criterion.value" item="listItem" open="(" separator=",">
                    #{listItem}
                  </foreach>
                </when>
              </choose>
            </foreach>
          </trim>
        </if>
      </foreach>
    </where>
  </sql>
```

通过以上 sqlmapper.xml 我们可以看出 Example 对象及它的内部对象属性在运行时被 mybatis 框架循环遍历解析成动态 sql 语句，与我们手写 sql 语句并无差别。
