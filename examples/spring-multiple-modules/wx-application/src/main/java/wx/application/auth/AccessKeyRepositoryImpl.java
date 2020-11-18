package wx.application.auth;

import static java.util.Optional.ofNullable;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wx.common.data.shared.id.*;
import wx.domain.auth.AccessKey;
import wx.domain.auth.AccessKeyRepository;
import wx.infra.tunnel.db.auth.AccessKeyDO;
import wx.infra.tunnel.db.auth.AccessKeyTunnel;

@Repository
public class AccessKeyRepositoryImpl implements AccessKeyRepository {

  private AccessKeyTunnel tunnel;
  private AccessKeyConverter converter;

  public AccessKeyRepositoryImpl(AccessKeyTunnel tunnel, AccessKeyConverter converter) {
    this.tunnel = tunnel;
    this.converter = converter;
  }

  @Override
  @Transactional
  public AccessKey save(AccessKey accessKey) {
    if (tunnel.exists(accessKey.getKey())) {
      throw new IllegalStateException("Duplicated access key");
    }
    tunnel.save(converter.convertTo(accessKey));
    return accessKey;
  }

  @Override
  public boolean removeKey(AccessKey accessKey) {
    return tunnel.remove(
        new LambdaQueryWrapper<AccessKeyDO>()
            .eq(AccessKeyDO::getTenantId, accessKey.getTenantId().getId())
            .eq(AccessKeyDO::getKey, accessKey.getKey()));
  }

  @Override
  public Optional<AccessKey> findByKey(TenantId tenantId, String key) {
    return ofNullable(
            tunnel.getOne(
                new LambdaQueryWrapper<AccessKeyDO>()
                    .eq(AccessKeyDO::getTenantId, tenantId.getId())
                    .eq(AccessKeyDO::getKey, key)))
        .map(converter::convertFrom);
  }

  @Override
  public Optional<AccessKey> findByKey(String key) {
    return ofNullable(
            tunnel.getOne(new LambdaQueryWrapper<AccessKeyDO>().eq(AccessKeyDO::getKey, key)))
        .map(converter::convertFrom);
  }
}
