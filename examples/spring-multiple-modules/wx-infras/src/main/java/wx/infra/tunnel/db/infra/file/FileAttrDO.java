package wx.infra.tunnel.db.infra.file;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wx.infra.tunnel.db.shared.BaseDO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("infra_file_attr")
public class FileAttrDO extends BaseDO<FileAttrDO> {

  private static final long serialVersionUID = 1L;

  private Long fileId;

  private String attrName;

  private String attrValue;
}
