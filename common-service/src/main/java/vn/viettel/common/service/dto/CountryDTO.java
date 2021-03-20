package vn.viettel.common.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Setter
@Getter
@NoArgsConstructor
public class CountryDTO extends BaseDTO {
    private String name;
}
