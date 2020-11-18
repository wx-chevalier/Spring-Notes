package wx.infra;

import java.util.Collection;

public interface CustomList<E> {

  /** 返回集合的长度 */
  int size();

  /**
   * 返回集合是否为空
   *
   * @return 集合为空返回 true, 不为空返回false
   */
  boolean isEmpty();

  /**
   * 判断集合中是否包含对象
   *
   * @param o
   * @return 包含该对象返回true, 否则返回false
   */
  boolean contains(Object o);

  /** 将集合转换为数组 */
  E[] toArray();

  /** 检查集合中是够包含集合c中的元素 */
  boolean containsAll(Collection<?> c);

  /** 将集合c添加到集合的尾部 */
  boolean addAll(Collection<? extends E> c);

  void clear();

  /** 获取指定索引位置的数据 */
  E get(int index);

  /**
   * 将index的位置数据替换为 element
   *
   * <p>若index超出链表的长度，抛出异常
   *
   * @return 返回插入的新元素
   */
  E set(int index, E element);

  /** 新增元素到指定位置 */
  void add(int index, E element);

  /**
   * 移除指定索引的数据
   *
   * <p>若index超出链表的长度，抛出异常
   */
  E remove(int index);

  /** 截取子串，若前后索引超出范围，则抛出异常 */
  CustomList<E> subList(int fromIndex, int toIndex);
}
