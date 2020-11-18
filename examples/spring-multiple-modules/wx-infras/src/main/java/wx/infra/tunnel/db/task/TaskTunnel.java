package wx.infra.tunnel.db.task;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import wx.infra.tunnel.db.mapper.task.TaskMapper;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TaskTunnel extends ServiceImpl<TaskMapper, TaskDO> {}
