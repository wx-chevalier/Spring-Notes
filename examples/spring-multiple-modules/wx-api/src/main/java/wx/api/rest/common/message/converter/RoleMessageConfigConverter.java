package wx.api.rest.common.message.converter;

import java.util.List;
import java.util.stream.Collectors;
import wx.api.rest.common.message.dto.RoleMessageConfigUpdateReq;
import wx.common.data.shared.id.MessageTypeId;
import wx.domain.infra.message.RoleMessageConfig;

public class RoleMessageConfigConverter {

  public static RoleMessageConfig toRoleMessageConfig(RoleMessageConfigUpdateReq req) {
    List<MessageTypeId> messageTypeIds =
        req.getMessageTypeIds().stream().map(MessageTypeId::new).collect(Collectors.toList());
    return new RoleMessageConfig(messageTypeIds, req.getSendChannelList(), req.getInterval());
  }
}
