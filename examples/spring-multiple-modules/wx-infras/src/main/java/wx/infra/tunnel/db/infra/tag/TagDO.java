package wx.infra.tunnel.db.infra.tag;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wx.common.data.infra.TagType;
import wx.infra.tunnel.db.shared.BaseDO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("infra_tag")
public class TagDO extends BaseDO<TagDO> {

  private static final long serialVersionUID = 1L;

  private TagType tagType;

  private String tag;

  private Long creatorId;
}
