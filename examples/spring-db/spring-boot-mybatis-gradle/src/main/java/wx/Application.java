package wx;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import wx.infra.db.DateTimeUtils;
import wx.infra.db.vart.VarTable;
import wx.infra.db.vart.VarTableTunnel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackageClasses = {Application.class})
public class Application implements CommandLineRunner {

  private VarTableTunnel varTableTunnel;

  public Application(VarTableTunnel varTableTunnel) {
    this.varTableTunnel = varTableTunnel;
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("===================== RUN =====================");

    long nowTs = System.currentTimeMillis();
    LocalDate nowDate = LocalDate.now();
    LocalDateTime nowDateTime = LocalDateTime.now();

    //        varTableTunnel.remove(new LambdaQueryWrapper<>());
    //    varTableTunnel.save(
    //        new VarTable()
    //            //            .setName("JDBC=Shanghai - " + ZonedDateTime.now())
    //            .setName("JDBC=UTC      - " + ZonedDateTime.now())
    //            .setTs(nowTs)
    //            .setDateVal(nowDate)
    //            .setDatetimeVal(nowDateTime)
    //            .setTimestampVal(nowDateTime));

    for (VarTable varTable : varTableTunnel.list()) {
      System.out.println(varTable);
    }

    System.out.println("===================== RAN =====================");

    System.out.println(DateTimeUtils.fromTimestamp(1574757997370L));
  }
}
