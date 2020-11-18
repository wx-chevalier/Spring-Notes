package wx.common.data.shared.id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import lombok.NonNull;

/** @author zhangzixiong */
public abstract class BaseEntityId implements EntityId {
  private final Long id;

  public BaseEntityId(@NonNull Long id) {
    this.id = id;
  }

  @Override
  public Long getId() {
    return id;
  }

  @JsonIgnore
  public String idStr() {
    return EntityId.getIdString(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseEntityId that = (BaseEntityId) o;
    return Objects.equals(getId(), that.getId()) && getEntityType() == that.getEntityType();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getEntityType());
  }

  @Override
  public String toString() {
    return getEntityType() + "{" + "id=" + id + '}';
  }
}
