package wx.utils.compress;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ZipTest {
  private String curFileRelativeToSrc;

  @BeforeEach
  void setUp() {
    curFileRelativeToSrc =
        String.join(File.separator, ("test.java." + getClass().getCanonicalName()).split("\\."))
            + ".java";
  }

  @Test
  void testZipAndUnZipDirectory(@TempDir File root) throws IOException {
    final File src = new File("src");
    final File zipFile = new File(root, "src.zip");
    final File unzipDir = new File(root, "src");

    assertTrue(new File(src, curFileRelativeToSrc).exists());
    assertFalse(zipFile.exists());
    Zip.zipDirectory(src, zipFile);
    assertTrue(zipFile.exists());

    assertFalse(unzipDir.exists());
    assertFalse(new File(unzipDir, curFileRelativeToSrc).exists());
    Zip.unzip(zipFile, unzipDir);
    assertTrue(unzipDir.exists());
    assertTrue(new File(unzipDir, curFileRelativeToSrc).exists());
  }
}
