package wx.infra.tunnel.db.infra.area;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wx.infra.tunnel.db.shared.BaseDO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("infra_cn_area")
public class AreaDO extends BaseDO<AreaDO> {

  private Integer level;

  private String parentCode;

  private String areaCode;

  private String zipCode;

  private String cityCode;

  private String name;

  private String shortName;

  private String mergerName;

  private String pinyin;

  private String lng;

  private String lat;
}
