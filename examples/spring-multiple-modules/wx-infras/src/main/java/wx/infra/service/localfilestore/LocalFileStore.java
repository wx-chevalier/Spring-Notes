package wx.infra.service.localfilestore;

import java.io.File;
import wx.common.data.shared.id.*;

public interface LocalFileStore {

  LocalFileStoreSetting getLocalFileStoreSetting();

  /**
   * 根据文件 ID 获取文件路径，注意返回的文件可能不存在
   *
   * @param id 文件在 LocalFileStore 中的 id
   */
  File getById(TenantId tenantId, String id);

  /**
   * 指定 ID 的文件是否存在
   *
   * @param id 文件在 LocalFileStore 中的 id
   */
  Boolean exists(TenantId tenantId, String id);

  /**
   * @param file 要存储的文件
   * @return 文件在 LocalFileStore 中的 id
   */
  String saveFile(TenantId tenantId, File file);

  /**
   * 移除文件
   *
   * @param id 文件在 LocalFileStore 中的 id
   */
  void removeFile(TenantId tenantId, String id);
}
