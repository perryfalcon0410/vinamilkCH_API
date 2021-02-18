package vn.viettel.authorization.service.dto.customer;

import java.time.Month;
import java.time.Year;

public class CustomerCardDTO {

    private String cardType;
    private String cardName;
    private String cardNumber;
    private Year expYear;
    private Month expMonth;

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Year getExpYear() {
        return expYear;
    }

    public void setExpYear(Year expYear) {
        this.expYear = expYear;
    }

    public Month getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(Month expMonth) {
        this.expMonth = expMonth;
    }

}
