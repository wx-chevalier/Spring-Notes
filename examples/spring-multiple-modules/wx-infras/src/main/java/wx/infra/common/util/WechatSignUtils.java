package wx.infra.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import wx.infra.service.wechat.WechatSetting;

@Slf4j
@Component
public class WechatSignUtils {

  private WechatSetting wechatSetting;

  public WechatSignUtils(WechatSetting wechatSetting) {
    this.wechatSetting = wechatSetting;
  }

  /**
   * 验证签名
   *
   * @param signature 微信加密签名
   * @param timestamp 时间戳
   * @param nonce 随机数
   * @return 签名校验结果
   */
  public boolean checkSignature(String signature, String timestamp, String nonce) {
    String token = wechatSetting.getToken();

    if (!StringUtils.hasText(signature)
        || !StringUtils.hasText(timestamp)
        || !StringUtils.hasText(nonce)
        || !StringUtils.hasText(token)) {
      return false;
    }

    String[] arr = new String[] {token, timestamp, nonce};
    // 将 token、timestamp、nonce 三个参数进行字典序排序
    Arrays.sort(arr);
    StringBuilder content = new StringBuilder();
    for (String s : arr) {
      content.append(s);
    }
    MessageDigest md;
    String tmpStr = null;

    try {
      md = MessageDigest.getInstance("SHA-1");
      // 将三个参数字符串拼接成一个字符串进行 sha1 加密
      byte[] digest = md.digest(content.toString().getBytes(StandardCharsets.UTF_8));
      tmpStr = byteToStr(digest);
    } catch (NoSuchAlgorithmException e) {
      log.error("计算SHA1出错", e);
    }
    // 将 sha1 加密后的字符串可与 signature 对比，标识该请求来源于微信
    return Objects.equals(tmpStr, signature.toUpperCase());
  }

  /** 将字节数组转换为十六进制字符串 */
  private static String byteToStr(byte[] byteArray) {
    StringBuilder strDigest = new StringBuilder();
    for (byte b : byteArray) {
      strDigest.append(byteToHexStr(b));
    }
    return strDigest.toString();
  }

  /** 将字节转换为十六进制字符串 */
  private static String byteToHexStr(byte mByte) {
    char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    char[] tempArr = new char[2];
    tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
    tempArr[1] = Digit[mByte & 0X0F];
    String s = new String(tempArr);
    return s;
  }
}
