package wx.utils;

import static com.google.common.base.Preconditions.checkState;
import static wx.constants.Misc.DEFAULT_CHARSET;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
public class ObjectStore {
  @Getter private File root;
  @Getter private Set<String> ids;

  private File metaDir;
  private File idsFile;

  public ObjectStore(String root) {
    this(new File(root));
  }

  public ObjectStore(File root) {
    this.root = root;
    this.metaDir = new File(root, "meta");
    this.idsFile = new File(this.metaDir, "ids");

    try {
      FileUtils.forceMkdir(this.root);
      FileUtils.forceMkdir(this.metaDir);
      if (this.idsFile.createNewFile()) {
        log.debug("ids file created: {}", idsFile);
      }
      loadIds();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @PreDestroy
  public void destroy() {
    File tmpIdsFile = new File(metaDir, "ids.tmp");
    tmpIdsFile.deleteOnExit();
    try (BufferedWriter bufferedWriter =
        new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(tmpIdsFile), DEFAULT_CHARSET))) {
      for (String id : ids) {
        bufferedWriter.write(id + "\n");
      }
      bufferedWriter.flush();
      FileUtils.copyFile(tmpIdsFile, idsFile);
    } catch (IOException e) {
      log.warn("compress failed", e);
      // ignored
    } finally {
      if (tmpIdsFile.delete()) {
        log.debug("tmp ids file removed: {}", tmpIdsFile);
      }
    }
  }

  public Boolean exists(String objId) {
    return getObjFile(objId).exists();
  }

  public void addFile(String objId, File srcFile) {
    checkState(srcFile.exists(), "srcFile not found: %s", srcFile);
    final File objFile = getObjFile(objId);
    try {
      FileUtils.forceMkdirParent(objFile);
      FileUtils.deleteQuietly(objFile);
      FileUtils.copyFile(srcFile, objFile);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    addId(objId);
  }

  public void saveJSONObject(String objId, Object object) {
    final File objFile = getObjFile(objId);
    try {
      FileUtils.forceMkdirParent(objFile);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    JSONUtils.saveJSON(object, objFile);
    addId(objId);
  }

  public <T> T readJSONObject(String objId, Class<T> clazz) {
    return JSONUtils.readJSON(getObjFile(objId), clazz);
  }

  /** @return 该对象存在且被删除返回 true */
  public Boolean remove(String objId) {
    removeId(objId);
    final File objFile = getObjFile(objId);
    return objFile.delete();
  }

  public File getObjFile(String objId) {
    objId = objId.toLowerCase();
    if (objId.length() <= 2) {
      return new File(root, objId);
    } else {
      return new File(new File(root, objId.substring(0, 2)), objId.substring(2));
    }
  }

  private void addId(String objId) {
    try {
      Files.write(
          Paths.get(new File(metaDir, "ids").getPath()),
          (objId + "\n").getBytes(DEFAULT_CHARSET),
          StandardOpenOption.APPEND);
      this.ids.add(objId);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void removeId(String objId) {
    try {
      Files.write(
          Paths.get(new File(metaDir, "ids").getPath()),
          ("- " + objId + "\n").getBytes(DEFAULT_CHARSET),
          StandardOpenOption.APPEND);
      ids.remove(objId);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void loadIds() throws IOException {
    ids = new HashSet<>();
    if (idsFile.exists()) {
      try (final BufferedReader bufferedReader =
          new BufferedReader(
              new InputStreamReader(new FileInputStream(idsFile), DEFAULT_CHARSET))) {
        bufferedReader
            .lines()
            .filter(line -> line.length() != 0)
            .forEach(
                line -> {
                  if (line.startsWith("- ")) {
                    ids.remove(line.substring(2));
                  } else {
                    ids.add(line);
                  }
                });
      }
    }
  }
}
