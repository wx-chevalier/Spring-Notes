package wx.infra.validator;

public abstract class DataValidator<T> {

  public T validate(T t) {
    return t;
  }
}
