package wx.controller.exceptions;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends RestAPIException {
  public EntityNotFoundException() {
    super(HttpStatus.NOT_FOUND);
  }

  public EntityNotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }

  public EntityNotFoundException(String message, Throwable cause) {
    super(message, cause, HttpStatus.NOT_FOUND);
  }

  public EntityNotFoundException(Throwable cause) {
    super(cause, HttpStatus.NOT_FOUND);
  }

  public EntityNotFoundException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace, HttpStatus.NOT_FOUND);
  }
}
