package wx.common.data.common;

import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractConverter<T, S> {

  protected AbstractConverter() {}

  public S convertTo(T t) {
    throw new UnsupportedOperationException("Not implemented");
  }

  public T convertFrom(S s) {
    throw new UnsupportedOperationException("Not implemented");
  }

  protected <A, B> B convertNullable(A t, Function<A, B> mapper) {
    return Optional.ofNullable(t).map(mapper).orElse(null);
  }
}
