package wx.utils.compress;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang.StringUtils;

public class Tar {
  public static void tarDirectory(File sourceDirectory, File tarFile) throws IOException {
    try (TarArchiveOutputStream out =
        new TarArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(tarFile)))) {
      tarDirectory(sourceDirectory.getAbsoluteFile(), sourceDirectory, out);
    }
  }

  public static void untarDirectory(File sourceFile, File targetDirectory) throws IOException {
    try (TarArchiveInputStream tarStream =
        new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(sourceFile)))) {
      extractStream(targetDirectory, tarStream);
    }
  }

  public static void tarGzipDirectory(File sourceDirectory, File tarGzFile) throws IOException {
    try (TarArchiveOutputStream out =
        new TarArchiveOutputStream(
            new GzipCompressorOutputStream(
                new BufferedOutputStream(new FileOutputStream(tarGzFile))))) {
      tarDirectory(sourceDirectory.getAbsoluteFile(), sourceDirectory, out);
    }
  }

  public static void untarGzipDirectory(File tarGzFile, File targetDirectory) throws IOException {
    try (TarArchiveInputStream tarStream =
        new TarArchiveInputStream(
            new GZIPInputStream(new BufferedInputStream(new FileInputStream(tarGzFile))))) {
      extractStream(targetDirectory, tarStream);
    }
  }

  private static void extractStream(File targetDirectory, TarArchiveInputStream tarStream)
      throws IOException {
    TarArchiveEntry entry = tarStream.getNextTarEntry();
    while (entry != null) {
      extractEntry(targetDirectory, tarStream, entry);
      entry = tarStream.getNextTarEntry();
    }
  }

  private static void extractEntry(
      File targetDirectory, TarArchiveInputStream tarStream, TarArchiveEntry entry)
      throws IOException {
    File newFile = newFile(targetDirectory, entry);
    if (entry.isDirectory()) {
      org.apache.commons.io.FileUtils.forceMkdir(newFile);
    } else {
      org.apache.commons.io.FileUtils.forceMkdirParent(newFile);
      try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(newFile))) {
        IOUtils.copy(tarStream, fos);
      }
    }
  }

  private static File newFile(File targetDirectory, TarArchiveEntry entry) throws IOException {
    File targetFile = new File(targetDirectory, entry.getName());
    if (!targetFile
        .getAbsolutePath()
        .startsWith(targetDirectory.getAbsolutePath() + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + entry.getName());
    }
    return targetFile;
  }

  private static void tarDirectory(File rootDir, File sourceDir, TarArchiveOutputStream out)
      throws IOException {
    for (File file : checkNotNull(sourceDir.listFiles())) {
      if (file.isDirectory()) {
        tarDirectory(rootDir, new File(sourceDir, file.getName()), out);
      } else {
        tarFile(out, file, getRelativeEntryName(rootDir, file));
      }
    }
  }

  private static void tarFile(TarArchiveOutputStream out, File file, String relativeEntryName)
      throws IOException {
    TarArchiveEntry entry = new TarArchiveEntry(relativeEntryName);
    entry.setSize(file.length());
    out.putArchiveEntry(entry);
    try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
      IOUtils.copy(in, out);
    }
    out.closeArchiveEntry();
  }

  private static String getRelativeEntryName(File rootDir, File file) {
    return StringUtils.removeStart(file.getAbsolutePath(), rootDir.getAbsolutePath());
  }
}
