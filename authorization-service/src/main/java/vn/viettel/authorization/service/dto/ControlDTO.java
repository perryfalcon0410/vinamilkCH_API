package vn.viettel.authorization.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ControlDTO {
    private Long id;

    private String controlCode;

    private String controlName;

    private Long formId;

    private String description;

    private Integer showStatus = 2;

}
