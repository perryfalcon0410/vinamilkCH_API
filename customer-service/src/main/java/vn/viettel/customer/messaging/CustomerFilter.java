package vn.viettel.customer.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.messaging.BaseRequest;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CustomerFilter extends BaseRequest {
    private String searchKeywords;
    private Date fromDate;
    private Date toDate;
    private Long customerTypeId;
    private Long status;
    private Long genderId;
    private Long areaId;
    private String phone;
    private String idNo;
}