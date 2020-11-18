package wx.dto.docker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

@ApiModel(description = "docker ps 参数; *Filter 表示不为空时，使用给定值进行过滤")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class DockerPSArgument {

  @ApiModelProperty(example = "-1")
  private Integer limit;

  @ApiModelProperty(example = "false")
  private Boolean showSize;

  @ApiModelProperty(example = "true")
  private Boolean showAll;

  @ApiModelProperty private String sinceId;

  @ApiModelProperty private String beforeId;

  @ApiModelProperty private List<String> ancestorFilters;

  @ApiModelProperty(notes = "不为空，按指定退出码过滤出要返回的容器", allowEmptyValue = true)
  private Integer exitCodeFilter;

  @ApiModelProperty private Map<String, String> labelFilter;

  @ApiModelProperty private List<String> containerIdsFilter;

  @ApiModelProperty private List<String> containerNamesFilter;

  @ApiModelProperty private List<String> statusesFilter;

  @ApiModelProperty private List<String> volumesFilter;

  @ApiModelProperty private List<String> networksFilter;

  @JsonCreator
  public DockerPSArgument(
      @JsonProperty(defaultValue = "-1") @Nullable Integer limit,
      @JsonProperty(defaultValue = "false") @Nullable Boolean showSize,
      @JsonProperty(defaultValue = "false") @Nullable Boolean showAll,
      @JsonProperty(defaultValue = "null") @Nullable String sinceId,
      @JsonProperty(defaultValue = "null") @Nullable String beforeId,
      @JsonProperty(defaultValue = "null") @Nullable List<String> ancestorFilters,
      @JsonProperty(defaultValue = "null") @Nullable Integer exitCodeFilter,
      @JsonProperty(defaultValue = "null") @Nullable Map<String, String> labelFilter,
      @JsonProperty(defaultValue = "null") @Nullable List<String> containerIdsFilter,
      @JsonProperty(defaultValue = "null") @Nullable List<String> containerNamesFilter,
      @JsonProperty(defaultValue = "null") @Nullable List<String> statusesFilter,
      @JsonProperty(defaultValue = "null") @Nullable List<String> volumesFilter,
      @JsonProperty(defaultValue = "null") @Nullable List<String> networksFilter) {
    this.limit = limit;
    this.showSize = showSize;
    this.showAll = showAll;
    this.sinceId = sinceId;
    this.beforeId = beforeId;
    this.ancestorFilters = ancestorFilters;
    this.exitCodeFilter = exitCodeFilter;
    this.labelFilter = labelFilter;
    this.containerIdsFilter = containerIdsFilter;
    this.containerNamesFilter = containerNamesFilter;
    this.statusesFilter = statusesFilter;
    this.volumesFilter = volumesFilter;
    this.networksFilter = networksFilter;
  }
}
