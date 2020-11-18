package wx.infra.service.localfilestore;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wx.common.data.shared.id.*;
import wx.infra.common.util.FileUtils;

@Slf4j
@Service
public class LocalFileHashStore implements LocalFileStore {

  @Getter private LocalFileStoreSetting localFileStoreSetting;

  public LocalFileHashStore(LocalFileStoreSetting localFileStoreSetting) {
    this.localFileStoreSetting = localFileStoreSetting;
  }

  @Override
  public File getById(TenantId tenantId, String id) {
    return getFile(tenantId, id);
  }

  @Override
  public Boolean exists(TenantId tenantId, String id) {
    return getFile(tenantId, id).exists();
  }

  @SuppressWarnings("UnstableApiUsage")
  @Override
  public String saveFile(TenantId tenantId, File file) {
    log.info("Save file {} - {}", tenantId, file);
    String fileId = FileUtils.md5(file);
    File dstFile = getFile(tenantId, fileId);
    FileUtils.ensureDir(dstFile.getParentFile());
    try {
      Files.copy(file, dstFile);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return fileId;
  }

  @Override
  public void removeFile(TenantId tenantId, String id) {
    File file = getFile(tenantId, id);
    if (file.exists()) {
      FileUtils.deleteFile(file);
    }
  }

  private File getFile(TenantId tenantId, String id) {
    File tenantDir =
        new File(localFileStoreSetting.getFilePath(), "tenant-" + tenantId.getId().toString());
    String dir = id.substring(0, 2);
    String filename = id.substring(2);
    File fileDir = new File(tenantDir, dir);
    return new File(fileDir, filename);
  }
}
