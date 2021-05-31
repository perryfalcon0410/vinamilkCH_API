package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.promotion.PromotionProgramDetailDTO;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.MaxTextLength;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AutoPromotionDTO {
    @ApiModelProperty(notes = "Tiền được giảm")
    private Float discountAmount;
    @ApiModelProperty(notes = "Danh sách sản phẩm khuyến mãi")
    private List<ZmFreeItemDTO> freeItems = new ArrayList<>();
    @ApiModelProperty(notes = "Chi tiết chương trính khuyến mãi")
    private List<PromotionProgramDetailDTO> listPromotion = new ArrayList<>();
}
