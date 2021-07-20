
package vn.viettel.core.util;

public enum ResponseMessage {

    SUCCESSFUL(200, "OK"),
    CREATED(201, "Cập nhập thành công"),
    ACCEPTED(202, "ACCEPTED"),
    CREATE_CANCEL(203, "Hủy thêm mới"),
    CHANGE_PASSWORD_SUCCESS(204, "Bạn đã thay đổi mật khẩu thành công"),
    ERROR(205, "ERROR"),
    SERVICE_NOT_START(206, "%s chưa được mở lên. Vui lòng liên hệ Quản trị"),
    NON_AUTHORITATIVE_INFORMATION(207, "NON_AUTHORITATIVE_INFORMATION"),
    CREATE_RED_INVOICE_SUCCESSFUL(208, "Thêm mới hóa đơn đỏ thành công"),

    SESSION_EXPIRED(408, "Token hết hạn"),
    DATA_NOT_FOUND(404, "DATA_NOT_FOUND"),
    UNKNOWN(5001, "UNKNOWN"),
    NULL_POINTER_EXCEPTION(5002, "NULL_POINTER_EXCEPTION"),
    SYSTEM_ERROR(5003, "SYSTEM_ERROR"),
    DO_NOT_CHEAT_DATABASE(5004, "Lỗi cheat database"),

    // 1xxx - Authentication failed
    INVALID_TOKEN(1001, "Token không hợp lệ"),
    NOT_AUTHORIZED(1002, "Không có quyền"),
    NOT_EXISTS_EMAIL(1003, "Email không tồn tại"),
    LOGIN_FAILED(1004, "Đăng nhập thất bại"),
    STOCK_COUNTING_NOT_FOUND(1005, "Không tìm thấy phiếu kiểm kê"),
    FORBIDDEN(1006, "Không có quyền"),
    PRODUCT_INFO_NOT_FOUND(1007, "Không tìm thấy thông tin sản phẩm"),
    ROW_WAS_UPDATED_OR_DELETED(1008, "Dữ liệu được cập nhật trước đó, vui lòng thực hiện lại để cập nhật giá trị mới."),
    STORE_WAS_UPDATED_OR_DELETED(1009, "Kho được cập nhật trước đó, vui lòng thực hiện lại để cập nhật giá trị mới."),

    // 2xxx - Data validation failed
    DATA_TYPE_ERROR(2000, "Kiểu dữ liệu không hợp lệ"),
    INVALID_BODY(2001, "Không tìm thấy body"),
    VALIDATED_ERROR(2002, "VALIDATED_ERROR"),
    NOT_AN_EXCEL_FILE(2003, "Không phải file excel"),
    EMPTY_LIST(2004, "Danh sách rỗng"),
    INVENTORY_QUANTITY_MUST_NOT_BE_NULL(2005, "Số lượng kiểm kê không được phép null"),
    NULL_POINT(2006, "Lỗi code, không kiểm tra null"),
    THE_EXCEL_FILE_IS_NOT_IN_THE_CORRECT_FORMAT(2007, "Tệp excel không đúng định dạng"),

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
    DO_NOT_HAVE_PERMISSION_TO_UPDATE(4010, "Không có quyền chỉnh sửa"),
    EXPIRED_FOR_UPDATE(4011, "Hết hạn cập nhật"),
    UPDATE_SUCCESSFUL(4012, "Cập nhật thành công"),
    CREATED_SUCCESSFUL(4013, "Thêm mới thành công"),

