# Spring Boot Production Boilerplate, with Mybatis, Swagger, JWT & RBAC

[Backend-Boilerplates https://url.wx-coder.cn/yHBlr](https://url.wx-coder.cn/yHBlr) 提供了十余种不同场景、用途下的 Spring 项目模板，本项目则是 [Spring Boot 与 Spring Cloud 微服务实战 https://url.wx-coder.cn/dDnII](https://url.wx-coder.cn/dDnII) 系列文章中笔者关于工程实践相关汇总的落地。如果想进一步了解该模板在实际电商等项目中的应用，建议参考 [Reinvent-Mall](https://github.com/wx-chevalier/Reinvent-Mall) 等实战项目。

# Development | 项目开发与运行

# IDE | 开发环境

-   开发环境配置：Intellij Idea, Lombok Plugin, google-java-format

-   启动测试用数据库，参考 [mysql-mall-matrix https://url.wx-coder.cn/Lmzp3](https://url.wx-coder.cn/Lmzp3)

## Dev & Debug

```sh
# with Gradle
$ ./gradlew build -t
$ ./gradlew build -t -x test
$ ./gradlew bootRun

# with Maven
$ mvn boot:run
```

## Mybatis Generator

`mbgenerator` 任务配置在 [gralde/mbgenerator.gradle](gralde/mbgenerator.gradle) 中。基础依赖有：

```bash
# with Gradle
compile {
    mbgenerator('org.mybatis.generator:mybatis-generator-core:1.3.7')
    mbgenerator('tk.mybatis:mapper:3.3.9')
}

# with Maven
```

参考配置文件完成配置后，就可以根据 sbp-mbg 子项目下的 `src/main/resources/generatorConfiguration.xml` 文件来执行 DAO 代码生成操作：

```bash
# 定位到 sbp-mgb 子目录
$ cd sbp-mbg

# with Gradle
$ gradle mbgenerator
$ gradle mbgenerator -PmbgeneratorConfigFile=/path/to/generatorConfig.xml

```

默认配置没有在 git 中管理，建议每次生成时修改默认配置，其中只包含要生成的表(发生改变的表)，生成代码之后可以使用相关代码质量工具检查后适当编辑：

```
./gradlew spotlessCheck spotBugsMain
```

## Code Style | 代码质量

下载导入风格配置： File -> Settings -> Editor -> Code Style -> Manage -> Import -> IntelliJ IDEA Code Style XML

```
# Google Style (Intellij Idea Code Style Configuration)
wget https://raw.githubusercontent.com/google/styleguide/gh-pages/intellij-java-google-style.xml
```

建议添加 git `pre-commit` hook，对于，本项目

```sh
cat << EOF > ../../.git/hooks/pre-commit
#!/bin/sh -x

cd agent/java && ./gradlew spotlessCheck spotBugsMain spotBugsTest && cd ../..
EOF
chmod u+x ../../.git/hooks/pre-commit
```

项目中使用了 spotless 与 spotBugs 来进行代码的静态错误与语法规范检查，在编译前后也会自动执行检查操作：

```bash
$ ./gradlew spotbugsMain
$ ./gradlew spotlessCheck
$ ./gradlew spotlessApply # 修正发现的语法样式问题
```

## Deployment | 部署

# Project Structure | 项目结构与设计

-   sbp-start: 项目的实际入口，用于启动项目的上下文，以及项目内的 Controller 以及 GraphQL Resolver 等定义。

-   sbp-api: 核心的模型类与接口定义，可作为二方包提供给第三方接入。

-   sbp-mbg: Mybatis Generator 生成的数据访问层；这里将 MBG 独立为单独项目，就是为了表明系统暴露给第三方的 DTO 或者 VO 需要自己添加兼容层，而不可直接将生成的代码暴露。

-   sbp-service: 项目的核心服务。

-   sbp-deploy: 部署相关的代码与脚本。
