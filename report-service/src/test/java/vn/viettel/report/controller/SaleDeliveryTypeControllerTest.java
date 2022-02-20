package vn.viettel.report.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.SaleDeliveryTypeFilter;
import vn.viettel.report.service.SaleDeliveryTypeService;
import vn.viettel.report.service.dto.SaleByDeliveryTypeDTO;
import vn.viettel.report.service.dto.SaleDeliTypeTotalDTO;
import vn.viettel.report.service.feign.CommonClient;
import vn.viettel.report.service.feign.ShopClient;
import vn.viettel.report.service.impl.SaleByDeliveryImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SaleDeliveryTypeControllerTest extends BaseTest {
    private final String root = "/reports/delivery-type";

    @Spy
    @InjectMocks
    SaleByDeliveryImpl saleDeliveryTypeService;

    @Mock
    ShopClient shopClient;

    @Mock
    CommonClient commonClient;

    @Mock
    SaleDeliveryTypeService service;

    private List<SaleByDeliveryTypeDTO> lstEntities;

    private List<ApParamDTO> lstParams;

    private SaleDeliveryTypeFilter filter;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final SaleDeliveryTypeController controller = new SaleDeliveryTypeController();
        controller.setService(service);
        this.setupAction(controller);
        lstEntities = new ArrayList<>();
        lstParams = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            SaleByDeliveryTypeDTO sale = new SaleByDeliveryTypeDTO();
            sale.setId(i);
            sale.setCustomerCode("CUS00" + i);
            sale.setCustomerName("TÊN KH " + i);
            sale.setCustomerAddress("Đia chỉ" + i);
            sale.setOrderNumber("ONL00" + i);
            sale.setOrderNumber("OD00" + i);
            lstEntities.add(sale);

            ApParamDTO apParamDTO = new ApParamDTO();
            apParamDTO.setApParamCode("AP00" + i);
            apParamDTO.setApParamName("Cấu hình: " + i);
            apParamDTO.setStatus(1);
            lstParams.add(apParamDTO);
        }
        filter = new SaleDeliveryTypeFilter();
        filter.setShopId(1L);
    }


    @Test
    public void deliveryType() throws Exception{
        String uri = V1 + root;
        Response<List<ApParamDTO>> ap = new Response<>();
        ap.setData(lstParams);
        doReturn(ap).when(commonClient).getApParamByTypeV1("SALEMT_DELIVERY_TYPE");

        List<ApParamDTO> response = saleDeliveryTypeService.deliveryType();
        assertEquals(lstParams.size(),response.size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());

    }

    @Test
    public void listSaleDeliType() throws Exception{
        String uri = V1 + root + "/type";

        Pageable pageable = PageRequest.of(0, 10);
        doReturn(lstEntities).when(saleDeliveryTypeService).callStoreProcedure(filter);

        CoverResponse<Page<SaleByDeliveryTypeDTO>, SaleDeliTypeTotalDTO>
            response =  saleDeliveryTypeService.getSaleDeliType(filter, pageable);
        Page<SaleByDeliveryTypeDTO> datas = response.getResponse();
        assertEquals(lstEntities.size(), datas.getContent().size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}