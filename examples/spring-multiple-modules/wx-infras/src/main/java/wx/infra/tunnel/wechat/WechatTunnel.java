package wx.infra.tunnel.wechat;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import wx.domain.wechat.WechatAccessToken;
import wx.domain.wechat.WechatQrCodeTicket;
import wx.domain.wechat.WechatUser;
import wx.infra.common.data.wechat.req.WechatQrCodeReq;
import wx.infra.common.data.wechat.req.WechatSendTemplateMessageReq;
import wx.infra.common.data.wechat.resp.WechatAccessTokenResp;
import wx.infra.common.data.wechat.resp.WechatQrCodeTicketResp;
import wx.infra.common.data.wechat.resp.WechatSendMessageResp;
import wx.infra.common.data.wechat.resp.WechatUserResp;
import wx.infra.common.exception.BadRequestException;
import wx.infra.converter.JsonConverter;

@Slf4j
@Component
public class WechatTunnel {

  private static final String WECHAT_SERVICE = "https://api.weixin.qq.com";

  // 获取AccessToken 地址
  private static final String ACCESS_TOKEN_URL = WECHAT_SERVICE + "/cgi-bin/token";

  // 获取二维码凭据地址
  private static final String QR_CODE_URL = WECHAT_SERVICE + "/cgi-bin/qrcode/create";

  // 获取用户信息接口
  private static final String GET_USER_INFO = WECHAT_SERVICE + "/cgi-bin/user/info";

  // 微信模板消息发送地址
  private static final String TEMPLATE_MESSAGE_URL =
      WECHAT_SERVICE + "/cgi-bin/message/template/send";

  private static CloseableHttpClient httpClient;

  private static RequestConfig requestConfig =
      RequestConfig.custom()
          .setSocketTimeout(5000)
          .setConnectTimeout(5000)
          .setConnectionRequestTimeout(5000)
          .build();

  static {
    PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
    connManager.setMaxTotal(300);
    connManager.setDefaultMaxPerRoute(300);
    httpClient = HttpClients.custom().setConnectionManager(connManager).build();
  }

  /** 请求微信服务器获取AccessToken */
  public static WechatAccessToken getAccessToken(String appId, String secret) {
    StringBuilder paramBuffer = new StringBuilder();
    paramBuffer
        .append("grant_type=client_credential")
        .append("&")
        .append("appid=")
        .append(appId)
        .append("&")
        .append("secret=")
        .append(secret);
    try {
      String result = get(ACCESS_TOKEN_URL, paramBuffer.toString());

      WechatAccessTokenResp resp = JsonConverter.readJSON(result, WechatAccessTokenResp.class);
      Long errorCode =
          Optional.ofNullable(resp).map(WechatAccessTokenResp::getErrorCode).orElse(null);
      if (errorCode != null && !Objects.equals(errorCode, 0L)) {
        log.error("请求获取微信AccessToken,出现问题,错误内容: 【{}】", result);
        throw new BadRequestException("请求获取微信AccessToken,出现问题");
      }

      WechatAccessToken accessToken = null;
      if (resp != null) {
        accessToken = new WechatAccessToken();
        BeanUtils.copyProperties(resp, accessToken);
      }
      return accessToken;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /** 请求微信服务器获取二维码凭证 */
  public static WechatQrCodeTicket getQrCodeTicket(
      String accessToken, WechatQrCodeReq wechatQrCodeReq) {
    String url = QR_CODE_URL + "?access_token=" + accessToken;
    String result = post(url, JsonConverter.toJSONString(wechatQrCodeReq));

    WechatQrCodeTicketResp resp = JsonConverter.readJSON(result, WechatQrCodeTicketResp.class);
    Long errorCode =
        Optional.ofNullable(resp).map(WechatQrCodeTicketResp::getErrorCode).orElse(null);
    if (errorCode != null && !Objects.equals(errorCode, 0L)) {
      log.error("请求获取二维码凭据错误,响应内容: 【{}】", result);
      throw new BadRequestException("获取微信二维码凭据失败");
    }
    WechatQrCodeTicket ticket = null;
    if (resp != null) {
      ticket = new WechatQrCodeTicket();
      BeanUtils.copyProperties(resp, ticket);
    }
    return ticket;
  }

  /** 请求微信服务器获取用户信息 */
  public static WechatUser getUserInfo(String accessToken, String appId, String unionId) {

    String paramBuffer = "access_token=" + accessToken + "&openid=" + unionId + "&lang=zh_CN";
    String result = get(GET_USER_INFO, paramBuffer);

    WechatUserResp wechatUserResp = JsonConverter.readJSON(result, WechatUserResp.class);
    Long errorCode =
        Optional.ofNullable(wechatUserResp).map(WechatUserResp::getErrorCode).orElse(null);
    if (errorCode != null && !Objects.equals(errorCode, 0L)) {
      log.error("获取用户信息失败,响应内容: 【{}】", result);
      throw new BadRequestException("获取微信用户信息失败");
    }

    WechatUser wechatUser = null;
    if (wechatUserResp != null) {
      wechatUser = new WechatUser();
      BeanUtils.copyProperties(wechatUserResp, wechatUser);
    }
    return wechatUser;
  }

  /** 发送微信模板消息 */
  public static void sendTemplateMessage(
      String accessToken, String dst, String wechatTemplateId, Map<String, String> param) {
    String url = TEMPLATE_MESSAGE_URL + "?access_token=" + accessToken;
    WechatSendTemplateMessageReq req = new WechatSendTemplateMessageReq();
    req.setTouser(dst).setTemplateId(wechatTemplateId);

    Map<String, WechatSendTemplateMessageReq.WechatTemplateMessageValue> dataList =
        new HashMap<>(param.size());
    for (Map.Entry<String, String> valueEntry : param.entrySet()) {
      String value = valueEntry.getValue();
      if (StringUtils.isEmpty(value)) {
        continue;
      }
      WechatSendTemplateMessageReq.WechatTemplateMessageValue messageValue =
          WechatSendTemplateMessageReq.WechatTemplateMessageValue.create(valueEntry.getValue());
      dataList.put(valueEntry.getKey(), messageValue);
    }
    req.setData(dataList);

    String result = post(url, JsonConverter.toJSONString(req));
    WechatSendMessageResp wechatSendMessageResp =
        JsonConverter.readJSON(result, WechatSendMessageResp.class);
    Long errorCode =
        Optional.ofNullable(wechatSendMessageResp)
            .map(WechatSendMessageResp::getErrorCode)
            .orElse(null);
    if (errorCode != null && !Objects.equals(errorCode, 0L)) {
      log.info("发送模板消息出现问题,响应内容: 【{}】", result);
      throw new BadRequestException("发送微信模板消息失败");
    }
  }

  private static String get(String url, String param) {
    log.info("微信服务请求:url={} params={}", url, param);
    HttpGet httpget = new HttpGet(url + "?" + param);
    httpget.setConfig(requestConfig);
    try {
      CloseableHttpResponse response = httpClient.execute(httpget);
      String resultStr = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
      log.info("请求响应结果:{}", resultStr);
      return resultStr;
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  private static String post(String url, String params) {
    log.info("微信服务请求:url={} params={}", url, params);
    HttpPost httpPost = new HttpPost(url);
    httpPost.setHeader("Accept", MediaType.APPLICATION_JSON_VALUE);
    httpPost.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    StringEntity entity = new StringEntity(params, StandardCharsets.UTF_8);
    httpPost.setEntity(entity);
    try {
      CloseableHttpResponse response = httpClient.execute(httpPost);
      String responseStr = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
      log.info("请求响应结果:{}", responseStr);
      return responseStr;
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
