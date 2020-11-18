package wx.domain.task;

import java.util.Collection;
import wx.common.data.shared.id.TaskId;
import wx.domain.shared.IdBasedEntityRepository;

public interface TaskRepository<T extends AbstractTask<T>>
    extends IdBasedEntityRepository<TaskId, T> {

  Collection<T> findByType(TaskType taskType);
}
