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
import vn.viettel.common.entities.Area;
import vn.viettel.common.messaging.AreaSearch;
import vn.viettel.common.repository.AreaRepository;
import vn.viettel.common.service.AreaService;
import vn.viettel.common.service.dto.AreaDefaultDTO;
import vn.viettel.common.service.feign.ShopClient;
import vn.viettel.common.service.impl.AreaServiceImpl;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.messaging.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AreaControllerTest extends BaseTest {
    private final String root = "/commons/areas";

    @InjectMocks
    AreaServiceImpl serviceImp;

    @Mock
    AreaService service;

    @Mock
    AreaRepository repository;

    @Mock
    ShopClient shopClient;

    private List<Area> lstEntities;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final AreaController controller = new AreaController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        lstEntities = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            final Area entity = new Area();
            entity.setId((long) i);
            lstEntities.add(entity);
        }
    }

    @Test
    public void getProvinces() throws Exception{
        String uri = V1 + root + "/provinces";

        doReturn(lstEntities).when(repository).getArea();
        List<AreaDTO> dtos = serviceImp.getProvinces();

        assertEquals(lstEntities.size(), dtos.size());
        verify(repository).getArea();

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getAreaDefault() throws Exception{
        Long id = lstEntities.get(4).getId();
        String uri = V1 + root + "/default";
        ShopDTO shopDTO = new ShopDTO();
        shopDTO.setId(1L);
        shopDTO.setAreaId(id);

        when(shopClient.getShopByIdV1(id)).thenReturn(new Response<ShopDTO>().withData(shopDTO));
        when(repository.getById(id)).thenReturn(lstEntities.get(4));
        AreaDefaultDTO dto = serviceImp.getAreaDefault(id);

        verify(repository).getById(id);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getDistrictsToSearchCustomer() throws Exception{
        String uri = V1 + root + "/districts/index-customers";

        when(shopClient.getShopByIdV1(any())).thenReturn(new Response<ShopDTO>().withData(new ShopDTO()));
        List<AreaSearch> areaSearches = Arrays.asList(new AreaSearch() , new AreaSearch());

        when(repository.getAllDistrict(any())).thenReturn(areaSearches);
        List<AreaSearch> dto = serviceImp.getDistrictsToSearchCustomer(any());

        verify(repository).getAllDistrict(any());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getDistrictsByProvinceId() throws Exception{
        String uri = V1 + root + "/districts";
        List<AreaDTO> areaSearches = Arrays.asList(new AreaDTO());

        when(repository.getAreaByDistrictId(1L)).thenReturn(areaSearches);
        List<AreaDTO> dtos = serviceImp.getDistrictsByProvinceId(1L);

        assertEquals(areaSearches.size(), dtos.size());
        verify(repository).getAreaByDistrictId(1L);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("provinceId" , "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getPrecinctsByDistrictId() throws Exception{
        String uri = V1 + root + "/precincts";
        List<AreaDTO> areaSearches = Arrays.asList(new AreaDTO());
        when(repository.getPrecinctsByDistrictId(3L)).thenReturn(areaSearches);
        List<AreaDTO> dtos = serviceImp.getPrecinctsByDistrictId(3L);

        assertEquals(areaSearches.size(), dtos.size());
        verify(repository).getPrecinctsByDistrictId(3L);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("districtId" , "3" )
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getById() throws Exception{
        Long id = lstEntities.get(4).getId();
        String uri = V1 + root + "/" + id.toString();

        doReturn(Optional.of(lstEntities.get(4))).when(repository).findById(id);
        AreaDTO dto = serviceImp.getAreaById(id);

        assertEquals(lstEntities.get(4).getId(), dto.getId());
        verify(repository).findById(id);

        ResultActions resultActions = mockMvc.perform(get(uri,1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getArea() throws Exception{
        String uri = V1 + root + "/find";

        when(repository.getArea("any()","any()","any()")).thenReturn(Optional.of(new Area()));
        AreaDTO dto = serviceImp.getArea("any()","any()","any()");

        verify(repository).getArea(any(),any(),any());

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("provinceName" , "test" )
                .param("districtName" , "test" )
                .param("precinctName" , "test" )
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}