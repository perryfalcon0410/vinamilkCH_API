package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class orderImportDTO {
    private String orderNumber;
    private LocalDateTime orderDate;
    private String poNumber;
    private String internalNumber;
    private String importNumber;
    private Integer orderQuantity;
    private Double orderTotal;
    private Double adjusted;
    private Double VAT;
    private Double totalAmount;
    List<PrintShopImportDTO> data;
}
