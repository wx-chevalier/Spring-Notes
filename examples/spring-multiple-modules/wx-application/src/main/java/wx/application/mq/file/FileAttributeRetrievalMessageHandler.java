package wx.application.mq.file;

import static wx.common.data.mq.MessageQueueName.GET_OSS_FILE_INFO_MESSAGE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Optional;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import wx.common.data.infra.filestore.FileAttributeNames;
import wx.common.data.infra.filestore.FileStoreInfo;
import wx.common.data.infra.filestore.OssFileStoreInfo;
import wx.common.data.mq.file.FileAttributeRetrievalMessage;
import wx.domain.infra.fileformat.StoredFileAttributeService;
import wx.domain.infra.filestore.StoredFile;
import wx.domain.infra.filestore.StoredFileRepository;
import wx.infra.service.aliyun.AliOssService;

@Slf4j
@Component
public class FileAttributeRetrievalMessageHandler {

  private ObjectMapper objectMapper;

  private StoredFileRepository storedFileRepository;

  private StoredFileAttributeService storedFileAttributeService;

  private AliOssService aliOssService;

  public FileAttributeRetrievalMessageHandler(
      ObjectMapper objectMapper,
      StoredFileRepository storedFileRepository,
      StoredFileAttributeService storedFileAttributeService,
      AliOssService aliOssService) {
    this.objectMapper = objectMapper;
    this.storedFileRepository = storedFileRepository;
    this.storedFileAttributeService = storedFileAttributeService;
    this.aliOssService = aliOssService;
  }

  @RabbitListener(queuesToDeclare = @Queue(GET_OSS_FILE_INFO_MESSAGE))
  public void process(@Valid FileAttributeRetrievalMessage msg) {
    // TODO: 文件属性获取
    log.info("接收到OSS文件处理消息:{}", msg);

    Optional<StoredFile> storedFile =
        storedFileRepository.findById(msg.getTenantId(), msg.getFileId());
    if (!storedFile.isPresent()) {
      log.warn("File not found: {} {}", msg.getTenantId(), msg.getFileId());
      return;
    }

    Optional<ObjectNode> attrs = Optional.empty();

    FileStoreInfo storeInfo = storedFile.get().getStoreInfo();
    if (storeInfo instanceof OssFileStoreInfo) {
      attrs = retrieveOssFileMeta((OssFileStoreInfo) storeInfo);
    }

    if (!attrs.isPresent()) {
      log.warn("File meta not found: {} {}", msg.getTenantId(), msg.getFileId());
      return;
    } else {
      storedFileAttributeService.setFileAttributes(msg.getTenantId(), msg.getFileId(), attrs.get());
    }

    log.info("消息处理完成");
  }

  private Optional<ObjectNode> retrieveOssFileMeta(OssFileStoreInfo storeInfo) {
    return aliOssService
        .getObjectMeta(storeInfo.getBucketName(), storeInfo.getKey())
        .map(
            meta ->
                objectMapper
                    .createObjectNode()
                    .put(FileAttributeNames.FILE_MD5, meta.getETag())
                    .put(FileAttributeNames.FILE_SIZE, meta.getSize()));
  }
}
