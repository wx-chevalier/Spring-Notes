package wx.infra.common.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.hash.Hashing;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import wx.common.data.common.fileformat.ImageInfo;

@Slf4j
public class FileUtils {

  @SuppressWarnings({"UnstableApiUsage", "deprecation"})
  public static String getMd5(byte[] bytes) {
    return Hashing.md5().hashBytes(bytes).toString();
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
  public static void deleteFile(File file) {
    file.delete();
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
  public static void mkdirs(File dir) {
    dir.mkdirs();
  }

  public static File ensureDir(File file) {
    if (file.exists()) {
      checkArgument(file.isDirectory(), "file %s is not dir", file);
    } else {
      mkdirs(file);
    }
    return file;
  }

  @SuppressWarnings({"UnstableApiUsage", "deprecation", "StatementWithEmptyBody"})
  public static String md5(File file) {
    try {
      return DigestUtils.md5Hex(new FileInputStream(file.getAbsolutePath()));
    } catch (IOException e) {
      log.error("计算文件MD5出现异常", e);
      throw new RuntimeException(e);
    }
  }

  public static ImageInfo readImageInfo(File file) {
    try (InputStream is = new FileInputStream(file)) {
      BufferedImage bufferedImage = ImageIO.read(is);
      checkNotNull(bufferedImage, "Not image file: %s", file);
      return new ImageInfo()
          .setHeight(bufferedImage.getHeight())
          .setWidth(bufferedImage.getWidth());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /** 获取文件的拓展名(小写) */
  public static String getExtension(String fileName) {
    String extension = null;
    int i = fileName.lastIndexOf('.');
    if (i > 0) {
      extension = fileName.substring(i + 1).toLowerCase();
    }
    return extension;
  }
}
