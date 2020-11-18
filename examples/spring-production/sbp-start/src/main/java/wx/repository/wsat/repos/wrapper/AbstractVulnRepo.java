package wx.repository.wsat.repos.wrapper;

import java.util.List;
import wx.repository.wsat.dao.mapper.VulnDaoMapper;
import wx.repository.wsat.dao.model.VulnDao;
import wx.repository.wsat.dao.model.VulnDaoExample;
import wx.repository.wsat.dao.model.VulnDaoExample.Criteria;
import wx.repository.wsat.dao.model.VulnDaoWithBLOBs;

public class AbstractVulnRepo
    extends MyBatisGeneratedRepo<
        VulnDaoExample, VulnDaoExample.Criteria, VulnDao, VulnDaoWithBLOBs> {
  private VulnDaoMapper mapper;

  public AbstractVulnRepo(VulnDaoMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  Mapper<VulnDaoExample, VulnDao, VulnDaoWithBLOBs> getMapper() {
    return new Mapper<VulnDaoExample, VulnDao, VulnDaoWithBLOBs>() {
      @Override
      public long countByExample(VulnDaoExample vulnDaoExample) {
        return mapper.countByExample(vulnDaoExample);
      }

      @Override
      public int insert(VulnDaoWithBLOBs vulnDaoWithBLOBs) {
        return mapper.insert(vulnDaoWithBLOBs);
      }

      @Override
      public int insertSelective(VulnDaoWithBLOBs vulnDaoWithBLOBs) {
        return mapper.insertSelective(vulnDaoWithBLOBs);
      }

      @Override
      public int deleteByExample(VulnDaoExample vulnDaoExample) {
        return mapper.deleteByExample(vulnDaoExample);
      }

      @Override
      public int updateByPrimaryKey(VulnDao vulnDao) {
        return mapper.updateByPrimaryKey(vulnDao);
      }

      @Override
      public int updateByPrimaryKeySelective(VulnDaoWithBLOBs vulnDaoWithBLOBs) {
        return mapper.updateByPrimaryKeySelective(vulnDaoWithBLOBs);
      }

      @Override
      public int updateByExample(VulnDao vulnDao, VulnDaoExample vulnDaoExample) {
        return mapper.updateByExample(vulnDao, vulnDaoExample);
      }

      @Override
      public int updateByExampleSelective(
          VulnDaoWithBLOBs vulnDaoWithBLOBs, VulnDaoExample vulnDaoExample) {
        return mapper.updateByExampleSelective(vulnDaoWithBLOBs, vulnDaoExample);
      }

      @Override
      public List<VulnDao> selectByExample(VulnDaoExample vulnDaoExample) {
        return mapper.selectByExample(vulnDaoExample);
      }
    };
  }

  @Override
  VulnDaoExample createExample() {
    return new VulnDaoExample();
  }

  @Override
  Criteria createCriteria(VulnDaoExample vulnDaoExample) {
    return vulnDaoExample.createCriteria();
  }
}
