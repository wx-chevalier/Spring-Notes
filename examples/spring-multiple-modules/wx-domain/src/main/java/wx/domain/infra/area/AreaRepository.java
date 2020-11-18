package wx.domain.infra.area;

import java.util.Optional;
import wx.common.data.shared.id.AreaId;
import wx.domain.shared.IdBasedEntityRepository;

public interface AreaRepository extends IdBasedEntityRepository<AreaId, Area> {

  Optional<Area> findByCode(String code);
}
