package wx.api.rest.common.message.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import wx.common.data.infra.notice.NoticeTypeKind;
import wx.infra.common.exception.NotAcceptException;

@Data
@ApiModel("消息查询命令")
public class MessageNoticeQueryReq {

  @ApiModelProperty(value = "是否已读(不提供查询全部)", example = "false")
  private Boolean hasRead;

  @ApiModelProperty("应用类型")
  private String appId;

  @ApiModelProperty("搜索关键字")
  private String searchKey;

  @ApiModelProperty("开始时间")
  private LocalDate startDate;

  @ApiModelProperty("结束时间")
  private LocalDate endDate;

  @ApiModelProperty(value = "消息类型的种类(警告、任务等)")
  private NoticeTypeKind kind;

  @ApiModelProperty(value = "页码", example = "1")
  private Integer pageNum;

  @ApiModelProperty(value = "分页大小", example = "10")
  private Integer pageSize;

  public Pageable toPage() {
    if (Objects.isNull(this.getPageNum()) || Objects.isNull(this.getPageSize())) {
      throw new NotAcceptException("操作失败, 缺少分页参数的信息");
    }
    return PageRequest.of(this.pageNum, this.pageSize);
  }
}
