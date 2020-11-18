package wx.domain.admin;

import wx.common.data.shared.id.ApplicationId;
import wx.domain.shared.IdBasedEntityRepository;

public interface ApplicationRepository
    extends IdBasedEntityRepository<ApplicationId, Application> {}
