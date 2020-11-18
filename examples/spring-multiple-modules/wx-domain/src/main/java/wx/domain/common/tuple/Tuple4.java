package wx.domain.common.tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Tuple4<A, B, C, D> {
  public final A _1;
  public final B _2;
  public final C _3;
  public final D _4;

  public static <A, B, C, D> Tuple4<A, B, C, D> from(final A a, final B b, final C c, final D d) {
    return new Tuple4<>(a, b, c, d);
  }

  public static <A, B, C, D> Tuple4<A, B, C, D> tuple(final A a, final B b, final C c, final D d) {
    return new Tuple4<>(a, b, c, d);
  }

  @SuppressWarnings("unchecked")
  <T> void forEach(final Consumer<? super T> consumer) {
    consumer.accept((T) _1);
    consumer.accept((T) _2);
    consumer.accept((T) _3);
    consumer.accept((T) _4);
  }

  @SuppressWarnings("unchecked")
  <TA, TB> Collection<TB> map(final Function<? super TA, ? super TB> function) {
    final List<TB> list = new ArrayList<>(4);

    list.add((TB) function.apply((TA) _1));
    list.add((TB) function.apply((TA) _2));
    list.add((TB) function.apply((TA) _3));
    list.add((TB) function.apply((TA) _4));

    return list;
  }

  private Tuple4(final A a, final B b, final C c, final D d) {
    this._1 = a;
    this._2 = b;
    this._3 = c;
    this._4 = d;
  }
}
