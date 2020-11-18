package wx.infra.common.exception;

import org.springframework.http.HttpStatus;
import wx.common.data.code.ApiErrorCode;

/**
 * InternalServerException.
 *
 * @author lotuc
 */
public class InternalServerException extends BaseBizException {

  public InternalServerException() {
    super(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public InternalServerException(String message) {
    super(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public InternalServerException(String message, Throwable cause) {
    super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public InternalServerException(String message, ApiErrorCode code) {
    super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    this.setCode(code);
  }
}
