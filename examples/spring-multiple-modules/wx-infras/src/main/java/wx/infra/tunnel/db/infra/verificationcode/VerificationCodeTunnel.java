package wx.infra.tunnel.db.infra.verificationcode;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.shared.id.UserId;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.infra.validatecode.ValidateCodeMapper;
import wx.infra.tunnel.db.shared.BaseDO;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class VerificationCodeTunnel extends ServiceImpl<ValidateCodeMapper, VerificationCodeDO> {

  /**
   * 检查在指定的时间范围内，某个类型的消息发送的次数
   *
   * @param second 时间范围 单位:秒
   * @param noticeType 发送的验证码类型
   * @param dest 发送目标
   * @return
   */
  public int getSendCount(
      int second, NoticeType noticeType, NoticeSendChannel channel, String dest) {

    Wrapper<VerificationCodeDO> queryWrapper =
        Helper.getQueryWrapper(VerificationCodeDO.class)
            .eq(VerificationCodeDO::getType, noticeType)
            .eq(VerificationCodeDO::getSendDst, dest.trim())
            .ge(VerificationCodeDO::getSentAt, LocalDateTime.now().plusSeconds(-second));
    return this.baseMapper.selectCount(queryWrapper);
  }

  public int checkVerificationCode(
      int second, String code, NoticeType noticeType, NoticeSendChannel channel, String dest) {
    if (!StringUtils.hasText(code)) {
      return 0;
    }

    Wrapper<VerificationCodeDO> queryWrapper =
        Helper.getQueryWrapper(VerificationCodeDO.class)
            .eq(VerificationCodeDO::getCode, code.trim())
            .eq(VerificationCodeDO::getType, noticeType)
            .eq(VerificationCodeDO::getSendDst, dest.trim())
            .ge(VerificationCodeDO::getSentAt, LocalDateTime.now().plusSeconds(-second));
    return this.baseMapper.selectCount(queryWrapper);
  }

  /**
   * 获取指定时间内未使用过的验证码信息系
   *
   * <p>本方法使用场景为：验证邮件链接地址信息</>
   *
   * @param noticeType 验证码类型
   * @param code 验证码
   * @return 存在的验证吗
   */
  public VerificationCodeDO get(int second, NoticeType noticeType, String code) {

    Wrapper<VerificationCodeDO> queryWrapper =
        Helper.getQueryWrapper(VerificationCodeDO.class)
            .eq(VerificationCodeDO::getCode, code)
            .eq(VerificationCodeDO::getType, noticeType)
            .ge(VerificationCodeDO::getSentAt, LocalDateTime.now().plusSeconds(-second))
            .orderByDesc(VerificationCodeDO::getSentAt)
            .last("LIMIT 1");
    return this.baseMapper.selectOne(queryWrapper);
  }

  /**
   * 清除指定用户的验证码
   *
   * <p>此方法应用场景为：用户多次发送验证码，在某次发送验证码之前需要将之前发送的同类型的验证码作废</>
   */
  public void cleanUnused(UserId userId, NoticeType type, NoticeSendChannel channel) {

    Wrapper<VerificationCodeDO> updateWrapper =
        Helper.getQueryWrapper(VerificationCodeDO.class)
            .eq(VerificationCodeDO::getUserId, userId.getId())
            .eq(VerificationCodeDO::getType, type)
            .eq(Objects.nonNull(channel), VerificationCodeDO::getChannel, channel)
            .isNull(VerificationCodeDO::getVerifiedAt)
            .isNotNull(BaseDO::getDeletedAt);

    VerificationCodeDO entity = new VerificationCodeDO();
    entity.setDeletedAt(LocalDateTime.now());
    this.baseMapper.update(entity, updateWrapper);
  }
}
