package vn.viettel.authorization.service.dto;

import vn.viettel.core.db.entity.Role;

import java.util.List;

public class LoginResponse {
    private String username;
    private String phoneNumber;
    private String email;
    private String DOB;
    private String firstName;
    private String lastName;
    private boolean active;
    private String lastLoginDate;
    private List<String> roles;
    private List<FunctionResponse> functions;

    public LoginResponse() {}

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public List<FunctionResponse> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionResponse> functions) {
        this.functions = functions;
    }
}
