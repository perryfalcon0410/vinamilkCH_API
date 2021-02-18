package vn.viettel.authorization.service.impl;

import vn.viettel.authorization.messaging.user.*;
import vn.viettel.core.dto.salon.SalonConfirmationHairdresserDetailDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.user.*;
import vn.viettel.authorization.messaging.user.*;
import vn.viettel.authorization.repository.ManagementUserRepository;
import vn.viettel.authorization.service.CommonService;
import vn.viettel.authorization.service.ManagementUserService;
import vn.viettel.authorization.service.RoleService;
import vn.viettel.authorization.service.UserService;
import vn.viettel.authorization.service.dto.group.GroupDTO;
import vn.viettel.authorization.service.dto.shop.ShopDTO;
import vn.viettel.authorization.service.dto.user.*;
import vn.viettel.core.db.entity.ManagementUsers;
import vn.viettel.core.db.entity.commonEnum.ManagementPrivileges;
import vn.viettel.core.db.entity.role.UserRole;
import vn.viettel.core.db.entity.status.Object;
import vn.viettel.core.dto.salon.SalonHairdresserResponseDTO;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.service.mapper.CustomModelMapper;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.exception.ValidateException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.viettel.authorization.service.dto.user.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ManagementUserServiceImpl extends BaseServiceImpl<ManagementUsers, ManagementUserRepository> implements ManagementUserService {

    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final CommonService commonService;
    private final CustomModelMapper customModelMapper;

    public ManagementUserServiceImpl(RoleService roleService, PasswordEncoder passwordEncoder, UserService userService, CommonService commonService, CustomModelMapper customModelMapper) {
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.commonService = commonService;
        this.customModelMapper = customModelMapper;
    }

    @Override
    public Response<ShopManagementUsersInfoDTO> getShopManagementUserInfo(ShopManagementUserInfoRequest request) {
        Long sid = request.getShopId();
        ShopDTO shop = commonService.getShopBy(request.getShopId());
        if (shop == null) {
            throw new ValidateException(ResponseMessage.SALON_DOES_NOT_EXIST);
        }
        Long managerRoleId = getShopManagerRoleId();
        Long employeeRoleId = getShopEmployeeRoleId();

        ManagementUsers managerUser = repository.getUserByObjectAndObjectIdAndRoleId(Object.SHOP.getId(), sid, managerRoleId).orElse(null);
        ManagementUsers employeeUser = repository.getUserByObjectAndObjectIdAndRoleId(Object.SHOP.getId(), sid, employeeRoleId).orElse(null);

        ShopManagerUserInfoDTO managerUserInfo = customModelMapper.mapIfExist(managerUser, ShopManagerUserInfoDTO.class);
        ShopEmployeeUserInfoDTO employeeUserInfo = customModelMapper.mapIfExist(employeeUser, ShopEmployeeUserInfoDTO.class);

        ShopManagementUsersInfoDTO managementUsers = new ShopManagementUsersInfoDTO();
        managementUsers.setShopManager(managerUserInfo);
        managementUsers.setShopEmployee(employeeUserInfo);

        return new Response<ShopManagementUsersInfoDTO>().withData(managementUsers);
    }

    @Override
    public Response<GroupManagementUserInfoDTO> getGroupManagementUserInfo(GroupManagementUserInfoRequest request) {
        Response<GroupManagementUserInfoDTO> response = new Response<>();

        GroupDTO groupDTO = commonService.getGroupBy(request.getGroupId());
        if (groupDTO == null) {
            throw new ValidateException(ResponseMessage.GROUP_DOES_NOT_EXIST);
        }

        ManagementUsers managerUser = repository.getUserByObjectAndObjectIdAndRoleId(Object.GROUP.getId(),
                request.getGroupId(), this.getGroupManagerRoleId()).orElse(null);

        ManagementUsers employeeUser = repository.getUserByObjectAndObjectIdAndRoleId(Object.GROUP.getId(),
                request.getGroupId(), this.getGroupEmployeeRoleId()).orElse(null);

        GroupManagerUserInfoDTO managerUserInfo = customModelMapper.mapIfExist(managerUser, GroupManagerUserInfoDTO.class);
        GroupEmployeeUserInfoDTO employeeUserInfo = customModelMapper.mapIfExist(employeeUser, GroupEmployeeUserInfoDTO.class);

        GroupManagementUserInfoDTO managementUsers = new GroupManagementUserInfoDTO();
        managementUsers.setGroupManager(managerUserInfo);
        managementUsers.setGroupEmployee(employeeUserInfo);

        return response.withData(managementUsers);
    }

    @Override
    public Response<String> updateShopManagerUser(ShopManagerUserUpdateRequest request) {
//        ShopDTO shop = commonService.getShopBy(request.getShopId());
//        if (shop == null) {
//            throw new ValidateException(ResponseMessage.SALON_DOES_NOT_EXIST);
//        }
//
//        Long managerRoleId = this.getShopManagerRoleId();
//        ManagementUsers user = repository.getUserByObjectAndObjectIdAndRoleId(Object.SHOP.getId(), request.getShopId(), managerRoleId).orElse(null);
//
//        boolean isNotMyselfEmail = (user == null || !user.getEmail().equals(request.getEmail()));
//        boolean isExistEmail = this.isExistEmailExcludeRoleCustomer(request.getEmail());
//
//        if (isExistEmail && isNotMyselfEmail) {
//            return new Response<String>().withError(ResponseMessage.ALREADY_EXISTS_EMAIL);
//        }
//
//        String securePassword = passwordEncoder.encode(request.getPassword());
//
//        user = Optional.ofNullable(user).orElse(new ManagementUsers());
//        user.setUserId(getUserId());
//        user.setObject(Object.SHOP);
//        user.setObjectId(request.getShopId());
//        user.setRoleId(managerRoleId);
//        user.setName(request.getName());
//        user.setEmail(request.getEmail());
//        user.setPassword(securePassword);
//        repository.save(user);

        return new Response<>();
    }

    @Override
    public Response<String> updateGroupManagerUser(GroupManagerUserUpdateRequest request) {
        Response<String> response = new Response<>();

//        GroupDTO groupDTO = commonService.getGroupBy(request.getGroupId());
//        if (groupDTO == null) {
//            throw new ValidateException(ResponseMessage.GROUP_DOES_NOT_EXIST);
//        }
//
//        ManagementUsers managerUser = repository.getUserByObjectAndObjectIdAndRoleId(Object.GROUP.getId(),
//                request.getGroupId(), this.getGroupManagerRoleId()).orElse(null);
//
//        boolean isNotMyselfEmail = (managerUser == null || !managerUser.getEmail().equals(request.getEmail()));
//        boolean isExistEmail = this.isExistEmailExcludeRoleCustomer(request.getEmail());
//
//        if (isExistEmail && isNotMyselfEmail) {
//            return new Response<String>().withError(ResponseMessage.ALREADY_EXISTS_EMAIL);
//        }
//
//        String securePassword = passwordEncoder.encode(request.getPassword());
//
//        managerUser = Optional.ofNullable(managerUser).orElse(new ManagementUsers());
//        managerUser.setUserId(this.getUserId());
//        managerUser.setObject(Object.GROUP);
//        managerUser.setObjectId(request.getGroupId());
//        managerUser.setRoleId(this.getGroupManagerRoleId());
//        managerUser.setName(request.getName());
//        managerUser.setEmail(request.getEmail());
//        managerUser.setPassword(securePassword);
//        repository.save(managerUser);

        return response;
    }

    @Override
    public Response<String> updateShopEmployeeUser(ShopEmployeeUserUpdateRequest request) {
        Response<String> response = new Response<>();
//        ShopDTO shop = commonService.getShopBy(request.getShopId());
//        if (shop == null) {
//            throw new ValidateException(ResponseMessage.SALON_DOES_NOT_EXIST);
//        }
//
//        Long employeeRoleId = this.getShopEmployeeRoleId();
//        ManagementUsers user = repository.getUserByObjectAndObjectIdAndRoleId(Object.SHOP.getId(), request.getShopId(), employeeRoleId).orElse(null);
//
//        boolean isNotMyselfEmail = (user == null || !user.getEmail().equals(request.getEmail()));
//        boolean isExistEmail = this.isExistEmailExcludeRoleCustomer(request.getEmail());
//
//        if (isExistEmail && isNotMyselfEmail) {
//            return response.withError(ResponseMessage.ALREADY_EXISTS_EMAIL);
//        }
//        if (request.getNumberOfDateExpiration() <= 0) {
//            return response.withError(ResponseMessage.USER_NUMBER_OF_DATE_EXPIRATION_MUST_BE_GREATER_THAN_ONE);
//        }
//        user = Optional.ofNullable(user).orElse(new ManagementUsers());
//        user.setUserId(getUserId());
//        user.setObject(Object.SHOP);
//        user.setObjectId(request.getShopId());
//        user.setRoleId(employeeRoleId);
//        user.setEmail(request.getEmail());
//        user.setNumberOfDateExpiration(request.getNumberOfDateExpiration());
//        repository.save(user);

        return response;
    }

    @Override
    public Response<String> updateGroupEmployeeUser(GroupEmployeeUserUpdateRequest request) {
        Response<String> response = new Response<>();

//        GroupDTO groupDTO = commonService.getGroupBy(request.getGroupId());
//        if (groupDTO == null) {
//            throw new ValidateException(ResponseMessage.GROUP_DOES_NOT_EXIST);
//        }
//
//        ManagementUsers employeeUser = repository.getUserByObjectAndObjectIdAndRoleId(Object.GROUP.getId(), request.getGroupId(), this.getGroupEmployeeRoleId()).orElse(null);
//
//        boolean isNotMyselfEmail = (employeeUser == null || !employeeUser.getEmail().equals(request.getEmail()));
//        boolean isExistEmail = this.isExistEmailExcludeRoleCustomer(request.getEmail());
//
//        if (isExistEmail && isNotMyselfEmail) {
//            return response.withError(ResponseMessage.ALREADY_EXISTS_EMAIL);
//        }
//
//        if (request.getNumberOfDateExpiration() <= 0) {
//            return response.withError(ResponseMessage.USER_NUMBER_OF_DATE_EXPIRATION_MUST_BE_GREATER_THAN_ONE);
//        }
//
//        employeeUser = Optional.ofNullable(employeeUser).orElse(new ManagementUsers());
//        employeeUser.setUserId(this.getUserId());
//        employeeUser.setObject(Object.GROUP);
//        employeeUser.setObjectId(request.getGroupId());
//        employeeUser.setRoleId(this.getGroupEmployeeRoleId());
//        employeeUser.setEmail(request.getEmail());
//        employeeUser.setNumberOfDateExpiration(request.getNumberOfDateExpiration());
//        repository.save(employeeUser);

        return response;
    }

    @Override
    public Response<String> updatePasswordShopEmployeeUser(ShopEmployeeUserUpdatePasswordRequest request) {
        Response<String> response = new Response<>();
//        ShopDTO shop = commonService.getShopBy(request.getShopId());
//        if (shop == null) {
//            throw new ValidateException(ResponseMessage.SALON_DOES_NOT_EXIST);
//        }
//
//        Long employeeRoleId = getShopEmployeeRoleId();
//        ManagementUsers shopEmloyeeUser = repository.getUserByObjectAndObjectIdAndRoleId(Object.SHOP.getId(), request.getShopId(), employeeRoleId).orElse(null);
//        if (shopEmloyeeUser == null) {
//            return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);
//        }
//
//        String securePassword = passwordEncoder.encode(request.getPassword());
//        shopEmloyeeUser.setPassword(securePassword);
//        repository.save(shopEmloyeeUser);
        return response;
    }

    @Override
    public Response<String> updatePasswordGroupEmployeeUser(GroupEmployeeUserUpdatePasswordRequest request) {
        Response<String> response = new Response<>();

//        GroupDTO groupDTO = commonService.getGroupBy(request.getGroupId());
//        if (groupDTO == null) {
//            throw new ValidateException(ResponseMessage.GROUP_DOES_NOT_EXIST);
//        }
//        ManagementUsers groupEmployeeUser = repository.getUserByObjectAndObjectIdAndRoleId(Object.GROUP.getId(),
//                request.getGroupId(), this.getGroupEmployeeRoleId()).orElse(null);
//
//        if (groupEmployeeUser == null) {
//            return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);
//        }
//
//        String securePassword = passwordEncoder.encode(request.getPassword());
//        groupEmployeeUser.setPassword(securePassword);
//        repository.save(groupEmployeeUser);

        return response;
    }

    @Override
    public Response<Boolean> isExpiredPasswordShopEmployeeUser(ShopEmployeeExpiredPasswordRequest request) {
        Response<Boolean> response = new Response<>();
//        ShopDTO shop = commonService.getShopBy(request.getShopId());
//        if (shop == null) {
//            throw new ValidateException(ResponseMessage.SALON_DOES_NOT_EXIST);
//        }
//
//        Long employeeRoleId = getShopEmployeeRoleId();
//        ManagementUsers user = repository.getUserByObjectAndObjectIdAndRoleId(Object.SHOP.getId(), request.getShopId(), employeeRoleId).orElse(null);
//        if (user == null) {
//            return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);
//        }
//
//        int numberExpiration = user.getNumberOfDateExpiration();
//        LocalDate dateExpired = user.getUpdatedAt().toLocalDate().plusDays(numberExpiration);
//
//        Boolean isExpired = false;
//        if (dateExpired.isBefore(LocalDate.now())) {
//            isExpired = true;
//        }
//        return response.withData(isExpired);
        return response;
    }

    @Override
    public Response<Boolean> isExpiredPasswordGroupEmployeeUser(GroupEmployeeExpiredPasswordRequest request) {
        Response<Boolean> response = new Response<>();
//        GroupDTO groupDTO = commonService.getGroupBy(request.getGroupId());
//        if (groupDTO == null) {
//            throw new ValidateException(ResponseMessage.GROUP_DOES_NOT_EXIST);
//        }
//        ManagementUsers employeeUser = repository.getUserByObjectAndObjectIdAndRoleId(Object.GROUP.getId(),
//                request.getGroupId(), this.getGroupEmployeeRoleId()).orElse(null);
//
//        int numberExpiration = employeeUser.getNumberOfDateExpiration();
//        LocalDate dateExpired = employeeUser.getUpdatedAt().toLocalDate().plusDays(numberExpiration);
//
//        Boolean isExpired = false;
//        if (dateExpired.isBefore(LocalDate.now())) {
//            isExpired = true;
//        }
//        return response.withData(isExpired);
        return response;
    }

    @Override
    public ManagementUsers getByEmailAndCompanyId(String email) {
        Optional<ManagementUsers> optManagementUsers = repository.getUserByEmail(email);
        return optManagementUsers.orElse(null);
    }

    private boolean isExistEmailExcludeRoleCustomer(String email) {
        boolean isExist = false;

        Long shopownerRoleId = getShopOwnerRoleId();
        UserDTO shopownerUser = userService.getByEmailAndRoleId(email, shopownerRoleId);

        if (shopownerUser != null) {
            isExist = true;
        } else {
            ManagementUsers managementUser = repository.getUserByEmail(email).orElse(null);
            if (managementUser != null) {
                isExist = true;
            }
        }
        return isExist;
    }

    private Long getShopOwnerRoleId() {
        return roleService.getByRoleName(UserRole.SHOP_OWNER).getId();
    }

    private Long getShopManagerRoleId() {
        return roleService.getByRoleName(UserRole.SHOP_MANAGER).getId();
    }

    private Long getShopEmployeeRoleId() {
        return roleService.getByRoleName(UserRole.SHOP_EMPLOYEE).getId();
    }

    private Long getGroupManagerRoleId() {
        return roleService.getByRoleName(UserRole.GROUP_MANAGER).getId();
    }

    private Long getGroupEmployeeRoleId() {
        return roleService.getByRoleName(UserRole.GROUP_EMPLOYEE).getId();
    }

    @Override
    public List<SalonHairdresserResponseDTO> getAllHairdressersInSalon(Long salonId) {
        List<SalonHairdresserResponseDTO> test = repository.findAllHairdressersInSalonAtNullDeleted(salonId, Arrays.asList(
                ManagementPrivileges.SALON_BEAUTICIAN_EMPLOYEE.getId(),
                ManagementPrivileges.SALON_BEAUTICIAN_PART_TIME.getId()
        )).stream().map(hairdresser -> modelMapper.map(hairdresser, SalonHairdresserResponseDTO.class)).
                collect(Collectors.toList());

        return test;
    }

    @Override
    public SalonHairdresserResponseDTO getHairdresserById(Long hairdresserId) {
        return modelMapper.map(repository.findByIdAndDeletedAtIsNull(hairdresserId), SalonHairdresserResponseDTO.class);
    }

    @Override
    public List<SalonConfirmationHairdresserDetailDTO> feignGetHairdressersByIds(List<Long> userIds) {
        return repository.findAllByIdInAndDeletedAtIsNull(userIds).stream().map(
                item -> modelMapper.map(item, SalonConfirmationHairdresserDetailDTO.class)
        ).collect(Collectors.toList());
    }

    @Override
    public List<ManagementUsers> feignGetHairdressersEntityByIds(List<Long> userIds) {
        return repository.findAllByIdInAndDeletedAtIsNull(userIds);
    }

    @Override
    public ManagementUsers getManagementUserById(Long id) {
        return repository.findByIdAndDeletedAtIsNull(id);
    }
}
