package wx.common.data.infra.filestore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = OssFileStoreInfo.class, name = "OSS"),
  @JsonSubTypes.Type(value = LocalFileStoreInfo.class, name = "LOCAL"),
})
@NoArgsConstructor
public abstract class FileStoreInfo {
  public abstract FileStoreType getType();
}
