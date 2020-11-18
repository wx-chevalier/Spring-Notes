package wx.infra.service.dingtalk;

public class DingTalkServiceException extends RuntimeException {

  public DingTalkServiceException() {}

  public DingTalkServiceException(String message) {
    super(message);
  }

  public DingTalkServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public DingTalkServiceException(Throwable cause) {
    super(cause);
  }

  public DingTalkServiceException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
