package wx.infra.tunnel.db.infra.kv;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@Data
@TableName("infra_ts_kv")
public class TsKvDO implements Serializable {

  private static final long serialVersionUID = 1L;

  @TableId(type = IdType.INPUT)
  private String entityType;

  @TableId(type = IdType.INPUT)
  private Long entityId;

  @TableId(type = IdType.INPUT)
  @TableField(keepGlobalFormat = true)
  private String key;

  private Long ts;

  private Boolean boolV;

  private String strV;

  private Long longV;

  private Double dblV;
}
