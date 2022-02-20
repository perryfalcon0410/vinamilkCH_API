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
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.InventoryImportExportFilter;
import vn.viettel.report.service.InventoryService;
import vn.viettel.report.service.dto.ImportExportInventoryDTO;
import vn.viettel.report.service.dto.ImportExportInventoryTotalDTO;
import vn.viettel.report.service.dto.PrintInventoryDTO;
import vn.viettel.report.service.feign.ShopClient;
import vn.viettel.report.service.impl.InventoryServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InventoryControllerTest extends BaseTest {
    private final String root = "/reports/inventories";

    @Spy
    @InjectMocks
    InventoryServiceImpl inventoryService;

    @Mock
    ShopClient shopClient;

    @Mock
    InventoryService service;

    private InventoryImportExportFilter filter;

    private List<ImportExportInventoryDTO> lstEntities;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        inventoryService.setModelMapper(this.modelMapper);
        final InventoryController controller = new InventoryController();
        controller.setService(service);

        this.setupAction(controller);

        lstEntities = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            ImportExportInventoryDTO data = new ImportExportInventoryDTO();
            data.setId(i);
            data.setProductCode("SP00" + i);
            data.setProductName("Sản phẩm " + i);
            data.setCatId( i);
            data.setCatName("Nghanh hàng " + i);
            data.setBeginningAmount(100000D);
            data.setBeginningQty(1000L);
            data.setEndingAmount(500000D);
            data.setEndingQty(500L);
            lstEntities.add(data);
        }

        filter = new InventoryImportExportFilter();
        filter.setShopId(1L);
    }

    @Test
    public void getReportInventoryImportExport() throws Exception{
        String uri = V1 + root + "/import-export";
        Pageable page = PageRequest.of(0, 10);

        doReturn(lstEntities).when(inventoryService).callStoreProcedure(filter);
        CoverResponse<Page<ImportExportInventoryDTO>, ImportExportInventoryTotalDTO>
            response = inventoryService.getReportInventoryImportExport(filter, page);
        Page<ImportExportInventoryDTO> datas = response.getResponse();
        assertEquals(lstEntities.size(),datas.getContent().size());

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("fromDate","2021/07/01")
                .param("toDate","2021/07/13")
                .param("productCodes","SP")
                .param("warehouseTypeId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getDataPrint() throws Exception{
        String uri = V1 + root + "/import-export/print";

        doReturn(lstEntities).when(inventoryService).callStoreProcedure(filter);
        doReturn(shop).when(shopClient).getShopByIdV1(filter.getShopId());

        PrintInventoryDTO response = inventoryService.getDataPrint(filter);
        assertEquals(lstEntities.size(),response.getCats().size());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .param("fromDate","2021/07/01")
                        .param("toDate","2021/07/13")
                        .param("productCodes","SP")
                        .param("warehouseTypeId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}