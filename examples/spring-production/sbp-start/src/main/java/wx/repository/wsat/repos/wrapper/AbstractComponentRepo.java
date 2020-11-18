package wx.repository.wsat.repos.wrapper;

import java.util.List;
import wx.repository.wsat.dao.mapper.ComponentDaoMapper;
import wx.repository.wsat.dao.model.ComponentDao;
import wx.repository.wsat.dao.model.ComponentDaoExample;
import wx.repository.wsat.dao.model.ComponentDaoExample.Criteria;
import wx.repository.wsat.dao.model.ComponentDaoWithBLOBs;

public abstract class AbstractComponentRepo
    extends MyBatisGeneratedRepo<
        ComponentDaoExample, ComponentDaoExample.Criteria, ComponentDao, ComponentDaoWithBLOBs> {
  private ComponentDaoMapper mapper;

  public AbstractComponentRepo(ComponentDaoMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  Mapper<ComponentDaoExample, ComponentDao, ComponentDaoWithBLOBs> getMapper() {
    return new Mapper<ComponentDaoExample, ComponentDao, ComponentDaoWithBLOBs>() {
      @Override
      public long countByExample(ComponentDaoExample componentDaoExample) {
        return mapper.countByExample(componentDaoExample);
      }

      @Override
      public int insert(ComponentDaoWithBLOBs componentDaoWithBLOBs) {
        return mapper.insert(componentDaoWithBLOBs);
      }

      @Override
      public int insertSelective(ComponentDaoWithBLOBs componentDaoWithBLOBs) {
        return mapper.insertSelective(componentDaoWithBLOBs);
      }

      @Override
      public int deleteByExample(ComponentDaoExample componentDaoExample) {
        return mapper.deleteByExample(componentDaoExample);
      }

      @Override
      public int updateByPrimaryKey(ComponentDao componentDao) {
        return mapper.updateByPrimaryKey(componentDao);
      }

      @Override
      public int updateByPrimaryKeySelective(ComponentDaoWithBLOBs componentDaoWithBLOBs) {
        return mapper.updateByPrimaryKeySelective(componentDaoWithBLOBs);
      }

      @Override
      public int updateByExample(
          ComponentDao componentDao, ComponentDaoExample componentDaoExample) {
        return mapper.updateByExample(componentDao, componentDaoExample);
      }

      @Override
      public int updateByExampleSelective(
          ComponentDaoWithBLOBs componentDaoWithBLOBs, ComponentDaoExample componentDaoExample) {
        return mapper.updateByExampleSelective(componentDaoWithBLOBs, componentDaoExample);
      }

      @Override
      public List<ComponentDao> selectByExample(ComponentDaoExample componentDaoExample) {
        return mapper.selectByExample(componentDaoExample);
      }
    };
  }

  @Override
  ComponentDaoExample createExample() {
    return new ComponentDaoExample();
  }

  @Override
  Criteria createCriteria(ComponentDaoExample componentDaoExample) {
    return componentDaoExample.createCriteria();
  }
}
