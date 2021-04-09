package vn.viettel.core;

public enum ResponseMessage {

    SUCCESSFUL(200, "OK"),
    CREATED(201, "CREATED"),
    ACCEPTED(202, "ACCEPTED"),
    NON_AUTHORITATIVE_INFORMATION(203, "NON_AUTHORITATIVE_INFORMATION"),

    SESSION_EXPIRED(408, "SESSION_EXPIRED"),
    DATA_NOT_FOUND(404, "DATA_NOT_FOUND"),

    UNKNOWN(5001, "UNKNOWN"),
    NULL_POINTER_EXCEPTION(5002, "NULL_POINTER_EXCEPTION"),
    SYSTEM_ERROR(5003, "SYSTEM_ERROR"),

    // 1xxx - Authentication failed
    INVALID_TOKEN(1001, "INVALID_TOKEN"),
    NOT_AUTHORIZED(1002, "NOT_AUTHORIZED"),
    NOT_EXISTS_EMAIL(1003, "NOT_EXISTS_EMAIL"),
    LOGIN_FAILED(1004, "LOGIN_FAILED"),
    PRODUCT_NOT_FOUND(1005, "PRODUCT_NOT_FOUND"),
    STOCK_COUNTING_NOT_FOUND(1006, "STOCK_COUNTING_NOT_FOUND"),
    PRODUCT_INFO_NOT_FOUND(1007, "PRODUCT_INFO_NOT_FOUND"),
    FORBIDDEN(1008, "FORBIDDEN"),

    // 2xxx - Data validation failed
    DATA_TYPE_ERROR(2000, "DATA_TYPE_ERROR"),
    INVALID_BODY(2001, "INVALID_BODY"),
    VALIDATED_ERROR(2002, "VALIDATED_ERROR"),
    NOT_AN_EXCEL_FILE(2003, "NOT_AN_EXCEL_FILE"),

    // 3xxx - Not found
    NOT_FOUND(3001, "NOT_FOUND"),
    NOT_EXISTS(3002, "NOT_EXISTS"),

    // 4xxx - Data manipulation failed
    NO_CONTENT(4000, "NO_CONTENT"),
    CREATE_FAILED(4001, "CREATE_FAILED"),
    UPDATE_FAILED(4002, "UPDATE_FAILED"),
    DELETE_FAILED(4003, "DELETE_FAILED"),
    NO_CONTENT_PASSED(4005, "NO_CONTENT_PASSED"),
    DUPLICATE_PASSWORD(4006, "DUPLICATE_PASSWORD"),
    INCORRECT_PASSWORD(4007, "INCORRECT_PASSWORD"),
    INVALID_PASSWORD_LENGTH(4008, "INVALID_PASSWORD_LENGTH"),
    INVALID_PASSWORD_FORMAT(4009, "INVALID_PASSWORD_FORMAT"),

