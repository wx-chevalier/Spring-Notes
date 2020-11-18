package wx.repository.wsat.repos.wrapper;

import java.util.List;
import wx.repository.wsat.dao.mapper.PocDaoMapper;
import wx.repository.wsat.dao.model.PocDao;
import wx.repository.wsat.dao.model.PocDaoExample;

public abstract class AbstractPocRepo
    extends MyBatisGeneratedRepo<PocDaoExample, PocDaoExample.Criteria, PocDao, PocDao> {
  private PocDaoMapper mapper;

  public AbstractPocRepo(PocDaoMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  Mapper<PocDaoExample, PocDao, PocDao> getMapper() {
    return new Mapper<PocDaoExample, PocDao, PocDao>() {
      @Override
      public long countByExample(PocDaoExample pocDaoExample) {
        return mapper.countByExample(pocDaoExample);
      }

      @Override
      public int insert(PocDao pocDao) {
        return mapper.insert(pocDao);
      }

      @Override
      public int insertSelective(PocDao pocDao) {
        return mapper.insertSelective(pocDao);
      }

      @Override
      public int deleteByExample(PocDaoExample pocDaoExample) {
        return mapper.deleteByExample(pocDaoExample);
      }

      @Override
      public int updateByPrimaryKey(PocDao pocDao) {
        return mapper.updateByPrimaryKey(pocDao);
      }

      @Override
      public int updateByPrimaryKeySelective(PocDao pocDao) {
        return mapper.updateByPrimaryKeySelective(pocDao);
      }

      @Override
      public int updateByExample(PocDao pocDao, PocDaoExample pocDaoExample) {
        return mapper.updateByExample(pocDao, pocDaoExample);
      }

      @Override
      public int updateByExampleSelective(PocDao pocDao, PocDaoExample pocDaoExample) {
        return mapper.updateByExampleSelective(pocDao, pocDaoExample);
      }

      @Override
      public List<PocDao> selectByExample(PocDaoExample pocDaoExample) {
        return mapper.selectByExample(pocDaoExample);
      }
    };
  }

  @Override
  PocDaoExample createExample() {
    return new PocDaoExample();
  }

  @Override
  PocDaoExample.Criteria createCriteria(PocDaoExample pocDaoExample) {
    return pocDaoExample.createCriteria();
  }
}
