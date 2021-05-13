package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReturnGoodsCatDTO {

    private String industry;

    private String returnCode;

    private String reciept;

    private String fullName;

    List<ProductReturnGoodsReportDTO> productReturnGoodsReportDTOS;



}
