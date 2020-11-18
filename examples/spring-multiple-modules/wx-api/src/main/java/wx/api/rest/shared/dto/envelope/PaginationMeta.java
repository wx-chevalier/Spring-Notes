package wx.api.rest.shared.dto.envelope;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel("分页信息")
@NoArgsConstructor
@AllArgsConstructor
public class PaginationMeta {
  @ApiModelProperty("当前页")
  private Integer pageNum;

  @ApiModelProperty("元素总数")
  private Long totalElements;

  @ApiModelProperty("总页数")
  private Integer totalPages;
}
