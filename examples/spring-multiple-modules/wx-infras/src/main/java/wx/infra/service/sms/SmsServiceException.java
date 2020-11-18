package wx.infra.service.sms;

public class SmsServiceException extends RuntimeException {

  public SmsServiceException() {}

  public SmsServiceException(String message) {
    super(message);
  }

  public SmsServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public SmsServiceException(Throwable cause) {
    super(cause);
  }

  public SmsServiceException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
