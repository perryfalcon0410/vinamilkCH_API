package vn.viettel.promotion.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class VoucherDTO extends BaseDTO {

    private String voucherProgramCode;

    private String voucherProgramName;

    private String voucherCode;

    private String voucherName;

    private String serial;

    private Float price;

    private Long voucherProgramId;

    private  String activeTime;

}
