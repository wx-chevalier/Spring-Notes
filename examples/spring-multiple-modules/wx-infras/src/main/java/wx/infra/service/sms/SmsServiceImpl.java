package wx.infra.service.sms;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import wx.infra.converter.JsonConverter;

@Slf4j
@Service("smsService")
public class SmsServiceImpl implements SmsService {

  private final String SMS_DOMAIN = "dysmsapi.aliyuncs.com";

  private final String SMS_VERSION = "2017-05-25";

  private final String SMS_ACTION = "SendSms";

  @Autowired private AliSmsSetting aliSmsSetting;

  @Override
  public void send(String phoneNumber, String templateId, Map<String, String> param) {
    if (!StringUtils.hasText(phoneNumber) || phoneNumber.length() < 11) {
      log.info("发送短信失败，手机号码：{}不合法", phoneNumber);
      return;
    }

    DefaultProfile profile =
        DefaultProfile.getProfile(
            aliSmsSetting.getRegionId(),
            aliSmsSetting.getAccessKeyId(),
            aliSmsSetting.getAccessSecret());
    IAcsClient client = new DefaultAcsClient(profile);

    CommonRequest request = new CommonRequest();
    request.setMethod(MethodType.POST);
    request.setDomain(SMS_DOMAIN);
    request.setVersion(SMS_VERSION);
    request.setAction(SMS_ACTION);
    request.putQueryParameter("RegionId", aliSmsSetting.getRegionId());
    // 移除形式+86 的前缀
    request.putQueryParameter("PhoneNumbers", phoneNumber.substring(phoneNumber.length() - 11));
    request.putQueryParameter("SignName", aliSmsSetting.getSignName());
    request.putQueryParameter("TemplateCode", templateId);
    request.putQueryParameter("TemplateParam", JsonConverter.toJSONString(param));
    try {
      CommonResponse commonResponse = client.getCommonResponse(request);
      log.info("发送短信到:{} 响应结果:{}", phoneNumber, commonResponse.getData());
    } catch (Exception e) {
      log.info("发送短信到:{}数据:{} 失败,异常原因:{}", phoneNumber, request.toString(), e.getMessage());
      log.error("==>", e);
    }
  }
}
