package wx.infra.common.data;

import java.time.LocalDate;
import lombok.Data;

@Data
public class BatchConfigPrinterInfo {

  private String utkPrinterCode;

  private String utkPrinterName;

  private LocalDate storageDate;

  private LocalDate warrantyExpirationDate;

  private Boolean variableSpot;

  private String materialName;

  private String bpName;
}
