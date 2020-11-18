package wx.infra.service.aliyun;

import com.aliyun.oss.model.SimplifiedObjectMeta;
import java.time.LocalDateTime;
import java.util.Optional;
import wx.common.data.infra.filestore.PreSignedUrl;
import wx.infra.service.aliyun.model.AliOssAuth;

public interface AliOssService {

  AliOssSetting getAliOssSetting();

  AliOssAuth createAuth(String dir) throws AliOssServiceException;

  Optional<SimplifiedObjectMeta> getObjectMeta(String bucketName, String fileName)
      throws AliOssServiceException;

  PreSignedUrl generatePreSignedUrl(String bucketName, String fileName, LocalDateTime expireAt)
      throws AliOssServiceException;
}
