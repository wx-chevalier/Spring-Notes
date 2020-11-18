package wx.common.data.page;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class PageNumBasedPageLink {

  /** zero based page number */
  @Getter private final int pageNum;

  @Getter private final int pageSize;

  @JsonCreator
  public PageNumBasedPageLink(
      @JsonProperty("pageNum") Integer pageNum, @JsonProperty("pageSize") Integer pageSize) {
    this.pageNum = pageNum == null ? 0 : pageNum;
    this.pageSize = pageSize == null ? 10 : pageSize;
  }

  public PageNumBasedPageLink() {
    this(null, null);
  }

  public PageNumBasedPageLink nextPage() {
    return new PageNumBasedPageLink(getPageNum() + 1, getPageSize());
  }
}