    // 5xxx - connection failed
    CONNECT_DATABASE_FAILED(5000, "Kết nối cơ sở dữ liệu thất bại"),
    DELETE_SUCCESSFUL(5001, "Xóa thành công"),
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
    WAREHOUSE_TYPE_ID_MUST_NOT_BE_NUll(6011, "Id loại kho không được null"),
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
    PRODUCT_OUT_OF_STOCK(6100, "Số lượng sản phẩm %s vượt quá tồn kho ( %s1 )"),
    INVALID_USERNAME_OR_PASSWORD(6156, "INVALID_USERNAME_OR_PASSWORD"),
    USER_ROLE_MUST_BE_NOT_BLANK(6160, "USER_ROLE_MUST_BE_NOT_BLANK"),
    USER_STATUS_MUST_BE_NOT_BLANK(6161, "USER_STATUS_MUST_BE_NOT_BLANK"),
    MEMBER_CARD_NOT_EXIST(6162, "MEMBER_CARD_NOT_EXIST"),
    ID_MUST_NOT_BE_NULL(6164, "ID_MUST_NOT_BE_NULL"),
    USER_IS_NOT_ACTIVE(6165, "Nhân viên đang tạm ngưng hoạt động. Vui lòng liên hệ quản trị hệ thống để biết thêm thông tin"),
    SALE_ORDER_TYPE_NOT_EXIST(6166, "Loại đơn hàng không hợp lệ"),
    WARE_HOUSE_NOT_EXIST(6167, "Không tìm thấy kho hàng"),
    RECEIPT_ONLINE_NOT_EXIST(6167, "Không tìm thấy hóa đơn online"),
    SHOP_NOT_MATCH(6168, "SHOP_NOT_MATCH"),
    USER_ROLE_NOT_MATCH(6169, "Tài khoản không có vai trò này"),
    SHOP_ID_MUST_NOT_BE_NULL(6168, "SHOP_ID_MUST_NOT_BE_NULL"),
    TOTAL_PAID_MUST_NOT_BE_NULL(6170, "TOTAL_PAID_MUST_NOT_BE_NULL"),
    TYPE_NOT_BE_NULL(6171, "TYPE_NOT_BE_NULL"),
    SALE_ORDER_TYPE_NOT_BE_NULL(6172, "SALE_ORDER_TYPE_NOT_BE_NULL"),
    REQUEST_BODY_NOT_BE_NULL(6173, "Yêu cầu truyền vào không được rỗng"),
    NO_PRICE_APPLIED(6174, "Không tìm thấy giá được áp dụng cho sản phẩm"),
    SHOP_IS_NOT_ACTIVE(6175, "Nhân viên thuộc cửa hàng đang tạm ngưng hoạt động. Vui lòng liên hệ quản trị hệ thống để biết thêm thông tin"),
    NEW_PASSWORD_MUST_BE_NOT_NULL(6176, "Vui lòng nhập mật khẩu mới"),
    CONFIRM_PASSWORD_MUST_BE_NOT_NULL(6176, "Vui lòng nhập mật khẩu xác nhận"),
    CONFIRM_PASSWORD_NOT_CORRECT(6177, "Mật khẩu và xác nhận mật khẩu phải giống nhau"),
    CHANGE_PASSWORD_FAIL(6178, "Đổi mật khẩu thất bại"),
    NO_FUNCTIONAL_PERMISSION(6179, "Nhân viên không có quyền thực hiện tác vụ này"),
    NO_PRIVILEGE_ON_ANY_SHOP(6180, "Tên đăng nhập chưa được gán quyền dữ liệu trên bất kỳ cửa hàng nào. Vui lòng liên hệ quản trị hệ thống để biết thêm thông tin"),
    NO_PERMISSION_ASSIGNED(6181, "Tên đăng nhập chưa được gán tập danh sách chức năng truy cập. Vui lòng liên hệ quản trị hệ thống để được hỗ trợ"),
    USER_HAVE_NO_ROLE(6182, "Tên đăng nhập chưa được gán vai trò. Vui lòng liên hệ quản trị hệ thống để biết thêm thông tin"),
    WRONG_CAPTCHA(6183, "Sai mã captcha"),
    ENTER_CAPTCHA_TO_LOGIN(6184, "Vui lòng nhập mã captcha"),
    USER_HAVE_NO_PRIVILEGE_ON_THIS_SHOP(6185, "Tên đăng nhập không được gán quyền trên cửa hàng này"),
    QUANTITY_CAN_NOT_BE_NULL(6186, "Số lượng không được null"),
    AMOUNT_CAN_NOT_BE_NULL(6187, "Thành tiền không được null"),
    RETURN_AMOUNT_CAN_NOT_BE_NULL(6188, "Số lượng trả không được null"),
    FORM_ID_CAN_NOT_BE_NULL(6189, "Tham số formId là bắt buộc"),
    CONTROL_ID_CAN_NOT_BE_NULL(6190, "Tham số ctrlId là bắt buộc"),
    STOCK_COUNTING_ALREADY_EXIST(6191, "Đã tồn tại dữ liệu kiểm kê trong ngày hôm nay, bạn có muốn lưu đè không"),
    SHOP_PARAM_NOT_FOUND(6192, "Không tìm thấy tham số của cửa hàng"),
    PRODUCT_NOT_IN_PROMOTION(6193, "Không tìm thấy sản phẩm %s trong CTKM %s1 hoặc số lượng vượt quá số lượng khuyến mãi."),
    PROMOTION_AMOUNT_NOT_CORRECT(6194, "Tiền khuyến mãi hoặc tiền phải trả chưa đúng."),
    NO_PERMISSION_TYPE_2(6195, "Nhân viên không quản lý đơn vị nào có trạng thái đang hoạt động. Vui lòng liên hệ quản trị để biết thêm thông tin"),

