package wx.repository.wsat.repos.wrapper;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public abstract class MyBatisGeneratedRepo<Example, Criteria, DAO, DAOWithBlobs extends DAO> {
  abstract Mapper<Example, DAO, DAOWithBlobs> getMapper();

  abstract Example createExample();

  abstract Criteria createCriteria(Example example);

  public ManualMapper manualMapper() {
    throw new UnsupportedOperationException();
  }

  /** 满足条件数据是否存在 */
  public boolean existsForCondition(Consumer<Criteria> criteriaConsumer) {
    return countByCondition(criteriaConsumer) != 0;
  }

  /** 按条件获取数目 */
  public long countByCondition(Consumer<Criteria> criteriaConsumer) {
    return getMapper().countByExample(createExample(criteriaConsumer));
  }

  /** 根据条件获取列表 */
  public List<DAO> selectList(Consumer<Criteria> criteriaConsumer) {
    return selectList(criteriaConsumer, example -> {});
  }

  public List<DAO> selectList() {
    return getMapper().selectByExample(createExample());
  }

  /** 根据条件获取列表，example 额外处理 */
  public List<DAO> selectList(
      Consumer<Criteria> criteriaConsumer, Consumer<Example> exampleConsumer) {
    final Example example = createExample(criteriaConsumer);
    exampleConsumer.accept(example);
    return getMapper().selectByExample(example);
  }

  /** 根据条件获取列表，包含分页信息 */
  public Page<DAO> selectListWithPageInfo(
      Consumer<Criteria> criteriaConsumer, int page, int pageSize) {
    return selectListWithPageInfo(criteriaConsumer, page, pageSize, example -> {});
  }

  /** 根据条件获取列表，example 额外处理 */
  public Page<DAO> selectListWithPageInfo(
      Consumer<Criteria> criteriaConsumer,
      int page,
      int pageSize,
      Consumer<Example> exampleConsumer) {
    PageHelper.startPage(page, pageSize);
    final Example example = createExample(criteriaConsumer);
    exampleConsumer.accept(example);
    return (Page<DAO>) getMapper().selectByExample(example);
  }

  /** 按条件获取一条数据 */
  public Optional<DAO> selectOne(Consumer<Criteria> criteriaConsumer) {
    return selectOne(criteriaConsumer, example -> {});
  }

  /** 按条件获取一条数据 */
  public Optional<DAO> selectOne(
      Consumer<Criteria> criteriaConsumer, Consumer<Example> exampleConsumer) {
    final List<DAO> daoList = selectList(criteriaConsumer, exampleConsumer);
    if (daoList.size() == 0) {
      return Optional.empty();
    } else {
      return Optional.of(daoList.get(0));
    }
  }

  /** 根据条件获取列表，包含比较大字段 */
  public List<DAOWithBlobs> selectListWithBlobs(Consumer<Criteria> criteriaConsumer) {
    return selectListWithBlobs(criteriaConsumer, example -> {});
  }

  /** 根据条件获取列表，example 额外处理 */
  public List<DAOWithBlobs> selectListWithBlobs(
      Consumer<Criteria> criteriaConsumer, Consumer<Example> exampleConsumer) {
    final Example example = createExample(criteriaConsumer);
    exampleConsumer.accept(example);
    return getMapper().selectByExampleWithBLOBs(example);
  }

  public List<DAOWithBlobs> selectListWithBlobs() {
    return getMapper().selectByExampleWithBLOBs(createExample());
  }

  /** 根据条件获取列表，包含比较大字段 */
  public Optional<DAOWithBlobs> selectOneWithBlobs(Consumer<Criteria> criteriaConsumer) {
    return selectOneWithBlobs(criteriaConsumer, example -> {});
  }

  /** 根据条件获取列表，包含比较大字段 */
  public Optional<DAOWithBlobs> selectOneWithBlobs(
      Consumer<Criteria> criteriaConsumer, Consumer<Example> exampleConsumer) {
    final List<DAOWithBlobs> daoList = selectListWithBlobs(criteriaConsumer, exampleConsumer);
    if (daoList.size() == 0) {
      return Optional.empty();
    } else {
      return Optional.of(daoList.get(0));
    }
  }

  /** 插入一条数据 */
  public int insert(DAOWithBlobs dao) {
    return getMapper().insert(dao);
  }

  /** 插入一组数据 */
  @Transactional
  public void insertList(List<DAOWithBlobs> daoList) {
    for (DAOWithBlobs dao : daoList) {
      insert(dao);
    }
  }

  /** 插入一条数据，selective */
  public int insertSelective(DAOWithBlobs dao) {
    return getMapper().insertSelective(dao);
  }

  /** 插入一组数据，selective */
  @Transactional
  public void insertListSelective(List<DAOWithBlobs> daoList) {
    for (DAOWithBlobs dao : daoList) {
      insertSelective(dao);
    }
  }

  /** 根据条件删除 */
  @Transactional
  public int deleteByCondition(Consumer<Criteria> criteriaConsumer) {
    return getMapper().deleteByExample(createExample(criteriaConsumer));
  }

  /** 根据主键更新所有数据 */
  public void updateListByPrimaryKey(List<DAO> daoList) {
    for (DAO dao : daoList) {
      getMapper().updateByPrimaryKey(dao);
    }
  }

  /** 根据主键更新所有数据，只更新非空字段 */
  public void updateListByPrimaryKeySelective(List<DAOWithBlobs> daoList) {
    for (DAOWithBlobs dao : daoList) {
      getMapper().updateByPrimaryKeySelective(dao);
    }
  }

  /** 根据条件更新数据 */
  public int updateByCondition(DAO dao, Consumer<Criteria> criteriaConsumer) {
    return getMapper().updateByExample(dao, createExample(criteriaConsumer));
  }

  /** 根据条件更新数据，只更新非空字段 */
  public int updateByConditionSelective(DAOWithBlobs dao, Consumer<Criteria> criteriaConsumer) {
    return getMapper().updateByExampleSelective(dao, createExample(criteriaConsumer));
  }

  /** 根据条件更新所有字段，包含比较大的字段 */
  public int updateByConditionWithBlobs(DAOWithBlobs dao, Consumer<Criteria> criteriaConsumer) {
    return getMapper().updateByExampleWithBlobs(dao, createExample(criteriaConsumer));
  }

  private Example createExample(Consumer<Criteria> criteriaConsumer) {
    final Example example = createExample();
    criteriaConsumer.accept(createCriteria(example));
    return example;
  }

  interface Mapper<Example, DAO, DAOWithBlobs extends DAO> {
    long countByExample(Example example);

    int insert(DAOWithBlobs dao);

    int insertSelective(DAOWithBlobs dao);

    int deleteByExample(Example example);

    int updateByPrimaryKey(DAO dao);

    int updateByPrimaryKeySelective(DAOWithBlobs dao);

    int updateByExample(DAO dao, Example example);

    int updateByExampleSelective(DAOWithBlobs dao, Example example);

    default int updateByExampleWithBlobs(DAOWithBlobs dao, Example example) {
      throw new UnsupportedOperationException();
    }

    List<DAO> selectByExample(Example example);

    default List<DAOWithBlobs> selectByExampleWithBLOBs(Example example) {
      throw new UnsupportedOperationException();
    }
  }

  public interface ManualMapper {}
}
