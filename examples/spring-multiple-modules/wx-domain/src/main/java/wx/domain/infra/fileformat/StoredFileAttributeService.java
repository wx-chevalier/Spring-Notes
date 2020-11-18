package wx.domain.infra.fileformat;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.reactivex.rxjava3.core.Single;
import java.io.File;
import wx.common.data.infra.filestore.FileFormat;
import wx.common.data.infra.filestore.OssFileStoreInfo;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.FileId;

public interface StoredFileAttributeService {

  ObjectNode setFileAttributes(TenantId tenantId, FileId fileId, ObjectNode attributes);

  ObjectNode parseAndSaveFileAttributes(TenantId tenantId, FileId fileId, File file);

  ObjectNode parseAndSaveFileAttributes(
      TenantId tenantId, FileId fileId, File file, FileFormat fileFormat);

  Single<ObjectNode> parseAndSaveFileAttributes(
      TenantId tenantId, FileId fileId, FileFormat fileFormat);

  Single<ObjectNode> parseAndSaveFileAttributes(
      TenantId tenantId, FileId fileId, OssFileStoreInfo ossFileStoreInfo, FileFormat fileFormat);
}
