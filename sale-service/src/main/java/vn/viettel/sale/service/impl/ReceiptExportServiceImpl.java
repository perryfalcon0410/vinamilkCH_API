package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.jms.JMSSender;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.utils.JMSType;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.ReceiptExportCreateRequest;
import vn.viettel.sale.messaging.ReceiptExportUpdateRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ReceiptExportService;
import vn.viettel.sale.service.dto.PoTransDTO;
import vn.viettel.sale.service.dto.ReceiptImportListDTO;
import vn.viettel.sale.service.dto.StockAdjustmentDTO;
import vn.viettel.sale.service.dto.StockBorrowingDTO;
import vn.viettel.sale.service.feign.*;
import vn.viettel.sale.specification.ReceiptSpecification;
import vn.viettel.sale.util.CreateCodeUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Slf4j
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
    private JMSSender jmsSender;

    @Override
    public CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> find(String transCode, String redInvoiceNo, LocalDateTime fromDate, LocalDateTime toDate, Integer type, Long shopId, Pageable pageable) {
        int totalQuantity = 0;
        Double totalPrice = 0D;
        if(type == null){
            if (redInvoiceNo!=null) redInvoiceNo = redInvoiceNo.toUpperCase();
            List<PoTrans> list1 = repository.findAll(Specification.where(ReceiptSpecification.hasStatus()).and(Specification.where(ReceiptSpecification.hasTransCode(transCode))).and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDate(fromDate, toDate)).and(ReceiptSpecification.hasTypeExport()).and(ReceiptSpecification.hasShopId(shopId)));
            List<StockAdjustmentTrans> list2 = stockAdjustmentTransRepository.findAll(Specification.where(ReceiptSpecification.hasStatusA().and(Specification.where(ReceiptSpecification.hasTransCodeA(transCode))).and(ReceiptSpecification.hasRedInvoiceNoA(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateA(fromDate, toDate)).and(ReceiptSpecification.hasTypeExportA())).and(ReceiptSpecification.hasShopIdA(shopId)));
            List<StockBorrowingTrans> list3 = stockBorrowingTransRepository.findAll(Specification.where(ReceiptSpecification.hasStatusB()).and(Specification.where(ReceiptSpecification.hasTransCodeB(transCode))).and(ReceiptSpecification.hasRedInvoiceNoB(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateB(fromDate, toDate)).and(ReceiptSpecification.hasTypeExportB()).and(ReceiptSpecification.hasFromShopId(shopId)));
            List<ReceiptImportListDTO> listAddDTO1 = new ArrayList<>();
            for(PoTrans poTrans : list1){
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                ReceiptImportListDTO poRecord = modelMapper.map(poTrans, ReceiptImportListDTO.class);
                poRecord.setReceiptType(0);
                listAddDTO1.add(poRecord);
            }
            List<ReceiptImportListDTO> listAddDTO2 = new ArrayList<>();
            for(StockAdjustmentTrans stockAdjustmentTrans : list2){
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                ReceiptImportListDTO poARecord = modelMapper.map(stockAdjustmentTrans, ReceiptImportListDTO.class);
                poARecord.setReceiptType(1);
                listAddDTO2.add(poARecord);
            }
            List<ReceiptImportListDTO> listAddDTO3 = new ArrayList<>();
            for(StockBorrowingTrans stockBorrowingTrans : list3){
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                ReceiptImportListDTO poBRecord = modelMapper.map(stockBorrowingTrans, ReceiptImportListDTO.class);
                poBRecord.setReceiptType(2);
                listAddDTO3.add(poBRecord);
            }
            List<ReceiptImportListDTO> result = new ArrayList<>();
            result.addAll(listAddDTO1);
            result.addAll(listAddDTO2);
            result.addAll(listAddDTO3);
            List<ReceiptImportListDTO> subList;
            for (int i = 0; i < result.size(); i++) {
                if(result.get(i).getTotalQuantity() == null) throw new ValidateException(ResponseMessage.QUANTITY_CAN_NOT_BE_NULL);
                if(result.get(i).getTotalAmount() == null) throw new ValidateException(ResponseMessage.AMOUNT_CAN_NOT_BE_NULL);
                totalQuantity += result.get(i).getTotalQuantity();
                totalPrice += result.get(i).getTotalAmount();
            }
            Collections.sort(result, Comparator.comparing(ReceiptImportListDTO::getTransDate, Comparator.reverseOrder()).thenComparing(ReceiptImportListDTO::getTransCode));
            TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), result.size());
            subList = result.subList(start, end);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(subList,pageable,result.size());
            CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> response = new CoverResponse(pageResponse, totalResponse);
            return response;
        }else if(type == 0){
            List<PoTrans> list1 = repository.findAll(Specification.where(ReceiptSpecification.hasStatus())
                    .and(ReceiptSpecification.hasTransCode(transCode)).and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDate(fromDate, toDate)).and(ReceiptSpecification.hasTypeExport()).and(ReceiptSpecification.hasShopId(shopId)));
            List<ReceiptImportListDTO> listAddDTO1 = new ArrayList<>();
            for(PoTrans poTrans : list1){
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                ReceiptImportListDTO poRecord = modelMapper.map(poTrans, ReceiptImportListDTO.class);
                poRecord.setReceiptType(0);
                listAddDTO1.add(poRecord);
            }
            List<ReceiptImportListDTO> subList;
            for (int i = 0; i < listAddDTO1.size(); i++) {
                if(listAddDTO1.get(i).getTotalQuantity() == null) throw new ValidateException(ResponseMessage.QUANTITY_CAN_NOT_BE_NULL);
                if(listAddDTO1.get(i).getTotalAmount() == null) throw new ValidateException(ResponseMessage.AMOUNT_CAN_NOT_BE_NULL);
                totalQuantity += listAddDTO1.get(i).getTotalQuantity();
                totalPrice += listAddDTO1.get(i).getTotalAmount();
            }

            Collections.sort(listAddDTO1, Comparator.comparing(ReceiptImportListDTO::getTransDate, Comparator.reverseOrder()).thenComparing(ReceiptImportListDTO::getTransCode));
            TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), listAddDTO1.size());
            subList = listAddDTO1.subList(start, end);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(subList,pageable,listAddDTO1.size());
            CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> response = new CoverResponse(pageResponse, totalResponse);
            return response;
        }else if(type == 1){
            if (redInvoiceNo!=null) redInvoiceNo = redInvoiceNo.toUpperCase();
            List<StockAdjustmentTrans> list2 = stockAdjustmentTransRepository.findAll(Specification.where(ReceiptSpecification.hasStatusA().and(ReceiptSpecification.hasTransCodeA(transCode)).and(ReceiptSpecification.hasRedInvoiceNoA(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateA(fromDate, toDate)).and(ReceiptSpecification.hasTypeExportA())).and(ReceiptSpecification.hasShopIdA(shopId)));
            List<ReceiptImportListDTO> listAddDTO2 = new ArrayList<>();
            for(StockAdjustmentTrans stockAdjustmentTrans : list2){
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                ReceiptImportListDTO poARecord = modelMapper.map(stockAdjustmentTrans, ReceiptImportListDTO.class);
                poARecord.setReceiptType(1);
                listAddDTO2.add(poARecord);
            }
            List<ReceiptImportListDTO> subList;
            for (int i = 0; i < listAddDTO2.size(); i++) {
                if(listAddDTO2.get(i).getTotalQuantity() == null) throw new ValidateException(ResponseMessage.QUANTITY_CAN_NOT_BE_NULL);
                if(listAddDTO2.get(i).getTotalAmount() == null) throw new ValidateException(ResponseMessage.AMOUNT_CAN_NOT_BE_NULL);
                totalQuantity += listAddDTO2.get(i).getTotalQuantity();
                totalPrice += listAddDTO2.get(i).getTotalAmount();
            }

            Collections.sort(listAddDTO2, Comparator.comparing(ReceiptImportListDTO::getTransDate, Comparator.reverseOrder()).thenComparing(ReceiptImportListDTO::getTransCode));
            TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), listAddDTO2.size());
            subList = listAddDTO2.subList(start, end);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(subList,pageable,listAddDTO2.size());
            CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> response =
                    new CoverResponse(pageResponse, totalResponse);
            return response;
        }else if (type == 2){
            if (redInvoiceNo!=null) redInvoiceNo = redInvoiceNo.toUpperCase();
            List<StockBorrowingTrans> list3 = stockBorrowingTransRepository.findAll(Specification.where(ReceiptSpecification.hasStatusB()).and(ReceiptSpecification.hasTransCodeB(transCode)).and(ReceiptSpecification.hasRedInvoiceNoB(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateB(fromDate, toDate)).and(ReceiptSpecification.hasTypeExportB()).and(ReceiptSpecification.hasFromShopId(shopId)));
            List<ReceiptImportListDTO> listAddDTO3 = new ArrayList<>();
            for(StockBorrowingTrans stockBorrowingTrans : list3){
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                ReceiptImportListDTO poBRecord = modelMapper.map(stockBorrowingTrans, ReceiptImportListDTO.class);
                poBRecord.setReceiptType(2);
                listAddDTO3.add(poBRecord);
            }
            List<ReceiptImportListDTO> subList;
            for (int i = 0; i < listAddDTO3.size(); i++) {
                if(listAddDTO3.get(i).getTotalQuantity() == null) throw new ValidateException(ResponseMessage.QUANTITY_CAN_NOT_BE_NULL);
                if(listAddDTO3.get(i).getTotalAmount() == null) throw new ValidateException(ResponseMessage.AMOUNT_CAN_NOT_BE_NULL);
                totalQuantity += listAddDTO3.get(i).getTotalQuantity();
                totalPrice += listAddDTO3.get(i).getTotalAmount();
            }

            Collections.sort(listAddDTO3, Comparator.comparing(ReceiptImportListDTO::getTransDate, Comparator.reverseOrder()).thenComparing(ReceiptImportListDTO::getTransCode));
            TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), listAddDTO3.size());
            subList = listAddDTO3.subList(start, end);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(subList,pageable,listAddDTO3.size());
            CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> response =
                    new CoverResponse(pageResponse, totalResponse);
            return response;
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
    public ResponseMessage updateReceiptExport(ReceiptExportUpdateRequest request, Long id) {
        switch (request.getType()){
            case 0:
                    return updatePoTransExport(request,id);
            case 1:
                    return updateAdjustmentTransExport(request,id);
            case 2:
                    return updateBorrowingTransExport(request,id);
        }
        return null;
    }
    @Override
    public ResponseMessage removeReceiptExport(Integer type, Long id) {

        switch (type){
            case 0:
                return removePoTransExport(id);
            case 1:
                return removeStockAdjustmentTransExport(id);
            case 2:
                return  removeStockBorrowingTransExport(id);

        }
        return ResponseMessage.UPDATE_SUCCESSFUL;
    }
    @Override
    public Page<PoTransDTO> getListPoTrans(String transCode, String redInvoiceNo, String internalNumber, String poCoNo, LocalDateTime fromDate, LocalDateTime toDate,Long shopId, Pageable pageable) {
        ShopParamDTO shopParamDTO = shopClient.getImportSaleReturn(shopId);
        if(shopParamDTO == null || shopParamDTO.getId() == null ) throw new ValidateException(ResponseMessage.SHOP_PARAM_NOT_FOUND);
        LocalDateTime dateTime = LocalDateTime.now().minusDays(Integer.valueOf(shopParamDTO.getName()));
        Page<PoTrans> poTrans = repository.findAll(Specification.where(ReceiptSpecification.hasTransCode(transCode)).and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo)).
                and(ReceiptSpecification.hasInternalNumber(internalNumber)).and(ReceiptSpecification.hasPoCoNo(poCoNo)).and(ReceiptSpecification.hasFromDateToDateRedInvoice(fromDate, toDate)).and(ReceiptSpecification.hasStatus()).and(ReceiptSpecification.hasPoIdIsNotNull()).
                and(ReceiptSpecification.hasTypeImport()).and(ReceiptSpecification.hasGreaterDay(dateTime)),pageable);
        List<PoTransDTO> rs = new ArrayList<>();
        for (PoTrans pt : poTrans){
            List<PoTransDetail> transDetailList = poTransDetailRepository.getPoTransDetailByTransId(pt.getId());
            int sumQuantity = 0;
            int sumReturnAmount = 0;
            for(int i=0;i<transDetailList.size();i++){
                if(transDetailList.get(i).getReturnAmount()==null) throw new ValidateException(ResponseMessage.RETURN_AMOUNT_CAN_NOT_BE_NULL);
                if(transDetailList.get(i).getQuantity()==null) throw new ValidateException(ResponseMessage.QUANTITY_CAN_NOT_BE_NULL);
                sumQuantity+=transDetailList.get(i).getQuantity();
                sumReturnAmount+= transDetailList.get(i).getReturnAmount();
            }
            if(sumReturnAmount< sumQuantity){
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTransDTO dto = modelMapper.map(pt, PoTransDTO.class);
                rs.add(dto);
            }
        }
        List<PoTransDTO> subList;
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), rs.size());
        subList = rs.subList(start, end);
        //////////////////////////////////
        Page<PoTransDTO> pageResponse = new PageImpl<>(subList,pageable,rs.size());
        return pageResponse;
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
    public ResponseMessage createPoTransExport(ReceiptExportCreateRequest request,Long userId, Long shopId) {

        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PoTrans poRecord = modelMapper.map(request, PoTrans.class);
        PoTrans poTrans = repository.findById(request.getReceiptImportId()).get();
        poRecord.setTransDate(LocalDateTime.now());
        poRecord.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
        poRecord.setTransCode(createPoTransExportCode(shopId));
        poRecord.setShopId(shopId);
        poRecord.setOrderDate(poTrans.getOrderDate());
        poRecord.setRedInvoiceNo(poTrans.getRedInvoiceNo());
        poRecord.setPocoNumber(poTrans.getPocoNumber());
        poRecord.setInternalNumber(poTrans.getInternalNumber());
        poRecord.setFromTransId(poTrans.getId());
        poRecord.setIsDebit(false);
        Integer total_quantity =0;
        Double total_amount = 0D;
        poRecord.setType(2);
        poRecord.setStatus(1);
        repository.save(poRecord);
        Set<Long> countNumSKU = new HashSet<>();
        List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
        for (int i = 0; i < poTransDetails.size(); i++) {
            PoTransDetail poTransDetail = new PoTransDetail();
                if (request.getIsRemainAll() == true) {
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
                    total_quantity +=poTransDetails.get(i).getQuantity();
                    total_amount += poTransDetails.get(i).getAmount();
                    poTransDetailRepository.save(poTransDetail);
                    poTransDetails.get(i).setReturnAmount(poTransDetails.get(i).getReturnAmount()+poTransDetail.getQuantity());
                    poTransDetailRepository.save(poTransDetails.get(i));
                    StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(poTransDetails.get(i).getProductId(), customerTypeDTO.getWareHouseTypeId());
                    if (stockTotal == null)
                        throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                    if (stockTotal.getQuantity() == null) {
                        stockTotal.setQuantity(0);
                    }
                    stockTotal.setQuantity(stockTotal.getQuantity() - poTransDetails.get(i).getQuantity());
                    stockTotalRepository.save(stockTotal);
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
                            poTransDetailRepository.save(poTransDetail);
                            total_quantity +=poTransDetail.getQuantity();
                            total_amount += poTransDetail.getAmount();
                            poTransDetails.get(i).setReturnAmount(poTransDetails.get(i).getReturnAmount()+ poTransDetail.getQuantity());
                            poTransDetailRepository.save(poTransDetails.get(i));
                            if(poTransDetails.get(i).getReturnAmount()> poTransDetails.get(i).getQuantity()) throw new ValidateException(ResponseMessage.RETURN_AMOUNT_MUST_BE_LESS_THAN_OR_EQUAL_TO_THE_QUANTITY_ENTERED);
                            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(poTransDetails.get(i).getProductId(), customerTypeDTO.getWareHouseTypeId());
                            if (stockTotal == null)
                                throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                            if (stockTotal.getQuantity() == null) {
                                stockTotal.setQuantity(0);
                            }
                            stockTotalRepository.save(stockTotal);
                        }
                    }
            }
        }
        poRecord.setTotalQuantity(total_quantity);
        poRecord.setTotalAmount(total_amount);
        poRecord.setNumSku(countNumSKU.size());
        repository.save(poRecord);
        return ResponseMessage.CREATED_SUCCESSFUL;
    }
    public ResponseMessage createAdjustmentTrans(ReceiptExportCreateRequest request, Long userId,Long shopId) {

        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        CustomerDTO cus = customerClient.getCusDefault(shopId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        StockAdjustmentTrans poAdjustTrans = modelMapper.map(request, StockAdjustmentTrans.class);
        StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(request.getReceiptImportId()).get();
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
        /*order.setOrderType(1);*/
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
        for(StockAdjustmentDetail sad : sads){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentTransDetail satd = modelMapper.map(sad, StockAdjustmentTransDetail.class);
            Optional<Price> price = productPriceRepository.getByASCCustomerType(sad.getProductId());
            satd.setTransId(poAdjustTrans.getId());
            satd.setShopId(shopId);
            totalQuantity +=sad.getQuantity();
            totalAmount += sad.getPrice()*sad.getQuantity();
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(sad.getProductId(), customerTypeDTO.getWareHouseTypeId());
            if(stockTotal == null)
                throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
            if(stockTotal.getQuantity() == null){
                stockTotal.setQuantity(0);
            }
            stockTotal.setQuantity(stockTotal.getQuantity()- sad.getQuantity());
            stockTotalRepository.save(stockTotal);
            stockAdjustmentTransDetailRepository.save(satd);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            SaleOrderDetail saleOrderDetail = modelMapper.map(sad, SaleOrderDetail.class);
            saleOrderDetail.setSaleOrderId(order.getId());
            saleOrderDetail.setAmount(sad.getPrice()*sad.getQuantity());
            saleOrderDetail.setTotal(sad.getPrice()*sad.getQuantity());
            saleOrderDetail.setIsFreeItem(false);
            saleOrderDetail.setAutoPromotion(0D);
            saleOrderDetail.setZmPromotion(0D);
            saleOrderDetail.setPriceNotVat(price.get().getPriceNotVat());
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
        sendSynRequest(JMSType.stock_adjustment, Arrays.asList(stockAdjustment.getId()));
        sendSynRequest(JMSType.stock_adjustment_trans, Arrays.asList(poAdjustTrans.getId()));
        return ResponseMessage.CREATED_SUCCESSFUL;
    }

    public ResponseMessage createBorrowingTrans(ReceiptExportCreateRequest request, Long userId,Long shopId) {
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        StockBorrowingTrans poBorrowTransRecord = modelMapper.map(request, StockBorrowingTrans.class);
        StockBorrowing stockBorrowing = stockBorrowingRepository.findById(request.getReceiptImportId()).get();
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
        for(StockBorrowingDetail sbd : sbds){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingTransDetail sbtd = modelMapper.map(sbd, StockBorrowingTransDetail.class);
            sbtd.setTransId(poBorrowTransRecord.getId());
            totalAmount+= sbd.getPrice()*sbd.getQuantity();
            totalQuantity += sbd.getQuantity();
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(sbd.getProductId(), customerTypeDTO.getWareHouseTypeId());
            if(stockTotal == null)
                throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
            if(stockTotal.getQuantity() == null){
                stockTotal.setQuantity(0);
            }
            stockTotal.setQuantity(stockTotal.getQuantity()- sbd.getQuantity());

            stockTotalRepository.save(stockTotal);
            stockBorrowingTransDetailRepository.save(sbtd);
        }
        poBorrowTransRecord.setTotalQuantity(totalQuantity);
        poBorrowTransRecord.setTotalAmount(totalAmount);
        poBorrowTransRecord.setNote(request.getNote()==null ? stockBorrowing.getNote() : request.getNote());
        stockBorrowing.setStatusExport(2);
        stockBorrowing = stockBorrowingRepository.save(stockBorrowing);
        if(null != stockBorrowing) {
            sendSynRequest(JMSType.stock_borrowing, Arrays.asList(stockBorrowing.getId()));
        }
        poBorrowTransRecord = stockBorrowingTransRepository.save(poBorrowTransRecord);
        if(null != poBorrowTransRecord) {
            sendSynRequest(JMSType.stock_borrowing_trans, Arrays.asList(poBorrowTransRecord.getId()));
        }
        return ResponseMessage.CREATED_SUCCESSFUL;
    }

    private void sendSynRequest(String type, List<Long> listId) {
        try {
            jmsSender.sendMessage(type, listId);
        } catch (Exception ex) {
            log.error("khoi tao jmsSender", ex);
        }
    }
    
    private void sendSynRequestByCode(String type, List<String> lstCodes) {
        try {
        	if(!lstCodes.isEmpty()) {
        		jmsSender.sendMessageByCode(type, lstCodes);
        	}
        } catch (Exception ex) {
            log.error("Cannot send request", ex);
        }
    }

    public ResponseMessage updatePoTransExport(ReceiptExportUpdateRequest request, Long id) {

        PoTrans poTrans = repository.findById(id).get();
        if (DateUtils.formatDate2StringDate(poTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
            if(poTrans.getFromTransId() == null) throw new ValidateException(ResponseMessage.RECORD_WRONG);
            List<PoTransDetail> poTransDetailImport = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getFromTransId());
            if(poTransDetailImport == null) throw new ValidateException(ResponseMessage.RECORD_WRONG);
            int total = 0;
            double totalAmount = 0D;
            if(!request.getListProductRemain().isEmpty()){
                for (int i=0;i<poTransDetails.size();i++){
                    PoTransDetail poTransDetail = poTransDetails.get(i);
                    for (int j = 0;j<request.getListProductRemain().size();j++){
                        if(poTransDetail.getId().equals(request.getListProductRemain().get(j).getId())){

                            if(poTransDetailImport.get(i).getReturnAmount()>poTransDetailImport.get(i).getQuantity())
                                throw new ValidateException(ResponseMessage.RETURN_AMOUNT_MUST_BE_LESS_THAN_OR_EQUAL_TO_THE_QUANTITY_ENTERED);
                            poTransDetailImport.get(i).setReturnAmount(poTransDetailImport.get(i).getReturnAmount() + (request.getListProductRemain().get(i).getQuantity()-poTransDetail.getQuantity()));
                            StockTotal st = stockTotalRepository.findByProductIdAndWareHouseTypeId(poTransDetail.getProductId(),poTrans.getWareHouseTypeId());
                            if(st == null) throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                            st.setQuantity(st.getQuantity()-poTransDetail.getQuantity() + request.getListProductRemain().get(j).getQuantity());
                            if(st.getQuantity()<0)
                                throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE);
                            poTransDetail.setQuantity(request.getListProductRemain().get(j).getQuantity());
                            total +=poTransDetail.getQuantity();
                            totalAmount+=(poTransDetail.getQuantity() * poTransDetail.getPriceNotVat());
                            stockTotalRepository.save(st);
                            poTransDetailRepository.save(poTransDetail);
                            poTransDetailRepository.save(poTransDetailImport.get(i));
                        }
                    }
                }
            }
            poTrans.setTotalQuantity(total);
            poTrans.setTotalAmount(totalAmount);
            poTrans.setNote(request.getNote());
            repository.save(poTrans);
            return ResponseMessage.UPDATE_SUCCESSFUL;
        }
        else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);
    }
    public ResponseMessage updateAdjustmentTransExport(ReceiptExportUpdateRequest request, Long id) {

        StockAdjustmentTrans adjustmentTrans = stockAdjustmentTransRepository.findById(id).get();
        if (DateUtils.formatDate2StringDate(adjustmentTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            adjustmentTrans.setNote(request.getNote());
            stockAdjustmentTransRepository.save(adjustmentTrans);
            sendSynRequest(JMSType.stock_adjustment_trans, Arrays.asList(adjustmentTrans.getId()));
            return ResponseMessage.UPDATE_SUCCESSFUL;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);
    }
    public ResponseMessage updateBorrowingTransExport(ReceiptExportUpdateRequest request, Long id) {

        Response<StockBorrowingTrans> response = new Response<>();
        StockBorrowingTrans borrowingTrans = stockBorrowingTransRepository.findById(id).get();
        if (DateUtils.formatDate2StringDate(borrowingTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            borrowingTrans.setNote(request.getNote());
            borrowingTrans = stockBorrowingTransRepository.save(borrowingTrans);
            sendSynRequest(JMSType.stock_borrowing_trans, Arrays.asList(borrowingTrans.getId()));
            return ResponseMessage.UPDATE_SUCCESSFUL;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);
    }
    public ResponseMessage removePoTransExport(Long id) {
        PoTrans poTrans = repository.findById(id).get();
        if(DateUtils.formatDate2StringDate(poTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))){
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
            if(poTransDetails == null) throw new ValidateException(ResponseMessage.PO_TRANS_DETAIL_IS_NOT_EXISTED);
            List<PoTransDetail> poTransDetailsImport = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getFromTransId());
            if(poTransDetailsImport == null) throw new ValidateException(ResponseMessage.PO_TRANS_DETAIL_IS_NOT_EXISTED);
            for (int i=0;i<poTransDetails.size();i++){
                PoTransDetail ptd = poTransDetails.get(i);
                poTransDetailsImport.get(i).setReturnAmount(poTransDetailsImport.get(i).getReturnAmount()-ptd.getQuantity());
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(ptd.getProductId(),poTrans.getWareHouseTypeId());
                if(stockTotal == null )throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                stockTotal.setQuantity(stockTotal.getQuantity()+ptd.getQuantity());
                stockTotalRepository.save(stockTotal);
                poTransDetailRepository.save( poTransDetailsImport.get(i));
            }
            poTrans.setStatus(-1);
            repository.save(poTrans);

            return ResponseMessage.DELETE_SUCCESSFUL;
        }else throw  new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }
    public ResponseMessage removeStockAdjustmentTransExport(Long id) {
        Optional<StockAdjustmentTrans> stockAdjustmentTrans = stockAdjustmentTransRepository.findById(id);
        if(!stockAdjustmentTrans.isPresent()) throw new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);
        if(DateUtils.formatDate2StringDate(stockAdjustmentTrans.get().getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))){
            List<StockAdjustmentTransDetail> stockAdjustmentTransDetails = stockAdjustmentTransDetailRepository.getStockAdjustmentTransDetailsByTransId(stockAdjustmentTrans.get().getId());
            for (StockAdjustmentTransDetail satd :stockAdjustmentTransDetails ){
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(satd.getProductId(),stockAdjustmentTrans.get().getWareHouseTypeId());
                if(stockTotal== null )throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                stockTotal.setQuantity(stockTotal.getQuantity()+satd.getQuantity());
                stockTotalRepository.save(stockTotal);
            }
            SaleOrder order = saleOrderRepository.getSaleOrderByOrderNumber(stockAdjustmentTrans.get().getRedInvoiceNo()).orElseThrow(() -> new ValidateException(ResponseMessage.SALE_ORDER_NOT_FOUND));
            String orderNumber = order.getOrderNumber();
            List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findSaleOrderDetail(order.getId(), null);
            saleOrderDetailRepository.deleteAll(saleOrderDetails);
            saleOrderRepository.delete(order);
            stockAdjustmentTrans.get().setStatus(-1);
            StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(stockAdjustmentTrans.get().getAdjustmentId()).get();
            stockAdjustment.setStatus(1);
            stockAdjustment = stockAdjustmentRepository.save(stockAdjustment);
            StockAdjustmentTrans stockAdjustmentTransResult = stockAdjustmentTransRepository.save(stockAdjustmentTrans.get());
            sendSynRequest(JMSType.stock_adjustment, Arrays.asList(stockAdjustment.getId()));
            sendSynRequest(JMSType.stock_adjustment_trans, Arrays.asList(stockAdjustmentTransResult.getId()));
            sendSynRequestByCode(JMSType.sale_orders_adjustment, Arrays.asList(orderNumber));
            return ResponseMessage.DELETE_SUCCESSFUL;
        }else throw  new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }
    public ResponseMessage removeStockBorrowingTransExport(Long id) {
        StockBorrowingTrans stockBorrowingTrans = stockBorrowingTransRepository.getStockBorrowingTransById(id);
        if(DateUtils.formatDate2StringDate(stockBorrowingTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))){
            List<StockBorrowingTransDetail> stockBorrowingTransDetails = stockBorrowingTransDetailRepository.getStockBorrowingTransDetailByTransId(stockBorrowingTrans.getId());
            for (StockBorrowingTransDetail sbtd :stockBorrowingTransDetails ){
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(sbtd.getProductId(),stockBorrowingTrans.getWareHouseTypeId());
                if(stockTotal == null) throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                stockTotal.setQuantity(stockTotal.getQuantity() + sbtd.getQuantity());
                stockTotalRepository.save(stockTotal);
            }
            stockBorrowingTrans.setStatus(-1);
            StockBorrowing sb = stockBorrowingRepository.findById(stockBorrowingTrans.getStockBorrowingId()).get();
            sb.setStatusExport(1);
            sb = stockBorrowingRepository.save(sb);
            if(null != sb) {
                sendSynRequest(JMSType.stock_borrowing, Arrays.asList(sb.getId()));
            }
            stockBorrowingTrans = stockBorrowingTransRepository.save(stockBorrowingTrans);
            if(null != stockBorrowingTrans) {
                sendSynRequest(JMSType.stock_borrowing_trans, Arrays.asList(stockBorrowingTrans.getId()));
            }
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
