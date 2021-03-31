package vn.viettel.core;

public enum ResponseMessage {

    SUCCESSFUL(200, "OK"),
    CHANGE_PASSWORD_SUCCESSFUL(201, "Bạn đã thay đổi mật khẩu thành công"),
    CHANGE_PASSWORD_FAIL(202, "Thay đổi mật khẩu that bai"),
    CONFIRM_PASSWORD_NOT_CORRECT(203, "wrong confirm password"),

    SESSION_EXPIRED(408, "Session Expired"),
    DATA_NOT_FOUND(404, "Data not found"),

    UNKNOWN(5001, "Unknown"),
    NULL_POINTER_EXCEPTION(5002, "Null pointer exception"),
    SYSTEM_ERROR(5003, "system error"),

    // 1xxx - Authentication failed
    INVALID_TOKEN(1001, "Invalid Token."),
    NOT_AUTHORIZED(1002, "UnAuthorized."),
    NOT_EXISTS_EMAIL(1003, "Not exists email."),
    LOGIN_FAILED(1004, "Not correct email or password"),
    PRODUCT_NOT_FOUND(1005, "product not found"),
    FORBIDDEN(1006, "Forbidden"),

    // 2xxx - Data validation failed
    DATA_TYPE_ERROR(2000, "Data type not correct."), INVALID_BODY(2001, "Invalid body."),
    VALIDATED_ERROR(2002, "Validated error."),

    // 3xxx - Not found
    NOT_FOUND(3001, "Not Found."), NOT_EXISTS(3002, "Not Exists."),

    // 4xxx - Data manipulation failed
    NO_CONTENT(4000, "No Content."),
    CREATE_FAILED(4001, "Create failed."),
    UPDATE_FAILED(4002, "Update failed."),
    DELETE_FAILED(4003, "Delete failed."),
    NO_CONTENT_PASSED(4005, "No Content Passed."),
    DUPLICATE_PASSWORD(4006, "duplicate with old password"),


    // 5xxx - connection failed
    CONNECT_DATABASE_FAILED(5000, "Connect database failed."),
    /**
     * USER: 6000 -> 6999
     */
    USER_DOES_NOT_EXISTS(6000, "user does not exists"),
    USER_EMAIL_MUST_BE_NOT_NULL(6001, "email must be not null"),
    USER_PASSWORD_MUST_BE_NOT_NULL(6002, "password must be not null"),
    USER_ID_MUST_BE_NOT_NULL(6002, "id must be not null"),
    ADDRESS_DOES_NOT_EXIST(6003, "address not exist"),
    USER_NAME_MUST_NOT_BE_NULL(6004, "name must be not null"),
    ID_CARD_ALREADY_EXIST(6005, "IDCard already exist"),
    ID_CARD_DOES_NOT_EXIST(6006, "IDCard not exist"),
    ID_CARD_ALREADY_BELONG_TO_OTHER_PEOPLE(6007, "not your id card"),
    USER_TOKEN_MUST_BE_NOT_NULL(6008, "token must be not null"),
    USER_ACTIVATION_TOKEN_HAS_EXPIRED(6009, "the activation token has expired"),
    USER_FORGOT_PASSWORD_TOKEN_HAS_EXPIRED(6010, "the forgot password token has expired"),
    WAREHOUSE_TYPE_ID_MUST_NOT_BE_NUll(6011, "warehouse id must not be null"),
    PASSWORD_MUST_NOT_BE_NULL(6165, "password must not be null"),
    MEMBER_CARD_ALREADY_BELONG_TO_OTHER_PEOPLE(6012, "not your member card"),
    MEMBER_CARD_CODE_HAVE_EXISTED(6020, "Member card code have existed"),
    SALE_ORDER_TYPE_MUST_NOT_BE_NULL(6013, "sale order type must not be null"),
    SHOP_NOT_FOUND(6014, "shop not found"),
    USER_OLD_PASSWORD_MUST_BE_NOT_NULL(6015, "oldPassword must be not null"),
    USER_OLD_PASSWORD_NOT_CORRECT(6016, "old password not correct"),
    USER_PASSWORD_IS_EXPIRED(6017, "user password is expired"),
    USER_NAME_MUST_BE_NOT_BLANK(6018, "name must be not blank"),
    USER_EMAIL_MUST_BE_NOT_BLANK(6019, "email must be not blank"),
    NOT_YOUR_COMPANY(6050, "not your company"),
    STOCK_NOT_FOUND(6051, "stock not found"),
    USER_UPDATE_FAILED(6052, "user update failed"),
    PRODUCT_OUT_OF_STOCK(6100, "not enough product to serve"),
    INVALID_USERNAME_OR_PASSWORD(6156, "wrong username or password"),
    USER_ROLE_MUST_BE_NOT_BLANK(6160, "role  must be not blank"),
    USER_STATUS_MUST_BE_NOT_BLANK(6161, "status  must be not blank"),
    MEMBER_CARD_NOT_EXIST(6162, "member card not exist"),
    ID_MUST_NOT_BE_NULL(6164, "required id must not be null"),
    USER_IS_NOT_ACTIVE(6165, "USER_IS_NOT_ACTIVE"),
    SALE_ORDER_TYPE_NOT_EXIST(6166, "sale order type not exist"),
    WARE_HOUSE_NOT_EXIST(6167, "warehouse not exist"),
    RECEIPT_ONLINE_NOT_EXIST(6167, "receipt online not exist"),
    SHOP_NOT_MATCH(6168, "user not have permission on this shop"),
    USER_ROLE_NOT_MATCH(6169, "user not have this role"),
    SHOP_ID_MUST_NOT_BE_NULL(6168, "shop id must not be null"),
    TOTAL_PAID_MUST_NOT_BE_NULL(6170, "customer total paid must not be null"),
    TYPE_NOT_BE_NULL(6171, "must define type sale or return"),
    SALE_ORDER_TYPE_NOT_BE_NULL(6172, "order type must not be null"),
    REQUEST_BODY_NOT_BE_NULL(6173, "request body must not be null"),
    NO_PRICE_APPLIED(6174, "no price was applied"),
    /**
     * CUSTOMER MESSAGE 40000 -> 40999
     */
    CUSTOMER_LAST_NAME_MUST_BE_NOT_BLANK(40000, "Customer Last Name must be required"),
    CUSTOMER_FIRST_NAME_MUST_BE_NOT_BLANK(40001, "Customer First Name must be required"),
    CUSTOMER_ADDRESS_MUST_NOT_BE_NULL(40002, "customer address must not be null"),
    CUSTOMER_STATUS_MUST_BE_NOT_NULL(40003, "CUSTOMER_STATUS_MUST_BE_NOT_NULL"),
    CUSTOMER_CODE_MUST_BE_NOT_BLANK(40004, "CUSTOMER_CODE_MUST_BE_NOT_BLANK"),
    CUSTOMER_CODE_HAVE_EXISTED(40005, "CUSTOMER_CODE_HAVE_EXISTED"),
    CUSTOMER_IS_NOT_EXISTED(40006, "CUSTOMER_IS_NOT_EXISTED"),
    CUSTOMER_IDS_MUST_BE_NOT_NULL(40007, "CUSTOMER_IDS_MUST_BE_NOT_NULL"),
    CUSTOMER_DOES_NOT_EXIST(40008, "Customer does not exist"),
    CUSTOMER_CARD_EXP_YEAR_MUST_BE_NOT_BLANK(40009, "expYear must be not blank"),
    CUSTOMER_CARD_EXP_MONTH_MUST_BE_NOT_BLANK(40010, "expMonth must be not blank"),
    CUSTOMER_CARD_HAS_EXIST(40011, "card has exist"),
    CUSTOMER_NUMBER_MUST_BE_NOT_NULL(40012, "customerNumber must be not null"),
    CUSTOMER_NAME_MUST_BE_NOT_BLANK(40013, "name must be must blank"),
    CUSTOMER_INFORMATION_DOES_NOT_EXIST(40014, "customer information does not exist"),
    CUSTOMER_INFORMATION_PHONE_MUST_BE_NOT_NULL(40015, "phone must be not null"),
    CUSTOMER_INFORMATION_GENDER_MUST_BE_NOT_NULL(40016, "gender must be not null"),
    CUSTOMER_CARD_HOLDER_NAME_MUST_BE_NOT_BLANK(40017, "cardHolderName must be not blank"),
    CUSTOMER_CARD_NUMBER_MUST_BE_NOT_BLANK(40018, "cardNumber must be not blank"),
    CUSTOMER_NOT_EXIST(40019, "customer dose not exist"),
    CUSTOMER_ID_MUST_BE_NOT_NULL(40020, "CUSTOMER_IDS_MUST_BE_NOT_NULL"),
    /**
     * COMPANY 20000 -> 20999
     */
    COMPANY_FEATURE_NOT_AVAILABLE(20099, "The feature of this company has been disabled. Please contact admin."),

