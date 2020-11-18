package wx.domain.account;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import wx.common.data.account.Authority;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.FileId;
import wx.common.data.shared.id.UserId;
import wx.domain.shared.IdBasedEntity;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends IdBasedEntity<UserId, User> {

  private String username;

  private Authority authority;

  private String nickname;

  private String phoneNumber;

  private String email;

  private String remark;

  private FileId avatarFileId;

  private UserId creatorId;

  public User(
      UserId id,
      TenantId tenantId,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      String username,
      Authority authority,
      String nickname,
      String phoneNumber,
      String email,
      String remark,
      FileId avatarFileId,
      UserId creatorId) {
    super(id, tenantId, createdAt, updatedAt);
    this.username = username;
    this.authority = authority;
    this.nickname = nickname;
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.remark = remark;
    this.avatarFileId = avatarFileId;
    this.creatorId = creatorId;
  }

  public User(
      TenantId tenantId,
      String username,
      Authority authority,
      String nickname,
      String phoneNumber,
      String email,
      String remark,
      FileId avatarFileId,
      UserId creatorId) {
    super(tenantId);
    this.username = username;
    this.authority = authority;
    this.nickname = nickname;
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.remark = remark;
    this.avatarFileId = avatarFileId;
    this.creatorId = creatorId;
  }

  public User(String username, Authority authority, String nickname, UserId creatorId) {
    this.username = username;
    this.authority = authority;
    this.nickname = nickname;
    this.creatorId = creatorId;
  }
}
