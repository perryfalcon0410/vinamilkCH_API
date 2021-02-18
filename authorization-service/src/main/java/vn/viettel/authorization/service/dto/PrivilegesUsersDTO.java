package vn.viettel.authorization.service.dto;

import vn.viettel.core.db.entity.ManagementPrivilege;
import vn.viettel.core.db.entity.ManagementUsers;
import vn.viettel.core.service.dto.BaseDTO;

public class PrivilegesUsersDTO extends BaseDTO {
    private ManagementUsers managementUsers;

    public ManagementUsers getManagementUsers() {
        return managementUsers;
    }

    public void setManagementUsers(ManagementUsers managementUsers) {
        this.managementUsers = managementUsers;
    }

    public ManagementPrivilege getManagementPrivilege() {
        return managementPrivilege;
    }

    public void setManagementPrivilege(ManagementPrivilege managementPrivilege) {
        this.managementPrivilege = managementPrivilege;
    }

    private ManagementPrivilege managementPrivilege;
}
