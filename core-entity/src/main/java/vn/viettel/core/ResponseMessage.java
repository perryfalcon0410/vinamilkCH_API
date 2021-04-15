package vn.viettel.core;

public enum ResponseMessage {

    SUCCESSFUL(200, "OK"),
    CREATED(201, "CREATED"),
    ACCEPTED(202, "ACCEPTED"),
    NON_AUTHORITATIVE_INFORMATION(203, "NON_AUTHORITATIVE_INFORMATION"),
    CREATE_CANCEL(203, "Hủy thêm mới"),
    CHANGE_PASSWORD_SUCCESS(204, "Bạn đã thay đổi mật khẩu thành công"),

    SESSION_EXPIRED(408, "SESSION_EXPIRED"),
    DATA_NOT_FOUND(404, "DATA_NOT_FOUND"),

    UNKNOWN(5001, "UNKNOWN"),
    NULL_POINTER_EXCEPTION(5002, "NULL_POINTER_EXCEPTION"),
    SYSTEM_ERROR(5003, "SYSTEM_ERROR"),

    // 1xxx - Authentication failed
    INVALID_TOKEN(1001, "Token không hợp lệ"),
    NOT_AUTHORIZED(1002, "Không có quyền"),
    NOT_EXISTS_EMAIL(1003, "Email không tồn tại"),
    LOGIN_FAILED(1004, "Đăng nhập thất bại"),
    STOCK_COUNTING_NOT_FOUND(1005, "Không tìm thấy phiếu kiểm kê"),
    FORBIDDEN(1006, "Không có quyền"),
    PRODUCT_INFO_NOT_FOUND(1007, "Không tìm thấy thông tin sản phẩm"),

    // 2xxx - Data validation failed
    DATA_TYPE_ERROR(2000, "Kiểu dữ liệu không hợp lệ"),
    INVALID_BODY(2001, "Không tìm thấy body"),
    VALIDATED_ERROR(2002, "VALIDATED_ERROR"),
    NOT_AN_EXCEL_FILE(2003, "Không phải file excel"),
    EMPTY_LIST(2004, "Danh sách rỗng"),
    INVENTORY_QUANTITY_MUST_NOT_BE_NULL(2005, "Số lượng kiểm kê không được phép null"),

    // 3xxx - Not found
    NOT_FOUND(3001, "Không tìm thấy dữ liệu"),
    NOT_EXISTS(3002, "Dữ liệu không tồn tại"),

    // 4xxx - Data manipulation failed
    NO_CONTENT(4000, "NO_CONTENT"),
    CREATE_FAILED(4001, "Tạo mới thất bại"),
    UPDATE_FAILED(4002, "Chỉnh sửa thất bại"),
    DELETE_FAILED(4003, "Xóa thất bại"),
    NO_CONTENT_PASSED(4005, "Không có dữ liệu truyền vào"),
    DUPLICATE_PASSWORD(4006, "Trùng mật khẩu"),
    INCORRECT_PASSWORD(4007, "Tên đăng nhập hoặc mật khẩu không đúng"),
    INVALID_PASSWORD_LENGTH(4008, "Độ dài mật khẩu không hợp lệ"),
    INVALID_PASSWORD_FORMAT(4009, "Mật khẩu thay đổi bắt buộc có chữ hoa, chữ thường, số và ký tự đặc biệt"),

