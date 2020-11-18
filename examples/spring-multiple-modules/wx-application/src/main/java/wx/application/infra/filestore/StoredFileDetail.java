package wx.application.infra.filestore;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import wx.domain.infra.filestore.StoredFile;

@Data
@EqualsAndHashCode(callSuper = true)
public class StoredFileDetail extends StoredFile {

  private String url;

  private ObjectNode attr;

  public StoredFileDetail(StoredFile storedFile, String url, ObjectNode attr) {
    super();
    BeanUtils.copyProperties(storedFile, this);
    this.url = url;
    this.attr = attr;
  }
}
