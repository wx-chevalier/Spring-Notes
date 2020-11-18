package wx.api.rest.common.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import wx.common.data.infra.TagType;

@Data
@ApiModel("新增标签")
public class TagAddRequest {
  @NotBlank
  @ApiModelProperty("标签名称")
  @Size(min = 1, max = 18, message = "标签名称长度必须在1-18之间")
  private String tag;

  @NotNull
  @ApiModelProperty("标签名称")
  private TagType type;
}
