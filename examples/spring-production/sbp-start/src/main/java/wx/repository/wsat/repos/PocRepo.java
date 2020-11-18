package wx.repository.wsat.repos;

import org.springframework.stereotype.Component;
import wx.repository.wsat.dao.mapper.PocDaoMapper;
import wx.repository.wsat.repos.wrapper.AbstractPocRepo;

@Component
public class PocRepo extends AbstractPocRepo {
  public PocRepo(PocDaoMapper mapper) {
    super(mapper);
  }
}
