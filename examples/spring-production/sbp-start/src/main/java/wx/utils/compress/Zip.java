package wx.utils.compress;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang.StringUtils;

public class Zip {
  public static void zipDirectory(File sourceDirectory, File zipFile) throws IOException {
    checkState(sourceDirectory.exists(), "Source directory is not exists: %s", sourceDirectory);
    try (ZipOutputStream out =
        new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))) {
      zipDirectory(sourceDirectory.getAbsoluteFile(), sourceDirectory, out);
    }
  }

  public static void zipFile(File file, File zipFile) throws IOException {
    try (ZipOutputStream out =
        new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))) {
      zipFile(out, file, file.getName());
    }
  }

  public static void unzip(File zipFile, File targetDirectory) throws IOException {
    try (ZipInputStream zis =
        new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)))) {
      extractStream(targetDirectory, zis);
    }
  }

  private static void zipDirectory(File rootDir, File sourceDir, ZipOutputStream out)
      throws IOException {
    for (File file : checkNotNull(sourceDir.listFiles())) {
      if (file.isDirectory()) {
        zipDirectory(rootDir, new File(sourceDir, file.getName()), out);
      } else {
        String zipEntryName = getRelativeZipEntryName(rootDir, file);
        zipFile(out, file, zipEntryName);
      }
    }
  }

  private static String getRelativeZipEntryName(File rootDir, File file) {
    return StringUtils.removeStart(file.getAbsolutePath(), rootDir.getAbsolutePath());
  }

  private static void zipFile(ZipOutputStream out, File file, String zipEntityName)
      throws IOException {
    ZipEntry entry = new ZipEntry(zipEntityName);
    out.putNextEntry(entry);

    try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
      IOUtils.copy(in, out);
    }
  }

  private static void extractStream(File targetDirectory, ZipInputStream zis) throws IOException {
    ZipEntry zipEntry = zis.getNextEntry();
    while (zipEntry != null) {
      extractEntry(targetDirectory, zis, zipEntry);
      zipEntry = zis.getNextEntry();
    }
    zis.closeEntry();
  }

  private static void extractEntry(File targetDirectory, ZipInputStream zis, ZipEntry zipEntry)
      throws IOException {
    File newFile = newFile(targetDirectory, zipEntry);
    if (zipEntry.isDirectory()) {
      org.apache.commons.io.FileUtils.forceMkdir(newFile);
    } else {
      org.apache.commons.io.FileUtils.forceMkdirParent(newFile);
      try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(newFile))) {
        IOUtils.copy(zis, fos);
      }
    }
  }

  private static File newFile(File targetDirectory, ZipEntry entry) throws IOException {
    File targetFile = new File(targetDirectory, entry.getName());
    if (!targetFile
        .getAbsolutePath()
        .startsWith(targetDirectory.getAbsolutePath() + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + entry.getName());
    }
    return targetFile;
  }
}
