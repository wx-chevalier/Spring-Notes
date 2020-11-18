package wx.infra.service.email;

public class EmailException extends RuntimeException {

  public EmailException(String message) {
    super(message);
  }

  public EmailException(String message, Throwable cause) {
    super(message, cause);
  }

  public EmailException(Throwable cause) {
    super(cause);
  }

  public EmailException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
