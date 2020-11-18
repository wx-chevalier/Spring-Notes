package wx.common.data.infra.filestore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

@Data
@EqualsAndHashCode(callSuper = true)
public class OssFileStoreInfo extends FileStoreInfo {

  @Deprecated private String url;

  private String bucketName;

  // Ali OSS 存储 key
  private String key;

  private @Nullable PreSignedUrl preSignedUrl;

  @Override
  public FileStoreType getType() {
    return FileStoreType.OSS;
  }
}
