package wx.infra.tunnel.db.mapper.infra.message;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import wx.common.data.infra.notice.NoticeTypeKind;
import wx.infra.tunnel.db.infra.message.MessageNoticeDO;
import wx.infra.tunnel.db.shared.KvDO;

@Component
public interface MessageNoticeMapper extends BaseMapper<MessageNoticeDO> {

  /** 根据是否已读统计消息数目 */
  List<KvDO<Boolean, Long>> groupStatistics(
      @Param("userId") Long userId,
      @Param("appId") Long appId,
      @Param("kinds") List<NoticeTypeKind> kinds);
}
