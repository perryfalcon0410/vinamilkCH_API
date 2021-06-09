package vn.viettel.core.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControlDTO {
    private Long id;
    private String controlCode;
    private String controlName;
    private int status;
    private String showStatus;
}
