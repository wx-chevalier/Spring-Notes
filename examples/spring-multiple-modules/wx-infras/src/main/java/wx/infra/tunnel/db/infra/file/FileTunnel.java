package wx.infra.tunnel.db.infra.file;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Set;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import wx.infra.tunnel.db.Helper;
import wx.infra.tunnel.db.mapper.infra.file.FileMapper;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class FileTunnel extends ServiceImpl<FileMapper, FileDO> {

  // 检查文件是否都存在
  // todo: remove this
  public Boolean exist(Set<Long> pictureIds) {
    if (CollectionUtils.isEmpty(pictureIds)) {
      return Boolean.TRUE;
    }

    Wrapper<FileDO> queryWrapper =
        Helper.getQueryWrapper(FileDO.class)
            .in(FileDO::getId, pictureIds)
            .isNull(FileDO::getDeletedAt);

    return this.baseMapper.selectCount(queryWrapper) == pictureIds.size();
  }
}
