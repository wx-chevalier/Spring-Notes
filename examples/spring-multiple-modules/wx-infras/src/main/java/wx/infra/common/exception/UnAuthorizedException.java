package wx.infra.common.exception;

import org.springframework.http.HttpStatus;
import wx.common.data.code.ApiErrorCode;

/**
 * UnAuthorizedException.
 *
 * @author lotuc
 */
public class UnAuthorizedException extends BaseBizException {

  public UnAuthorizedException() {
    super(HttpStatus.UNAUTHORIZED);
  }

  public UnAuthorizedException(String message) {
    super(message, HttpStatus.UNAUTHORIZED);
  }

  public UnAuthorizedException(String message, Throwable cause) {
    super(message, cause, HttpStatus.UNAUTHORIZED);
  }

  public UnAuthorizedException(String message, ApiErrorCode code) {
    super(message, HttpStatus.UNAUTHORIZED);
    this.setCode(code);
  }
}
