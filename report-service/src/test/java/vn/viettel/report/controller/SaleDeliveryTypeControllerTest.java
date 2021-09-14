package vn.viettel.report.controller;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.BaseTest;
import vn.viettel.report.service.SaleDeliveryTypeService;
import vn.viettel.report.service.dto.SaleByDeliveryTypeDTO;
import vn.viettel.report.service.dto.SaleDeliTypeTotalDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SaleDeliveryTypeControllerTest extends BaseTest {
    private final String root = "/reports/delivery-type";

    @MockBean
    SaleDeliveryTypeService saleDeliveryTypeService;

    @Test
    public void exportToExcel() {
    }

    @Test
    public void deliveryType() throws Exception{
        String uri = V1 + root;
        int size = 2;
        int page = 5;
        PageRequest pageReq = PageRequest.of(page, size);
        List<SaleByDeliveryTypeDTO> lstDto = Arrays.asList(new SaleByDeliveryTypeDTO(), new SaleByDeliveryTypeDTO());
        Page<SaleByDeliveryTypeDTO> pageDto = new PageImpl<>(lstDto, pageReq, lstDto.size());
        CoverResponse<Page<SaleByDeliveryTypeDTO>, SaleDeliTypeTotalDTO> response = new CoverResponse<>(pageDto, new SaleDeliTypeTotalDTO());

        given(saleDeliveryTypeService.getSaleDeliType(any(), Mockito.any(PageRequest.class)))
                .willReturn(response);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"pageNumber\":" + page));
        assertThat(responseData, containsString("\"pageSize\":" + size));
    }

    @Test
    public void listSaleDeliType() throws Exception{
        String uri = V1 + root + "/type";
        List<ApParamDTO> response = new ArrayList<>();
        given(saleDeliveryTypeService.deliveryType())
                .willReturn(response);
        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":["));
    }
}