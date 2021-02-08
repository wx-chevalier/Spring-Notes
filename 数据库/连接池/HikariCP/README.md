# HikariCP

HikariCP 是一个高性能的 JDBC 连接池，基于 BoneCP 做了不少的改进和优化。JDBC 连接池的实现并不复杂，主要是对 JDBC 中几个核心对象 Connection、Statement、PreparedStatement、CallableStatement 以及 ResultSet 的封装与动态代理。目前主流的两个数据库连接池就是 Druid 与 HikariCP，HikariCP 在性能方面是要优于 Druid，但是 Druid 在监控以及可扩展性上是较为完善的。
