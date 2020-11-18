package wx.infra.validator;

import java.util.Objects;
import wx.common.data.shared.id.*;

public abstract class CrudObjectValidator<D> {

  public void validateDataImpl(TenantId tenantId, D data) {}

  public void validateCreate(TenantId tenantId, D data) {}

  public void validateUpdate(TenantId tenantId, D data) {}

  protected boolean isSameData(D existentData, D actualData) {
    return Objects.equals(existentData, actualData);
  }
}
