package wx.application.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import wx.common.data.shared.id.*;
import wx.domain.account.PermissionRepository;
import wx.domain.admin.PermissionSetting;
import wx.domain.admin.PermissionSettingRepository;

@Slf4j
@Service
public class PermissionSettingQueryServiceImpl implements PermissionSettingQueryService {

  private PermissionSettingRepository permissionSettingRepository;

  private PermissionRepository permissionRepository;

  public PermissionSettingQueryServiceImpl(
      PermissionSettingRepository permissionSettingRepository,
      PermissionRepository permissionRepository) {
    this.permissionSettingRepository = permissionSettingRepository;
    this.permissionRepository = permissionRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<PermissionSettingDetail> findByAppId(TenantId tenantId, ApplicationId appId) {

    List<PermissionSetting> permissionSettings =
        permissionSettingRepository.findByAppId(tenantId, appId);
    if (CollectionUtils.isEmpty(permissionSettings)) {
      return new ArrayList<>(0);
    }

    Set<String> permissionNameSet =
        permissionSettings.stream()
            .map(PermissionSetting::getPermissionName)
            .collect(Collectors.toSet());
    Map<String, String> permissionNickNameMap =
        permissionRepository.findByNames(permissionNameSet).entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getNickname()));

    return permissionSettings.stream()
        .map(
            permissionSetting -> {
              String nickname = permissionNickNameMap.get(permissionSetting.getPermissionName());
              return new PermissionSettingDetail(permissionSetting, nickname);
            })
        .collect(Collectors.toList());
  }
}
