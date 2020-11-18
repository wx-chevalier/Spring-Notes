package wx.application.infra.area;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wx.common.data.shared.id.AreaId;
import wx.domain.infra.area.Area;
import wx.domain.infra.area.AreaRepository;
import wx.infra.common.persistence.MyBatisIdBasedEntityRepository;
import wx.infra.tunnel.db.infra.area.AreaDO;
import wx.infra.tunnel.db.infra.area.AreaTunnel;
import wx.infra.tunnel.db.mapper.infra.area.AreaMapper;

@Slf4j
@Component
public class AreaRepositoryImpl
    extends MyBatisIdBasedEntityRepository<AreaTunnel, AreaMapper, AreaDO, Area, AreaId>
    implements AreaRepository {

  @Getter(AccessLevel.PROTECTED)
  private AreaConverter converter;

  public AreaRepositoryImpl(AreaTunnel areaTunnel, AreaMapper mapper, AreaConverter converter) {
    super(areaTunnel, mapper);
    this.converter = converter;
  }

  @Override
  public Optional<Area> findByCode(String code) {
    return getTunnel().findByCode(code).map(converter::convertFrom);
  }
}
