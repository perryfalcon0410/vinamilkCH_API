package vn.viettel.sale.service.impl;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
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
import vn.viettel.sale.service.dto.StockAdjustmentDTO;
import vn.viettel.sale.service.dto.StockBorrowingDTO;
import vn.viettel.sale.service.feign.ApparamClient;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.ReceiptSpecification;
import vn.viettel.sale.util.CreateCodeUtils;

@Service
public class ReceiptExportServiceImpl extends BaseServiceImpl<PoTrans, PoTransRepository> implements ReceiptExportService {
    @Autowired
    ShopClient shopClient;
    @Autowired
    PoConfirmRepository poConfirmRepository;
    @Autowired
    PoDetailRepository poDetailRepository;
    @Autowired
    UserClient userClient;
    @Autowired
    PoTransDetailRepository poTransDetailRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    StockTotalRepository stockTotalRepository;
    @Autowired
    StockAdjustmentTransRepository stockAdjustmentTransRepository;
    @Autowired
    StockAdjustmentRepository stockAdjustmentRepository;
    @Autowired
    StockAdjustmentDetailRepository stockAdjustmentDetailRepository;
    @Autowired
    StockAdjustmentTransDetailRepository stockAdjustmentTransDetailRepository;
    @Autowired
    StockBorrowingTransRepository stockBorrowingTransRepository;
    @Autowired
    StockBorrowingTransDetailRepository stockBorrowingTransDetailRepository;
    @Autowired
    StockBorrowingRepository stockBorrowingRepository;
    @Autowired
    StockBorrowingDetailRepository stockBorrowingDetailRepository;
    @Autowired
    SaleOrderRepository saleOrderRepository;
    @Autowired
    SaleOrderDetailRepository saleOrderDetailRepository;
    @Autowired
    CustomerTypeClient customerTypeClient;
    @Autowired
    WareHouseTypeRepository wareHouseTypeRepository;
    @Autowired
    ApparamClient apparamClient;
    @Autowired
    CustomerClient customerClient;
    @Autowired
    ProductPriceRepository productPriceRepository;
    @Autowired
    StockTotalService stockTotalService;

    @Autowired
    SaleService saleService;

