package wx.infra.common.util;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.constraints.NotNull;
import org.springframework.util.CollectionUtils;

public class StringUtil {
  private StringUtil() throws InstantiationException {
    throw new InstantiationException("StringUtil不支持实例化");
  }

  /**
   * 替换字符串
   *
   * @param sourceText 原文本文件
   * @param param 替换的参数
   * @return 替换后的结果
   */
  public static String replaceAll(@NotNull String sourceText, Map<String, String> param) {
    if (CollectionUtils.isEmpty(param)) {
      return sourceText;
    }
    Set<Map.Entry<String, String>> sets = param.entrySet();
    for (Map.Entry<String, String> entry : sets) {
      String regex = "\\$\\{" + entry.getKey() + "}";
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(sourceText);
      sourceText = matcher.replaceAll(entry.getValue());
    }
    return sourceText;
  }
}
