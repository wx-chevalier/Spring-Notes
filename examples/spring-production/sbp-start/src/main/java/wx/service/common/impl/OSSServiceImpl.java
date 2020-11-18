package wx.service.common.impl;

import com.aliyun.oss.OSSClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.springframework.stereotype.Service;
import wx.context.properties.AliyunProperty;
import wx.context.properties.ApplicationProperty;
import wx.dto.version.WASTVersionInfo;
import wx.service.common.OSSService;
import wx.utils.JSONUtils;

@Service
public class OSSServiceImpl implements OSSService {
  private AliyunProperty aliyunProperty;
  private ObjectMapper objectMapper;

  public OSSServiceImpl(ApplicationProperty applicationProperty, ObjectMapper objectMapper) {
    this.aliyunProperty = applicationProperty.getAliyun();
    this.objectMapper = objectMapper;
  }

  @Override
  public WASTVersionInfo getWASTVersionInfo() {
    return JSONUtils.readJSON(
        new BufferedReader(
            new InputStreamReader(
                new OSSClient(
                        aliyunProperty.getEndpoint(),
                        aliyunProperty.getAccessKeyId(),
                        aliyunProperty.getAccessKeySecret())
                    // TODO: put into configuration
                    .getObject("cncs-archives", "index.json")
                    .getObjectContent(),
                Charset.forName("utf8"))),
        WASTVersionInfo.class,
        objectMapper);
  }
}
