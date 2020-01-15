![Spring 题图](https://s2.ax1x.com/2019/09/03/nFQaut.png)

# Spring 微服务实践

Spring 的设计目标是为我们提供一个一站式的轻量级应用开发平台，抽象了应用开发中遇到的共性问题。作为平台，它考虑到了企业应用资源的使用，比如数据的持久化、数据集成、事务处理、消息中间件、分布式式计算等高效可靠处理企业数据方法的技术抽象。开发过程中的共性问题，Spring 封装成了各种组件，而且 Spring 通过社区，形成了一个开放的生态系统，比如 Spring Security 就是来源于一个社区贡献。

使用 Spring 进行开发，对开发人员比较轻量，可以使用 POJO 和 Java Bean 的开发方式，使应用面向接口开发，充分支持了面向对象的设计方法。通过 IOC 容器减少了直接耦合，通过 AOP 以动态和非侵入的方式增加了服务的功能，为灵活选取不同的服务实现提供了基础，这也是 Spring 的核心。轻量级是相对于传统 J2EE 而言的，传统的 J2EE 开发，需要依赖按照 J2EE 规范实现的 J2EE 应用服务器，设计和实现时，需要遵循一系列的接口标准，这种开发方式耦合性高，使应用在可测试性和部署上都有影响，对技术的理解和要求相对较高。

![Spring](https://s2.ax1x.com/2019/09/03/nkYetx.png)

本篇是《[服务端开发实践与工程架构](https://ng-tech.icu/Backend-Series/#/)》系列文章的一部分，关联的示例代码请参考 [java-snippets](https://github.com/Dev-Snippets/java-snippets)，[spring-examples](https://github.com/BE-Kits/spring-snippets)。

> 如果你是才入门的新手，那么建议首先阅读 《[Java Series]()》、《[数据结构与算法]()》。如果你已经有了一定的经验，想了解更多工程架构方面的知识，那么建议阅读 []()。如果你想寻求分布式系统的解决方案，那么建议阅读 []()。

# About

## Copyright

<img src="https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-lightgrey.svg"/><img src="https://parg.co/bDm" />

笔者所有文章遵循 [知识共享 署名-非商业性使用-禁止演绎 4.0 国际许可协议](https://creativecommons.org/licenses/by-nc-nd/4.0/deed.zh)，欢迎转载，尊重版权。如果觉得本系列对你有所帮助，欢迎给我家布丁买点狗粮(支付宝扫码)~

![default](https://i.postimg.cc/y1QXgJ6f/image.png)

## Home & More | 延伸阅读

![技术视野](https://s2.ax1x.com/2019/12/03/QQJLvt.png)

您可以通过以下导航来在 Gitbook 中阅读笔者的系列文章，涵盖了技术资料归纳、编程语言与理论、Web 与大前端、服务端开发与基础架构、云计算与大数据、数据科学与人工智能、产品设计等多个领域：

- 知识体系：《[Awesome Lists | CS 资料集锦](https://ng-tech.icu/Awesome-Lists)》、《[Awesome CheatSheets | 速学速查手册](https://ng-tech.icu/Awesome-CheatSheets)》、《[Awesome Interviews | 求职面试必备](https://ng-tech.icu/Awesome-Interviews)》、《[Awesome RoadMaps | 程序员进阶指南](https://ng-tech.icu/Awesome-RoadMaps)》、《[Awesome MindMaps | 知识脉络思维脑图](https://ng-tech.icu/Awesome-MindMaps)》、《[Awesome-CS-Books | 开源书籍（.pdf）汇总](https://github.com/wx-chevalier/Awesome-CS-Books)》

- 编程语言：《[编程语言理论](https://ng-tech.icu/ProgrammingLanguage-Series/#/)》、《[Java 实战](https://ng-tech.icu/Java-Series)》、《[JavaScript 实战](https://ng-tech.icu/JavaScript-Series)》、《[Go 实战](https://ng-tech.icu/Go-Series)》、《[Python 实战](https://ng-tech.icu/ProgrammingLanguage-Series/#/)》、《[Rust 实战](https://ng-tech.icu/ProgrammingLanguage-Series/#/)》
- 软件工程、模式与架构：《[编程范式与设计模式](https://ng-tech.icu/SoftwareEngineering-Series/)》、《[数据结构与算法](https://ng-tech.icu/SoftwareEngineering-Series/)》、《[软件架构设计](https://ng-tech.icu/SoftwareEngineering-Series/)》、《[整洁与重构](https://ng-tech.icu/SoftwareEngineering-Series/)》、《[研发方式与工具](https://ng-tech.icu/SoftwareEngineering-Series/)》

* Web 与大前端：《[现代 Web 全栈开发与工程架构](https://ng-tech.icu/Web-Series/)》、《[数据可视化](https://ng-tech.icu/Frontend-Series/)》、《[iOS](https://ng-tech.icu/Frontend-Series/)》、《[Android](https://ng-tech.icu/Frontend-Series/)》、《[混合开发与跨端应用](https://ng-tech.icu/Web-Series/)》、《[Node.js 全栈开发](https://ng-tech.icu/Node-Series/)》

* 服务端开发实践与工程架构：《[服务端基础](https://ng-tech.icu/Backend-Series/#/)》、《[微服务与云原生](https://ng-tech.icu/MicroService-Series/#/)》、《[测试与高可用保障](https://ng-tech.icu/Backend-Series/#/)》、《[DevOps](https://ng-tech.icu/Backend-Series/#/)》、《[Spring](https://ng-tech.icu/Spring-Series/#/)》、《[信息安全与渗透测试](https://ng-tech.icu/Backend-Series/#/)》

* 分布式基础架构：《[分布式系统](https://ng-tech.icu/DistributedSystem-Series/#/)》、《[分布式计算](https://ng-tech.icu/DistributedSystem-Series/#/)》、《[数据库](https://github.com/wx-chevalier/Database-Series)》、《[网络](https://ng-tech.icu/DistributedSystem-Series/#/)》、《[虚拟化与云计算](https://github.com/wx-chevalier/Cloud-Series)》、《[Linux 与操作系统](https://github.com/wx-chevalier/Linux-Series)》

* 数据科学，人工智能与深度学习：《[数理统计](https://ng-tech.icu/AI-Series/#/)》、《[数据分析](https://ng-tech.icu/AI-Series/#/)》、《[机器学习](https://ng-tech.icu/AI-Series/#/)》、《[深度学习](https://ng-tech.icu/AI-Series/#/)》、《[自然语言处理](https://ng-tech.icu/AI-Series/#/)》、《[工具与工程化](https://ng-tech.icu/AI-Series/#/)》、《[行业应用](https://ng-tech.icu/AI-Series/#/)》

* 产品设计与用户体验：《[产品设计](https://ng-tech.icu/Product-Series/#/)》、《[交互体验](https://ng-tech.icu/Product-Series/#/)》、《[项目管理](https://ng-tech.icu/Product-Series/#/)》

* 行业应用：《[行业迷思](https://github.com/wx-chevalier/Business-Series)》、《[功能域](https://github.com/wx-chevalier/Business-Series)》、《[电子商务](https://github.com/wx-chevalier/Business-Series)》、《[智能制造](https://github.com/wx-chevalier/Business-Series)》

此外，你还可前往 [xCompass](https://ng-tech.icu/) 交互式地检索、查找需要的文章/链接/书籍/课程；或者也可以关注微信公众号：**某熊的技术之路**以获取最新资讯。

## 链接

- https://zhuanlan.zhihu.com/p/78104880
- https://netfilx.github.io/spring-boot/
