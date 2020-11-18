package wx.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DockerUtilTest {
  private final DockerUtil dockerUtil = new DockerUtil();
  private String srcVolume;
  private String dstVolume;

  @BeforeEach
  void setUp() {
    srcVolume = "test-srcVolume"; // + System.nanoTime();
    dstVolume = "test-dstVolume"; // + System.nanoTime();
    dockerUtil.removeVolumeIfExists(srcVolume);
    dockerUtil.removeVolumeIfExists(dstVolume);
    dockerUtil.createOrGetVolume(srcVolume);
    dockerUtil.createOrGetVolume(dstVolume);
    final Volume volume = new Volume("/data");
    dockerUtil
        .createAndStartAndWaitContainerAndRemove(
            "busybox",
            cmd ->
                cmd.withVolumes(volume)
                    .withHostConfig(new HostConfig().withBinds(new Bind(this.srcVolume, volume)))
                    .withCmd("touch", "/data/hello_world"))
        .join();
  }

  @AfterEach
  void tearDown() {
    dockerUtil.removeVolumeIfExists(srcVolume);
    dockerUtil.removeVolumeIfExists(dstVolume);
  }

  @Test
  void testBackupAndRecoverVolume(@TempDir File tmpdir) throws IOException {
    final File srcBack = new File(tmpdir, "srcBack.tar.gz");
    final File srcBackupDecompress = new File(tmpdir, "srcBack");
    final File dstBack = new File(tmpdir, "dstBack.tar.gz");
    final File dstBackupDecompress = new File(tmpdir, "dstBack");
    final File dstBackAfterRecover = new File(tmpdir, "dstBackAfterRecover.tar.gz");
    final File dstBackAfterRecoverDecompress = new File(tmpdir, "dstBackAfterRecover");

    assertFalse(srcBack.exists());
    dockerUtil.backupVolume(srcVolume, srcBack);
    assertTrue(srcBack.exists());

    CompressUtils.unTarGzip(srcBack, srcBackupDecompress);
    assertTrue(new File(srcBackupDecompress, "hello_world").exists());

    // dstVolume 不存在 hello_world
    dockerUtil.backupVolume(dstVolume, dstBack);
    CompressUtils.unTarGzip(dstBack, dstBackupDecompress);
    assertFalse(new File(dstBackupDecompress, "hello_world").exists());

    // recover
    dockerUtil.recoverVolume(dstVolume, srcBack);

    // dstVolume 恢复之后存在 hello_world
    dockerUtil.backupVolume(dstVolume, dstBackAfterRecover);
    CompressUtils.unTarGzip(dstBackAfterRecover, dstBackAfterRecoverDecompress);
    assertTrue(new File(dstBackAfterRecoverDecompress, "hello_world").exists());
  }
}
