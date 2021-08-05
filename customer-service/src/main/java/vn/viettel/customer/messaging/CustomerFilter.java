package vn.viettel.customer.messaging;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Api(tags = "Thông tin tìm kiếm")
public class CustomerFilter extends BaseRequest {
    @ApiModelProperty(value = "Họ và tên, mã khách hàng")
    private String searchKeywords;
    @ApiModelProperty(value = "Id loại khách hàng")
    private Long customerTypeId;
    @ApiModelProperty(value = "Trạng thái, 1-Hoạt động, 0-Ngưng hoạt động")
    private Integer status;
    @ApiModelProperty(value = "Id giới tính")
    private Long genderId;
    @ApiModelProperty(value = "Id khu vực")
    private Long areaId;
    @ApiModelProperty(value = "Số điện thoại di động")
    private String phone;
    @ApiModelProperty(value = "Cmnd")
    private String idNo;
    @ApiModelProperty(value = "Id shop đang login")
    private Long shopId;
    @ApiModelProperty(value = "Khách hàng của cửa hàng: shop-true/all-false")
    private Boolean isShop;
}