package vn.viettel.authorization.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.authorization.repository.RoleRepository;
import vn.viettel.authorization.service.UserAuthenticateService;
import vn.viettel.authorization.service.dto.ChangePasswordRequest;
import vn.viettel.authorization.service.dto.LoginRequest;
import vn.viettel.authorization.service.dto.ShopDTO;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.core.service.dto.PermissionDTO;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = "Api sử dụng cho đăng nhập và đổi mật khẩu")
public class UserAuthenticateController extends BaseController {

    @Autowired
    private UserAuthenticateService userLoginService;
    @Autowired
    RoleRepository repo;
    private final String root = "/users";

    @ApiOperation(value = "Api dùng khi đăng nhập, trả về danh sách vai trò và cửa hàng cho người dùng chọn để đăng nhập")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 6000, message = "Tên đăng nhập hoặc mật khẩu không đúng"),
            @ApiResponse(code = 4007, message = "Tên đăng nhập hoặc mật khẩu không đúng"),
            @ApiResponse(code = 6014, message = "Không tìm thấy cửa hàng"),
            @ApiResponse(code = 6175, message = "Nhân viên thuộc cửa hàng đang tạm ngưng hoạt động. Vui lòng liên hệ quản trị hệ thống để biết thêm thông tin"),
            @ApiResponse(code = 6184, message = "Vui lòng nhập mã captcha"),
            @ApiResponse(code = 6183, message = "Sai mã captcha"),
            @ApiResponse(code = 6182, message = "Tên đăng nhập chưa được gán vai trò. Vui lòng liên hệ quản trị hệ thống để biết thêm thông tin"),
            @ApiResponse(code = 6181, message = "Tên đăng nhập chưa được gán tập danh sách chức năng truy cập. Vui lòng liên hệ quản trị hệ thống để được hỗ trợ"),
            @ApiResponse(code = 6180, message = "Tên đăng nhập chưa được gán quyền dữ liệu trên bất kì cửa hàng nào. Vui lòng liên hệ quản trị hệ thống để biết thêm thông tin"),
            @ApiResponse(code = 6175, message = "Nhân viên thuộc cửa hàng đang tạm ngưng hoạt động. Vui lòng liên hệ quản trị hệ thống để biết thêm thông tin"),
            @ApiResponse(code = 6179, message = "nhân viên không có quyền thực hiện tác vụ này"),
    })
    @PostMapping(value = { V1 + root + "/preLogin"})
    public Response<Object> preLogin(HttpServletRequest request, @Valid @RequestBody LoginRequest loginInfo,
                                     @RequestParam(value = "captcha", required = false) String captcha) {
        Response<Object> result = userLoginService.preLogin(loginInfo, captcha);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.LOGIN_SUCCESS);
        return result;
    }

    @ApiOperation(value = "Api dùng khi đăng nhập")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 6000, message = "Tên đăng nhập hoặc mật khẩu không đúng"),
            @ApiResponse(code = 4007, message = "Tên đăng nhập hoặc mật khẩu không đúng"),
            @ApiResponse(code = 6014, message = "Không tìm thấy cửa hàng"),
            @ApiResponse(code = 6175, message = "Nhân viên thuộc cửa hàng đang tạm ngưng hoạt động. Vui lòng liên hệ quản trị hệ thống để biết thêm thông tin"),
            @ApiResponse(code = 6184, message = "Vui lòng nhập mã captcha"),
            @ApiResponse(code = 6183, message = "Sai mã captcha"),
            @ApiResponse(code = 6182, message = "Tên đăng nhập chưa được gán vai trò. Vui lòng liên hệ quản trị hệ thống để biết thêm thông tin"),
            @ApiResponse(code = 6181, message = "Tên đăng nhập chưa được gán tập danh sách chức năng truy cập. Vui lòng liên hệ quản trị hệ thống để được hỗ trợ"),
            @ApiResponse(code = 6180, message = "Tên đăng nhập chưa được gán quyền dữ liệu trên bất kì cửa hàng nào. Vui lòng liên hệ quản trị hệ thống để biết thêm thông tin"),
            @ApiResponse(code = 6175, message = "Nhân viên thuộc cửa hàng đang tạm ngưng hoạt động. Vui lòng liên hệ quản trị hệ thống để biết thêm thông tin"),
            @ApiResponse(code = 6179, message = "nhân viên không có quyền thực hiện tác vụ này"),
    })
    @PostMapping(value = { V1 + root + "/login"})
    public Response<Object> userLogin(HttpServletRequest request, @Valid @RequestBody LoginRequest loginInfo) {
        Response<Object> result = userLoginService.login(loginInfo);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.LOGIN_SUCCESS);
        return result;
    }

    @ApiOperation(value = "Api dùng khi đổi mật khẩu")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Bạn đã thay đổi mật khẩu thành công"),
            @ApiResponse(code = 6187, message = "Đổi mật khẩu thất bại"),
            @ApiResponse(code = 6000, message = "Tên đăng nhập hoặc mật khẩu không đúng"),
            @ApiResponse(code = 6016, message = "Mật khẩu cũ không chính xác"),
            @ApiResponse(code = 8008, message = "Độ dài mật khẩu không hợp lệ"),
            @ApiResponse(code = 4006, message = "Trùng mật khẩu"),
            @ApiResponse(code = 6177, message = "Mật khẩu và xác nhận mật khẩu phải giống nhau"),
    })
    @PutMapping(value = { V1 + root + "/change-password"})
    public Response<Object> changePassword(HttpServletRequest request, @Valid @RequestBody ChangePasswordRequest requestBody) {
        Response<Object> result = userLoginService.changePassword(requestBody);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.CHANGE_PASSWORD);
        return result;
    }


    @ApiOperation(value = "Kiểm tra quyền dữ liệu của shop")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RoleFeign
    @PostMapping(value = { V1 + root + "/permission-valid"})
    public Boolean gateWayCheckPermissionType2(@RequestParam Long roleId, @RequestParam Long shopId) {
        Boolean result = userLoginService.gateWayCheckPermissionType2(roleId, shopId);
        return result;
    }

    @RoleFeign
    @GetMapping(value = { V1 + root + "/findById/{id}"})
    public UserDTO getUserById(@PathVariable long id) {
        return userLoginService.getUserById(id);
    }

    @RoleFeign
    @GetMapping(value = { V1 + root + "/findByIds"})
    public List<UserDTO> getUserByIds(@RequestParam List<Long> userIds) {
        return userLoginService.getUserByIds(userIds);
    }

    @RoleAdmin
    @RoleFeign
    @GetMapping(value = { V1 + root + "/get-shop-by-role/{roleId}"})
    public List<ShopDTO> getShopByRole(@PathVariable long roleId) {
        return userLoginService.getShopByRole(roleId);
    }

    @RoleAdmin
    @RoleFeign
    @GetMapping(value = { V1 + root + "/get-user-permission/{roleId}"})
    public List<PermissionDTO> getUserPermission(@PathVariable Long roleId) {
        return userLoginService.getUserPermission(roleId);
    }

    @RoleAdmin
    @RoleFeign
    @GetMapping(value = { V1 + root + "/get-data-user"})
    public List<UserDTO> getUserData(@RequestParam Long shopId) {
        List<UserDTO> dtoList = userLoginService.getDataUser(shopId);
        return dtoList;
    }
}
