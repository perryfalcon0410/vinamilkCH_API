package vn.viettel.authorization.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.viettel.authorization.entities.*;
import vn.viettel.authorization.repository.*;
import vn.viettel.authorization.security.ClaimsTokenBuilder;
import vn.viettel.authorization.security.JwtTokenCreate;
import vn.viettel.authorization.service.UserAuthenticateService;
import vn.viettel.authorization.service.dto.*;
import vn.viettel.authorization.service.feign.AreaClient;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserAuthenticateServiceImpl extends BaseServiceImpl<User, UserRepository> implements UserAuthenticateService {
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    RolePermissionMapRepository rolePermissionMapRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    OrgAccessRepository orgAccessRepository;

    @Autowired
    FunctionAccessRepository functionAccessRepository;

    @Autowired
    FormRepository formRepository;

    @Autowired
    ControlRepository controlRepository;

    @Autowired
    UserLogRepository userLogRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenCreate jwtTokenCreate;

    @Autowired
    ShopRepository shopRepository;

    @Autowired
    ShopParamRepository shopParamRepository;

    @Autowired
    AreaClient areaClient;

    private User user;

    @Override
    public Response<Object> preLogin(LoginRequest loginInfo, String captchaCode) {
        Response<Object> response = checkLoginValid(loginInfo);

        if (Boolean.FALSE.equals(response.getSuccess()))
            return response;

        user = repository.findByUsername(loginInfo.getUsername());
        LoginResponse resData = modelMapper.map(user, LoginResponse.class);
        if (user.getWrongTime() > user.getMaxWrongTime()) {
            if (loginInfo.getCaptchaCode() == null)
                return response.withData(new CaptchaDTO(ResponseMessage.ENTER_CAPTCHA_TO_LOGIN, user.getCaptcha()));
            if (!loginInfo.getCaptchaCode().equals(user.getCaptcha()))
                return response.withData(new CaptchaDTO(ResponseMessage.WRONG_CAPTCHA, user.getCaptcha()));
        }

        List<RoleDTO> roleList = getUserRoles(user.getId());
        if (roleList.isEmpty())
            return response.withError(ResponseMessage.USER_HAVE_NO_ROLE);
        List<Permission> permissionList = permissionRepository.findByListRoleId(getUserRoleIds(user.getId()));
        if (permissionList.isEmpty()) return response.withError(ResponseMessage.NO_PERMISSION_ASSIGNED);

        List<ShopDTO> shops = new ArrayList<>();
        for (RoleDTO roleDTO : roleList) {
            List<ShopDTO> shopInRoles = checkShopContain(shops, getShopByRole(roleDTO.getId()));
            shops.addAll(shopInRoles);
            roleDTO.setShops(shops);
        }
        if (shops.isEmpty())
            return response.withError(ResponseMessage.NO_PRIVILEGE_ON_ANY_SHOP);

        if (roleList.size() == 1 && shops.size() == 1) {
            ShopDTO usedShop = shops.get(0);
            RoleDTO usedRole = roleList.get(0);
            Shop shop = shopRepository.findById(usedShop.getId()).get();

            if (shop.getStatus() == 0)
                return response.withError(ResponseMessage.SHOP_IS_NOT_ACTIVE);
            if (getUserPermission(usedRole.getId()).isEmpty())
                return response.withError(ResponseMessage.NO_FUNCTIONAL_PERMISSION);
            usedShop.setAddress(shop.getAddress() + ", " + getShopArea(shop.getAreaId()));
            setOnlineOrderPermission(usedShop, shop);

            resData.setUsedShop(usedShop);
            resData.setUsedRole(usedRole);
            resData.setPermissions(getUserPermission(usedRole.getId()));
            response.setToken(createToken(usedRole.getRoleName(), usedShop.getId(),
                    usedRole.getId()));

            saveLoginLog(usedShop.getId(), user.getUserAccount());
        }
        resData.setRoles(roleList);
        response.setData(resData);

        user.setWrongTime(0);
        repository.save(user);
        return response.withData(resData);
    }

    @Override
    public Response<Object> login(LoginRequest loginInfo) {
        Response<Object> response = checkLoginValid(loginInfo);

        if (Boolean.FALSE.equals(response.getSuccess()))
            return response;

        user = repository.findByUsername(loginInfo.getUsername());
        if (user == null)
            return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);

        if (!checkUserMatchRole(user.getId(), loginInfo.getRoleId()))
            return response.withError(ResponseMessage.USER_ROLE_NOT_MATCH);
        Role role = new Role();
        if (roleRepository.findById(loginInfo.getRoleId()).isPresent())
            role = roleRepository.findById(loginInfo.getRoleId()).get();

        if (getUserPermission(role.getId()).isEmpty() || !checkShopByRole(loginInfo.getRoleId(), loginInfo.getShopId()))
            return response.withError(ResponseMessage.NO_FUNCTIONAL_PERMISSION);

        LoginResponse resData = modelMapper.map(user, LoginResponse.class);
        Shop shop = new Shop();
        if (shopRepository.findById(loginInfo.getShopId()).isPresent())
            shop = shopRepository.findById(loginInfo.getShopId()).get();

        ShopDTO usedShop = new ShopDTO(loginInfo.getShopId(), shop.getShopName(),
                shop.getAddress() + ", " + getShopArea(shop.getAreaId()),
                shop.getMobiPhone(), shop.getEmail());
        setOnlineOrderPermission(usedShop, shop);

        resData.setUsedShop(usedShop);
        resData.setPermissions(getUserPermission(loginInfo.getRoleId()));

        resData.setRoles(null);
        resData.setUsedRole(modelMapper.map(role, RoleDTO.class));

        response.setToken(createToken(role.getRoleName(), shop.getId(), role.getId()));
        response.setData(resData);

        saveLoginLog(shop.getId(), user.getUserAccount());
        return response;
    }

    @Override
    public Response<Object> changePassword(ChangePasswordRequest request) {
        Response<Object> response = new Response<>();

        user = repository.findByUsername(request.getUsername());
        if (user == null)
            return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            return response.withError(ResponseMessage.USER_OLD_PASSWORD_NOT_CORRECT);

        if (request.getNewPassword().length() < 8 || request.getNewPassword().length() > 20)
            return new Response<>().withError(ResponseMessage.INVALID_PASSWORD_LENGTH);

        if (request.getOldPassword().equals(request.getNewPassword()))
            return response.withError(ResponseMessage.DUPLICATE_PASSWORD);

        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            return response.withError(ResponseMessage.CONFIRM_PASSWORD_NOT_CORRECT);

        if (user.getPasswordConfig() != null && user.getPasswordConfig() == 1 &&
                checkPassword(request.getNewPassword()).getData() == null)
            return checkPassword(request.getNewPassword());

        String securePassword = passwordEncoder.encode(request.getNewPassword());
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());
        user.setUpdatedAt(time);
        user.setPassword(securePassword);
        try {
            repository.save(user);
        } catch (Exception e) {
            return response.withError(ResponseMessage.CHANGE_PASSWORD_FAIL);
        }

        return response.withData(ResponseMessage.CHANGE_PASSWORD_SUCCESS.toString());
    }

    public String createToken(String role, Long shopId, Long roleId) {
        return jwtTokenCreate.createToken(ClaimsTokenBuilder.build(role)
                .withUserId(user.getId()).withShopId(shopId).withRoleId(roleId).get());
    }

    public boolean checkShopByRole(Long roleId, Long shopId) {
        return getShopByRole(roleId).stream().anyMatch(shop -> shop.getId().equals(shopId));
    }

    public boolean checkUserMatchRole(Long userId, Long roleId) {
        return getUserRoles(userId).stream().anyMatch(role -> role.getId().equals(roleId));
    }

    public List<ShopDTO> checkShopContain(List<ShopDTO> shopList, List<ShopDTO> subList) {
        List<ShopDTO> result = new ArrayList<>();
        for (ShopDTO sub : subList) {
            if (!shopList.stream().anyMatch(shop -> shop.getId().equals(sub.getId())))
                result.add(sub);
        }
        return result;
    }

    public Response<Object> checkLoginValid(LoginRequest loginInfo) {
        Response<Object> response = new Response<>();
        response.setSuccess(false);

        user = repository.findByUsername(loginInfo.getUsername());
        if (user == null)
            return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);

        int wrongTime = user.getWrongTime();
        if (!passwordEncoder.matches(loginInfo.getPassword(), user.getPassword())) {
            wrongTime++;
            user.setWrongTime(wrongTime);
            repository.save(user);
            if (wrongTime > user.getMaxWrongTime()) {
                String captcha = generateCaptchaString();
                user.setCaptcha(captcha);
                repository.save(user);
                return response.withData(new CaptchaDTO(ResponseMessage.INCORRECT_PASSWORD, user.getCaptcha()));
            }
            return response.withError(ResponseMessage.INCORRECT_PASSWORD);
        }

        if (user.getStatus() == 0)
            return response.withError(ResponseMessage.USER_IS_NOT_ACTIVE);

        if (loginInfo.getShopId() != null) {
            if (Boolean.FALSE.equals(shopRepository.findById(loginInfo.getShopId()).isPresent()))
                return response.withError(ResponseMessage.SHOP_NOT_FOUND);
            if (shopRepository.findById(loginInfo.getShopId()).get().getStatus() != 1)
                return response.withError(ResponseMessage.SHOP_IS_NOT_ACTIVE);
        }
        response.setSuccess(true);
        return response;
    }

    public List<RoleDTO> getUserRoles(Long userId) {
        List<RoleDTO> roles = new ArrayList<>();
        userRoleRepository.findByUserId(userId).
                forEach(e -> {
                    if (roleRepository.findById(e.getRoleId()).isPresent())
                        roles.add(modelMapper.map(roleRepository.findById(e.getRoleId()).get(), RoleDTO.class));
                });
        return roles;
    }

    public List<Long> getUserRoleIds(Long userId) {
        List<Long> roles = new ArrayList<>();
        userRoleRepository.findByUserId(userId).
                forEach(e -> roles.add(e.getRoleId()));
        return roles;
    }

    public List<ShopDTO> getUserManageShops(Long roleId) {
        List<ShopDTO> result = new ArrayList<>();
        List<BigDecimal> listShopId = orgAccessRepository.finShopIdByRoleId(roleId);

        for (BigDecimal shopId : listShopId) {
            if (shopRepository.findById(shopId.longValue()).isPresent()) {
                Shop shop = shopRepository.findById(shopId.longValue()).get();
                ShopDTO shopDTO = modelMapper.map(shop, ShopDTO.class);
                result.add(shopDTO);
            }
        }
        return result;
    }

    @Override
    public List<PermissionDTO> getUserPermission(Long roleId) {
        List<PermissionDTO> result = new ArrayList<>();
        List<FunctionAccess> functionAccessList = functionAccessRepository.findByRoleId(roleId);

        for (FunctionAccess funcAccess : functionAccessList) {
            Permission permission = new Permission();
            if (permissionRepository.findById(funcAccess.getPermissionId()).isPresent())
                permission = permissionRepository.findById(funcAccess.getPermissionId()).get();
            if (permission.getIsFullPrivilege() == 1)
                result.addAll(getPermissionWhenFullPrivilege(permission.getId()));

            if (formRepository.findById(funcAccess.getFormId()).isPresent())
                setUserPermission(result, formRepository.findById(funcAccess.getFormId()).get(),
                        controlRepository.findByFormId(funcAccess.getFormId()), funcAccess.getShowStatus());
        }
        return result;
    }

    public List<PermissionDTO> getPermissionWhenFullPrivilege(Long permissionId) {
        List<PermissionDTO> result = new ArrayList<>();
        List<FunctionAccess> functionAccess = functionAccessRepository.findByPermissionId(permissionId);

        for (FunctionAccess funcAccess : functionAccess) {
            if (formRepository.findById(funcAccess.getFormId()).isPresent())
                setUserPermission(result, formRepository.findById(funcAccess.getFormId()).get(),
                        controlRepository.findByFormId(funcAccess.getFormId()), funcAccess.getShowStatus());
        }
        return result;
    }

    public void setUserPermission(List<PermissionDTO> result, Form form, List<Control> controls, int showStatus) {
        PermissionDTO permissionDTO = modelMapper.map(form, PermissionDTO.class);
        List<ControlDTO> listControl = controls.stream().map(ctrl -> modelMapper.map(ctrl, ControlDTO.class)).collect(Collectors.toList());

        for (ControlDTO control : listControl)
            control.setShowStatus(ShowStatus.getValueOf(showStatus));
        permissionDTO.setControls(listControl);

        if (!checkPermissionContain(result, form))
            result.add(permissionDTO);
    }

    public boolean checkPermissionContain(List<PermissionDTO> list, Form form) {
        return list.stream().anyMatch(perm -> perm.getFormCode().equalsIgnoreCase(form.getFormCode()));
    }

    public String getShopArea(Long areaId) {
        AreaDTO ward = areaClient.getById(areaId).getData();
        AreaDTO district = areaClient.getById(ward.getParentAreaId()).getData();
        return ward.getAreaName() + ", " + district.getAreaName();
    }

    @Override
    public UserDTO getUserById(long id) {
        return repository.findById(id).isPresent() ? modelMapper.map(repository.findById(id).get(), UserDTO.class) : null;
    }

    @Override
    public List<ShopDTO> getShopByRole(Long roleId) {
        return getUserManageShops(roleId);
    }

    public Response<Object> checkPassword(String password) {
        boolean containNum = false;
        boolean containUpperCase = false;
        boolean containLowerCase = false;

        for (int i = 0; i < password.length(); i++) {
            char letter = password.charAt(i);
            if (Character.isDigit(letter))
                containNum = true;
            else if (Character.isUpperCase(letter))
                containUpperCase = true;
            else if (Character.isLowerCase(letter))
                containLowerCase = true;
        }
        boolean containSpecialCharacter = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();
        return containNum && containUpperCase && containLowerCase && containSpecialCharacter ? new Response<>().withData("OK") :
                new Response<>().withError(ResponseMessage.INVALID_PASSWORD_FORMAT);
    }

    public void saveLoginLog(Long shopId, String userAccount) {
        UserLogOnTime userLogOnTime = new UserLogOnTime();
        userLogOnTime.setShopId(shopId);
        userLogOnTime.setAccount(userAccount);
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String hostName = inetAddress.getHostName(); //Get Host Name
            String macAddress = "";
            String ipAddress = inetAddress.getHostAddress(); // Get IP Address
            //Get MAC Address
            NetworkInterface network = NetworkInterface.getByInetAddress(inetAddress);
            byte[] macArray = network.getHardwareAddress();
            StringBuilder str = new StringBuilder();
            // Convert the macArray to String
            for (int i = 0; i < macArray.length; i++) {
                str.append(String.format("%02X%s", macArray[i], (i < macArray.length - 1) ? " " : ""));
                macAddress = str.toString();
            }
            Date date = new Date();
            Timestamp time = new Timestamp(date.getTime());
            userLogOnTime.setLogCode(hostName + "_" + macAddress + "_" + time);
            userLogOnTime.setComputerName(hostName);
            userLogOnTime.setMacAddress(macAddress);
            userLogOnTime.setCreatedAt(time);

            userLogRepository.save(userLogOnTime);
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public String generateCaptchaString() {
        Random random = new Random();
        int length = 7 + (Math.abs(random.nextInt()) % 3);

        StringBuffer captchaStringBuffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int baseCharNumber = Math.abs(random.nextInt()) % 62;
            int charNumber;
            if (baseCharNumber < 26) {
                charNumber = 65 + baseCharNumber;
            } else if (baseCharNumber < 52) {
                charNumber = 97 + (baseCharNumber - 26);
            } else {
                charNumber = 48 + (baseCharNumber - 52);
            }
            captchaStringBuffer.append((char) charNumber);
        }
        return captchaStringBuffer.toString();
    }

    public void setOnlineOrderPermission(ShopDTO usedShop, Shop shop) {
        if (shopParamRepository.isEditable(shop.getId()) != null)
            usedShop.setEditable(true);
        else
            usedShop.setEditable(false);

        if (shopParamRepository.isManuallyCreatable(shop.getId()) != null)
            usedShop.setManuallyCreatable(true);
        else
            usedShop.setManuallyCreatable(false);
    }
}
