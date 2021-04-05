package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.stock.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.messaging.ReceiptExportCreateRequest;
import vn.viettel.sale.messaging.ReceiptExportUpdateRequest;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ReceiptExportService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.ReceiptSpecification;
import vn.viettel.sale.util.CreateCodeUtils;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReceiptExportServiceImpl extends BaseServiceImpl<PoTrans, PoTransRepository> implements ReceiptExportService {
    @Autowired
    ShopRepository shopRepository;
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
    Date date = new Date();
    Timestamp ts = new Timestamp(date.getTime());

    @Override
    public Response<Page<ReceiptImportListDTO>> index(String redInvoiceNo, Date fromDate, Date toDate, Integer type, Pageable pageable) {
        if(type == null){
            Page<PoTrans> list1 = repository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNull()).and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDate(fromDate, toDate)).and(ReceiptSpecification.hasTypeExport()),pageable);
            Page<StockAdjustmentTrans> list2 = stockAdjustmentTransRepository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNullA().and(ReceiptSpecification.hasRedInvoiceNoA(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateA(fromDate, toDate)).and(ReceiptSpecification.hasTypeExportA())),pageable);
            Page<StockBorrowingTrans> list3 = stockBorrowingTransRepository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNullB()).and(ReceiptSpecification.hasRedInvoiceNoB(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateB(fromDate, toDate)).and(ReceiptSpecification.hasTypeExportB()),pageable);
            List<PoTransDTO> listAddDTO1 = new ArrayList<>();
            for(PoTrans poTrans : list1){
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTransDTO poRecord = modelMapper.map(poTrans, PoTransDTO.class);
                poRecord.setReceiptType(0);
                listAddDTO1.add(poRecord);
            }
            Page<PoTransDTO> page1 = new PageImpl<>(listAddDTO1);
            List<ReceiptImportListDTO> listDTO1 = page1.getContent().stream().map(
                    item -> modelMapper.map(item, ReceiptImportListDTO.class)
            ).collect(Collectors.toList());

            List<StockAdjustmentTransDTO> listAddDTO2 = new ArrayList<>();
            for(StockAdjustmentTrans stockAdjustmentTrans : list2){
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                StockAdjustmentTransDTO poARecord = modelMapper.map(stockAdjustmentTrans, StockAdjustmentTransDTO.class);
                poARecord.setReceiptType(1);
                listAddDTO2.add(poARecord);
            }
            Page<StockAdjustmentTransDTO> page2 = new PageImpl<>(listAddDTO2);

            List<ReceiptImportListDTO> listDTO2 = page2.getContent().stream().map(
                    item -> modelMapper.map(item, ReceiptImportListDTO.class)
            ).collect(Collectors.toList());

            List<StockBorrowingTransDTO> listAddDTO3 = new ArrayList<>();
            for(StockBorrowingTrans stockBorrowingTrans : list3){
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                StockBorrowingTransDTO poBRecord = modelMapper.map(stockBorrowingTrans, StockBorrowingTransDTO.class);
                poBRecord.setReceiptType(2);
                listAddDTO3.add(poBRecord);
            }
            Page<StockBorrowingTransDTO> page3 = new PageImpl<>(listAddDTO3);

            List<ReceiptImportListDTO> listDTO3 = page3.getContent().stream().map(
                    item -> modelMapper.map(item, ReceiptImportListDTO.class)
            ).collect(Collectors.toList());
            List<ReceiptImportListDTO> result = new ArrayList<>();
            result.addAll(listDTO1);
            result.addAll(listDTO2);
            result.addAll(listDTO3);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(result);
            Response<Page<ReceiptImportListDTO>> response = new Response<>();
            return response.withData(pageResponse);
        }else if(type == 0){
            Page<PoTrans> list1 = repository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNull()).and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDate(fromDate, toDate)).and(ReceiptSpecification.hasTypeExport()),pageable);
            List<PoTransDTO> listAddDTO1 = new ArrayList<>();
            for(PoTrans poTrans : list1){
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTransDTO poRecord = modelMapper.map(poTrans, PoTransDTO.class);
                poRecord.setReceiptType(0);
                listAddDTO1.add(poRecord);
            }
            Page<PoTransDTO> page1 = new PageImpl<>(listAddDTO1);
            List<ReceiptImportListDTO> listDTO1 = page1.getContent().stream().map(
                    item -> modelMapper.map(item, ReceiptImportListDTO.class)
            ).collect(Collectors.toList());
            List<ReceiptImportListDTO> result = new ArrayList<>();
            result.addAll(listDTO1);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(result);
            Response<Page<ReceiptImportListDTO>> response = new Response<>();
            return response.withData(pageResponse);
        }else if(type == 1){
            Page<StockAdjustmentTrans> list2 = stockAdjustmentTransRepository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNullA().and(ReceiptSpecification.hasRedInvoiceNoA(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateA(fromDate, toDate)).and(ReceiptSpecification.hasTypeExportA())),pageable);
            List<StockAdjustmentTransDTO> listAddDTO2 = new ArrayList<>();
            for(StockAdjustmentTrans stockAdjustmentTrans : list2){
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                StockAdjustmentTransDTO poARecord = modelMapper.map(stockAdjustmentTrans, StockAdjustmentTransDTO.class);
                poARecord.setReceiptType(1);
                listAddDTO2.add(poARecord);
            }
            Page<StockAdjustmentTransDTO> page2 = new PageImpl<>(listAddDTO2);
            List<ReceiptImportListDTO> listDTO2 = page2.getContent().stream().map(
                    item -> modelMapper.map(item, ReceiptImportListDTO.class)
            ).collect(Collectors.toList());
            List<ReceiptImportListDTO> result = new ArrayList<>();
            result.addAll(listDTO2);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(result);
            Response<Page<ReceiptImportListDTO>> response = new Response<>();
            return response.withData(pageResponse);
        }else if (type == 2){
            Page<StockBorrowingTrans> list3 = stockBorrowingTransRepository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNullB()).and(ReceiptSpecification.hasRedInvoiceNoB(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateB(fromDate, toDate)).and(ReceiptSpecification.hasTypeExportB()),pageable);
            List<StockBorrowingTransDTO> listAddDTO3 = new ArrayList<>();
            for(StockBorrowingTrans stockBorrowingTrans : list3){
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                StockBorrowingTransDTO poBRecord = modelMapper.map(stockBorrowingTrans, StockBorrowingTransDTO.class);
                poBRecord.setReceiptType(2);
                listAddDTO3.add(poBRecord);
            }
            Page<StockBorrowingTransDTO> page3 = new PageImpl<>(listAddDTO3);
            List<ReceiptImportListDTO> listDTO3 = page3.getContent().stream().map(
                    item -> modelMapper.map(item, ReceiptImportListDTO.class)
            ).collect(Collectors.toList());
            List<ReceiptImportListDTO> result = new ArrayList<>();
            result.addAll(listDTO3);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(result);
            Response<Page<ReceiptImportListDTO>> response = new Response<>();
            return response.withData(pageResponse);
        }
        return null;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<String> createReceipt(ReceiptExportCreateRequest request, Long userId) {
        Response<String> response = new Response<>();
        switch (request.getType()){
            case 0:
                try{
                    createPoTransExport(request,userId);
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
                break;
            case 1:
                try {
                    createAdjustmentTrans(request,userId);
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
                break;
            case 2:
                try {
                    createBorrowingTrans(request,userId);
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
                break;
        }
        return response.withData(ResponseMessage.SUCCESSFUL.toString());
    }

    @Override
    public Response<String> updateReceiptExport(ReceiptExportUpdateRequest request, Long id) {
        Response<String> response = new Response<>();
        switch (request.getType()){
            case 0:
                try{
                    updatePoTransExport(request,id);
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
                break;
            case 1:
                try {
                    updateStockAdjustmentTransExport(request,id);
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
                break;
            case 2:
                try {
                    updateStockBorrowingTransExport(request,id);
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
                break;
        }
        return response.withData(ResponseMessage.SUCCESSFUL.toString());
    }

    @Override
    public Response<String> removeReceiptExport(ReceiptExportUpdateRequest request, Long id) {
        Response<String> response = new Response<>();
        switch (request.getType()){
            case 0:
                try{
                    removePoTransExport(id);
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
                break;
            case 1:
                try {
                    removeStockAdjustmentTransExport(id);
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
                break;
            case 2:
                try {
                    removeStockBorrowingTransExport(id);
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
                break;
        }
        return response.withData(ResponseMessage.SUCCESSFUL.toString());
    }


    @Override
    public Response<Page<PoTransDTO>> getListPoTrans(String transCode,String redInvoiceNo,String internalNumber,String poNo,Date fromDate,Date toDate, Pageable pageable) {
        Response<Page<PoTransDTO>> response = new Response<>();
        if (fromDate == null || toDate == null) {
            LocalDate initial = LocalDate.now();
            fromDate = Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            toDate = Date.from(initial.withDayOfMonth(initial.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        Page<PoTrans> poTrans = repository.findAll(Specification.where(ReceiptSpecification.hasTransCode(transCode)).and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo)).
                and(ReceiptSpecification.hasInternalNumber(internalNumber)).and(ReceiptSpecification.hasPoNo(poNo)).and(ReceiptSpecification.hasFromDateToDate(fromDate, toDate)).and(ReceiptSpecification.hasDeletedAtIsNull()).and(ReceiptSpecification.hasPoIdIsNull()),pageable);
        List<PoTransDTO> rs = new ArrayList<>();
        for (PoTrans pt : poTrans){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoTransDTO dto = modelMapper.map(pt, PoTransDTO.class);
            rs.add(dto);
        }
        Page<PoTransDTO> pageResponse = new PageImpl<>(rs);

        return response.withData(pageResponse);
    }






    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    public PoTrans createPoTransExport(ReceiptExportCreateRequest request, Long userId) {
        Response<PoTrans> response = new Response<>();
        User user = userClient.getUserById(userId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PoTrans poRecord = modelMapper.map(request, PoTrans.class);
        PoTrans poTrans = repository.findById(request.getReceiptImportId()).get();
        poRecord.setTransDate(date);
        poRecord.setTransCode(CreateCodeUtils.createPoTransExportCode(request.getShopId()));
        poRecord.setOrderDate(poTrans.getOrderDate());
        poRecord.setRedInvoiceNo(poTrans.getRedInvoiceNo());
        poRecord.setPoNumber(poTrans.getPoNumber());
        poRecord.setInternalNumber(poTrans.getInternalNumber());
        poRecord.setFromTransId(poTrans.getId());
        poRecord.setCreateUser(user.getUserAccount());
        poRecord.setType(2);
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
                poTransDetailRepository.save(poTransDetail);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(poTransDetails.get(i).getProductId(), request.getWareHouseTypeId());
                if (stockTotal == null)
                    response.setFailure(ResponseMessage.NO_CONTENT);
                if (stockTotal.getQuantity() == null) {
                    stockTotal.setQuantity(0);
                }
                stockTotal.setQuantity(stockTotal.getQuantity() - poTransDetails.get(i).getQuantity());
                stockTotalRepository.save(stockTotal);
            } else {
                for (int j = 0; j < request.getLitQuantityRemain().size(); j++) {
                    poTransDetail.setTransId(poRecord.getId());
                    poTransDetail.setProductId(poTransDetails.get(i).getProductId());
                    poTransDetail.setQuantity(request.getLitQuantityRemain().get(j));
                    poTransDetail.setPrice(poTransDetails.get(i).getPrice());
                    poTransDetail.setPriceNotVat(poTransDetails.get(i).getPriceNotVat());
                    poTransDetail.setAmountNotVat(poTransDetails.get(i).getAmountNotVat());
                    poTransDetailRepository.save(poTransDetail);
                    StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(poTransDetails.get(j).getProductId(), request.getWareHouseTypeId());
                    if (stockTotal == null)
                        response.setFailure(ResponseMessage.NO_CONTENT);
                    if (stockTotal.getQuantity() == null) {
                        stockTotal.setQuantity(0);
                    }
                    stockTotalRepository.save(stockTotal);
                }
            }
        }
        // THIẾU SET STATUS: GẶP ISSUE ĐÃ HỎI BA VÀ ĐANG CHỜ TRẢ Lời
        repository.save(poRecord);
        return response.withData(poRecord).getData();
    }

    public StockAdjustmentTrans createAdjustmentTrans(ReceiptExportCreateRequest request, Long userId) {
        Response<StockAdjustmentTrans> response = new Response<>();
        User user = userClient.getUserById(userId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        StockAdjustmentTrans poAdjustTrans = modelMapper.map(request, StockAdjustmentTrans.class);
        StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(request.getReceiptImportId()).get();
        poAdjustTrans.setTransDate(date);
        poAdjustTrans.setTransCode(CreateCodeUtils.createStockAdjustmentExportCode(request.getShopId()));
        poAdjustTrans.setRedInvoiceNo(CreateCodeUtils.createStockAdjustmentExportRedInvoice(request.getShopId()));
        poAdjustTrans.setAdjustmentDate(date);
        poAdjustTrans.setOrderDate(stockAdjustment.getAdjustmentDate());
        poAdjustTrans.setInternalNumber(CreateCodeUtils.createPoTransCode(request.getShopId()));
        //Po no để trống
        poAdjustTrans.setAdjustmentId(stockAdjustment.getId());
        poAdjustTrans.setCreateUser(user.getUserAccount());
        poAdjustTrans.setType(2);
        List<StockAdjustmentDetail> sads = stockAdjustmentDetailRepository.getStockAdjustmentDetailByAdjustmentId(stockAdjustment.getId());
        for(StockAdjustmentDetail sad : sads){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentTransDetail satd = modelMapper.map(sad, StockAdjustmentTransDetail.class);
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(sad.getProductId(), request.getWareHouseTypeId());
            if(stockTotal == null)
                response.setFailure(ResponseMessage.NO_CONTENT);
            if(stockTotal.getQuantity() == null){
                stockTotal.setQuantity(0);
            }
            stockTotal.setQuantity(stockTotal.getQuantity()- sad.getQuantity());

            stockTotalRepository.save(stockTotal);
            stockAdjustmentTransDetailRepository.save(satd);
        }
        stockAdjustment.setStatus(1);
        stockAdjustmentRepository.save(stockAdjustment);
        stockAdjustmentTransRepository.save(poAdjustTrans);
        return response.withData(poAdjustTrans).getData();
    }

    public StockBorrowingTrans createBorrowingTrans(ReceiptExportCreateRequest request, Long userId) {
        Response<StockBorrowingTrans> response = new Response<>();
        User user = userClient.getUserById(userId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        StockBorrowingTrans poBorrowTransRecord = modelMapper.map(request, StockBorrowingTrans.class);
        StockBorrowing stockBorrowing = stockBorrowingRepository.findById(request.getReceiptImportId()).get();
        poBorrowTransRecord.setTransDate(date);
        poBorrowTransRecord.setTransCode(CreateCodeUtils.createStockBorrowTransCode(request.getShopId()));
        poBorrowTransRecord.setRedInvoiceNo(CreateCodeUtils.createStockBorrowTransExportRedInvoice(request.getShopId()));
        poBorrowTransRecord.setAdjustmentDate(stockBorrowing.getBorrowDate());
        //Inernal Number default null
        //Po no default null
        poBorrowTransRecord.setStockBorrowingId(stockBorrowing.getId());
        poBorrowTransRecord.setCreateUser(user.getUserAccount());
        poBorrowTransRecord.setType(2);
        List<StockBorrowingDetail> sbds = stockBorrowingDetailRepository.findByBorrowingId(stockBorrowing.getId());
        for(StockBorrowingDetail sbd : sbds){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingTransDetail sbtd = modelMapper.map(sbd, StockBorrowingTransDetail.class);
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(sbd.getProductId(), request.getWareHouseTypeId());
            if(stockTotal == null)
                response.setFailure(ResponseMessage.NO_CONTENT);
            if(stockTotal.getQuantity() == null){
                stockTotal.setQuantity(0);
            }
            stockTotal.setQuantity(stockTotal.getQuantity()- sbd.getQuantity());

            stockTotalRepository.save(stockTotal);
            stockBorrowingTransDetailRepository.save(sbtd);
        }
        stockBorrowing.setStatus(4);
        stockBorrowingRepository.save(stockBorrowing);
        stockBorrowingTransRepository.save(poBorrowTransRecord);
        return response.withData(poBorrowTransRecord).getData();
    }
    public Response<String> updatePoTransExport(ReceiptExportUpdateRequest request, Long id) {
        Response<String> response = new Response<>();
        PoTrans poTrans = repository.findById(id).get();
        List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
        if(!request.getLitQuantityRemain().isEmpty()){
            for (int i=0;i<poTransDetails.size();i++){
                PoTransDetail poTransDetail = poTransDetails.get(i);
                StockTotal st = stockTotalRepository.findByProductIdAndWareHouseTypeId(poTransDetail.getProductId(),poTrans.getWareHouseTypeId());
                st.setQuantity(st.getQuantity()-poTransDetail.getQuantity() + request.getLitQuantityRemain().get(i));
                poTransDetail.setQuantity(request.getLitQuantityRemain().get(i));
                stockTotalRepository.save(st);
                poTransDetailRepository.save(poTransDetail);
            }
        }
        poTrans.setNote(request.getNote());
        repository.save(poTrans);
        return response.withData(ResponseMessage.SUCCESSFUL.toString());
    }

    public Response<String> updateStockAdjustmentTransExport(ReceiptExportUpdateRequest request, Long id) {
        Response<String> response = new Response<>();
        StockAdjustmentTrans stockAdjustmentTrans  = stockAdjustmentTransRepository.findById(id).get();
        if (stockAdjustmentTrans != null){
            stockAdjustmentTrans.setNote(request.getNote());
            stockAdjustmentTransRepository.save(stockAdjustmentTrans);
            return response.withData(ResponseMessage.SUCCESSFUL.toString());
        }
        return null;
    }
    public Response<String> updateStockBorrowingTransExport(ReceiptExportUpdateRequest request, Long id) {
        Response<String> response = new Response<>();
        StockBorrowingTrans stockBorrowingTrans  = stockBorrowingTransRepository.findById(id).get();
        if (stockBorrowingTrans != null){
            stockBorrowingTrans.setNote(request.getNote());
            stockBorrowingTransRepository.save(stockBorrowingTrans);
            return response.withData(ResponseMessage.SUCCESSFUL.toString());
        }
        return null;
    }
    public Response<String> removePoTransExport(Long id) {
        Response<String> response = new Response<>();
        PoTrans poTrans = repository.findById(id).get();
        List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
        for (PoTransDetail ptd :poTransDetails ){
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(ptd.getProductId(),poTrans.getWareHouseTypeId());
            stockTotal.setQuantity(stockTotal.getQuantity()+ptd.getQuantity());
            stockTotalRepository.save(stockTotal);
        }
        poTrans.setDeletedAt(ts);
        repository.save(poTrans);

        return response.withData(ResponseMessage.SUCCESSFUL.toString());
    }

    public Response<String> removeStockAdjustmentTransExport(Long id) {
        Response<String> response = new Response<>();
        StockAdjustmentTrans stockAdjustmentTrans = stockAdjustmentTransRepository.getStockAdjustmentTransByIdAndDeletedAtIsNull(id);
        List<StockAdjustmentTransDetail> stockAdjustmentTransDetails = stockAdjustmentTransDetailRepository.getStockAdjustmentTransDetailsByTransId(stockAdjustmentTrans.getId());
        for (StockAdjustmentTransDetail satd :stockAdjustmentTransDetails ){
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(satd.getProductId(),stockAdjustmentTrans.getWareHouseTypeId());
            stockTotal.setQuantity(stockTotal.getQuantity() + satd.getQuantity());
            stockTotalRepository.save(stockTotal);
        }
        stockAdjustmentTrans.setDeletedAt(ts);
        stockAdjustmentTransRepository.save(stockAdjustmentTrans);
        StockAdjustment sa = stockAdjustmentRepository.findById(stockAdjustmentTrans.getAdjustmentId()).get();
        sa.setStatus(2);
        stockAdjustmentRepository.save(sa);
        return response.withData(ResponseMessage.SUCCESSFUL.toString());
    }

    public Response<String> removeStockBorrowingTransExport(Long id) {
        Response<String> response = new Response<>();
        StockBorrowingTrans stockBorrowingTrans = stockBorrowingTransRepository.getStockBorrowingTransByIdAndDeletedAtIsNull(id);
        List<StockBorrowingTransDetail> stockBorrowingTransDetails = stockBorrowingTransDetailRepository.getStockBorrowingTransDetailByTransId(stockBorrowingTrans.getId());
        for (StockBorrowingTransDetail sbtd :stockBorrowingTransDetails ){
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(sbtd.getProductId(),stockBorrowingTrans.getWareHouseTypeId());
            stockTotal.setQuantity(stockTotal.getQuantity() + sbtd.getQuantity());
            stockTotalRepository.save(stockTotal);
        }
        stockBorrowingTrans.setDeletedAt(ts);
        stockBorrowingTransRepository.save(stockBorrowingTrans);
        StockBorrowing sb = stockBorrowingRepository.findById(stockBorrowingTrans.getStockBorrowingId()).get();
        sb.setStatus(1);
        stockBorrowingRepository.save(sb);
        return response.withData(ResponseMessage.SUCCESSFUL.toString());
    }
}
