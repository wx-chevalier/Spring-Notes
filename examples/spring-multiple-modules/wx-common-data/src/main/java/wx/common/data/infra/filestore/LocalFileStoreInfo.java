package wx.common.data.infra.filestore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LocalFileStoreInfo extends FileStoreInfo {

  private String localFileId;

  @Override
  public FileStoreType getType() {
    return FileStoreType.LOCAL;
  }
}
