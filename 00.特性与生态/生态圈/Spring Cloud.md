# Spring Cloud

我们将 Spring Cloud 与 K8s 中的概念进行协调对比：

![Spring Cloud 与 K8s 对比](https://assets.ng-tech.icu/item/20230417205616.png)

Spring Cloud 为我们提供了分布式/版本化配置、服务注册和发现、路由、服务和服务之间的调用、负载均衡、断路器、分布式消息传递等特性。其核心子项目包含了如下模块：

- Spring Cloud Config: 配置中心，利用 Git 来集中管理程序的配置。

- Spring Cloud Netflix: 集成众多 Netflix 的开源软件，包括 Eureka、Hystrix、Zuul、Archaius 等。

- Spring Cloud Bus: Bus 即消息总线，消息总线利用分布式消息将服务和服务实例连接在一起，用于在一个集群中传播状态的变化，比如配置更改事件，其可以与 Spring Cloud Config 联合来实现热部署。

- Spring Cloud Cluster: 基于 Zookeeper、Redis、Hazelcast、Consul 实现的领导选举和平民状态模式的抽象和实现。

- Spring Cloud Consul: 基于 Hashicorp Consul 实现的服务发现和配置管理。

- Spring Cloud Security: 在 Zuul 代理中 OAuth2 REST 客户端和认证头转发提供负载均衡。

- Spring Cloud Sleuth: 适用于 Spring Cloud 应用程序的分布式跟踪，与 Zipkin HTrace 和基于日志（例如 ELK）的跟踪相兼容，可以实现日志的收集。

- Spring Cloud Data Flow: 一种针对现代运行时可组合的微服务应用程序的云本地编排服务，易于使用的 DSL、拖拽式 GUI 和 REST API 一起简化了基于微服务的数据管道的整体编排。

- Spring Cloud Stream: 一个轻量级的事件驱动的微服务框架，来快速构建可以连接到外部系统的应用程序。使用 Apache Kafka 或者 RabbitMQ 在 Spring Boot 应用程序之间发生和接受消息的简单声明模型。

- Spring Cloud Stream App Starters: 基于 Spring Boot 为外部系统提供 Spring 的集成。

- Spring Cloud Task App Starters: Spring Cloud Task App Starters 是 Spring Boot 应用程序，可以是任意的进程，包括 Spring Batch 作业，并可以在数据处理有限时间内终止。

- Spring Cloud Connectors: 便于 PaaS 应用在各平台上连接到后端数据库或者消息服务。

- Spring Cloud Starters: 基于 Spring Boot 的项目，用以简化 Spring Cloud 的依赖管理。

- Spring Cloud CLI: Spring Boot CLI 插件用于在 Groovy 中快速创建 Spring Cloud 组件应用程序。

- Spring Cloud Contract: Spring Cloud Contract 是一个总体项目，其中包含帮助用户成功实施消费者驱动契约（Consumer Driven Contracts）的解决方案。

- Spring Cloud for Cloud Foundry: Cloud Foundry 是 VMware 推出的开源 PaaS 云平台，利用 Pivotal Cloud Foundry 集成你的应用程序。

- Spring Cloud Cloud Foundry Service Broker: 为建立管理云托管服务的服务代理提供一个起点。
