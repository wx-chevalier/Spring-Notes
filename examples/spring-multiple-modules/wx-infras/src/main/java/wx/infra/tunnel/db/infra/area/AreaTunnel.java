package wx.infra.tunnel.db.infra.area;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.infra.area.AreaMapper;
import wx.infra.tunnel.db.shared.BaseDO;

@Component
public class AreaTunnel extends ServiceImpl<AreaMapper, AreaDO> {

  /** 通过code获取数据 */
  public Optional<AreaDO> findByCode(String code) {
    if (!StringUtils.hasText(code)) {
      return Optional.empty();
    }
    Wrapper<AreaDO> queryWrapper =
        Helper.getQueryWrapper(AreaDO.class)
            .eq(AreaDO::getAreaCode, code)
            .isNull(AreaDO::getDeletedAt);
    return Optional.ofNullable(this.getOne(queryWrapper));
  }

  public List<AreaDO> getByParentCode(String code) {
    if (!StringUtils.hasText(code)) {
      return new ArrayList<>(0);
    }
    Wrapper<AreaDO> queryWrapper =
        Helper.getQueryWrapper(AreaDO.class)
            .eq(AreaDO::getParentCode, code)
            .isNull(BaseDO::getDeletedAt)
            .orderByAsc(BaseDO::getId);
    return this.baseMapper.selectList(queryWrapper);
  }
}