    @Override
    public CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> find(String transCode, String redInvoiceNo, LocalDateTime fromDate,
                                                                         LocalDateTime toDate, Integer type, Long shopId, Pageable pageable) {
        if (transCode != null) transCode = transCode.toUpperCase();
        if (redInvoiceNo != null) redInvoiceNo = redInvoiceNo.toUpperCase();
        if (fromDate == null) fromDate = LocalDateTime.of(2015, 1, 1, 0, 0);
        if (toDate == null) toDate = LocalDateTime.now();
        fromDate = DateUtils.convertFromDate(fromDate);
        toDate = DateUtils.convertToDate(toDate);

        if (type == null) {
            Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

            List<Integer> sorts = new ArrayList<>();
            if(pageable.getSort() != null) {
                for (Sort.Order order : pageable.getSort()) {
                    if(order.getProperty().equals("transDate")){
                        if(order.getDirection().toString().equalsIgnoreCase("DESC"))
                            sorts.add(1);
                        else sorts.add(2);
                    }else if(order.getProperty().equals("transCode")){
                        if(order.getDirection().toString().equalsIgnoreCase("DESC"))
                            sorts.add(3);
                        else sorts.add(4);
                    }else if(order.getProperty().equals("redInvoiceNo")){
                        if(order.getDirection().toString().equalsIgnoreCase("DESC"))
                            sorts.add(5);
                        else sorts.add(6);
                    }else if(order.getProperty().equals("internalNumber")){
                        if(order.getDirection().toString().equalsIgnoreCase("DESC"))
                            sorts.add(7);
                        else sorts.add(8);
                    }else if(order.getProperty().equals("totalQuantity")){
                        if(order.getDirection().toString().equalsIgnoreCase("DESC"))
                            sorts.add(9);
                        else sorts.add(10);
                    }else if(order.getProperty().equals("totalAmount")){
                        if(order.getDirection().toString().equalsIgnoreCase("DESC"))
                            sorts.add(11);
                        else sorts.add(12);
                    }else if(order.getProperty().equals("receiptType")){
                        if(order.getDirection().toString().equalsIgnoreCase("DESC"))
                            sorts.add(13);
                        else sorts.add(14);
                    }else if(order.getProperty().equals("note")){
                        if(order.getDirection().toString().equalsIgnoreCase("DESC"))
                            sorts.add(15);
                        else sorts.add(16);
                    }
                }
            }

            Page<ReceiptImportDTO> pageResponse = repository.getReceipt(shopId, 2, transCode, redInvoiceNo, fromDate, toDate, sorts, page);
            TotalResponse totalResponse = repository.getTotalResponsePo(shopId, 2, transCode, redInvoiceNo, fromDate, toDate);
            TotalResponse totalResponse2 = repository.getTotalResponseAdjustment(shopId, 2, transCode, redInvoiceNo, fromDate, toDate);
            TotalResponse totalResponse3 = repository.getTotalResponseBorrowing(shopId, 2, transCode, redInvoiceNo, fromDate, toDate);
            if (totalResponse.getTotalQuantity() == null) totalResponse.setTotalQuantity(0);
            if (totalResponse.getTotalPrice() == null) totalResponse.setTotalPrice(0.0);
            if (totalResponse2.getTotalQuantity() == null) totalResponse2.setTotalQuantity(0);
            if (totalResponse2.getTotalPrice() == null) totalResponse2.setTotalPrice(0.0);
            if (totalResponse3.getTotalQuantity() == null) totalResponse3.setTotalQuantity(0);
            if (totalResponse3.getTotalPrice() == null) totalResponse3.setTotalPrice(0.0);
            totalResponse.setTotalQuantity(totalResponse.getTotalQuantity() + totalResponse2.getTotalQuantity() + totalResponse3.getTotalQuantity());
            totalResponse.setTotalPrice(totalResponse.getTotalPrice() + totalResponse2.getTotalPrice() + totalResponse3.getTotalPrice());

            return new CoverResponse(pageResponse, totalResponse);
        } else if (type == 0) {
            Page<ReceiptImportListDTO> pageResponse = repository.getReceiptPo(shopId, 2, transCode, redInvoiceNo, fromDate, toDate, pageable);
            TotalResponse totalResponse = repository.getTotalResponsePo(shopId, 2, transCode, redInvoiceNo, fromDate, toDate);
            if(totalResponse == null) totalResponse = new TotalResponse();
            if (totalResponse.getTotalQuantity() == null) totalResponse.setTotalQuantity(0);
            if (totalResponse.getTotalPrice() == null) totalResponse.setTotalPrice(0.0);
            return new CoverResponse(pageResponse, totalResponse);
        } else if (type == 1) {
            Page<ReceiptImportListDTO> pageResponse = repository.getReceiptAdjustment(shopId, 2, transCode, redInvoiceNo, fromDate, toDate, pageable);
            TotalResponse totalResponse = repository.getTotalResponseAdjustment(shopId, 2, transCode, redInvoiceNo, fromDate, toDate);
            if(totalResponse == null) totalResponse = new TotalResponse();
            if (totalResponse.getTotalQuantity() == null) totalResponse.setTotalQuantity(0);
            if (totalResponse.getTotalPrice() == null) totalResponse.setTotalPrice(0.0);
            return new CoverResponse(pageResponse, totalResponse);
        } else if (type == 2) {
            Page<ReceiptImportListDTO> pageResponse = repository.getReceiptBorrowing(shopId, 2, transCode, redInvoiceNo, fromDate, toDate, pageable);
            TotalResponse totalResponse = repository.getTotalResponseBorrowing(shopId, 2, transCode, redInvoiceNo, fromDate, toDate);
            if(totalResponse == null) totalResponse = new TotalResponse();
            if (totalResponse.getTotalQuantity() == null) totalResponse.setTotalQuantity(0);
            if (totalResponse.getTotalPrice() == null) totalResponse.setTotalPrice(0.0);
            return new CoverResponse(pageResponse, totalResponse);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> createReceipt(ReceiptExportCreateRequest request, Long userId,Long shopId) {
        switch (request.getImportType()){
            case 0:
                return createPoTransExport(request, userId, shopId);
            case 1:
                return createAdjustmentTrans(request, userId, shopId);
            case 2:
                return createBorrowingTrans(request, userId, shopId);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> updateReceiptExport(ReceiptExportUpdateRequest request, Long id,Long shopId) {
        switch (request.getType()){
            case 0:
            	return updatePoTransExport(request,id,shopId);
            case 1:
            	return updateAdjustmentTransExport(request,id,shopId);
            case 2:
            	return updateBorrowingTransExport(request,id,shopId);
        }
        return null;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<List<String>> removeReceiptExport(Integer type, Long id,Long shopId) {

        switch (type) {
            case 0:
                return removePoTransExport(id, shopId);
            case 1:
                return removeStockAdjustmentTransExport(id, shopId);
            case 2:
                return removeStockBorrowingTransExport(id,shopId);
        }
        return null;
    }

    @Override
    public Page<PoTransDTO> getListPoTrans(String transCode, String redInvoiceNo, String internalNumber, String poCoNo, LocalDateTime fromDate, LocalDateTime toDate, Long shopId, Pageable pageable) {
        String returnDay = shopClient.getImportSaleReturn(shopId);
        if (returnDay == null) throw new ValidateException(ResponseMessage.DATE_RETURN_MUST_NOT_BE_NULL);
        LocalDateTime dateTime = LocalDateTime.now().minusDays(Integer.valueOf(returnDay));
        Page<PoTrans> poTrans = repository.findAll(Specification.where(ReceiptSpecification.hasTransCode(transCode))
                        .and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo)).and(ReceiptSpecification.hasInternalNumber(internalNumber))
                        .and(ReceiptSpecification.hasPoCoNo(poCoNo)).and(ReceiptSpecification.hasFromDateToDateRedInvoice(fromDate, toDate))
                        .and(ReceiptSpecification.hasStatus()).and(ReceiptSpecification.hasTypeImport())
                        .and(ReceiptSpecification.hasGreaterDay(dateTime)).and(ReceiptSpecification.hasNotReturn()).and(ReceiptSpecification.hasShopId(shopId))
                        ,pageable);
        //List<WareHouseType> wareHouseTypeName = wareHouseTypeRepository.findAllById(poTrans.stream().map(e->e.getWareHouseTypeId()).collect(Collectors.toList()));
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return poTrans.map(e -> modelMapper.map(e, PoTransDTO.class));
    }
    @Override
    public List<StockAdjustmentDTO> getListStockAdjustment(Long shopId, Pageable pageable) {
        List<StockAdjustmentDTO> stockAdjustments = stockAdjustmentRepository.getStockAdjustmentExport(shopId);
        Collections.sort(stockAdjustments, Comparator.comparing(StockAdjustmentDTO::getAdjustmentDate, Comparator.reverseOrder()).thenComparing(StockAdjustmentDTO::getAdjustmentCode, Comparator.reverseOrder()));
        return stockAdjustments;
    }

    @Override
    public List<StockBorrowingDTO> getListStockBorrowing(Long shopId, Pageable pageable) {
        LocalDateTime date1 = LocalDateTime.now().toLocalDate().atTime(LocalTime.MIN);
        LocalDateTime date2 = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);
        List<StockBorrowingDTO> stockBorrowings = stockBorrowingRepository.getStockBorrowingExport(shopId, date1, date2);
        Collections.sort(stockBorrowings, Comparator.comparing(StockBorrowingDTO::getBorrowDate, Comparator.reverseOrder()).thenComparing(StockBorrowingDTO::getPoBorrowCode, Comparator.reverseOrder()));
        return stockBorrowings;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Transactional(rollbackFor = Exception.class)
    public List<Long> createPoTransExport(ReceiptExportCreateRequest request, Long userId, Long shopId) {
        LocalDateTime transDate = LocalDateTime.now();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PoTrans poRecord = modelMapper.map(request, PoTrans.class);
        PoTrans poTrans = repository.findByIdAndShopIdAndTypeAndStatus(request.getReceiptImportId(), shopId, 1,1 )
                .orElseThrow(() -> new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED));

        poRecord.setId(null);
        poRecord.setTransDate(transDate);
        poRecord.setWareHouseTypeId(poTrans.getWareHouseTypeId());
        poRecord.setTransCode(createPoTransExportCode(shopId));
        poRecord.setShopId(shopId);
        poRecord.setOrderDate(poTrans.getOrderDate());
        poRecord.setRedInvoiceNo(poTrans.getRedInvoiceNo());
        poRecord.setPocoNumber(poTrans.getPocoNumber());
        poRecord.setInternalNumber(poTrans.getInternalNumber());
        poRecord.setFromTransId(poTrans.getId());
        poRecord.setPoNumber(poTrans.getPoNumber());
        poRecord.setIsDebit(false);
        Integer total_quantity = 0;
        Double total_amount = 0D;
        poRecord.setType(2);
        poRecord.setStatus(1);
        repository.save(poRecord);
        Set<Long> countNumSKU = new HashSet<>();
        List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
        List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, poTrans.getWareHouseTypeId(), poTransDetails.stream().map(item ->
                item.getProductId()).distinct().filter(Objects::nonNull).collect(Collectors.toList()));
        List<PoTransDetail> savePoTransDetails = new ArrayList<>();
        HashMap<Long, Integer> idAndValues = new HashMap<>();

        for (int i = 0; i < poTransDetails.size(); i++) {
            if (request.getIsRemainAll() == true) {
                for (int j =0;j<request.getLitQuantityRemain().size();j++){
                    if(request.getLitQuantityRemain().get(j).getId().equals(poTransDetails.get(i).getId())){
                        countNumSKU.add(poTransDetails.get(i).getProductId());
                        PoTransDetail poTransDetail = new PoTransDetail();
                        poTransDetail.setTransId(poRecord.getId());
                        poTransDetail.setTransDate(transDate);
                        poTransDetail.setProductId(poTransDetails.get(i).getProductId());
                        if(poTransDetails.get(i).getReturnAmount()==null) poTransDetails.get(i).setReturnAmount(0);
                        poTransDetail.setQuantity(poTransDetails.get(i).getQuantity()-poTransDetails.get(i).getReturnAmount());
                        poTransDetail.setPrice(poTransDetails.get(i).getPrice());
                        poTransDetail.setShopId(shopId);
                        poTransDetail.setPriceNotVat(poTransDetails.get(i).getPriceNotVat());
                        poTransDetail.setAmountNotVat(poTransDetails.get(i).getAmountNotVat());
                        poTransDetail.setReturnAmount(poTransDetail.getQuantity());
                        poTransDetail.setAmount(poTransDetails.get(i).getPrice() * poTransDetail.getQuantity());
                        total_quantity +=poTransDetail.getQuantity();
                        total_amount += poTransDetail.getAmount();
                        savePoTransDetails.add(poTransDetail);
                        poTransDetails.get(i).setReturnAmount(poTransDetails.get(i).getQuantity());
                        savePoTransDetails.add(poTransDetails.get(i));
                        if(poTransDetails.get(i).getReturnAmount()> poTransDetails.get(i).getQuantity())
                            throw new ValidateException(ResponseMessage.RETURN_AMOUNT_MUST_BE_LESS_THAN_OR_EQUAL_TO_THE_QUANTITY_ENTERED);
                        StockTotal stockTotal = null;

                        for(StockTotal st : stockTotals){
                            if(st.getProductId().equals(poTransDetails.get(i).getProductId())){
                                if (st.getQuantity() == null)  st.setQuantity(0);
                                stockTotal = st;
                                int value = ((-1) * poTransDetail.getReturnAmount());
                                if(idAndValues.containsKey(st.getId())){
                                    value += idAndValues.get(st.getId());
                                }
                                idAndValues.put(st.getId(), value);
                                if((st.getQuantity() - poTransDetail.getReturnAmount()) < 0) throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SSS,
                                        request.getLitQuantityRemain().get(j).getProductCode()+"-"+request.getLitQuantityRemain().get(j).getProductName());
                                break;
                            }
                        }
                        if (stockTotal == null)
                            throw new ValidateException(ResponseMessage.PRODUCT_STOCK_TOTAL_NOT_FOUND, request.getLitQuantityRemain().get(j).getProductCode()+"-"+request.getLitQuantityRemain().get(j).getProductName());
                    }
                }
            } else {
                for (int j = 0; j < request.getLitQuantityRemain().size(); j++) {
                    if (poTransDetails.get(i).getId().equals(request.getLitQuantityRemain().get(j).getId())) {
                        countNumSKU.add(request.getLitQuantityRemain().get(j).getProductId());
                        if (poTransDetails.get(i).getQuantity() == null) poTransDetails.get(i).setQuantity(0);
                        if (poTransDetails.get(i).getAmount() == null) poTransDetails.get(i).setAmount(0D);
                        PoTransDetail poTransDetail = new PoTransDetail();
                        poTransDetail.setTransId(poRecord.getId());
                        poTransDetail.setTransDate(transDate);
                        poTransDetail.setProductId(poTransDetails.get(i).getProductId());
                        poTransDetail.setQuantity(request.getLitQuantityRemain().get(j).getQuantity());
                        poTransDetail.setPrice(poTransDetails.get(i).getPrice());
                        poTransDetail.setPriceNotVat(poTransDetails.get(i).getPriceNotVat());
                        poTransDetail.setAmountNotVat(poTransDetails.get(i).getAmountNotVat());
                        poTransDetail.setShopId(shopId);
                        poTransDetail.setReturnAmount(request.getLitQuantityRemain().get(j).getQuantity());
                        poTransDetail.setAmount(poTransDetail.getQuantity() * poTransDetails.get(i).getPrice());
                        savePoTransDetails.add(poTransDetail);
                        total_quantity += poTransDetail.getQuantity();
                        total_amount += poTransDetail.getAmount();
                        poTransDetails.get(i).setReturnAmount(poTransDetails.get(i).getReturnAmount() + poTransDetail.getQuantity());
                        savePoTransDetails.add(poTransDetails.get(i));
                        if (poTransDetails.get(i).getReturnAmount() > poTransDetails.get(i).getQuantity())
                            throw new ValidateException(ResponseMessage.RETURN_AMOUNT_MUST_BE_LESS_THAN_OR_EQUAL_TO_THE_QUANTITY_ENTERED);
                        StockTotal stockTotal = null;
                        for (StockTotal st : stockTotals) {
                            if (st.getProductId().equals(poTransDetails.get(i).getProductId())) {
                                if (st.getQuantity() == null) st.setQuantity(0);
                                int value = ((-1) * poTransDetail.getReturnAmount());
                                if (idAndValues.containsKey(st.getId())) {
                                    value += idAndValues.get(st.getId());
                                }
                                idAndValues.put(st.getId(), value);
                                stockTotal = st;
                                if ((st.getQuantity() - poTransDetail.getReturnAmount()) < 0)
                                    throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SSS,
                                            request.getLitQuantityRemain().get(j).getProductCode() + "-" + request.getLitQuantityRemain().get(j).getProductName());
                                break;
                            }
                        }
                        if (stockTotal == null)
                            throw new ValidateException(ResponseMessage.PRODUCT_STOCK_TOTAL_NOT_FOUND, request.getLitQuantityRemain().get(j).getProductCode()+"-"+request.getLitQuantityRemain().get(j).getProductName());
                    }
                }
            }
        }
        for (PoTransDetail poTransDetail1 : savePoTransDetails) {
            poTransDetailRepository.save(poTransDetail1);
        }
        stockTotalService.updateWithLock(idAndValues);
        if(total_quantity==0) throw new ValidateException(ResponseMessage.RETURN_AMOUNT_MUST_BE_LESS_THAN_OR_EQUAL_TO_THE_QUANTITY_ENTERED);
        poRecord.setTotalQuantity(total_quantity);
        poRecord.setTotalAmount(total_amount);
        poRecord.setNumSku(countNumSKU.size());
        poRecord = repository.save(poRecord);
        