    // 5xxx - connection failed
    CONNECT_DATABASE_FAILED(5000, "CONNECT_DATABASE_FAILED"),
    /**
     * USER: 6000 -> 6999
     */
    USER_DOES_NOT_EXISTS(6000, "USER_DOES_NOT_EXISTS"),
    USER_EMAIL_MUST_BE_NOT_NULL(6001, "USER_EMAIL_MUST_BE_NOT_NULL"),
    USER_PASSWORD_MUST_BE_NOT_NULL(6002, "USER_PASSWORD_MUST_BE_NOT_NULL"),
    USER_ID_MUST_BE_NOT_NULL(6002, "USER_ID_MUST_BE_NOT_NULL"),
    ADDRESS_DOES_NOT_EXIST(6003, "ADDRESS_DOES_NOT_EXIST"),
    USER_NAME_MUST_NOT_BE_NULL(6004, "USER_NAME_MUST_NOT_BE_NULL"),
    ID_CARD_ALREADY_EXIST(6005, "ID_CARD_ALREADY_EXIST"),
    ID_CARD_DOES_NOT_EXIST(6006, "ID_CARD_DOES_NOT_EXIST"),
    ID_CARD_ALREADY_BELONG_TO_OTHER_PEOPLE(6007, "ID_CARD_ALREADY_BELONG_TO_OTHER_PEOPLE"),
    USER_TOKEN_MUST_BE_NOT_NULL(6008, "USER_TOKEN_MUST_BE_NOT_NULL"),
    USER_ACTIVATION_TOKEN_HAS_EXPIRED(6009, "USER_ACTIVATION_TOKEN_HAS_EXPIRED"),
    USER_FORGOT_PASSWORD_TOKEN_HAS_EXPIRED(6010, "USER_FORGOT_PASSWORD_TOKEN_HAS_EXPIRED"),
    WAREHOUSE_TYPE_ID_MUST_NOT_BE_NUll(6011, "WAREHOUSE_TYPE_ID_MUST_NOT_BE_NUll"),
    PASSWORD_MUST_NOT_BE_NULL(6165, "PASSWORD_MUST_NOT_BE_NULL"),
    MEMBER_CARD_ALREADY_BELONG_TO_OTHER_PEOPLE(6012, "MEMBER_CARD_ALREADY_BELONG_TO_OTHER_PEOPLE"),
    MEMBER_CARD_CODE_HAVE_EXISTED(6020, "MEMBER_CARD_CODE_HAVE_EXISTED"),
    PHONE_HAVE_EXISTED(6020, "PHONE_HAVE_EXISTED"),
    SALE_ORDER_TYPE_MUST_NOT_BE_NULL(6013, "SALE_ORDER_TYPE_MUST_NOT_BE_NULL"),
    SHOP_NOT_FOUND(6014, "SHOP_NOT_FOUND"),
    USER_OLD_PASSWORD_MUST_BE_NOT_NULL(6015, "USER_OLD_PASSWORD_MUST_BE_NOT_NULL"),
    USER_OLD_PASSWORD_NOT_CORRECT(6016, "USER_OLD_PASSWORD_NOT_CORRECT"),
    USER_PASSWORD_IS_EXPIRED(6017, "USER_PASSWORD_IS_EXPIRED"),
    USER_NAME_MUST_BE_NOT_BLANK(6018, "USER_NAME_MUST_BE_NOT_BLANK"),
    USER_EMAIL_MUST_BE_NOT_BLANK(6019, "USER_EMAIL_MUST_BE_NOT_BLANK"),
    MEMBER_CUSTOMER_NOT_EXIT(6021, "MEMBER_CUSTOMER_NOT_EXIT"),
    NOT_YOUR_COMPANY(6050, "NOT_YOUR_COMPANY"),
    STOCK_NOT_FOUND(6051, "STOCK_NOT_FOUND"),
    USER_UPDATE_FAILED(6052, "USER_UPDATE_FAILED"),
    PRODUCT_OUT_OF_STOCK(6100, "PRODUCT_OUT_OF_STOCK"),
    INVALID_USERNAME_OR_PASSWORD(6156, "INVALID_USERNAME_OR_PASSWORD"),
    USER_ROLE_MUST_BE_NOT_BLANK(6160, "USER_ROLE_MUST_BE_NOT_BLANK"),
    USER_STATUS_MUST_BE_NOT_BLANK(6161, "USER_STATUS_MUST_BE_NOT_BLANK"),
    MEMBER_CARD_NOT_EXIST(6162, "MEMBER_CARD_NOT_EXIST"),
    ID_MUST_NOT_BE_NULL(6164, "ID_MUST_NOT_BE_NULL"),
    USER_IS_NOT_ACTIVE(6165, "USER_IS_NOT_ACTIVE"),
    SALE_ORDER_TYPE_NOT_EXIST(6166, "SALE_ORDER_TYPE_NOT_EXIST"),
    WARE_HOUSE_NOT_EXIST(6167, "WARE_HOUSE_NOT_EXIST"),
    RECEIPT_ONLINE_NOT_EXIST(6167, "RECEIPT_ONLINE_NOT_EXIST"),
    SHOP_NOT_MATCH(6168, "SHOP_NOT_MATCH"),
    USER_ROLE_NOT_MATCH(6169, "USER_ROLE_NOT_MATCH"),
    SHOP_ID_MUST_NOT_BE_NULL(6168, "SHOP_ID_MUST_NOT_BE_NULL"),
    TOTAL_PAID_MUST_NOT_BE_NULL(6170, "TOTAL_PAID_MUST_NOT_BE_NULL"),
    TYPE_NOT_BE_NULL(6171, "TYPE_NOT_BE_NULL"),
    SALE_ORDER_TYPE_NOT_BE_NULL(6172, "SALE_ORDER_TYPE_NOT_BE_NULL"),
    REQUEST_BODY_NOT_BE_NULL(6173, "REQUEST_BODY_NOT_BE_NULL"),
    NO_PRICE_APPLIED(6174, "NO_PRICE_APPLIED"),
    SHOP_IS_NOT_ACTIVE(6175, "SHOP_IS_NOT_ACTIVE"),
    NEW_PASSWORD_MUST_BE_NOT_NULL(6176, "NEW_PASSWORD_MUST_BE_NOT_NULL"),
    CONFIRM_PASSWORD_MUST_BE_NOT_NULL(6176, "CONFIRM_PASSWORD_MUST_BE_NOT_NULL"),
    CONFIRM_PASSWORD_NOT_CORRECT(6177, "CONFIRM_PASSWORD_NOT_CORRECT"),
    CHANGE_PASSWORD_FAIL(6178, "CHANGE_PASSWORD_FAIL"),
    NO_FUNCTIONAL_PERMISSION(6179, "NO_FUNCTIONAL_PERMISSION"),
    NO_PRIVILEGE_ON_ANY_SHOP(6180, "NO_PRIVILEGE_ON_ANY_SHOP"),
    NO_PERMISSION_ASSIGNED(6181, "NO_PERMISSION_ASSIGNED"),
    USER_HAVE_NO_ROLE(6182, "USER_HAVE_NO_ROLE"),
    WRONG_CAPTCHA(6183, "WRONG_CAPTCHA"),
    ENTER_CAPTCHA_TO_LOGIN(6184, "ENTER_CAPTCHA_TO_LOGIN"),
    USER_HAVE_NO_PRIVILEGE_ON_THIS_SHOP(6185, "USER_HAVE_NO_PRIVILEGE_ON_THIS_SHOP"),
    /**
     * CUSTOMER: 7000 -> 7999
     */
    CUSTOMER_LAST_NAME_MUST_BE_NOT_BLANK(7000, "Customer Last Name must be required"),
    CUSTOMER_FIRST_NAME_MUST_BE_NOT_BLANK(7001, "Customer First Name must be required"),
    CUSTOMER_ADDRESS_MUST_NOT_BE_NULL(7002, "customer address must not be null"),
    CUSTOMER_STATUS_MUST_BE_NOT_NULL(7003, "CUSTOMER_STATUS_MUST_BE_NOT_NULL"),
    CUSTOMER_CODE_MUST_BE_NOT_BLANK(7004, "CUSTOMER_CODE_MUST_BE_NOT_BLANK"),
    CUSTOMER_CODE_HAVE_EXISTED(7005, "CUSTOMER_CODE_HAVE_EXISTED"),
    CUSTOMER_IS_NOT_EXISTED(7006, "CUSTOMER_IS_NOT_EXISTED"),
    CUSTOMER_IDS_MUST_BE_NOT_NULL(7007, "CUSTOMER_IDS_MUST_BE_NOT_NULL"),
    CUSTOMER_DOES_NOT_EXIST(7008, "CUSTOMER_DOES_NOT_EXIST"),
    CUSTOMER_CARD_EXP_YEAR_MUST_BE_NOT_BLANK(7009, "CUSTOMER_CARD_EXP_YEAR_MUST_BE_NOT_BLANK"),
    CUSTOMER_CARD_EXP_MONTH_MUST_BE_NOT_BLANK(7010, "CUSTOMER_CARD_EXP_MONTH_MUST_BE_NOT_BLANK"),
    CUSTOMER_CARD_HAS_EXIST(7011, "CUSTOMER_CARD_HAS_EXIST"),
    CUSTOMER_NUMBER_MUST_BE_NOT_NULL(7012, "CUSTOMER_NUMBER_MUST_BE_NOT_NULL"),
    CUSTOMER_NAME_MUST_BE_NOT_BLANK(7013, "CUSTOMER_NAME_MUST_BE_NOT_BLANK"),
    CUSTOMER_INFORMATION_DOES_NOT_EXIST(7014, "CUSTOMER_INFORMATION_DOES_NOT_EXIST"),
    CUSTOMER_INFORMATION_PHONE_MUST_BE_NOT_NULL(7015, "CUSTOMER_INFORMATION_PHONE_MUST_BE_NOT_NULL"),
    CUSTOMER_INFORMATION_GENDER_MUST_BE_NOT_NULL(7016, "CUSTOMER_INFORMATION_GENDER_MUST_BE_NOT_NULL"),
    CUSTOMER_CARD_HOLDER_NAME_MUST_BE_NOT_BLANK(7017, "CUSTOMER_CARD_HOLDER_NAME_MUST_BE_NOT_BLANK"),
    CUSTOMER_CARD_NUMBER_MUST_BE_NOT_BLANK(7018, "CUSTOMER_CARD_NUMBER_MUST_BE_NOT_BLANK"),
    CUSTOMER_NOT_EXIST(7019, "CUSTOMER_NOT_EXIST"),
    CUSTOMER_ID_MUST_BE_NOT_NULL(7020, "CUSTOMER_IDS_MUST_BE_NOT_NULL"),
    CUSTOMER_DOES_NOT_EXIST_IN_RPT_CUS_MEM_AMOUNT(7021, "CUSTOMER_DOES_NOT_EXIST_IN_RPT_CUS_MEM_AMOUNT"),
    AREA_NOT_EXISTS(7022, "AREA_NOT_EXISTS"),
    CUSTOMER_TYPE_NOT_EXISTS(7023, "CUSTOMER_TYPE_NOT_EXISTS"),
    GENDER_NOT_EXISTS(7024, "GENDER_NOT_EXISTS"),
    PRECINCT_NOT_EXITS(7025, "PRECINCT_NOT_EXITS"),
    DISTRICT_NOT_EXITS(7026, "DISTRICT_NOT_EXITS"),
    PROVINCE_NOT_EXITS(7027, "PROVINCE_NOT_EXITS"),
    CARD_TYPE_NOT_EXISTS(7028, "CARD_TYPE_NOT_EXISTS"),
    CLOSELY_TYPE_NOT_EXISTS(7029, "CLOSELY_TYPE_NOT_EXISTS"),
    INVALID_PHONE_NUMBER(7030, "INVALID_PHONE_NUMBER"),



