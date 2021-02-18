package vn.viettel.core.db.entity.commonEnum;

import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public enum CustomJPAType {
    STRING(1, StandardBasicTypes.STRING),
    LONG(2, StandardBasicTypes.LONG),
    BYTE(3, StandardBasicTypes.BYTE),
    DOUBLE(4, StandardBasicTypes.DOUBLE),
    ;

    private long id;

    private Type type;

    CustomJPAType(long id, Type type) {
        this.id = id;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public Type getType() {
        return type;
    }
}
