package wx.api.rest.shared.dto.envelope;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.Data;

@Data
@ApiModel(value = "响应数据", description = "- err 和 data 字段只有一个存在\n- meta 字段可选")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseEnvelope<T> {

  @ApiModelProperty("响应状态")
  private ResponseStatus status;

  @ApiModelProperty("额外元数据")
  private ResponseMeta meta;

  @ApiModelProperty("错误信息")
  private ErrorMessage err;

  @ApiModelProperty("响应内容")
  private T data;

  public static <T> ResponseEnvelope<T> createOk(Single<T> single) {
    return createOk(single.blockingGet());
  }

  public static <T> ResponseEnvelope<T> createOk(Single<T> single, PaginationMeta paginationMeta) {
    return createOk(single.blockingGet(), paginationMeta);
  }

  public static ResponseEnvelope<Void> createOk(Completable completable) {
    completable.blockingAwait();
    return createOk();
  }

  public static ResponseEnvelope<Void> createOk() {
    return new ResponseEnvelope<Void>().setStatus(ResponseStatus.ok);
  }

  public static <T> ResponseEnvelope<List<T>> createOk(Flowable<T> flowable) {
    return createOk(flowable, true);
  }

  public static <T> ResponseEnvelope<List<T>> createOk(Flowable<T> flowable, boolean parallel) {
    return createOk(
        StreamSupport.stream(flowable.blockingIterable().spliterator(), parallel)
            .collect(Collectors.toList()));
  }

  public static <T> ResponseEnvelope<T> createOk(T data) {
    return new ResponseEnvelope<T>().setStatus(ResponseStatus.ok).setData(data);
  }

  public static <T> ResponseEnvelope<Void> createOkIgnoreResult(T data) {
    return new ResponseEnvelope<Void>().setStatus(ResponseStatus.ok);
  }

  public static <T> ResponseEnvelope<T> createOk(T data, PaginationMeta pagination) {
    return createOk(data).setMeta(new ResponseMeta().setPagination(pagination));
  }

  public static <T> ResponseEnvelope<T> createErr(ErrorMessage err) {
    return new ResponseEnvelope<T>().setStatus(ResponseStatus.error).setErr(err);
  }

  public static <T> ResponseEnvelope<T> createErr(T errInfo) {
    String reason = errInfo == null ? null : errInfo.toString();
    ErrorMessage message = new ErrorMessage("PARAM_ERROR", reason);
    return new ResponseEnvelope<T>().setStatus(ResponseStatus.error).setErr(message);
  }
}
