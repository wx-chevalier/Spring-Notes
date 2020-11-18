package wx.common.data.common.process;

import lombok.Getter;

public enum TaskStatus {
  CREATED(false),
  RUNNING(false),
  COMPLETED_NORMALLY(true),
  COMPLETED_EXCEPTIONALLY(true),
  TIMEOUT(true);

  @Getter private boolean isCompletionStatus;

  TaskStatus(boolean isCompletionStatus) {
    this.isCompletionStatus = isCompletionStatus;
  }
}
