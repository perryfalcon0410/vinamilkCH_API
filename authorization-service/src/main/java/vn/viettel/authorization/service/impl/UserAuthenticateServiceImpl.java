package vn.viettel.authorization.service.impl;

import org.modelmapper.convention.MatchingStrategies;
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
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.service.dto.ControlDTO;
import vn.viettel.core.service.dto.DataPermissionDTO;
import vn.viettel.core.service.dto.PermissionDTO;
import vn.viettel.core.util.ResponseMessage;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;
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

    @Autowired
    UserRepository userRepository;

    @Override
    public Response<Object> preLogin(LoginRequest loginInfo, String captchaCode) {
        User user = repository.findByUsername(loginInfo.getUsername())
            .orElseThrow(() -> new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS));

        Response<Object> response = checkLoginValid(user, loginInfo);
        if (Boolean.FALSE.equals(response.getSuccess()))
            return response;

        if (!user.getStatus().equals(1)) throw new ValidateException(ResponseMessage.USER_IS_NOT_ACTIVE);

        LoginResponse resData = modelMapper.map(user, LoginResponse.class);
        if (user.getWrongTime() >= user.getMaxWrongTime()) {
            if (loginInfo.getCaptchaCode() == null) {
                response.setData(new CaptchaDTO(ResponseMessage.ENTER_CAPTCHA_TO_LOGIN, user.getCaptcha()));
                return response.withError(ResponseMessage.ENTER_CAPTCHA_TO_LOGIN);
            }
            if (!loginInfo.getCaptchaCode().equals(user.getCaptcha())) {
                response.setData(new CaptchaDTO(ResponseMessage.WRONG_CAPTCHA, user.getCaptcha()));
                return response.withError(ResponseMessage.WRONG_CAPTCHA);
            }
        }

        List<RoleDTO> roleDTOS = this.getAllRoles(user);

        //TH chỉ có 1 role + 1 shop -> login luôn
        if (roleDTOS.size() == 1 ) {
            RoleDTO usedRole = roleDTOS.get(0);
            if(usedRole.getShops() != null && usedRole.getShops().size() == 1) {
                ShopDTO shopDTO = usedRole.getShops().get(0);
                // Các step trước đã lọc shop ko tồn tại
                Shop shop = shopRepository.findById(shopDTO.getId()).get();
                // Check quyền dữ liệu
                List<Permission> permissionsType2 = permissionRepository.findPermissionType2(usedRole.getId(), shop.getId());
                if(permissionsType2.isEmpty()) throw new ValidateException(ResponseMessage.NO_PERMISSION_TYPE_2);
                // 1 shop duy nhất có loại = 4
                List<PermissionDTO> permissions = getUserPermission(usedRole.getId());
                if (permissions.isEmpty())
                    return response.withError(ResponseMessage.NO_FUNCTIONAL_PERMISSION);
                shopDTO.setAddress(shop.getAddress() + ", " + getShopArea(shop.getAreaId()));
                setOnlineOrderPermission(shopDTO, shop);

                resData.setUsedShop(shopDTO);
                resData.setUsedRole(usedRole);
                resData.setPermissions(permissions);
                response.setToken(createToken(user, usedRole.getRoleName(), shopDTO.getId(), usedRole.getId()));

                saveLoginLog(shopDTO.getId(), user.getUserAccount());
            }
        }

        resData.setRoles(roleDTOS);
        response.setData(resData);

        user.setWrongTime(0);
        repository.save(user);
        return response.withData(resData);
    }


    //Tất cả các quyền của user có
    private List<RoleDTO> getAllRoles(User user) {
        List<Role> roles = roleRepository.findRoles(user.getId());
        if(roles.isEmpty()) throw new ValidateException(ResponseMessage.USER_HAVE_NO_ROLE);
        List<Role> roleActives = permissionRepository.findRoles(roles.stream().map(Role::getId).collect(Collectors.toList()));

        if(roleActives.isEmpty()) throw new ValidateException(ResponseMessage.NO_PERMISSION_ASSIGNED);

        List<RoleDTO> roleDTOS = roleActives.stream().map(role -> modelMapper.map(role, RoleDTO.class)).collect(Collectors.toList());

        List<ShopDTO> shops = new ArrayList<>();
        for (RoleDTO roleDTO : roleDTOS) {

            List<ShopDTO> shopInRoles = checkShopContain(shops, getShopByRole(roleDTO.getId()));
            roleDTO.setShops(shopInRoles);
            shops.addAll(shopInRoles);
        }

        if (shops.isEmpty()) throw new ValidateException(ResponseMessage.NO_PRIVILEGE_ON_ANY_SHOP);

        /*
         *  TH1 chỉ có 1 role + 1 shop có ShopType = 4 -> return
         *  TH2 chỉ có 1 role + 1 ShopType != 4 -> lấy shop con có ShopType = 4 -> return
         */
        if (roleDTOS.size() == 1 ) {
            RoleDTO usedRole = roleDTOS.get(0);
            if(usedRole.getShops() != null && usedRole.getShops().size() == 1) {
                ShopDTO usedShop = usedRole.getShops().get(0);
                // Các step trước đã lọc shop cha ko tồn tại
                Shop shop = shopRepository.findById(usedShop.getId()).get();
                if(shop.getShopType() == null || !shop.getShopType().equals("4")) {
                    // 1 shop duy nhất có loại != 4 lấy shop con có loại = 4 status =1
                    List<Shop> subShops = shopRepository.findByParentShopIdAndShopTypeAndStatus(shop.getId(), "4", 1);
                    subShops.add(shop);
                    List<ShopDTO> shopDTOs = subShops.stream().map(s -> modelMapper.map(s, ShopDTO.class)).collect(Collectors.toList());
                    roleDTOS.get(0).setShops(shopDTOs);
                }

                return roleDTOS;
            }
        }

        //Có nhiều role nếu có shop nào là cha thì lấy thêm đơn vị có shop_type = 4
        for (RoleDTO roleDTO : roleDTOS) {
            List<ShopDTO> shopDTOS = new ArrayList<>();
            shopDTOS.addAll(roleDTO.getShops());
            for(ShopDTO shop: roleDTO.getShops()) {
                List<Shop> subShops = shopRepository.findByParentShopIdAndShopTypeAndStatus(shop.getId(), "4", 1);
                List<ShopDTO> subShopDTOs = subShops.stream().map(s -> modelMapper.map(s, ShopDTO.class)).collect(Collectors.toList());
                shopDTOS.addAll(subShopDTOs);
            }
            roleDTO.setShops(shopDTOS);
        }

        return roleDTOS;
    }


    @Override
    public Response<Object> login(LoginRequest loginInfo) {

        User user = repository.findByUsername(loginInfo.getUsername())
                .orElseThrow(() -> new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS));

        Shop shop = shopRepository.findById(loginInfo.getShopId())
                .orElseThrow(() -> new ValidateException(ResponseMessage.SHOP_NOT_FOUND));

        Response<Object> response = checkLoginValid(user, loginInfo);
        if (Boolean.FALSE.equals(response.getSuccess()))
            return response;

        if (!user.getStatus().equals(1)) throw new ValidateException(ResponseMessage.USER_IS_NOT_ACTIVE);

        List<RoleDTO> roleDTOS = this.getAllRoles(user);
        List<Long> roleIds = roleDTOS.stream().map(RoleDTO::getId).collect(Collectors.toList());

        //step getAllRoles đã check role tồn tại
        if(!roleIds.contains(loginInfo.getRoleId()))
            throw new ValidateException(ResponseMessage.USER_ROLE_NOT_MATCH);

        if(!this.checkPermissionType2(loginInfo.getRoleId(), shop))
            throw new ValidateException(ResponseMessage.NO_PERMISSION_TYPE_2);

        Role role = roleRepository.findById(loginInfo.getRoleId()).get();

        RoleDTO roleDTO = null;
        for(RoleDTO roleD : roleDTOS) {
            if(roleD.getId().equals(loginInfo.getRoleId())) roleDTO = roleD;
        }
        List<Long> shopIds = roleDTO.getShops().stream().map(ShopDTO::getId).collect(Collectors.toList());

        if (getUserPermission(loginInfo.getRoleId()).isEmpty() || !shopIds.contains(loginInfo.getShopId()))
            return response.withError(ResponseMessage.NO_FUNCTIONAL_PERMISSION);

        LoginResponse resData = modelMapper.map(user, LoginResponse.class);
        ShopDTO usedShop = new ShopDTO(loginInfo.getShopId(), shop.getShopName(),
                shop.getAddress() + ", " + getShopArea(shop.getAreaId()),
                shop.getMobiPhone(), shop.getEmail());
        setOnlineOrderPermission(usedShop, shop);

        resData.setUsedShop(usedShop);
        List<PermissionDTO> permissions = getUserPermission(loginInfo.getRoleId());
        resData.setPermissions(permissions);

        resData.setRoles(null);
        resData.setUsedRole(modelMapper.map(role, RoleDTO.class));

        response.setToken(createToken(user, role.getRoleName(), shop.getId(), role.getId()));
        response.setData(resData);

        saveLoginLog(shop.getId(), user.getUserAccount());
        return response;
    }
    /*
     * Kiểm tra role và shop/shopcon có quyền dữ liệu nào ko khi đăng nhập
     */
    public Boolean checkPermissionType2(Long roleId, Shop shop) {
        List<Permission> permissionsType2 = permissionRepository.findPermissionType2(roleId, shop.getId());
        if(permissionsType2.isEmpty()) {
            List<Permission> permissionsType2Parent = new ArrayList<>();
            if(shop.getParentShopId()!=null) {
                permissionsType2Parent = permissionRepository.findPermissionType2(roleId, shop.getParentShopId());
            }
            if(permissionsType2Parent.isEmpty()) return false;
        }

        return true;
    }

    @Override
    public Response<Object> changePassword(ChangePasswordRequest request) {
        Response<Object> response = new Response<>();

        User user = repository.findByUsername(request.getUsername())
            .orElseThrow(() -> new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS));

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
        user.setPassword(securePassword);
        try {
            repository.save(user);
        } catch (Exception e) {
            return response.withError(ResponseMessage.CHANGE_PASSWORD_FAIL);
        }

        response.setSuccess(true);
        response.setStatusCode(204);
        response.setStatusValue(ResponseMessage.CHANGE_PASSWORD_SUCCESS.statusCodeValue());
        return response;
    }

    public String createToken(User user, String role, Long shopId, Long roleId) {
        return jwtTokenCreate.createToken(ClaimsTokenBuilder.build(role)
                .withUserId(user.getId()).withUserName(user.getUserAccount()).withShopId(shopId).withRoleId(roleId)
                .withPermission(getDataPermission(roleId)).get());
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

    public Response<Object> checkLoginValid(User user, LoginRequest loginInfo) {
        Response<Object> response = new Response<>();
        response.setSuccess(false);

//        String securePassword = passwordEncoder.encode(loginInfo.getPassword()).toUpperCase();
        int wrongTime = user.getWrongTime();
//        if(!securePassword.equals( user.getPassword().toUpperCase())){
        if (!passwordEncoder.matches(loginInfo.getPassword(), user.getPassword())) {
            wrongTime++;
            user.setWrongTime(wrongTime);
            repository.save(user);
            if (wrongTime >= user.getMaxWrongTime()) {
                String captcha = generateCaptchaString();
                user.setCaptcha(captcha);
                repository.save(user);

                response.setData(new CaptchaDTO(ResponseMessage.INCORRECT_PASSWORD, user.getCaptcha()));
                return response.withError(ResponseMessage.INCORRECT_PASSWORD);
            }
            return response.withError(ResponseMessage.INCORRECT_PASSWORD);
        }

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
        List<RoleDTO> roleDTOs = new ArrayList<>();
        List<RoleUser> userRoles = userRoleRepository.findByUserIdAndStatus(userId, 1);
        List<Role> roles = roleRepository.findByIdInAndStatus(userRoles.stream().map(RoleUser::getRoleId).collect(Collectors.toList()), 1);
        for (Role role: roles) roleDTOs.add(modelMapper.map(role, RoleDTO.class));

        return roleDTOs;
    }

    public List<ShopDTO> getUserManageShops(Long roleId) {
        List<ShopDTO> result = new ArrayList<>();
        List<Long> listShopId = orgAccessRepository.finShopIdByRoleId(roleId);

        for (Long shopId : listShopId) {
            if (shopRepository.findById(shopId.longValue()).isPresent()) {
                Shop shop = shopRepository.findByIdAndStatus(shopId, 1).orElse(null);
                if(shop == null) continue;
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
            if (permission.getIsFullPrivilege() == 1) {
                result.addAll(getPermissionWhenFullPrivilege(permission));
                return result;
            }

            Form form = formRepository.findByIdAndStatus(funcAccess.getFormId(), 1);
            if (form != null)
                setUserPermission(permission, result, form, controlRepository.findByFormIdAndStatus(funcAccess.getFormId(), 1),
                        funcAccess.getShowStatus(), false);
        }
        return result;
    }

    public List<PermissionDTO> getPermissionWhenFullPrivilege(Permission permission) {
        List<PermissionDTO> result = new ArrayList<>();
        List<FunctionAccess> functionAccess = functionAccessRepository.findByPermissionId(permission.getId());

        for (FunctionAccess funcAccess : functionAccess) {
            Form form = formRepository.findByIdAndStatus(funcAccess.getFormId(), 1);
            if (form != null)
                setUserPermission(permission, result, form, controlRepository.findByFormIdAndStatus(funcAccess.getFormId(), 1),
                        funcAccess.getShowStatus(), true);
        }
        return result;
    }

    public void setUserPermission(Permission permission, List<PermissionDTO> result, Form form, List<Control> controls, int showStatus, boolean isFullPrivilege) {
        PermissionDTO permissionDTO = modelMapper.map(form, PermissionDTO.class);
        permissionDTO.setPrivilegeType(permission.getPermissionType());
        List<ControlDTO> listControl = controls.stream().map(ctrl -> modelMapper.map(ctrl, ControlDTO.class)).collect(Collectors.toList());

        for (ControlDTO control : listControl) {
            if (isFullPrivilege)
                control.setShowStatus(ShowStatus.getValueOf(1));
            else
                control.setShowStatus(ShowStatus.getValueOf(showStatus));
        }
        permissionDTO.setControls(listControl);

        if (!checkPermissionContain(result, form))
            result.add(permissionDTO);
    }

    public List<DataPermissionDTO> getDataPermission(Long roleId) {
        List<DataPermissionDTO> result = new ArrayList<>();
        List<BigDecimal> permissionIds = permissionRepository.findByRoleId(roleId);
        if (permissionIds.size() == 0)
            return new ArrayList<>();
        List<BigDecimal> shopIds = orgAccessRepository.findShopIdByPermissionId(permissionIds);

        for (int i = 0; i < permissionIds.size(); i++) {
            DataPermissionDTO dataPermissionDTO = new DataPermissionDTO(permissionIds.get(i).longValue(), shopIds.get(i).longValue());
            result.add(dataPermissionDTO);
        }
        return result;
    }

    public boolean checkPermissionContain(List<PermissionDTO> list, Form form) {
        return list.stream().anyMatch(perm -> perm.getFormCode().equalsIgnoreCase(form.getFormCode()));
    }

    public String getShopArea(Long areaId) {
        AreaDTO ward = areaClient.getByIdV1(areaId).getData();
        AreaDTO district = areaClient.getByIdV1(ward.getParentAreaId()).getData();
        return ward.getAreaName() + ", " + district.getAreaName();
    }

    @Override
    public UserDTO getUserById(long id) {
        return repository.findById(id).isPresent() ? modelMapper.map(repository.findById(id).get(), UserDTO.class) : null;
    }

    @Override
    public List<UserDTO> getUserByIds(List<Long> userIds){
        if (userIds == null || userIds.isEmpty()) return null;
        List<User> users = repository.getUserByIds(userIds);
        if (users == null || users.isEmpty()) return null;
        return users.stream().map(item -> modelMapper.map(item, UserDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Boolean gateWayCheckPermissionType2(Long roleId, Long shopId) {
        Shop shop = shopRepository.findByIdAndStatus(shopId, 1).orElse(null);
        if(shop == null )return false;
        return this.checkPermissionType2(roleId, shop);
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


    @Override
    public List<UserDTO> getDataUser(Long shopId) {
        List<User> userList = userRepository.findAllByShopId(shopId);
        List<UserDTO> dtoList = new ArrayList<>();
        for (User user : userList){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            UserDTO userDTO = modelMapper.map(user,UserDTO.class);
           dtoList.add(userDTO);
        }
        for (int i = 0; i < dtoList.size(); i++) {
            for (int j = i + 1; j < dtoList.size(); j++) {
                if (dtoList.get(i).getId().equals(dtoList.get(j).getId())) {
                    dtoList.remove(j);
                    j--;
                }
            }
        }
        return dtoList;
    }
}
