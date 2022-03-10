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
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.QuantitySalesReceiptFilter;
import vn.viettel.report.service.QuantitySalesReceiptService;
import vn.viettel.report.service.dto.TableDynamicDTO;
import vn.viettel.report.service.impl.QuantitySalesReceiptServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuantitySalesReceiptControllerTest extends BaseTest {
    private final String root = "/reports/customers/quantity";

    @Spy
    @InjectMocks
    QuantitySalesReceiptServiceImpl quantitySalesReceiptService;

    @Mock
    QuantitySalesReceiptService service;

    private QuantitySalesReceiptFilter filter;

    private TableDynamicDTO tableDynamicDTO;

    private List<Object[]> rowData;

    private List<CategoryDataDTO> reasonsDT;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final QuantitySalesReceiptController controller = new QuantitySalesReceiptController();
        controller.setService(service);
        this.setupAction(controller);
        tableDynamicDTO = new TableDynamicDTO();
        reasonsDT = new ArrayList<>();
        rowData = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            Object[] rowDatas = new Object[5];
            rowDatas[0] = "2021-05-01 22:20:20";
            rowDatas[1] = "PO00" + i;
            rowDatas[2] = "RED00"  + i;
            rowDatas[3] = LocalDateTime.now();
            rowDatas[4] = LocalDateTime.now();
            rowData.add(rowDatas);

            CategoryDataDTO dto  = new  CategoryDataDTO();
            dto.setCategoryCode("CODE00" + i);
            dto.setCategoryName("Tên là " + i);
            dto.setCategoryGroupCode("G_CODE00" + i);
            dto.setStatus(1);
            reasonsDT.add(dto);
        }

        tableDynamicDTO.setTotals(rowData.get(rowData.size() - 1));
        tableDynamicDTO.setResponse(rowData);

        filter = new QuantitySalesReceiptFilter();
        filter.setShopId(1L);
        filter.setFromDate(LocalDateTime.now());
        filter.setToDate(LocalDateTime.now());
    }

    @Test
    public void findQuantity() throws Exception{
        String uri = V1 + root;
        Pageable page = PageRequest.of(0, 10);
        doReturn(tableDynamicDTO).when(quantitySalesReceiptService).callProcedure(filter);
        TableDynamicDTO response =  quantitySalesReceiptService.findQuantity(filter, page);

        Page<Object[]> pageRes = (Page<Object[]>) response.getResponse();

        assertEquals(rowData.size(),pageRes.getContent().size());

        ResultActions resultActions = mockMvc.perform(get(uri)
            .param("fromDate", "01/04/2021")
            .param("toDate", "01/05/2021")
            .param("customerTypeId", "100")
            .param("keySearch", "Test")
            .param("phoneNumber", "09870")
            .param("fromQuantity", "1000")
            .param("toQuantity", "100")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

}
