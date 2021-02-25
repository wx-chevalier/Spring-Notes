# Sofa Boot

SOFABoot 是蚂蚁金服开源的基于 Spring Boot 的研发框架，它在 Spring Boot 的基础上，提供了诸如 Readiness Check，类隔离，日志空间隔离等能力。在增强了 Spring Boot 的同时，SOFABoot 提供了让用户可以在 Spring Boot 中非常方便地使用 SOFA 中间件的能力。SOFABoot 在 Spring Boot 基础上，提供了以下能力：

- 扩展 Spring Boot 健康检查的能力：在 Spring Boot 健康检查能力基础上，提供了 Readiness Check 的能力，保证应用实例安全上线。
- 提供模块化开发的能力：基于 Spring 上下文隔离提供模块化开发能力，每个 SOFABoot 模块使用独立的 Spring 上下文，避免不同 SOFABoot 模块间的 BeanId 冲 突。
- 增加模块并行加载和 Spring Bean 异步初始化能力，加速应用启动；
- 增加日志空间隔离的能力：中间件框架自动发现应用的日志实现依赖并独立打印日志，避免中间件和应用日志实现绑定，通过 sofa-common-tools 实现。
- 增加类隔离的能力：基于 SOFAArk 框架提供类隔离能力，方便使用者解决各种类冲突问题。
- 增加中间件集成管理的能力：统一管控、提供中间件统一易用的编程接口、每一个 SOFA 中间件都是独立可插拔的组件。
- 提供完全兼容 Spring Boot 的能力：SOFABoot 基于 Spring Boot 的基础上进行构建，并且完全兼容 Spring Boot。
