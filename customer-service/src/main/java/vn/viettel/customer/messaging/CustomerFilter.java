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
    @ApiModelProperty(value = "Từ khóa")
    private String searchKeywords;
    private Date fromDate;
    private Date toDate;
    private Long customerTypeId;
    private Long status;
    private Long genderId;
    private Long areaId;
    private String phone;
    private String idNo;
    private Long shopId;
}