package vn.viettel.sale.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.convention.MatchingStrategies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.stock.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.messaging.ReceiptCreateRequest;
import vn.viettel.sale.messaging.ReceiptUpdateRequest;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ReceiptService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.PoTransSpecification;

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
    Timestamp timestamp=new Timestamp(date.getTime());

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
        poTrans = repository.findAll(Specification.where(PoTransSpecification.hasRedInvoiceNo(redInvoiceNo)).and(PoTransSpecification.hasFromDateToDate(fromDate, toDate)).and(PoTransSpecification.hasType(type)), pageable);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Page<PoTransDTO> dtos = poTrans.map(this::mapPoTransToPoTranDTO);
        return response.withData(dtos);
    }



    public Response<String> createReceipt(ReceiptCreateRequest request,Long userId) {
        Response<String> response = new Response<>();
        switch (request.getType()){
            case 0:
                try{
                    createPoTrans(request,userId);
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
    @Transactional(rollbackFor = Exception.class)
    public Response<PoTransDTO> getOnePoTransById(Long tranId) {
        Response<PoTransDTO> response = new Response<>();

        Optional<PoTrans> poTrans = repository.getPoTransByIdAndDeletedAtIsNull(tranId);

        if (!poTrans.get().getId().equals(tranId)) {
            return response.withError(ResponseMessage.CUSTOMER_IS_NOT_EXISTED);
        }
        PoTransDTO poTransDTO = modelMapper.map(poTrans, PoTransDTO.class);

        return response.withData(poTransDTO);
    }

    @Override
    public Response<StockAdjustmentTransDTO> getOneStockAdjustmentTransById(Long tranId) {
        Response<StockAdjustmentTransDTO> response = new Response<>();

        Optional<StockAdjustmentTrans> stockAdjustmentTrans = stockAdjustmentTransRepository.getStockAdjustmentTransByIdAndDeletedAtIsNull(tranId);

        if (!stockAdjustmentTrans.get().getId().equals(tranId)) {
            return response.withError(ResponseMessage.CUSTOMER_IS_NOT_EXISTED);
        }
        StockAdjustmentTransDTO stockAdjustmentTransDTO = modelMapper.map(stockAdjustmentTrans, StockAdjustmentTransDTO.class);

        return response.withData(stockAdjustmentTransDTO);
    }

    @Override
    public Response<StockBorrowingTransDTO> getOneStockBorrowingTransById(Long tranId) {
        Response<StockBorrowingTransDTO> response = new Response<>();
        Optional<StockBorrowingTrans> stockBorrowingTrans = stockBorrowingTransRepository.getStockBorrowingTransByIdAndDeletedAtIsNull(tranId);
        if (!stockBorrowingTrans.get().getId().equals(tranId)) {
            return response.withError(ResponseMessage.CUSTOMER_IS_NOT_EXISTED);
        }
        StockBorrowingTransDTO stockBorrowingTransDTO = modelMapper.map(stockBorrowingTrans, StockBorrowingTransDTO.class);

        return response.withData(stockBorrowingTransDTO);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////   ////////////////////////////////////////////////////////////////////////////////////////////////
    private PoTransDTO mapPoTransToPoTranDTO(PoTrans poTrans) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PoTransDTO dto = modelMapper.map(poTrans, PoTransDTO.class);
        return dto;
    }
    public PoTrans updatePoTrans(ReceiptUpdateRequest request, Long Id) {
        Response<PoTrans> response = new Response<>();
        PoTrans po = repository.getPoTransByIdAndDeletedAtIsNull(Id).get();
        if (po != null) {
            po.setRedInvoiceNo(request.getRedInvoiceNo());
            po.setNote(request.getNote());
            repository.save(po);
        } else {
            response.setFailure(ResponseMessage.NO_CONTENT);
        }
        return response.withData(po).getData();
    }
    public PoTrans createPoTrans(ReceiptCreateRequest request, Long userId) {
        Response<PoTrans> response = new Response<>();
        User user = userClient.getUserById(userId);
        if (request.getImportType() == 0) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoTrans poRecord = modelMapper.map(request, PoTrans.class);
            //poRecord.setTransDate(date);
            poRecord.setTransCode(createPoTransCode(request.getShopId()));
            poRecord.setWareHouseTypeId(request.getWareHouseTypeId());
            poRecord.setRedInvoiceNo(request.getRedInvoiceNo());
            PoConfirm poConfirm = poComfirmRepository.findById(request.getPoId()).get();
            poRecord.setOrderDate(poConfirm.getOrderDate());
            poRecord.setInternalNumber(poConfirm.getInternalNumber());
            poRecord.setPoId(request.getPoId());
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
            poRecord.setCreatedAt(timestamp);
            poConfirm.setStatus(1);
            repository.save(poRecord);
            poComfirmRepository.save(poConfirm);

            return poRecord;
        }
        return null;
    }
    public StockAdjustmentTrans createAdjustmentTrans(ReceiptCreateRequest request, Long userId) {
        Response<StockAdjustmentTrans> response = new Response<>();
        User user = userClient.getUserById(userId);
        if (request.getImportType() == 1) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentTrans stockAdjustmentRecord = modelMapper.map(request, StockAdjustmentTrans.class);
            //stockAdjustmentRecord.setTransDate(date);
            stockAdjustmentRecord.setTransCode(createStockAdjustmentCode(request.getShopId()));
            stockAdjustmentRecord.setWareHouseTypeId(request.getWareHouseTypeId());
            //THIẾU SỐ HÓA ĐƠN// TRONG DB KO THẤY TRƯỜNG NÀO MAP
            StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(request.getPoId()).get();
            //THIẾU NGÀY HÓA ĐƠN// TRONG DB KO THẤY TRƯỜNG NÀO MAP
            //THIẾU SỐ NỘI BỘ// TRONG DB KO THẤY TRƯỜNG NÀO MAP

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
            stockAdjustmentRecord.setCreatedAt(timestamp);
            stockAdjustment.setStatus(1);
            stockAdjustmentTransRepository.save(stockAdjustmentRecord);
            stockAdjustmentRepository.save(stockAdjustment);

            return stockAdjustmentRecord;
        }
        return null;
    }

    public StockBorrowingTrans createBorrowingTrans(ReceiptCreateRequest request, Long userId) {
        Response<StockBorrowingTrans> response = new Response<>();
        User user = userClient.getUserById(userId);
        if (request.getImportType() == 2) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingTrans stockBorrowingTrans = modelMapper.map(request, StockBorrowingTrans.class);
            stockBorrowingTrans.setTransDate(date);
            stockBorrowingTrans.setTransCode(createStockBorrowingCode(request.getShopId()));
            stockBorrowingTrans.setWareHouseTypeId(request.getWareHouseTypeId());
            //THIẾU SỐ HÓA ĐƠN// TRONG DB KO THẤY TRƯỜNG NÀO MAP
            StockBorrowing stockBorrowing = stockBorrowingRepository.findById(request.getPoId()).get();
            //THIẾU NGÀY HÓA ĐƠN// TRONG DB KO THẤY TRƯỜNG NÀO MAP
            //THIẾU SỐ NỘI BỘ// TRONG DB KO THẤY TRƯỜNG NÀO MAP
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
            stockBorrowingTrans.setCreatedAt(timestamp);
            stockBorrowing.setStatus(1);
            stockBorrowingRepository.save(stockBorrowing);
            stockBorrowingTransRepository.save(stockBorrowingTrans);
            return stockBorrowingTrans;
        }
        return null;
    }
    public String createPoTransCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        int reciNum = repository.getQuantityPoTrans();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("IMP.");
        reciCode.append(shopRepository.findById(idShop).get().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(formatReceINumber(reciNum));
        return reciCode.toString();
    }

    public String createStockAdjustmentCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        int reciNum = stockAdjustmentTransRepository.getQuantityStockAdjustmentTrans();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("IMP.");
        reciCode.append(shopRepository.findById(idShop).get().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(formatReceINumber(reciNum));
        return reciCode.toString();
    }
    public String createStockBorrowingCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        int reciNum = stockBorrowingTransRepository.getQuantityStockBorrowingTrans();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("IMP.");
        reciCode.append(shopRepository.findById(idShop).get().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(formatReceINumber(reciNum));
        return reciCode.toString();
    }

    public String formatReceINumber(int number) {
        StringBuilder recei_num = new StringBuilder();
        int num = number + 1;

        if (num < 10) {
            recei_num.append("0000");
        }
        if (num < 100 && num >= 10) {
            recei_num.append("000");
        }
        if (num < 1000 && num >= 100) {
            recei_num.append("00");
        }
        if (num < 10000 && num >= 1000) {
            recei_num.append("0");
        }
        recei_num.append(num);

        return recei_num.toString();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
}
