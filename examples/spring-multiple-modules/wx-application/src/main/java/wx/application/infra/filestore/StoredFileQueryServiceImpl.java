package wx.application.infra.filestore;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wx.common.data.infra.filestore.FileStoreType;
import wx.common.data.infra.filestore.LocalFileStoreInfo;
import wx.common.data.infra.filestore.OssFileStoreInfo;
import wx.common.data.infra.filestore.PreSignedUrl;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.EntityId;
import wx.common.data.shared.id.FileId;
import wx.domain.infra.fileformat.StoredFileAttributeRepository;
import wx.domain.infra.filestore.StoredFile;
import wx.domain.infra.filestore.StoredFileCommandService;
import wx.domain.infra.filestore.StoredFileRepository;
import wx.infra.service.localfilestore.LocalFileStore;
import wx.infra.service.localfilestore.LocalFileStoreSetting;

@Service
public class StoredFileQueryServiceImpl implements StoredFileQueryService {

  private StoredFileCommandService storedFileCommandService;
  private StoredFileRepository storedFileRepository;
  private StoredFileAttributeRepository storedFileAttributeRepository;
  private LocalFileStore localFileStore;
  private LocalFileStoreSetting fileStoreConfig;

  public StoredFileQueryServiceImpl(
      StoredFileCommandService storedFileCommandService,
      StoredFileRepository storedFileRepository,
      StoredFileAttributeRepository storedFileAttributeRepository,
      LocalFileStore localFileStore,
      LocalFileStoreSetting fileStoreConfig) {
    this.storedFileCommandService = storedFileCommandService;
    this.storedFileRepository = storedFileRepository;
    this.storedFileAttributeRepository = storedFileAttributeRepository;
    this.localFileStore = localFileStore;
    this.fileStoreConfig = fileStoreConfig;
  }

  @Override
  public StoredFileDetail findFileDetail(StoredFile storedFile) {
    TenantId tenantId = storedFile.getTenantId();
    String url = retrieveFileUrl(tenantId, storedFile);
    ObjectNode attr = storedFileAttributeRepository.find(tenantId, storedFile.getId());
    return new StoredFileDetail(storedFile, url, attr);
  }

  @Override
  public Optional<StoredFileDetail> findFileDetail(TenantId tenantId, FileId fileId) {
    return storedFileRepository
        .findById(tenantId, fileId)
        .map(storedFile -> findFileDetail(tenantId, storedFile));
  }

  @Override
  public StoredFileDetail findFileDetail(TenantId tenantId, StoredFile storedFile) {
    String url = retrieveFileUrl(tenantId, storedFile);
    ObjectNode attr = storedFileAttributeRepository.find(tenantId, storedFile.getId());
    return new StoredFileDetail(storedFile, url, attr);
  }

  @Override
  public List<StoredFileDetail> findFileDetailByIds(
      final TenantId tenantId, Collection<FileId> fileIds) {
    // 查询所有的文件
    return fileIds.stream()
        .parallel()
        .map(fileId -> findFileDetail(tenantId, fileId).orElse(null))
        .collect(Collectors.toList());
  }

  @Override
  public File findLocalFile(TenantId tenantId, LocalFileStoreInfo storeInfo) {
    return localFileStore.getById(tenantId, storeInfo.getLocalFileId());
  }

  private String retrieveFileUrl(TenantId tenantId, StoredFile storedFile) {
    if (storedFile.getStoreInfo() == null) {
      return null;
    }
    if (storedFile.getStoreInfo().getType() == FileStoreType.OSS) {
      PreSignedUrl preSignedUrl = storedFileCommandService.getPreSignedUrl(tenantId, storedFile);
      ((OssFileStoreInfo) storedFile.getStoreInfo()).setPreSignedUrl(preSignedUrl);
      return preSignedUrl.getUrl();
    } else if (storedFile.getStoreInfo().getType() == FileStoreType.LOCAL) {
      return fileStoreConfig.getBaseUrl() + "/" + EntityId.getIdString(storedFile.getId());
    } else {
      return null;
    }
  }
}