        return Arrays.asList(poRecord.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Long> createAdjustmentTrans(ReceiptExportCreateRequest request, Long userId, Long shopId) {
        CustomerDTO cus = customerClient.getCusDefault(shopId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        StockAdjustmentTrans poAdjustTrans = modelMapper.map(request, StockAdjustmentTrans.class);

        StockAdjustment stockAdjustment = stockAdjustmentRepository.getById(request.getReceiptImportId(), shopId, 2);
        if(stockAdjustment == null) throw new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);

        if (stockAdjustment.getStatus() == 3) throw new ValidateException(ResponseMessage.RECEIPT_HAS_BEEN_EXPORTED);
        Response<ApParamDTO> reason = apparamClient.getReasonV1(stockAdjustment.getReasonId());
        if (reason.getData() == null || reason.getData().getId() == null)
            throw new ValidateException(ResponseMessage.REASON_NOT_FOUND);
        ShopDTO shop = shopClient.getByIdV1(shopId).getData();
        if (shop == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);

        LocalDateTime ldt = LocalDateTime.now();
        poAdjustTrans.setId(null);
        poAdjustTrans.setTransDate(ldt);
        poAdjustTrans.setShopId(shopId);
        poAdjustTrans.setTransCode(stockAdjustment.getAdjustmentCode());
        poAdjustTrans.setAdjustmentDate(stockAdjustment.getAdjustmentDate());
        poAdjustTrans.setWareHouseTypeId(stockAdjustment.getWareHouseTypeId());
        poAdjustTrans.setOrderDate(stockAdjustment.getAdjustmentDate());
        poAdjustTrans.setInternalNumber(createInternalExportCode(shopId));
        poAdjustTrans.setAdjustmentId(stockAdjustment.getId());
        poAdjustTrans.setReasonId(stockAdjustment.getReasonId());
        poAdjustTrans.setType(2);
        poAdjustTrans.setStatus(1);

        SaleOrder order = new SaleOrder();
        order.setType(4);
        order.setOrderDate(ldt);
        order.setShopId(shopId);
        order.setSalemanId(userId);
        if(cus != null) {
            order.setCustomerId(cus.getId());
            order.setTotalCustomerPurchase(cus.getTotalBill());
        }
        order.setWareHouseTypeId(stockAdjustment.getWareHouseTypeId());
        order.setBalance(0D);
        order.setNote(reason.getData().getApParamName());
        order.setMemberCardAmount(0D);
        order.setTotalVoucher(0D);
        order.setPaymentType(1);
        order.setDeliveryType(0);
        order.setAutoPromotionNotVat(0D);
        order.setAutoPromotion(0D);
        order.setZmPromotion(0D);
        order.setTotalPromotionNotVat(0D);
        order.setAutoPromotionVat(0D);
        order.setCustomerPurchase(0D);
        order.setDiscountCodeAmount(0D);
        order.setUsedRedInvoice(false);

      /*  order.setOrderNumber(saleService.createOrderNumber(shop));
        saleOrderRepository.save(order);*/
        try{
            saleService.safeSave(order, shop);
        }catch (Exception ex) {
            throw new ValidateException(ResponseMessage.SAVE_FAIL);
        }

        poAdjustTrans.setRedInvoiceNo(order.getOrderNumber());
        stockAdjustmentTransRepository.save(poAdjustTrans);

        List<StockAdjustmentDetail> sads = stockAdjustmentDetailRepository.getStockAdjustmentDetailByAdjustmentId(stockAdjustment.getId());
        Integer totalQuantity = 0;
        Double totalAmount = 0D;
        List<Price> prices = productPriceRepository.findProductPriceWithType(sads.stream().map(item -> item.getProductId()).distinct()
                .collect(Collectors.toList()), stockAdjustment.getWareHouseTypeId(), DateUtils.convertToDate(ldt));
        List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, stockAdjustment.getWareHouseTypeId(),
                sads.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()));
        for (StockAdjustmentDetail sad : sads) {
            stockTotalService.validateStockTotal(stockTotals, sad.getProductId(), -sad.getQuantity());
        }

