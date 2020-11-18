package wx.controller.sys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import wx.dto.sys.network.WsatNetworkInterfaceInput;
import wx.service.sys.NetworkService;

@RestController
@RequestMapping("/network")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class NetworkController {

  @Autowired NetworkService networkService;

  @PostMapping("/interfaces")
  public ResponseEntity getInterfaces() {
    return ResponseEntity.ok(networkService.getInterfaces());
  }

  @PostMapping("/interfaces-config")
  public ResponseEntity getInterfacesFromConfig() {
    return ResponseEntity.ok(networkService.getInterfacesFromConfig());
  }

  @PostMapping("/interface")
  public ResponseEntity updateInterface(@RequestBody WsatNetworkInterfaceInput interfaceInput) {

    Boolean resp = networkService.updateInterface(interfaceInput);

    if (resp) {
      return ResponseEntity.ok(true);
    } else {
      return ResponseEntity.ok(false);
    }
  }

  @PostMapping("/interface-config")
  public ResponseEntity updateInterfaceConfig(
      @RequestBody WsatNetworkInterfaceInput interfaceInput) {

    Boolean resp = networkService.updateInterfaceConfig(interfaceInput);

    if (resp) {
      return ResponseEntity.ok(true);
    } else {
      return ResponseEntity.ok(false);
    }
  }
}
