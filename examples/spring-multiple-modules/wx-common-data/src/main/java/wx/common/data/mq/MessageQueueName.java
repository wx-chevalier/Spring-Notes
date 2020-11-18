package wx.common.data.mq;

/** MessageQueue Name */
public interface MessageQueueName {

  // 租户定时任务
  String TENANT_SCHEDULED_TASK = "TENANT_SCHEDULED_TASK";

  // utk printer 远程数据
  String UTK_PRINTER_TELEMETRY = "UTK_PRINTER_TELEMETRY";

  // 工单事件
  String WORK_ORDER_EVENT = "WORK_ORDER_EVENT";

  // 获取OSS的文件信息
  String GET_OSS_FILE_INFO_MESSAGE = "GET_OSS_FILE_INFO_MESSAGE";

  // 消息发送队列
  String SEND_MESSAGE_QUEUE = "SEND_MESSAGE";

  // 异步钉钉消息
  String DING_TALK_SEND_MESSAGE = "DING_TALK_SEND_MESSAGE";

  // 发送消息到租户下的所有用户
  String SEND_MESSAGE = "SMS_SEND_MESSAGE";

  // 发送消息到单个用户
  String SEND_USER_MESSAGE = "SEND_USER_MESSAGE";
}
