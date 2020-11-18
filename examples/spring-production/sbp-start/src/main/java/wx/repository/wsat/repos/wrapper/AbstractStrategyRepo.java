package wx.repository.wsat.repos.wrapper;

import java.util.List;
import wx.repository.wsat.dao.mapper.StrategyDaoMapper;
import wx.repository.wsat.dao.model.StrategyDao;
import wx.repository.wsat.dao.model.StrategyDaoExample;
import wx.repository.wsat.dao.model.StrategyDaoExample.Criteria;

public abstract class AbstractStrategyRepo
    extends MyBatisGeneratedRepo<
        StrategyDaoExample, StrategyDaoExample.Criteria, StrategyDao, StrategyDao> {
  private StrategyDaoMapper mapper;

  public AbstractStrategyRepo(StrategyDaoMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  Mapper<StrategyDaoExample, StrategyDao, StrategyDao> getMapper() {
    return new Mapper<StrategyDaoExample, StrategyDao, StrategyDao>() {
      @Override
      public long countByExample(StrategyDaoExample strategyDaoExample) {
        return mapper.countByExample(strategyDaoExample);
      }

      @Override
      public int insert(StrategyDao strategyDao) {
        return mapper.insert(strategyDao);
      }

      @Override
      public int insertSelective(StrategyDao strategyDao) {
        return mapper.insertSelective(strategyDao);
      }

      @Override
      public int deleteByExample(StrategyDaoExample strategyDaoExample) {
        return mapper.deleteByExample(strategyDaoExample);
      }

      @Override
      public int updateByPrimaryKey(StrategyDao strategyDao) {
        return mapper.updateByPrimaryKey(strategyDao);
      }

      @Override
      public int updateByPrimaryKeySelective(StrategyDao strategyDao) {
        return mapper.updateByPrimaryKeySelective(strategyDao);
      }

      @Override
      public int updateByExample(StrategyDao strategyDao, StrategyDaoExample strategyDaoExample) {
        return mapper.updateByExample(strategyDao, strategyDaoExample);
      }

      @Override
      public int updateByExampleSelective(
          StrategyDao strategyDao, StrategyDaoExample strategyDaoExample) {
        return mapper.updateByExampleSelective(strategyDao, strategyDaoExample);
      }

      @Override
      public List<StrategyDao> selectByExample(StrategyDaoExample strategyDaoExample) {
        return mapper.selectByExample(strategyDaoExample);
      }
    };
  }

  @Override
  StrategyDaoExample createExample() {
    return new StrategyDaoExample();
  }

  @Override
  Criteria createCriteria(StrategyDaoExample strategyDaoExample) {
    return strategyDaoExample.createCriteria();
  }
}
