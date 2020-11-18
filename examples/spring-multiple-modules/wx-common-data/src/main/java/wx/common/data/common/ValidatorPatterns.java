package wx.common.data.common;

/** 字段校验常量. */
public interface ValidatorPatterns {

  // 手机号码校验 1开头 第二位只允许（3,4,5,7,8）剩下9位随意(0-9)
  String PHONE_NUMBER = "^1(3|4|5|7|8)\\d{9}$";

  // 用户名校验，大小写字母，数字，中文 长度4-16位
  String USER_NAME = "^[a-zA-Z0-9\\u4E00-\\u9FA5]{4,20}$";

  // 6-12位，必须包含大写字母，小写字母和数字
  String PASSWORD = "^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{6,20}$";

  // 电子邮箱地址校验
  String EMAIL = "^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$";

  // 工单格式校验
  String WORK_ORDER_CODE = "20[0-9]{2}\\d{8}";
}
