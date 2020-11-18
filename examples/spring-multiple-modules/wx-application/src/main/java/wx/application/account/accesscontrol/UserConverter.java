package wx.application.account.accesscontrol;

import java.util.Optional;
import org.springframework.stereotype.Component;
import wx.common.data.common.AbstractConverter;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.FileId;
import wx.common.data.shared.id.UserId;
import wx.domain.account.User;
import wx.infra.tunnel.db.account.UserDO;

@Component
public class UserConverter extends AbstractConverter<User, UserDO> {

  @Override
  public UserDO convertTo(User user) {
    return new UserDO()
        .setId(Optional.ofNullable(user.getId()).map(UserId::getId).orElse(null))
        .setTenantId(user.getTenantId().getId())
        .setUsername(user.getUsername())
        .setNickName(user.getNickname())
        .setPhoneNumber(user.getPhoneNumber())
        .setEmail(user.getEmail())
        .setAuthority(user.getAuthority())
        .setCreatorId(user.getCreatorId().getId())
        .setAvatarFileId(
            Optional.ofNullable(user.getAvatarFileId()).map(FileId::getId).orElse(null));
  }

  @Override
  public User convertFrom(UserDO userDO) {
    return new User(
        new UserId(userDO.getId()),
        new TenantId(userDO.getTenantId()),
        userDO.getCreatedAt(),
        userDO.getUpdatedAt(),
        userDO.getUsername(),
        userDO.getAuthority(),
        userDO.getNickName(),
        userDO.getPhoneNumber(),
        userDO.getEmail(),
        userDO.getRemark(),
        Optional.ofNullable(userDO.getAvatarFileId()).map(FileId::new).orElse(null),
        new UserId(userDO.getCreatorId()));
  }
}
