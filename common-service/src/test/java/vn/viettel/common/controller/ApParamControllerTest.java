package vn.viettel.common.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.common.BaseTest;
import vn.viettel.common.entities.ApParam;
import vn.viettel.common.repository.ApParamRepository;
import vn.viettel.common.service.ApParamService;
import vn.viettel.common.service.impl.ApParamServiceImpl;
import vn.viettel.core.dto.common.ApParamDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ApParamControllerTest extends BaseTest {
    private final String root = "/commons/apparams";

    @InjectMocks
    ApParamServiceImpl apParamService;

    @Mock
    ApParamService service;

    @Mock
    ApParamRepository repository;

    private List<ApParam> lstEntities;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        apParamService.setModelMapper(this.modelMapper);
        final ApParamController controller = new ApParamController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        lstEntities = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            final ApParam entity = new ApParam();
            entity.setId((long) i);
            entity.setApParamCode("" + i);
            lstEntities.add(entity);
        }
    }

    @Test
    public void getApParamById() throws Exception{
        Long id = lstEntities.get(4).getId();
        String uri = V1 + root + "/" + id.toString();

        doReturn(Optional.of(lstEntities.get(4))).when(repository).findById(id);
        ApParamDTO dto = apParamService.getApParamById(id);

        assertEquals(lstEntities.get(4).getId(), dto.getId());
        verify(repository).findById(id);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getCardTypes() throws Exception{
        String uri = V1 + root + "/cardtypes";

//        doReturn(lstEntities).when(repository).getApParamByType("SALEMT_CUSTOMER_CARD");
        given(repository.getApParamByType("SALEMT_CUSTOMER_CARD")).willReturn(lstEntities);
        List<ApParamDTO> dtos = apParamService.getCardTypes();

        assertEquals(lstEntities.size(), dtos.size());
        verify(repository).getApParamByType("SALEMT_CUSTOMER_CARD");

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getCloselytypes() throws Exception{
        String uri = V1 + root + "/closelytypes";

        doReturn(lstEntities).when(repository).getApParamByType("SALEMT_CLOSELY_CUSTOMER");
        List<ApParamDTO> dtos = apParamService.getCloselytypes();

        assertEquals(lstEntities.size(), dtos.size());
        verify(repository).getApParamByType("SALEMT_CLOSELY_CUSTOMER");

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

   @Test
    public void getReasonAdjust() throws Exception{
        Long id = lstEntities.get(4).getId();
        String uri = V1 + root + "/reason-adjust/" + id.toString();

        doReturn(lstEntities.get(4)).when(repository).getApParamByIdAndType(id,"SALEMT_LY_DO_DC_KHO");
        ApParamDTO dto = apParamService.getReason(id);

        assertEquals(lstEntities.get(4).getId(), dto.getId());
        verify(repository).getApParamByIdAndType(id,"SALEMT_LY_DO_DC_KHO");

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

       assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

     @Test
    public void getByType() throws Exception{
        String type = "SALEMT_CLOSELY_CUSTOMER";
        String uri = V1 + root + "/type/" + type;

        doReturn(lstEntities).when(repository).findByTypeAndStatus(type, 1);
        List<ApParamDTO> dtos = apParamService.getByType(type);

        assertEquals(lstEntities.size(), dtos.size());
        verify(repository).findByTypeAndStatus(type, 1);

        ResultActions resultActions = mockMvc.perform(get(uri,type).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getReasonNotImport() throws Exception{
        String type = "SALEMT_PO_DENY";
        String uri = V1 + root + "/sale-mt-deny";

        doReturn(lstEntities).when(repository).getApParamByTypeAndStatus(type, 1);
        List<ApParamDTO> dtos = apParamService.getReasonNotImport();

        assertEquals(lstEntities.size(), dtos.size());
        verify(repository).getApParamByTypeAndStatus(type, 1);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

   @Test
    public void getApParams() throws Exception{
        String uri = V1 + root;

        doReturn(lstEntities).when(repository).findAll();
        List<ApParamDTO> dtos = apParamService.findAll();

        assertEquals(lstEntities.size(), dtos.size());
        verify(repository).findAll();

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getApParamByCode() throws Exception{
        String code = "1";
        String uri = V1 + root + "/getByCode/" + code;

        doReturn(lstEntities).when(repository).findByCode(code);
        ApParamDTO dto = apParamService.getByCode(code);

        assertEquals(lstEntities.get(0).getApParamCode(), dto.getApParamCode());
        verify(repository).findByCode(code);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void testGetApParams() throws Exception {
        String type = "1";
        String uri = V1 + root + "/get-by-value/" + type;
        List<String> values = Arrays.asList("value1");

        when(repository.getApParams(values, type)).thenReturn(lstEntities);
        apParamService.getApParams(values, type);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getSalesChannel() throws Exception {
        String uri = V1 + root + "/get_sales_channel";

        when(repository.getSalesChannel()).thenReturn(lstEntities);
        apParamService.getSalesChannel();

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getApParamByTypeAndvalue() throws Exception {
        String uri = V1 + root + "/type-value";
        String type = "1";
        String value = "1";

        when(repository.findByTypeAndValueAndStatus(type, value, 1)).thenReturn(Optional.ofNullable(lstEntities.get(0)));
        apParamService.getApParamByTypeAndvalue(type, value);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("type", type)
                .param("value", value)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getApParamOnlineOrder() throws Exception {
        String uri = V1 + root + "/online-order/type";
        String discription = "1";

        when(repository.getOnlineOrderType()).thenReturn(lstEntities);
        apParamService.getApParamOnlineOrder(discription);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("discription", discription)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getApParamByCodeType() throws Exception {
        String code = "1";
        String uri = V1 + root + "/code-type/" + code;
        String type = "1";

        when(repository.findByCode(code, type)).thenReturn(lstEntities);
        apParamService.getByCode(code, type);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("code", code)
                .param("type", type)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}
