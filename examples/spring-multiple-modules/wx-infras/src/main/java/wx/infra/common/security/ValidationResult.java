package wx.infra.common.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import wx.infra.common.exception.ForbiddenException;
import wx.infra.common.exception.InternalServerException;
import wx.infra.common.exception.NotFoundException;
import wx.infra.common.exception.UnAuthorizedException;

/**
 * ValidationResult.
 *
 * @author lotuc
 */
@Data
@AllArgsConstructor
public class ValidationResult<V> {
  private final ValidationResultCode resultCode;
  private final String message;
  private final V value;

  public static <V> ValidationResult<V> ok(V v) {
    return new ValidationResult<>(ValidationResultCode.OK, "Ok", v);
  }

  public static <V> ValidationResult<V> accessDenied(String message) {
    return new ValidationResult<>(ValidationResultCode.ACCESS_DENIED, message, null);
  }

  public static <V> ValidationResult<V> entityNotFound(String message) {
    return new ValidationResult<>(ValidationResultCode.ENTITY_NOT_FOUND, message, null);
  }

  public static <V> ValidationResult<V> unauthorized(String message) {
    return new ValidationResult<>(ValidationResultCode.UNAUTHORIZED, message, null);
  }

  public static <V> ValidationResult<V> internalError(String message) {
    return new ValidationResult<>(ValidationResultCode.INTERNAL_ERROR, message, null);
  }

  public void throwException() {
    orElseThrow();
  }

  /**
   * 如果 resultCode 不为 {@link ValidationResultCode#OK}，抛出对应异常；否则返回 value.
   *
   * @return value
   */
  public V orElseThrow() {
    switch (resultCode) {
      case UNAUTHORIZED:
        throw new UnAuthorizedException(message);
      case ACCESS_DENIED:
        throw new ForbiddenException(message);
      case INTERNAL_ERROR:
        throw new InternalServerException(message);
      case ENTITY_NOT_FOUND:
        throw new NotFoundException(message);
    }
    return value;
  }
}
