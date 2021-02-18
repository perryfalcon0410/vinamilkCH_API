package vn.viettel.authorization.messaging.customer;

import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

import java.time.Month;
import java.time.Year;

public class CustomerCardUpdateRequest extends BaseRequest {

    @NotNull(responseMessage = ResponseMessage.CUSTOMER_CARD_HOLDER_NAME_MUST_BE_NOT_BLANK)
    private String cardName;

    @NotNull(responseMessage = ResponseMessage.CUSTOMER_CARD_NUMBER_MUST_BE_NOT_BLANK)
    private Long cardNumber;

    @NotNull(responseMessage = ResponseMessage.CUSTOMER_CARD_CVC_MUST_BE_NOT_BLANK)
    private Long cvv;

    @NotNull(responseMessage = ResponseMessage.CUSTOMER_CARD_EXP_YEAR_MUST_BE_NOT_BLANK)
    private Year expYear;

    @NotNull(responseMessage = ResponseMessage.CUSTOMER_CARD_EXP_MONTH_MUST_BE_NOT_BLANK)
    private Month expMonth;

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public Long getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(Long cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Long getCvv() {
        return cvv;
    }

    public void setCvv(Long cvv) {
        this.cvv = cvv;
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
