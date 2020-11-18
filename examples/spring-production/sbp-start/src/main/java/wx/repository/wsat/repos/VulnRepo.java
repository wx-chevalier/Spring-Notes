package wx.repository.wsat.repos;

import org.springframework.stereotype.Component;
import wx.repository.wsat.dao.mapper.VulnDaoMapper;
import wx.repository.wsat.repos.wrapper.AbstractVulnRepo;

@Component
public class VulnRepo extends AbstractVulnRepo {
  public VulnRepo(VulnDaoMapper mapper) {
    super(mapper);
  }
}
