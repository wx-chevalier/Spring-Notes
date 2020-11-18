package wx.common.data.page;

import lombok.Getter;
import lombok.Setter;

public abstract class IdBasedPageLink {

  @Getter private final int limit;

  @Getter @Setter private Long idOffset;

  protected IdBasedPageLink(int limit, Long idOffset) {
    this.limit = limit;
    this.idOffset = idOffset;
  }
}