    /**
     * CUSTOMER: 7000 -> 7999
     */
    CUSTOMER_LAST_NAME_MUST_BE_NOT_BLANK(7000, "Họ và tên đệm khách hàng không được để trống"),
    CUSTOMER_FIRST_NAME_MUST_BE_NOT_BLANK(7001, "Tên khách hàng không được để trống"),
    CUSTOMER_ADDRESS_MUST_NOT_BE_NULL(7002, "customer address must not be null"),
    CUSTOMER_STATUS_MUST_BE_NOT_NULL(7003, "Trạng thái khách hàng không được bỏ trống"),
    CUSTOMER_CODE_MUST_BE_NOT_BLANK(7004, "CUSTOMER_CODE_MUST_BE_NOT_BLANK"),
    CUSTOMER_CODE_HAVE_EXISTED(7005, "CUSTOMER_CODE_HAVE_EXISTED"),
    CUSTOMER_IS_NOT_EXISTED(7006, "CUSTOMER_IS_NOT_EXISTED"),
    CUSTOMER_IDS_MUST_BE_NOT_NULL(7007, "CUSTOMER_IDS_MUST_BE_NOT_NULL"),
    CUSTOMER_DOES_NOT_EXIST(7008, "Không tìm thấy khách hàng"),
    CUSTOMER_DEFAULT_DOES_NOT_EXIST(7008, "Không tìm thấy khách hàng mặc định"),
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
    CUSTOMER_CREATE_FAILED(7031, "Thêm mới khách hàng thất bại"),
    CUSTOMER_NOT_EXIST(7032, "Khách hàng không tồn tại"),
    DATE_OF_BIRTH_MUST_BE_NOT_NULL(7033, "Ngày sinh không được để trống"),
    CUSTOMER_DOES_NOT_EXIST_IN_SALE_ORDER(7034, "Khách hàng chưa có đơn hàng nào"),
    CUSTOMERS_ARE_NOT_DIFFERENT(7035, "Mã khách hàng không trùng nhau"),
    CUSTOMERS_EXIST_IDNO(7036, "Số CMND thuộc khách hàng: %s"),
    CUSTOMERS_EXIST_FONE(7038, "Số điện thoại thuộc khách hàng: %s"),
    MAX_LENGTH_STRING(7039, "Bạn đã nhập quá số kí tự quy định"),
    STREET_MUST_BE_NOT_NULL(7040, "Số nhà, Đường không thể bỏ trống"),
    CUSTOMER_TYPE_MUST_BE_NOT_NULL(7041, "Nhóm khách hàng không được bỏ trống"),
    CUSTOMER_REJECT(7042, "Sai khách hàng"),
    RPT_CUST_MEM_AMOUNT_AMOUNT_MUST_BE_NOT_NULL(7043, "Tổng doanh số tích lũy không được trống"),
    RPT_CUST_MEM_AMOUNT_NOT_EXEITS(7044, "Không tìm thấy thông tin tổng hợp doanh số theo thẻ thành viên"),
    MEMBER_CARD_SCORE_CUMULATED_INVALID(7045, "Doanh số tích lũy sử dụng lơn hơn doanh số tích lũy hiện có"),
    CUSTOMER_AGE_NOT_BE_YOUNGER(7046, "Tuổi của khách hàng không được nhỏ hơn %s"),
    WARE_HOUSE_TYPE_NOT_EXISTS(7047, "Không tìm thấy kho của cửa hàng, vui lòng tạo kho hàng trước"),
    CUSTOMER_CAN_NOT_UPDATE(7048, "Khách hàng không được phép chỉnh sửa"),

