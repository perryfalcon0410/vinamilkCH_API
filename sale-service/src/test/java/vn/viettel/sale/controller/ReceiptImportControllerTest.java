package vn.viettel.sale.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.PoConfirm;
import vn.viettel.sale.entities.PoDetail;
import vn.viettel.sale.entities.PoTrans;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.StockAdjustment;
import vn.viettel.sale.entities.StockAdjustmentDetail;
import vn.viettel.sale.entities.StockBorrowing;
import vn.viettel.sale.entities.StockBorrowingDetail;
import vn.viettel.sale.entities.WareHouseType;
import vn.viettel.sale.messaging.NotImportRequest;
import vn.viettel.sale.messaging.ReceiptCreateDetailRequest;
import vn.viettel.sale.messaging.ReceiptCreateRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.messaging.TotalResponseV1;
import vn.viettel.sale.repository.PoConfirmRepository;
import vn.viettel.sale.repository.PoDetailRepository;
import vn.viettel.sale.repository.PoTransDetailRepository;
import vn.viettel.sale.repository.PoTransRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.StockAdjustmentDetailRepository;
import vn.viettel.sale.repository.StockAdjustmentRepository;
import vn.viettel.sale.repository.StockBorrowingDetailRepository;
import vn.viettel.sale.repository.StockBorrowingRepository;
import vn.viettel.sale.repository.WareHouseTypeRepository;
import vn.viettel.sale.service.ReceiptImportService;
import vn.viettel.sale.service.StockTotalService;
import vn.viettel.sale.service.dto.PoConfirmDTO;
import vn.viettel.sale.service.dto.PoDetailDTO;
import vn.viettel.sale.service.dto.ReceiptImportDTO;
import vn.viettel.sale.service.dto.ReceiptImportListDTO;
import vn.viettel.sale.service.dto.StockAdjustmentDTO;
import vn.viettel.sale.service.dto.StockBorrowingDTO;
import vn.viettel.sale.service.feign.ApparamClient;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.service.impl.ReceiptImportServiceImpl;

public class ReceiptImportControllerTest extends BaseTest {
    private final String root = "/sales/import";

    @InjectMocks
    ReceiptImportServiceImpl serviceImp;

    @Mock
    ReceiptImportService service;

    @Mock
    PoTransRepository repository;

    @Mock
    ProductRepository productRepository;

    @Mock
    PoTransDetailRepository poTransDetailRepository;

    @Mock
    StockTotalService stockTotalService;

    @Mock
    ShopClient shopClient;

    @Mock
    PoConfirmRepository poConfirmRepository;

    @Mock
    PoDetailRepository poDetailRepository;

    @Mock
    UserClient userClient;

    @Mock
    CustomerClient customerClient;

    @Mock
    StockAdjustmentRepository stockAdjustmentRepository;

    @Mock
    ApparamClient apparamClient;

    @Mock
    StockBorrowingRepository stockBorrowingRepository;

    @Mock
    StockAdjustmentDetailRepository stockAdjustmentDetailRepository;

    @Mock
    StockBorrowingDetailRepository stockBorrowingDetailRepository;

    @Mock
    CustomerTypeClient customerTypeClient;

    @Mock
    WareHouseTypeRepository wareHouseTypeRepository;

    Integer type = 0;
    Long shopId = 1L;
    String transCode = "CODE";
    String redInvoiceNo = "INVOICENO";
    java.time.LocalDateTime fromDate = LocalDateTime.of(2015,1,1,0,0);
    java.time.LocalDateTime toDate = LocalDateTime.now();
    List<ReceiptImportListDTO> list = new ArrayList<>();
    List<ReceiptImportDTO> listDto = new ArrayList<>();
    Pageable pageable;
    Page<ReceiptImportListDTO> receiptImportListDTO;
    ReceiptCreateRequest request = new ReceiptCreateRequest();
    ResponseMessage responseMessage = ResponseMessage.CREATED_SUCCESSFUL;
    Long userId = 1L;
    List<Product> products = new ArrayList<>();
    List<PoDetail> poDetails = new ArrayList<>();
    Long id = 1L;
    Long wareHouseTypeId = 1L;
    List<StockAdjustmentDetail> adjustmentDetails = new ArrayList<>();
    List<StockBorrowingDetail> borrowingDetails = new ArrayList<>();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final ReceiptImportController controller = new ReceiptImportController();
        controller.setService(service);
        this.setupAction(controller);

        fromDate = DateUtils.convertFromDate(fromDate);
        toDate = DateUtils.convertToDate(toDate);
        int page = 1;

