package wx.application.daemon;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wx.infra.tunnel.db.account.TenantTunnel;

/** 租户工单统计定时任务 */
@Slf4j
@Component
public class TenantScheduledTask {

  private final TenantTunnel tenantTunnel;

  private final AmqpTemplate amqpTemplate;

  public TenantScheduledTask(TenantTunnel tenantTunnel, AmqpTemplate amqpTemplate) {
    this.tenantTunnel = tenantTunnel;
    this.amqpTemplate = amqpTemplate;
  }

  /**
   * 租户工单信息统计接口
   *
   * <p>每隔一小时执行一次的
   */
  @Scheduled(cron = "0 0 0/1 * * ?")
  public void workOrderDailyStatisticsTask() {
    log.info("Sending work order daily statistics task");
  }
}
