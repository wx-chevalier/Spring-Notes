package wx.infra.tunnel.db.account;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import wx.common.data.shared.id.*;
import wx.infra.converter.PageConverter;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.account.TenantMapper;

@Component
public class TenantTunnel extends ServiceImpl<TenantMapper, TenantDO> {
  public TenantDO get(TenantId tenantId) {
    return getOne(Helper.getQueryWrapper(TenantDO.class).eq(TenantDO::getId, tenantId.getId()));
  }

  public Collection<TenantId> getAllTenantIds() {
    return list(new LambdaQueryWrapper<TenantDO>().isNull(TenantDO::getDeletedAt)).stream()
        .map(TenantDO::getId)
        .map(TenantId::new)
        .collect(Collectors.toSet());
  }

  public IPage<TenantDO> getAllTenant(Pageable pageable, String searchText, String areaCode) {
    IPage<TenantDO> page = PageConverter.toIPage(pageable);
    return page(
        page,
        new LambdaQueryWrapper<TenantDO>()
            .eq(StringUtils.hasText(areaCode), TenantDO::getAreaCode, areaCode)
            .like(StringUtils.hasText(searchText), TenantDO::getName, searchText)
            .isNull(TenantDO::getDeletedAt)
            .orderByDesc(TenantDO::getId));
  }

  public List<TenantDO> getAllTenant() {
    return list(new LambdaQueryWrapper<TenantDO>().isNull(TenantDO::getDeletedAt));
  }

  public TenantDO getByName(String name) {
    return getOne(
        new LambdaQueryWrapper<TenantDO>()
            .eq(TenantDO::getName, name)
            .isNull(TenantDO::getDeletedAt));
  }
}