        request.setPoId(1L);
        request.setImportType(0);
        request.setInternalNumber("PO12345");
        request.setPoCoNumber("CO123");
        request.setRedInvoiceNo("RE123");
        List<ReceiptCreateDetailRequest> receiptCreateDetailRequests = new ArrayList<>();

        for (int i = 1; i < 5; i++){
            ReceiptImportListDTO dto = new ReceiptImportListDTO();
            dto.setId((long)i);
            dto.setTransCode(transCode);
            dto.setRedInvoiceNo(redInvoiceNo);
            dto.setTotalQuantity(2);
            dto.setTotalAmount(Float.parseFloat("10"));
            dto.setTransDate(java.time.LocalDateTime.now());
            dto.setReceiptType(1);
            list.add(dto);
            listDto.add(dto);

            ReceiptCreateDetailRequest receiptCreateDetailRequest = new ReceiptCreateDetailRequest();
            receiptCreateDetailRequest.setId((long)i);
            receiptCreateDetailRequest.setProductId((long)i);
            receiptCreateDetailRequest.setQuantity(1 + i);
            receiptCreateDetailRequests.add(receiptCreateDetailRequest);

            Product product = new Product();
            product.setId((long)i);
            product.setProductCode("CODE" + i);
            products.add(product);

            PoDetail poDetail = new PoDetail();
            poDetail.setId((long)i);
            poDetail.setPoId((long)i);
            poDetail.setProductId((long)i);
            poDetail.setPrice(5.0);
            poDetail.setQuantity(i + 2);
            poDetail.setPriceNotVat(5.0);
            poDetails.add(poDetail);

            StockAdjustmentDetail stockAdjustmentDetail = new StockAdjustmentDetail();
            stockAdjustmentDetail.setId((long)i);
            stockAdjustmentDetail.setProductId((long)i);
            stockAdjustmentDetail.setPrice(5.0);
            stockAdjustmentDetail.setQuantity(i + 2);
            adjustmentDetails.add(stockAdjustmentDetail);

            StockBorrowingDetail stockBorrowingDetail = new StockBorrowingDetail();
            stockBorrowingDetail.setId((long)i);
            stockBorrowingDetail.setProductId((long)i);
            stockBorrowingDetail.setPrice(Float.parseFloat(5 + ""));
            stockBorrowingDetail.setQuantity(i + 2);
            borrowingDetails.add(stockBorrowingDetail);
        }
        pageable = PageRequest.of(page, list.size());
        receiptImportListDTO = new PageImpl<>(list, pageable , list.size());

        request.setLst(receiptCreateDetailRequests);

        ShopDTO shopDTO = new ShopDTO();
        shopDTO.setShopCode("ShopCode");
        Response<ShopDTO> responseShopDTO = new Response<>();
        responseShopDTO.withData(shopDTO);
        given(shopClient.getByIdV1(shopId)).willReturn(responseShopDTO);

        given(poDetailRepository.getPoDetailByPoIdAndPriceIsGreaterThan(id)).willReturn(poDetails);

