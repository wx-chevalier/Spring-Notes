package wx.infra.converter;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

public class PageConverter {

  /** Convert spring data domain {@code Pageable} to mybatis plus {@code IPage}. */
  public static <T> IPage<T> toIPage(Pageable pageable) {
    Page<T> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
    page.setOrders(
        pageable.getSort().stream()
            .map(
                ord ->
                    new OrderItem()
                        .setColumn(ord.getProperty())
                        .setAsc(ord.getDirection() == Direction.ASC))
            .collect(Collectors.toList()));
    return page;
  }

  /** Convert mybatis plus {@code IPage} to spring data domain {@code Page}. */
  public static <T> org.springframework.data.domain.Page<T> toPage(
      IPage<T> page, Pageable pageable) {
    return new PageImpl<T>(page.getRecords(), pageable, page.getTotal());
  }
}
