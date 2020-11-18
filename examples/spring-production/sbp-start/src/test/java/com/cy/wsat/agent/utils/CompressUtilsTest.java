package wx.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import wx.utils.compress.Zip;

class CompressUtilsTest {
  private String curFileRelativeToSrc;

  @BeforeEach
  void setUp() {
    curFileRelativeToSrc =
        String.join(File.separator, ("test.java." + getClass().getCanonicalName()).split("\\."))
            + ".java";
  }

  @Test
  void testTarGzipDirectory(@TempDir File root) throws IOException {
    final File src = new File("src");
    final File tarGz = new File(root, "src.tar.gz");
    final File unTarGz = new File(root, "src");

    assertTrue(new File(src, curFileRelativeToSrc).exists());
    assertFalse(tarGz.exists());
    Zip.zipDirectory(src, tarGz);
    assertTrue(tarGz.exists());

    assertFalse(unTarGz.exists());
    assertFalse(new File(unTarGz, curFileRelativeToSrc).exists());
    Zip.unzip(tarGz, unTarGz);
    assertTrue(unTarGz.exists());
    assertTrue(new File(unTarGz, curFileRelativeToSrc).exists());
  }

  @Test
  void name() throws IOException {
    //    CompressUtils.tarGzipDirectory(new File("/tmp/fuck"), new
    // File("/tmp/shit/fuck.java.tar.gz"));
    CompressUtils.unTarGzip(new File("/tmp/shit/fuck.tar.gz"), new File("/tmp/shit"));
  }
}
