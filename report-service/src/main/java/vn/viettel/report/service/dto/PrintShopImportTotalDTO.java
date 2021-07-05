package vn.viettel.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PrintShopImportTotalDTO {
    private String type;
    private Integer totalQuantity;
    private Float totalAmount;
    private List<orderImportDTO> orderImport;
}
