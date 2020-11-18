package wx.api.rest.admin.tenant;

import lombok.Data;

@Data
public class AddTenantRequest {

  private String name;

  private String areaCode;

  private String tenantAdminUsername;

  private String tenantAdminUserPassword;
}
