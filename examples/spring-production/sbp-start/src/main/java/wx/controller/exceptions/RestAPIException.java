package wx.controller.exceptions;

import org.springframework.http.HttpStatus;

public class RestAPIException extends RuntimeException {
  HttpStatus status;

  public RestAPIException(HttpStatus status) {
    this.status = status;
  }

  public RestAPIException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }

  public RestAPIException(String message, Throwable cause, HttpStatus status) {
    super(message, cause);
    this.status = status;
  }

  public RestAPIException(Throwable cause, HttpStatus status) {
    super(cause);
    this.status = status;
  }

  public RestAPIException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      HttpStatus status) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.status = status;
  }
}
