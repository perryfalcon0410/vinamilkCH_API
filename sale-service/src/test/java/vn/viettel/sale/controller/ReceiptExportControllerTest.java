package vn.viettel.sale.controller;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.PoTrans;
import vn.viettel.sale.entities.PoTransDetail;
import vn.viettel.sale.entities.Price;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.entities.SaleOrderDetail;
import vn.viettel.sale.entities.StockAdjustment;
import vn.viettel.sale.entities.StockAdjustmentDetail;
import vn.viettel.sale.entities.StockAdjustmentTrans;
import vn.viettel.sale.entities.StockAdjustmentTransDetail;
import vn.viettel.sale.entities.StockBorrowing;
import vn.viettel.sale.entities.StockBorrowingDetail;
import vn.viettel.sale.entities.StockBorrowingTrans;
import vn.viettel.sale.entities.StockBorrowingTransDetail;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.sale.messaging.ReceiptCreateDetailRequest;
import vn.viettel.sale.messaging.ReceiptExportCreateRequest;
import vn.viettel.sale.messaging.ReceiptExportUpdateRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.repository.PoConfirmRepository;
import vn.viettel.sale.repository.PoDetailRepository;
import vn.viettel.sale.repository.PoTransDetailRepository;
import vn.viettel.sale.repository.PoTransRepository;
import vn.viettel.sale.repository.ProductPriceRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.SaleOrderDetailRepository;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.repository.StockAdjustmentDetailRepository;
import vn.viettel.sale.repository.StockAdjustmentRepository;
import vn.viettel.sale.repository.StockAdjustmentTransDetailRepository;
import vn.viettel.sale.repository.StockAdjustmentTransRepository;
import vn.viettel.sale.repository.StockBorrowingDetailRepository;
import vn.viettel.sale.repository.StockBorrowingRepository;
import vn.viettel.sale.repository.StockBorrowingTransDetailRepository;
import vn.viettel.sale.repository.StockBorrowingTransRepository;
import vn.viettel.sale.repository.StockTotalRepository;
import vn.viettel.sale.repository.WareHouseTypeRepository;
import vn.viettel.sale.service.ReceiptExportService;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.StockTotalService;
import vn.viettel.sale.service.dto.PoTransDTO;
import vn.viettel.sale.service.dto.ReceiptImportDTO;
import vn.viettel.sale.service.dto.ReceiptImportListDTO;
import vn.viettel.sale.service.dto.StockBorrowingDTO;
import vn.viettel.sale.service.feign.ApparamClient;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.service.impl.ReceiptExportServiceImpl;

public class ReceiptExportControllerTest extends BaseTest {
    private final String root = "/sales/export";

    @InjectMocks
    ReceiptExportServiceImpl serviceImp;

    @Mock
    ReceiptExportService service;

    @Mock
    PoTransRepository repository;

    @Mock
    PoTransDetailRepository poTransDetailRepository;

    @Mock
    StockTotalRepository stockTotalRepository;

    @Mock
    StockTotalService stockTotalService;

    @Mock
    CustomerClient customerClient;

    @Mock
    ShopClient shopClient;

    @Mock
    StockAdjustmentRepository stockAdjustmentRepository;

    @Mock
    ApparamClient apparamClient;

    @Mock
    StockAdjustmentDetailRepository stockAdjustmentDetailRepository;

    @Mock
    ProductPriceRepository productPriceRepository;

    @Mock
    StockAdjustmentTransRepository stockAdjustmentTransRepository;

    @Mock
    StockAdjustmentTransDetailRepository stockAdjustmentTransDetailRepository;

    @Mock
    SaleOrderDetailRepository saleOrderDetailRepository;

    @Mock
    SaleService saleService;

