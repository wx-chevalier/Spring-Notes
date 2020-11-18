package wx.common.data.infra.filestore;

import wx.common.data.common.AbstractJsonSerializer;

public class FileInfoSerializer extends AbstractJsonSerializer<FileStoreInfo> {
  private static FileInfoSerializer fileInfoSerializer;

  private FileInfoSerializer() {
    super(FileStoreInfo.class);
  }

  public static synchronized FileInfoSerializer instance() {
    if (fileInfoSerializer == null) {
      fileInfoSerializer = new FileInfoSerializer();
    }
    return fileInfoSerializer;
  }

  @Override
  public FileStoreInfo fromJson(String jsonString) {
    return super.fromJson(jsonString);
  }

  @Override
  public String toJson(FileStoreInfo fileStoreInfo) {
    return super.toJson(fileStoreInfo);
  }
}
