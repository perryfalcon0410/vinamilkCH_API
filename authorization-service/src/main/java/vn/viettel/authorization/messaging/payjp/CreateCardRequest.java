package vn.viettel.authorization.messaging.payjp;

import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CreateCardRequest {

    @NotNull(responseMessage = ResponseMessage.CUSTOMER_NUMBER_MUST_BE_NOT_NULL)
    private String customerNumber;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_CARD_HOLDER_NAME_MUST_BE_NOT_BLANK)
    private String cardName;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_CARD_NUMBER_MUST_BE_NOT_BLANK)
    private String cardNumber;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_CARD_EXP_MONTH_MUST_BE_NOT_BLANK)
    private String expMonth;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_CARD_EXP_YEAR_MUST_BE_NOT_BLANK)
    private String expYear;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_CARD_CVC_MUST_BE_NOT_BLANK)
    private String cvv;

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
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

    public String getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(String expMonth) {
        this.expMonth = expMonth;
    }

    public String getExpYear() {
        return expYear;
    }

    public void setExpYear(String expYear) {
        this.expYear = expYear;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public Map<String, Object> getCardParams() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("number", cardNumber);
        params.put("cvc", cvv);
        params.put("exp_month", expMonth);
        params.put("exp_year", expYear);
        return params;
    }

}
