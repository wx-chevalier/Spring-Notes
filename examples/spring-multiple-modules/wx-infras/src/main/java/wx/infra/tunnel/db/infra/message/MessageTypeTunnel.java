package wx.infra.tunnel.db.infra.message;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import wx.common.data.infra.notice.NoticeType;
import wx.common.data.infra.notice.NoticeTypeKind;
import wx.common.data.shared.id.BaseEntityId;
import wx.common.data.shared.id.MessageTypeId;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.infra.message.MessageTypeMapper;
import wx.infra.tunnel.db.shared.BaseDO;

@Component
public class MessageTypeTunnel extends ServiceImpl<MessageTypeMapper, MessageTypeDO> {

  /** 根据消息类型的Key获取详情信息 */
  public MessageTypeDO getByNoticeType(NoticeType type) {
    Wrapper<MessageTypeDO> queryWrapper =
        Helper.getQueryWrapper(MessageTypeDO.class).eq(MessageTypeDO::getKey, type.name());
    return this.baseMapper.selectOne(queryWrapper);
  }

  /** 校验集合中的消息类型属于消息类型kind */
  public boolean match(List<MessageTypeId> ids, NoticeTypeKind kind) {
    if (CollectionUtils.isEmpty(ids)) {
      return true;
    }

    if (kind == null) {
      return false;
    }
    Set<Long> messageTypeIds = ids.stream().map(BaseEntityId::getId).collect(Collectors.toSet());
    Wrapper<MessageTypeDO> queryWrapper =
        Helper.getQueryWrapper(MessageTypeDO.class).in(MessageTypeDO::getId, messageTypeIds);

    int queryCount = this.baseMapper.selectCount(queryWrapper);
    return queryCount == messageTypeIds.size();
  }

  public int count(List<MessageTypeId> idList) {
    Set<Long> messageTYpeIds = idList.stream().map(BaseEntityId::getId).collect(Collectors.toSet());

    Wrapper<MessageTypeDO> queryWrapper =
        Helper.getQueryWrapper(MessageTypeDO.class)
            .in(MessageTypeDO::getId, messageTYpeIds)
            .isNull(BaseDO::getDeletedAt);
    return this.baseMapper.selectCount(queryWrapper);
  }
}
