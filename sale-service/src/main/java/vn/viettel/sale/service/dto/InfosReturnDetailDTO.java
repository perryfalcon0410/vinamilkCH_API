package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class InfosReturnDetailDTO {
    private Date orderDate;
    private String CustomerName;
    private long reasonId;
    private String reasonDesc;
    private Date returnDate;
    private String userName;
    private String note;
}
