package wx.domain.common.tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Tuple3<A, B, C> {
  public final A _1;
  public final B _2;
  public final C _3;

  public static <A, B, C> Tuple3<A, B, C> from(final A a, final B b, final C c) {
    return new Tuple3<>(a, b, c);
  }

  public static <A, B, C> Tuple3<A, B, C> tuple(final A a, final B b, final C c) {
    return new Tuple3<>(a, b, c);
  }

  @SuppressWarnings("unchecked")
  <T> void forEach(final Consumer<? super T> consumer) {
    consumer.accept((T) _1);
    consumer.accept((T) _2);
    consumer.accept((T) _3);
  }

  @SuppressWarnings("unchecked")
  <TA, TB> Collection<TB> map(final Function<? super TA, ? super TB> function) {
    final List<TB> list = new ArrayList<>(3);

    list.add((TB) function.apply((TA) _1));
    list.add((TB) function.apply((TA) _2));
    list.add((TB) function.apply((TA) _3));

    return list;
  }

  private Tuple3(final A a, final B b, final C c) {
    this._1 = a;
    this._2 = b;
    this._3 = c;
  }
}
