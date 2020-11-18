package wx.application.infra.filestore;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import wx.common.data.infra.filestore.LocalFileStoreInfo;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.FileId;
import wx.domain.infra.filestore.StoredFile;

public interface StoredFileQueryService {

  StoredFileDetail findFileDetail(StoredFile storedFile);

  Optional<StoredFileDetail> findFileDetail(TenantId tenantId, FileId fileId);

  StoredFileDetail findFileDetail(TenantId tenantId, StoredFile storedFile);

  List<StoredFileDetail> findFileDetailByIds(TenantId tenantId, Collection<FileId> fileIds);

  File findLocalFile(TenantId tenantId, LocalFileStoreInfo storeInfo);
}
