package vn.viettel.core.db.entity;

import vn.viettel.core.db.entity.status.ObjectShop;
import vn.viettel.core.db.entity.status.converter.ObjectShopConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "group_shop")
public class GroupShop extends BaseEntity {

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Convert(converter = ObjectShopConverter.class)
    @Column(name = "object_shop", nullable = false)
    private ObjectShop objectShop;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public ObjectShop getObjectShop() {
        return objectShop;
    }

    public void setObjectShop(ObjectShop objectShop) {
        this.objectShop = objectShop;
    }
}
