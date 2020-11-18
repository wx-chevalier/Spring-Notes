package wx.application.infra.message;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import wx.common.data.infra.notice.NoticeSendChannel;
import wx.common.data.infra.notice.NoticeTypeKind;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.MessageTypeId;
import wx.common.data.shared.id.RoleId;
import wx.common.data.shared.id.UserId;
import wx.domain.account.RoleRepository;
import wx.domain.infra.message.RoleMessageConfig;
import wx.domain.infra.message.RoleMessageConfigCommandService;
import wx.infra.common.exception.DataValidationException;
import wx.infra.common.exception.NotAcceptException;
import wx.infra.common.exception.NotFoundException;
import wx.infra.tunnel.db.infra.message.RoleMessageConfigDO;
import wx.infra.tunnel.db.infra.message.RoleMessageConfigTunnel;

@Slf4j
@Service
public class RoleMessageConfigCommandServiceImpl implements RoleMessageConfigCommandService {

  private RoleRepository roleRepository;

  private RoleMessageConfigTunnel configTunnel;

  private MessageTypeQueryService messageTypeQueryService;

  public RoleMessageConfigCommandServiceImpl(
      RoleMessageConfigTunnel configTunnel, MessageTypeQueryService messageTypeQueryService) {
    this.configTunnel = configTunnel;
    this.messageTypeQueryService = messageTypeQueryService;
  }

  @Override
  @Transactional
  public void update(
      TenantId tenantId, UserId userId, RoleId roleId, List<RoleMessageConfig> configs) {
    // 校验数据
    validateConfig(tenantId, roleId, configs);
    // 使之前的配置失效
    configTunnel.removeByRoleId(tenantId, roleId);
    // 遍历类型
    for (RoleMessageConfig config : configs) {
      NoticeTypeKind kind = config.getKind();
      List<NoticeSendChannel> sendChannel = config.getSendChannel();
      List<MessageTypeId> messageTypeIds = config.getMessageTypeId();
      // 校验给定的消息类型是否和给定的消息种类一致
      boolean match = messageTypeQueryService.checkKindMatch(messageTypeIds, kind);
      if (!match) {
        log.info("给定的类型:{}和给定的消息种类不匹配:{}", kind, messageTypeIds);
        throw new NotAcceptException("给定的消息种类%s中出现了不是该种类的消息类型");
      }
      saveSingleConfig(
          tenantId, roleId, userId, config.getSendInterval(), sendChannel, messageTypeIds);
    }
  }

  // 校验数据
  private void validateConfig(TenantId tenantId, RoleId roleId, List<RoleMessageConfig> configs) {
    // 判断角色存在
    boolean roleExist = roleRepository.exist(tenantId, roleId);
    if (!roleExist) {
      throw new NotFoundException("配置失败，角色不存在");
    }

    // 校验消息类型存在并且不重复
    List<MessageTypeId> messageTypeIdList =
        configs.stream()
            .map(RoleMessageConfig::getMessageTypeId)
            .flatMap(List::stream)
            .collect(Collectors.toList());

    if (messageTypeIdList.size() != Sets.newHashSet(messageTypeIdList).size()) {
      throw new DataValidationException("数据校验失败,出现重复消息类型");
    }

    // 判定消息类型均存在
    boolean messageTypesExist = messageTypeQueryService.exist(messageTypeIdList);
    if (!messageTypesExist) {
      throw new NotFoundException("数据校验失败, 出现无效的消息类型");
    }

    // 校验发送渠道不能为空
    boolean existEmpty =
        configs.stream().map(RoleMessageConfig::getSendChannel).anyMatch(CollectionUtils::isEmpty);
    if (existEmpty) {
      throw new DataValidationException("数据校验失败,发送渠道不能为空");
    }

    // 判定间隔时间不能为空
    boolean existErrorInterval =
        configs.stream()
            .map(RoleMessageConfig::getSendInterval)
            .allMatch(interval -> Objects.isNull(interval) || interval <= 0);
    if (existErrorInterval) {
      throw new DataValidationException("数据校验失败,发送时间间隔不能为空");
    }
  }

  private void saveSingleConfig(
      TenantId tenantId,
      RoleId roleId,
      UserId userId,
      Integer sendInterval,
      List<NoticeSendChannel> sendChannel,
      List<MessageTypeId> messageTypeIds) {
    // 构建保存的集合
    List<RoleMessageConfigDO> configDetails = new ArrayList<>();

    // 遍历构建数据
    for (NoticeSendChannel channel : sendChannel) {
      for (MessageTypeId messageTypeId : messageTypeIds) {
        RoleMessageConfigDO configDO = new RoleMessageConfigDO();
        configDO
            .setMessageTypeId(messageTypeId.getId())
            .setCreatorId(userId.getId())
            .setRoleId(roleId.getId())
            .setTenantId(tenantId.getId())
            .setSendChannel(channel.name())
            .setSendInterval(sendInterval);
        configDetails.add(configDO);
      }
    }
    // 写入持久层
    configTunnel.saveBatch(configDetails);
  }
}
