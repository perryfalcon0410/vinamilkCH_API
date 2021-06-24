package vn.viettel.promotion.controller;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.promotion.BaseTest;
import vn.viettel.promotion.service.PromotionProgramService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc(secure = false)
public class PromotionControllerTest extends BaseTest {

    private final String root = "/promotions";
    private final String uri = V1 + root;

    private final String secretKey = "Feign dMV6xG4narokclMfpuJkOCFW1XYJoCsX";
    private final String headerType = "Authorization";

    @MockBean
    private PromotionProgramService programService;

    //-------------------------------listPromotionProgramDiscountByOrderNumber-------------------------------
    @Test
    public void listPromotionProgramDiscountByOrderNumberTest() throws Exception {
        String url = uri + "/promotion-program-discount/{orderNumber}";
        String orderNumber = "A01";

        List<PromotionProgramDiscountDTO> result = new ArrayList<>();
        PromotionProgramDiscountDTO data = new PromotionProgramDiscountDTO();
        result.add(data);

        given(programService.listPromotionProgramDiscountByOrderNumber(any())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url,orderNumber)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":["));
    }

    //-------------------------------getById-------------------------------
    @Test
    public void getByIdTest() throws Exception {
        String url = uri + "/{id}";

        PromotionProgramDTO result = new PromotionProgramDTO();

        given(programService.getPromotionProgramById(anyLong())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url,1L)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    //-------------------------------getGroupCustomerMatchProgram-------------------------------
    @Test
    public void getGroupCustomerMatchProgramTest() throws Exception {
        String url = uri + "/available-promotion-cus-attr/{shopId}";

        List<PromotionCustATTRDTO> result = new ArrayList<>();
        result.add(new PromotionCustATTRDTO());

        given(programService.getGroupCustomerMatchProgram(anyLong())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url,1L)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":["));
    }

    //-------------------------------getPromotionDetailByPromotionId-------------------------------
    @Test
    public void getPromotionDetailByPromotionIdTest() throws Exception {
        String url = uri + "/get-promotion-detail/{shopId}";

        List<PromotionProgramDetailDTO> result = new ArrayList<>();
        result.add(new PromotionProgramDetailDTO());

        given(programService.getPromotionDetailByPromotionId(anyLong())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url,1L)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":["));
    }

    //-------------------------------getRejectProduct-------------------------------
    @Test
    public void getRejectProductTestMissingBody() throws Exception {
        String url = uri + "/get-rejected-products";

        List<PromotionProgramProductDTO> result = new ArrayList<>();
        result.add(new PromotionProgramProductDTO());

        given(programService.findByPromotionIds(any())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("\"statusCode\":400"));
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("ids parameter is missing"));
    }

    @Test
    public void getRejectProductTest() throws Exception {
        String url = uri + "/get-rejected-products";

        List<PromotionProgramProductDTO> result = new ArrayList<>();
        result.add(new PromotionProgramProductDTO());

        given(programService.findByPromotionIds(any())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url)
                .header(headerType, secretKey)
                .param("ids", "1")
                .param("ids", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":["));
    }

    //-------------------------------getPromotionShopMap-------------------------------
    @Test
    public void getPromotionShopMapTest() throws Exception {
        String url = uri + "/get-promotion-shop-map";

        PromotionShopMapDTO result = new PromotionShopMapDTO();

        given(programService.getPromotionShopMap(anyLong(), anyLong())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url)
                .header(headerType, secretKey)
                .param("promotionProgramId", "2")
                .param("shopId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    //-------------------------------getZmPromotion-------------------------------
    @Test
    public void getZmPromotionTest() throws Exception {
        String url = uri + "/get-zm-promotion";

        List<PromotionSaleProductDTO> result = new ArrayList<>();
        result.add(new PromotionSaleProductDTO());

        given(programService.getZmPromotionByProductId(anyLong())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url)
                .header(headerType, secretKey)
                .param("productId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":["));
    }

    //-------------------------------getFreeItem-------------------------------
    @Test
    public void getFreeItemTest() throws Exception {
        String url = uri + "/get-free-items/{programId}";

        List<PromotionProductOpenDTO> result = new ArrayList<>();
        result.add(new PromotionProductOpenDTO());

        given(programService.getFreeItems(anyLong())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url, 1)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":["));
    }

    //-------------------------------getPromotionDiscounts-------------------------------
    @Test
    public void getPromotionDiscountsTest() throws Exception {
        String url = uri + "/get-promotion-discount";

        List<PromotionProgramDiscountDTO> result = new ArrayList<>();
        result.add(new PromotionProgramDiscountDTO());

        given(programService.getPromotionDiscounts(any(), any())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url)
                .header(headerType, secretKey)
                .param("ids", "1")
                .param("ids", "2")
                .param("cusCode", "CODE_123")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":["));
    }

    //-------------------------------getPromotionDiscountByDiscountCode-------------------------------
//    @Test
//    public void getPromotionDiscountByDiscountCodeTest() throws Exception {
//        String url = uri + "/promotion-program-discount/discount-code/{code}";
//
//        PromotionProgramDiscountDTO result = new PromotionProgramDiscountDTO();
//
//        given(programService.getPromotionDiscount(any(), any())).willReturn(result);
//
//        ResultActions resultActions = mockMvc.perform(get(url, "ABCD")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print());
//
//        MvcResult mvcResult = resultActions.andReturn();
//        assertEquals(200, mvcResult.getResponse().getStatus());
//       assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
//    }
}