    /**
     *
     */
    DELIVERY_TYPE_MUST_BE_NOT_NULL(7001, "id must be not null"),
    PAYMENT_METHOD_MUST_BE_NOT_NULL(7003, "name must be not blank"),
    PAYMENT_FAIL(7004,"PAYMENT_FAIL"),
    /**
     * MANAGEMENT USER MESSAGE 45000 -> 45999
     */
    MANAGEMENT_USER_DOES_NOT_EXISTS(45000, "management users does not exists"),

    IDENTITY_CARD_CODE_HAVE_EXISTED(65000, "IDENTITY_CARD_CODE_HAVE_EXISTED"),
    /**
     * SALE_ORDER 46000-46999
     */
    SALE_ORDER_DETAIL_DOES_NOT_EXISTS(46000, "SALE ORDER DETAIL DOES NOT EXISTS"),
    PO_TRANS_IS_NOT_EXISTED(46001, "PO_TRANS_IS_NOT_EXISTED"),
    STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED(46002, "STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED"),
    STOCK_BORROWING_TRANS_IS_NOT_EXISTED(46003, "STOCK_BORROWING_TRANS_IS_NOT_EXISTED"),
    PROMOTION_DOSE_NOT_EXISTS(46004,"PROMOTION_DOES_NOT_EXISTS"),
    /**
     * Voucher 47000-47999
     */
    VOUCHER_DOES_NOT_EXISTS(47000, "Voucher does not exist"),
    VOUCHER_CODE_MUST_BE_NOT_BLANK(47001, "Voucher code must be not blank"),
    VOUCHER_CODE_HAVE_EXISTED(47002, "Voucher code have existed"),
    VOUCHER_NAME_MUST_BE_NOT_BLANK(47003, "Voucher name must be not blank"),
    VOUCHER_SERIAL_MUST_BE_NOT_BLANK(47004, "Voucher serial must be not blank"),

    ;

    private final int statusCode;
    private final String statusCodeValue;

    private ResponseMessage(int statusCode, String statusCodeValue) {
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
