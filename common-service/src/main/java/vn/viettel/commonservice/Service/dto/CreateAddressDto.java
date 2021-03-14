package vn.viettel.commonservice.Service.dto;

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
