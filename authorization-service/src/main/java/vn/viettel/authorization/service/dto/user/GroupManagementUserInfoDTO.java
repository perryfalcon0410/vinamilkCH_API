package vn.viettel.authorization.service.dto.user;

public class GroupManagementUserInfoDTO {

    private GroupManagerUserInfoDTO groupManager;
    private GroupEmployeeUserInfoDTO groupEmployee;

    public GroupManagerUserInfoDTO getGroupManager() {
        return groupManager;
    }

    public void setGroupManager(GroupManagerUserInfoDTO groupManager) {
        this.groupManager = groupManager;
    }

    public GroupEmployeeUserInfoDTO getGroupEmployee() {
        return groupEmployee;
    }

    public void setGroupEmployee(GroupEmployeeUserInfoDTO groupEmployee) {
        this.groupEmployee = groupEmployee;
    }
}
