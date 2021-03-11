package vn.viettel.commonservice.Service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
//@AllArgsConstructor
public class CountryDto {
    private long id;
    private String name;

    public CountryDto(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
