package wx.common.data.infra.filestore;

import java.util.Arrays;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

/** 文件类型后缀 */
@AllArgsConstructor
public enum FileFormat {
  CLI("CLI零件文件", ".cli", "cli"),
  PNG("PNG图片文件", ".png", "pic"),
  JPG("JPG图片文件", ".jpg", "pic"),
  PRINTER_CONFIG,
  UTK_BASE_BP;

  @Getter String desc;

  @Getter String suffix;

  @Getter String type;

  FileFormat() {}

  public static Boolean isPicture(@Nullable String fileName) {
    if (fileName == null) {
      return false;
    }
    return Arrays.stream(FileFormat.values())
        .filter(fileFormat -> Objects.equals("pic", fileFormat.getType()))
        .anyMatch(fileFormat -> fileName.toLowerCase().endsWith(fileFormat.getSuffix()));
  }

  public static Boolean isCliFile(String fileName) {
    if (fileName == null) {
      return false;
    }
    return fileName.toLowerCase().endsWith(CLI.getSuffix());
  }
}
