package vn.viettel.report.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.report.service.dto.PrintGoodDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrintGoods {
    private List<PrintGoodDTO> lstAdjust;
    private List<PrintGoodDTO> lstPo;
    private List<PrintGoodDTO> lstStock;
}