        for (StockAdjustmentDetail sad : sads) {
            if (sad.getQuantity() == null) sad.setQuantity(0);
            if (sad.getPrice() == null) sad.setPrice(0D);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentTransDetail satd = modelMapper.map(sad, StockAdjustmentTransDetail.class);
            satd.setId(null);
            satd.setTransId(poAdjustTrans.getId());
            satd.setShopId(shopId);
            satd.setTransDate(ldt);
            totalQuantity += sad.getQuantity();
            totalAmount += sad.getPrice() * sad.getQuantity();
            stockTotalService.updateWithLock(shopId, stockAdjustment.getWareHouseTypeId(), sad.getProductId(), (-1) * sad.getQuantity());
            stockAdjustmentTransDetailRepository.save(satd);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            SaleOrderDetail saleOrderDetail = modelMapper.map(sad, SaleOrderDetail.class);
            saleOrderDetail.setId(null);
            saleOrderDetail.setSaleOrderId(order.getId());
            saleOrderDetail.setAmount(sad.getPrice() * sad.getQuantity());
            saleOrderDetail.setTotal(sad.getPrice() * sad.getQuantity());
            saleOrderDetail.setIsFreeItem(false);
            saleOrderDetail.setAutoPromotion(0D);
            saleOrderDetail.setZmPromotion(0D);
            if (prices != null) {
                for (Price price : prices) {
                    if (price.getProductId().equals(sad.getProductId())) {
                        saleOrderDetail.setPriceNotVat(price.getPriceNotVat());
                        break;
                    }
                }
            }
            saleOrderDetail.setAutoPromotionNotVat(0D);
            saleOrderDetail.setAutoPromotionVat(0D);
            saleOrderDetail.setZmPromotionVat(0D);
            saleOrderDetail.setZmPromotionNotVat(0D);
            saleOrderDetail.setOrderDate(ldt);
            saleOrderDetailRepository.save(saleOrderDetail);
        }
        order.setAmount(totalAmount);
        order.setTotalPromotion(0D);
        order.setTotal(totalAmount);
        order.setTotalPaid(totalAmount);
        saleOrderRepository.save(order);
        poAdjustTrans.setTotalQuantity(totalQuantity);
        poAdjustTrans.setTotalAmount(totalAmount);
        poAdjustTrans.setNote(request.getNote() == null ? stockAdjustment.getDescription() : request.getNote());
        stockAdjustment.setStatus(3);
        stockAdjustmentRepository.save(stockAdjustment);
        stockAdjustmentTransRepository.save(poAdjustTrans);
        List<Long> ids = new ArrayList<Long>();
        ids.add(stockAdjustment.getId());
        ids.add(poAdjustTrans.getId());
        return ids;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Long> createBorrowingTrans(ReceiptExportCreateRequest request, Long userId, Long shopId) {
        LocalDateTime transDate = LocalDateTime.now();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        StockBorrowingTrans poBorrowTransRecord = modelMapper.map(request, StockBorrowingTrans.class);

        StockBorrowing stockBorrowing = stockBorrowingRepository.getExportById(request.getReceiptImportId(), shopId);
        if(stockBorrowing == null) throw new ValidateException(ResponseMessage.STOCK_BORROWING_NOT_EXITS);

        if (stockBorrowing.getStatusExport() == 2)
            throw new ValidateException(ResponseMessage.RECEIPT_HAS_BEEN_EXPORTED);
        poBorrowTransRecord.setId(null);
        poBorrowTransRecord.setTransDate(transDate);
        poBorrowTransRecord.setFromShopId(shopId);
        poBorrowTransRecord.setToShopId(stockBorrowing.getToShopId());
        poBorrowTransRecord.setShopId(shopId);
        poBorrowTransRecord.setWareHouseTypeId(stockBorrowing.getWareHouseTypeId());
        poBorrowTransRecord.setTransCode(createStockBorrowTransCode(shopId));
        poBorrowTransRecord.setRedInvoiceNo(createStockBorrowTransExportRedInvoice(shopId));
        poBorrowTransRecord.setBorrowDate(stockBorrowing.getBorrowDate());
        //Internal Number default null
        //Po no default null
        poBorrowTransRecord.setStockBorrowingId(stockBorrowing.getId());
        poBorrowTransRecord.setType(2);
        poBorrowTransRecord.setStatus(1);
        stockBorrowingTransRepository.save(poBorrowTransRecord);
        List<StockBorrowingDetail> sbds = stockBorrowingDetailRepository.findByBorrowingId(stockBorrowing.getId());
        Integer totalQuantity = 0;
        Double totalAmount = 0D;
        List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, stockBorrowing.getWareHouseTypeId(),
                sbds.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()));
        for (StockBorrowingDetail sad : sbds) {
            stockTotalService.validateStockTotal(stockTotals, sad.getProductId(), -sad.getQuantity());
        }

        for (StockBorrowingDetail sbd : sbds) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingTransDetail sbtd = modelMapper.map(sbd, StockBorrowingTransDetail.class);
            sbtd.setId(null);
            sbtd.setTransId(poBorrowTransRecord.getId());
            sbtd.setTransDate(transDate);
            totalAmount += sbd.getPrice() * sbd.getQuantity();
            totalQuantity += sbd.getQuantity();
            stockTotalService.updateWithLock(shopId, stockBorrowing.getWareHouseTypeId(), sbd.getProductId(), (-1) * sbd.getQuantity());
            stockBorrowingTransDetailRepository.save(sbtd);
        }
        poBorrowTransRecord.setTotalQuantity(totalQuantity);
        poBorrowTransRecord.setTotalAmount(totalAmount);
        poBorrowTransRecord.setNote(request.getNote() == null ? stockBorrowing.getNote() : request.getNote());
        stockBorrowing.setStatusExport(2);
        stockBorrowing = stockBorrowingRepository.save(stockBorrowing);
        poBorrowTransRecord = stockBorrowingTransRepository.save(poBorrowTransRecord);

        List<Long> ids = new ArrayList<Long>();
        ids.add(stockBorrowing.getId());
        ids.add(poBorrowTransRecord.getId());
        return ids;
    }
    /** Chỉnh sửa phiếu trả PO **/
    @Transactional(rollbackFor = Exception.class)
    public List<Long> updatePoTransExport(ReceiptExportUpdateRequest request, Long id,Long shopId) {
        PoTrans poTrans = repository.findByIdAndShopIdAndTypeAndStatus(id, shopId, 2, 1 )
                .orElseThrow(() -> new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED));

        if (poTrans.getTransDate().isBefore(DateUtils.convertFromDate(LocalDateTime.now())))
            throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);
        else {
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
            if (poTrans.getFromTransId() == null) throw new ValidateException(ResponseMessage.RECORD_WRONG);
            List<PoTransDetail> poTransDetailImport = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getFromTransId());
            if (poTransDetailImport == null) throw new ValidateException(ResponseMessage.RECORD_WRONG);
            int total = 0;
            double totalAmount = 0D;
            List<PoTransDetail> savePoTransDetails = new ArrayList<>();
            List<StockTotal> stockTotals = new ArrayList<>();
            HashMap<Long, Integer> idAndValues = new HashMap<>();

            if (!request.getListProductRemain().isEmpty()) {
                stockTotals = stockTotalRepository.getStockTotal(shopId, poTrans.getWareHouseTypeId(), poTransDetails.stream().map(item ->
                        item.getProductId()).distinct().filter(Objects::nonNull).collect(Collectors.toList()));
                for (int i = 0; i < poTransDetails.size(); i++) {
                    PoTransDetail poTransDetail = poTransDetails.get(i);
                    for (int j = 0; j < request.getListProductRemain().size(); j++) {
                        if (poTransDetail.getId().equals(request.getListProductRemain().get(j).getId())) {
                            if(request.getListProductRemain().get(j).getQuantity() == null)
                                request.getListProductRemain().get(j).setQuantity(0);
                            //số lượng còn lại =  tổng số lượng đơn nhập - số lượng đã trả
                            //số lượng trả = số lương trả mới - số lượng trả cũ của đươ (600 +100) -600 -> trả thêm 100
                            int availableQty = poTransDetailImport.get(i).getQuantity() - (poTransDetailImport.get(i).getReturnAmount()!=null?poTransDetailImport.get(i).getReturnAmount():0);
                            int returnQty = request.getListProductRemain().get(j).getQuantity() - (poTransDetails.get(i).getQuantity()!=null?poTransDetails.get(i).getQuantity():0);
                            if(availableQty < returnQty) throw new ValidateException(ResponseMessage.RETURN_AMOUNT_MUST_BE_LESS_THAN_OR_EQUAL_TO_THE_QUANTITY_ENTERED);
                            poTransDetailImport.get(i).setReturnAmount(poTransDetailImport.get(i).getReturnAmount() + (request.getListProductRemain().get(j).getQuantity() - poTransDetail.getQuantity()));
                            StockTotal st = null;
                            if (stockTotals != null) {
                                for (StockTotal stockTotal : stockTotals) {
                                    if (stockTotal.getProductId().equals(poTransDetail.getProductId())) {
                                        int value = (-1) * (request.getListProductRemain().get(j).getQuantity() - poTransDetail.getQuantity());
                                        if (idAndValues.containsKey(stockTotal.getId())) {
                                            value += idAndValues.get(stockTotal.getId());
                                        }
                                        idAndValues.put(stockTotal.getId(), value);

                                        if ((stockTotal.getQuantity() + value) < 0)
                                            throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SSS,
                                                    request.getListProductRemain().get(j).getProductCode() + "-" + request.getListProductRemain().get(j).getProductName());
                                        st = stockTotal;
                                        break;
                                    }
                                }
                            }
                            if (st == null)
                                throw new ValidateException(ResponseMessage.PRODUCT_STOCK_TOTAL_NOT_FOUND, request.getListProductRemain().get(j).getProductCode() + "-" + request.getListProductRemain().get(j).getProductName());
                            poTransDetail.setQuantity(request.getListProductRemain().get(j).getQuantity());
                            total += poTransDetail.getQuantity();
                            totalAmount += (poTransDetail.getQuantity() * poTransDetail.getPriceNotVat());
                            savePoTransDetails.add(poTransDetail);
                            savePoTransDetails.add(poTransDetailImport.get(i));
                        }
                    }
                }
            }
            poTrans.setTotalQuantity(total);
            poTrans.setTotalAmount(totalAmount);
            poTrans.setNote(request.getNote());
            repository.save(poTrans);
            for (PoTransDetail poTransDetail : savePoTransDetails) {
                poTransDetailRepository.save(poTransDetail);
            }
            if (stockTotals != null) {
                stockTotalService.updateWithLock(idAndValues);
            }
            return Arrays.asList(poTrans.getId());
        }
    }
    /** Chỉnh sửa phiếu trả điều chỉnh **/

    private List<Long> updateAdjustmentTransExport(ReceiptExportUpdateRequest request, Long id,Long shopId) {
        StockAdjustmentTrans adjustmentTrans = stockAdjustmentTransRepository.getByIdAndShopId(id, shopId, 2);
        if(adjustmentTrans == null) throw new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);
        if (DateUtils.formatDate2StringDate(adjustmentTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            adjustmentTrans.setNote(request.getNote());
            stockAdjustmentTransRepository.save(adjustmentTrans);
            return Arrays.asList(adjustmentTrans.getId());
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);
    }

    /** Chỉnh sửa phiếu trả vay mượn **/
    private List<Long> updateBorrowingTransExport(ReceiptExportUpdateRequest request, Long id,Long shopId) {
        StockBorrowingTrans borrowingTrans = stockBorrowingTransRepository.getByIdAndShopId(id, shopId, 2);
        if(borrowingTrans == null) throw new ValidateException(ResponseMessage.STOCK_BORROWING_TRANS_IS_NOT_EXISTED);
        if (DateUtils.formatDate2StringDate(borrowingTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            borrowingTrans.setNote(request.getNote());
            borrowingTrans = stockBorrowingTransRepository.save(borrowingTrans);
            return Arrays.asList(borrowingTrans.getId());
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);
    }

    /** Xóa phiếu xuất trả PO **/
    @Transactional(rollbackFor = Exception.class)
    public List<List<String>> removePoTransExport(Long id,Long shopId) {
        PoTrans poTrans = repository.getById(id);
        if(poTrans == null || poTrans.getStatus()==-1) throw new ValidateException(ResponseMessage.RECEIPT_HAS_BEEN_DELETED);
        if(DateUtils.formatDate2StringDate(poTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))){
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
            if (poTransDetails == null) throw new ValidateException(ResponseMessage.PO_TRANS_DETAIL_IS_NOT_EXISTED);
            List<PoTransDetail> poTransDetailsImport = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getFromTransId());
            if (poTransDetailsImport == null)
                throw new ValidateException(ResponseMessage.PO_TRANS_DETAIL_IS_NOT_EXISTED);

            for (int i = 0; i < poTransDetails.size(); i++) {
                PoTransDetail ptd = poTransDetails.get(i);
                poTransDetailsImport.get(i).setReturnAmount(poTransDetailsImport.get(i).getReturnAmount() - ptd.getQuantity());
                stockTotalService.updateWithLock(shopId, poTrans.getWareHouseTypeId(), ptd.getProductId(), ptd.getQuantity(),2);
            }
            poTrans.setStatus(-1);
            poTrans = repository.save(poTrans);

            for (int i = 0; i < poTransDetails.size(); i++) {
                poTransDetailRepository.save(poTransDetailsImport.get(i));
            }

            return Arrays.asList(Arrays.asList(poTrans.getId().toString()));
        }else throw  new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }
    /** Xóa phiếu xuất điều chỉnh **/
    @Transactional(rollbackFor = Exception.class)
    public List<List<String>> removeStockAdjustmentTransExport(Long id, Long shopId) {
        Optional<StockAdjustmentTrans> stockAdjustmentTrans = stockAdjustmentTransRepository.findById(id);
        if (!stockAdjustmentTrans.isPresent())
            throw new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);
        if (stockAdjustmentTrans.get().getStatus() == -1)
            throw new ValidateException(ResponseMessage.RECEIPT_HAS_BEEN_DELETED);
        if (DateUtils.formatDate2StringDate(stockAdjustmentTrans.get().getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            List<StockAdjustmentTransDetail> stockAdjustmentTransDetails = stockAdjustmentTransDetailRepository.getStockAdjustmentTransDetailsByTransId(stockAdjustmentTrans.get().getId());
            List<String> orderNumbers = new ArrayList<String>();
            for (StockAdjustmentTransDetail satd :stockAdjustmentTransDetails ){
                stockTotalService.updateWithLock(shopId,stockAdjustmentTrans.get().getWareHouseTypeId(), satd.getProductId(), satd.getQuantity(),2);
            }
            if(stockAdjustmentTrans.get().getRedInvoiceNo() != null && !stockAdjustmentTrans.get().getRedInvoiceNo().isEmpty()) {
                List<SaleOrder> orders = saleOrderRepository.findSaleOrderByOrderCode(Arrays.asList(stockAdjustmentTrans.get().getRedInvoiceNo()));
                if(orders != null && !orders.isEmpty()){
                    for (SaleOrder order : orders){
                    	orderNumbers.add(order.getOrderNumber());
                        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findSaleOrderDetail(order.getId(), null);
                        saleOrderDetailRepository.deleteAll(saleOrderDetails);
                    }
                    saleOrderRepository.deleteAll(orders);
                }
            }
            stockAdjustmentTrans.get().setStatus(-1);
            String stockAdjustmentId = Strings.EMPTY;
            StockAdjustment stockAdjustment = stockAdjustmentRepository.getById(stockAdjustmentTrans.get().getAdjustmentId());
            if(stockAdjustment != null) {
                stockAdjustment.setStatus(1);
                stockAdjustment = stockAdjustmentRepository.save(stockAdjustment);
                stockAdjustmentId = stockAdjustment.getId().toString();
            }
            StockAdjustmentTrans stockAdjustmentTransResult = stockAdjustmentTransRepository.save(stockAdjustmentTrans.get());
            List<List<String>> syncIds = new ArrayList<List<String>>();
            syncIds.add(Arrays.asList(stockAdjustmentId));
            syncIds.add(Arrays.asList(stockAdjustmentTransResult.getId().toString()));
            syncIds.add(orderNumbers);
            return syncIds;
        } else throw new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }

    /** Xóa phiếu xuất vay mượn **/
    @Transactional(rollbackFor = Exception.class)
    public List<List<String>> removeStockBorrowingTransExport(Long id,Long shopId) {
        StockBorrowingTrans stockBorrowingTrans = stockBorrowingTransRepository.getById(id);
        if(stockBorrowingTrans == null || stockBorrowingTrans.getStatus()==-1) throw new ValidateException(ResponseMessage.RECEIPT_HAS_BEEN_DELETED);
        if(DateUtils.formatDate2StringDate(stockBorrowingTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))){
            List<StockBorrowingTransDetail> stockBorrowingTransDetails = stockBorrowingTransDetailRepository.getStockBorrowingTransDetailByTransId(stockBorrowingTrans.getId());
            List<List<String>> syncIds = new ArrayList<List<String>>();
            String stockBorrowingId = Strings.EMPTY; 
            for (StockBorrowingTransDetail sbtd : stockBorrowingTransDetails) {
                stockTotalService.updateWithLock(shopId, stockBorrowingTrans.getWareHouseTypeId(), sbtd.getProductId(), sbtd.getQuantity(),2);
            }
            stockBorrowingTrans.setStatus(-1);
            StockBorrowing sb = stockBorrowingRepository.findById(stockBorrowingTrans.getStockBorrowingId()).get();
            if(sb != null) {
                sb.setStatusExport(1);
                stockBorrowingRepository.save(sb);
                stockBorrowingId = sb.getId().toString();
            }
            stockBorrowingTrans = stockBorrowingTransRepository.save(stockBorrowingTrans);
            syncIds.add(Arrays.asList(stockBorrowingId));
            syncIds.add(Arrays.asList(stockBorrowingTrans.getId().toString()));
            return syncIds;
        }
        throw new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private String createPoTransExportCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());

        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXSP.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        Page<PoTrans> pos = repository.getLastTransCode(2, reciCode.toString(),
                LocalDateTime.now().with(firstDayOfYear()), PageRequest.of(0,1));

        int STT = 0;
        if(!pos.getContent().isEmpty()) {
            String str = pos.getContent().get(0).getTransCode();
            String numberString = str.substring(str.length() - 5);
            STT = Integer.valueOf(numberString);
        }
        reciCode.append(CreateCodeUtils.formatReceINumber(STT));
        return reciCode.toString();
    }


    public String createStockBorrowTransCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXSB.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        Page<StockBorrowingTrans> borrTrans = stockBorrowingTransRepository.getLastTransCode(2,
                reciCode.toString(), LocalDateTime.now().with(firstDayOfYear()), PageRequest.of(0, 1));
        int STT = 1;
        if(!borrTrans.getContent().isEmpty()) {
            String str = borrTrans.getContent().get(0).getTransCode();
            String numberString = str.substring(str.length() - 5);
            STT = Integer.valueOf(numberString);
        }
        reciCode.append(CreateCodeUtils.formatReceINumber(STT));
        return reciCode.toString();
    }

    private String createStockBorrowTransExportRedInvoice(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        LocalDate currentDate = LocalDate.now();
        String yy = df.format(Calendar.getInstance().getTime());
        String mm = String.valueOf(currentDate.getMonthValue());
        String dd = String.valueOf(currentDate.getDayOfMonth());

        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXP_");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append("_");
        reciCode.append(yy);
        reciCode.append(mm.length() < 2 ? "0" + mm : mm);
        reciCode.append(dd.length() < 2 ? "0" + dd : dd);
        reciCode.append("_");
        Page<StockBorrowingTrans> borrTrans = stockBorrowingTransRepository.getLastRedInvoiceNo(2,
                reciCode.toString(), LocalDateTime.now().with(firstDayOfYear()), PageRequest.of(0,1));
        int STT = 0;
        if(!borrTrans.getContent().isEmpty()) {
            String str = borrTrans.getContent().get(0).getRedInvoiceNo();
            String numberString = str.substring(str.length() - 3);
            STT = Integer.valueOf(numberString);
        }
        reciCode.append(CreateCodeUtils.formatReceINumberVer2(STT));
        return reciCode.toString();
    }

    private String createInternalExportCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXST.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        Pageable pageable = PageRequest.of(0,2);
        Page<StockAdjustmentTrans> stockAdjustmentTrans = stockAdjustmentTransRepository.getLastInternalCode(2,
                reciCode.toString(), LocalDateTime.now().with(firstDayOfYear()), pageable);
        int STT = 0;
        if(!stockAdjustmentTrans.getContent().isEmpty()) {
            String str = stockAdjustmentTrans.getContent().get(0).getInternalNumber();
            String numberString = str.substring(str.length() - 5);
            STT = Integer.valueOf(numberString);
        }

        reciCode.append(CreateCodeUtils.formatReceINumber(STT));
        return reciCode.toString();
    }
    
}
