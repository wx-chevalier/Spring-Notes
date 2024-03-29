# 第 7 章 调用 REST 服务

本章内容：

- 使用 RestTemplate 调用 REST API
- 使用 Traverson 引导超媒体 API

你是否曾经去看电影，当电影开始的时候，你发现只有你一个人在电影院？从本质上说，这是一次私人观影的美妙经历。你可以选择任何你想要的座位，和屏幕上的人物交谈，甚至可以打开你的手机发推特谈论它，而不会有人因为破坏了他们的观影体验而生气。最棒的是，也没有其他人会为你毁了这部电影！

这种情况在我身上并不常见。但当它出现的时候，我在想如果我没有出现会发生什么。他们还会放映这部电影吗？英雄还会拯救世界吗？电影结束后，工作人员还会打扫影院吗？

没有观众的电影就像没有客户端的 API。它已经准备好接受和提供数据了，但是如果 API 从未被调用过，那么它真的是一个 API 吗？就像薛定谔的猫一样，在我们向它发出请求之前，我们无法知道 API 是活动的还是返回 HTTP 404 响应。

在前一章中，重点介绍了如何定义 REST 端点，以供应用程序外部的一些客户端使用。尽管开发这样一个 API 的驱动力是一个用作 Taco Cloud 网站的单页面 Angular 应用程序，但事实是客户端可以是任何语言的任何应用程序 —— 甚至是另一个 Java 应用程序。

Spring 应用程序既提供 API，又向另一个应用程序的 API 发出请求，这种情况并不少见。事实上，在微服务的世界里，这正变得越来越普遍。因此，花点时间看看如何使用 Spring 与 REST API 交互是值得的。

Spring 应用程序可以通过以下方式使用 REST API：

- RestTemplate —— 一个由 Spring 核心框架提供的简单、同步 REST 客户端。
- _Traverson_ —— 可感知超链接的同步 REST 客户端，由 Spring HATEOAS 提供，灵感来自同名的 JavaScript 库。
- WebClient —— 一个在 Spring 5 中引入的响应式、异步 REST 客户端。

我将推迟到第 11 章讨论 Spring 的响应式 web 框架的时候再讨论 WebClient。现在，我们将主要关注另外两个 REST 客户端，首先是 RestTemplate。
