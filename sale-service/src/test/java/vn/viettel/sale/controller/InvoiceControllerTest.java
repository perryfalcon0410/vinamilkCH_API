package vn.viettel.sale.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ReportProductTransService;
import vn.viettel.sale.service.dto.ReportProductTransDTO;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.impl.ReportProductTransServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InvoiceControllerTest extends BaseTest {
    private final String root = "/sales/invoices";

    @InjectMocks
    ReportProductTransServiceImpl serviceImp;

    @Mock
    ReportProductTransService service;

    @Mock
    PoTransRepository repository;

    @Mock
    ShopClient shopClient;

    @Mock
    PoTransRepository poTransRepo;

    @Mock
    PoTransDetailRepository poTransDetailRepo;

    @Mock
    ProductRepository productRepo;

    @Mock
    ProductInfoRepository productInfoRepo;

    @Mock
    StockAdjustmentTransRepository stockAdjustmentTransRepo;

    @Mock
    StockAdjustmentTransDetailRepository stockAdjustmentTransDetailRepo;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final InvoiceController controller = new InvoiceController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void findComboProductsReceiptType0Type3Test() throws Exception {
        String uri = V1 + root + "/product-trans/1";
        Integer receiptType = 0;
        Long shopId = 1L;
        Long id = 1L;
        ShopDTO shopDTO = new ShopDTO();
        PoTrans poTran = new PoTrans();
        poTran.setType(3);
        when(shopClient.getByIdV1(shopId)).thenReturn(new Response<ShopDTO>().withData(shopDTO));
        when(poTransRepo.findById(id)).thenReturn(java.util.Optional.of(poTran));
        ReportProductTransDTO data = new ReportProductTransDTO();
        data.setId(1L);
        data.setShop(shopDTO);

        Response<ReportProductTransDTO> response = serviceImp.getInvoice(shopId, id, receiptType);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("receiptType", "" + receiptType)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findComboProductsReceiptType0Type2Test() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/product-trans/" + id;
        Long shopId = 1L;
        Integer receiptType = 0;
        ShopDTO shopDTO = new ShopDTO();
        PoTrans poTran = new PoTrans();
        poTran.setId(1L);
        poTran.setType(2);
        when(shopClient.getByIdV1(shopId)).thenReturn(new Response<ShopDTO>().withData(shopDTO));
        when(poTransRepo.findById(id)).thenReturn(java.util.Optional.of(poTran));
        List<PoTransDetail> poTransDetails = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<Long> productIds = new ArrayList<>();
        List<ProductInfo> productInfos = new ArrayList<>();
        List<Long> productInfoIds = new ArrayList<>();
        for(int i = 1; i < 5; i++){
            PoTransDetail poDtl = new PoTransDetail();
            poDtl.setId((long)i);
            poDtl.setProductId((long)i);
            poTransDetails.add(poDtl);

            Product p = new Product();
            p.setId((long)i);
            p.setCatId((long)i);
            products.add(p);
            productIds.add((long)i);

            ProductInfo pInf = new ProductInfo();
            pInf.setId((long)i);
            pInf.setProductInfoCode("code" + i);
            productInfos.add(pInf);
            productInfoIds.add(p.getCatId());
        }
        when(poTransDetailRepo.getPoTransDetailPrint(poTran.getId())).thenReturn(poTransDetails);
        when(productRepo.findAllById(productIds)).thenReturn(products);
//        when(productInfoRepo.findAllById(productInfoIds)).thenReturn(productInfos);
        ReportProductTransDTO data = new ReportProductTransDTO();
        data.setId(1L);
        data.setShop(shopDTO);

        Response<ReportProductTransDTO> response = serviceImp.getInvoice(shopId, id, receiptType);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("receiptType", "" + receiptType)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findComboProductsReceiptType0Type1Test() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/product-trans/" + id;
        Long shopId = 1L;
        Integer receiptType = 0;
        ShopDTO shopDTO = new ShopDTO();
        PoTrans poTran = new PoTrans();
        poTran.setId(1L);
        poTran.setType(1);
        when(shopClient.getByIdV1(shopId)).thenReturn(new Response<ShopDTO>().withData(shopDTO));
        when(poTransRepo.findById(id)).thenReturn(java.util.Optional.of(poTran));
        List<PoTransDetail> poTransDetails = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<Long> productIds = new ArrayList<>();
        List<ProductInfo> productInfos = new ArrayList<>();
        List<Long> productInfoIds = new ArrayList<>();
        for(int i = 1; i < 5; i++){
            PoTransDetail poDtl = new PoTransDetail();
            poDtl.setId((long)i);
            poDtl.setProductId((long)i);
            poTransDetails.add(poDtl);

            Product p = new Product();
            p.setId((long)i);
            p.setCatId((long)i);
            products.add(p);
            productIds.add((long)i);

            ProductInfo pInf = new ProductInfo();
            pInf.setId((long)i);
            pInf.setProductInfoCode("code" + i);
            productInfos.add(pInf);
            productInfoIds.add(p.getCatId());
        }
//        when(poTransDetailRepo.getPoTransDetailPrint(poTran.getId())).thenReturn(poTransDetails);
//        when(productRepo.findAllById(productIds)).thenReturn(products);
//        when(productInfoRepo.findAllById(productInfoIds)).thenReturn(productInfos);
        ReportProductTransDTO data = new ReportProductTransDTO();
        data.setId(1L);
        data.setShop(shopDTO);

        Response<ReportProductTransDTO> response = serviceImp.getInvoice(shopId, id, receiptType);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("receiptType", "" + receiptType)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findComboProductsReceiptType1Test() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/product-trans/" + id;
        Long shopId = 1L;
        Integer receiptType = 1;
        ShopDTO shopDTO = new ShopDTO();
        PoTrans poTran = new PoTrans();
        poTran.setId(1L);
        poTran.setType(1);
        when(shopClient.getByIdV1(shopId)).thenReturn(new Response<ShopDTO>().withData(shopDTO));
//        when(poTransRepo.findById(id)).thenReturn(java.util.Optional.of(poTran));
        List<PoTransDetail> poTransDetails = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<Long> productIds = new ArrayList<>();
        List<ProductInfo> productInfos = new ArrayList<>();
        List<Long> productInfoIds = new ArrayList<>();
        List<StockAdjustmentTransDetail> stockAdjustmentTransDetails = new ArrayList<>();
        for(int i = 1; i < 5; i++){
            PoTransDetail poDtl = new PoTransDetail();
            poDtl.setId((long)i);
            poDtl.setProductId((long)i);
            poTransDetails.add(poDtl);

            Product p = new Product();
            p.setId((long)i);
            p.setCatId((long)i);
            products.add(p);
            productIds.add((long)i);

            ProductInfo pInf = new ProductInfo();
            pInf.setId((long)i);
            pInf.setProductInfoCode("code" + i);
            productInfos.add(pInf);
            productInfoIds.add(p.getCatId());

            StockAdjustmentTransDetail dtl = new StockAdjustmentTransDetail();
            dtl.setId((long)i);
            dtl.setProductId((long)i);
            stockAdjustmentTransDetails.add(dtl);
        }
//        when(poTransDetailRepo.getPoTransDetailPrint(poTran.getId())).thenReturn(poTransDetails);

//
        StockAdjustmentTrans stockTran = new StockAdjustmentTrans();
        stockTran.setId((long)1);
        when(stockAdjustmentTransRepo.findById(id)).thenReturn(java.util.Optional.of(stockTran));
        when(stockAdjustmentTransDetailRepo.getStockAdjustmentTransDetail(stockTran.getId())).thenReturn(stockAdjustmentTransDetails);
        when(productRepo.findAllById(productIds)).thenReturn(products);
        ReportProductTransDTO data = new ReportProductTransDTO();
        data.setId(1L);
        data.setShop(shopDTO);

        Response<ReportProductTransDTO> response = serviceImp.getInvoice(shopId, id, receiptType);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("receiptType", "" + receiptType)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}
