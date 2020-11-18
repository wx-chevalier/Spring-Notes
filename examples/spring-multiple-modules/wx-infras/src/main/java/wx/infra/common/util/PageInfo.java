package wx.infra.common.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/** 分页信息 */
public interface PageInfo<T> {

  Integer getPageNumber();

  Integer getPageSize();

  default Page<T> generatorPage() {
    return new Page<T>(getPageNumber(), getPageSize());
  }
}