    @Mock
    PoConfirmRepository poConfirmRepository;
    @Mock
    PoDetailRepository poDetailRepository;
    @Mock
    UserClient userClient;
    @Mock
    ProductRepository productRepository;
    @Mock
    StockBorrowingTransRepository stockBorrowingTransRepository;
    @Mock
    StockBorrowingTransDetailRepository stockBorrowingTransDetailRepository;
    @Mock
    StockBorrowingRepository stockBorrowingRepository;
    @Mock
    StockBorrowingDetailRepository stockBorrowingDetailRepository;
    @Mock
    SaleOrderRepository saleOrderRepository;
    @Mock
    CustomerTypeClient customerTypeClient;
    @Mock
    WareHouseTypeRepository wareHouseTypeRepository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final ReceiptExportController controller = new ReceiptExportController();
        controller.setService(service);
        this.setupAction(controller);
    }

    @Test
    public void findTypeNullTest() throws Exception {
        String uri = V1 + root;
        Long shopId = 1L;
        String transCode = "123";
        String redInvoiceNo = "123";
        LocalDateTime fromDate = LocalDateTime.now();
        LocalDateTime toDate = LocalDateTime.now();
        fromDate = DateUtils.convertFromDate(fromDate);
        toDate = DateUtils.convertToDate(toDate);
        Sort order = Sort.by(Sort.Direction.DESC, "transDate");
        Pageable pageable = PageRequest.of(0, 5, order);
        List<Integer> sorts = new ArrayList<>();
        sorts.add(1);
        Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        List<ReceiptImportDTO> lstReceiptImportDTO = new ArrayList<>();
        Page<ReceiptImportDTO> pageResponse = new PageImpl<>(lstReceiptImportDTO);
        when(repository.getReceipt(shopId, 2, transCode, redInvoiceNo, fromDate, toDate, sorts, page)).thenReturn(pageResponse);
        when(repository.getTotalResponsePo(shopId, 2, transCode, redInvoiceNo, fromDate, toDate)).thenReturn(new TotalResponse());
        when(repository.getTotalResponseAdjustment(shopId, 2, transCode, redInvoiceNo, fromDate, toDate)).thenReturn(new TotalResponse());
        when(repository.getTotalResponseBorrowing(shopId, 2, transCode, redInvoiceNo, fromDate, toDate)).thenReturn(new TotalResponse());

        CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> result = serviceImp.find(transCode, redInvoiceNo, fromDate,
                toDate, null, shopId, pageable);
        assertNotNull(result);

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
    public void findType0Test() throws Exception {
        String uri = V1 + root;
        Long shopId = 1L;
        String transCode = "123";
        String redInvoiceNo = "123";
        LocalDateTime fromDate = LocalDateTime.now();
        LocalDateTime toDate = LocalDateTime.now();
        fromDate = DateUtils.convertFromDate(fromDate);
        toDate = DateUtils.convertToDate(toDate);
        Sort order = Sort.by(Sort.Direction.DESC, "transDate");
        Pageable pageable = PageRequest.of(0, 5, order);
        List<Integer> sorts = new ArrayList<>();
        sorts.add(1);
        List<ReceiptImportListDTO> lstReceiptImportDTO = new ArrayList<>();
        Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(lstReceiptImportDTO);
        when(repository.getReceiptPo(shopId, 2, transCode, redInvoiceNo, fromDate, toDate, pageable)).thenReturn(pageResponse);

        CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> result = serviceImp.find(transCode, redInvoiceNo, fromDate,
                toDate, 0, shopId, pageable);
        assertNotNull(result);

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
    public void findType1Test() throws Exception {
        String uri = V1 + root;
        Long shopId = 1L;
        String transCode = "123";
        String redInvoiceNo = "123";
        LocalDateTime fromDate = LocalDateTime.now();
        LocalDateTime toDate = LocalDateTime.now();
        fromDate = DateUtils.convertFromDate(fromDate);
        toDate = DateUtils.convertToDate(toDate);
        Sort order = Sort.by(Sort.Direction.DESC, "transDate");
        Pageable pageable = PageRequest.of(0, 5, order);
        List<Integer> sorts = new ArrayList<>();
        sorts.add(1);
        List<ReceiptImportListDTO> lstReceiptImportDTO = new ArrayList<>();
        Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(lstReceiptImportDTO);
        when(repository.getReceiptAdjustment(shopId, 2, transCode, redInvoiceNo, fromDate, toDate, pageable)).thenReturn(pageResponse);

        CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> result = serviceImp.find(transCode, redInvoiceNo, fromDate,
                toDate, 1, shopId, pageable);
        assertNotNull(result);

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
    public void findType2Test() throws Exception {
        String uri = V1 + root;
        Long shopId = 1L;
        String transCode = "123";
        String redInvoiceNo = "123";
        LocalDateTime fromDate = LocalDateTime.now();
        LocalDateTime toDate = LocalDateTime.now();
        fromDate = DateUtils.convertFromDate(fromDate);
        toDate = DateUtils.convertToDate(toDate);
        Sort order = Sort.by(Sort.Direction.DESC, "transDate");
        Pageable pageable = PageRequest.of(0, 5, order);
        List<Integer> sorts = new ArrayList<>();
        sorts.add(1);
        List<ReceiptImportListDTO> lstReceiptImportDTO = new ArrayList<>();
        Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(lstReceiptImportDTO);
        when(repository.getReceiptBorrowing(shopId, 2, transCode, redInvoiceNo, fromDate, toDate, pageable)).thenReturn(pageResponse);

        CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> result = serviceImp.find(transCode, redInvoiceNo, fromDate,
                toDate, 2, shopId, pageable);
        assertNotNull(result);

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
    public void createImportType0IsRemainAllTrueTest() throws Exception {
        String uri = V1 + root;
        Long id = 1L;
        ReceiptExportCreateRequest request = new ReceiptExportCreateRequest();
        request.setImportType(0);
        request.setReceiptImportId(id);
        request.setInternalNumber("PO12345");
        request.setRedInvoiceNo("RE123");
        request.setIsRemainAll(true);
        request.setNote("Ghi chu");
        request.setTransCode("CD001");
        List<ReceiptCreateDetailRequest> lstReceips = new ArrayList<>();
        ReceiptCreateDetailRequest receiptCreateDetailRequest = new ReceiptCreateDetailRequest();
        receiptCreateDetailRequest.setId(id);
        lstReceips.add(receiptCreateDetailRequest);
        request.setLitQuantityRemain(lstReceips);
        Long userId = 1L;
        Long shopId = 1L;
        PoTrans poTrans = new PoTrans();
        poTrans.setWareHouseTypeId(2L);
        poTrans.setId(id);
        poTrans.setTransCode("00001");
        List<PoTransDetail> poTransDetails = new ArrayList<>();
        PoTransDetail poTransDetail = new PoTransDetail();
        poTransDetail.setProductId(1L);
        poTransDetail.setId(1L);
        poTransDetail.setQuantity(5);
        poTransDetail.setPrice(5.0);
        poTransDetail.setReturnAmount(1);
        poTransDetails.add(poTransDetail);

        Response shopResponse = new Response();
        ShopDTO shopDTO = new ShopDTO();
        shopDTO.setShopCode("123");
        shopResponse.setData(shopDTO);
        when(shopClient.getByIdV1(shopId)).thenReturn(shopResponse);
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());

        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXSP.");
        reciCode.append(shopDTO.getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        List<PoTrans> lstPoTrans = new ArrayList<>();
        lstPoTrans.add(poTrans);
        Pageable pageable = PageRequest.of(0,1);
        Page<PoTrans> pos = new PageImpl<>(lstPoTrans, pageable, lstPoTrans.size());
        try(MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            String instantExpected = "2022-02-22T10:15:30Z";
            Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
            LocalDateTime now = LocalDateTime.now(clock);
            mockedStatic.when(LocalDateTime::now).thenReturn(now);

            when(repository.getLastTransCode(2, reciCode.toString(),
                    LocalDateTime.now().with(firstDayOfYear()), PageRequest.of(0,1))).thenReturn(pos);
            when(repository.findByIdAndShopIdAndTypeAndStatus(request.getReceiptImportId(), shopId, 1,1 ))
                    .thenReturn(java.util.Optional.of(poTrans));
            when(poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId())).thenReturn(poTransDetails);

            List<StockTotal> stockTotals = new ArrayList<>();
            StockTotal stockTotal = new StockTotal();
            stockTotal.setProductId(1L);
            stockTotal.setId(1L);
            stockTotal.setQuantity(20);
            stockTotals.add(stockTotal);

            when(stockTotalRepository.getStockTotal(shopId, poTrans.getWareHouseTypeId(),
                    poTransDetails.stream().map(item -> item.getProductId()).distinct().filter(Objects::nonNull)
                            .collect(Collectors.toList()))).thenReturn(stockTotals);

            PoTrans po = new PoTrans();
            po.setId(1l);
            Mockito.when(repository.save(Mockito.isA(PoTrans.class))).thenReturn(po);
            List<Long> res = serviceImp.createReceipt( request, userId, shopId);
            assertNotNull(res);
        }

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
    public void createImportType0IsRemainAllFalseTest() throws Exception {
        String uri = V1 + root;
        Long id = 1L;
        ReceiptExportCreateRequest request = new ReceiptExportCreateRequest();
        request.setImportType(0);
        request.setReceiptImportId(id);
        request.setInternalNumber("PO12345");
        request.setRedInvoiceNo("RE123");
        request.setIsRemainAll(false);
        request.setNote("Ghi chu");
        request.setTransCode("CD001");

        List<ReceiptCreateDetailRequest> lstReceips = new ArrayList<>();
        ReceiptCreateDetailRequest receiptCreateDetailRequest = new ReceiptCreateDetailRequest();
        receiptCreateDetailRequest.setId(id);
        receiptCreateDetailRequest.setQuantity(1);
        lstReceips.add(receiptCreateDetailRequest);
        request.setLitQuantityRemain(lstReceips);
        Long userId = 1L;
        Long shopId = 1L;
        PoTrans poTrans = new PoTrans();
        poTrans.setWareHouseTypeId(2L);
        poTrans.setId(id);
        poTrans.setTransCode("00001");
        List<PoTransDetail> poTransDetails = new ArrayList<>();
        PoTransDetail poTransDetail = new PoTransDetail();
        poTransDetail.setProductId(1L);
        poTransDetail.setId(1L);
        poTransDetail.setQuantity(5);
        poTransDetail.setPrice(5.0);
        poTransDetail.setReturnAmount(1);
        poTransDetails.add(poTransDetail);

        Response shopResponse = new Response();
        ShopDTO shopDTO = new ShopDTO();
        shopDTO.setShopCode("123");
        shopResponse.setData(shopDTO);
        when(shopClient.getByIdV1(shopId)).thenReturn(shopResponse);
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());

        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXSP.");
        reciCode.append(shopDTO.getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        List<PoTrans> lstPoTrans = new ArrayList<>();
        lstPoTrans.add(poTrans);
        Pageable pageable = PageRequest.of(0,1);
        Page<PoTrans> pos = new PageImpl<>(lstPoTrans, pageable, lstPoTrans.size());
        try(MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            String instantExpected = "2022-02-22T10:15:30Z";
            Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
            LocalDateTime now = LocalDateTime.now(clock);
            mockedStatic.when(LocalDateTime::now).thenReturn(now);

            when(repository.getLastTransCode(2, reciCode.toString(),
                    LocalDateTime.now().with(firstDayOfYear()), PageRequest.of(0,1))).thenReturn(pos);
            when(repository.findByIdAndShopIdAndTypeAndStatus(request.getReceiptImportId(), shopId, 1,1 ))
                    .thenReturn(java.util.Optional.of(poTrans));
            when(poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId())).thenReturn(poTransDetails);

            List<StockTotal> stockTotals = new ArrayList<>();
            StockTotal stockTotal = new StockTotal();
            stockTotal.setProductId(1L);
            stockTotal.setId(1L);
            stockTotal.setQuantity(20);
            stockTotals.add(stockTotal);

            when(stockTotalRepository.getStockTotal(shopId, poTrans.getWareHouseTypeId(),
                    poTransDetails.stream().map(item -> item.getProductId()).distinct().filter(Objects::nonNull)
                            .collect(Collectors.toList()))).thenReturn(stockTotals);

            PoTrans po = new PoTrans();
            po.setId(1l);
            Mockito.when(repository.save(Mockito.isA(PoTrans.class))).thenReturn(po);
            List<Long> res = serviceImp.createReceipt( request, userId, shopId);
            assertNotNull(res);
        }

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
    public void createImportType1Test() throws Exception {
        String uri = V1 + root;
        Long id = 1L;
        ReceiptExportCreateRequest request = new ReceiptExportCreateRequest();
        request.setImportType(1);
        request.setReceiptImportId(id);
        request.setInternalNumber("PO12345");
        request.setRedInvoiceNo("RE123");
        request.setIsRemainAll(false);
        request.setNote("Ghi chu");
        request.setTransCode("CD001");

        List<ReceiptCreateDetailRequest> lstReceips = new ArrayList<>();
        ReceiptCreateDetailRequest receiptCreateDetailRequest = new ReceiptCreateDetailRequest();
        receiptCreateDetailRequest.setId(id);
        receiptCreateDetailRequest.setQuantity(1);
        lstReceips.add(receiptCreateDetailRequest);
        request.setLitQuantityRemain(lstReceips);
        Long userId = 1L;
        Long shopId = 1L;
        PoTrans poTrans = new PoTrans();
        poTrans.setWareHouseTypeId(2L);
        poTrans.setId(id);
        poTrans.setTransCode("00001");
        List<PoTransDetail> poTransDetails = new ArrayList<>();
        PoTransDetail poTransDetail = new PoTransDetail();
        poTransDetail.setProductId(1L);
        poTransDetail.setId(1L);
        poTransDetail.setQuantity(5);
        poTransDetail.setPrice(5.0);
        poTransDetail.setReturnAmount(1);
        poTransDetails.add(poTransDetail);

        Response shopResponse = new Response();
        ShopDTO shopDTO = new ShopDTO();
        shopDTO.setShopCode("123");
        shopResponse.setData(shopDTO);
        when(shopClient.getByIdV1(shopId)).thenReturn(shopResponse);
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());

        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXST.");
        reciCode.append(shopDTO.getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        List<PoTrans> lstPoTrans = new ArrayList<>();
        lstPoTrans.add(poTrans);
        Pageable pageable = PageRequest.of(0,2);
        Page<PoTrans> pos = new PageImpl<>(lstPoTrans, pageable, lstPoTrans.size());
        try(MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            String instantExpected = "2022-02-22T10:15:30Z";
            Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
            LocalDateTime now = LocalDateTime.now(clock);
            mockedStatic.when(LocalDateTime::now).thenReturn(now);

            List<StockAdjustmentTrans> lstStockAdjustmentTrans = new ArrayList<>();
            StockAdjustmentTrans stockAdjustmentTrans = new StockAdjustmentTrans();
            stockAdjustmentTrans.setInternalNumber("00001");
            lstStockAdjustmentTrans.add(stockAdjustmentTrans);
            Page<StockAdjustmentTrans> stockAdjustmentTransPage = new PageImpl<>(lstStockAdjustmentTrans, pageable, lstStockAdjustmentTrans.size());
            when(stockAdjustmentTransRepository.getLastInternalCode(2,
                    reciCode.toString(), LocalDateTime.now().with(firstDayOfYear()), pageable)).thenReturn(stockAdjustmentTransPage);

            CustomerDTO cus = new CustomerDTO();
            when(customerClient.getCusDefault(shopId)).thenReturn(cus);

            StockAdjustment stockAdjustment = new StockAdjustment();
            stockAdjustment.setStatus(1);
            stockAdjustment.setReasonId(1L);
            stockAdjustment.setId(1L);
            stockAdjustment.setWareHouseTypeId(1L);
            when(stockAdjustmentRepository.getById(request.getReceiptImportId(), shopId, 2)).thenReturn(stockAdjustment);

            Response<ApParamDTO> reason = new Response<>();
            ApParamDTO apParamDTO = new ApParamDTO();
            apParamDTO.setId(1L);
            reason.setData(apParamDTO);
            when(apparamClient.getReasonV1(stockAdjustment.getReasonId())).thenReturn(reason);

            List<StockAdjustmentDetail> sads = new ArrayList<>();
            StockAdjustmentDetail stockAdjustmentDetail = new StockAdjustmentDetail();
            stockAdjustmentDetail.setProductId(1L);
            stockAdjustmentDetail.setQuantity(1);
            sads.add(stockAdjustmentDetail);
            when(stockAdjustmentDetailRepository.getStockAdjustmentDetailByAdjustmentId(stockAdjustment.getId()))
                    .thenReturn(sads);

            LocalDateTime ldt = LocalDateTime.now();
            List<Price> prices = new ArrayList<>();
            Price price = new Price();
            price.setProductId(1L);
            prices.add(price);
            when(productPriceRepository.findProductPriceWithType(sads.stream().map(item -> item.getProductId()).distinct()
                    .collect(Collectors.toList()), stockAdjustment.getWareHouseTypeId(), DateUtils.convertToDate(ldt))).thenReturn(prices);

            List<StockTotal> stockTotals = new ArrayList<>();
            StockTotal stockTotal = new StockTotal();
            stockTotal.setProductId(1L);
            stockTotal.setId(1L);
            stockTotal.setQuantity(20);
            stockTotals.add(stockTotal);

            when(stockTotalRepository.getStockTotal(shopId, stockAdjustment.getWareHouseTypeId(),
                    sads.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()))).thenReturn(stockTotals);

            List<Long> res = serviceImp.createReceipt( request, userId, shopId);
            assertNotNull(res);
        }

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
    public void createImportType2Test() throws Exception {
        String uri = V1 + root;
        Long id = 1L;
        ReceiptExportCreateRequest request = new ReceiptExportCreateRequest();
        request.setImportType(2);
        request.setReceiptImportId(id);
        request.setInternalNumber("PO12345");
        request.setRedInvoiceNo("RE123");
        request.setIsRemainAll(false);
        request.setNote("Ghi chu");
        request.setTransCode("CD001");

        List<ReceiptCreateDetailRequest> lstReceips = new ArrayList<>();
        ReceiptCreateDetailRequest receiptCreateDetailRequest = new ReceiptCreateDetailRequest();
        receiptCreateDetailRequest.setId(id);
        receiptCreateDetailRequest.setQuantity(1);
        lstReceips.add(receiptCreateDetailRequest);
        request.setLitQuantityRemain(lstReceips);
        Long userId = 1L;
        Long shopId = 1L;
        PoTrans poTrans = new PoTrans();
        poTrans.setWareHouseTypeId(2L);
        poTrans.setId(id);
        poTrans.setTransCode("00001");
        List<PoTransDetail> poTransDetails = new ArrayList<>();
        PoTransDetail poTransDetail = new PoTransDetail();
        poTransDetail.setProductId(1L);
        poTransDetail.setId(1L);
        poTransDetail.setQuantity(5);
        poTransDetail.setPrice(5.0);
        poTransDetail.setReturnAmount(1);
        poTransDetails.add(poTransDetail);

        Response shopResponse = new Response();
        ShopDTO shopDTO = new ShopDTO();
        shopDTO.setShopCode("123");
        shopResponse.setData(shopDTO);
        when(shopClient.getByIdV1(shopId)).thenReturn(shopResponse);
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());

        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXSB.");
        reciCode.append(shopDTO.getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        List<PoTrans> lstPoTrans = new ArrayList<>();
        lstPoTrans.add(poTrans);
        Pageable pageable = PageRequest.of(0,2);

        try(MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            String instantExpected = "2022-02-22T10:15:30Z";
            Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
            LocalDateTime now = LocalDateTime.now(clock);
            mockedStatic.when(LocalDateTime::now).thenReturn(now);

            List<StockBorrowingTrans> lstStockBorrowingTrans = new ArrayList<>();
            StockBorrowingTrans stockBorrowingTrans = new StockBorrowingTrans();
            stockBorrowingTrans.setTransCode("00001");
            lstStockBorrowingTrans.add(stockBorrowingTrans);
            Page<StockBorrowingTrans> pos = new PageImpl<>(lstStockBorrowingTrans, pageable, lstStockBorrowingTrans.size());
            when(stockBorrowingTransRepository.getLastTransCode(2,
                    reciCode.toString(), LocalDateTime.now().with(firstDayOfYear()), PageRequest.of(0, 1))).thenReturn(pos);

            LocalDate currentDate = LocalDate.now();
            String mm = String.valueOf(currentDate.getMonthValue());
            String dd = String.valueOf(currentDate.getDayOfMonth());

            StringBuilder reciCode1 = new StringBuilder();
            reciCode1.append("EXP_");
            reciCode1.append(shopDTO.getShopCode());
            reciCode1.append("_");
            reciCode1.append(yy);
            reciCode1.append(mm.length() < 2 ? "0" + mm : mm);
            reciCode1.append(dd.length() < 2 ? "0" + dd : dd);
            reciCode1.append("_");

            List<StockBorrowingTrans> lstStockAdjustmentTrans = new ArrayList<>();
            StockBorrowingTrans stockAdjustmentTrans = new StockBorrowingTrans();
            stockAdjustmentTrans.setRedInvoiceNo("001");
            lstStockAdjustmentTrans.add(stockAdjustmentTrans);
            Page<StockBorrowingTrans> stockAdjustmentTransPage = new PageImpl<>(lstStockAdjustmentTrans, pageable, lstStockAdjustmentTrans.size());
            when(stockBorrowingTransRepository.getLastRedInvoiceNo(2,
                    reciCode1.toString(), LocalDateTime.now().with(firstDayOfYear()), PageRequest.of(0,1))).thenReturn(stockAdjustmentTransPage);

            StockBorrowing stockBorrowing = new StockBorrowing();
            stockBorrowing.setStatusExport(1);
            stockBorrowing.setId(1L);
            stockBorrowing.setWareHouseTypeId(1L);
            when(stockBorrowingRepository.getExportById(request.getReceiptImportId(), shopId)).thenReturn(stockBorrowing);

            StockBorrowingDetail stockBorrowingDetail = new StockBorrowingDetail();
            stockBorrowingDetail.setId(1L);
            stockBorrowingDetail.setProductId(1L);
            stockBorrowingDetail.setQuantity(1);
            stockBorrowingDetail.setPrice(Float.parseFloat("2"));
            stockBorrowingDetail.setQuantity(1);
            List<StockBorrowingDetail> sbds = new ArrayList<>();
            sbds.add(stockBorrowingDetail);
            when(stockBorrowingDetailRepository.findByBorrowingId(stockBorrowing.getId())).thenReturn(sbds);

            List<StockTotal> stockTotals = new ArrayList<>();
            StockTotal stockTotal = new StockTotal();
            stockTotal.setProductId(1L);
            stockTotal.setId(1L);
            stockTotal.setQuantity(20);
            stockTotals.add(stockTotal);

            when(stockTotalRepository.getStockTotal(shopId, stockBorrowing.getWareHouseTypeId(),
                    sbds.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()))).thenReturn(stockTotals);

            Mockito.when(stockBorrowingRepository.save(Mockito.isA(StockBorrowing.class))).thenReturn(stockBorrowing);
            Mockito.when(stockBorrowingTransRepository.save(Mockito.isA(StockBorrowingTrans.class))).thenReturn(stockAdjustmentTrans);
            List<Long> res = serviceImp.createReceipt( request, userId, shopId);
            assertNotNull(res);
        }

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
    public void updateReceiptExportType0Test() throws Exception {
        String uri = V1 + root + "/update/{Id}";
        ReceiptExportUpdateRequest request = new ReceiptExportUpdateRequest();
        request.setId(1L);
        request.setType(0);
        List<ReceiptCreateDetailRequest> lstReceips = new ArrayList<>();
        ReceiptCreateDetailRequest receiptCreateDetailRequest = new ReceiptCreateDetailRequest();
        receiptCreateDetailRequest.setId(1L);
        receiptCreateDetailRequest.setQuantity(1);
        lstReceips.add(receiptCreateDetailRequest);

        request.setListProductRemain(lstReceips);
        request.setNote("Ghi chu 2");

        PoTrans poTrans = new PoTrans();
        poTrans.setWareHouseTypeId(2L);
        poTrans.setId(1L);
        poTrans.setTransCode("00001");
        poTrans.setTransDate(LocalDateTime.now());
        poTrans.setFromTransId(1L);

        when(repository.findByIdAndShopIdAndTypeAndStatus(1L, 1L, 2,1 ))
                .thenReturn(java.util.Optional.of(poTrans));

        List<PoTransDetail> poTransDetails = new ArrayList<>();
        PoTransDetail poTransDetail = new PoTransDetail();
        poTransDetail.setProductId(1L);
        poTransDetail.setId(1L);
        poTransDetail.setQuantity(5);
        poTransDetail.setPrice(5.0);
        poTransDetail.setReturnAmount(1);
        poTransDetail.setPriceNotVat(3.0);
        poTransDetails.add(poTransDetail);
        when(poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId())).thenReturn(poTransDetails);

        List<StockTotal> stockTotals = new ArrayList<>();
        StockTotal stockTotal = new StockTotal();
        stockTotal.setProductId(1L);
        stockTotal.setId(1L);
        stockTotal.setQuantity(20);
        stockTotals.add(stockTotal);

        when(stockTotalRepository.getStockTotal(1L, poTrans.getWareHouseTypeId(), poTransDetails.stream().map(item ->
                item.getProductId()).distinct().filter(Objects::nonNull).collect(Collectors.toList()))).thenReturn(stockTotals);

        List<Long> res = serviceImp.updateReceiptExport(request, 1L, 1L);
        assertNotNull(res);

        String inputJson = super.mapToJson(request);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.patch(uri, 1L)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void updateReceiptExportType1Test() throws Exception {
        String uri = V1 + root + "/update/{Id}";
        ReceiptExportUpdateRequest request = new ReceiptExportUpdateRequest();
        request.setId(1L);
        request.setType(1);
        List<ReceiptCreateDetailRequest> lstReceips = new ArrayList<>();
        ReceiptCreateDetailRequest receiptCreateDetailRequest = new ReceiptCreateDetailRequest();
        receiptCreateDetailRequest.setId(1L);
        receiptCreateDetailRequest.setQuantity(1);
        lstReceips.add(receiptCreateDetailRequest);

        request.setListProductRemain(lstReceips);
        request.setNote("Ghi chu 2");

        StockAdjustmentTrans adjustmentTrans = new StockAdjustmentTrans();
        adjustmentTrans.setId(1L);
        adjustmentTrans.setTransDate(LocalDateTime.now());

        when(stockAdjustmentTransRepository.getByIdAndShopId(1L, 1L, 2))
                .thenReturn(adjustmentTrans);

        List<Long> res = serviceImp.updateReceiptExport(request, 1L, 1L);
        assertNotNull(res);

        String inputJson = super.mapToJson(request);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.patch(uri, 1L)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void updateReceiptExportType2Test() throws Exception {
        String uri = V1 + root + "/update/{Id}";
        ReceiptExportUpdateRequest request = new ReceiptExportUpdateRequest();
        request.setId(1L);
        request.setType(2);
        List<ReceiptCreateDetailRequest> lstReceips = new ArrayList<>();
        ReceiptCreateDetailRequest receiptCreateDetailRequest = new ReceiptCreateDetailRequest();
        receiptCreateDetailRequest.setId(1L);
        receiptCreateDetailRequest.setQuantity(1);
        lstReceips.add(receiptCreateDetailRequest);

        request.setListProductRemain(lstReceips);
        request.setNote("Ghi chu 2");

        StockBorrowingTrans adjustmentTrans = new StockBorrowingTrans();
        adjustmentTrans.setId(1L);
        adjustmentTrans.setTransDate(LocalDateTime.now());

        when(stockBorrowingTransRepository.getByIdAndShopId(1L, 1L, 2))
                .thenReturn(adjustmentTrans);

        Mockito.when(stockBorrowingTransRepository.save(Mockito.isA(StockBorrowingTrans.class))).thenReturn(adjustmentTrans);
        List<Long> res = serviceImp.updateReceiptExport(request, 1L, 1L);
        assertNotNull(res);

        String inputJson = super.mapToJson(request);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.patch(uri, 1L)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void removeReceiptExportType0() throws Exception {
        String uri = V1 + root + "/remove/1?type=2";
        Integer type = 0; Long id = 1L; Long shopId = 1L;

        PoTrans poTrans = new PoTrans();
        poTrans.setWareHouseTypeId(2L);
        poTrans.setId(1L);
        poTrans.setTransCode("00001");
        poTrans.setTransDate(LocalDateTime.now());
        poTrans.setFromTransId(1L);
        poTrans.setStatus(1);
        when(repository.getById(id)).thenReturn(poTrans);

        List<PoTransDetail> poTransDetails = new ArrayList<>();
        PoTransDetail poTransDetail = new PoTransDetail();
        poTransDetail.setProductId(1L);
        poTransDetail.setId(1L);
        poTransDetail.setQuantity(5);
        poTransDetail.setPrice(5.0);
        poTransDetail.setReturnAmount(1);
        poTransDetails.add(poTransDetail);
        when(poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId())).thenReturn(poTransDetails);

        Mockito.when(repository.save(Mockito.isA(PoTrans.class))).thenReturn(poTrans);
        List<List<String>> res = serviceImp.removeReceiptExport(type, id, shopId);
        assertNotNull(res);

        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.put(uri)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void removeReceiptExportType1Test() throws Exception {
        String uri = V1 + root + "/remove/1?type=2";
        Integer type = 1; Long id = 1L; Long shopId = 1L;

        StockAdjustmentTrans stockAdjustmentTrans = new StockAdjustmentTrans();
        stockAdjustmentTrans.setId(1L);
        stockAdjustmentTrans.setTransDate(LocalDateTime.now());
        stockAdjustmentTrans.setStatus(1);
        stockAdjustmentTrans.setRedInvoiceNo("123");
        stockAdjustmentTrans.setAdjustmentId(1L);
        when(stockAdjustmentTransRepository.findById(id)).thenReturn(Optional.of(stockAdjustmentTrans));

        List<StockAdjustmentTransDetail> poTransDetails = new ArrayList<>();
        StockAdjustmentTransDetail poTransDetail = new StockAdjustmentTransDetail();
        poTransDetail.setProductId(1L);
        poTransDetail.setId(1L);
        poTransDetail.setQuantity(5);
        poTransDetails.add(poTransDetail);
        when(stockAdjustmentTransDetailRepository.getStockAdjustmentTransDetailsByTransId(stockAdjustmentTrans.getId())).thenReturn(poTransDetails);

        List<SaleOrder> orders = new ArrayList<>();
        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setId(1L);
        orders.add(saleOrder);
        when(saleOrderRepository.findSaleOrderByOrderCode(Arrays.asList(stockAdjustmentTrans.getRedInvoiceNo()))).thenReturn(orders);

        List<SaleOrderDetail> saleOrderDetails = new ArrayList<>();
        when(saleOrderDetailRepository.findSaleOrderDetail(saleOrder.getId(), null)).thenReturn(saleOrderDetails);

        when(stockAdjustmentRepository.getById(stockAdjustmentTrans.getAdjustmentId())).thenReturn(new StockAdjustment());

        StockAdjustment stockAdjustment = new StockAdjustment();
        stockAdjustment.setId(2l);
        Mockito.when(stockAdjustmentRepository.save(Mockito.isA(StockAdjustment.class))).thenReturn(stockAdjustment);
        Mockito.when(stockAdjustmentTransRepository.save(Mockito.isA(StockAdjustmentTrans.class))).thenReturn(stockAdjustmentTrans);
        List<List<String>> res = serviceImp.removeReceiptExport(type, id, shopId);
        assertNotNull(res);

        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.put(uri)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void removeReceiptExportType2Test() throws Exception {
        String uri = V1 + root + "/remove/1?type=2";
        Integer type = 2; Long id = 1L; Long shopId = 1L;

        StockBorrowingTrans stockBorrowingTrans = new StockBorrowingTrans();
        stockBorrowingTrans.setId(1L);
        stockBorrowingTrans.setTransDate(LocalDateTime.now());
        stockBorrowingTrans.setStatus(1);
        stockBorrowingTrans.setStockBorrowingId(1L);
        when(stockBorrowingTransRepository.getById(id)).thenReturn(stockBorrowingTrans);

        List<StockBorrowingTransDetail> poTransDetails = new ArrayList<>();
        StockBorrowingTransDetail poTransDetail = new StockBorrowingTransDetail();
        poTransDetail.setId(1L);
        poTransDetails.add(poTransDetail);
        when(stockBorrowingTransDetailRepository.getStockBorrowingTransDetailByTransId(stockBorrowingTrans.getId())).thenReturn(poTransDetails);

        StockBorrowing stockBorrowing = new StockBorrowing();
        stockBorrowing.setId(1L);
        when(stockBorrowingRepository.findById(stockBorrowingTrans.getStockBorrowingId())).thenReturn(Optional.of(stockBorrowing));


        Mockito.when(stockBorrowingTransRepository.save(Mockito.isA(StockBorrowingTrans.class))).thenReturn(stockBorrowingTrans);
        List<List<String>> res = serviceImp.removeReceiptExport(type, id, shopId);
        assertNotNull(res);

        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.put(uri)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getListPoTransTest() throws Exception {
        String uri = V1 + root + "/po-trans";
        int size = 2;
        int page = 5;
        Pageable pageable = PageRequest.of(page, size);
        List<PoTrans> list = Arrays.asList(new PoTrans(), new PoTrans());
        Page<PoTrans> pageResponse =  new PageImpl<>(list, pageable , list.size());
        when(shopClient.getImportSaleReturn(1L)).thenReturn("2");
        when(repository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(pageResponse);

        Page<PoTransDTO> result = serviceImp.getListPoTrans("123", "456", "145",
                "55", null, null, 1L, pageable);
        assertNotNull(result);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getListStockBorrowing() throws Exception {
        String uri = V1 + root + "/borrowing";
        List<StockBorrowingDTO> stockBorrowings = new ArrayList<>();
        StockBorrowingDTO stockBorrowingDTO =  new StockBorrowingDTO();
        stockBorrowingDTO.setBorrowDate(LocalDateTime.now());
        stockBorrowingDTO.setPoBorrowCode("123");
        stockBorrowings.add(stockBorrowingDTO);
        LocalDateTime date1 = LocalDateTime.now().toLocalDate().atTime(LocalTime.MIN);
        LocalDateTime date2 = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);
        when(stockBorrowingRepository.getStockBorrowingExport(1L, date1, date2)).thenReturn(stockBorrowings);

        List<StockBorrowingDTO> result = serviceImp.getListStockBorrowing(1L, PageRequest.of(0,5));
        assertNotNull(result);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

}