    // 5xxx - connection failed
    CONNECT_DATABASE_FAILED(5000, "Kết nối cơ sở dữ liệu thất bại"),
    /**
     * USER: 6000 -> 6999
     */
    USER_DOES_NOT_EXISTS(6000, "Tên đăng nhập hoặc mật khẩu không đúng"),
    USER_EMAIL_MUST_BE_NOT_NULL(6001, "Email người dùng không được phép trống"),
    USER_PASSWORD_MUST_BE_NOT_NULL(6002, "Mật khẩu người dùng không được phép trống"),
    USER_ID_MUST_BE_NOT_NULL(6002, "id người dùng không được phép trống"),
    ADDRESS_DOES_NOT_EXIST(6003, "Địa chỉ không tồn tại"),
    USER_NAME_MUST_NOT_BE_NULL(6004, "Vui lòng nhập tên đăng nhập"),
    ID_CARD_ALREADY_EXIST(6005, "CMND đã tồn tại"),
    ID_CARD_DOES_NOT_EXIST(6006, "CMND không tồn tại"),
    ID_CARD_ALREADY_BELONG_TO_OTHER_PEOPLE(6007, "CMND thuộc sở hữu của người khác"),
    USER_TOKEN_MUST_BE_NOT_NULL(6008, "Token người dùng không dược phép trống"),
    USER_ACTIVATION_TOKEN_HAS_EXPIRED(6009, "Token đã hết hạn"),
    USER_FORGOT_PASSWORD_TOKEN_HAS_EXPIRED(6010, "USER_FORGOT_PASSWORD_TOKEN_HAS_EXPIRED"),
    WAREHOUSE_TYPE_ID_MUST_NOT_BE_NUll(6011, "WAREHOUSE_TYPE_ID_MUST_NOT_BE_NUll"),
    PASSWORD_MUST_NOT_BE_NULL(6165, "PASSWORD_MUST_NOT_BE_NULL"),
    MEMBER_CARD_ALREADY_BELONG_TO_OTHER_PEOPLE(6012, "MEMBER_CARD_ALREADY_BELONG_TO_OTHER_PEOPLE"),
    SALE_ORDER_TYPE_MUST_NOT_BE_NULL(6013, "SALE_ORDER_TYPE_MUST_NOT_BE_NULL"),
    SHOP_NOT_FOUND(6014, "Không tìm thấy cửa hàng"),
    USER_OLD_PASSWORD_MUST_BE_NOT_NULL(6015, "Vui lòng nhập mật khẩu cũ"),
    USER_OLD_PASSWORD_NOT_CORRECT(6016, "Mật khẩu cũ không chính xác"),
    USER_PASSWORD_IS_EXPIRED(6017, "USER_PASSWORD_IS_EXPIRED"),
    USER_NAME_MUST_BE_NOT_BLANK(6018, "USER_NAME_MUST_BE_NOT_BLANK"),
    USER_EMAIL_MUST_BE_NOT_BLANK(6019, "USER_EMAIL_MUST_BE_NOT_BLANK"),
    MEMBER_CARD_CODE_HAVE_EXISTED(6020, "MEMBER_CARD_CODE_HAVE_EXISTED"),
    PHONE_HAVE_EXISTED(6021, "Số điện thoại đã tồn tại"),
    MEMBER_CUSTOMER_NOT_EXIT(6022, "MEMBER_CUSTOMER_NOT_EXIT"),
    NOT_YOUR_COMPANY(6050, "NOT_YOUR_COMPANY"),
    STOCK_NOT_FOUND(6051, "STOCK_NOT_FOUND"),
    USER_UPDATE_FAILED(6052, "USER_UPDATE_FAILED"),
    PRODUCT_OUT_OF_STOCK(6100, "PRODUCT_OUT_OF_STOCK"),
    INVALID_USERNAME_OR_PASSWORD(6156, "INVALID_USERNAME_OR_PASSWORD"),
    USER_ROLE_MUST_BE_NOT_BLANK(6160, "USER_ROLE_MUST_BE_NOT_BLANK"),
    USER_STATUS_MUST_BE_NOT_BLANK(6161, "USER_STATUS_MUST_BE_NOT_BLANK"),
    MEMBER_CARD_NOT_EXIST(6162, "MEMBER_CARD_NOT_EXIST"),
    ID_MUST_NOT_BE_NULL(6164, "ID_MUST_NOT_BE_NULL"),
    USER_IS_NOT_ACTIVE(6165, "Nhân viên đang tạm ngưng hoạt động. Vui lòng liên hệ quản trị hệ thống để biết thêm thông tin"),
    SALE_ORDER_TYPE_NOT_EXIST(6166, "SALE_ORDER_TYPE_NOT_EXIST"),
    WARE_HOUSE_NOT_EXIST(6167, "WARE_HOUSE_NOT_EXIST"),
    RECEIPT_ONLINE_NOT_EXIST(6167, "RECEIPT_ONLINE_NOT_EXIST"),
    SHOP_NOT_MATCH(6168, "SHOP_NOT_MATCH"),
    USER_ROLE_NOT_MATCH(6169, "Tài khoản không có vai trò này"),
    SHOP_ID_MUST_NOT_BE_NULL(6168, "SHOP_ID_MUST_NOT_BE_NULL"),
    TOTAL_PAID_MUST_NOT_BE_NULL(6170, "TOTAL_PAID_MUST_NOT_BE_NULL"),
    TYPE_NOT_BE_NULL(6171, "TYPE_NOT_BE_NULL"),
    SALE_ORDER_TYPE_NOT_BE_NULL(6172, "SALE_ORDER_TYPE_NOT_BE_NULL"),
    REQUEST_BODY_NOT_BE_NULL(6173, "REQUEST_BODY_NOT_BE_NULL"),
    NO_PRICE_APPLIED(6174, "Không tìm thấy giá được áp dụng cho sản phẩm"),
    SHOP_IS_NOT_ACTIVE(6175, "Nhân viên thuộc cửa hàng đang tạm ngưng hoạt động. Vui lòng liên hệ quản trị hệ thống để biết thêm thông tin"),
    NEW_PASSWORD_MUST_BE_NOT_NULL(6176, "Vui lòng nhập mật khẩu mới"),
    CONFIRM_PASSWORD_MUST_BE_NOT_NULL(6176, "Vui lòng nhập mật khẩu xác nhận"),
    CONFIRM_PASSWORD_NOT_CORRECT(6177, "Mật khẩu và xác nhận mật khẩu phải giống nhau"),
    CHANGE_PASSWORD_FAIL(6178, "Đổi mật khẩu thất bại"),
    NO_FUNCTIONAL_PERMISSION(6179, "nhân viên rỗng persmission"),
    NO_PRIVILEGE_ON_ANY_SHOP(6180, "Tên đăng nhập chưa được gán quyền dữ liệu trên bất kì cửa hàng nào. Vui lòng liên hệ quản trị hệ thống để biết thêm thông tin"),
    NO_PERMISSION_ASSIGNED(6181, "Tên đăng nhập chưa được gán tập danh sách chức năng truy cập. Vui lòng liên hệ quản trị hệ thống để được hỗ trợ"),
    USER_HAVE_NO_ROLE(6182, "Tên đăng nhập chưa được gán vai trò. Vui lòng liên hệ quản trị hệ thống để biết thêm thông tin"),
    WRONG_CAPTCHA(6183, "Sai mã captcha"),
    ENTER_CAPTCHA_TO_LOGIN(6184, "Vui lòng nhập mã captcha"),
    USER_HAVE_NO_PRIVILEGE_ON_THIS_SHOP(6185, "Tên đăng nhập không được gán quyền trên cửa hàng này"),
    /**
     * CUSTOMER: 7000 -> 7999
     */
    CUSTOMER_LAST_NAME_MUST_BE_NOT_BLANK(7000, "Họ và tên đệm khách hàng không được để trống"),
    CUSTOMER_FIRST_NAME_MUST_BE_NOT_BLANK(7001, "Tên khách hàng không được để trống"),
    CUSTOMER_ADDRESS_MUST_NOT_BE_NULL(7002, "customer address must not be null"),
    CUSTOMER_STATUS_MUST_BE_NOT_NULL(7003, "CUSTOMER_STATUS_MUST_BE_NOT_NULL"),
    CUSTOMER_CODE_MUST_BE_NOT_BLANK(7004, "CUSTOMER_CODE_MUST_BE_NOT_BLANK"),
    CUSTOMER_CODE_HAVE_EXISTED(7005, "CUSTOMER_CODE_HAVE_EXISTED"),
    CUSTOMER_IS_NOT_EXISTED(7006, "CUSTOMER_IS_NOT_EXISTED"),
    CUSTOMER_IDS_MUST_BE_NOT_NULL(7007, "CUSTOMER_IDS_MUST_BE_NOT_NULL"),
    CUSTOMER_DOES_NOT_EXIST(7008, "Khách hàng không tồn tại"),
    CUSTOMER_CARD_EXP_YEAR_MUST_BE_NOT_BLANK(7009, "CUSTOMER_CARD_EXP_YEAR_MUST_BE_NOT_BLANK"),
    CUSTOMER_CARD_EXP_MONTH_MUST_BE_NOT_BLANK(7010, "CUSTOMER_CARD_EXP_MONTH_MUST_BE_NOT_BLANK"),
    CUSTOMER_CARD_HAS_EXIST(7011, "CUSTOMER_CARD_HAS_EXIST"),
    CUSTOMER_NUMBER_MUST_BE_NOT_NULL(7012, "CUSTOMER_NUMBER_MUST_BE_NOT_NULL"),
    CUSTOMER_NAME_MUST_BE_NOT_BLANK(7013, "CUSTOMER_NAME_MUST_BE_NOT_BLANK"),
    CUSTOMER_INFORMATION_DOES_NOT_EXIST(7014, "CUSTOMER_INFORMATION_DOES_NOT_EXIST"),
    CUSTOMER_INFORMATION_PHONE_MUST_BE_NOT_NULL(7015, "Số điện thoại không được bỏ trống"),
    CUSTOMER_INFORMATION_GENDER_MUST_BE_NOT_NULL(7016, "Giới tính không được bỏ trống"),
    CUSTOMER_CARD_HOLDER_NAME_MUST_BE_NOT_BLANK(7017, "CUSTOMER_CARD_HOLDER_NAME_MUST_BE_NOT_BLANK"),
    CUSTOMER_CARD_NUMBER_MUST_BE_NOT_BLANK(7018, "CUSTOMER_CARD_NUMBER_MUST_BE_NOT_BLANK"),
    CUSTOMER_ID_MUST_BE_NOT_NULL(7020, "Id khách hàng không được để trống"),
    CUSTOMER_DOES_NOT_EXIST_IN_RPT_CUS_MEM_AMOUNT(7021, "Khách hàng không có trong bảng prt_cus_mem_amount"),
    AREA_NOT_EXISTS(7022, "Địa bàn không tồn tại"),
    CUSTOMER_TYPE_NOT_EXISTS(7023, "Nhóm khách hàng không tồn tại"),
    GENDER_NOT_EXISTS(7024, "Giới tính không tồn tại"),
    PRECINCT_NOT_EXITS(7025, "Tỉnh/Thành phố không tồn tại"),
    DISTRICT_NOT_EXITS(7026, "Quận/Huyện không tồn tại"),
    PROVINCE_NOT_EXITS(7027, "Phường/Xã không tồn tại"),
    CARD_TYPE_NOT_EXISTS(7028, "Loại thẻ không tồn tại"),
    CLOSELY_TYPE_NOT_EXISTS(7029, "Loại khách hàng không tồn tại"),
    INVALID_PHONE_NUMBER(7030, "Số điện thoại không hợp lệ"),
    CUSTOMER_CREATE_FALE(7031, "Thêm mới khách hàng thất bại"),
    CUSTOMER_NOT_EXIST(7032, "CUSTOMER_NOT_EXIST"),



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
    PRODUCT_INFO_NOT_EXISTS(9009, "PRODUCT_INFO_NOT_EXITS"),
    ORDER_ONLINE_NOT_FOUND(9010,"ORDER_ONLINE_NOT_FOUND"),
    AP_PARAM_NOT_EXISTS(9011,"AP_PARAM_NOT_EXITS"),
    CATEGORY_DATA_NOT_EXISTS(9012,"CATEGORY_DATA_NOT_EXISTS"),
    RED_INVOICE_DETAIL_NOT_EXISTS(9013,"RED_INVOICE_DETAIL_NOT_EXISTS"),
    NO_PRODUCT_TO_ORDER(9014,"NO_PRODUCT_TO_ORDER"),
    SALE_ORDER_ALREADY_CREATED(9015, "Đơn hàng đã được tạo"),
    ORDER_EXPIRED_FOR_RETURN(9016, "ORDER_EXPIRED_FOR_RETURN"),
    PO_CONFIRM_NOT_EXISTS(9017, "Đơn mua hàng không tồn tại"),
    NO_MORE_STOCK_COUNTING_FOR_TODAY(9018,"Đã có 1 phiếu kiểm kê được tạo trong hôm nay"),
    PRODUCT_PRICE_NOT_FOUND(9019, "Giá của sản phẩm không tồn tại"),
    PRODUCT_NOT_FOUND(9020, "Sản phẩm không tồn tại"),
    DATE_RETURN_MUST_NOT_BE_NULL(9022,"DATE_RETURN_MUST_NOT_BE_NULL"),
    SALE_ORDER_NUMBER_MUST_NOT_BE_NULL(9023,"SALE_ORDER_NUMBER_MUST_NOT_BE_NULL"),
    REASON_MUST_NOT_BE_NULL(9024,"REASON_MUST_NOT_BE_NULL"),
    CREATE_USER_MUST_NOT_BE_NULL(9025, "CREATE_USER_MUST_NOT_BE_NULL"),
    ORDER_RETURN_DOES_NOT_EXISTS(9026, "ORDER_RETURN_DOES_NOT_EXISTS"),
    /**
     * MANAGEMENT USER MESSAGE 10000 -> 10999
     */
    MANAGEMENT_USER_DOES_NOT_EXISTS(10000, "MANAGEMENT_USER_DOES_NOT_EXISTS"),
    IDENTITY_CARD_CODE_HAVE_EXISTED(10001, "Chứng minh nhân dân đã tồn tại"),

    /**
     * Voucher 11000-11999
     */
    VOUCHER_DOES_NOT_EXISTS(11000, "Voucher không tồn tại"),
    VOUCHER_CODE_MUST_BE_NOT_BLANK(11001, "Mã Voucher không được rỗng"),
    VOUCHER_CODE_HAVE_EXISTED(11002, "Mã Voucher đã tồn tại"),
    VOUCHER_NAME_MUST_BE_NOT_BLANK(11003, "Tên Voucher không được rỗng"),
    VOUCHER_SERIAL_MUST_BE_NOT_BLANK(11004, "Serial Voucher không được rỗng"),

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
