package wx.domain.wechat;

import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;

public interface WechatQrCodeTicketRepository {

  WechatQrCodeTicket getBoundQrCode(TenantId tenantId, UserId userId);
}
