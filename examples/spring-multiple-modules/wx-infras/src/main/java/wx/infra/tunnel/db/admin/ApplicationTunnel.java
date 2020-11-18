package wx.infra.tunnel.db.admin;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.admin.ApplicationMapper;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ApplicationTunnel extends ServiceImpl<ApplicationMapper, ApplicationDO> {
  public List<ApplicationDO> getAll() {
    Wrapper<ApplicationDO> queryWrapper =
        Helper.getQueryWrapper(ApplicationDO.class)
            .isNull(ApplicationDO::getDeletedAt)
            .orderByAsc(ApplicationDO::getCreatedAt);
    return this.baseMapper.selectList(queryWrapper);
  }
}
