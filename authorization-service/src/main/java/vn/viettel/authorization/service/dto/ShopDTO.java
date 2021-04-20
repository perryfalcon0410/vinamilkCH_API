package vn.viettel.authorization.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopDTO {
    private Long id;
    private String shopName;
    private String address;
    private String phone;
    private String email;
    private boolean isEditable;
    private boolean isManuallyCreatable;

    public ShopDTO(Long id, String shopName, String address, String phone, String email) {
        this.id = id;
        this.shopName = shopName;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }
}
