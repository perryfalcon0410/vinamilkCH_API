package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.ShopDTO;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionProductReportDTO {

    private Date fromDate;

    private Date toDate;

    private Date reportDate = new Date();

    private Integer totalQuantity;

    private  Float totalPrice;

    private ShopDTO shop;

    Set<PromotionProductCatDTO> productCats;

    public PromotionProductReportDTO (Date fromDate, Date toDate, ShopDTO shop) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.shop = shop;
    }

    public void addProductCat(PromotionProductCatDTO productCat) {
        if(productCats == null) productCats = new HashSet<>();
        this.productCats.add(productCat);
    }

}
