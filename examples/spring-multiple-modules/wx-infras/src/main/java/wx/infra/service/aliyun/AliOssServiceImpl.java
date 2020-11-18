package wx.infra.service.aliyun;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyun.oss.model.SimplifiedObjectMeta;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wx.common.data.infra.filestore.PreSignedUrl;
import wx.infra.service.aliyun.model.AliOssAuth;

@Slf4j
@Service
public class AliOssServiceImpl implements AliOssService {

  @Getter private AliOssSetting aliOssSetting;

  private OSSClient ossClient;

  private synchronized OSSClient getOssClient() {
    if (this.ossClient == null) {
      this.ossClient =
          new OSSClient(
              aliOssSetting.getEndpoint(),
              new DefaultCredentialProvider(
                  aliOssSetting.getAccessKeyId(), aliOssSetting.getAccessKeySecret()),
              null);
    }
    return this.ossClient;
  }

  public AliOssServiceImpl(AliOssSetting aliOssSetting) {
    this.aliOssSetting = aliOssSetting;
  }

  @Override
  public AliOssAuth createAuth(String parentDir) {
    try {
      return internalCreateAuth(parentDir);
    } catch (Throwable t) {
      throw new AliOssServiceException(t);
    }
  }

  @Override
  public Optional<SimplifiedObjectMeta> getObjectMeta(String bucketName, String fileName) {
    try {
      return internalGetObjectMeta(bucketName, fileName);
    } catch (Throwable t) {
      throw new AliOssServiceException(t);
    }
  }

  @Override
  public PreSignedUrl generatePreSignedUrl(
      String bucketName, String fileKey, LocalDateTime expireAt) {
    try {
      return internalGeneratePreSignedUrl(bucketName, fileKey, expireAt);
    } catch (Throwable t) {
      throw new AliOssServiceException(t);
    }
  }

  private AliOssAuth internalCreateAuth(String parentDir) {
    String accessId = aliOssSetting.getAccessKeyId();
    Long maxSize = aliOssSetting.getMaxSize();

    log.info("尝试生成OSS 授权,配置信息：{}", aliOssSetting);

    // 存储目录
    String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
    String dir = String.format("%s/%s", parentDir, dateStr);

    // 签名有效期
    LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(aliOssSetting.getExpiration());
    Date expiration = Date.from(expirationDate.atZone(ZoneId.systemDefault()).toInstant());

    PolicyConditions pConditions = new PolicyConditions();
    pConditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, maxSize);
    pConditions.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
    String postPolicy = getOssClient().generatePostPolicy(expiration, pConditions);

    byte[] binaryData = postPolicy.getBytes(Charset.defaultCharset());
    String policy = BinaryUtil.toBase64String(binaryData);

    String signature = getOssClient().calculatePostSignature(postPolicy);

    return new AliOssAuth(accessId, signature, aliOssSetting.getAction(), dir, policy);
  }

  private Optional<SimplifiedObjectMeta> internalGetObjectMeta(String bucketName, String fileName) {
    boolean objectExist = getOssClient().doesObjectExist(bucketName, fileName);
    if (!objectExist) {
      log.warn("File not found: bucketName={} fileName={}", bucketName, fileName);
      return Optional.empty();
    }
    return Optional.of(
        getOssClient().getSimplifiedObjectMeta(aliOssSetting.getBucketName(), fileName));
  }

  private PreSignedUrl internalGeneratePreSignedUrl(
      String bucketName, String fileKey, LocalDateTime expireAt) {
    bucketName = bucketName == null ? aliOssSetting.getBucketName() : bucketName;
    if (expireAt.isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("expiredAt before now: " + expireAt);
    }
    log.info(
        "generate pre signed url for bucketName={} fileName={}, expires at {}",
        bucketName,
        fileKey,
        expireAt);
    if (fileKey.startsWith("https")) {
      // work around: 对于之前上传的保存了 url 但是没有保存 key 的数据，手工从 url 中剥开 key
      fileKey = fileKey.replaceAll(aliOssSetting.getAction() + "/", "");
    }
    URL url =
        getOssClient()
            .generatePresignedUrl(
                bucketName,
                fileKey,
                Date.from(expireAt.atZone(ZoneId.systemDefault()).toInstant()));
    return new PreSignedUrl(url.toString(), expireAt);
  }
}
