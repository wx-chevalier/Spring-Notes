package wx.application.infra.filestore;

import java.util.Collection;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.stereotype.Repository;
import wx.common.data.shared.id.*;
import wx.common.data.shared.id.BaseEntityId;
import wx.common.data.shared.id.FileId;
import wx.domain.infra.filestore.StoredFile;
import wx.domain.infra.filestore.StoredFileRepository;
import wx.infra.common.persistence.MyBatisIdBasedEntityRepository;
import wx.infra.tunnel.db.infra.file.FileDO;
import wx.infra.tunnel.db.infra.file.FileTunnel;
import wx.infra.tunnel.db.mapper.infra.file.FileMapper;

@Repository
public class StoredFileRepositoryImpl
    extends MyBatisIdBasedEntityRepository<FileTunnel, FileMapper, FileDO, StoredFile, FileId>
    implements StoredFileRepository {

  @Getter(AccessLevel.PROTECTED)
  private StoredFileConverter converter;

  public StoredFileRepositoryImpl(
      FileTunnel fileTunnel, FileMapper mapper, StoredFileConverter converter) {
    super(fileTunnel, mapper);
    this.converter = converter;
  }

  @Override
  public boolean assertExist(TenantId tenantId, Collection<FileId> fileIds) {
    return getTunnel().exist(fileIds.stream().map(BaseEntityId::getId).collect(Collectors.toSet()));
  }
}