        given(productRepository.getProducts(poDetails.stream().map(item -> item.getProductId()).distinct()
                .collect(Collectors.toList()), null)).willReturn(products);
    }

    @Test
    public void findALlProductInfoTypeNULLTest() throws Exception {
        String uri = V1 + root ;
        type = null;
        List<Integer> sorts = new ArrayList<>();
        Page<ReceiptImportDTO> redInvoiceDTOs = new PageImpl<>(listDto, pageable , listDto.size());
        when(repository.getReceipt(shopId, 1, transCode, redInvoiceNo, fromDate, toDate, sorts, pageable)).thenReturn(redInvoiceDTOs);
        when(repository.getTotalResponsePo(shopId, 1, transCode, redInvoiceNo, fromDate, toDate)).thenReturn(new TotalResponse());
        when(repository.getTotalResponseAdjustment(shopId, 1, transCode, redInvoiceNo, fromDate, toDate)).thenReturn(new TotalResponse());
        when(repository.getTotalResponseBorrowing(shopId, 1, transCode, redInvoiceNo, fromDate, toDate)).thenReturn(new TotalResponse());

        CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> result = serviceImp.find(transCode, redInvoiceNo, fromDate, toDate, type, shopId, pageable);
        assertNotNull(result);
        assertEquals(listDto.size(),((Page<ReceiptImportListDTO>)result.getResponse()).getSize());

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("fromDate", "01/04/2021")
                .param("toDate", "01/04/2021")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findALlProductInfoType0Test() throws Exception {
        String uri = V1 + root ;
        type = 0;

        when(repository.getReceiptPo(shopId, 1, transCode, redInvoiceNo, fromDate, toDate, pageable)).thenReturn(receiptImportListDTO);
        when(repository.getTotalResponsePo(shopId, 1, transCode, redInvoiceNo, fromDate, toDate)).thenReturn(new TotalResponse());

        CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> result = serviceImp.find(transCode, redInvoiceNo, fromDate, toDate, type, shopId, pageable);
        assertNotNull(result);
        assertEquals(list.size(),((Page<ReceiptImportListDTO>)result.getResponse()).getSize());

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("fromDate", "2022/02/22")
                .param("toDate", "2022/02/22")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findALlProductInfoType1Test() throws Exception {
        String uri = V1 + root ;
        type = 1;
        when(repository.getReceiptAdjustment(shopId, 1, transCode, redInvoiceNo, fromDate, toDate, pageable)).thenReturn(receiptImportListDTO);
        when(repository.getTotalResponseAdjustment(shopId, 1, transCode, redInvoiceNo, fromDate, toDate)).thenReturn(new TotalResponse());

        CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> result = serviceImp.find(transCode, redInvoiceNo, fromDate, toDate, type, shopId, pageable);
        assertNotNull(result);
        assertEquals(list.size(),((Page<ReceiptImportListDTO>)result.getResponse()).getSize());

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("fromDate", "2022/02/22")
                .param("toDate", "2022/02/22")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findALlProductInfoType2Test() throws Exception {
        String uri = V1 + root ;
        type = 2;
        when(repository.getReceiptBorrowing(shopId, 1, transCode, redInvoiceNo, fromDate, toDate, pageable)).thenReturn(receiptImportListDTO);
        when(repository.getTotalResponseBorrowing(shopId, 1, transCode, redInvoiceNo, fromDate, toDate)).thenReturn(new TotalResponse());

        CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> result = serviceImp.find(transCode, redInvoiceNo, fromDate, toDate, type, shopId, pageable);
        assertNotNull(result);
        assertEquals(list.size(),((Page<ReceiptImportListDTO>)result.getResponse()).getSize());

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("fromDate", "2022/02/22")
                        .param("toDate", "2022/02/22")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void createImportType0AndPoIdNULLTest() throws Exception {
        String uri = V1 + root;
        request.setImportType(0);
        request.setPoId(null);

        given(repository.getByRedInvoiceNo(request.getRedInvoiceNo().trim())).willReturn(new ArrayList<PoTrans>());
        given(repository.getByPoCoNumber(request.getPoCoNumber().trim())).willReturn(new ArrayList<PoTrans>());
        given(repository.getByInternalNumber(request.getInternalNumber().trim())).willReturn(new ArrayList<PoTrans>());

        List<Long> result = serviceImp.createReceipt(request, userId, shopId);
        assertNotNull(result);
        assertEquals(result, responseMessage);

        String inputJson = super.mapToJson(request);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void createImportType0AndPoIdNOTNULLTest() throws Exception {
        String uri = V1 + root;
        request.setImportType(0);
        request.setPoId((long)1);
        PoConfirm poConfirm = new PoConfirm();
        poConfirm.setId((long)1);
        poConfirm.setShopId(shopId);
        poConfirm.setStatus(0);
        poConfirm.setWareHouseTypeId(wareHouseTypeId);
        given(poConfirmRepository.getById(request.getPoId())).willReturn(poConfirm);

        given(poDetailRepository.findByPoId(poConfirm.getId())).willReturn(new ArrayList<>());

        List<Long> result = serviceImp.createReceipt(request, userId, shopId);
        assertNotNull(result);
        assertEquals(result, responseMessage);

        String inputJson = super.mapToJson(request);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    public void createImportType1Test() throws Exception {
        String uri = V1 + root;
        request.setImportType(0);
        request.setPoId((long)1);

        given(userClient.getUserByIdV1(userId)).willReturn(new UserDTO());
        given(customerClient.getCusDefault(shopId)).willReturn(new CustomerDTO());

        StockAdjustment stockAdjustment = new StockAdjustment();
        stockAdjustment.setWareHouseTypeId(wareHouseTypeId);
        stockAdjustment.setStatus(0);
        stockAdjustment.setReasonId((long)1);
        given(stockAdjustmentRepository.getById(request.getPoId(), shopId, 1)).willReturn(stockAdjustment);
        Response<ApParamDTO> reason = new Response<>();
        ApParamDTO apParamDTO = new ApParamDTO();
        apParamDTO.setId((long)1);
        reason.withData(apParamDTO);
        given(apparamClient.getReasonV1(stockAdjustment.getReasonId())).willReturn(reason);

        List<Long> result = serviceImp.createReceipt(request, userId, shopId);
        assertNotNull(result);
        assertEquals(result, responseMessage);

        String inputJson = super.mapToJson(request);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    public void createImportType2Test() throws Exception {
        String uri = V1 + root;
        request.setImportType(2);
        request.setPoId((long)1);

        given(stockBorrowingRepository.getImportById(request.getPoId(), shopId)).willReturn(new StockBorrowing());

        List<Long> result = serviceImp.createReceipt(request, userId, shopId);
        assertNotNull(result);
        assertEquals(result, responseMessage);

        String inputJson = super.mapToJson(request);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getListPoConfirmTest() throws Exception {
        String uri = V1 + root + "/po-confirm";
        List<PoConfirm> poConfirms = Arrays.asList(new PoConfirm(), new PoConfirm());
        given(poConfirmRepository.getPoConfirm(shopId)).willReturn(poConfirms);

        List<PoConfirmDTO> lstResult = serviceImp.getListPoConfirm(shopId);
        assertNotNull(lstResult);
        assertEquals(poConfirms.size(), lstResult.size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getListStockAdjustmentTest() throws Exception {
        String uri = V1 + root + "/adjustment" ;
        List<StockAdjustment> list = Arrays.asList(new StockAdjustment(), new StockAdjustment());
        given(stockAdjustmentRepository.getStockAdjustment(shopId)).willReturn(list);

        List<StockAdjustmentDTO> lstResult = serviceImp.getListStockAdjustment(shopId, PageRequest.of(1, 5));

        assertNotNull(lstResult);
        assertEquals(list.size(), lstResult.size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getListStockBorrowingTest() throws Exception {
        String uri = V1 + root + "/borrowing" ;

        LocalDateTime date1 = LocalDateTime.now().toLocalDate().atTime(LocalTime.MIN);
        LocalDateTime date2 = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);
        List<StockBorrowingDTO> list = Arrays.asList(new StockBorrowingDTO(), new StockBorrowingDTO());
        given(stockBorrowingRepository.getStockBorrowingImport(shopId,date1,date2)).willReturn(list);

        List<StockBorrowingDTO> lstResult = serviceImp.getListStockBorrowing(shopId, PageRequest.of(1, 5));
        assertNotNull(lstResult);
        assertEquals(list.size(), lstResult.size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getPoDetailByPoIdTest() throws Exception {
        String uri = V1 + root + "/po-detail0/{id}";

        CoverResponse<List<PoDetailDTO>, TotalResponseV1> result = serviceImp.getPoDetailByPoId(id, shopId);

        ResultActions resultActions = mockMvc.perform(get(uri, id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getPoDetailByPoIdAndPriceIsNullTest() throws Exception {
        String uri = V1 + root + "/po-detail1/" + id.toString();

        serviceImp.getPoDetailByPoIdAndPriceIsNull(id, shopId);

        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getStockAdjustmentDetail() throws Exception {
        String uri = V1 + root + "/adjustment-detail/{id}";
        given(stockAdjustmentDetailRepository.getStockAdjustmentDetailByAdjustmentId(id)).willReturn(adjustmentDetails);

        serviceImp.getStockAdjustmentDetail(id);

        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getStockBorrowingDetail() throws Exception {
        String uri = V1 + root + "/borrowing-detail/{id}";
        given(stockBorrowingDetailRepository.findByBorrowingId(id)).willReturn(borrowingDetails);

        serviceImp.getStockBorrowingDetail(id);

        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getWareHouseType() throws Exception {
        String uri = V1 + root + "/warehouse-type";

        given(customerTypeClient.getWarehouseTypeByShopId(shopId)).willReturn(wareHouseTypeId);
        given(wareHouseTypeRepository.findById(wareHouseTypeId)).willReturn(java.util.Optional.of(new WareHouseType()));
        vn.viettel.core.dto.sale.WareHouseTypeDTO result = serviceImp.getWareHouseTypeName(shopId);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void setNotImport() throws Exception {
        String uri = V1 + root + "/not-import/{Id}";
        NotImportRequest request = new NotImportRequest();
        request.setId(1L);
        request.setReasonDeny(1);
        ResponseMessage response = ResponseMessage.SUCCESSFUL;

        PoConfirm poConfirm = new PoConfirm();
        poConfirm.setId((long)1);
        poConfirm.setShopId(shopId);
        poConfirm.setStatus(0);
        poConfirm.setWareHouseTypeId(wareHouseTypeId);
        given(poConfirmRepository.getById(id)).willReturn(poConfirm);

        serviceImp.setNotImport( id, "userName", request);

        String inputJson = super.mapToJson(request);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.put(uri, 1L)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

}