    /**
     * COMPANY 8000 -> 8999
     */
    COMPANY_FEATURE_NOT_AVAILABLE(8000, "COMPANY_FEATURE_NOT_AVAILABLE"),

    /**
     * SALE: 9000 - 9999
     */
    DELIVERY_TYPE_MUST_BE_NOT_NULL(9001, "Vui lòng nhập phương thức giao hàng"),
    PAYMENT_METHOD_MUST_BE_NOT_NULL(9002, "Vui lòng nhập phương thức thanh toán"),
    PAYMENT_FAIL(9003,"Thanh toán thất bại"),
    SALE_ORDER_DETAIL_DOES_NOT_EXISTS(9004, "Chi tiết đơn hàng không tìm thấy"),
    PO_TRANS_IS_NOT_EXISTED(9005, "Phiếu nhập không tồn tại"),
    STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED(9006, "Phiếu giao dịch điều chỉnh không tồn tại"),
    STOCK_BORROWING_TRANS_IS_NOT_EXISTED(9007, "Phiếu giao dịch vay mượn không tồn tại"),
    PROMOTION_DOSE_NOT_EXISTS(9008,"Khuyến mãi không tìm thấy"),
    SALE_ORDER_ID_MUST_NOT_BE_NULL(9009,"ID hóa đơn không tìm thấy"),
    SALE_ORDER_NUMBER_MUST_NOT_BE_NULL(9010,"Số hóa đơn không được rỗng"),
    DATE_RETURN_MUST_NOT_BE_NULL(9011,"Ngày trả hàng không được rỗng"),
    REASON_MUST_NOT_BE_NULL(9012,"ID lý do không được rỗng"),
    ORDER_RETURN_DOES_NOT_EXISTS(9013,"Đơn hàng trả lại không tìm thấy"),
    CREATE_USER_MUST_NOT_BE_NULL(9014, "Người dùng tạo hóa đơn không được để rỗng"),
    PRODUCT_INFO_NOT_EXISTS(9009, "Không tìm thấy thông tin sản phẩm"),
    ORDER_ONLINE_NOT_FOUND(9010,"Không tìm thấy đơn online"),
    AP_PARAM_NOT_EXISTS(9011,"AP_PARAM_NOT_EXITS"),
    CATEGORY_DATA_NOT_EXISTS(9012,"CATEGORY_DATA_NOT_EXISTS"),
    RED_INVOICE_DETAIL_NOT_EXISTS(9013,"Không tìm thấy chi tiết hóa đơn đỏ"),
    NO_PRODUCT_TO_ORDER(9014,"Vui lòng chọn sản phẩm để mua hàng"),
    SALE_ORDER_ALREADY_CREATED(9015, "Đơn hàng đã được tạo"),
    ORDER_EXPIRED_FOR_RETURN(9016, "Hóa đơn đã hết hạn trả hàng"),
    PO_CONFIRM_NOT_EXISTS(9017, "Đơn mua hàng không tồn tại"),
    NO_MORE_STOCK_COUNTING_FOR_TODAY(9018,"Đã có 1 phiếu kiểm kê được tạo trong hôm nay"),
    PRODUCT_NOT_FOUND(9019, "Không tìm thấy sản phẩm"),
    REASON_DESC_MUST_NOT_BE_NULL(9020, "Mô tả lý do không được rỗng"),
    INVALID_REASON(9021,"Lý do đổi trả không hợp lệ"),
    STOCK_TOTAL_NOT_FOUND(9022, "Không tìm thấy thông tin tồn kho"),
    REASON_NOT_FOUND(9023,"Không tìm thấy id lý do"),
    EDITING_IS_NOT_ALLOWED(9024,"không được phép chỉnh sửa"),
    COMBO_PRODUCT_TRANS_NOT_EXISTS(9025,"không tìm thấy thông tin xuất nhập sản phẩm Combo"),
    COMBO_PRODUCT_NOT_EXISTS(9026,"không tìm thấy sản phẩm Combo"),
    FROM_SALE_ORDER_NOT_FOUND(9027,"không tìm thấy hóa đơn gốc(cha)"),
    STOCK_TOTAL_CANNOT_BE_NEGATIVE(9028, "Không đủ tồn kho"),
    DO_NOT_HAVE_PERMISSION_TO_DELETE(9029, "Không có quyền xóa"),
    EXPIRED_FOR_DELETE(9030, "Hết hạn xóa"),
    EDITABLE_ONLINE_ORDER_NOT_ALLOW(9031, "Cửa hàng không có quyền chỉnh sửa đơn Online"),
    MANUALLY_CREATABLE_ONLINE_ORDER_NOT_ALLOW(9032, "Cửa hàng không có quyền tạo tay đơn Online"),
    ORDER_FOR_RETURN_NOT_FOUND(9033,"Không có kết quả cần tìm"),
    RED_INVOICE_ID_IS_NULL(9034,"Chưa chọn hóa đơn để xóa"),
    PROMOTION_PROGRAM_DISCOUNT_NOT_EXIST(9035, "Mã giảm giá không hợp lệ"),
    EXCHANGE_TRANS_NOT_FOUND(9036,"Không có kết quả cần tìm"),
    EXCHANGE_TRANS_DETAIL_NOT_FOUND(9037,"Chi tiết phiếu đổi hàng hỏng không tìm thấy"),
    RED_INVOICE_CODE_HAVE_EXISTED(9038,"Trùng số hóa đơn đỏ không được phép lưu"),
    INVALID_STRING_LENGTH(9034,"Nhập vượt quá giới hạn quy định"),
    PO_NO_IS_EXIST(9035,"Số đơn mua hàng đã tồn tại"),
    INTERNAL_NUMBER_IS_EXIST(9036,"Số nội bộ đã tồn tại"),
    RED_INVOICE_NO_IS_EXIST(9037,"Số hóa đơn đã tồn tại"),
    TRANS_DATE_MUST_BE_NOT_NULL(9038, "Ngày giao dịch không được để trống"),
    TRANS_TYPE_MUST_BE_NOT_NULL(9039, "Loại giao dịch không được để trống"),
    COMBO_PRODUCT_ID_MUST_BE_NOT_NULL(9040, "Id sản phẩm Combo không được để trống"),
    QUANTITY_MUST_BE_NOT_NULL(9041, "Số lượng không được để trống"),
    PRICE_MUST_BE_NOT_NULL(9042, "Giá không được để trống"),
    COMBO_PRODUCT_LIST_BE_NOT_NULL(9043, "Danh sách sản phẩm combo không được để trống"),
    COMBO_PRODUCT_LIST_MUST_BE_NOT_EMPTY(9044, "Không có sản phẩm combo nào được chọn"),
    CREATE_COMBO_PRODUCT_TRANS_FAIL(9045, "Tạo mới nhập xuất combo thất bại"),
    QUANTITY_INVALID_STRING_LENGTH(9046, "Số lượng vượt quá giới hạn"),
    PROMOTION_SHOP_MAP_CANNOT_BE_NULL(9047, "thông tin số lượng khuyến mãi tại cửa hàng có áp dụng CTKM không được null"),
    SALE_ORDER_DOES_NOT_EXIST(9048, "Đơn hàng không tồn tại"),
    NO_PRODUCT_IN_STOCK_COUNTING(9047, "Không có sản phẩm kiểm kê trong đơn kiểm kê này"),
    PLEASE_IMPORT_PRODUCTS(9048, "Vui lòng nhập sản phẩm"),
    STOCK_TOTAL_LESS_THAN(9049, "Sản phẩm %s hiện không còn đủ tồn kho. Số lượng còn lại %s"),
    COMBO_PRODUCT_QUANTITY_REJECT(9050, "Số lượng sản phẩm nhập/xuất combo phải lớn hơn 0"),
    SALE_ORDER_NOT_FOUND(9051, "Hóa đơn bán hàng không được tìm thấy"),
    RED_INVOICE_NUMBER_NOT_FOUND(9052,"Danh sách cập nhập số hóa đơn rỗng"),
    RED_INVOICE_NUMBER_IS_NULL(9053,"Số hóa đơn đỏ rỗng"),
    STOCK_TOTAL_CANNOT_BE_NEGATIVE_SS(9054, "Sản phẩm %s hiện không còn đủ tồn kho. Không thể thực hiện xóa mã nhập hàng %s"),
    SALE_ORDER_CANNOT_RETURN(9055, "Đơn hàng không được phép trả"),
    PRICE_REJECT(9056, "Giá phải lớn hơn 0"),
    COMBO_PRODUCT_FACTOR_REJECT(9057, "Hệ số quy đổi sản phẩm combo phải lớn hơn 0"),
    SALE_ORDER_DETAIL_NOT_FOUND(9058, "Chi tiết đơn hàng rỗng"),
    NOT_IMPORT_SUCCESS(9056, "Không nhập thành công"),
    NUMBER_GREATER_THAN_ZERO(9057, "Số lượng phải lớn hơn 0"),
    INVOICE_NUMBER_EMPTY(9058, "Bạn chưa chọn hóa đơn bán hàng"),
    NO_CUSTOMER_TYPE_IS_APPLIED_PROMOTION(9059,"Không có loại khách hàng nào áp dụng khuyến mãi"),
    NO_MEMBER_CARD_IS_APPLIED_PROMOTION(9060,"Không có khách hàng thành viên nào áp dụng khuyến mãi"),
    NO_CUS_LOYAL_IS_APPLIED_PROMOTION(9061,"Không có khách hàng thân thiết nào áp dụng khuyến mãi"),
    NO_CUS_CARD_IS_APPLIED_PROMOTION(9062,"Không có khách hàng nào có thẻ áp dụng khuyến mãi"),
    RECORD_DOES_NOT_EXISTS(9063, "Bản ghi không tại"),
    ONLINE_NUMBER_IS_EXISTS(9064, "Số đơn online đã tồn tại"),
    PROMOTION_PROGRAM_NOT_EXISTS(9065, "Số đơn online đã tồn tại"),
    PROMOTION_SALE_PRODUCT_REJECT(9065, "Vui lòng mua đúng sản phẩm để được hưởng khuyến mãi"),
    SALE_ORDER_HAS_ALREADY_RETURNED(9065, "Đơn hàng này đã được trả"),
    SALE_ORDER_DOES_NOT_HAVE_PRODUCT(9066, "ĐƠN HÀNG KHÔNG CÓ SẢN PHẨM"),
    PROMOTION_SHOP_MAP_NOT_EXISTS(9067, "Không tìm thấy đơn vị tham gia chưong trình"),
    ORDER_TYPE_NOT_NULL(9068, "Loại mua hàng không được để trống"),
    ORDER_ITEM_NOT_NULL(9099, "Không tìm thấy sản phẩm mua"),
    RED_INVOICE_NOT_FOUND(9070,"Không tìm thấy hóa đơn đỏ"),
    PROMOTION_IN_USE(9071,"Khuyến mãi %s đã được sử dụng"),
    PROMOTION_AMOUNT_NOTEQUALS(9072,"Tiền khuyến mãi không khớp"),
    ACCUMULATED_AMOUNT_OVER(9073,"Sử dụng vượt quá tiền tích lũy"),
    RECORD_WRONG(9074, "Bản ghi không hợp lệ"),
    PROMOTION_NOT_ENOUGH_VALUE(9075, "Số suất không đủ cho khuyến mãi %s"),
    STOCK_ADJUSTMENT_DOSE_NOT_EXISTED(9076, "Phiếu nhập điều chỉnh không tồn tại"),
    PRODUCT_NOT_ACCUMULATED_NOT_EXISTS(9077, "Sản phẩm không được tích lũy không tồn tại"),
    RETURN_AMOUNT_MUST_BE_LESS_THAN_OR_EQUAL_TO_THE_QUANTITY_ENTERED(9078, "Số lượng trả không được vượt quá số lượng còn lại trong phiếu"),
    SALE_ORDER_NUMBER_NOT_FOUND(9079, "Không tìm thấy số hóa đơn bán hàng"),
    PRICE_NOT_FOUND(9080, "Không tìm thấy giá của sản phẩm"),
    EXCHANGE_CODE_IS_EXIST(9081,"Số biên bản đã tồn tại"),
    PO_TRANS_DETAIL_IS_NOT_EXISTED(9082, "Không tồn tại bản ghi chi tiết của phiếu giao dịch"),
    STOCK_COUTING_DETAIL_NOT_FOUND(9083,"Chi tiết phiếu kiểm kê không tìm thấy"),
    DISCOUNT_CODE_NOT_EXISTS(9084,"Mã giảm giá không tồn tại"),
    MGG_SALE_AMOUNT_REJECT(9085,"Tổng tiền đơn hàng chưa đủ điều kiện dùng mã giảm giá %s"),
    ONLINE_NUMBER_MAX_LENGTH_STRING(9086, "Số đơn online không vượt quá 50 ký tự"),
    PRODUCT_PRICE_NOT_FOUND(9087, "Giá sản phẩm '%s' không tìm thấy"),
    PRODUCT_STOCK_TOTAL_NOT_FOUND(9088, "Sản phẩm '%s' không có trong kho"),
    PROMOTION_CODE_NOT_ENOUGH_VALUE(9075, "Số suất không đủ cho khuyến mãi mã giảm giá %s"),
    SHOP_DOES_HAVE_DAY_RETURN(9076,"Cửa hàng không có ngày cho phép trả hàng"),
    PRODUCT_NOT_EXISTS(9077, "Không tìm thấy sản phẩm %s"),
    NO_PRODUCT(9078, "Phải nhập đủ số lượng cơ cấu cho khuyến mãi %s"),
    STOCK_TOTAL_CANNOT_BE_NEGATIVE_SSS(9078, "Sản phẩm %s hiện không còn đủ tồn kho"),
    RECEIPT_HAS_BEEN_EXPORTED(9079, "Đơn đã được xuất"),
    RECEIPT_HAS_BEEN_IMPORTED(9080, "Đơn đã được nhập"),
    RECEIPT_HAS_BEEN_DELETED(9081, "Đơn đã được xóa"),
    STOCK_TOTALS_LESS_THAN(9082, "Sản phẩm quy đổi hiện không còn đủ tồn kho, số lượng còn lại: %s"),
    /*
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
    VOUCHER_SHOP_MAP_REJECT(11005, "Voucher bị từ chối. Sai cửa hàng"),
    VOUCHER_CUSTOMER_TYPE_REJECT(11006, "Voucher bị từ chối. Sai loại khách hàng"),
    UPDATE_VOUCHER_FAIL(11007, "Cập nhật voucher thất bại"),
    CANNOT_SEARCH_VOUCHER(11008, "Chức năng tìm kiếm Voucher bị khóa vui lòng liên hệ admin/quản lý để mở khoá"),
    VOUCHER_CUSTOMER_REJECT(11009, "Voucher bị từ chối. Sai khách hàng"),
    VOUCHER_PRODUCT_REJECT(11010, "Vui lòng chọn đúng sản phẩm để sử dụng voucher"),
    VOUCHER_PROGRAM_DATE_REJECT(11011, "Chương trình voucher đã hết hiệu lực hoặc chưa được kích hoạt"),
    VOUCHER_PROGRAM_IS_USED(11012, "Voucher đã sử dụng"),
    /**
     * Product 12000-12999
     */
    PRODUCT_DOES_NOT_EXISTS(12000, "Sản phẩm không tồn tại"),
    INDUSTRY_ARE_NOT_DIFFERENT(12001, "Các sản phẩm in trong hóa đơn phải cùng ngành hàng"),
    PRODUCT_CODE_DOES_NOT_EMPTY(12002, "Mã sản phẩm không được để trống"),
    PACKAGE_OR_UINT_QUANTITY_MUST_NOT_BE_NULL(12003, "Số lượng pakage hoặc số lượng lẻ không được phép để trống"),
    /**
     * RPT_ZV23 13000-13500
     */
    RPT_ZV23_NOT_EXISTS(13000,"RPT_ZV23 không tồn tại hoặc ngưng hoạt động"),
    CUSTOMER_NOT_IN_RPT_ZV23(13001,"Khách hàng không có trong khuyễn mãi ZV23"),
    CUSTOMER_NOT_REACH_RPT_ZV23(13002,"Khách hàng không đạt ZV23"),


    /**
     * REPORT 13501-13999
     */
    NUMBER_OF_MONTH_LESS_THAN_OR_EQUAL_12(13501,"Từ ngày đến ngày phải nhỏ hơn hoặc băng 12 tháng"),
    SELL_REPORT_NOT_FOUND(13502,"Báo cáo bán hàng không được tìm thấy"),
    SALES_FROM_CANNOT_BE_GREATER_THAN_SALES_TO(13503,"Doanh số đến không hợp lệ"),


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
