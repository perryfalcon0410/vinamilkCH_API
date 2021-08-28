package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.ShopDTO;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReturnGoodsReportDTO {

    private LocalDate fromDate;

    private LocalDate toDate;

    private LocalDate reportDate = LocalDate.now();

    private ShopDTO shop;

    List<ReturnGoodsCatDTO> returnGoodsCatDTOS;

    public ReturnGoodsReportDTO(LocalDate fromDate, LocalDate toDate, ShopDTO shop) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.shop = shop;
    }

}
