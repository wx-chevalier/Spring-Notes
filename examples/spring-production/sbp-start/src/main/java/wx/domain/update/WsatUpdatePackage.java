package wx.domain.update;

import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import lombok.Getter;
import wx.utils.CompressUtils;
import wx.utils.DockerUtil;
import wx.utils.JSONUtils;
import wx.utils.Utils;

public class WsatUpdatePackage {
  @Getter WsatUpdateManifest manifest;

  private File rootDir;

  private WsatUpdatePackage(File rootDir) {
    this.rootDir = rootDir;
  }

  /**
   * @param updateFile tar.gz
   *     <li>manifest.json
   *     <li>update files...
   */
  public static WsatUpdatePackage loadUpdatePackage(File updateFile) throws IOException {
    File packDir =
        new File(
            System.getProperty("java.io.tmpdir"),
            String.format("wsat-%s-pack", Utils.md5sum(updateFile)));
    if (packDir.mkdirs()) {
      CompressUtils.unTarGzip(updateFile, packDir);
    }
    WsatUpdatePackage pack = new WsatUpdatePackage(packDir);
    pack.checkAndLoad();
    return pack;
  }

  private File file(String name) {
    return new File(rootDir, name);
  }

  private Optional<File> getAgentJarFile() {
    return Optional.ofNullable(manifest.getAgentJar()).map(this::file);
  }

  private Optional<File> getProductImageFile() {
    return Optional.ofNullable(manifest.getProductImage()).map(this::file);
  }

  private Optional<File> getCscanImageFile() {
    return Optional.ofNullable(manifest.getCscanImage()).map(this::file);
  }

  private Optional<File> getPocPackageFile() {
    return Optional.ofNullable(manifest.getPocPackage()).map(this::file);
  }

  private void checkAndLoad() {
    File manifestFile = new File(rootDir, "manifest.json");
    this.manifest = JSONUtils.readJSON(manifestFile, WsatUpdateManifest.class);
    checkState(getAgentJarFile().map(File::exists).orElse(true), "更新包异常：agent 更新文件不存在");
    checkState(getProductImageFile().map(File::exists).orElse(true), "更新包异常：产品镜像更新文件不存在");
    checkState(getCscanImageFile().map(File::exists).orElse(true), "更新包异常：扫描镜像更新文件不存在");
    checkState(getPocPackageFile().map(File::exists).orElse(true), "更新包异常：POC 包更新文件不存在");
  }

  public void doUpdate() {
    getCscanImageFile().ifPresent(this::updateCscan);
    getProductImageFile().ifPresent(this::updateProduct);
    getPocPackageFile().ifPresent(this::updatePocPackage);
  }

  private void updatePocPackage(File pocPackageFile) {}

  private void updateProduct(File productImageFile) {
    new DockerUtil().loadImage(productImageFile);
  }

  private void updateCscan(File cscanImageFile) {
    new DockerUtil().loadImage(cscanImageFile);
  }
}
