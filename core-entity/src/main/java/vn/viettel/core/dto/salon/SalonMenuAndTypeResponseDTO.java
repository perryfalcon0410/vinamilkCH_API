package vn.viettel.core.dto.salon;

import vn.viettel.core.db.entity.SalonMenuType;

import java.util.List;

public class SalonMenuAndTypeResponseDTO {
    private List<SalonMenuType> menuTypes;

    private List<SalonMenuResponseDTO> menus;

    public List<SalonMenuType> getMenuTypes() {
        return menuTypes;
    }

    public void setMenuTypes(List<SalonMenuType> menuTypes) {
        this.menuTypes = menuTypes;
    }

    public List<SalonMenuResponseDTO> getMenus() {
        return menus;
    }

    public void setMenus(List<SalonMenuResponseDTO> menus) {
        this.menus = menus;
    }
}
