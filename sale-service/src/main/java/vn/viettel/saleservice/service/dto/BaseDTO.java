package vn.viettel.saleservice.service.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
@NoArgsConstructor
public  class BaseDTO {
    private long id;
    private Timestamp createdAt;
    private long createdBy;
    private Timestamp updatedAt;
    private long updatedBy;
    private Timestamp deletedAt;
    private long deletedBy;


}
