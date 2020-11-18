package wx.api.rest.account.user;

import lombok.Data;

@Data
public class UserQueryRequest {

  private String tenantId;

  private String searchText;

  private Integer pageNum;

  private String roleId;

  private Integer pageSize;
}
