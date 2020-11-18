package wx.domain.task;

import java.io.Serializable;
import java.time.ZonedDateTime;
import lombok.Getter;
import wx.common.data.common.process.TaskStatus;
import wx.common.data.shared.id.TaskId;
import wx.domain.shared.IdBasedEntity;

@Getter
@SuppressWarnings("unchecked")
public abstract class AbstractTask<E extends AbstractTask<E>> extends IdBasedEntity<TaskId, E>
    implements Serializable {

  private ZonedDateTime willAbortAt;

  private ZonedDateTime startTime;

  private ZonedDateTime finishTime;

  private Short progress = 0;

  private TaskStatus status = TaskStatus.CREATED;

  public E setWillAbortAt(ZonedDateTime willAbortAt) {
    this.willAbortAt = willAbortAt;
    return (E) this;
  }

  public E setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
    return (E) this;
  }

  public E setFinishTime(ZonedDateTime finishTime) {
    this.finishTime = finishTime;
    return (E) this;
  }

  public E setProgress(Short progress) {
    this.progress = progress;
    return (E) this;
  }

  public E setStatus(TaskStatus status) {
    this.status = status;
    return (E) this;
  }

  /**
   * 任务是否应该被终止，如果设定了 {@link #willAbortAt}，在该时间之后未完成，需要被终止。
   *
   * @return Boolean
   */
  public boolean shouldAbort() {
    return !status.isCompletionStatus()
        && willAbortAt != null
        && ZonedDateTime.now().isBefore(willAbortAt);
  }
}
