package wx.api.rest.wechat;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import wx.application.wechat.WechatCommandService;
import wx.common.data.wechat.WechatMessage;
import wx.common.data.wechat.WechatResponseTextMessage;
import wx.infra.common.util.WechatSignUtils;

@Slf4j
@RestController
@RequestMapping("/wechat")
public class WechatController {

  private WechatSignUtils wechatSignUtils;

  private WechatCommandService wechatCommandService;

  public WechatController(
      WechatSignUtils wechatSignUtils, WechatCommandService wechatCommandService) {
    this.wechatSignUtils = wechatSignUtils;
    this.wechatCommandService = wechatCommandService;
  }

  @GetMapping("/access")
  @ApiOperation("服务器绑定接口")
  public String boundDomainService(
      String signature, String timestamp, String nonce, @RequestParam("echostr") String randomStr) {
    if (wechatSignUtils.checkSignature(signature, timestamp, nonce)) {
      log.info("微信服务器绑定成功");
      return randomStr;
    } else {
      log.error("微信URL校验失败,发现非法请求");
      return "ERROR";
    }
  }

  @PostMapping(
      value = "/access",
      consumes = MediaType.TEXT_XML_VALUE,
      produces = MediaType.TEXT_XML_VALUE)
  @ApiOperation("处理微信消息")
  public WechatResponseTextMessage handleWechatMessage(@RequestBody WechatMessage message) {
    return wechatCommandService.handleMessage(message);
  }
}
