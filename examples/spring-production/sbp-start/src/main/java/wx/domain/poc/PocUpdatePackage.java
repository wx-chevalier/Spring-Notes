package wx.domain.poc;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Optional.ofNullable;

import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import wx.utils.JSONUtils;

public class PocUpdatePackage extends PocPackage {
  private File updateInfoFile;
  @Setter private UpdateInfo updateInfo;

  public PocUpdatePackage(String packageDir) {
    this(new File(packageDir));
  }

  public PocUpdatePackage(File packageDir) {
    super(packageDir);
    this.updateInfoFile = new File(packageDir, "update_info");
    try {
      this.updateInfo = JSONUtils.readJSON(this.updateInfoFile, UpdateInfo.class);
    } catch (UncheckedIOException e) {
      this.updateInfo = new UpdateInfo();
    }
  }

  private void flushUpdateInfo() {
    JSONUtils.saveJSON(updateInfo, updateInfoFile);
  }

  public void updatePocFrom(PocPackage pack, String pocId, Boolean isNew) {
    copyPoc(pack, pocId);
    (isNew ? this.updateInfo.newPocIds : this.updateInfo.updatedPocIds).add(pocId);
  }

  public void updateVulnFrom(PocPackage pack, String vulnId, Boolean isNew) {
    copyVuln(pack, vulnId);
    (isNew ? this.updateInfo.newVulnIds : this.updateInfo.updatedVulnIds).add(vulnId);
  }

  public void updateStrategyFrom(PocPackage pack, String strategyId, Boolean isNew) {
    copyStrategy(pack, strategyId);
    (isNew ? this.updateInfo.newStrategyIds : this.updateInfo.updatedStrategyIds).add(strategyId);
  }

  public static void createPackedUpdatePackage(
      PocPackage prevVersion, PocPackage curVersion, File updatePackageFile) throws IOException {
    final File tmpDir = File.createTempFile("poc_package", Long.toString(System.nanoTime()));
    tmpDir.deleteOnExit();
    createUpdatePackage(prevVersion, curVersion, tmpDir).pack(updatePackageFile);
    FileUtils.deleteQuietly(tmpDir);
  }

  /** 对比两个版本的 PocPackage 生成更新包 */
  public static PocUpdatePackage createUpdatePackage(
      PocPackage prevVersion, PocPackage curVersion, File updatePackageDir) {
    checkState(!updatePackageDir.exists(), "目标目录已经存在: %s", updatePackageDir);
    try {
      FileUtils.forceMkdir(updatePackageDir);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    final Set<String> prevCodeIds = prevVersion.getCode().getIds();
    final Set<String> curCodeIds = curVersion.getCode().getIds();

    PocUpdatePackage update = new PocUpdatePackage(updatePackageDir);

    // 只考虑增量更新
    final Sets.SetView<String> newCodeIds = Sets.difference(curCodeIds, prevCodeIds);

    for (String codeId : newCodeIds) {
      final CodeMetaInfo meta = curVersion.loadCodeMeta(codeId);
      ofNullable(meta.getPocIds())
          .ifPresent(
              pocIds ->
                  pocIds.forEach(
                      pocId ->
                          update.updatePocFrom(
                              curVersion, pocId, !prevVersion.getPoc().exists(pocId))));
      ofNullable(meta.getStrategyId())
          .ifPresent(
              strategyId ->
                  update.updateStrategyFrom(
                      curVersion, strategyId, !prevVersion.getStrategy().exists(strategyId)));
      ofNullable(meta.getVulnId())
          .ifPresent(
              vulnId ->
                  update.updateVulnFrom(curVersion, vulnId, !prevVersion.getVuln().exists(vulnId)));
    }

    update.flushUpdateInfo();

    return update;
  }

  @Getter
  @NoArgsConstructor
  public static class UpdateInfo {
    Set<String> newPocIds = new HashSet<>();
    Set<String> newVulnIds = new HashSet<>();
    Set<String> newStrategyIds = new HashSet<>();
    Set<String> updatedPocIds = new HashSet<>();
    Set<String> updatedVulnIds = new HashSet<>();
    Set<String> updatedStrategyIds = new HashSet<>();
  }
}
