package wx.application.wechat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.domain.wechat.WechatAccessToken;
import wx.domain.wechat.WechatAccessTokenRepository;
import wx.domain.wechat.WechatQrCodeTicket;
import wx.domain.wechat.WechatQrCodeTicketRepository;
import wx.infra.common.data.wechat.req.WechatActionInfo;
import wx.infra.common.data.wechat.req.WechatQrCodeReq;
import wx.infra.common.data.wechat.req.WechatScene;
import wx.infra.tunnel.wechat.WechatTunnel;

@Slf4j
@Service
public class WechatQrCodeTicketRepositoryImpl implements WechatQrCodeTicketRepository {

  private WechatAccessTokenRepository wechatAccessTokenRepository;

  public WechatQrCodeTicketRepositoryImpl(WechatAccessTokenRepository wechatAccessTokenRepository) {
    this.wechatAccessTokenRepository = wechatAccessTokenRepository;
  }

  @Override
  public WechatQrCodeTicket getBoundQrCode(TenantId tenantId, UserId userId) {
    WechatAccessToken latestToken = wechatAccessTokenRepository.getLatest();
    WechatQrCodeReq req =
        new WechatQrCodeReq(new WechatActionInfo(WechatScene.bound(tenantId, userId)));
    return WechatTunnel.getQrCodeTicket(latestToken.getAccessToken(), req);
  }
}
