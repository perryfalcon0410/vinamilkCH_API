package vn.viettel.authorization.controller;

import io.jsonwebtoken.Claims;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.authorization.BaseTest;
import vn.viettel.authorization.entities.*;
import vn.viettel.authorization.repository.*;
import vn.viettel.authorization.security.ClaimsTokenBuilder;
import vn.viettel.authorization.security.JwtTokenCreate;
import vn.viettel.authorization.service.ShopService;
import vn.viettel.authorization.service.UserAuthenticateService;
import vn.viettel.authorization.service.dto.ChangePasswordRequest;
import vn.viettel.authorization.service.dto.LoginRequest;
import vn.viettel.authorization.service.dto.LoginResponse;
import vn.viettel.authorization.service.dto.ShopDTO;
import vn.viettel.authorization.service.impl.UserAuthenticateServiceImpl;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.context.SecurityContexHolder;
import vn.viettel.core.service.dto.DataPermissionDTO;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserAuthenticateControllerTest extends BaseTest {
    private final String root = "/users";

    private final String secretKey = "Feign dMV6xG4narokclMfpuJkOCFW1XYJoCsX";
    private final String headerType = "Authorization";

    @InjectMocks
    UserAuthenticateServiceImpl userAuthenticateService;

    @Mock
    UserRepository repository;

    @Mock
    UserAuthenticateService service;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PermissionRepository permissionRepository;

    @Mock
    FormRepository formRepository;

    @Mock
    ControlRepository controlRepository;

    @Mock
    ShopService shopService;

    @Mock
    SecurityContexHolder contexHolder;

    @Mock
    JwtTokenCreate jwtTokenCreate;

    @Mock
    UserLogRepository userLogRepository;

    @Mock
    OrgAccessRepository orgAccessRepository;

    @Mock
    ShopRepository shopRepository;

    private List<User> lstEntities;

    private List<Role> lstRoleEntities;

    private List<Shop> lstShopEntities;

    private List<Permission> lstPermissionEntities;

    private List<Form> lstFormEntities;

    private List<Control> lstControlEntities;

    private List<DataPermissionDTO> dataPermissionDTOS;

    private List<UserLogOnTime> userLogOnTimes;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        userAuthenticateService.setModelMapper(this.modelMapper);
        final UserAuthenticateController controller = new UserAuthenticateController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        lstEntities = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            final User entity = new User();
            entity.setId((long) i);
            entity.setUserAccount("admin" + i);
            entity.setCaptcha(null);
            entity.setMaxWrongTime(5);
            entity.setWrongTime(0);
            entity.setPassword("$2a$04$kaZSFDxgokCLbh9/kiGQXeyvbgaQ03KrEA9QlzTRK.dXhEQei04/K");
            entity.setStatus(1);
            lstEntities.add(entity);
        }

        lstRoleEntities = new ArrayList<>();
        final Role role = new Role();
        role.setId(1L);
        role.setRoleName("ABC");
        role.setRoleCode("ABC");
        role.setDescription("ABC");
        role.setStatus(1);
        lstRoleEntities.add(role);

        lstShopEntities = new ArrayList<>();
        final Shop shop = new Shop();
        shop.setId(1L);
        shop.setShopName("Shop 1");
        shop.setStatus(1);
        lstShopEntities.add(shop);

        lstPermissionEntities = new ArrayList<>();
        final Permission permission = new Permission();
        permission.setId(1L);
        permission.setPermissionCode("ABC");
        permission.setStatus(1);
        permission.setDescription("ABC");
        permission.setIsFullPrivilege(1);
        lstPermissionEntities.add(permission);

        lstFormEntities = new ArrayList<>();
        final Form form = new Form();
        form.setId(1L);
        form.setFormCode("A");
        form.setStatus(1);
        form.setType(1);
        lstFormEntities.add(form);

        lstControlEntities = new ArrayList<>();
        final Control control = new Control();
        control.setId(1L);
        control.setStatus(1);
        control.setControlCode("A");
        control.setFormId(1L);
        lstControlEntities.add(control);

        dataPermissionDTOS = new ArrayList<>();

        userLogOnTimes = new ArrayList<>();
        final UserLogOnTime userLogOnTime = new UserLogOnTime();
        userLogOnTime.setId(1L);
        userLogOnTimes.add(userLogOnTime);
    }

    @Test
    public void preLogin() throws Exception{
        String uri = V1 + root + "/preLogin";

        LoginRequest requestObj = new LoginRequest();
        requestObj.setUsername("admin1");
        requestObj.setPassword("1234");
        requestObj.setRoleId(1L);
        requestObj.setShopId(1L);

        Mockito.when(repository.findByUsername("admin1")).thenReturn(Optional.ofNullable(lstEntities.get(0)));

        Mockito.when(passwordEncoder.matches(
                "1234",
                "$2a$04$kaZSFDxgokCLbh9/kiGQXeyvbgaQ03KrEA9QlzTRK.dXhEQei04/K")
        ).thenReturn(true);

        Mockito.when(roleRepository.findRoles(lstEntities.get(0).getId())).thenReturn(lstRoleEntities);
        Mockito.when(permissionRepository
                .findRoles(lstRoleEntities.stream().map(Role::getId).collect(Collectors.toList())))
                .thenReturn(lstRoleEntities);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(1L);
        loginResponse.setUserAccount("admin1");

        LoginResponse loginResponse1 = (LoginResponse) userAuthenticateService.preLogin(requestObj).getData();

        assertEquals(loginResponse.getUserAccount(), loginResponse1.getUserAccount());

        ResultActions resultActions = mockMvc.perform(post(uri)
                        .header(headerType, secretKey)
                        .content(super.mapToJson(requestObj))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void userLogin() throws Exception{
        String uri = V1 + root + "/login";
        LoginRequest requestObj = new LoginRequest();
        requestObj.setUsername("admin1");
        requestObj.setPassword("1234");
        requestObj.setShopId(1L);
        requestObj.setRoleId(1L);

        Mockito.when(repository.findByUsername("admin1")).thenReturn(Optional.ofNullable(lstEntities.get(0)));

        Mockito.when(shopRepository.findById(1L))
                .thenReturn(Optional.ofNullable(lstShopEntities.get(0)));

        Mockito.when(roleRepository.findRoles(requestObj.getRoleId())).thenReturn(lstRoleEntities);
        Mockito.when(permissionRepository
                        .findRoles(lstRoleEntities.stream().map(Role::getId).collect(Collectors.toList())))
                .thenReturn(lstRoleEntities);

        Mockito.when(permissionRepository.findShops(requestObj.getRoleId())).thenReturn(lstShopEntities);

        Mockito.when(roleRepository.findById(requestObj.getRoleId())).thenReturn(Optional.ofNullable(lstRoleEntities.get(0)));

        Mockito.when(permissionRepository.findPermissionByRole(requestObj.getRoleId())).thenReturn(lstPermissionEntities);

        Mockito.when(formRepository.findByStatusAndTypeNotNull(1)).thenReturn(lstFormEntities);

        Mockito.when(controlRepository.findByStatusAndFormIdNotNull(1)).thenReturn(lstControlEntities);

        Mockito.when(shopService.isEditableOnlineOrder(requestObj.getShopId())).thenReturn(true);

        Mockito.when(jwtTokenCreate.createToken(ClaimsTokenBuilder.build(lstRoleEntities.get(0).getRoleName())
                .withUserId(1)
                .withUserName(requestObj.getUsername())
                .withShopId(1L)
                .withRoleId(1L)
                .withPermission(dataPermissionDTOS).get()
        )).thenReturn("$2a$04$kaZSFDxgokCLbh9/kiGQXeyvbgaQ03KrEA9QlzTRK.dXhEQei04/K");

        userAuthenticateService.saveLoginLog(requestObj.getShopId(), requestObj.getUsername(), requestObj);


        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(1L);
        loginResponse.setUserAccount("admin1");

        LoginResponse loginResponse1 = (LoginResponse) userAuthenticateService.getRoleShop(requestObj).getData();


        assertEquals( loginResponse.getId(), loginResponse1.getId());

        ResultActions resultActions = mockMvc.perform(post(uri)
                        .header(headerType, secretKey)
                        .content(super.mapToJson(requestObj))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void changePassword() throws Exception{
        String uri = V1 + root + "/change-password";

        ChangePasswordRequest requestObj = new ChangePasswordRequest();
        requestObj.setUsername("admin1");
        requestObj.setOldPassword("1234");
        requestObj.setNewPassword("123456@Xa");
        requestObj.setConfirmPassword("123456@Xa");

        Mockito.when(repository.findByUsername("admin1")).thenReturn(Optional.ofNullable(lstEntities.get(0)));

        Mockito.when(passwordEncoder.matches(
                "1234",
                "$2a$04$kaZSFDxgokCLbh9/kiGQXeyvbgaQ03KrEA9QlzTRK.dXhEQei04/K")
        ).thenReturn(true);

        Response response = userAuthenticateService.changePassword(requestObj);

        assertEquals(true, response.getSuccess());

        ResultActions resultActions = mockMvc.perform(put(uri)
                        .header(headerType, secretKey)
                        .content(super.mapToJson(requestObj))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getUserById() throws Exception{
        Long id = lstEntities.get(0).getId();
        String uri = V1 + root + "/findById/" + id.toString();

        Mockito.when(repository.findById(id)).thenReturn(Optional.ofNullable(lstEntities.get(0)));

        UserDTO userDTO = userAuthenticateService.getUserById(id);

        assertEquals(lstEntities.get(0).getId(), userDTO.getId());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .header(headerType, secretKey)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());

    }

    @Test
    public void getShopByRole() throws Exception{
        Long roleId = lstRoleEntities.get(0).getId();
        String uri = V1 + root + "/get-shop-by-role/" + roleId;

        Mockito.when(orgAccessRepository.finShopIdByRoleId(roleId))
                .thenReturn(lstShopEntities.stream().map(Shop::getId).collect(Collectors.toList()));

        Mockito.when(shopRepository.findById(1L)).thenReturn(Optional.ofNullable(lstShopEntities.get(0)));
        Mockito.when(shopRepository.findByIdAndStatus(1L,1)).thenReturn(Optional.ofNullable(lstShopEntities.get(0)));

        List<ShopDTO> shopDTOS = userAuthenticateService.getShopByRole(roleId);


        assertEquals(lstShopEntities.size(), shopDTOS.size());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .header(headerType, secretKey)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}