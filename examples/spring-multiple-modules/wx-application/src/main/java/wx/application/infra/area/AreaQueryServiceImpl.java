package wx.application.infra.area;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wx.domain.infra.area.Area;
import wx.infra.tunnel.db.infra.area.AreaTunnel;

@Slf4j
@Service
public class AreaQueryServiceImpl implements AreaQueryService {

  private AreaTunnel areaTunnel;

  private AreaConverter areaConverter;

  public AreaQueryServiceImpl(AreaTunnel areaTunnel, AreaConverter areaConverter) {
    this.areaTunnel = areaTunnel;
    this.areaConverter = areaConverter;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Area> getByParentCode(String code) {
    log.info("获取区域代码为:{} 的下级别的区域信息", code);
    return areaTunnel.getByParentCode(code).stream()
        .map(areaConverter::convertFrom)
        .collect(Collectors.toList());
  }
}
