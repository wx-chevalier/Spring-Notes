package wx.common.data.page;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import wx.common.data.shared.HasId;
import wx.common.data.shared.id.EntityId;

public class TimePageData<T extends HasId<EntityId>> {

  @Getter private final List<T> data;
  @Getter private final TimePageLink nextPageLink;
  private final boolean hasNext;

  public TimePageData(List<T> data, TimePageLink pageLink) {
    super();
    this.data = data;
    int limit = pageLink.getLimit();
    if (data != null && data.size() == limit) {
      int index = data.size() - 1;
      Long idOffset = data.get(index).getId().getId();
      nextPageLink =
          new TimePageLink(
              limit,
              pageLink.getStartTime(),
              pageLink.getEndTime(),
              pageLink.isAscOrder(),
              idOffset);
      hasNext = true;
    } else {
      nextPageLink = null;
      hasNext = false;
    }
  }

  @JsonCreator
  public TimePageData(
      @JsonProperty("data") List<T> data,
      @JsonProperty("nextPageLink") TimePageLink nextPageLink,
      @JsonProperty("hasNext") boolean hasNext) {
    this.data = data;
    this.nextPageLink = nextPageLink;
    this.hasNext = hasNext;
  }

  @JsonProperty("hasNext")
  public boolean hasNext() {
    return hasNext;
  }
}
