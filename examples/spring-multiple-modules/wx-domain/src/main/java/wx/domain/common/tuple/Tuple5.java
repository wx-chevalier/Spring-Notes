package wx.domain.common.tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Tuple5<A, B, C, D, E> {
  public final A _1;
  public final B _2;
  public final C _3;
  public final D _4;
  public final E _5;

  public static <A, B, C, D, E> Tuple5<A, B, C, D, E> from(
      final A a, final B b, final C c, final D d, final E e) {
    return new Tuple5<>(a, b, c, d, e);
  }

  public static <A, B, C, D, E> Tuple5<A, B, C, D, E> tuple(
      final A a, final B b, final C c, final D d, final E e) {
    return new Tuple5<>(a, b, c, d, e);
  }

  @SuppressWarnings("unchecked")
  <T> void forEach(final Consumer<? super T> consumer) {
    consumer.accept((T) _1);
    consumer.accept((T) _2);
    consumer.accept((T) _3);
    consumer.accept((T) _4);
    consumer.accept((T) _5);
  }

  @SuppressWarnings("unchecked")
  <TA, TB> Collection<TB> map(final Function<? super TA, ? super TB> function) {
    final List<TB> list = new ArrayList<>(5);

    list.add((TB) function.apply((TA) _1));
    list.add((TB) function.apply((TA) _2));
    list.add((TB) function.apply((TA) _3));
    list.add((TB) function.apply((TA) _4));
    list.add((TB) function.apply((TA) _5));

    return list;
  }

  private Tuple5(final A a, final B b, final C c, final D d, final E e) {
    this._1 = a;
    this._2 = b;
    this._3 = c;
    this._4 = d;
    this._5 = e;
  }
}
