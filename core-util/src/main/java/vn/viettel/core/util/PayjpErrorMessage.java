package vn.viettel.core.util;

import vn.viettel.core.ResponseMessage;

public class PayjpErrorMessage {

    public static ResponseMessage getErrorByCode(String code) {
        ResponseMessage error = null;
        switch (code) {
            case "invalid_number":
                error = ResponseMessage.PAYJP_CARD_INVALID_NUMBER;
                break;
            case "invalid_cvc":
                error = ResponseMessage.PAYJP_CARD_INVALID_CVC;
                break;
            case "invalid_expiration_date":
                error = ResponseMessage.PAYJP_CARD_INVALID_EXPIRATION_DATE;
                break;
            case "invalid_expiry_month":
                error = ResponseMessage.PAYJP_CARD_INVALID_EXPIRY_MONTH;
                break;
            case "invalid_expiry_year":
                error = ResponseMessage.PAYJP_CARD_INVALID_EXPIRY_YEAR;
                break;
            case "expired_card":
                error = ResponseMessage.PAYJP_CARD_EXPIRED;
                break;
            case "card_declined":
                error = ResponseMessage.PAYJP_CARD_DECLINED;
                break;
            case "unacceptable_brand":
                error = ResponseMessage.PAYJP_CARD_UNACCEPTABLE_BRAND;
                break;
            case "invalid_card_name":
                error = ResponseMessage.PAYJP_CARD_INVALID_CARD_HOLDER_NAME;
                break;
            case "invalid_card":
                error = ResponseMessage.PAYJP_CARD_INVALID;
                break;
            case "processing_error":
                error = ResponseMessage.PAYJP_CARD_PROCESSING_ERROR;
                break;
        }
        return error;
    }

}
