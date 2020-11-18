package wx.utils;

import java.io.File;
import java.io.IOException;
import wx.utils.compress.GZip;
import wx.utils.compress.Tar;
import wx.utils.compress.Zip;

public class CompressUtils {
  public static void zipDirectory(File sourceDirectory, File zipFile) throws IOException {
    Zip.zipDirectory(sourceDirectory, zipFile);
  }

  public static void zipFile(File file, File zipFile) throws IOException {
    Zip.zipFile(file, zipFile);
  }

  public static void unzip(File zipFile, File targetDirectory) throws IOException {
    Zip.unzip(zipFile, targetDirectory);
  }

  /** 等同于 {@code cd sourceDirectory && tar -cf tarFile .} */
  public static void tarDirectory(File sourceDirectory, File tarFile) throws IOException {
    Tar.tarDirectory(sourceDirectory, tarFile);
  }

  /** 等同于 {@code cd targetDirectory && tar -xf tarFile} */
  public static void untarDirectory(File tarFile, File targetDirectory) throws IOException {
    Tar.untarDirectory(tarFile, targetDirectory);
  }

  public static void gzipFile(File sourceFile, File gzipFile) throws IOException {
    GZip.gzipCompress(sourceFile, gzipFile);
  }

  public static void ungzipFile(File gzipFile, File targetFile) throws IOException {
    GZip.gzipDecompress(gzipFile, targetFile);
  }

  /** 等同于 {@code cd sourceDirectory && tar -czf tarGzipFile .} */
  public static void tarGzipDirectory(File sourceDirectory, File tarGzipFile) throws IOException {
    Tar.tarGzipDirectory(sourceDirectory, tarGzipFile);
  }

  /** 等同于 {@code cd targetDirectory && tar xfz tarGzipFile} */
  public static void unTarGzip(File tarGzipFile, File targetDirectory) throws IOException {
    Tar.untarGzipDirectory(tarGzipFile, targetDirectory);
  }
}
