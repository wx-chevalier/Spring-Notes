package wx.utils.compress;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TarTest {
  private String curFileRelativeToSrc;

  @BeforeEach
  void setUp() {
    curFileRelativeToSrc =
        String.join(File.separator, ("test.java." + getClass().getCanonicalName()).split("\\."))
            + ".java";
  }

  @Test
  void testTarAndUntarDirectory(@TempDir File root) throws IOException {
    final File src = new File("src");
    final File tarFile = new File(root, "src.tar");
    final File untarDir = new File(root, "src-untar");

    assertTrue(new File(src, curFileRelativeToSrc).exists());
    assertFalse(tarFile.exists());
    Tar.tarDirectory(src, tarFile);
    assertTrue(tarFile.exists());

    assertFalse(untarDir.exists());
    assertFalse(new File(untarDir, curFileRelativeToSrc).exists());
    Tar.untarDirectory(tarFile, untarDir);
    assertTrue(untarDir.exists());
    assertTrue(new File(untarDir, curFileRelativeToSrc).exists());
  }

  @Test
  void testTarGzipAndUntarGzipDirectory(@TempDir File root) throws IOException {
    final File src = new File("src");
    final File tarFile = new File(root, "src.tar.gz");
    final File untarDir = new File(root, "src-untar-gz");

    assertTrue(new File(src, curFileRelativeToSrc).exists());
    assertFalse(tarFile.exists());
    Tar.tarGzipDirectory(src, tarFile);
    assertTrue(tarFile.exists());

    assertFalse(untarDir.exists());
    assertFalse(new File(untarDir, curFileRelativeToSrc).exists());
    Tar.untarGzipDirectory(tarFile, untarDir);
    assertTrue(untarDir.exists());
    assertTrue(new File(untarDir, curFileRelativeToSrc).exists());
  }
}