    /**
     * COMPANY 8000 -> 8999
     */
    COMPANY_FEATURE_NOT_AVAILABLE(8000, "COMPANY_FEATURE_NOT_AVAILABLE"),

    /**
     * SALE: 9000 - 9999
     */
    DELIVERY_TYPE_MUST_BE_NOT_NULL(9001, "DELIVERY_TYPE_MUST_BE_NOT_NULL"),
    PAYMENT_METHOD_MUST_BE_NOT_NULL(9002, "PAYMENT_METHOD_MUST_BE_NOT_NULL"),
    PAYMENT_FAIL(9003,"PAYMENT_FAIL"),
    SALE_ORDER_DETAIL_DOES_NOT_EXISTS(9004, "SALE ORDER DETAIL DOES NOT EXISTS"),
    PO_TRANS_IS_NOT_EXISTED(9005, "PO_TRANS_IS_NOT_EXISTED"),
    STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED(9006, "STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED"),
    STOCK_BORROWING_TRANS_IS_NOT_EXISTED(9007, "STOCK_BORROWING_TRANS_IS_NOT_EXISTED"),
    PROMOTION_DOSE_NOT_EXISTS(9008,"PROMOTION_DOES_NOT_EXISTS"),
    SALE_ORDER_ID_MUST_NOT_BE_NULL(9009,"SALE_ORDER_ID_MUST_NOT_BE_NULL"),
    SALE_ORDER_NUMBER_MUST_NOT_BE_NULL(9010,"SALE_ORDER_NUMBER_MUST_NOT_BE_NULL"),
    DATE_RETURN_MUST_NOT_BE_NULL(9011,"DATE_RETURN_MUST_NOT_BE_NULL"),
    REASON_MUST_NOT_BE_NULL(9012,"REASON_MUST_NOT_BE_NULL"),
    ORDER_RETURN_DOES_NOT_EXISTS(9013,"ORDER_RETURN_DOES_NOT_EXISTS"),
    CREATE_USER_MUST_NOT_BE_NULL(9014, "CREATE_USER_MUST_NOT_BE_NULL"),
    PRODUCT_INFO_NOT_EXISTS(9009, "PRODUCT_INFO_NOT_EXITS"),
    ORDER_ONLINE_NOT_FOUND(9010,"ORDER_ONLINE_NOT_FOUND"),
    AP_PARAM_NOT_EXISTS(9011,"AP_PARAM_NOT_EXITS"),
    CATEGORY_DATA_NOT_EXISTS(9012,"CATEGORY_DATA_NOT_EXISTS"),
    RED_INVOICE_DETAIL_NOT_EXISTS(9013,"RED_INVOICE_DETAIL_NOT_EXISTS"),
    NO_PRODUCT_TO_ORDER(9014,"NO_PRODUCT_TO_ORDER"),
    ORDER_EXPIRED_FOR_RETURN(9015, "ORDER_EXPIRED_FOR_RETURN"),
    /**
     * MANAGEMENT USER MESSAGE 10000 -> 10999
     */
    MANAGEMENT_USER_DOES_NOT_EXISTS(10000, "MANAGEMENT_USER_DOES_NOT_EXISTS"),
    IDENTITY_CARD_CODE_HAVE_EXISTED(10001, "IDENTITY_CARD_CODE_HAVE_EXISTED"),

