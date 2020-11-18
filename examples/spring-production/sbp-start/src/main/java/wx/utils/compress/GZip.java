package wx.utils.compress;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class GZip {
  public static void gzipCompress(File sourceFile, File targetGZipFile) throws IOException {
    try (InputStream in = new FileInputStream(sourceFile);
        OutputStream out =
            new GzipCompressorOutputStream(
                new BufferedOutputStream(new FileOutputStream(targetGZipFile)))) {
      IOUtils.copy(in, out);
    }
  }

  public static void gzipDecompress(File sourceGzipFile, File targetFile) throws IOException {
    try (InputStream in = new BufferedInputStream(new FileInputStream(sourceGzipFile));
        GzipCompressorOutputStream out =
            new GzipCompressorOutputStream(
                new BufferedOutputStream(new FileOutputStream(targetFile)))) {
      IOUtils.copy(in, out);
    }
  }
}
