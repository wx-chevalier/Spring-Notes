package wx.api.rest.shared.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Positive;

@ApiModel("分页参数")
public class PageParameter {
  @Positive(message = "页数必须为正整数")
  @ApiModelProperty(value = "页数", example = "10")
  private Integer pageSize;

  @Positive(message = "页码必须为正整数")
  @ApiModelProperty(value = "页码", example = "1")
  private Integer page;
}
