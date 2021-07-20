package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.db.entity.status.Object;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.ReceiptExportCreateRequest;
import vn.viettel.sale.messaging.ReceiptExportUpdateRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ReceiptExportService;
import vn.viettel.sale.service.StockTotalService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.*;
import vn.viettel.sale.specification.ReceiptSpecification;
import vn.viettel.sale.util.CreateCodeUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> find(String transCode, String redInvoiceNo, LocalDateTime fromDate,
                                                                         LocalDateTime toDate, Integer type, Long shopId, Pageable pageable) {
        if (transCode!=null) transCode = transCode.toUpperCase();
        if (redInvoiceNo!=null) redInvoiceNo = redInvoiceNo.toUpperCase();
        if (fromDate == null) fromDate = LocalDateTime.of(2015,1,1,0,0);
        if (toDate == null) toDate = LocalDateTime.now();
        fromDate = DateUtils.convertFromDate(fromDate);
        toDate = DateUtils.convertToDate(toDate);

        if(type == null){
            Page<ReceiptImportDTO> pageResponse = repository.getReceipt(shopId, 2, transCode, redInvoiceNo, fromDate, toDate, pageable);
            TotalResponse totalResponse = repository.getTotalResponsePo(shopId, 2, transCode, redInvoiceNo, fromDate, toDate);
            TotalResponse totalResponse2 = repository.getTotalResponseAdjustment(shopId, 2, transCode, redInvoiceNo, fromDate, toDate);
            TotalResponse totalResponse3 = repository.getTotalResponseBorrowing(shopId, 2, transCode, redInvoiceNo, fromDate, toDate);
            if(totalResponse.getTotalQuantity() == null) totalResponse.setTotalQuantity(0);
            if(totalResponse.getTotalPrice() == null) totalResponse.setTotalPrice(0.0);
            if(totalResponse2.getTotalQuantity() == null) totalResponse2.setTotalQuantity(0);
            if(totalResponse2.getTotalPrice() == null) totalResponse2.setTotalPrice(0.0);
            if(totalResponse3.getTotalQuantity() == null) totalResponse3.setTotalQuantity(0);
            if(totalResponse3.getTotalPrice() == null) totalResponse3.setTotalPrice(0.0);
            totalResponse.setTotalQuantity(totalResponse.getTotalQuantity() + totalResponse2.getTotalQuantity() + totalResponse3.getTotalQuantity());
            totalResponse.setTotalPrice(totalResponse.getTotalPrice() + totalResponse2.getTotalPrice() + totalResponse3.getTotalPrice());

            return new CoverResponse(pageResponse, totalResponse);
        }else if(type == 0){
            Page<ReceiptImportListDTO> pageResponse = repository.getReceiptPo(shopId, 2, transCode, redInvoiceNo, fromDate, toDate, pageable);
            TotalResponse totalResponse = repository.getTotalResponsePo(shopId, 2, transCode, redInvoiceNo, fromDate, toDate);
            if(totalResponse.getTotalQuantity() == null) totalResponse.setTotalQuantity(0);
            if(totalResponse.getTotalPrice() == null) totalResponse.setTotalPrice(0.0);
            return new CoverResponse(pageResponse, totalResponse);
        }else if(type == 1){
            Page<ReceiptImportListDTO> pageResponse = repository.getReceiptAdjustment(shopId, 2, transCode, redInvoiceNo, fromDate, toDate, pageable);
            TotalResponse totalResponse = repository.getTotalResponseAdjustment(shopId, 2, transCode, redInvoiceNo, fromDate, toDate);
            if(totalResponse.getTotalQuantity() == null) totalResponse.setTotalQuantity(0);
            if(totalResponse.getTotalPrice() == null) totalResponse.setTotalPrice(0.0);
            return new CoverResponse(pageResponse, totalResponse);
        }else if (type == 2){
            Page<ReceiptImportListDTO> pageResponse = repository.getReceiptBorrowing(shopId, 2, transCode, redInvoiceNo, fromDate, toDate, pageable);
            TotalResponse totalResponse = repository.getTotalResponseBorrowing(shopId, 2, transCode, redInvoiceNo, fromDate, toDate);
            if(totalResponse.getTotalQuantity() == null) totalResponse.setTotalQuantity(0);
            if(totalResponse.getTotalPrice() == null) totalResponse.setTotalPrice(0.0);
            return new CoverResponse(pageResponse, totalResponse);
        }
        return null;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage createReceipt(ReceiptExportCreateRequest request, Long userId,Long shopId) {
        switch (request.getImportType()){
            case 0:
                   return createPoTransExport(request,userId,shopId);
            case 1:
                   return createAdjustmentTrans(request,userId,shopId);
            case 2:
                   return createBorrowingTrans(request,userId,shopId);
        }
        return ResponseMessage.CREATE_FAILED;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage updateReceiptExport(ReceiptExportUpdateRequest request, Long id,Long shopId) {
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
    public ResponseMessage removeReceiptExport(Integer type, Long id,Long shopId) {

        switch (type){
            case 0:
                return removePoTransExport(id,shopId);
            case 1:
                return removeStockAdjustmentTransExport(id,shopId);
            case 2:
                return  removeStockBorrowingTransExport(id,shopId);

        }
        return ResponseMessage.UPDATE_SUCCESSFUL;
    }
    @Override
    public Page<PoTransDTO> getListPoTrans(String transCode, String redInvoiceNo, String internalNumber, String poCoNo, LocalDateTime fromDate, LocalDateTime toDate,Long shopId, Pageable pageable) {
        ShopParamDTO shopParamDTO = shopClient.getImportSaleReturn(shopId);
        if(shopParamDTO == null || shopParamDTO.getId() == null ) throw new ValidateException(ResponseMessage.SHOP_PARAM_NOT_FOUND);
        LocalDateTime dateTime = LocalDateTime.now().minusDays(Integer.valueOf(shopParamDTO.getName()));
        Page<PoTrans> poTrans = repository.findAll(Specification.where(ReceiptSpecification.hasTransCode(transCode))
                .and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo)).and(ReceiptSpecification.hasInternalNumber(internalNumber))
                .and(ReceiptSpecification.hasPoCoNo(poCoNo)).and(ReceiptSpecification.hasFromDateToDateRedInvoice(fromDate, toDate))
                .and(ReceiptSpecification.hasStatus()).and(ReceiptSpecification.hasPoIdIsNotNull()).and(ReceiptSpecification.hasTypeImport())
                        .and(ReceiptSpecification.hasGreaterDay(dateTime)).and(ReceiptSpecification.hasNotReturn()).and(ReceiptSpecification.hasShopId(shopId))
                ,pageable);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return poTrans.map(e-> modelMapper.map(e, PoTransDTO.class));
    }

    @Override
    public List<StockAdjustmentDTO> getListStockAdjustment(Long shopId, Pageable pageable) {
        List<StockAdjustmentDTO> stockAdjustments = stockAdjustmentRepository.getStockAdjustmentExport(shopId);
        Collections.sort(stockAdjustments, Comparator.comparing(StockAdjustmentDTO::getAdjustmentDate, Comparator.reverseOrder()).thenComparing(StockAdjustmentDTO::getAdjustmentCode, Comparator.reverseOrder()));
        return stockAdjustments;
    }

    @Override
    public List<StockBorrowingDTO> getListStockBorrowing(Long shopId,Pageable pageable) {
        LocalDateTime date1 = LocalDateTime.now().toLocalDate().atTime(LocalTime.MIN);
        LocalDateTime date2 = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);
        List<StockBorrowingDTO> stockBorrowings = stockBorrowingRepository.getStockBorrowingExport(shopId,date1,date2);
        Collections.sort(stockBorrowings, Comparator.comparing(StockBorrowingDTO::getBorrowDate, Comparator.reverseOrder()).thenComparing(StockBorrowingDTO::getPoBorrowCode, Comparator.reverseOrder()));
        return stockBorrowings;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage createPoTransExport(ReceiptExportCreateRequest request,Long userId, Long shopId) {
        Long customerTypeDTO = customerTypeClient.getWarehouseTypeByShopId(shopId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PoTrans poRecord = modelMapper.map(request, PoTrans.class);
        PoTrans poTrans = repository.findById(request.getReceiptImportId()).get();
        poRecord.setTransDate(LocalDateTime.now());
        poRecord.setWareHouseTypeId(customerTypeDTO);
        poRecord.setTransCode(createPoTransExportCode(shopId));
        poRecord.setShopId(shopId);
        poRecord.setOrderDate(poTrans.getOrderDate());
        poRecord.setRedInvoiceNo(poTrans.getRedInvoiceNo());
        poRecord.setPocoNumber(poTrans.getPocoNumber());
        poRecord.setInternalNumber(poTrans.getInternalNumber());
        poRecord.setFromTransId(poTrans.getId());
        poRecord.setPoNumber(poTrans.getPoNumber());
        poRecord.setIsDebit(false);
        Integer total_quantity =0;
        Double total_amount = 0D;
        poRecord.setType(2);
        poRecord.setStatus(1);
        repository.save(poRecord);
        Set<Long> countNumSKU = new HashSet<>();
        List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
        List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, customerTypeDTO, poTransDetails.stream().map(item ->
                item.getProductId()).distinct().filter(Objects::nonNull).collect(Collectors.toList()));
        List<PoTransDetail> savePoTransDetails = new ArrayList<>();
        HashMap<StockTotal,Integer> idAndValues = new HashMap<>();

        for (int i = 0; i < poTransDetails.size(); i++) {
            PoTransDetail poTransDetail = new PoTransDetail();
                if (request.getIsRemainAll() == true) {
                    for (int j =0;j<request.getLitQuantityRemain().size();j++){
                        if(request.getLitQuantityRemain().get(j).getId().equals(poTransDetails.get(i).getId())){
                            countNumSKU.add(poTransDetails.get(i).getProductId());
                            poTransDetail.setTransId(poRecord.getId());
                            poTransDetail.setTransDate(LocalDateTime.now());
                            poTransDetail.setProductId(poTransDetails.get(i).getProductId());
                            poTransDetail.setQuantity(poTransDetails.get(i).getQuantity()-poTransDetails.get(i).getReturnAmount());
                            poTransDetail.setPrice(poTransDetails.get(i).getPrice());
                            poTransDetail.setShopId(shopId);
                            poTransDetail.setPriceNotVat(poTransDetails.get(i).getPriceNotVat());
                            poTransDetail.setAmountNotVat(poTransDetails.get(i).getAmountNotVat());
                            poTransDetail.setAmount(poTransDetails.get(i).getPrice()*poTransDetails.get(i).getQuantity());
                            poTransDetail.setReturnAmount(request.getLitQuantityRemain().get(i).getQuantity());
                            if(poTransDetails.get(i).getReturnAmount() != null)
                                poTransDetail.setReturnAmount(poTransDetails.get(i).getQuantity() - poTransDetails.get(i).getReturnAmount());
                            total_quantity +=request.getLitQuantityRemain().get(j).getQuantity();
                            total_amount += (request.getLitQuantityRemain().get(j).getQuantity()*poTransDetails.get(i).getPrice());
                            savePoTransDetails.add(poTransDetail);
                            poTransDetails.get(i).setReturnAmount(poTransDetails.get(i).getQuantity());
                            savePoTransDetails.add(poTransDetails.get(i));
                            StockTotal stockTotal = null; //stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(poTransDetails.get(i).getProductId(), customerTypeDTO,shopId);
                            for(StockTotal st : stockTotals){
                                if(st.getProductId().equals(poTransDetails.get(i).getProductId())){
                                    if (st.getQuantity() == null)  st.setQuantity(0);
                                    st.setQuantity(st.getQuantity() - request.getLitQuantityRemain().get(i).getQuantity());
                                    stockTotal = st;
                                    idAndValues.put(st, 0 - request.getLitQuantityRemain().get(i).getQuantity());
                                    if(stockTotal.getQuantity()<0) throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SSS,
                                            request.getLitQuantityRemain().get(i).getProductCode()+"-"+request.getLitQuantityRemain().get(i).getProductName());
                                    break;
                                }
                            }
                            if (stockTotal == null) throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                        }

                    }
            } else {
                    for (int j =0;j<request.getLitQuantityRemain().size();j++){
                        if(poTransDetails.get(i).getId().equals(request.getLitQuantityRemain().get(j).getId())){
                            countNumSKU.add(request.getLitQuantityRemain().get(j).getProductId());
                            if(poTransDetails.get(i).getQuantity()==null) throw new ValidateException(ResponseMessage.QUANTITY_CAN_NOT_BE_NULL);
                            if(poTransDetails.get(i).getAmount()==null) throw new ValidateException(ResponseMessage.AMOUNT_CAN_NOT_BE_NULL);
                            poTransDetail.setTransId(poRecord.getId());
                            poTransDetail.setProductId(poTransDetails.get(i).getProductId());
                            poTransDetail.setQuantity(request.getLitQuantityRemain().get(j).getQuantity());
                            poTransDetail.setPrice(poTransDetails.get(i).getPrice());
                            poTransDetail.setPriceNotVat(poTransDetails.get(i).getPriceNotVat());
                            poTransDetail.setAmountNotVat(poTransDetails.get(i).getAmountNotVat());
                            poTransDetail.setShopId(shopId);
                            poTransDetail.setReturnAmount(request.getLitQuantityRemain().get(i).getQuantity());
                            poTransDetail.setAmount(request.getLitQuantityRemain().get(j).getQuantity()*poTransDetails.get(i).getPrice());
                            savePoTransDetails.add(poTransDetail);
                            total_quantity +=poTransDetail.getQuantity();
                            total_amount += poTransDetail.getAmount();
                            poTransDetails.get(i).setReturnAmount(poTransDetails.get(i).getReturnAmount()+ poTransDetail.getQuantity());
                            savePoTransDetails.add(poTransDetails.get(i));
                            if(poTransDetails.get(i).getReturnAmount()> poTransDetails.get(i).getQuantity())
                                throw new ValidateException(ResponseMessage.RETURN_AMOUNT_MUST_BE_LESS_THAN_OR_EQUAL_TO_THE_QUANTITY_ENTERED);
                            StockTotal stockTotal = null;//stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(poTransDetails.get(i).getProductId(), customerTypeDTO,shopId);
                            for(StockTotal st : stockTotals){
                                if(st.getProductId().equals(poTransDetails.get(i).getProductId())){
                                    if (st.getQuantity() == null)  st.setQuantity(0);
                                    st.setQuantity(st.getQuantity() - request.getLitQuantityRemain().get(i).getQuantity());
                                    idAndValues.put(st, 0 - request.getLitQuantityRemain().get(i).getQuantity());
                                    stockTotal = st;
                                    if(stockTotal.getQuantity()<0) throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SSS,
                                            request.getLitQuantityRemain().get(i).getProductCode()+"-"+request.getLitQuantityRemain().get(i).getProductName());
                                    break;
                                }
                            }
                            if (stockTotal == null) throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                        }
                    }
            }
        }
        for(PoTransDetail poTransDetail : savePoTransDetails) {
            poTransDetailRepository.save(poTransDetail);
        }
        stockTotalService.updateWithLock(idAndValues);
        poRecord.setTotalQuantity(total_quantity);
        poRecord.setTotalAmount(total_amount);
        poRecord.setNumSku(countNumSKU.size());
        repository.save(poRecord);
        return ResponseMessage.CREATED_SUCCESSFUL;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage createAdjustmentTrans(ReceiptExportCreateRequest request, Long userId,Long shopId) {
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        CustomerDTO cus = customerClient.getCusDefault(shopId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        StockAdjustmentTrans poAdjustTrans = modelMapper.map(request, StockAdjustmentTrans.class);
        StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(request.getReceiptImportId()).get();
        if(stockAdjustment.getStatus()==3) throw new ValidateException(ResponseMessage.RECEIPT_HAS_BEEN_EXPORTED);
        Response<ApParamDTO> reason = apparamClient.getReasonV1(stockAdjustment.getReasonId());
        if(reason.getData() == null || reason.getData().getId() == null ) throw new ValidateException(ResponseMessage.REASON_NOT_FOUND);
        LocalDateTime ldt = LocalDateTime.now();
        poAdjustTrans.setTransDate(ldt);
        poAdjustTrans.setTransCode(stockAdjustment.getAdjustmentCode());
        poAdjustTrans.setShopId(shopId);
        poAdjustTrans.setRedInvoiceNo(createStockAdjustmentExportRedInvoice(shopId));
        poAdjustTrans.setAdjustmentDate(stockAdjustment.getAdjustmentDate());
        poAdjustTrans.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
        poAdjustTrans.setOrderDate(stockAdjustment.getAdjustmentDate());
        poAdjustTrans.setInternalNumber(createInternalExportCode(shopId));
        poAdjustTrans.setAdjustmentId(stockAdjustment.getId());
        poAdjustTrans.setReasonId(stockAdjustment.getReasonId());
        poAdjustTrans.setType(2);
        poAdjustTrans.setStatus(1);
        stockAdjustmentTransRepository.save(poAdjustTrans);
        SaleOrder order = new SaleOrder();
        order.setType(4);
        order.setOrderNumber(poAdjustTrans.getRedInvoiceNo());
        order.setOrderDate(ldt);
        order.setShopId(shopId);
        order.setSalemanId(userId);
        order.setCustomerId(cus.getId());
        order.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
        order.setBalance(0D);
        order.setNote(reason.getData().getApParamName());
        order.setMemberCardAmount(0D);
        order.setTotalVoucher(0D);
        order.setPaymentType(1);
        order.setDeliveryType(0);
        order.setTotalCustomerPurchase(cus.getTotalBill());
        order.setAutoPromotionNotVat(0D);
        order.setAutoPromotion(0D);
        order.setZmPromotion(0D);
        order.setTotalPromotionNotVat(0D);
        order.setAutoPromotionVat(0D);
        order.setCustomerPurchase(0D);
        order.setDiscountCodeAmount(0D);
        order.setUsedRedInvoice(false);
        saleOrderRepository.save(order);
        List<StockAdjustmentDetail> sads = stockAdjustmentDetailRepository.getStockAdjustmentDetailByAdjustmentId(stockAdjustment.getId());
        Integer totalQuantity =0;
        Double totalAmount = 0D;
        List<Price> prices = productPriceRepository.findProductPrice(sads.stream().map(item -> item.getProductId()).distinct()
                .collect(Collectors.toList()), customerTypeDTO.getWareHouseTypeId(), LocalDateTime.now());
        List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, customerTypeDTO.getWareHouseTypeId(), sads.stream().map(item ->
                item.getProductId()).distinct().filter(Objects::nonNull).collect(Collectors.toList()));

        for(StockAdjustmentDetail sad : sads){
            if(sad.getQuantity()==null) sad.setQuantity(0);
            if(sad.getPrice()==null) sad.setPrice(0D);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentTransDetail satd = modelMapper.map(sad, StockAdjustmentTransDetail.class);
            satd.setTransId(poAdjustTrans.getId());
            satd.setShopId(shopId);
            totalQuantity +=sad.getQuantity();
            totalAmount += sad.getPrice()*sad.getQuantity();
            StockTotal stockTotal = null;//stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(sad.getProductId(), customerTypeDTO.getWareHouseTypeId(),shopId);
            if(stockTotals != null){
                for(StockTotal st : stockTotals){
                    if(st.getProductId().equals(sad.getProductId())){
                        stockTotal = st;
                        break;
                    }
                }
            }
            if(stockTotal == null)
                continue;
            if(stockTotal.getQuantity() == null){
                stockTotal.setQuantity(0);
            }
            stockTotal.setQuantity(stockTotal.getQuantity()- sad.getQuantity());
            stockTotalService.updateWithLock(stockTotal, 0 - sad.getQuantity());
            stockAdjustmentTransDetailRepository.save(satd);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            SaleOrderDetail saleOrderDetail = modelMapper.map(sad, SaleOrderDetail.class);
            saleOrderDetail.setSaleOrderId(order.getId());
            saleOrderDetail.setAmount(sad.getPrice()*sad.getQuantity());
            saleOrderDetail.setTotal(sad.getPrice()*sad.getQuantity());
            saleOrderDetail.setIsFreeItem(false);
            saleOrderDetail.setAutoPromotion(0D);
            saleOrderDetail.setZmPromotion(0D);
            if(prices != null){
                for(Price price : prices){
                    if(price.getProductId().equals(sad.getProductId())){
                        saleOrderDetail.setPriceNotVat(price.getPriceNotVat());
                        break;
                    }
                }
            }
            saleOrderDetail.setAutoPromotionNotVat(0D);
            saleOrderDetail.setAutoPromotionVat(0D);
            saleOrderDetail.setZmPromotionVat(0D);
            saleOrderDetail.setZmPromotionNotVat(0D);
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
        return ResponseMessage.CREATED_SUCCESSFUL;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage createBorrowingTrans(ReceiptExportCreateRequest request, Long userId,Long shopId) {
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        StockBorrowingTrans poBorrowTransRecord = modelMapper.map(request, StockBorrowingTrans.class);
        StockBorrowing stockBorrowing = stockBorrowingRepository.findById(request.getReceiptImportId()).get();
        if(stockBorrowing.getStatusExport() == 2) throw new ValidateException(ResponseMessage.RECEIPT_HAS_BEEN_EXPORTED);
        poBorrowTransRecord.setTransDate(LocalDateTime.now());
        poBorrowTransRecord.setFromShopId(shopId);
        poBorrowTransRecord.setToShopId(stockBorrowing.getToShopId());
        poBorrowTransRecord.setShopId(shopId);
        poBorrowTransRecord.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
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
        List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, customerTypeDTO.getWareHouseTypeId(), sbds.stream().map(item ->
                item.getProductId()).distinct().filter(Objects::nonNull).collect(Collectors.toList()));

        for(StockBorrowingDetail sbd : sbds){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingTransDetail sbtd = modelMapper.map(sbd, StockBorrowingTransDetail.class);
            sbtd.setTransId(poBorrowTransRecord.getId());
            sbtd.setTransDate(LocalDateTime.now());
            totalAmount+= sbd.getPrice()*sbd.getQuantity();
            totalQuantity += sbd.getQuantity();
            StockTotal stockTotal = null;//stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(sbd.getProductId(), customerTypeDTO.getWareHouseTypeId(),shopId);
            if(stockTotals != null){
                for(StockTotal st : stockTotals){
                    if(st.getProductId().equals(sbd.getProductId())){
                        stockTotal = st;
                        break;
                    }
                }
            }
            if(stockTotal == null)
                continue;
            if(stockTotal.getQuantity() == null){
                stockTotal.setQuantity(0);
            }
            stockTotal.setQuantity(stockTotal.getQuantity()- sbd.getQuantity());
            stockTotalService.updateWithLock(stockTotal, 0 - sbd.getQuantity());
            stockBorrowingTransDetailRepository.save(sbtd);
        }
        poBorrowTransRecord.setTotalQuantity(totalQuantity);
        poBorrowTransRecord.setTotalAmount(totalAmount);
        poBorrowTransRecord.setNote(request.getNote()==null ? stockBorrowing.getNote() : request.getNote());
        stockBorrowing.setStatusExport(2);
        stockBorrowingRepository.save(stockBorrowing);
        stockBorrowingTransRepository.save(poBorrowTransRecord);
        return ResponseMessage.CREATED_SUCCESSFUL;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage updatePoTransExport(ReceiptExportUpdateRequest request, Long id,Long shopId) {

        PoTrans poTrans = repository.findById(id).get();
        if (DateUtils.formatDate2StringDate(poTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
            if(poTrans.getFromTransId() == null) throw new ValidateException(ResponseMessage.RECORD_WRONG);
            List<PoTransDetail> poTransDetailImport = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getFromTransId());
            if(poTransDetailImport == null) throw new ValidateException(ResponseMessage.RECORD_WRONG);
            int total = 0;
            double totalAmount = 0D;
            List<PoTransDetail> savePoTransDetails = new ArrayList<>();
            List<StockTotal> stockTotals = new ArrayList<>();
            HashMap<StockTotal,Integer> idAndValues = new HashMap<>();

            if(!request.getListProductRemain().isEmpty()){
                stockTotals = stockTotalRepository.getStockTotal(shopId, poTrans.getWareHouseTypeId(), poTransDetails.stream().map(item ->
                        item.getProductId()).distinct().filter(Objects::nonNull).collect(Collectors.toList()));

                for (int i=0;i<poTransDetails.size();i++){
                    PoTransDetail poTransDetail = poTransDetails.get(i);

                    for (int j = 0;j<request.getListProductRemain().size();j++){
                        if(poTransDetail.getId().equals(request.getListProductRemain().get(j).getId())){
                            if(poTransDetailImport.get(i).getReturnAmount()>poTransDetailImport.get(i).getQuantity())
                                throw new ValidateException(ResponseMessage.RETURN_AMOUNT_MUST_BE_LESS_THAN_OR_EQUAL_TO_THE_QUANTITY_ENTERED);
                            poTransDetailImport.get(i).setReturnAmount(poTransDetailImport.get(i).getReturnAmount() + (request.getListProductRemain().get(j).getQuantity()-poTransDetail.getQuantity()));
                            StockTotal st = null;//stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(poTransDetail.getProductId(),poTrans.getWareHouseTypeId(),shopId);
                            if(stockTotals != null){
                                for(StockTotal stockTotal : stockTotals){
                                    if(stockTotal.getProductId().equals(poTransDetail.getProductId())){
                                        stockTotal.setQuantity(stockTotal.getQuantity()-(request.getListProductRemain().get(j).getQuantity()-poTransDetail.getQuantity()));
                                        idAndValues.put(stockTotal, 0 - (request.getListProductRemain().get(j).getQuantity()-poTransDetail.getQuantity()));
                                        if(stockTotal.getQuantity()<0) throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SSS,
                                                request.getListProductRemain().get(i).getProductCode()+"-"+request.getListProductRemain().get(i).getProductName());
                                        st = stockTotal; break;
                                    }
                                }
                            }
                            if(st == null) throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                            poTransDetail.setQuantity(request.getListProductRemain().get(j).getQuantity());
                            total +=poTransDetail.getQuantity();
                            totalAmount+=(poTransDetail.getQuantity() * poTransDetail.getPriceNotVat());
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
            for(PoTransDetail poTransDetail : savePoTransDetails){
                poTransDetailRepository.save(poTransDetail);
            }
            if(stockTotals != null){
                stockTotalService.updateWithLock(idAndValues);
            }
            return ResponseMessage.UPDATE_SUCCESSFUL;
        }
        else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);
    }

    private ResponseMessage updateAdjustmentTransExport(ReceiptExportUpdateRequest request, Long id,Long shopId) {

        StockAdjustmentTrans adjustmentTrans = stockAdjustmentTransRepository.findById(id).get();
        if (DateUtils.formatDate2StringDate(adjustmentTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            adjustmentTrans.setNote(request.getNote());
            stockAdjustmentTransRepository.save(adjustmentTrans);
            return ResponseMessage.UPDATE_SUCCESSFUL;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);
    }

    private ResponseMessage updateBorrowingTransExport(ReceiptExportUpdateRequest request, Long id,Long shopId) {
        StockBorrowingTrans borrowingTrans = stockBorrowingTransRepository.findById(id).get();
        if (DateUtils.formatDate2StringDate(borrowingTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            borrowingTrans.setNote(request.getNote());
            stockBorrowingTransRepository.save(borrowingTrans);
            return ResponseMessage.UPDATE_SUCCESSFUL;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage removePoTransExport(Long id,Long shopId) {
        PoTrans poTrans = repository.findById(id).get();
        if(poTrans.getStatus()==-1) throw new ValidateException(ResponseMessage.RECEIPT_HAS_BEEN_DELETED);
        if(DateUtils.formatDate2StringDate(poTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))){
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
            if(poTransDetails == null) throw new ValidateException(ResponseMessage.PO_TRANS_DETAIL_IS_NOT_EXISTED);
            List<PoTransDetail> poTransDetailsImport = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getFromTransId());
            if(poTransDetailsImport == null) throw new ValidateException(ResponseMessage.PO_TRANS_DETAIL_IS_NOT_EXISTED);
            List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, poTrans.getWareHouseTypeId(), poTransDetails.stream().map(item ->
                    item.getProductId()).distinct().filter(Objects::nonNull).collect(Collectors.toList()));
            HashMap<StockTotal,Integer> idAndValues = new HashMap<>();

            for (int i=0;i<poTransDetails.size();i++){
                PoTransDetail ptd = poTransDetails.get(i);
                poTransDetailsImport.get(i).setReturnAmount(poTransDetailsImport.get(i).getReturnAmount()-ptd.getQuantity());
                StockTotal st = null;//stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(ptd.getProductId(),poTrans.getWareHouseTypeId(),shopId);
                if(stockTotals != null){
                    for(StockTotal stockTotal : stockTotals){
                        if(stockTotal.getProductId().equals(ptd.getProductId())){
                            stockTotal.setQuantity(stockTotal.getQuantity()+ptd.getQuantity());
                            idAndValues.put(stockTotal, ptd.getQuantity());
                            st = stockTotal; break;
                        }
                    }
                }
                if(st == null )throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
            }
            poTrans.setStatus(-1);
            repository.save(poTrans);

            for (int i=0;i<poTransDetails.size();i++){
                poTransDetailRepository.save( poTransDetailsImport.get(i));
            }
            if(stockTotals != null){
                stockTotalService.updateWithLock(idAndValues);
            }

            return ResponseMessage.DELETE_SUCCESSFUL;
        }else throw  new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage removeStockAdjustmentTransExport(Long id,Long shopId) {
        Optional<StockAdjustmentTrans> stockAdjustmentTrans = stockAdjustmentTransRepository.findById(id);
        if(!stockAdjustmentTrans.isPresent()) throw new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);
        if(stockAdjustmentTrans.get().getStatus()==-1) throw new ValidateException(ResponseMessage.RECEIPT_HAS_BEEN_DELETED);
        if(DateUtils.formatDate2StringDate(stockAdjustmentTrans.get().getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))){
            List<StockAdjustmentTransDetail> stockAdjustmentTransDetails = stockAdjustmentTransDetailRepository.getStockAdjustmentTransDetailsByTransId(stockAdjustmentTrans.get().getId());
            List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, stockAdjustmentTrans.get().getWareHouseTypeId(),
                    stockAdjustmentTransDetails.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()));

            for (StockAdjustmentTransDetail satd :stockAdjustmentTransDetails ){
                StockTotal stockTotal = null;//stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(satd.getProductId(),stockAdjustmentTrans.get().getWareHouseTypeId(),shopId);
                if(stockTotals != null){
                    for (StockTotal st : stockTotals){
                        if(st.getProductId().equals(satd.getProductId())){
                            stockTotal = st;break;
                        }
                    }
                }
                if(stockTotal== null ) continue; //throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                stockTotal.setQuantity(stockTotal.getQuantity()+satd.getQuantity());
                stockTotalService.updateWithLock(stockTotal, satd.getQuantity());
            }

            SaleOrder order = saleOrderRepository.getSaleOrderByOrderNumber(stockAdjustmentTrans.get().getRedInvoiceNo()).orElseThrow(() -> new ValidateException(ResponseMessage.SALE_ORDER_NOT_FOUND));
            List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findSaleOrderDetail(order.getId(), null);
            saleOrderDetailRepository.deleteAll(saleOrderDetails);
            saleOrderRepository.delete(order);
            stockAdjustmentTrans.get().setStatus(-1);
            StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(stockAdjustmentTrans.get().getAdjustmentId()).get();
            stockAdjustment.setStatus(1);
            stockAdjustmentRepository.save(stockAdjustment);
            stockAdjustmentTransRepository.save(stockAdjustmentTrans.get());
            return ResponseMessage.DELETE_SUCCESSFUL;
        }else throw  new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage removeStockBorrowingTransExport(Long id,Long shopId) {
        StockBorrowingTrans stockBorrowingTrans = stockBorrowingTransRepository.getStockBorrowingTransById(id);
        if(stockBorrowingTrans.getStatus()==-1) throw new ValidateException(ResponseMessage.RECEIPT_HAS_BEEN_DELETED);
        if(DateUtils.formatDate2StringDate(stockBorrowingTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))){
            List<StockBorrowingTransDetail> stockBorrowingTransDetails = stockBorrowingTransDetailRepository.getStockBorrowingTransDetailByTransId(stockBorrowingTrans.getId());
            List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, stockBorrowingTrans.getWareHouseTypeId(),
                    stockBorrowingTransDetails.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()));

            for (StockBorrowingTransDetail sbtd :stockBorrowingTransDetails ){
                StockTotal stockTotal = null;//stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(sbtd.getProductId(),stockBorrowingTrans.getWareHouseTypeId(),shopId);
                if(stockTotals != null){
                    for (StockTotal st : stockTotals){
                        if(st.getProductId().equals(sbtd.getProductId())){
                            stockTotal = st;break;
                        }
                    }
                }
                if(stockTotal == null) continue; // throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                stockTotal.setQuantity(stockTotal.getQuantity() + sbtd.getQuantity());
                stockTotalService.updateWithLock(stockTotal, sbtd.getQuantity());
            }
            stockBorrowingTrans.setStatus(-1);
            StockBorrowing sb = stockBorrowingRepository.findById(stockBorrowingTrans.getStockBorrowingId()).get();
            sb.setStatusExport(1);
            stockBorrowingRepository.save(sb);
            stockBorrowingTransRepository.save(stockBorrowingTrans);
            return ResponseMessage.DELETE_SUCCESSFUL;
        }throw new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public  String createPoTransExportCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        int reciNum = repository.getQuantityPoTransExport();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXSP.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(CreateCodeUtils.formatReceINumber(reciNum));
        return reciCode.toString();
    }
    public  String createStockAdjustmentExportCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        int reciNum = stockAdjustmentTransRepository.getQuantityStockAdjustTransExport();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXST.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(CreateCodeUtils.formatReceINumber(reciNum));
        return reciCode.toString();
    }
    public  String createStockAdjustmentExportRedInvoice(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        LocalDate currentDate = LocalDate.now();
        String yy = df.format(Calendar.getInstance().getTime());
        String mm = String.valueOf(currentDate.getMonthValue());
        String dd = String.valueOf(currentDate.getDayOfMonth());
        int reciNum = saleOrderRepository.countIdFromSaleOrder();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("SAL.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(yy);
        reciCode.append(mm.length()<2 ? "0"+mm : mm);
        reciCode.append(dd.length()<2 ? "0"+dd : dd);
        reciCode.append(CreateCodeUtils.formatReceINumber(reciNum));
        return reciCode.toString();
    }
    public  String createStockBorrowTransCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        int reciNum = stockBorrowingTransRepository.getQuantityStockBorrowingTransExport();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXSB.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(CreateCodeUtils.formatReceINumber(reciNum));
        return reciCode.toString();
    }
    public  String createStockBorrowTransExportRedInvoice(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        LocalDate currentDate = LocalDate.now();
        String yy = df.format(Calendar.getInstance().getTime());
        String mm = String.valueOf(currentDate.getMonthValue());
        String dd = String.valueOf(currentDate.getDayOfMonth());
        int reciNum = stockBorrowingTransRepository.getQuantityStockBorrowingTransExport();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXP_");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append("_");
        reciCode.append(yy);
        reciCode.append(mm.length()<2 ? "0"+mm : mm);
        reciCode.append(dd.length()<2 ? "0"+dd : dd);
        reciCode.append("_");
        reciCode.append(CreateCodeUtils.formatReceINumberVer2(reciNum));
        return reciCode.toString();
    }

    public  String createInternalExportCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        int reciNum = stockAdjustmentTransRepository.getQuantityStockAdjustTransExport();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXST.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(CreateCodeUtils.formatReceINumber(reciNum));
        return reciCode.toString();
    }
}
