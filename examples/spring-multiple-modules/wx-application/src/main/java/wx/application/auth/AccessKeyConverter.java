package wx.application.auth;

import org.springframework.stereotype.Component;
import wx.common.data.common.AbstractConverter;
import wx.domain.auth.AccessKey;
import wx.infra.tunnel.db.auth.AccessKeyDO;

@Component
public class AccessKeyConverter extends AbstractConverter<AccessKey, AccessKeyDO> {

  private AccessKeyConverter() {}

  @Override
  public AccessKeyDO convertTo(AccessKey accessKey) {
    return new AccessKeyDO(
        accessKey.getTenantId().getId(),
        accessKey.getKey(),
        accessKey.getSecret(),
        accessKey.getEntityId().getEntityType(),
        accessKey.getEntityId().getId());
  }

  @Override
  public AccessKey convertFrom(AccessKeyDO accessKeyDO) {
    return AccessKey.assemble(
        accessKeyDO.getTenantId(),
        accessKeyDO.getKey(),
        accessKeyDO.getSecret(),
        accessKeyDO.getEntityId(),
        accessKeyDO.getEntityType());
  }
}
