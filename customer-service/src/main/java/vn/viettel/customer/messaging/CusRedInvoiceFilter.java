package vn.viettel.customer.messaging;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Api(tags = "Thông tin tìm kiếm khách hàng tạo mới hóa đơn đỏ")
public class CusRedInvoiceFilter {
    @ApiModelProperty(value = "Họ và tên, mã khách hàng")
    private String searchKeywords;
    @ApiModelProperty(value = "Số điện thoại")
    private String mobiphone;
    @ApiModelProperty(value = "Tên đơn vị")
    private String workingOffice;
    @ApiModelProperty(value = "Địa chỉ cơ quan")
    private String officeAddress;
    @ApiModelProperty(value = "Mã số thuế")
    private String taxCode;
}
