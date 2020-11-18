package wx.domain.infra.fileformat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Optional;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.FileId;

public interface StoredFileAttributeRepository {

  ObjectNode save(TenantId tenantId, FileId fileId, ObjectNode attrs);

  ObjectNode find(TenantId tenantId, FileId fileId);

  Optional<JsonNode> findAttr(TenantId tenantId, FileId fileId, String attrName);
}
