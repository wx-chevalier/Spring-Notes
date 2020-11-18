package wx.infra.tunnel.db.mapper.account;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;
import wx.infra.tunnel.db.account.UserCredentialsDO;

@Component
public interface UserCredentialsMapper extends BaseMapper<UserCredentialsDO> {}
