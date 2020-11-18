package wx.api.rest.common.message;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import wx.api.rest.common.message.dto.MessageNoticeQueryReq;
import wx.api.rest.common.message.dto.SendMessageReq;
import wx.api.rest.shared.BaseController;
import wx.api.rest.shared.dto.envelope.PaginationMeta;
import wx.api.rest.shared.dto.envelope.ResponseEnvelope;
import wx.application.infra.message.MessageNoticeCount;
import wx.common.data.shared.id.ApplicationId;
import wx.common.data.shared.id.MessageNoticeId;
import wx.common.data.shared.id.MessageTypeId;
import wx.common.data.shared.id.UserId;
import wx.domain.infra.message.MessageNotice;

@RestController
@RequestMapping("/message_notice")
@Api(tags = "📩 消息通知")
public class MessageNoticeController extends BaseController {

  @GetMapping
  @ApiOperation("获取当前登录用户的站内消息")
  public ResponseEnvelope<List<MessageNotice>> list(MessageNoticeQueryReq req) {

    UserId userId = getCurrentUserId();
    Pageable pageable = req.toPage();

    Page<MessageNotice> result =
        siteMessageService.search(
            userId,
            pageable,
            ApplicationId.create(req.getAppId()),
            req.getKind(),
            req.getHasRead(),
            req.getSearchKey(),
            req.getStartDate(),
            req.getEndDate());

    return ResponseEnvelope.createOk(
        result.getContent(),
        new PaginationMeta(result.getNumber(), result.getTotalElements(), result.getTotalPages()));
  }

  @GetMapping("/count")
  @ApiOperation("获取当前登录用户的站内消息统计数据")
  public ResponseEnvelope<MessageNoticeCount> count(String appId) {
    UserId userId = getCurrentUserId();
    return ResponseEnvelope.createOk(
        siteMessageService.countOfSiteMessage(userId, ApplicationId.create(appId)));
  }

  @PostMapping
  @ApiOperation("标记指定消息为已读")
  public ResponseEnvelope<Void> markMessageNoticesRead(@RequestBody Set<String> ids) {
    Set<Long> messageNoticeIds = ids.stream().map(Long::valueOf).collect(Collectors.toSet());
    siteMessageService.markedRead(messageNoticeIds);
    return ResponseEnvelope.createOk();
  }

  @GetMapping("/{messageNoticeId}")
  @ApiOperation("获取指定消息详情内容")
  public ResponseEnvelope<MessageNotice> markMessageNoticesRead(
      @PathVariable("messageNoticeId") String messageNoticeId) {
    MessageNotice messageNotice =
        siteMessageService.findById(getCurrentUserId(), MessageNoticeId.create(messageNoticeId));
    return ResponseEnvelope.createOk(messageNotice);
  }

  @DeleteMapping
  @ApiOperation("删除指定消息")
  public ResponseEnvelope<Void> delete(@RequestBody Set<String> ids) {
    Set<Long> messageNoticeIds = ids.stream().map(Long::valueOf).collect(Collectors.toSet());
    siteMessageService.markedRead(getCurrentUserId(), messageNoticeIds);
    return ResponseEnvelope.createOk();
  }

  @PostMapping("/all")
  @ApiOperation("标记应用消息全部已读")
  public ResponseEnvelope<Void> markAllMessageNoticesRead(
      @RequestParam("appId") Set<String> appId) {
    if (CollectionUtils.isEmpty(appId)) {
      return ResponseEnvelope.createOk();
    }
    siteMessageService.markedRead(
        getCurrentUserId(), appId.stream().map(ApplicationId::new).collect(Collectors.toSet()));
    return ResponseEnvelope.createOk();
  }

  @PostMapping("/send/template/{templateId}")
  @ApiOperation("发送模板消息")
  public ResponseEnvelope<Void> sendMessage(
      @PathVariable("templateId") String templateId, @Valid @RequestBody SendMessageReq req) {

    notifyService.sendCustomerMessage(
        MessageTypeId.create(templateId),
        req.getUserIds().stream().map(UserId::create).collect(Collectors.toSet()),
        req.getChannels(),
        req.getParam());
    return ResponseEnvelope.createOk();
  }
}
