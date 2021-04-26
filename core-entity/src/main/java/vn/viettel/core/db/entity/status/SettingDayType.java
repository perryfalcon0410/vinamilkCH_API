package vn.viettel.core.db.entity.status;

import vn.viettel.core.util.status.Validatable;

public enum SettingDayType implements Validatable {

    SHOP(1), ITEM(2);

    private int typeId;

    SettingDayType(int typeId) {
        this.typeId = typeId;
    }

    public int typeId() {
        return this.typeId;
    }

    public int value() {
        return this.typeId;
    }

    @Override
    public String validateValue() {
        return String.valueOf(this.typeId);
    }

    public static SettingDayType getById(int id) {
        for (SettingDayType type : values()) {
            if (type.typeId == id) {
                return type;
            }
        }
        return null;
    }

}
