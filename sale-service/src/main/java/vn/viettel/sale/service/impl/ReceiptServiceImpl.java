package vn.viettel.sale.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.convention.MatchingStrategies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.stock.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.messaging.ReceiptCreateDetailRequest;
import vn.viettel.sale.messaging.ReceiptCreateRequest;
import vn.viettel.sale.messaging.ReceiptUpdateRequest;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ReceiptService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.PoTransSpecification;
import vn.viettel.sale.util.CreateCodeUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class ReceiptServiceImpl extends BaseServiceImpl<PoTrans, PoTransRepository> implements ReceiptService {
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    PoComfirmRepository poComfirmRepository;
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
    StockAdjustmentTransRepository stockAdjustmentTransRepository;
    StockAdjustmentRepository stockAdjustmentRepository;
    StockAdjustmentDetailRepository stockAdjustmentDetailRepository;
    StockAdjustmentTransDetailRepository stockAdjustmentTransDetailRepository;
    StockBorrowingTransRepository stockBorrowingTransRepository;
    StockBorrowingTransDetailRepository stockBorrowingTransDetailRepository;
    StockBorrowingRepository stockBorrowingRepository;
    StockBorrowingDetailRepository stockBorrowingDetailRepository;
    Date date = new Date();
    Timestamp ts =new Timestamp(date.getTime());
    @Override
    public Response<Page<PoTransDTO>> test(Pageable pageable) {
        Response<Page<PoTransDTO>> response = new Response<>();
        Page<PoTrans> poTrans;
        //poTrans = repository.findAll(Specification.where(PoTransSpecification.hasRedInvoiceNo(redInvoiceNo)).and(PoTransSpecification.hasFromDateToDate(fromDate, toDate)).and(PoTransSpecification.hasType(type)), pageable);
        poTrans = repository.getAllByKeyWords(pageable);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Page<PoTransDTO> dtos = poTrans.map(this::mapPoTransToPoTranDTO);
        return response.withData(dtos);
    }
    @Override
    public Response<Page<PoTransDTO>> getAll(String redInvoiceNo, Date fromDate, Date toDate, Integer type, Pageable pageable) {
        Response<Page<PoTransDTO>> response = new Response<>();
        redInvoiceNo = StringUtils.defaultIfBlank(redInvoiceNo, StringUtils.EMPTY);

        if (fromDate == null || toDate == null) {
            LocalDate initial = LocalDate.now();
            fromDate = Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            toDate = Date.from(initial.withDayOfMonth(initial.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        Page<PoTrans> poTrans;

        //poTrans = repository.findAll(Specification.where(PoTransSpecification.hasRedInvoiceNo(redInvoiceNo)).and(PoTransSpecification.hasFromDateToDate(fromDate, toDate)).and(PoTransSpecification.hasType(type)), pageable);

        poTrans = repository.getAllByKeyWords(pageable);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Page<PoTransDTO> dtos = poTrans.map(this::mapPoTransToPoTranDTO);
        return response.withData(dtos);
    }



    public Response<String> createReceipt(ReceiptCreateRequest request,Long shopId,Long userId) {
        Response<String> response = new Response<>();
        switch (request.getType()){
            case 0:
                try{
                    createPoTrans(request,shopId,userId);
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
                break;
            case 1:
                try {
                    createAdjustmentTrans(request,shopId,userId);
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
                break;
            case 2:
                try {
                    createBorrowingTrans(request,shopId,userId);
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
                break;
        }
        return response.withData(ResponseMessage.SUCCESSFUL.toString());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<String> updatePoTrans(ReceiptUpdateRequest request, Long id) {
        Response<String> response = new Response<>();
        PoTrans poTrans = repository.getPoTransByIdAndDeletedAtIsNull(id);
        if (poTrans.getPoId()== null)
        {
            poTrans.setRedInvoiceNo(request.getRedInvoiceNumber());
            poTrans.setInternalNumber(request.getInternalNumber());
            poTrans.setPoNumber(request.getPoNumber());
            if (!request.getLstUpdate().isEmpty()){
                for (ReceiptCreateDetailRequest rcdr :request.getLstUpdate()){
                    PoTransDetail poTransDetail = modelMapper.map(rcdr,PoTransDetail.class);
                    poTransDetail.setPrice((float) 0);
                    poTransDetail.setPriceNotVat((float) 0);
                    StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(rcdr.getProductId(),poTrans.getWareHouseTypeId());
                    if(stockTotal == null) return null;
                    stockTotal.setQuantity(stockTotal.getQuantity()+rcdr.getQuantity());
                    poTransDetailRepository.save(poTransDetail);
                    stockTotalRepository.save(stockTotal);
                }
            }
            if(!request.getIdRemove().isEmpty()){
                for (Long idRemove : request.getIdRemove()){
                    PoTransDetail poTransDetail = poTransDetailRepository.findByIdAndDeletedAtIsNull(idRemove);
                    poTransDetail.setDeletedAt(ts);
                    StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(poTransDetail.getProductId(),poTrans.getWareHouseTypeId());
                    stockTotal.setQuantity(stockTotal.getQuantity()- poTransDetail.getQuantity());
                    poTransDetailRepository.save(poTransDetail);
                    stockTotalRepository.save(stockTotal);
                }
            }
            poTrans.setRedInvoiceNo(request.getRedInvoiceNumber());
            poTrans.setNote(request.getNote());
            return response.withData(ResponseMessage.SUCCESSFUL.toString());
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<String> updateStockAdjustmentTrans(ReceiptUpdateRequest request, Long id) {
        Response<String> response = new Response<>();
        StockAdjustmentTrans stockAdjustmentTrans = stockAdjustmentTransRepository.getStockAdjustmentTransByIdAndDeletedAtIsNull(id);
        if (stockAdjustmentTrans!=null){
            stockAdjustmentTrans.setRedInvoiceNo(request.getRedInvoiceNumber());
            stockAdjustmentTrans.setNote(request.getNote());
            stockAdjustmentTransRepository.save(stockAdjustmentTrans);
            return response.withData(ResponseMessage.SUCCESSFUL.toString());
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<String> updateStockBorrowingTrans(ReceiptUpdateRequest request, Long id) {
        Response<String> response = new Response<>();
        StockBorrowingTrans stockBorrowingTrans = stockBorrowingTransRepository.getStockBorrowingTransByIdAndDeletedAtIsNull(id);
        if (stockBorrowingTrans!=null){
            stockBorrowingTrans.setRedInvoiceNo(request.getRedInvoiceNumber());
            stockBorrowingTrans.setNote(request.getNote());
            stockBorrowingTransRepository.save(stockBorrowingTrans);
            return response.withData(ResponseMessage.SUCCESSFUL.toString());
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removePoTrans(Long id) {
        PoTrans poTrans = repository.getPoTransByIdAndDeletedAtIsNull(id);
        List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransIdAndDeletedAtIsNull(poTrans.getId());
        for (PoTransDetail ptd :poTransDetails ){
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(ptd.getProductId(),poTrans.getWareHouseTypeId());
            stockTotal.setQuantity(stockTotal.getQuantity() - ptd.getQuantity());
            stockTotalRepository.save(stockTotal);
        }
        if (poTrans.getPoId() != null){
            PoConfirm poConfirm = poComfirmRepository.findById(poTrans.getPoId()).get();
            poConfirm.setStatus(0);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeStockAdjustmentTrans(Long id) {
        StockAdjustmentTrans stockAdjustmentTrans = stockAdjustmentTransRepository.getStockAdjustmentTransByIdAndDeletedAtIsNull(id);
        List<StockAdjustmentTransDetail> stockAdjustmentTransDetails = stockAdjustmentTransDetailRepository.getStockAdjustmentTransDetailsByTransIdAndDeletedAtIsNull(stockAdjustmentTrans.getId());
        for (StockAdjustmentTransDetail satd :stockAdjustmentTransDetails ){
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(satd.getProductId(),stockAdjustmentTrans.getWareHouseTypeId());
            stockTotal.setQuantity(stockTotal.getQuantity() - satd.getQuantity());
            stockTotalRepository.save(stockTotal);
        }
            StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(stockAdjustmentTrans.getAdjustmentId()).get();
        stockAdjustment.setStatus(2);
    }

    @Override
    public void removeStockBorrowingTrans(Long id) {
        StockBorrowingTrans stockBorrowingTrans = stockBorrowingTransRepository.getStockBorrowingTransByIdAndDeletedAtIsNull(id);
        List<StockBorrowingTransDetail> stockBorrowingTransDetails = stockBorrowingTransDetailRepository.getStockBorrowingTransDetailByTransIdAndDeletedAtIsNull(stockBorrowingTrans.getId());
        for (StockBorrowingTransDetail sbtd :stockBorrowingTransDetails ){
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(sbtd.getProductId(),stockBorrowingTrans.getWareHouseTypeId());
            stockTotal.setQuantity(stockTotal.getQuantity() - sbtd.getQuantity());
            stockTotalRepository.save(stockTotal);
        }
        StockBorrowing stockBorrowing = stockBorrowingRepository.findById(stockBorrowingTrans.getStockBorrowingId()).get();
        stockBorrowing.setStatus(1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<PoTransDTO> getPoTransById(Long transId) {
        Response<PoTransDTO> response = new Response<>();
        PoTrans poTrans = repository.getPoTransByIdAndDeletedAtIsNull(transId);
        if (!poTrans.getId().equals(transId)) {
            return response.withError(ResponseMessage.PO_TRANS_IS_NOT_EXISTED);
        }
        PoTransDTO poTransDTO = modelMapper.map(poTrans, PoTransDTO.class);

        return response.withData(poTransDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<StockAdjustmentTransDTO> getStockAdjustmentById(Long transId) {
        Response<StockAdjustmentTransDTO> response = new Response<>();
        StockAdjustmentTrans sat = stockAdjustmentTransRepository.getStockAdjustmentTransByIdAndDeletedAtIsNull(transId);
        if (!sat.getId().equals(transId)) {
            return response.withError(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);
        }
        StockAdjustmentTransDTO stockAdjustmentTransDTO = modelMapper.map(sat, StockAdjustmentTransDTO.class);

        return response.withData(stockAdjustmentTransDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<StockBorrowingTransDTO> getStockBorrowingById(Long transId) {
        Response<StockBorrowingTransDTO> response = new Response<>();
        StockBorrowingTrans sbt = stockBorrowingTransRepository.getStockBorrowingTransByIdAndDeletedAtIsNull(transId);
        if (!sbt.getId().equals(transId)) {
            return response.withError(ResponseMessage.STOCK_BORROWING_TRANS_IS_NOT_EXISTED);
        }
        StockBorrowingTransDTO stockBorrowingTransDTO = modelMapper.map(sbt, StockBorrowingTransDTO.class);

        return response.withData(stockBorrowingTransDTO);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    private PoTransDTO mapPoTransToPoTranDTO(PoTrans poTrans) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PoTransDTO dto = modelMapper.map(poTrans, PoTransDTO.class);
        return dto;
    }
    public PoTrans createPoTrans(ReceiptCreateRequest request, Long shopId, Long userId) {
        Response<PoTrans> response = new Response<>();
        User user = userClient.getUserById(userId);
        if (request.getImportType() == 0) {
            if(request.getRedInvoiceNo() != null && request.getInternalNumber() != null){
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTrans poRecord = modelMapper.map(request, PoTrans.class);
                poRecord.setTransDate(date);
                poRecord.setTransCode(CreateCodeUtils.createPoTransCode(shopId));
                poRecord.setCreateUser(user.getUserAccount());
                poRecord.setType(1);
                for (ReceiptCreateDetailRequest rcdr : request.getLst()) {
                    PoTransDetail poTransDetail = new PoTransDetail();
                    poTransDetail.setTransId(poRecord.getId());
                    poTransDetail.setProductId(rcdr.getProductId());
                    poTransDetail.setQuantity(rcdr.getQuantity());
                    poTransDetail.setPrice(rcdr.getPrice());
                    poTransDetail.setPriceNotVat(rcdr.getPriceNotVat());
                    poTransDetail.setAmountNotVat(rcdr.getAmountNotVat());
                    poTransDetailRepository.save(poTransDetail);
                    Product product = productRepository.findById(rcdr.getProductId()).get();
                    if (product == null) response.setFailure(ResponseMessage.NO_CONTENT);
                    StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(product.getId(), request.getWareHouseTypeId());
                    if (stockTotal == null)
                        response.setFailure(ResponseMessage.NO_CONTENT);
                    if (stockTotal.getQuantity() == null) {
                        stockTotal.setQuantity(0);
                    }
                    stockTotal.setQuantity(stockTotal.getQuantity() + rcdr.getQuantity());
                    stockTotalRepository.save(stockTotal);
                }
                poRecord.setNote(request.getNote());
                repository.save(poRecord);
                return poRecord;
            } else{
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTrans poRecord = modelMapper.map(request, PoTrans.class);
                PoConfirm poConfirm = poComfirmRepository.findById(request.getPoId()).get();
                poRecord.setTransDate(date);
                poRecord.setTransCode(CreateCodeUtils.createPoTransCode(shopId));
                poRecord.setOrderDate(poConfirm.getOrderDate());
                poRecord.setInternalNumber(poConfirm.getInternalNumber());
                poRecord.setPoId(poConfirm.getId());
                poRecord.setCreateUser(user.getUserAccount());
                poRecord.setType(1);
                List<PoDetail> poDetails = poDetailRepository.findByPoId(poConfirm.getId());
                for (PoDetail pod : poDetails) {
                    PoTransDetail poTransDetail = new PoTransDetail();
                    poTransDetail.setTransId(poRecord.getId());
                    poTransDetail.setProductId(pod.getProductId());
                    poTransDetail.setQuantity(pod.getQuantity());
                    poTransDetail.setPrice(pod.getPrice());
                    poTransDetail.setPriceNotVat(pod.getPriceNotVat());
                    poTransDetail.setAmountNotVat(pod.getAmountNotVat());
                    poTransDetailRepository.save(poTransDetail);
                    Product product = productRepository.findById(pod.getProductId()).get();
                    if (product == null) response.setFailure(ResponseMessage.NO_CONTENT);
                    StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(product.getId(), request.getWareHouseTypeId());
                    if (stockTotal == null)
                        response.setFailure(ResponseMessage.NO_CONTENT);
                    if (stockTotal.getQuantity() == null) {
                        stockTotal.setQuantity(0);
                    }
                    stockTotal.setQuantity(stockTotal.getQuantity() + pod.getQuantity());
                    stockTotalRepository.save(stockTotal);
                }
                poRecord.setNote(request.getNote());
                poConfirm.setStatus(1);
                repository.save(poRecord);
                poComfirmRepository.save(poConfirm);

                return poRecord;
            }
        }

        return null;
    }


    public StockAdjustmentTrans createAdjustmentTrans(ReceiptCreateRequest request, Long shopId, Long userId) {
        Response<StockAdjustmentTrans> response = new Response<>();
        User user = userClient.getUserById(userId);
        if (request.getImportType() == 1) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentTrans stockAdjustmentRecord = modelMapper.map(request, StockAdjustmentTrans.class);
            StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(request.getPoId()).get();
            stockAdjustmentRecord.setTransDate(date);
            stockAdjustmentRecord.setTransCode(stockAdjustment.getAdjustmentCode());
            stockAdjustmentRecord.setWareHouseTypeId(request.getWareHouseTypeId());
            stockAdjustmentRecord.setRedInvoiceNo(CreateCodeUtils.createRedInvoiceCodeAdjust(shopId));
            stockAdjustmentRecord.setAdjustmentDate(request.getTransDate());
            stockAdjustmentRecord.setInternalNumber(request.getInternalNumber());
            stockAdjustmentRecord.setCreateUser(user.getUserAccount());
            stockAdjustmentRecord.setType(1);
            List<StockAdjustmentDetail> stockAdjustmentDetails = stockAdjustmentDetailRepository.findByAdjustmentId(stockAdjustment.getId());
            for (StockAdjustmentDetail sad : stockAdjustmentDetails) {
                StockAdjustmentTransDetail stockAdjustmentTransDetail = new StockAdjustmentTransDetail();
                stockAdjustmentTransDetail.setTransId(sad.getId());
                stockAdjustmentTransDetail.setProductId(sad.getProductId());
                stockAdjustmentTransDetail.setQuantity(sad.getQuantity());
                stockAdjustmentTransDetail.setPrice(sad.getPrice());

                Product product = productRepository.findById(sad.getProductId()).get();
                if (product == null) response.setFailure(ResponseMessage.NO_CONTENT);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(product.getId(), request.getWareHouseTypeId());
                if (stockTotal == null)
                    response.setFailure(ResponseMessage.NO_CONTENT);
                if (stockTotal.getQuantity() == null) {
                    stockTotal.setQuantity(0);
                }
                stockAdjustmentTransDetail.setStockQuantity(stockTotal.getQuantity());
                stockAdjustmentTransDetail.setOriginalQuantity(sad.getQuantity());
                stockTotal.setQuantity(stockTotal.getQuantity() + sad.getQuantity());

                stockTotalRepository.save(stockTotal);
                stockAdjustmentTransDetailRepository.save(stockAdjustmentTransDetail);
            }
            stockAdjustmentRecord.setNote(request.getNote());
            stockAdjustment.setStatus(1);
            stockAdjustmentTransRepository.save(stockAdjustmentRecord);
            stockAdjustmentRepository.save(stockAdjustment);

            return stockAdjustmentRecord;
        }
        return null;
    }

    public StockBorrowingTrans createBorrowingTrans(ReceiptCreateRequest request, Long shopId, Long userId) {
        Response<StockBorrowingTrans> response = new Response<>();
        User user = userClient.getUserById(userId);
        if (request.getImportType() == 2) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingTrans stockBorrowingTrans = modelMapper.map(request, StockBorrowingTrans.class);
            StockBorrowing stockBorrowing = stockBorrowingRepository.findById(request.getPoId()).get();
            stockBorrowingTrans.setTransDate(date);
            stockBorrowingTrans.setTransCode(CreateCodeUtils.createBorrowingTransCode(shopId));
            stockBorrowingTrans.setWareHouseTypeId(request.getWareHouseTypeId());
            stockBorrowingTrans.setRedInvoiceNo(stockBorrowing.getPoBorrowCode());
            stockBorrowingTrans.setTransDate(request.getTransDate());
            stockBorrowingTrans.setInternalNumber(request.getInternalNumber());
            stockBorrowingTrans.setCreateUser(user.getUserAccount());
            stockBorrowingTrans.setType(1);
            List<StockBorrowingDetail> stockBorrowingDetails = stockBorrowingDetailRepository.findByBorrowingId(stockBorrowing.getId());
            for (StockBorrowingDetail sbd : stockBorrowingDetails) {
                StockBorrowingTransDetail stockBorrowingTransDetail = new StockBorrowingTransDetail();
                stockBorrowingTransDetail.setTransId(sbd.getId());
                stockBorrowingTransDetail.setProductId(sbd.getProductId());
                stockBorrowingTransDetail.setQuantity(sbd.getQuantity());
                stockBorrowingTransDetail.setPrice(sbd.getPrice());

                Product product = productRepository.findById(sbd.getProductId()).get();
                if (product == null) response.setFailure(ResponseMessage.NO_CONTENT);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(product.getId(), request.getWareHouseTypeId());
                if (stockTotal == null)
                    response.setFailure(ResponseMessage.NO_CONTENT);
                if (stockTotal.getQuantity() == null) {
                    stockTotal.setQuantity(0);
                }
                stockTotal.setQuantity(stockTotal.getQuantity() + sbd.getQuantity());

                stockTotalRepository.save(stockTotal);
                stockBorrowingTransDetailRepository.save(stockBorrowingTransDetail);
            }
            stockBorrowingTrans.setNote(request.getNote());
            stockBorrowing.setStatus(1);
            stockBorrowingRepository.save(stockBorrowing);
            stockBorrowingTransRepository.save(stockBorrowingTrans);
            return stockBorrowingTrans;
        }
        return null;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
}