    /**
     * Voucher 11000-11999
     */
    VOUCHER_DOES_NOT_EXISTS(11000, "VOUCHER_DOES_NOT_EXISTS"),
    VOUCHER_CODE_MUST_BE_NOT_BLANK(11001, "VOUCHER_CODE_MUST_BE_NOT_BLANK"),
    VOUCHER_CODE_HAVE_EXISTED(11002, "VOUCHER_CODE_HAVE_EXISTED"),
    VOUCHER_NAME_MUST_BE_NOT_BLANK(11003, "VOUCHER_NAME_MUST_BE_NOT_BLANK"),
    VOUCHER_SERIAL_MUST_BE_NOT_BLANK(11004, "VOUCHER_SERIAL_MUST_BE_NOT_BLANK"),

    ;

    private final int statusCode;
    private final String statusCodeValue;

    ResponseMessage(int statusCode, String statusCodeValue) {
        this.statusCode = statusCode;
        this.statusCodeValue = statusCodeValue;
    }

    public int statusCode() {
        return statusCode;
    }

    public String statusCodeValue() {
        return statusCodeValue;
    }

    public static ResponseMessage getByStatus(int statusCode) {
        for (ResponseMessage e : values()) {
            if (e.statusCode == statusCode) return e;
        }
        return null;
    }

}
