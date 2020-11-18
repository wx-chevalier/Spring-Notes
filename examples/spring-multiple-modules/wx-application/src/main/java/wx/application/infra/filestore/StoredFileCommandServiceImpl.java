package wx.application.infra.filestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wx.common.data.infra.filestore.FileStoreType;
import wx.common.data.infra.filestore.LocalFileStoreInfo;
import wx.common.data.infra.filestore.OssFileStoreInfo;
import wx.common.data.infra.filestore.PreSignedUrl;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.FileId;
import wx.domain.infra.fileformat.StoredFileAttributeService;
import wx.domain.infra.filestore.StoredFile;
import wx.domain.infra.filestore.StoredFileCommandService;
import wx.domain.infra.filestore.StoredFileRepository;
import wx.infra.common.exception.BadRequestException;
import wx.infra.common.util.FileUtils;
import wx.infra.service.aliyun.AliOssService;
import wx.infra.service.localfilestore.LocalFileStore;

@Slf4j
@Service
public class StoredFileCommandServiceImpl implements StoredFileCommandService {

  private static final int BUFFER_SIZE = 2 * 1024;

  private LocalFileStore localFileStore;
  private StoredFileRepository storedFileRepository;
  private StoredFileAttributeService storedFileAttributeService;
  private AliOssService aliOssService;

  public StoredFileCommandServiceImpl(
      LocalFileStore localFileStore,
      StoredFileRepository storedFileRepository,
      StoredFileAttributeService storedFileAttributeService,
      AliOssService aliOssService) {
    this.localFileStore = localFileStore;
    this.storedFileRepository = storedFileRepository;
    this.storedFileAttributeService = storedFileAttributeService;
    this.aliOssService = aliOssService;
  }

  @Override
  public StoredFile saveToLocalStore(TenantId tenantId, String fileName, File file) {
    String localFileId = localFileStore.saveFile(tenantId, file);
    String fileMd5 = FileUtils.md5(file);
    StoredFile storedFile =
        storedFileRepository.save(
            tenantId,
            new StoredFile(
                tenantId,
                fileName,
                fileMd5,
                file.length(),
                FileStoreType.LOCAL,
                new LocalFileStoreInfo().setLocalFileId(localFileId)));
    storedFileAttributeService.parseAndSaveFileAttributes(tenantId, storedFile.getId(), file);
    return storedFile;
  }

  @Override
  public StoredFile saveOssFile(TenantId tenantId, String fileName, String ossFileKey) {
    return storedFileRepository.save(
        tenantId,
        new StoredFile(
            tenantId,
            fileName,
            null,
            null,
            FileStoreType.OSS,
            new OssFileStoreInfo()
                .setBucketName(aliOssService.getAliOssSetting().getBucketName())
                .setKey(ossFileKey)));
  }

  @Override
  @Transactional
  public PreSignedUrl getPreSignedUrl(TenantId tenantId, StoredFile storedFile) {
    if (storedFile.getStoreInfo() instanceof OssFileStoreInfo) {
      OssFileStoreInfo storeInfo = (OssFileStoreInfo) storedFile.getStoreInfo();
      PreSignedUrl url = storeInfo.getPreSignedUrl();
      if (url != null && url.getExpiresAt().isAfter(LocalDateTime.now())) {
        return url;
      } else {
        PreSignedUrl preSignedUrl =
            aliOssService.generatePreSignedUrl(
                storeInfo.getBucketName(),
                // work around
                storeInfo.getKey() == null ? storeInfo.getUrl() : storeInfo.getKey(),
                LocalDateTime.now().plusDays(1));
        storeInfo.setPreSignedUrl(preSignedUrl);
        storedFileRepository.save(tenantId, storedFile);
        return preSignedUrl;
      }
    } else {
      throw new BadRequestException(
          "Can't retrieve presigned url for this store type: " + storedFile);
    }
  }

  @Override
  public void removeByIds(TenantId tenantId, Collection<FileId> fileIds) {
    fileIds.forEach(fileId -> storedFileRepository.removeById(tenantId, fileId));
  }

  @Override
  public StoredFile compressFiles(TenantId tenantId, String name, List<FileId> fileIds)
      throws IOException {
    Collection<StoredFile> storedFiles =
        storedFileRepository.findByIds(tenantId, fileIds).blockingGet();
    storedFiles.forEach(
        file -> {
          if (file.getStoreInfo().getType() != FileStoreType.LOCAL) {
            // TODO: 其它存储文件的压缩
            throw new BadRequestException("Supports only local stored file");
          }
        });

    File tempFile = Files.createTempFile(name, "tmp").toFile();

    byte[] buf = new byte[BUFFER_SIZE];
    int readSize;

    try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(tempFile))) {
      for (StoredFile srcFile : storedFiles) {
        LocalFileStoreInfo storeInfo = (LocalFileStoreInfo) srcFile.getStoreInfo();
        File file = localFileStore.getById(tenantId, storeInfo.getLocalFileId());
        if (!file.exists()) {
          log.warn("File not found " + srcFile);
          continue;
        }
        zos.putNextEntry(new ZipEntry(srcFile.getName()));
        try (FileInputStream in = new FileInputStream(file)) {
          while ((readSize = in.read(buf)) != -1) {
            zos.write(buf, 0, readSize);
          }
          zos.closeEntry();
        }
      }
    }

    String localFileId = localFileStore.saveFile(tenantId, tempFile);
    String md5 = FileUtils.md5(tempFile);
    return storedFileRepository.save(
        tenantId,
        new StoredFile(
            tenantId,
            name,
            md5,
            tempFile.length(),
            FileStoreType.LOCAL,
            new LocalFileStoreInfo().setLocalFileId(localFileId)));
  }
}
