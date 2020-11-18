package wx.infra.tunnel.db.infra.file;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import wx.infra.tunnel.db.mapper.infra.file.FileAttrMapper;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class FileAttrTunnel extends ServiceImpl<FileAttrMapper, FileAttrDO> {}
