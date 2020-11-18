package wx.utils;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {
  public static File createDirIfNotExists(String dirPath) {
    final File file = new File(dirPath);
    if (file.exists()) {
      if (!file.isDirectory()) {
        throw new UncheckedIOException(
            new IOException(String.format("%s is not a directory", dirPath)));
      }
    } else {
      final boolean created = file.mkdirs();
      if (created) {
        log.debug("Created directory {}", dirPath);
      }
    }
    return file;
  }

  @SuppressWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  public static Boolean isDirEmpty(File file) {
    try (DirectoryStream<Path> paths = Files.newDirectoryStream(Paths.get(file.getPath()))) {
      return !paths.iterator().hasNext();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
