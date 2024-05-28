# HikariCP

HikariCP 是一个高性能的 JDBC 连接池，基于 BoneCP 做了不少的改进和优化。它提供了很多特性，使得它成为 Java 应用程序中常用的连接池之一。以下是 HikariCP 的一些主要特性和优点：

- 快速启动时间：HikariCP 具有非常快速的启动时间，这意味着当应用程序启动时，它可以迅速准备好数据库连接池，而不会造成长时间的延迟。
- 高性能：HikariCP 以高效的方式管理连接池，减少了与数据库交互时的延迟。它的设计目标是最小化对数据库的负载，同时最大化性能。
- 轻量级：HikariCP 是一个轻量级的连接池，jar 文件大小很小，且没有依赖其他库，这使得它在应用程序中的集成变得非常简单。
- 自动化管理：HikariCP 可以自动地管理连接的生命周期，包括创建、释放、回收等操作，开发者无需手动干预连接的管理。
- 高度可配置：HikariCP 提供了丰富的配置选项，允许开发者根据应用程序的需求进行调整和优化。这些配置选项包括连接池大小、最大连接数、最小空闲连接数、连接超时等。
- 监控和诊断：HikariCP 内置了监控和诊断功能，可以实时地监视连接池的状态，包括活动连接数、空闲连接数、连接等待时间等指标。
- 兼容性：HikariCP 与各种数据库和 JDBC 驱动程序兼容性良好，可以与 MySQL、PostgreSQL、Oracle 等主流数据库无缝集成。
- 开源：HikariCP 是一个开源项目，代码托管在 GitHub 上，可以自由获取、使用和修改。

# 快速开始

以下是一个简单的 Java 应用程序示例，演示如何在 Java 项目中使用 HikariCP 连接到数据库：首先，确保将 HikariCP 的 jar 文件（例如 hikaricp-xxx.jar）添加到你的项目中的 classpath 中。接下来，创建一个 HikariCP 连接池并使用它来获取数据库连接。

```java
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HikariCPExample {

    public static void main(String[] args) {
        // 创建Hikari配置对象
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/mydatabase");
        config.setUsername("username");
        config.setPassword("password");

        // 设置连接池属性
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000); // 30秒超时

        // 创建Hikari数据源
        HikariDataSource dataSource = new HikariDataSource(config);

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // 从连接池获取连接
            connection = dataSource.getConnection();

            // 执行SQL查询
            String sql = "SELECT * FROM my_table";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            // 处理查询结果
            while (resultSet.next()) {
                // 处理结果集
                String column1 = resultSet.getString("column1");
                int column2 = resultSet.getInt("column2");
                // 输出结果
                System.out.println("Column1: " + column1 + ", Column2: " + column2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // 关闭数据源
        dataSource.close();
    }
}
```

在这个示例中，我们首先创建了一个 HikariConfig 对象，设置了数据库的连接 URL、用户名和密码等信息，然后配置了连接池的一些属性，比如最大连接数、最小空闲连接数、连接超时等。

然后，我们通过创建 HikariDataSource 来实例化数据源，该数据源可以用于从连接池中获取数据库连接。接着，我们通过调用 getConnection()方法从连接池中获取一个连接，然后执行 SQL 查询，并处理查询结果。最后，我们在 finally 块中关闭了所有资源，包括连接、预处理语句和结果集，并调用数据源的 close() 方法关闭数据源。

这是一个简单的 HikariCP 入门示例，演示了如何在 Java 应用程序中使用 HikariCP 连接到数据库并执行 SQL 查询。
