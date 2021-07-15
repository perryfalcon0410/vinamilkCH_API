package vn.viettel.authorization.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FormDTO {

    private Long id;

    private String formCode;

    private String formName;

    private Long parentFormId;

    private Integer type;

    private String url;

    private String description;

    private Integer orderNumber;

    List<FormDTO> subForms;

    List<ControlDTO> controls;

}
