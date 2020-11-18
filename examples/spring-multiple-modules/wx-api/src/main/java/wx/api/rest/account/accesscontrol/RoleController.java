package wx.api.rest.account.accesscontrol;

import static wx.api.rest.shared.dto.envelope.ResponseEnvelope.createOk;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import wx.api.rest.shared.BaseController;
import wx.api.rest.shared.dto.envelope.ResponseEnvelope;
import wx.application.account.role.RoleDetail;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.RoleId;
import wx.common.data.shared.id.UserId;
import wx.domain.account.Permission;
import wx.domain.account.Role;

@RestController
@RequestMapping("/role")
@Api(tags = "🎎 角色相关接口")
public class RoleController extends BaseController {

  @PostMapping
  @ApiOperation(value = " 新增或更新角色")
  public ResponseEnvelope<Role> saveOrUpdateRole(@RequestBody RoleAddRequest req) {
    Role role = req.toRole();
    if (req.getTenantId() == null) {
      role.setTenantId(getTenantId());
    }
    role.setCreatorId(getCurrentUserId());
    return createOk(accessControlCommandService.addRole(role));
  }

  @GetMapping
  @ApiOperation(value = " 获取当前租户下的所有的角色")
  public ResponseEnvelope<List<RoleDetail>> getAllRoles() {
    return createOk(accessControlQueryService.findRoles(getTenantId()));
  }

  @PatchMapping
  @ApiOperation(value = "更新角色信息")
  public ResponseEnvelope<Role> updateRole(RoleUpdateRequest req) {
    TenantId tenantId = getTenantId();
    Role role = req.toRole().setId(RoleId.create(req.getId())).setTenantId(tenantId);
    return createOk(accessControlCommandService.addRole(role));
  }

  @DeleteMapping("/{roleName}")
  @ApiOperation(value = "删除角色")
  public ResponseEnvelope<Void> deleteRole(@PathVariable("roleName") String roleName) {
    accessControlCommandService.removeRole(getTenantId(), roleName);
    return createOk();
  }

  @PostMapping("/{roleName}/permission")
  @ApiOperation(value = " 为指定角色添加权限")
  public ResponseEnvelope<Void> updateRolePermissionRelation(
      @PathVariable("roleName") String roleName, @RequestBody @Valid Set<String> permissionNames) {
    TenantId tenantId = getTenantId();
    UserId currentUserId = getCurrentUserId();
    accessControlCommandService.addRolePermissions(
        tenantId,
        currentUserId,
        new Role(roleName, tenantId),
        permissionNames.stream()
            .map(name -> new Permission(tenantId, name))
            .collect(Collectors.toList()));
    return createOk();
  }

  @DeleteMapping("/{roleName}/permission")
  @ApiOperation(value = " 为指定角色移除权限")
  public ResponseEnvelope<Void> removeRolePermissionRelation(
      @PathVariable("roleName") String roleName, @RequestBody @Valid Set<String> permissionNames) {
    TenantId tenantId = getTenantId();
    accessControlCommandService.removeRolePermissions(
        tenantId,
        new Role(roleName, tenantId),
        permissionNames.stream()
            .map(name -> new Permission(tenantId, name))
            .collect(Collectors.toList()));
    return createOk();
  }
}
