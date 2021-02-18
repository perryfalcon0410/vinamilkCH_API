package vn.viettel.core.dto;

public class ManagementUserNameResponseDTO {
    private Long managementUserId;
    private String name;

    public ManagementUserNameResponseDTO() {
    }

    public ManagementUserNameResponseDTO(Long managementUserId, String name) {
        this.managementUserId = managementUserId;
        this.name = name;
    }

    public Long getManagementUserId() {
        return managementUserId;
    }

    public void setManagementUserId(Long managementUserId) {
        this.managementUserId = managementUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
