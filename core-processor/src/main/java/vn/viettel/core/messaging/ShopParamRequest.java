package vn.viettel.core.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopParamRequest extends BaseRequest{

    private String shopId;

    private String type;

    private String code;

    private String name;

    private String description;

    private Integer status;
}
