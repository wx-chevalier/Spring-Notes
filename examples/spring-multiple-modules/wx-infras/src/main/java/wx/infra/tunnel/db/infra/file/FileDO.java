package wx.infra.tunnel.db.infra.file;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import wx.common.data.infra.filestore.FileStoreType;
import wx.infra.tunnel.db.shared.BaseDO;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("infra_file")
public class FileDO extends BaseDO<FileDO> {

  private static final long serialVersionUID = 1L;

  private String name;

  private Long size;

  private String fileMd5;

  private String fileStoreInfo;

  private FileStoreType saveType;
}
