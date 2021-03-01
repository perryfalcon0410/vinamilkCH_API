package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "shop_menu_setting_item_setting")
public class ShopMenuSettingItemSetting extends BaseEntity {

    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "item_id")
    private Long itemId;

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}
