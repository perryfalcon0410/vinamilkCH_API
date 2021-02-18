package vn.viettel.authorization.service.dto.user;

public class ShopEmployeeUserInfoDTO {

    private String email;
    private int numberOfDateExpiration;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNumberOfDateExpiration() {
        return numberOfDateExpiration;
    }

    public void setNumberOfDateExpiration(int numberOfDateExpiration) {
        this.numberOfDateExpiration = numberOfDateExpiration;
    }
}
