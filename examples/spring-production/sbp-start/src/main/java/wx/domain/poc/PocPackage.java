package wx.domain.poc;

import static com.google.common.base.Preconditions.checkState;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import wx.utils.CompressUtils;
import wx.utils.ObjectStore;

/** POC 包 */
public class PocPackage {
  @Getter private ObjectStore code;
  @Getter private ObjectStore codeMeta;
  @Getter private ObjectStore strategy;
  @Getter private ObjectStore vuln;
  @Getter private ObjectStore poc;

  private File packageDir;
  private Map<String, String> cachedMapStrategyIdCodeId;
  private Map<String, String> cachedMapPocIdCodeId;
  private Map<String, String> cachedMapVulnIdCodeId;

  public PocPackage(String packageDir) {
    this(new File(packageDir));
  }

  public PocPackage(File packageDir) {
    this.packageDir = packageDir;
    this.code = new ObjectStore(new File(packageDir, "code"));
    this.codeMeta = new ObjectStore(new File(packageDir, "code_meta"));
    this.strategy = new ObjectStore(new File(packageDir, "strategy"));
    this.poc = new ObjectStore(new File(this.packageDir, "poc"));
    this.vuln = new ObjectStore(new File(this.packageDir, "vuln"));
  }

  public Map<String, String> calcMapStrategyIdCodeId() {
    calculateMapObjIdCodeId(false);
    return cachedMapStrategyIdCodeId;
  }

  public Map<String, String> calcMapPocIdCodeId() {
    calculateMapObjIdCodeId(false);
    return cachedMapPocIdCodeId;
  }

  public Map<String, String> calcMapVulnIdCodeId() {
    calculateMapObjIdCodeId(false);
    return cachedMapVulnIdCodeId;
  }

  public Poc loadPoc(String pocId) {
    return poc.readJSONObject(pocId, Poc.class);
  }

  public Vuln loadVuln(String vulnId) {
    return poc.readJSONObject(vulnId, Vuln.class);
  }

  public Strategy loadStrategy(String vulnId) {
    return poc.readJSONObject(vulnId, Strategy.class);
  }

  public CodeMetaInfo loadCodeMeta(String codeId) {
    CodeMetaInfo codeMetaInfo;
    try {
      codeMetaInfo = codeMeta.readJSONObject(codeId, CodeMetaInfo.class);
    } catch (Exception e) {
      codeMetaInfo = new CodeMetaInfo();
    }
    codeMetaInfo.setSrcHash(codeId);
    return codeMetaInfo;
  }

  void copyCode(PocPackage otherPackage, String codeId) {
    getCode().addFile(codeId, otherPackage.getCode().getObjFile(codeId));
    getCodeMeta().addFile(codeId, otherPackage.getCodeMeta().getObjFile(codeId));
  }

  public void copyPoc(PocPackage otherPackage, String pocId) {
    final Poc poc = otherPackage.loadPoc(pocId);
    getPoc().addFile(pocId, otherPackage.getPoc().getObjFile(pocId));
    copyCode(otherPackage, poc.getCodeId());
  }

  public void copyVuln(PocPackage otherPackage, String vulnId) {
    final Vuln vuln = otherPackage.loadVuln(vulnId);
    getVuln().addFile(vulnId, otherPackage.getVuln().getObjFile(vulnId));
    copyCode(otherPackage, vuln.getCodeId());
  }

  public void copyStrategy(PocPackage otherPackage, String strategyId) {
    final Strategy strategy = otherPackage.loadStrategy(strategyId);
    getStrategy().addFile(strategyId, otherPackage.getVuln().getObjFile(strategyId));
    copyCode(otherPackage, strategy.getCodeId());
  }

  public Iterator<CodeMetaInfo> iterateCodeMetas() {
    final Iterator<String> codeIds = this.code.getIds().iterator();
    return new Iterator<CodeMetaInfo>() {
      @Override
      public boolean hasNext() {
        return codeIds.hasNext();
      }

      @Override
      public CodeMetaInfo next() {
        return loadCodeMeta(codeIds.next());
      }
    };
  }

  public void pack(File dstFile) throws IOException {
    checkState(dstFile.exists(), "Dst file exists: %s", dstFile);
    CompressUtils.zipDirectory(packageDir, dstFile);
  }

  public static PocPackage readPackageFile(File packageFile, File extractDir) {
    checkState(extractDir.exists(), "Extraction dir exists: %s", extractDir);
    try {
      FileUtils.forceMkdir(extractDir);
      CompressUtils.unzip(packageFile, extractDir);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return new PocPackage(extractDir);
  }

  private void calculateMapObjIdCodeId(Boolean recalculate) {
    if (recalculate
        || cachedMapPocIdCodeId == null
        || cachedMapStrategyIdCodeId == null
        || cachedMapVulnIdCodeId == null) {
      cachedMapVulnIdCodeId = new HashMap<>();
      cachedMapStrategyIdCodeId = new HashMap<>();
      cachedMapPocIdCodeId = new HashMap<>();
      this.iterateCodeMetas()
          .forEachRemaining(
              codeMetaInfo -> {
                if (codeMetaInfo.getStrategyId() != null) {
                  cachedMapStrategyIdCodeId.put(
                      codeMetaInfo.getStrategyId(), codeMetaInfo.getSrcHash());
                }
                if (codeMetaInfo.getVulnId() != null) {
                  cachedMapVulnIdCodeId.put(codeMetaInfo.getVulnId(), codeMetaInfo.getSrcHash());
                }
                if (codeMetaInfo.getPocIds() != null) {
                  codeMetaInfo
                      .getPocIds()
                      .forEach(pocId -> cachedMapPocIdCodeId.put(pocId, codeMetaInfo.getSrcHash()));
                }
              });
    }
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CodeMetaInfo {
    @JsonProperty("strategy_id")
    String strategyId;

    @JsonProperty("vuln_id")
    String vulnId;

    @JsonProperty("poc_ids")
    List<String> pocIds;

    @Setter String srcHash;
  }
}
