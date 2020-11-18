package wx.application.wechat;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wx.domain.wechat.WechatAccessToken;
import wx.domain.wechat.WechatAccessTokenRepository;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.infra.wechat.WechatAccessTokenDO;
import wx.infra.tunnel.db.infra.wechat.WechatAccessTokenTunnel;

@Slf4j
@Service
public class WechatAccessTokenRepositoryImpl implements WechatAccessTokenRepository {

  private WechatAccessTokenTunnel wechatAccessTokenTunnel;

  private WechatAccessTokenConverter wechatAccessTokenConverter;

  public WechatAccessTokenRepositoryImpl(
      WechatAccessTokenTunnel wechatAccessTokenTunnel,
      WechatAccessTokenConverter wechatAccessTokenConverter) {
    this.wechatAccessTokenTunnel = wechatAccessTokenTunnel;
    this.wechatAccessTokenConverter = wechatAccessTokenConverter;
  }

  @Override
  public WechatAccessToken getLatest() {
    Wrapper<WechatAccessTokenDO> updateWrapper =
        Helper.getUpdateWrapper(WechatAccessTokenDO.class)
            .isNull(WechatAccessTokenDO::getDeletedAt)
            .orderByDesc(WechatAccessTokenDO::getCreatedAt)
            .last("LIMIT 1");
    WechatAccessTokenDO tokenDO = wechatAccessTokenTunnel.getOne(updateWrapper);
    return wechatAccessTokenConverter.convertFrom(tokenDO);
  }

  @Override
  public void save(WechatAccessToken wechatAccessToken) {
    Wrapper<WechatAccessTokenDO> updateWrapper =
        Helper.getUpdateWrapper(WechatAccessTokenDO.class)
            .isNull(WechatAccessTokenDO::getDeletedAt)
            .set(WechatAccessTokenDO::getDeletedAt, LocalDateTime.now());
    wechatAccessTokenTunnel.update(updateWrapper);

    Optional.of(wechatAccessToken)
        .map(wechatAccessTokenConverter::convertTo)
        .ifPresent(wechatAccessTokenTunnel::save);
  }
}
