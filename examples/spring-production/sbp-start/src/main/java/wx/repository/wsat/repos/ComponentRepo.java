package wx.repository.wsat.repos;

import org.springframework.stereotype.Component;
import wx.repository.wsat.dao.mapper.ComponentDaoMapper;
import wx.repository.wsat.repos.wrapper.AbstractComponentRepo;

@Component
public class ComponentRepo extends AbstractComponentRepo {
  public ComponentRepo(ComponentDaoMapper mapper) {
    super(mapper);
  }
}
