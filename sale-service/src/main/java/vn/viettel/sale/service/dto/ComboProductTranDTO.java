package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComboProductTranDTO extends BaseDTO {

    private Long shopId;

    private String transCode;

    private Date transDate;

    private Integer transType;

    private String note;

    private Long wareHouseTypeId;

    private Float totalQuantity;

    private Float totalAmount;

    private String createUser;

    List<ComboProductTransDetailDTO> comboProducts;

}
