package vn.viettel.core.dto.customer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
public class CustomerTypeDTO extends BaseDTO {
    private String code;
    private String name;
    private Integer kaCustomerType;
    private Integer posModifyCustomer;
    private Long wareHoseTypeId;
    private Integer status;
}
