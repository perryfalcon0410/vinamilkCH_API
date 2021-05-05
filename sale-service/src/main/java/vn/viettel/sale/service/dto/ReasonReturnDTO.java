package vn.viettel.sale.service.dto;

import liquibase.pro.packaged.S;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReasonReturnDTO {
    private String apCode;
    private String apName;
    private String value;
}
