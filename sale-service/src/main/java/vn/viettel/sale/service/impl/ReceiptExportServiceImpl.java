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
            Page<PoTrans> list1 = repository.getPoTransExport(Specification.where(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDate(fromDate, toDate)),pageable);
            Page<StockAdjustmentTrans> list2 = stockAdjustmentTransRepository.getStockAdjustmentTransExport(Specification.where(ReceiptSpecification.hasRedInvoiceNoA(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateA(fromDate, toDate)),pageable);
            Page<StockBorrowingTrans> list3 = stockBorrowingTransRepository.getStockBorrowingTransExport(Specification.where(ReceiptSpecification.hasRedInvoiceNoB(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateB(fromDate, toDate)),pageable);
            List<ReceiptImportListDTO> listDTO1 = list1.getContent().stream().map(
                    item -> modelMapper.map(item, ReceiptImportListDTO.class)
            ).collect(Collectors.toList());
            List<ReceiptImportListDTO> listDTO2 = list2.getContent().stream().map(
                    item -> modelMapper.map(item, ReceiptImportListDTO.class)
            ).collect(Collectors.toList());
            List<ReceiptImportListDTO> listDTO3 = list3.getContent().stream().map(
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
            Page<PoTrans> list1 = repository.getPoTransImport(Specification.where(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDate(fromDate, toDate)),pageable);
            List<ReceiptImportListDTO> listDTO1 = list1.getContent().stream().map(
                    item -> modelMapper.map(item, ReceiptImportListDTO.class)
            ).collect(Collectors.toList());
            List<ReceiptImportListDTO> result = new ArrayList<>();
            result.addAll(listDTO1);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(result);
            Response<Page<ReceiptImportListDTO>> response = new Response<>();
            return response.withData(pageResponse);
        }else if(type == 1){
            Page<StockAdjustmentTrans> list2 = stockAdjustmentTransRepository.getStockAdjustmentTransImport(Specification.where(ReceiptSpecification.hasRedInvoiceNoA(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateA(fromDate, toDate)),pageable);
            List<ReceiptImportListDTO> listDTO2 = list2.getContent().stream().map(
                    item -> modelMapper.map(item, ReceiptImportListDTO.class)
            ).collect(Collectors.toList());
            List<ReceiptImportListDTO> result = new ArrayList<>();
            result.addAll(listDTO2);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(result);
            Response<Page<ReceiptImportListDTO>> response = new Response<>();
            return response.withData(pageResponse);
        }else if (type == 2){
            Page<StockBorrowingTrans> list3 = stockBorrowingTransRepository.getStockBorrowingTransImport(Specification.where(ReceiptSpecification.hasRedInvoiceNoB(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateB(fromDate, toDate)),pageable);
            List<ReceiptImportListDTO> listDTO3 = list3.getContent().stream().map(
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

    /*@Override
    @Transactional(rollbackFor = Exception.class)
    public Response<PoTransDTO> getPoTransExportById(Long transId) {
        Response<PoTransDTO> response = new Response<>();
        PoTrans poTrans = repository.getPoTransExportByIdAndDeletedAtIsNull(transId);
        if (!poTrans.getId().equals(transId)) {
            return response.withError(ResponseMessage.PO_TRANS_IS_NOT_EXISTED);
        }
        PoTransDTO poTransDTO = modelMapper.map(poTrans, PoTransDTO.class);

        return response.withData(poTransDTO);
    }*/

    /*@Override
    @Transactional(rollbackFor = Exception.class)
    public Response<StockAdjustmentTransDTO> getStockAdjustmentTransById(Long transId) {
        Response<StockAdjustmentTransDTO> response = new Response<>();
        StockAdjustmentTrans sat = stockAdjustmentTransRepository.getStockAdjustmentTransExportById(transId);
        if (!sat.getId().equals(transId)) {
            return response.withError(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);
        }
        StockAdjustmentTransDTO stockAdjustmentTransDTO = modelMapper.map(sat, StockAdjustmentTransDTO.class);

        return response.withData(stockAdjustmentTransDTO);
    }*/

    /*@Override
    @Transactional(rollbackFor = Exception.class)
    public Response<StockBorrowingTransDTO> getStockBorrowingTransById(Long transId) {
        Response<StockBorrowingTransDTO> response = new Response<>();
        StockBorrowingTrans sbt = stockBorrowingTransRepository.getStockBorrowingTransExportById(transId);
        if (!sbt.getId().equals(transId)) {
            return response.withError(ResponseMessage.STOCK_BORROWING_TRANS_IS_NOT_EXISTED);
        }
        StockBorrowingTransDTO stockBorrowingTransDTO = modelMapper.map(sbt, StockBorrowingTransDTO.class);

        return response.withData(stockBorrowingTransDTO);
    }*/

    @Override
    public Response<Page<PoTransDTO>> getListPoTrans(String transCode,String redInvoiceNo,String internalNumber,String poNo,Date fromDate,Date toDate, Pageable pageable) {
        Response<Page<PoTransDTO>> response = new Response<>();
        if (fromDate == null || toDate == null) {
            LocalDate initial = LocalDate.now();
            fromDate = Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            toDate = Date.from(initial.withDayOfMonth(initial.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        Page<PoTrans> poTrans = repository.getPoTransImportNotPromotion(Specification.where(ReceiptSpecification.hasTransCode(transCode)).and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo)).
                and(ReceiptSpecification.hasInternalNumber(internalNumber)).and(ReceiptSpecification.hasPoNo(poNo)).and(ReceiptSpecification.hasFromDateToDate(fromDate, toDate)),pageable);
        List<PoTransDTO> rs = new ArrayList<>();
        for (PoTrans pt : poTrans){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoTransDTO dto = modelMapper.map(pt, PoTransDTO.class);
            rs.add(dto);
        }
        Page<PoTransDTO> pageResponse = new PageImpl<>(rs);

        return response.withData(pageResponse);
    }


    /*@Override
    @Transactional(rollbackFor = Exception.class)
    public Response<List<StockAdjustmentDTO>> getListStockAdjustmentExport() {
        List<StockAdjustment> stockAdjustments = stockAdjustmentRepository.getStockAdjustment();
        List<StockAdjustmentDTO> rs = new ArrayList<>();
        for (StockAdjustment sa : stockAdjustments){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentDTO dto = modelMapper.map(sa, StockAdjustmentDTO.class);
            rs.add(dto);
        }
        Response<List<StockAdjustmentDTO>> response = new Response<>();
        return response.withData(rs);
    }*/

   /* @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<List<StockBorrowingDTO>> getListStockBorrowingExport() {
        List<StockBorrowing> stockBorrowings = stockBorrowingRepository.getStockBorrowingExport();
        List<StockBorrowingDTO> rs = new ArrayList<>();
        for (StockBorrowing sb : stockBorrowings){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingDTO dto = modelMapper.map(sb, StockBorrowingDTO.class);
            rs.add(dto);
        }
        Response<List<StockBorrowingDTO>> response = new Response<>();
        return response.withData(rs);
    }*/

    @Override
    @Transactional(rollbackFor = Exception.class)
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

    @Override
    @Transactional(rollbackFor = Exception.class)
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
    @Override
    @Transactional(rollbackFor = Exception.class)
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

    @Override
    @Transactional(rollbackFor = Exception.class)
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

    @Override
    @Transactional(rollbackFor = Exception.class)
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
    @Override
    @Transactional(rollbackFor = Exception.class)
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

   /* @Override
    public Response<List<PoTransDetailDTO>> getPoTransDetailExportByTransId(Long transId) {
        List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(transId);
        PoTrans poTrans = repository.findById(transId).get();
        List<PoTransDetailDTO> rs = new ArrayList<>();
        for (PoTransDetail ptd : poTransDetails){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoTransDetailDTO dto = modelMapper.map(ptd, PoTransDetailDTO.class);
            dto.setProductCode(productRepository.findById(ptd.getProductId()).get().getProductCode());
            dto.setProductCode(productRepository.findById(ptd.getProductId()).get().getProductName());
            rs.add(dto);
        }
        Response<List<PoTransDetailDTO>> response = new Response<>();
        return response.withData(rs);
    }*/


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


}
