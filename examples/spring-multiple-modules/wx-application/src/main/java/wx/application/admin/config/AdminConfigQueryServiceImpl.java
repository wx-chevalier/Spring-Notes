package wx.application.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wx.application.infra.filestore.StoredFileDetail;
import wx.application.infra.filestore.StoredFileQueryService;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.FileId;
import wx.infra.common.exception.NotFoundException;

@Slf4j
@Service
public class AdminConfigQueryServiceImpl implements AdminConfigQueryService {

  private StoredFileQueryService storedFileQueryService;

  public AdminConfigQueryServiceImpl(StoredFileQueryService storedFileQueryService) {
    this.storedFileQueryService = storedFileQueryService;
  }

  @Override
  public FileTemplateConfig getTemplateConfig(TenantId tenantId) {
    // TODO : 设备模板应通过 租户ID获取
    FileId batchConfigDeviceFileId = FileId.create("493");

    StoredFileDetail batchConfigDeviceFile =
        storedFileQueryService
            .findFileDetail(TenantId.NULL_TENANT_ID, batchConfigDeviceFileId)
            .orElseThrow(() -> new NotFoundException("批量配置模板文件不存在"));

    return new FileTemplateConfig().setUtkPrinterBatchFile(batchConfigDeviceFile);
  }
}
