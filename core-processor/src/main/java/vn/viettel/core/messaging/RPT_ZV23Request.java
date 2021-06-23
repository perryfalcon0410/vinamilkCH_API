package vn.viettel.core.messaging;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Api(value = "Thông tin cập nhật bảng rpt_zv23 của bán hàng")
public class RPT_ZV23Request extends BaseRequest{

    private Long promotionProgramId;

    private String promotionProgramCode;

//    private LocalDateTime fromDate;
//
//    private LocalDateTime toDate;

    private Long shopId;

    private Long customerId;

    private Double totalAmount;

}
