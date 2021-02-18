package vn.viettel.authorization.service.dto;

public class AdminPrivilegeDTO {
    private Long id;
    private String privilegeName;
    private boolean isSuperAdmin;

    public AdminPrivilegeDTO() {
    }

    public AdminPrivilegeDTO(Long id, String privilegeName, boolean isSuperAdmin) {
        this.id = id;
        this.privilegeName = privilegeName;
        this.isSuperAdmin = isSuperAdmin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrivilegeName() {
        return privilegeName.toUpperCase();
    }


    public void setPrivilegeName(String privilegeName) {
        this.privilegeName = privilegeName;
    }

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        isSuperAdmin = superAdmin;
    }
}
