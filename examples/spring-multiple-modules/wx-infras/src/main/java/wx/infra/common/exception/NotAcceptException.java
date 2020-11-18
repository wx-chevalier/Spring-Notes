package wx.infra.common.exception;

import org.springframework.http.HttpStatus;
import wx.common.data.code.ApiErrorCode;

public class NotAcceptException extends BaseBizException {
  public NotAcceptException() {
    super(HttpStatus.NOT_ACCEPTABLE);
  }

  public NotAcceptException(String message) {
    super(message, HttpStatus.NOT_ACCEPTABLE);
  }

  public NotAcceptException(String message, Throwable cause) {
    super(message, cause, HttpStatus.NOT_ACCEPTABLE);
  }

  public NotAcceptException(String message, ApiErrorCode code) {
    super(message, HttpStatus.NOT_ACCEPTABLE);
    this.setCode(code);
  }
}
