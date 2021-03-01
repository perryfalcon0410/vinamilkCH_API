package vn.viettel.core.dto.user;

public class AdminUserResponseDTO {
    private Long serviceAdminUserId;

    private String serviceAdminUserName;

    public AdminUserResponseDTO() {
    }

    public AdminUserResponseDTO(Long serviceAdminUserId, String serviceAdminUserName) {
        this.serviceAdminUserId = serviceAdminUserId;
        this.serviceAdminUserName = serviceAdminUserName;
    }

    public Long getServiceAdminUserId() {
        return serviceAdminUserId;
    }

    public void setServiceAdminUserId(Long serviceAdminUserId) {
        this.serviceAdminUserId = serviceAdminUserId;
    }

    public String getServiceAdminUserName() {
        return serviceAdminUserName;
    }

    public void setServiceAdminUserName(String serviceAdminUserName) {
        this.serviceAdminUserName = serviceAdminUserName;
    }
}
