package wx.api.config;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wx.api.rest.shared.ErrorCode;
import wx.api.rest.shared.dto.envelope.ErrorMessage;
import wx.api.rest.shared.dto.envelope.ResponseEnvelope;
import wx.infra.common.exception.BaseBizException;
import wx.infra.common.exception.DataValidationException;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResponseEnvelope> handleException(Exception e) {
    log.error("Uncaught Exception", e);
    return new ResponseEntity<>(
        ResponseEnvelope.createErr(
            new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage(), e)),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(DataValidationException.class)
  public ResponseEntity<ResponseEnvelope> handleDataValidationException(DataValidationException e) {
    log.error("DataValidationException", e);
    return new ResponseEntity<>(
        ResponseEnvelope.createErr(new ErrorMessage(ErrorCode.INVALID_DATA.name(), e.getMessage())),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ResponseEnvelope> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    return new ResponseEntity<>(
        ResponseEnvelope.createErr(new ErrorMessage(ErrorCode.INVALID_DATA.name(), e.getMessage())),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(BaseBizException.class)
  public ResponseEntity<ResponseEnvelope> handleBaseBizException(BaseBizException e) {
    String errorCode;
    if (e.getCode() != null) {
      errorCode = e.getCode().name();
    } else {
      errorCode = e.getStatus().getReasonPhrase();
    }

    return new ResponseEntity<>(
        ResponseEnvelope.createErr(new ErrorMessage(errorCode, e.getMessage(), e)), e.getStatus());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ResponseEnvelope> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    FieldError fieldError =
        Optional.ofNullable(e.getBindingResult().getFieldError())
            .orElse(new FieldError("未知对象", "未知字段", "未知消息"));
    String fieldErr = fieldError.getDefaultMessage();
    log.warn("发生参数校验不通过异常: [{} ===> {}]", fieldError.getField(), fieldErr);
    return new ResponseEntity<>(ResponseEnvelope.createErr(fieldErr), HttpStatus.BAD_REQUEST);
  }
}
