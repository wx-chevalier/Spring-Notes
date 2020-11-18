package wx.application.wechat;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.util.internal.StringUtil;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.common.data.wechat.WechatMessage;
import wx.common.data.wechat.WechatResponseTextMessage;
import wx.domain.wechat.WechatAccessToken;
import wx.domain.wechat.WechatAccessTokenRepository;
import wx.domain.wechat.WechatUser;
import wx.domain.wechat.WechatUserInfoRepository;
import wx.infra.common.data.wechat.req.WechatSceneEnum;
import wx.infra.common.util.DateTimeUtils;
import wx.infra.service.wechat.WechatSetting;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.infra.wechat.WechatUserDO;
import wx.infra.tunnel.db.infra.wechat.WechatUserTunnel;
import wx.infra.tunnel.wechat.WechatTunnel;

@Slf4j
@Service
public class WechatCommandServiceImpl implements WechatCommandService {

  // 扫描二维码前缀
  private static final String QR_CODE_CONTENT_PREFIX = "qrscene_";

  private WechatSetting wechatSetting;

  private WechatUserInfoRepository wechatUserInfoRepository;

  private WechatUserTunnel wechatUserTunnel;

  private WechatAccessTokenRepository wechatAccessTokenRepository;

  public WechatCommandServiceImpl(
      WechatSetting wechatSetting,
      WechatUserTunnel wechatUserTunnel,
      WechatUserInfoRepository wechatUserInfoRepository,
      WechatAccessTokenRepository wechatAccessTokenRepository) {
    this.wechatSetting = wechatSetting;
    this.wechatUserTunnel = wechatUserTunnel;
    this.wechatUserInfoRepository = wechatUserInfoRepository;
    this.wechatAccessTokenRepository = wechatAccessTokenRepository;
  }

  @Override
  public void updateAccessToken() {
    WechatAccessToken wechatAccessToken =
        WechatTunnel.getAccessToken(wechatSetting.getAppID(), wechatSetting.getAppSecret());
    wechatAccessTokenRepository.save(wechatAccessToken);
  }

  @Override
  public WechatResponseTextMessage handleMessage(WechatMessage message) {
    log.info("接收到微信消息:{}", message);
    WechatResponseTextMessage responseMessage = null;
    switch (message.getMsgType()) {
      case text:
        responseMessage = handleTextMessage(message, "接收到:" + message.getContent());
        break;
      case event:
        responseMessage = handleEventMessage(message);
      default:
        log.info("暂不支持的消息类型");
    }

    return responseMessage;
  }

  /** 处理微信的事件消息 */
  private WechatResponseTextMessage handleEventMessage(WechatMessage message) {
    log.info("接收到事件类型:{}", message.getEvent());
    switch (message.getEvent()) {
      case SCAN:
        handScanQrCodeEvent(message);
        break;
      case subscribe:
        handleSubscribeEvent(message);
        break;
      case unsubscribe:
        handleUnsubscribeEvent(message);
        break;
      default:
        log.info("暂不支持处理的事件消息:{}", message);
    }
    return null;
  }

  /** 处理微信的响应消息 */
  private WechatResponseTextMessage handleTextMessage(WechatMessage message, String content) {
    return new WechatResponseTextMessage()
        .setContent(content)
        .setMsgType("text")
        .setCreateTime(String.valueOf(new Date().getTime()))
        .setFromUserName(message.getToUserName())
        .setToUserName(message.getFromUserName());
  }

  // region 处理微信事件代码块

  /** 处理解绑消息 */
  private void handleUnsubscribeEvent(WechatMessage message) {
    String openId = message.getFromUserName();
    Optional<WechatUser> wechatUserOptional = wechatUserInfoRepository.getByOpenId(openId);
    if (!wechatUserOptional.isPresent()) {
      log.info("解绑失败, 用户信息不存在");
      return;
    }
    WechatUser wechatUser = wechatUserOptional.get();
    wechatUser.setUnsubscribeTime(DateTimeUtils.toTimestamp(LocalDateTime.now()));
    wechatUserInfoRepository.save(wechatUser);
    Wrapper<WechatUserDO> updateWrapper =
        Helper.getUpdateWrapper(WechatUserDO.class)
            .eq(WechatUserDO::getOpenId, openId)
            .isNull(WechatUserDO::getUnsubscribeTime)
            .set(WechatUserDO::getUnsubscribeTime, LocalDateTime.now());
    wechatUserTunnel.update(updateWrapper);

    log.info("解绑微信用户成功");
  }

  /** 处理微信用户关注消息 */
  private void handleSubscribeEvent(WechatMessage message) {
    WechatAccessToken accessToken = wechatAccessTokenRepository.getLatest();
    // 拉取用户信息
    WechatUser userInfo =
        WechatTunnel.getUserInfo(
            accessToken.getAccessToken(), wechatSetting.getAppID(), message.getFromUserName());
    if (userInfo == null) {
      log.info("获取用户信息失败,绑定用户信息完成");
    }
    // 持久化微信用户
    wechatUserInfoRepository.save(userInfo);

    handScanQrCodeEvent(message);
  }

  /** 尝试处理二维码事件 */
  private void handScanQrCodeEvent(WechatMessage message) {
    String userOpenId = message.getFromUserName();

    Optional<WechatUser> wechatUserOptional = wechatUserInfoRepository.getByOpenId(userOpenId);
    if (!wechatUserOptional.isPresent()) {
      log.info("用户尚未关注公众号，无法绑定用户");
      return;
    }
    // 解析二维码携带的信息
    String qrCodeContent = message.getEventKey();
    if (StringUtils.isEmpty(qrCodeContent)) {
      log.info("扫描二维码内容为空，暂不支持处理");
      return;
    }
    // 根据场景分别处理
    String qrCodeInfo = qrCodeContent.replace(QR_CODE_CONTENT_PREFIX, StringUtil.EMPTY_STRING);

    if (StringUtils.isEmpty(qrCodeInfo)) {
      log.info("二维码携带信息为空，暂不支持处理");
      return;
    }

    log.info("二维码携带信息：{}", qrCodeInfo);
    JsonObject jsonObject = new JsonParser().parse(qrCodeInfo).getAsJsonObject();
    WechatSceneEnum scene = WechatSceneEnum.valueOf(jsonObject.get("scene").getAsString());

    WechatUser wechatUser = wechatUserOptional.get();
    switch (scene) {
      case BOUND:
        UserId userId = UserId.create(jsonObject.get("userId").getAsString());
        TenantId tenantId = TenantId.create(jsonObject.get("tenantId").getAsString());
        wechatUser.setUserId(userId).setTenantId(tenantId);
        wechatUserInfoRepository.save(wechatUser);
        log.info("通过扫描二维码,微信用户:{} 成功和用户:{} 绑定", userOpenId, userId);
        break;
      case LOGIN:
        break;
      default:
        log.info("暂不支持的二维码扫描场景");
    }
  }
  // endregion
}
