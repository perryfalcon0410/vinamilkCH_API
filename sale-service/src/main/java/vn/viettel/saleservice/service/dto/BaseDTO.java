package vn.viettel.saleservice.service.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public  class BaseDTO {
    private long id;
    private LocalDateTime createdAt;
    private long createdBy;
    private LocalDateTime updatedAt;
    private long updatedBy;
    private LocalDateTime deletedAt;
    private long deletedBy;


}
