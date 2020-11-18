package wx.common.data.infra.kv;

import lombok.Data;

@Data
public class AttributeKey {
  private final String scope;
  private final String key;
}
