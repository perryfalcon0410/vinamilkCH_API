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
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ShopImportFilter;
import vn.viettel.report.service.ShopImportReportService;
import vn.viettel.report.service.dto.PrintShopImportDTO;
import vn.viettel.report.service.dto.ShopImportDTO;
import vn.viettel.report.service.dto.ShopImportTotalDTO;
import vn.viettel.report.service.feign.ShopClient;
import vn.viettel.report.service.impl.ShopImportReportServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ShopImportReportControllerTest extends BaseTest {
    private final String root = "/reports/shop-import";
    @Spy
    @InjectMocks
    ShopImportReportServiceImpl shopImportReportService;

    @Mock
    ShopClient shopClient;

    @Mock
    ShopImportReportService service;

    private List<ShopImportDTO> lstEntities;

    private ShopImportFilter filter;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final ShopImportReportController controller = new ShopImportReportController();
        controller.setService(service);
        this.setupAction(controller);
        lstEntities = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            ShopImportDTO shopDTO = new ShopImportDTO();
            shopDTO.setId(i);
            shopDTO.setInternalNumber("IN00" + i);
            shopDTO.setPoNumber("PO00" + i);
            shopDTO.setCatId(i);
            shopDTO.setAmount(100000F);
            shopDTO.setQuantity(1000);
            shopDTO.setProductCode("SP00" + i);
            shopDTO.setProductName("Tên sản phẩm " + i);
            shopDTO.setTypess(0);
            shopDTO.setType(1);
            shopDTO.setOrderId(1L);
            shopDTO.setProductInfoName("NGHANH HANG");
            lstEntities.add(shopDTO);
        }
        filter = new ShopImportFilter();
        filter.setShopId(1L);
    }

    @Test
    public void index() throws Exception{
        String uri = V1 + root;

        Pageable pageable = PageRequest.of(0, 10);
        Response<List<ShopImportDTO>> list = new Response<>();
        list.setData(lstEntities);
        doReturn(list).when(shopImportReportService).callProcedure(filter);

        CoverResponse<Page<ShopImportDTO>, ShopImportTotalDTO> response = shopImportReportService.find(filter, pageable);
        Page<ShopImportDTO> datas = response.getResponse();
        assertEquals(lstEntities.size(),datas.getContent().size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON)
            .param("fromDate", "01/04/2021")
            .param("toDate", "01/05/2021"))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());

    }


    @Test
    public void print() throws Exception{
        String uri = V1 + root + "/print";

        Response<List<ShopImportDTO>> list = new Response<>();
        list.setData(lstEntities);
        doReturn(list).when(shopImportReportService).callProcedure(filter);
        doReturn(shop).when(shopClient).getShopByIdV1(filter.getShopId());
        PrintShopImportDTO response = shopImportReportService.print(filter, 1L);
      assertEquals(1,response.getImpPO().getOrderImports().size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON)
                        .param("fromDate", "01/04/2021")
                        .param("toDate", "01/05/2021"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());

    }

}