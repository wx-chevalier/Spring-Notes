package wx.infra.validator;

import java.util.Objects;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import wx.common.data.shared.HasId;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.EntityId;
import wx.infra.common.exception.DataValidationException;

@Slf4j
public abstract class EntityValidator<D extends HasId<Id>, Id extends EntityId> {

  public void validate(D data, Function<D, TenantId> tenantIdFunction) {
    log.debug("validate data: {}", data);
    try {
      if (data == null) {
        throw new DataValidationException("Data object can't be null!");
      }
      TenantId tenantId = tenantIdFunction.apply(data);
      validateDataImpl(tenantId, data);
      if (data.getId() == null) {
        validateCreate(tenantId, data);
      } else {
        validateUpdate(tenantId, data);
      }
    } catch (DataValidationException e) {
      log.error("Data object is invalid: [{}]", e.getMessage());
      throw e;
    }
  }

  protected void validateDataImpl(TenantId tenantId, D data) {}

  protected void validateCreate(TenantId tenantId, D data) {}

  protected void validateUpdate(TenantId tenantId, D data) {}

  protected boolean isSameData(D existentData, D actualData) {
    return existentData != null
        && actualData != null
        && Objects.equals(existentData.getId(), actualData.getId());
  }
}
