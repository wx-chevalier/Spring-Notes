package wx.repository.wsat.repos;

import org.springframework.stereotype.Component;
import wx.repository.wsat.dao.mapper.StrategyDaoMapper;
import wx.repository.wsat.repos.wrapper.AbstractStrategyRepo;

@Component
public class StrategyRepo extends AbstractStrategyRepo {
  public StrategyRepo(StrategyDaoMapper mapper) {
    super(mapper);
  }
}
