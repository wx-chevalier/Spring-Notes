package wx.infra;

import java.util.Collection;

public class CustomListImpl<String> implements CustomList<String> {
  @Override
  public int size() {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean contains(Object o) {
    return false;
  }

  @Override
  public String[] toArray() {
    return null;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return false;
  }

  @Override
  public boolean addAll(Collection<? extends String> c) {
    return false;
  }

  @Override
  public void clear() {}

  @Override
  public String get(int index) {
    return null;
  }

  @Override
  public String set(int index, String element) {
    return null;
  }

  @Override
  public void add(int index, String element) {}

  @Override
  public String remove(int index) {
    return null;
  }

  @Override
  public CustomList<String> subList(int fromIndex, int toIndex) {
    return null;
  }
}
