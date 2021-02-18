package vn.viettel.authorization.service.dto.user;

public class ShopManagementUsersInfoDTO {

    private ShopManagerUserInfoDTO shopManager;
    private ShopEmployeeUserInfoDTO shopEmployee;

    public ShopManagerUserInfoDTO getShopManager() {
        return shopManager;
    }

    public void setShopManager(ShopManagerUserInfoDTO shopManager) {
        this.shopManager = shopManager;
    }

    public ShopEmployeeUserInfoDTO getShopEmployee() {
        return shopEmployee;
    }

    public void setShopEmployee(ShopEmployeeUserInfoDTO shopEmployee) {
        this.shopEmployee = shopEmployee;
    }
}
