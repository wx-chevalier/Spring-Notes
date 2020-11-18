package wx.infra.service.aliyun;

public class AliOssServiceException extends RuntimeException {

  public AliOssServiceException() {}

  public AliOssServiceException(String message) {
    super(message);
  }

  public AliOssServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public AliOssServiceException(Throwable cause) {
    super(cause);
  }

  public AliOssServiceException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
