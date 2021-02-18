package vn.viettel.authorization.service.dto;

public class ManagementPrivilegeDTO {
    private Long id;
    private String name;
    private Boolean isSuperadmin;

    public String getName() {
        return name.toUpperCase();
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsSuperadmin() {
        return isSuperadmin;
    }

    public void setIsSuperadmin(Boolean isSuperadmin) {
        this.isSuperadmin = isSuperadmin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ManagementPrivilegeDTO() {
    }

    public ManagementPrivilegeDTO(Long id, String name, Boolean isSuperadmin) {
        this.id = id;
        this.name = name;
        this.isSuperadmin = isSuperadmin;
    }
}
