package vn.viettel.authorization.controller;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.customer.CustomerInfoReservationByCustomerNumberRequest;
import vn.viettel.authorization.messaging.customer.CustomerInfomationResponse;
import vn.viettel.authorization.messaging.user.*;
import vn.viettel.authorization.service.CustomerInfomationService;
import vn.viettel.authorization.service.DistributorService;
import vn.viettel.authorization.service.ManagementUserService;
import vn.viettel.authorization.service.UserService;
import vn.viettel.authorization.service.dto.DistributorDTO;
import vn.viettel.authorization.service.dto.customer.CustomerInfoReservationDTO;
import vn.viettel.authorization.service.dto.user.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.security.anotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.authorization.messaging.user.*;
import vn.viettel.authorization.service.dto.user.*;
import vn.viettel.core.security.anotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final ManagementUserService managementUserService;
    private final CustomerInfomationService customerInformationService;
    private final DistributorService distributorService;

    public UserController(UserService userService, ManagementUserService managementUserService,
                          CustomerInfomationService customerInformationService, DistributorService distributorService) {
        this.userService = userService;
        this.managementUserService = managementUserService;
        this.customerInformationService = customerInformationService;
        this.distributorService = distributorService;
    }

    @RoleAdmin
    @RoleShopOwner
    @RoleDistributor
    @GetMapping("/api/user/shop-management-user-info")
    public Response<ShopManagementUsersInfoDTO> getShopManagementUser(@Valid ShopManagementUserInfoRequest request) {
        logger.info("[getShopManagementUser()] - get shop management user #shopId: {}", request.getShopId());
        return managementUserService.getShopManagementUserInfo(request);
    }

    @RoleAdmin
    @RoleShopOwner
    @RoleDistributor
    @GetMapping("/api/user/group-management-user-info")
    public Response<GroupManagementUserInfoDTO> getGroupManagementUser(@Valid GroupManagementUserInfoRequest request) {
        logger.info("[getGroupManagementUser()] - get group management user #groupId: {}", request.getGroupId());
        return managementUserService.getGroupManagementUserInfo(request);
    }

    @RoleAdmin
    @RoleShopOwner
    @RoleDistributor
    @PostMapping("/api/user/update-shop-manager-user")
    public Response<String> updateShopManagerUser(@Valid @RequestBody ShopManagerUserUpdateRequest request) {
        logger.info("[updateShopManagerUser()] - user: {},shop: {}, email: {}", getUserId(), request.getShopId(), request.getEmail());
        return managementUserService.updateShopManagerUser(request);
    }

    @RoleAdmin
    @RoleShopOwner
    @RoleDistributor
    @PostMapping("/api/user/update-group-manager-user")
    public Response<String> updateGroupManagerUser(@Valid @RequestBody GroupManagerUserUpdateRequest request) {
        logger.info("[updateGroupManagerUser()] - userId: {}, groupId: {}, email: {}",
                this.getUserId(), request.getGroupId(), request.getEmail());
        return managementUserService.updateGroupManagerUser(request);
    }

    @RoleAdmin
    @RoleShopOwner
    @RoleDistributor
    @PostMapping("/api/user/update-shop-employee-user")
    public Response<String> updateShopEmployeeUser(@Valid @RequestBody ShopEmployeeUserUpdateRequest request) {
        logger.info("[updateShopEmployeeUser()] - user: {},shop: {}, email: {}", getUserId(), request.getShopId(), request.getEmail());
        return managementUserService.updateShopEmployeeUser(request);
    }

    @RoleAdmin
    @RoleShopOwner
    @RoleDistributor
    @PostMapping("/api/user/update-group-employee-user")
    public Response<String> updateGroupEmployeeUser(@Valid @RequestBody GroupEmployeeUserUpdateRequest request) {
        logger.info("[updateGroupEmployeeUser()] - #userId: {}, #groupId: {}, #email: {}",
                this.getUserId(), request.getGroupId(), request.getEmail());
        return managementUserService.updateGroupEmployeeUser(request);
    }

    @RoleAdmin
    @RoleShopOwner
    @RoleDistributor
    @RoleShopManager
    @PostMapping("/api/user/update-password-shop-employee-user")
    public Response<String> updatePasswordShopEmployeeUser(@Valid @RequestBody ShopEmployeeUserUpdatePasswordRequest request) {
        logger.info("[updatePasswordShopEmployeeUser()] - user: {},shop: {}", getUserId(), request.getShopId());
        return managementUserService.updatePasswordShopEmployeeUser(request);
    }

    @RoleAdmin
    @RoleShopOwner
    @RoleDistributor
    @RoleGroupManager
    @PostMapping("/api/user/update-password-group-employee-user")
    public Response<String> updatePasswordGroupEmployeeUser(@Valid @RequestBody GroupEmployeeUserUpdatePasswordRequest request) {
        logger.info("[updatePasswordGroupEmployeeUser()] - #userId: {}, #groupId: {}", this.getUserId(), request.getGroupId());
        return managementUserService.updatePasswordGroupEmployeeUser(request);
    }

    @RoleShopOwner
    @RoleDistributor
    @RoleShopManager
    @GetMapping("/api/user/check-expired-password-shop-employee-user")
    public Response<Boolean> isExpiredShopEmployeePassword(@Valid ShopEmployeeExpiredPasswordRequest request) {
        logger.info("[isExpiredShopEmployeePassword()] - user: {}, shop: {}", getUserId(), request.getShopId());
        return managementUserService.isExpiredPasswordShopEmployeeUser(request);
    }

    @RoleAdmin
    @RoleShopOwner
    @RoleDistributor
    @RoleGroupManager
    @GetMapping("/api/user/check-expired-password-group-employee-user")
    public Response<Boolean> isExpiredGroupEmployeePassword(@Valid GroupEmployeeExpiredPasswordRequest request) {
        logger.info("[isExpiredGroupEmployeePassword()] - #userId: {}, #shopId: {}", this.getUserId(), request.getGroupId());
        return managementUserService.isExpiredPasswordGroupEmployeeUser(request);
    }

    @RoleAdmin
    @RoleShopOwner
    @RoleDistributor
    @PostMapping("/api/user/profile/update")
    public Response<UserProfileDTO> updateProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        logger.info("[updateProfile()] - update profile #id: {}", getUserId());
        return userService.updateProfile(request);
    }

    @RoleAdmin
    @RoleShopOwner
    @RoleDistributor
    @RoleShopManager
    @RoleShopEmployee
    @RoleFeign
    @GetMapping("/api/user/reservation/info/find-by-customer-number")
    public CustomerInfomationResponse<CustomerInfoReservationDTO> getCustomerInfoByNumberId(@Valid CustomerInfoReservationByCustomerNumberRequest request) {
        logger.info("[getCustomerInfoByNumberId()] - get customer information #customer_number_id: {}", request.getCustomerNumber());
        return customerInformationService.findCustomerInfoReservationByCustomerNumber(request);
    }

    @RoleFeign
    @GetMapping("/get-list-email-by-id")
    public Response<List<String>> getListEmailById(@RequestParam("ids") long[] ids) {
        logger.info("[getListEmailById()] - find user email by id: {}", ids);
        return userService.getListEmailById(ids);
    }

    @RoleFeign
    @GetMapping("/findByEmail/{email}")
    public Response<UserDTO> findByEmail(@PathVariable("email") String email) {
        return userService.findUserByEmail(email);
    }

    @RoleFeign
    @GetMapping("/findUsersByEmail/{email}")
    public Response<List<UserDTO>> findUsersByEmail(@PathVariable("email") String email) {
        return userService.findUsersByEmail(email);
    }

    @RoleFeign
    @GetMapping("/checkExistsUser/{id}")
    public UserExistResponse checkExistsUser(@PathVariable("id") Long id) {
        return userService.checkUserExist(id);
    }

    @RoleFeign
    @GetMapping("/findById/{id}")
    public Response<UserDTO> getUserById(@PathVariable("id") Long id) {
        logger.info("[findById()] - find User By Id." + id);
        return userService.getUserById(id);
    }

    @RoleFeign
    @GetMapping("/findByRole/{rid}")
    public List<Long> findByRole(@PathVariable("rid") Long rid) {
        logger.info("[findByRole()] - find User By RoleId." + rid);
        return userService.findByRole(rid);
    }

    @RoleFeign
    @GetMapping("/exist/{id}")
    public Boolean exist(@PathVariable("id") Long id) {
        return userService.exists(id);
    }

    @RoleAdmin
    @GetMapping("/api/user/index")
    public Response<Page<UserIndexDTO>> usersIndex(@RequestParam(value = "roleId", required = false) Long roleId,
                                                   @RequestParam(value = "q", required = false) String searchKeywords, Pageable pageable) {
        logger.info("[index()] - user index #role_id: {}, #searchKeywords: {}", roleId, searchKeywords);
        return userService.usersIndex(roleId, searchKeywords, pageable);
    }

    @RoleAdmin
    @PostMapping("/api/user/create")
    public Response<UserDTO> create(@Valid @RequestBody UserCreateRequest request) {
        logger.info("[add()] - add user #email: {}", request.getEmail());
        return userService.create(request);
    }

    @RoleAdmin
    @GetMapping("/api/user/{id}/edit")
    public Response<UserEditDTO> edit(@PathVariable("id") Long id) {
        logger.info("[edit()] - user edit #userId: {}", id);
        return userService.edit(id);
    }

    @RoleAdmin
    @PostMapping("/api/user/{id}/update")
    public Response<UserDTO> update(@PathVariable("id") Long id, @Valid @RequestBody UserCreateRequest request) {
        logger.info("[update()] - update user #Id: {}", id);
        return userService.update(id, request);
    }

    @RoleAdmin
    @PostMapping("/api/user/{id}/delete")
    public Response<UserDTO> delete(@PathVariable("id") Long id) {
        logger.info("[delete()] - delete user #Id: {}", id);
        return userService.delete(id);
    }

    @RoleAdmin
    @PostMapping("/api/user/delete-all")
    public Response<List<Response<UserDTO>>> bulkDelete(@Valid @RequestBody UserBulkDeleteRequest request) {
        logger.info("[bulkDelete()] - bulk delete user #Ids: {}", request);
        return userService.bulkDelete(request);
    }

    @RoleAdmin
    @RoleShopOwner
    @RoleDistributor
    @RoleFeign
    @GetMapping("/api/distributors")
    public Response<DistributorDTO> getDistributorByDistributorNumber(@RequestParam(name = "distributorNumber") String distributorNumber) {
        logger.info("[getDistributorByDistributorNumber()] - get distributor by number #Distributor number: {}", distributorNumber);
        return distributorService.getByDistributorNumber(distributorNumber);
    }

    @RoleAdmin
    @RoleShopOwner
    @RoleDistributor
    @RoleFeign
    @GetMapping("/api/distributor/{id}")
    public Response<DistributorDTO> getDistributorById(@PathVariable(name = "id") Long distributorId) {
        logger.info("[getDistributorById()] - get distributor by id #Distributor id: {}", distributorId);
        return distributorService.getDistributorById(distributorId);
    }

    @RoleAdmin
    @RoleShopOwner
    @RoleDistributor
    @RoleFeign
    @GetMapping("/api/distributor/get-by-user-id")
    public Response<DistributorDTO> getDistributorByUserId(@RequestParam(name = "userId") Long userId) {
        logger.info("[getDistributorByUserId()] - get distributor by user id #user id: {}", userId);
        return distributorService.getDistributorByUserId(userId);
    }

    @RoleFeign
    @GetMapping("/api/user/get-all-user-id-by-customer-name")
    public Response<List<Long>> getAllUserIdByCustomerName(@RequestParam("name") String name) {
        logger.info("[getAllUserIdByCustomerName()] - get all user id by customer name." + name);
        return userService.getAllUserIdByUserName(name);
    }

    @RoleFeign
    @GetMapping("/api/user/get-all-distributor")
    public Response<List<DistributorDTO>> getAllDistributor() {
        logger.info("[getAllDistributor()] - get all distributor.");
        return distributorService.getAllDistributor();
    }

    @RoleFeign
    @GetMapping("/api/distributor/get-by-email")
    public Response<List<DistributorDTO>> getDistributorByEmail(@RequestParam(name = "email") String email) {
        logger.info("[getDistributorByEmail()] - get distributor by email #email: {}", email);
        return distributorService.getDistributorByEmail(email);
    }

    @RoleFeign
    @GetMapping("/api/distributor/get-by-name")
    public Response<List<DistributorDTO>> getDistributorByName(@RequestParam(name = "name") String name) {
        logger.info("[getDistributorByName()] - get distributor by name #name: {}", name);
        return distributorService.getDistributorByName(name);
    }
}
