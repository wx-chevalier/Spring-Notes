package wx.domain.shared;

import wx.domain.event.EventBus;

public interface DomainService {

  EventBus getEventBus();
}
