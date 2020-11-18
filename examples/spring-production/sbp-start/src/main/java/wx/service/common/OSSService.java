package wx.service.common;

import wx.dto.version.WASTVersionInfo;

/**
 * 提供 OSS 上传与下载相关内容
 *
 * @author wxyyxc1992
 */
public interface OSSService {

  /** 获取应用版本信息 */
  WASTVersionInfo getWASTVersionInfo();
}
