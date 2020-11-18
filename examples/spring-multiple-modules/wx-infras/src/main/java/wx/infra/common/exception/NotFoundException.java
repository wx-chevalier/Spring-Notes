package wx.infra.common.exception;

import org.springframework.http.HttpStatus;
import wx.common.data.code.ApiErrorCode;

public class NotFoundException extends BaseBizException {
  public NotFoundException() {
    super(HttpStatus.NOT_FOUND);
  }

  public NotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }

  public NotFoundException(String message, Throwable cause) {
    super(message, cause, HttpStatus.NOT_FOUND);
  }

  public NotFoundException(String message, ApiErrorCode code) {
    super(message, HttpStatus.NOT_FOUND);
    this.setCode(code);
  }
}
