package vn.viettel.common.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.common.BaseTest;
import vn.viettel.common.entities.CategoryData;
import vn.viettel.common.repository.CategoryDataRepository;
import vn.viettel.common.service.CategoryDataService;
import vn.viettel.common.service.impl.CategoryDataServiceImpl;
import vn.viettel.core.dto.common.CategoryDataDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CategoryDataControllerTest extends BaseTest {
    private final String root = "/commons/categorydata";

    @MockBean
    private CategoryDataService categoryDataService;

    @InjectMocks
    CategoryDataServiceImpl serviceImp;

    @Mock
    CategoryDataService service;

    @Mock
    CategoryDataRepository repository;

    private List<CategoryData> lstEntities;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final CategoryDataController controller = new CategoryDataController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        lstEntities = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            final CategoryData entity = new CategoryData();
            entity.setId((long) i);
            entity.setCategoryGroupCode("MASTER_SEX");
            entity.setStatus(1);
            lstEntities.add(entity);
        }
    }

    @Test
    public void getGenders() throws Exception{
        String uri = V1 + root + "/genders";

        doReturn(lstEntities).when(repository).findAll();
        List<CategoryDataDTO> dtos = serviceImp.getGenders();

        assertEquals(lstEntities.size(), dtos.size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getReasonExchange() throws Exception {
        String uri = V1 + root + "/get-reason-exchange";

        List<CategoryDataDTO> lstDto = Arrays.asList(new CategoryDataDTO());
        doReturn(lstDto).when(repository).listReasonExchangeTrans();
        List<CategoryDataDTO> dtos = serviceImp.getListReasonExchange();

        assertEquals(lstDto.size(), dtos.size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getCategoryDataById() throws Exception {
        Long id = lstEntities.get(4).getId();
        String uri = V1 + root + "/" + id;

        doReturn(Optional.of(lstEntities.get(4))).when(repository).findById(id);
        CategoryDataDTO dto = serviceImp.getCategoryDataById(id);

        assertEquals(id, dto.getId());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getByCategoryGroupCode() throws Exception {
        String uri = V1 + root + "/get-by-group-code";

        List<CategoryDataDTO> lstDto = Arrays.asList(new CategoryDataDTO());
        doReturn(lstDto).when(repository).listReasonExchangeTrans();
        List<CategoryDataDTO> dtos = serviceImp.getListReasonExchange();

        assertEquals(lstDto.size(), dtos.size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getReasonById() throws Exception {
        Long id = lstEntities.get(4).getId();
        String uri = V1 + root + "/" + id;

        doReturn(Optional.of(lstEntities.get(4))).when(repository).findById(id);
        CategoryDataDTO dto = serviceImp.getCategoryDataById(id);

        assertEquals(id, dto.getId());

        ResultActions resultActions = mockMvc.perform(get(uri,1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}