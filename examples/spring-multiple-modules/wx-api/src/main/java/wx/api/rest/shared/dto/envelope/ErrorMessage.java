package wx.api.rest.shared.dto.envelope;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.Nullable;
import wx.infra.common.util.ExceptionUtils;

@Data
@AllArgsConstructor
public class ErrorMessage {

  private String code;

  private String reason;

  @Nullable private String exception;

  public ErrorMessage(String code, String reason) {
    this.code = code;
    this.reason = reason;
    this.exception = null;
  }

  public ErrorMessage(String code, String reason, @Nullable Throwable t) {
    this.code = code;
    this.reason = reason;
    this.exception = ExceptionUtils.getStackTrace(t);
  }
}
