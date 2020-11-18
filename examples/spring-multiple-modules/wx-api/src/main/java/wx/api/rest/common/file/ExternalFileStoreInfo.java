package wx.api.rest.common.file;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import wx.api.rest.common.file.ExternalFileStoreInfo.GatewayFileStoreInfo;
import wx.api.rest.common.file.ExternalFileStoreInfo.OssFileStoreInfo;
import wx.common.data.infra.filestore.FileStoreType;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = GatewayFileStoreInfo.class, name = "GATEWAY"),
  @JsonSubTypes.Type(value = OssFileStoreInfo.class, name = "OSS"),
})
@NoArgsConstructor
public abstract class ExternalFileStoreInfo {
  public abstract String getType();

  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class OssFileStoreInfo extends ExternalFileStoreInfo {

    private String name;

    // Ali OSS 存储 key
    @NotBlank private String key;

    @Override
    public String getType() {
      return FileStoreType.OSS.name();
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class GatewayFileStoreInfo extends ExternalFileStoreInfo {

    @NotBlank private String name;

    @NotBlank private Long size;

    @NotBlank private String md5;

    @NotBlank private String gatewayFileId;

    @NotBlank private String gatewayId;

    @Override
    public String getType() {
      return FileStoreType.GATEWAY.name();
    }
  }
}
