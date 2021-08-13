package vn.viettel.customer.messaging;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Api(value = "Thông tin tìm khách hàng ở màn hình bán hàng")
public class CustomerSaleFilter {

    @ApiModelProperty(notes = "searchKeywords")
    private String searchKeywords;

    @ApiModelProperty(notes = "Tìm kiếm theo khách hàng của cửa hàng")
    private boolean customerOfShop = true;

    @ApiModelProperty(value = "Chỉ tìm theo số điện thoại")
    private boolean searchPhoneOnly = true;
}
