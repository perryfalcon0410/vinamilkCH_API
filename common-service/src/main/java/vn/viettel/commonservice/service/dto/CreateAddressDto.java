package vn.viettel.commonservice.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateAddressDto {
    private String address;
    private long wardId;
}
