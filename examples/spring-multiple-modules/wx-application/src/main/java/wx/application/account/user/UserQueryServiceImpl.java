package wx.application.account.user;

import static java.util.stream.Collectors.toMap;

import io.reactivex.rxjava3.core.Single;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Sets;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import wx.application.account.accesscontrol.UserConverter;
import wx.application.account.permission.PermissionDetail;
import wx.application.account.permission.PermissionQueryService;
import wx.application.account.role.RoleQueryService;
import wx.application.infra.filestore.StoredFileDetail;
import wx.application.infra.filestore.StoredFileQueryService;
import wx.application.page.PageNumberLinkConverter;
import wx.common.data.account.Authority;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.UserId;
import wx.domain.account.*;
import wx.domain.wechat.WechatUser;
import wx.domain.wechat.WechatUserInfoRepository;
import wx.infra.common.exception.NotFoundException;
import wx.infra.converter.PageConverter;
import wx.infra.tunnel.db.account.UserTunnel;

@Service
@Slf4j
public class UserQueryServiceImpl implements UserQueryService {

  private UserTunnel userTunnel;

  private UserRepository userRepository;

  private TenantRepository tenantRepository;

  private RoleQueryService roleQueryService;

  private PermissionQueryService permissionQueryService;

  private PageNumberLinkConverter pageNumberLinkConverter;

  private UserConverter userConverter;

  private StoredFileQueryService storedFileQueryService;

  private WechatUserInfoRepository wechatUserInfoRepository;

  public UserQueryServiceImpl(
      UserTunnel userTunnel,
      UserRepository userRepository,
      TenantRepository tenantRepository,
      RoleQueryService roleQueryService,
      WechatUserInfoRepository wechatUserInfoRepository,
      PermissionQueryService permissionQueryService,
      PageNumberLinkConverter pageNumberLinkConverter,
      UserConverter userConverter,
      StoredFileQueryService storedFileQueryService) {
    this.userTunnel = userTunnel;
    this.userRepository = userRepository;
    this.tenantRepository = tenantRepository;
    this.roleQueryService = roleQueryService;
    this.wechatUserInfoRepository = wechatUserInfoRepository;
    this.permissionQueryService = permissionQueryService;
    this.pageNumberLinkConverter = pageNumberLinkConverter;
    this.userConverter = userConverter;
    this.storedFileQueryService = storedFileQueryService;
  }

  @Override
  public Page<User> findUsers(UserQuery query) {
    log.info("findUsers {}", query);
    Pageable pageable = pageNumberLinkConverter.convertTo(query.getPageLink());
    return PageConverter.toPage(
        userTunnel
            .find(
                query.getTenantId(),
                query.getRoleId(),
                query.getSearchText(),
                Authority.TENANT_USER,
                pageable)
            .convert(userConverter::convertFrom),
        pageable);
  }

  @Override
  public Page<UserDetail> findUserDetails(UserQuery query) {
    Page<User> users = findUsers(query);
    return users.map(this::findUserDetail);
  }

  @Override
  public Single<List<UserDetail>> findUsers(TenantId tenantId) {
    return Single.defer(
        () ->
            Single.just(
                userRepository
                    .find(tenantId)
                    .parallelStream()
                    .map(this::findUserDetail)
                    .collect(Collectors.toList())));
  }

  @Override
  public Single<Map<UserId, UserDetail>> findUserDetailsByIds(
      TenantId tenantId, Collection<UserId> userIds) {
    return Single.defer(
        () ->
            Single.just(
                userIds
                    .parallelStream()
                    .map(userId -> findUserDetail(tenantId, userId))
                    .collect(toMap(UserDetail::getId, Function.identity()))));
  }

  @Override
  public UserDetail findUserDetail(TenantId tenantId, UserId userId) {
    log.info("find user detail {} {}", tenantId, userId);
    return userRepository
        .findById(tenantId, userId)
        .map(this::findUserDetail)
        .orElseThrow(() -> new NotFoundException("查询失败, 当前用户不存在"));
  }

  private UserDetail findUserDetail(User user) {
    TenantId tenantId = user.getTenantId();
    User creator = userRepository.findById(tenantId, user.getCreatorId()).orElse(null);
    Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(NotFoundException::new);
    StoredFileDetail storedFile =
        Optional.ofNullable(user.getAvatarFileId())
            .map(fileId -> storedFileQueryService.findFileDetail(tenantId, fileId))
            .flatMap(Function.identity())
            .orElse(null);

    // 查询对应的角色信息
    List<Role> roleList = roleQueryService.findRoleByUserId(tenantId, user.getId()).blockingGet();

    // 查询当前用户的权限
    List<PermissionDetail> permissionDetails =
        permissionQueryService.findByRoleIds(
            tenantId, roleList.stream().map(Role::getId).collect(Collectors.toSet()));
    // 角色权限 + 用户权限

    // 获取关联的微信用户信息
    WechatUser wechatUser = wechatUserInfoRepository.getByUserId(user.getId()).orElse(null);

    return new UserDetail(
        user, creator, tenant, storedFile, permissionDetails, roleList, wechatUser);
  }

  @Override
  @Transactional
  public Set<User> findTenantAdmin(Set<TenantId> tenantIdStream) {
    Set<Long> tenantIds = tenantIdStream.stream().map(TenantId::getId).collect(Collectors.toSet());
    return userTunnel.findTenantAdminUser(tenantIds).stream()
        .map(userConverter::convertFrom)
        .collect(Collectors.toSet());
  }

  @Override
  @Transactional
  public Optional<User> findTenantAdmin(TenantId tenantId) {
    Set<User> tenantAdminUsers = findTenantAdmin(Sets.newHashSet(tenantId));
    if (CollectionUtils.isEmpty(tenantAdminUsers)) {
      return Optional.empty();
    }
    for (User adminUser : tenantAdminUsers) {
      if (Objects.equals(adminUser.getTenantId(), tenantId)) {
        return Optional.of(adminUser);
      }
    }
    return Optional.empty();
  }
}
