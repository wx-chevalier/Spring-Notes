package wx.application.infra.area;

import java.util.List;
import wx.domain.infra.area.Area;

public interface AreaQueryService {

  /** 通过父级code获取区域列表 */
  List<Area> getByParentCode(String code);
}
