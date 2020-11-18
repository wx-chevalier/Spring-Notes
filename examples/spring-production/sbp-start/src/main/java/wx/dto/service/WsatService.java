package wx.dto.service;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "WSAT Service")
public class WsatService {
  WsatServivceName name;

  String status;

  public enum WsatServivceName {
    sys,
    store,
    agent,
    product,
    asset,
    crawler,
    scheduler,
    poc
  }
}
