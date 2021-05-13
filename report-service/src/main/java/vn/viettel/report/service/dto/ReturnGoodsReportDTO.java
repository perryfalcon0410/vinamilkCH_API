package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.ShopDTO;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReturnGoodsReportDTO {

    private Date fromDate;

    private Date toDate;

    private Date reportDate = new Date();

    private ShopDTO shop;

    List<ReturnGoodsCatDTO> returnGoodsCatDTOS;

    public ReturnGoodsReportDTO(Date fromDate, Date toDate, ShopDTO shop) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.shop = shop;
    }

}
