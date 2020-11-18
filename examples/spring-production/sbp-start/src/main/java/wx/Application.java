package wx;

import static com.google.common.base.Preconditions.checkState;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import java.io.File;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import wx.context.properties.ApplicationProperty;
import wx.domain.terminal.config.TerminalConfig;
import wx.utils.DockerUtil;

@EnableAsync
@MapperScan(basePackages = "wx.repository")
@SpringBootApplication(scanBasePackages = "wx")
@EnableScheduling
@Slf4j
public class Application implements CommandLineRunner {
  private static final String PID_FILE_ARG = "--pid-file=";
  private static final String DEFAULT_PID_FILE = "/opt/wsat/agent/wsat-agent.pid";
  private ApplicationProperty applicationProperty;

  public Application(ApplicationProperty applicationProperty) {
    this.applicationProperty = applicationProperty;
  }

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(Application.class);

    TerminalConfig terminalConfig = TerminalConfig.parseConfig();

    app.setDefaultProperties(
        Collections.singletonMap("server.port", terminalConfig.getAgent().getPort()));

    String pidFile = parsePidFile(args).orElse(DEFAULT_PID_FILE);
    checkState(!new File(pidFile).exists(), "another process is running");
    app.addListeners(new ApplicationPidFileWriter(pidFile));
    app.run(args);
  }

  private static Optional<String> parsePidFile(String[] args) {
    for (final String arg : args) {
      if (arg.startsWith(PID_FILE_ARG)) {
        return Optional.of(arg.substring(PID_FILE_ARG.length()));
      }
    }
    return Optional.empty();
  }

  @Bean
  BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  public void run(String... args) {
    log.info("JWT secret: {}", applicationProperty.getSecurity().getJwt().getSecret());
  }

  @Bean
  public TaskScheduler taskScheduler() {
    return new ConcurrentTaskScheduler();
  }

  @Bean
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(2);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("WSAT-AsyncWorker-");
    executor.initialize();
    return executor;
  }

  @Bean
  public DockerClient dockerClient() {
    return DockerClientBuilder.getInstance().build();
  }

  @Bean
  public DockerUtil dockerUtil() {
    return new DockerUtil(dockerClient());
  }
}
