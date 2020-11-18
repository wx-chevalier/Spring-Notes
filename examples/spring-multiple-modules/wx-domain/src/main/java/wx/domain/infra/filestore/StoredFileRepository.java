package wx.domain.infra.filestore;

import java.util.Collection;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.FileId;
import wx.domain.shared.IdBasedEntityRepository;

public interface StoredFileRepository extends IdBasedEntityRepository<FileId, StoredFile> {

  /** 断定文件均存在 */
  boolean assertExist(TenantId tenantId, Collection<FileId> fileIds);
}
