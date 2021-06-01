package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
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
import vn.viettel.sale.service.feign.ApparamClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.ReceiptSpecification;
import vn.viettel.sale.util.CreateCodeUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

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
    @Override
    public CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> find(String redInvoiceNo, Date fromDate, Date toDate, Integer type, Long shopId, Pageable pageable) {
        int totalQuantity = 0;
        Double totalPrice = 0D;
        if(type == null){
            if (redInvoiceNo!=null) redInvoiceNo = redInvoiceNo.toUpperCase();
            List<PoTrans> list1 = repository.findAll(Specification.where(ReceiptSpecification.hasStatus()).and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDate(fromDate, toDate)).and(ReceiptSpecification.hasTypeExport()).and(ReceiptSpecification.hasShopId(shopId)));
            List<StockAdjustmentTrans> list2 = stockAdjustmentTransRepository.findAll(Specification.where(ReceiptSpecification.hasStatusA().and(ReceiptSpecification.hasRedInvoiceNoA(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateA(fromDate, toDate)).and(ReceiptSpecification.hasTypeExportA())).and(ReceiptSpecification.hasShopIdA(shopId)));
            List<StockBorrowingTrans> list3 = stockBorrowingTransRepository.findAll(Specification.where(ReceiptSpecification.hasStatusB()).and(ReceiptSpecification.hasRedInvoiceNoB(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateB(fromDate, toDate)).and(ReceiptSpecification.hasTypeExportB()).and(ReceiptSpecification.hasFromShopId(shopId)));
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
            TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), result.size());
            subList = result.subList(start, end);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(subList,pageable,result.size());
            CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> response = new CoverResponse(pageResponse, totalResponse);
            return response;
        }else if(type == 0){
            if (redInvoiceNo!=null) redInvoiceNo = redInvoiceNo.toUpperCase();
            Page<PoTrans> list1 = repository.findAll(Specification.where(ReceiptSpecification.hasStatus()).and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDate(fromDate, toDate)).and(ReceiptSpecification.hasTypeExport()).and(ReceiptSpecification.hasShopId(shopId)),pageable);
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
            TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), listAddDTO1.size());
            subList = listAddDTO1.subList(start, end);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(subList,pageable,listAddDTO1.size());
            CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> response = new CoverResponse(pageResponse, totalResponse);
            return response;
        }else if(type == 1){
            if (redInvoiceNo!=null) redInvoiceNo = redInvoiceNo.toUpperCase();
            Page<StockAdjustmentTrans> list2 = stockAdjustmentTransRepository.findAll(Specification.where(ReceiptSpecification.hasStatusA().and(ReceiptSpecification.hasRedInvoiceNoA(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateA(fromDate, toDate)).and(ReceiptSpecification.hasTypeExportA())).and(ReceiptSpecification.hasShopIdA(shopId)),pageable);
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
            Page<StockBorrowingTrans> list3 = stockBorrowingTransRepository.findAll(Specification.where(ReceiptSpecification.hasStatusB()).and(ReceiptSpecification.hasRedInvoiceNoB(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateB(fromDate, toDate)).and(ReceiptSpecification.hasTypeExportB()).and(ReceiptSpecification.hasFromShopId(shopId)),pageable);
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
    public Page<PoTransDTO> getListPoTrans(String transCode, String redInvoiceNo, String internalNumber, String poNo, Date fromDate, Date toDate, Pageable pageable) {
        if (redInvoiceNo!=null) redInvoiceNo = redInvoiceNo.toUpperCase();
        Page<PoTrans> poTrans = repository.findAll(Specification.where(ReceiptSpecification.hasTransCode(transCode)).and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo)).
                and(ReceiptSpecification.hasInternalNumber(internalNumber)).and(ReceiptSpecification.hasPoNo(poNo)).and(ReceiptSpecification.hasFromDateToDate(fromDate, toDate)).and(ReceiptSpecification.hasStatus()).and(ReceiptSpecification.hasPoIdIsNull()),pageable);
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
            if(sumQuantity != sumReturnAmount){
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTransDTO dto = modelMapper.map(pt, PoTransDTO.class);
                rs.add(dto);
            }
        }
        Page<PoTransDTO> pageResponse = new PageImpl<>(rs);
        return pageResponse;
    }
    @Override
    public List<StockAdjustmentDTO> getListStockAdjustment(Pageable pageable) {
        List<StockAdjustment> stockAdjustments = stockAdjustmentRepository.getStockAdjustmentExport();
        List<StockAdjustmentDTO> rs = new ArrayList<>();
        for (StockAdjustment sa : stockAdjustments) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentDTO dto = modelMapper.map(sa, StockAdjustmentDTO.class);
            rs.add(dto);
        }
        return rs;
    }

    @Override
    public List<StockBorrowingDTO> getListStockBorrowing(Long shopId,Pageable pageable) {
        List<StockBorrowing> stockBorrowings = stockBorrowingRepository.getStockBorrowingExport(shopId);
        List<StockBorrowingDTO> rs = new ArrayList<>();
        for (StockBorrowing sb : stockBorrowings) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingDTO dto = modelMapper.map(sb, StockBorrowingDTO.class);
            rs.add(dto);
        }
        return rs;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    public ResponseMessage createPoTransExport(ReceiptExportCreateRequest request, Long userId,Long shopId) {
        Date date = new Date();
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PoTrans poRecord = modelMapper.map(request, PoTrans.class);
        PoTrans poTrans = repository.findById(request.getReceiptImportId()).get();
        poRecord.setTransDate(date);
        poRecord.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
        poRecord.setTransCode(createPoTransExportCode(shopId));
        poRecord.setShopId(shopId);
        poRecord.setOrderDate(poTrans.getOrderDate());
        poRecord.setRedInvoiceNo(poTrans.getRedInvoiceNo());
        poRecord.setPocoNumber(poTrans.getPocoNumber());
        poRecord.setInternalNumber(poTrans.getInternalNumber());
        poRecord.setFromTransId(poTrans.getId());
        Integer total_quantity =0;
        Double total_amount = 0D;
        poRecord.setType(2);
        poRecord.setStatus(1);
        repository.save(poRecord);
        List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
        for (int i = 0; i < poTransDetails.size(); i++) {
            PoTransDetail poTransDetail = new PoTransDetail();
                if (request.getIsRemainAll() == true) {
                    poTransDetail.setTransId(poRecord.getId());
                    poTransDetail.setProductId(poTransDetails.get(i).getProductId());
                    poTransDetail.setQuantity(poTransDetails.get(i).getQuantity());
                    poTransDetail.setPrice(poTransDetails.get(i).getPrice());
                    poTransDetail.setPriceNotVat(poTransDetails.get(i).getPriceNotVat());
                    poTransDetail.setAmountNotVat(poTransDetails.get(i).getAmountNotVat());
                    total_quantity +=poTransDetails.get(i).getQuantity();
                    total_amount += poTransDetails.get(i).getAmount();
                    poTransDetailRepository.save(poTransDetail);
                    poTransDetail.setReturnAmount(poTransDetail.getQuantity());
                    poTransDetailRepository.save(poTransDetail);
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
                            if(poTransDetails.get(i).getQuantity()==null) throw new ValidateException(ResponseMessage.QUANTITY_CAN_NOT_BE_NULL);
                            if(poTransDetails.get(i).getAmount()==null) throw new ValidateException(ResponseMessage.AMOUNT_CAN_NOT_BE_NULL);
                            poTransDetail.setTransId(poRecord.getId());
                            poTransDetail.setProductId(poTransDetails.get(i).getProductId());
                            poTransDetail.setQuantity(request.getLitQuantityRemain().get(j).getQuantity());
                            poTransDetail.setPrice(poTransDetails.get(i).getPrice());
                            poTransDetail.setPriceNotVat(poTransDetails.get(i).getPriceNotVat());
                            poTransDetail.setAmountNotVat(poTransDetails.get(i).getAmountNotVat());
                            poTransDetail.setShopId(shopId);
                            poTransDetail.setAmount(request.getLitQuantityRemain().get(j).getQuantity()*poTransDetails.get(i).getPrice());
                            poTransDetailRepository.save(poTransDetail);
                            total_quantity +=poTransDetails.get(i).getQuantity();
                            total_amount += poTransDetails.get(i).getAmount();
                            poTransDetail.setReturnAmount(poTransDetail.getQuantity());
                            poTransDetailRepository.save(poTransDetail);
                            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(poTransDetails.get(i).getProductId(), customerTypeDTO.getWareHouseTypeId());
                            if (stockTotal == null)
                                throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                            if (stockTotal.getQuantity() == null) {
                                stockTotal.setQuantity(0);
                            }
                            stockTotalRepository.save(stockTotal);
                        }else throw new ValidateException(ResponseMessage.RECORD_DOES_NOT_EXISTS);
                    }
            }
        }
        poRecord.setTotalQuantity(total_quantity);
        poRecord.setTotalAmount(total_amount);
        repository.save(poRecord);
        return ResponseMessage.CREATED_SUCCESSFUL;
    }
    public ResponseMessage createAdjustmentTrans(ReceiptExportCreateRequest request, Long userId,Long shopId) {
        Date date = new Date();
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        StockAdjustmentTrans poAdjustTrans = modelMapper.map(request, StockAdjustmentTrans.class);
        StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(request.getReceiptImportId()).get();
        ApParamDTO reason = apparamClient.getReasonV1(stockAdjustment.getReasonId());
        poAdjustTrans.setTransDate(date);
        poAdjustTrans.setTransCode(createStockAdjustmentExportCode(shopId));
        poAdjustTrans.setShopId(shopId);
        poAdjustTrans.setRedInvoiceNo(createStockAdjustmentExportRedInvoice(shopId));
        poAdjustTrans.setAdjustmentDate(date);
        poAdjustTrans.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
        poAdjustTrans.setOrderDate(stockAdjustment.getAdjustmentDate());
        poAdjustTrans.setInternalNumber(createPoTransCode(shopId));
        poAdjustTrans.setAdjustmentId(stockAdjustment.getId());
        poAdjustTrans.setType(2);
        poAdjustTrans.setStatus(1);
        poAdjustTrans.setNote(reason.getApParamName());
        stockAdjustmentTransRepository.save(poAdjustTrans);
        SaleOrder order = new SaleOrder();
        order.setType(4);
        order.setOrderNumber(createStockAdjustmentExportRedInvoice(shopId));
        order.setOrderDate(date);
        saleOrderRepository.save(order);
        List<StockAdjustmentDetail> sads = stockAdjustmentDetailRepository.getStockAdjustmentDetailByAdjustmentId(stockAdjustment.getId());
        Integer totalQuantity =0;
        Double totalAmount = 0D;
        for(StockAdjustmentDetail sad : sads){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentTransDetail satd = modelMapper.map(sad, StockAdjustmentTransDetail.class);
            satd.setTransId(poAdjustTrans.getId());
            totalAmount+=sad.getQuantity();
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
            saleOrderDetailRepository.save(saleOrderDetail);
        }
        poAdjustTrans.setTotalQuantity(totalQuantity);
        poAdjustTrans.setTotalAmount(totalAmount);
        poAdjustTrans.setNote(stockAdjustment.getDescription());
        stockAdjustment.setStatus(3);
        stockAdjustmentRepository.save(stockAdjustment);
        stockAdjustmentTransRepository.save(poAdjustTrans);
        return ResponseMessage.CREATED_SUCCESSFUL;
    }

    public ResponseMessage createBorrowingTrans(ReceiptExportCreateRequest request, Long userId,Long shopId) {
        Date date = new Date();
        UserDTO user = userClient.getUserByIdV1(userId);
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        StockBorrowingTrans poBorrowTransRecord = modelMapper.map(request, StockBorrowingTrans.class);
        StockBorrowing stockBorrowing = stockBorrowingRepository.findById(request.getReceiptImportId()).get();
        poBorrowTransRecord.setTransDate(date);
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
        poBorrowTransRecord.setNote(stockBorrowing.getNote());
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
        stockBorrowing.setStatusExport(2);
        stockBorrowingRepository.save(stockBorrowing);
        stockBorrowingTransRepository.save(poBorrowTransRecord);
        return ResponseMessage.CREATED_SUCCESSFUL;
    }
    public ResponseMessage updatePoTransExport(ReceiptExportUpdateRequest request, Long id) {
        Date date = new Date();
        PoTrans poTrans = repository.findById(id).get();
        if (formatDate(poTrans.getTransDate()).equals(formatDate(date))) {
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
            if(!request.getListProductRemain().isEmpty()){
                for (int i=0;i<poTransDetails.size();i++){
                    PoTransDetail poTransDetail = poTransDetails.get(i);
                    for (int j = 0;j<request.getListProductRemain().size();j++){
                        if(poTransDetail.getId()==request.getListProductRemain().get(j).getId()){
                            StockTotal st = stockTotalRepository.findByProductIdAndWareHouseTypeId(poTransDetail.getProductId(),poTrans.getWareHouseTypeId());
                            if(st == null) throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                            st.setQuantity(st.getQuantity()-poTransDetail.getQuantity() + request.getListProductRemain().get(j).getQuantity());
                            if(st.getQuantity()<0)
                                throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE);
                            poTransDetail.setQuantity(request.getListProductRemain().get(j).getQuantity());
                            stockTotalRepository.save(st);
                            poTransDetailRepository.save(poTransDetail);
                        }
                    }
                }
            }
            poTrans.setNote(request.getNote());
            repository.save(poTrans);
            return ResponseMessage.UPDATE_SUCCESSFUL;
        }
        else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);

    }
    public ResponseMessage updateAdjustmentTransExport(ReceiptExportUpdateRequest request, Long id) {
        Date date = new Date();
        Response<StockAdjustmentTrans> response = new Response<>();
        StockAdjustmentTrans adjustmentTrans = stockAdjustmentTransRepository.findById(id).get();
        if (formatDate(adjustmentTrans.getTransDate()).equals(formatDate(date))) {
            adjustmentTrans.setNote(request.getNote());
            stockAdjustmentTransRepository.save(adjustmentTrans);
            return ResponseMessage.UPDATE_SUCCESSFUL;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);
    }
    public ResponseMessage updateBorrowingTransExport(ReceiptExportUpdateRequest request, Long id) {
        Date date = new Date();
        Response<StockBorrowingTrans> response = new Response<>();
        StockBorrowingTrans borrowingTrans = stockBorrowingTransRepository.findById(id).get();
        if (formatDate(borrowingTrans.getTransDate()).equals(formatDate(date))) {
            borrowingTrans.setNote(request.getNote());
            stockBorrowingTransRepository.save(borrowingTrans);
            return ResponseMessage.UPDATE_SUCCESSFUL;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);
    }
    public ResponseMessage removePoTransExport(Long id) {
        Date date = new Date();
        PoTrans poTrans = repository.findById(id).get();
        if(formatDate(poTrans.getTransDate()).equals(formatDate(date))){
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
            for (PoTransDetail ptd :poTransDetails ){
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(ptd.getProductId(),poTrans.getWareHouseTypeId());
                if(stockTotal == null )throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                stockTotal.setQuantity(stockTotal.getQuantity()+ptd.getQuantity());
                stockTotalRepository.save(stockTotal);
            }
            poTrans.setStatus(-1);
            repository.save(poTrans);

            return ResponseMessage.DELETE_SUCCESSFUL;
        }else throw  new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }
    public ResponseMessage removeStockAdjustmentTransExport(Long id) {
        Date date = new Date();
        Optional<StockAdjustmentTrans> stockAdjustmentTrans = stockAdjustmentTransRepository.findById(id);
        if(!stockAdjustmentTrans.isPresent()) throw new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);
        if(formatDate(stockAdjustmentTrans.get().getTransDate()).equals(formatDate(date))){
            List<StockAdjustmentTransDetail> stockAdjustmentTransDetails = stockAdjustmentTransDetailRepository.getStockAdjustmentTransDetailsByTransId(stockAdjustmentTrans.get().getId());
            for (StockAdjustmentTransDetail satd :stockAdjustmentTransDetails ){
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(satd.getProductId(),stockAdjustmentTrans.get().getWareHouseTypeId());
                if(stockTotal== null )throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                stockTotal.setQuantity(stockTotal.getQuantity()+satd.getQuantity());
                stockTotalRepository.save(stockTotal);
            }
            stockAdjustmentTrans.get().setStatus(-1);
            stockAdjustmentTransRepository.save(stockAdjustmentTrans.get());

            return ResponseMessage.DELETE_SUCCESSFUL;
        }else throw  new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }
    public ResponseMessage removeStockBorrowingTransExport(Long id) {
        Date date = new Date();
        Response<String> response = new Response<>();
        StockBorrowingTrans stockBorrowingTrans = stockBorrowingTransRepository.getStockBorrowingTransById(id);
        if(formatDate(stockBorrowingTrans.getTransDate()).equals(formatDate(date))){
            List<StockBorrowingTransDetail> stockBorrowingTransDetails = stockBorrowingTransDetailRepository.getStockBorrowingTransDetailByTransId(stockBorrowingTrans.getId());
            for (StockBorrowingTransDetail sbtd :stockBorrowingTransDetails ){
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(sbtd.getProductId(),stockBorrowingTrans.getWareHouseTypeId());
                if(stockTotal == null) throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                stockTotal.setQuantity(stockTotal.getQuantity() + sbtd.getQuantity());
                stockTotalRepository.save(stockTotal);
            }
            StockBorrowing sb = stockBorrowingRepository.findById(stockBorrowingTrans.getStockBorrowingId()).get();
            sb.setStatusExport(1);
            stockBorrowingRepository.save(sb);
            stockBorrowingTransRepository.delete(stockBorrowingTrans);
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
        Integer mm = currentDate.getMonthValue();
        Integer dd = currentDate.getDayOfMonth();
        int reciNum = stockAdjustmentTransRepository.getQuantityStockAdjustTransExport();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("SAL.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(yy);
        reciCode.append(mm.toString());
        reciCode.append(dd.toString());
        reciCode.append(CreateCodeUtils.formatReceINumber(reciNum));
        return reciCode.toString();
    }
    public  String createStockBorrowTransCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        int reciNum = stockBorrowingTransRepository.getQuantityStockBorrowingTransExport();
        String reciCode = "EXSB." +
                shopClient.getByIdV1(idShop).getData().getShopCode() +
                "." +
                yy +
                "." +
                CreateCodeUtils.formatReceINumber(reciNum);
        return reciCode;
    }
    public  String createStockBorrowTransExportRedInvoice(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        LocalDate currentDate = LocalDate.now();
        String yy = df.format(Calendar.getInstance().getTime());
        Integer mm = currentDate.getMonthValue();
        Integer dd = currentDate.getDayOfMonth();
        int reciNum = stockAdjustmentTransRepository.getQuantityStockAdjustTransExport();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXP_");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append("_");
        reciCode.append(yy);
        reciCode.append(mm.toString());
        reciCode.append(dd.toString());
        reciCode.append("_");
        reciCode.append(CreateCodeUtils.formatReceINumberVer2(reciNum));
        return reciCode.toString();
    }
    public  String createPoTransCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        String code = repository.getQuantityPoTrans();
        int reciNum = Integer.valueOf(code.split(".")[3]);
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("IMP.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(CreateCodeUtils.formatReceINumber(reciNum));
        return reciCode.toString();
    }
}
