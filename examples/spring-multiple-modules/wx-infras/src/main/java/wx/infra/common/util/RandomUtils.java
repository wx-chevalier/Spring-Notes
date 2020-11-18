package wx.infra.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class RandomUtils {

  private static final Random rand = new Random(System.currentTimeMillis());

  /** 注意此处移除1，i，I o o O等易混淆的符号 */
  private static final String VERIFY_CODES = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

  public static String randomCode(int size) {
    int codesLen = VERIFY_CODES.length();
    StringBuilder verifyCode = new StringBuilder(size);
    for (int i = 0; i < size; i++) {
      verifyCode.append(VERIFY_CODES.charAt(rand.nextInt(codesLen - 1)));
    }
    return verifyCode.toString();
  }

  public static double randomRate() {
    // 生成随机比例
    double v = rand.nextDouble();
    // 随机正负号
    int prefixFlag = rand.nextInt() % 3 == 0 ? -1 : 1;
    BigDecimal b = new BigDecimal(prefixFlag * v);
    return b.setScale(2, RoundingMode.HALF_UP).doubleValue();
  }

  public static Long randomLong() {
    return rand.nextLong();
  }

  public static int randomInt(int bound) {
    return rand.nextInt(bound);
  }

  public static int randomInt() {
    return rand.nextInt();
  }

  public static Long randomLong(Integer start, Integer endExclusive) {
    return start + randomLong() % (endExclusive - start);
  }
}
