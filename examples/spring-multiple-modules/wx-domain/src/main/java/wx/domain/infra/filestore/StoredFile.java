package wx.domain.infra.filestore;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import wx.common.data.infra.filestore.FileStoreInfo;
import wx.common.data.infra.filestore.FileStoreType;
import wx.common.data.shared.HasName;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.FileId;
import wx.domain.shared.IdBasedEntity;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoredFile extends IdBasedEntity<FileId, StoredFile> implements HasName {

  private String name;

  private String md5;

  private Long size;

  private FileStoreType saveType;

  private FileStoreInfo storeInfo;

  public StoredFile(
      FileId id,
      TenantId tenantId,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      String name,
      String md5,
      Long size,
      FileStoreType saveType,
      FileStoreInfo storeInfo) {
    super(id, tenantId, createdAt, updatedAt);
    this.name = name;
    this.md5 = md5;
    this.size = size;
    this.saveType = saveType;
    this.storeInfo = storeInfo;
  }

  public StoredFile(
      TenantId tenantId,
      String name,
      String md5,
      Long size,
      FileStoreType saveType,
      FileStoreInfo storeInfo) {
    super(tenantId);
    this.name = name;
    this.md5 = md5;
    this.size = size;
    this.saveType = saveType;
    this.storeInfo = storeInfo;
  }
}
