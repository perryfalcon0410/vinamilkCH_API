package vn.viettel.core.db.entity.status;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TenantStatus {

	@JsonProperty("PENDING")
    PENDING(0),
	@JsonProperty("SUCCESS")
    SUCCESS(1), 
	@JsonProperty("FAILED")
    FAILED(2);


    private int id;

    TenantStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static TenantStatus valueOf(int id) {
        for (TenantStatus status : values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        return null;
    }

    public static TenantStatus getByName(String name) {
        for (TenantStatus status : values()) {
            if (status.name().equals(name)) {
                return status;
            }
        }
        return null;
    }

}
