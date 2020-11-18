package wx.constants;

import java.nio.charset.Charset;

public class Misc {
  public static final Charset DEFAULT_CHARSET;

  static {
    DEFAULT_CHARSET = Charset.forName("utf8");
  }
}
