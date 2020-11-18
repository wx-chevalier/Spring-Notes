package wx.application.admin.config;

import lombok.Data;
import wx.application.infra.filestore.StoredFileDetail;

@Data
public class FileTemplateConfig {

  // 批量配置文件模板信息
  private StoredFileDetail utkPrinterBatchFile;
}
