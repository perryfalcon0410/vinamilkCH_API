package vn.viettel.authorization.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.authorization.entities.*;
import vn.viettel.authorization.repository.*;
import vn.viettel.authorization.security.ClaimsTokenBuilder;
import vn.viettel.authorization.security.JwtTokenCreate;
import vn.viettel.authorization.service.ShopService;
import vn.viettel.authorization.service.UserAuthenticateService;
import vn.viettel.authorization.service.dto.*;
import vn.viettel.authorization.service.feign.AreaClient;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.jms.JMSSender;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.JwtTokenBody;
import vn.viettel.core.security.JwtTokenValidate;
import vn.viettel.core.security.context.SecurityContexHolder;
import vn.viettel.core.security.context.UserContext;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.service.dto.DataPermissionDTO;
import vn.viettel.core.service.dto.PermissionDTO;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.utils.JMSType;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

    @Autowired
    JMSSender jmsSender;

    @Autowired
    ShopService shopService;

    @Autowired
    JwtTokenValidate jwtTokenValidate;

    @Autowired
    SecurityContexHolder contexHolder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Object> preLogin(LoginRequest loginInfo) {
        User user = repository.findByUsername(loginInfo.getUsername())
            .orElseThrow(() -> new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS));

        int wrongTime = user.getWrongTime()!=null?user.getWrongTime():0;
        int maxWrongTime = user.getMaxWrongTime()!=null?user.getMaxWrongTime():0;

        Response<Object> response = new Response<>();
        if (wrongTime >= maxWrongTime) {
            if (loginInfo.getCaptchaCode() == null) {
                response.setData(new CaptchaDTO(ResponseMessage.ENTER_CAPTCHA_TO_LOGIN, user.getCaptcha()));
                return response.withError(ResponseMessage.ENTER_CAPTCHA_TO_LOGIN);
            }
            if (!loginInfo.getCaptchaCode().equals(user.getCaptcha())) {
                String captcha = generateCaptchaString();
                user.setCaptcha(captcha);
                repository.save(user);
                response.setData(new CaptchaDTO(ResponseMessage.WRONG_CAPTCHA, user.getCaptcha()));
                return response.withError(ResponseMessage.WRONG_CAPTCHA);
            }
        }

        if (!passwordEncoder.matches(loginInfo.getPassword(), user.getPassword())) {
            wrongTime += 1;
            if (wrongTime >= maxWrongTime) {
                String captcha = generateCaptchaString();
                if(maxWrongTime == wrongTime - 1)  user.setWrongTime(wrongTime);
                user.setCaptcha(captcha);
                repository.save(user);
                response.setData(new CaptchaDTO(ResponseMessage.INCORRECT_PASSWORD, user.getCaptcha()));
                return response.withError(ResponseMessage.INCORRECT_PASSWORD);
            }

            user.setWrongTime(wrongTime);
            repository.save(user);
            return response.withError(ResponseMessage.INCORRECT_PASSWORD);
        }

        LoginResponse resData = modelMapper.map(user, LoginResponse.class);
        List<RoleDTO> roleDTOS = this.getAllRoles(user);

        //TH chỉ có 1 role + 1 shop -> login luôn (1 shop duy nhất có loại = 4)
        if (roleDTOS.size() == 1 ) {
            RoleDTO usedRole = roleDTOS.get(0);
            if(usedRole.getShops() != null && usedRole.getShops().size() == 1) {
                ShopDTO shopDTO = usedRole.getShops().get(0);
                // Các step trước đã lọc shop ko tồn tại
                Shop shop = shopRepository.findById(shopDTO.getId()).get();
                List<FormDTO> froms = this.getForms(usedRole.getId());
                shopDTO.setAddress(shop.getAddress());
                setOnlineOrderPermission(shopDTO, shop);

                resData.setUsedShop(shopDTO);
                resData.setUsedRole(usedRole);
                resData.setForms(froms);
                response.setToken(createToken(user, usedRole.getRoleName(), shopDTO.getId(), usedRole.getId()));
                this.loginSuccess(user.getUserAccount(), shopDTO.getId(), user.getId());
                saveLoginLog(shopDTO.getId(), user.getUserAccount());
            }
        }

        resData.setRoles(roleDTOS);
        response.setData(resData);

        user.setWrongTime(0);
        user.setCaptcha(null);
        repository.save(user);
        return response.withData(resData);
    }


    //Tất cả các quyền của user có
    private List<RoleDTO> getAllRoles(User user) {
        //Các role mà user có
        List<Role> roles = roleRepository.findRoles(user.getId());
        if(roles.isEmpty()) throw new ValidateException(ResponseMessage.USER_HAVE_NO_ROLE);
        //Role + shop có quyền dữ liệu
        List<Role> roleActives = permissionRepository.findRoles(roles.stream().map(Role::getId).collect(Collectors.toList()));
        if(roleActives.isEmpty()) throw new ValidateException(ResponseMessage.NO_PRIVILEGE_ON_ANY_SHOP);

        List<RoleDTO> roleDTOS = roleActives.stream().map(role -> modelMapper.map(role, RoleDTO.class)).collect(Collectors.toList());

        for (RoleDTO roleDTO : roleDTOS) {
            List<Shop> shopInRoles = permissionRepository.findShops(roleDTO.getId());
            List<ShopDTO> shopDTOS = shopInRoles.stream().map(s -> modelMapper.map(s, ShopDTO.class)).collect(Collectors.toList());
            roleDTO.setShops(shopDTOS);
        }

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
//                    List<Shop> subShops = shopRepository.findByParentShopIdAndShopTypeAndStatus(shop.getId(), "4", 1);
//                    subShops.add(shop);
//                    List<ShopDTO> shopDTOs = subShops.stream().map(s -> modelMapper.map(s, ShopDTO.class)).collect(Collectors.toList());
//                    roleDTOS.get(0).setShops(shopDTOs);
                    List<Shop> shops = getChildrenShop(shop.getId());
                    shops.add(shop);
                    List<ShopDTO> shopDTOs = shops.stream().map(s -> modelMapper.map(s, ShopDTO.class)).collect(Collectors.toList());
                    roleDTOS.get(0).setShops(shopDTOs);
                }

                return roleDTOS;
            }
        }

        //Có nhiều role nếu có shop nào là cha thì lấy thêm đơn vị có shop_type = 4

        for (RoleDTO roleDTO : roleDTOS) {
            List<ShopDTO> shopDTOS = new ArrayList<>();
            shopDTOS.addAll(roleDTO.getShops());// add shop cha trước
            for(ShopDTO shop: roleDTO.getShops()) {
                List<Shop> childrenShops = this.getChildrenShop(shop.getId());
                List<ShopDTO> childrenShopDTOS = childrenShops.stream().map(s -> modelMapper.map(s, ShopDTO.class)).collect(Collectors.toList());
                shopDTOS.addAll(childrenShopDTOS);
            }
            roleDTO.setShops(shopDTOS);
        }

        return roleDTOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Object> getRoleShop(LoginRequest loginInfo) {

        User user = repository.findByUsername(loginInfo.getUsername())
                .orElseThrow(() -> new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS));

        Shop shop = shopRepository.findById(loginInfo.getShopId())
                .orElseThrow(() -> new ValidateException(ResponseMessage.SHOP_NOT_FOUND));

        List<RoleDTO> roleDTOS = this.getAllRoles(user);
        List<Long> roleIds = roleDTOS.stream().map(RoleDTO::getId).collect(Collectors.toList());

        //step getAllRoles đã check role tồn tại
        if(!roleIds.contains(loginInfo.getRoleId()))
            throw new ValidateException(ResponseMessage.USER_ROLE_NOT_MATCH);


        // check shop
        List<Long> shopIds = new ArrayList<>();
        for(RoleDTO role:  roleDTOS) {
            if(role.getId().equals(loginInfo.getRoleId())) {
                shopIds = role.getShops().stream().map(ShopDTO::getId).collect(Collectors.toList());
                break;
            }
        }
        if(!shopIds.contains(loginInfo.getShopId()))
            throw new ValidateException(ResponseMessage.NO_PERMISSION_TYPE_2);

        Role role = roleRepository.findById(loginInfo.getRoleId()).get();

        List<FormDTO> froms = this.getForms(loginInfo.getRoleId());

        LoginResponse resData = modelMapper.map(user, LoginResponse.class);
        ShopDTO usedShop = new ShopDTO(loginInfo.getShopId(), shop.getShopName(), shop.getAddress(), shop.getMobiPhone(), shop.getEmail());
        setOnlineOrderPermission(usedShop, shop);

        resData.setUsedShop(usedShop);
        resData.setForms(froms);

        resData.setRoles(null);
        resData.setUsedRole(modelMapper.map(role, RoleDTO.class));

        Response<Object> response = new Response<>();
        response.setToken(createToken(user, role.getRoleName(), shop.getId(), role.getId()));
        response.setData(resData);
        this.loginSuccess(user.getUserAccount(), shop.getId(), user.getId());
        saveLoginLog(shop.getId(), user.getUserAccount());
        return response;
    }

    @Override
    public String reloadCaptcha(String username) {
        User user = repository.findByUsername(username).orElseThrow(() -> new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS));
        String captcha = generateCaptchaString();
        user.setCaptcha(captcha);
        repository.save(user);
        return user.getCaptcha();
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

    /*
     * Kiểm tra role và shop/shopcon có quyền dữ liệu nào ko khi đăng nhập
     */
    public Boolean checkPermissionType2(Long roleId, Shop shop) {
        List<Long> shopIds = this.getParentIds(shop);
        shopIds.add(shop.getId());
        List<Permission> permissionsType2 = permissionRepository.findPermissionType(roleId, shopIds, 2);
        if(permissionsType2.isEmpty()) return false;
        return true;
    }


    public String createToken(User user, String role, Long shopId, Long roleId) {
        return jwtTokenCreate.createToken(ClaimsTokenBuilder.build(role)
                .withUserId(user.getId()).withUserName(user.getUserAccount()).withShopId(shopId).withRoleId(roleId)
                .withPermission(getDataPermission(roleId)).get());
    }


    private List<Shop> getChildrenShop(Long parentId) {
        List<Shop> lstResults = new ArrayList<>();
        List<Shop> shops1 = shopRepository.findByParentShopIdAndShopTypeAndStatus(parentId, "4", 1);
        if (shops1.isEmpty()) return lstResults;
        lstResults.addAll(shops1);

        for (Shop shop : shops1) {
            lstResults.addAll(getChildrenShop(shop.getId()));
        }
        return lstResults;
    }

    private List<Long> getParentIds(Shop shop) {
        List<Long> lstResults = new ArrayList<>();
        if(shop == null ||  shop.getParentShopId() == null) return lstResults;
        Shop parent = shopRepository.findByIdAndStatus(shop.getParentShopId(), 1).orElse(null);
        if (parent == null) return lstResults;
        lstResults.add(parent.getId());
        lstResults.addAll(getParentIds(parent));
        return lstResults;
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

    //Shop cha cũng truy cập vào role shop con
    public List<FormDTO> getForms(Long roleId) {
        List<Permission> permissions = permissionRepository.findPermissionByRole(roleId);
        if(permissions.isEmpty()) throw new ValidateException(ResponseMessage.NO_PERMISSION_ASSIGNED);
        //Có full quyền
        List<FormDTO> formDTOS = new ArrayList<>();
        if(permissions.stream().anyMatch(p -> p.getIsFullPrivilege() != null && p.getIsFullPrivilege() == 1)) {
            List<Form> allForms = formRepository.findByStatusAndTypeNotNull(1);
            List<Control> allControls = controlRepository.findByStatusAndFormIdNotNull(1);

            List<FormDTO> allFormDTOS = allForms.stream().map(form -> modelMapper.map(form, FormDTO.class)).collect(Collectors.toList());
            List<FormDTO> allFormModules = allFormDTOS.stream().filter(form -> form.getType() == 1).collect(Collectors.toList());
            List<FormDTO> allFormComponents = allFormDTOS.stream().filter(form -> form.getType() == 2).collect(Collectors.toList());
            List<FormDTO> allFormSubComponents = allFormDTOS.stream().filter(form -> form.getType() == 3).collect(Collectors.toList());

            List<ControlDTO> allControlDTOS = allControls.stream().map(control -> modelMapper.map(control, ControlDTO.class)).collect(Collectors.toList());

            //type 1
            for(FormDTO formModule: allFormModules) {
                FormDTO formDTO = modelMapper.map(formModule, FormDTO.class);
                List<FormDTO> formComponents = allFormComponents.stream().filter(form -> form.getParentFormId().equals(formModule.getId())).collect(Collectors.toList());
                //type 2
                for(FormDTO formCom: formComponents) {
                    List<FormDTO> formSubComponents = allFormSubComponents.stream().filter(form -> form.getParentFormId().equals(formCom.getId())).collect(Collectors.toList());
                    //type =3
                    for(FormDTO formSub: formSubComponents) {
                        formSub.setControls(allControlDTOS.stream().filter(c -> c.getFormId()!=null && c.getFormId().equals(formSub.getId())).collect(Collectors.toList()));
                    }
                    formCom.setSubForms(formSubComponents);
                    formCom.setControls(allControlDTOS.stream().filter(c -> c.getFormId()!=null && c.getFormId().equals(formCom.getId())).collect(Collectors.toList()));
                }

                formDTO.setSubForms(formComponents);
                formDTO.setControls(allControlDTOS.stream().filter(c -> c.getFormId()!=null && c.getFormId().equals(formModule.getId())).collect(Collectors.toList()));
            }
            formDTOS.addAll(allFormModules);

        }else{
            /*
             * Ko full quyền FunctionAccess lưu từ cha -> con ,loại case con có mà cha ko có
             */
            Set<Long> permissionIds = permissions.stream().map(Permission::getId).collect(Collectors.toSet());
            //Các form trong functionAccess status = 1
            List<FunctionAccess> formAccess = functionAccessRepository.findForms(permissionIds);

            if(formAccess.isEmpty()) throw new ValidateException(ResponseMessage.NO_FORM_FUNCIION_ACCESSS);

            List<FunctionAccess> functionAccess = functionAccessRepository.findByPermissionIds(permissionIds, formAccess.stream().map(f ->f.getFormId()).collect(Collectors.toSet()));

            //Gộp permissions cũng 1 form
            Map<Long, List<FunctionAccess>> formMaps = functionAccess.stream().collect(Collectors.groupingBy(FunctionAccess::getFormId));

            //Tất cả các from
            List<Form> formsAllCtl = formRepository.findByIdInAndStatus(new ArrayList<>(formMaps.keySet()), 1);
            List<FormDTO> allForms = formsAllCtl.stream().map(form -> modelMapper.map(form, FormDTO.class)).collect(Collectors.toList());

            //Tất cả các control
            List<Long> controlIds = functionAccess.stream().filter(f -> f.getControlId()!=null).map(FunctionAccess::getControlId).collect(Collectors.toList());
            List<Control> controls = controlRepository.findByIdInAndStatus( controlIds, 1);
            List<ControlDTO> controlDTOs = new ArrayList<>();
            for(Control control: controls) {
                for(FunctionAccess functionAcces: functionAccess) {
                    if(control.getId().equals(functionAcces.getControlId())) {
                        ControlDTO controlDTO =  modelMapper.map(control, ControlDTO.class);
                        controlDTO.setShowStatus(functionAcces.getShowStatus());
                        controlDTOs.add(controlDTO);
                    }
                }
            }

            //Bỏ các form ko có parent & gộp form cùng cha
            List<FormDTO> formModules = allForms.stream().filter(form -> form.getType() == 1).collect(Collectors.toList());
            List<Long> parentModuleIds = formModules.stream().map(FormDTO::getId).collect(Collectors.toList());

            List<FormDTO> formComponents = allForms.stream().filter(form -> form.getType() == 2 && form.getParentFormId()!=null && parentModuleIds.contains(form.getParentFormId())).collect(Collectors.toList());
            Map<Long, List<FormDTO>> componentMaps = formComponents.stream().collect(Collectors.groupingBy(FormDTO::getParentFormId));
            List<Long> parentComponentIds = formComponents.stream().map(FormDTO::getId).collect(Collectors.toList());

            List<FormDTO> formSubComponents = allForms.stream().filter(form -> form.getType() == 3 && form.getParentFormId()!=null && parentComponentIds.contains(form.getParentFormId())).collect(Collectors.toList());
            Map<Long, List<FormDTO>> subComponentMaps = formSubComponents.stream().collect(Collectors.groupingBy(FormDTO::getParentFormId));

            //Gộp các control cùng Form
            Map<Long, List<ControlDTO>> controlMaps = controlDTOs.stream().collect(Collectors.groupingBy(ControlDTO::getFormId));


            //FormId - showStatus
            Map<Long, Integer> formsMapsAccess = new HashMap<>();
            for (FunctionAccess dto : formAccess){
                if(!formsMapsAccess.containsKey(dto.getFormId())) {
                    formsMapsAccess.put(dto.getFormId(), dto.getShowStatus());
                }
            }
            //subcomponent
            for(FormDTO module: formSubComponents) {
                module.setShowStatus(formsMapsAccess.get(module.getId()));
                module.setControls(controlMaps.get(module.getId()));
            }
            //component
            for(FormDTO module: formComponents) {
                module.setShowStatus(formsMapsAccess.get(module.getId()));
                module.setSubForms(subComponentMaps.get(module.getId()));
                module.setControls(controlMaps.get(module.getId()));
            }
            //module
            for(FormDTO module: formModules) {
                module.setShowStatus(formsMapsAccess.get(module.getId()));
                module.setSubForms(componentMaps.get(module.getId()));
                module.setControls(controlMaps.get(module.getId()));
                formDTOS.add(module);
            }

        }

        if(formDTOS.isEmpty()) throw new ValidateException(ResponseMessage.NO_PERMISSION_ASSIGNED);

        return formDTOS;
    }

    public List<DataPermissionDTO> getDataPermission(Long roleId) {
        List<DataPermissionDTO> result = new ArrayList<>();
        List<Long> permissionIds = permissionRepository.findByRoleId(roleId);
        if (permissionIds.size() == 0)
            return new ArrayList<>();
        List<Long> shopIds = orgAccessRepository.findShopIdByPermissionId(permissionIds);

        for (int i = 0; i < permissionIds.size(); i++) {
            DataPermissionDTO dataPermissionDTO = new DataPermissionDTO(permissionIds.get(i).longValue(), shopIds.get(i));
            result.add(dataPermissionDTO);
        }
        return result;
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
        UserLogOnTime log = new UserLogOnTime();
        log.setShopId(shopId);
        log.setAccount(userAccount);
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
            log.setLogCode(hostName + "_" + macAddress + "_" + time);
            log.setComputerName(hostName);
            log.setMacAddress(macAddress);
            log = userLogRepository.save(log);
            if(log.getId() != null) {
                sendSynRequest(Arrays.asList(log.getId()));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void loginSuccess(String userName, Long shopId, Long userId) {
        UserContext context = new UserContext();
        context.setUserId(userId);
        context.setUserName(userName);
        context.setShopId(shopId);
        contexHolder.setContext(context);
    }

    public String generateCaptchaString() {
        Random random = new Random();
        int length = 7 + (Math.abs(random.nextInt() % 3));

        StringBuffer captchaStringBuffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int baseCharNumber = Math.abs(random.nextInt() % 62);
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
        usedShop.setEditable(shopService.isEditableOnlineOrder(shop.getId()));
        usedShop.setManuallyCreatable(shopService.isManuallyCreatableOnlineOrder(shop.getId()));
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

	private void sendSynRequest(List<Long> lstIds) {
		try {
			if(!lstIds.isEmpty()) {
				jmsSender.sendMessage(JMSType.user_log_on_time, lstIds);
			}
		} catch (Exception ex) {
			LogFile.logToFile("vn.viettel.authorization.service.impl.UserAuthenticateServiceImpl.sendSynRequest", JMSType.user_log_on_time, LogLevel.ERROR, null, "has error when encode data " + ex.getMessage());
		}
	}
}
