package wx.api.rest.shared.dto.envelope;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("响应元信息")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseMeta {

  @ApiModelProperty("分页信息")
  private PaginationMeta pagination;
}
