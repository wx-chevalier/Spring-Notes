package wx.application.admin;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.stereotype.Repository;
import wx.common.data.shared.id.ApplicationId;
import wx.domain.admin.Application;
import wx.domain.admin.ApplicationRepository;
import wx.infra.common.persistence.MyBatisIdBasedEntityRepository;
import wx.infra.tunnel.db.admin.ApplicationDO;
import wx.infra.tunnel.db.admin.ApplicationTunnel;
import wx.infra.tunnel.db.mapper.admin.ApplicationMapper;

@Repository
public class ApplicationRepositoryImpl
    extends MyBatisIdBasedEntityRepository<
        ApplicationTunnel, ApplicationMapper, ApplicationDO, Application, ApplicationId>
    implements ApplicationRepository {

  @Getter(AccessLevel.PROTECTED)
  private ApplicationConverter converter;

  public ApplicationRepositoryImpl(
      ApplicationTunnel applicationTunnel,
      ApplicationMapper mapper,
      ApplicationConverter converter) {
    super(applicationTunnel, mapper);
    this.converter = converter;
  }
}
