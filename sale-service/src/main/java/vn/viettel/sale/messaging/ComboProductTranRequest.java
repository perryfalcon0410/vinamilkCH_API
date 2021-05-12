package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Request tạo mới xuất nhập combo")
public class ComboProductTranRequest extends BaseRequest {

    @ApiModelProperty(notes = "Ngày giao dịch")
    private Date transDate;

    @ApiModelProperty(notes = "Loại giao dịch: 1 nhập combo, 2 xuất combo")
    private Integer transType;

    @ApiModelProperty(notes = "Ghi chú")
    private String note;

    @ApiModelProperty(notes = "Danh sách sản phẩm combo")
    List<ComboProductTranDetailRequest> details;
}
