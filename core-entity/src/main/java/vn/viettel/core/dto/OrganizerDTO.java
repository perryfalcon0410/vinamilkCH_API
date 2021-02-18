package vn.viettel.core.dto;

public class OrganizerDTO {

    private String name;

    private Long object;

    private Long objectId;

    public OrganizerDTO() {
    }

    public OrganizerDTO(String name, Long object, Long objectId) {
        this.name = name;
        this.object = object;
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getObject() {
        return object;
    }

    public void setObject(Long object) {
        this.object = object;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }
}
