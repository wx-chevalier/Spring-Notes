package wx.mapper;

import wx.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Product mapper for operate data of products table
 *
 */

@Mapper
public interface ProductDao {
    Product select(@Param("id") long id);

    Integer update(Product product);

    Integer insert(Product product);

    Integer delete(long productId);

    List<Product> selectAll();
}
