package wx.api.rest.shared;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import wx.api.config.properties.ApplicationProperties;
import wx.api.config.security.model.SecurityUser;
import wx.application.account.accesscontrol.AccessControlQueryService;
import wx.application.account.user.TenantQueryService;
import wx.application.account.user.UserQueryService;
import wx.application.admin.ApplicationQueryService;
import wx.application.admin.PermissionSettingQueryService;
import wx.application.admin.config.AdminConfigQueryService;
import wx.application.infra.area.AreaQueryService;
import wx.application.infra.filestore.StoredFileQueryService;
import wx.application.infra.message.MessageTypeQueryService;
import wx.application.infra.message.NotifyService;
import wx.application.infra.message.RoleMessageConfigQueryService;
import wx.application.infra.notice.email.EmailService;
import wx.application.infra.notice.site.SiteMessageService;
import wx.application.infra.notice.sms.SmsService;
import wx.common.data.shared.id.TenantId;
import wx.common.data.shared.id.UserId;
import wx.domain.account.AccessControlCommandService;
import wx.domain.account.UserCommandService;
import wx.domain.infra.filestore.StoredFileCommandService;
import wx.domain.infra.message.RoleMessageConfigCommandService;
import wx.domain.infra.verificationcode.VerificationCodeCommandService;
import wx.infra.common.exception.UnAuthorizedException;

@Slf4j
public class BaseController {

  @Autowired protected UserCommandService userCommandService;

  @Autowired protected UserQueryService userQueryService;

  @Autowired protected VerificationCodeCommandService verificationCodeCommandService;

  @Autowired protected StoredFileCommandService storedFileCommandService;

  @Autowired protected StoredFileQueryService storedFileQueryService;

  // ===============================通知相关=======================================

  @Autowired protected MessageTypeQueryService messageTypeQueryService;

  @Autowired protected SmsService smsService;

  @Autowired protected EmailService emailService;

  @Autowired protected SiteMessageService siteMessageService;

  @Autowired protected NotifyService notifyService;

  @Autowired protected RoleMessageConfigCommandService roleMessageConfigCommandService;

  @Autowired protected RoleMessageConfigQueryService roleMessageConfigQueryService;

  // ===============================权限相关=======================================

  @Autowired protected PermissionSettingQueryService permissionSettingQueryService;

  @Autowired protected AccessControlCommandService accessControlCommandService;

  @Autowired protected AccessControlQueryService accessControlQueryService;

  // ===============================应用相关=======================================

  @Autowired protected ApplicationQueryService applicationQueryService;

  @Autowired protected ApplicationProperties applicationProperties;

  // ===============================管理员配置=======================================

  @Autowired protected AdminConfigQueryService adminConfigQueryService;

  @Autowired protected TenantQueryService tenantQueryService;

  // ===============================通用相关=======================================

  @Autowired protected AreaQueryService areaQueryService;

  protected Optional<SecurityUser> tryGetCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
      return Optional.of((SecurityUser) authentication.getPrincipal());
    } else {
      throw new UnAuthorizedException();
    }
  }

  protected SecurityUser getCurrentUser() {
    return tryGetCurrentUser().orElseThrow(UnAuthorizedException::new);
  }

  protected UserId getCurrentUserId() {
    return getCurrentUser().getId();
  }

  protected TenantId getTenantId() {
    return getCurrentUser().getTenantId();
  }
}
