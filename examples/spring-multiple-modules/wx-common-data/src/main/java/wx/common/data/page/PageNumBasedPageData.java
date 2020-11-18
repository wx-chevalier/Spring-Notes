package wx.common.data.page;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public class PageNumBasedPageData<T> {

  @Getter private final List<T> data;
  @Getter private final PageNumBasedPageLink pageLink;
  @Getter private final Integer totalPage;
  @Getter private final Integer totalElements;

  @JsonCreator
  public PageNumBasedPageData(
      @JsonProperty("data") List<T> data,
      PageNumBasedPageLink pageLink,
      @JsonProperty("totalPage") Integer totalPage,
      @JsonProperty("totalElements") Integer totalElements) {
    this.data = data;
    this.pageLink = pageLink;
    this.totalPage = totalPage;
    this.totalElements = totalElements;
  }

  @JsonProperty("nextPageLink")
  @Nullable
  public PageNumBasedPageLink getNextPageLink() {
    if (pageLink.getPageNum() < totalPage) {
      return pageLink.nextPage();
    } else {
      return null;
    }
  }

  public <U> PageNumBasedPageData<U> map(Function<T, U> mapper) {
    return new PageNumBasedPageData<>(
        data.stream().map(mapper).collect(Collectors.toList()), pageLink, totalPage, totalElements);
  }
}
