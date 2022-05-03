package vn.viettel.sale.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.OnlineOrder;
import vn.viettel.sale.entities.WareHouseType;
import vn.viettel.sale.repository.PoConfirmRepository;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.repository.WareHouseTypeRepository;
import vn.viettel.sale.service.PoConfirmService;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.feign.ApparamClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.impl.PoConfirmServiceImpl;
import vn.viettel.sale.service.impl.SaleOrderServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PoConfirmControllerTest extends BaseTest {

    private final String root = "/sales/po-confirm";

    @InjectMocks
    PoConfirmServiceImpl serviceImpl;

    @Mock
    PoConfirmService service;

    @Mock
    PoConfirmRepository repository;
    @Mock
    ApparamClient apparamClient;
    @Mock
    ShopClient shopClient;
    @Mock
    WareHouseTypeRepository wareHouseTypeRepo;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImpl.setModelMapper(this.modelMapper);
        final PoConfirmController controller = new PoConfirmController();
//        controller.setService(service);
        this.setupAction(controller);
    }

    @Test
    public void updatePoCofirm() throws Exception {
        String uri = V1 + root + "/xml";
        ShopDTO shopDTO = new ShopDTO();
        Response response1 = new Response();
        response1.setData(shopDTO);
        BDDMockito.given(shopClient.getByIdV1(1L)).willReturn(response1);

        List<ApParamDTO> apParamDTOList = new ArrayList<>();
        ApParamDTO apParamDTO = new ApParamDTO();
        apParamDTO.setValue("11");
        apParamDTOList.add(apParamDTO);
        Response response = new Response();
        response.setData(apParamDTOList);
        BDDMockito.given(apparamClient.getApParamByTypeV1("FTP")).willReturn(response);

        List<WareHouseType> onlineOrders = new ArrayList<>();
        WareHouseType onlineOrder = new WareHouseType();
        onlineOrders.add(onlineOrder);
        BDDMockito.given(wareHouseTypeRepo.findDefault()).willReturn(onlineOrders);

        serviceImpl.updatePoCofirm(1L);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}