package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.dto.sale.WareHouseTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.jms.JMSSender;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.utils.JMSType;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ReceiptImportService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.*;
import vn.viettel.sale.util.CreateCodeUtils;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReceiptImportServiceImpl extends BaseServiceImpl<PoTrans, PoTransRepository> implements ReceiptImportService {
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
        if (transCode!=null) transCode = transCode.toUpperCase();
        if (redInvoiceNo!=null) redInvoiceNo = redInvoiceNo.toUpperCase();
        if (fromDate == null) fromDate = LocalDateTime.of(2015,1,1,0,0);
        if (toDate == null) toDate = LocalDateTime.now();
        fromDate = DateUtils.convertFromDate(fromDate);
        toDate = DateUtils.convertToDate(toDate);
        if (type == null) {
            Page<ReceiptImportDTO> pageResponse = repository.getReceipt(shopId, 1, transCode, redInvoiceNo, fromDate, toDate, pageable);
            TotalResponse totalResponse = repository.getTotalResponsePo(shopId, 1, transCode, redInvoiceNo, fromDate, toDate);
            TotalResponse totalResponse2 = repository.getTotalResponseAdjustment(shopId, 1, transCode, redInvoiceNo, fromDate, toDate);
            TotalResponse totalResponse3 = repository.getTotalResponseBorrowing(shopId, 1, transCode, redInvoiceNo, fromDate, toDate);
            if(totalResponse.getTotalQuantity() == null) totalResponse.setTotalQuantity(0);
            if(totalResponse.getTotalPrice() == null) totalResponse.setTotalPrice(0.0);
            if(totalResponse2.getTotalQuantity() == null) totalResponse2.setTotalQuantity(0);
            if(totalResponse2.getTotalPrice() == null) totalResponse2.setTotalPrice(0.0);
            if(totalResponse3.getTotalQuantity() == null) totalResponse3.setTotalQuantity(0);
            if(totalResponse3.getTotalPrice() == null) totalResponse3.setTotalPrice(0.0);
            totalResponse.setTotalQuantity(totalResponse.getTotalQuantity() + totalResponse2.getTotalQuantity() + totalResponse3.getTotalQuantity());
            totalResponse.setTotalPrice(totalResponse.getTotalPrice() + totalResponse2.getTotalPrice() + totalResponse3.getTotalPrice());
            return new CoverResponse(pageResponse, totalResponse);
        } else if (type == 0) {
            Page<ReceiptImportListDTO> pageResponse = repository.getReceiptPo(shopId, 1, transCode, redInvoiceNo, fromDate, toDate, pageable);
            TotalResponse totalResponse = repository.getTotalResponsePo(shopId, 1, transCode, redInvoiceNo, fromDate, toDate);
            if(totalResponse.getTotalQuantity() == null) totalResponse.setTotalQuantity(0);
            if(totalResponse.getTotalPrice() == null) totalResponse.setTotalPrice(0.0);
            return new CoverResponse(pageResponse, totalResponse);
        }else if(type == 1){
            Page<ReceiptImportListDTO> pageResponse = repository.getReceiptAdjustment(shopId, 1, transCode, redInvoiceNo, fromDate, toDate, pageable);
            TotalResponse totalResponse = repository.getTotalResponseAdjustment(shopId, 1, transCode, redInvoiceNo, fromDate, toDate);
            if(totalResponse.getTotalQuantity() == null) totalResponse.setTotalQuantity(0);
            if(totalResponse.getTotalPrice() == null) totalResponse.setTotalPrice(0.0);
            return new CoverResponse(pageResponse, totalResponse);
        }else if (type == 2){
            Page<ReceiptImportListDTO> pageResponse = repository.getReceiptBorrowing(shopId, 1, transCode, redInvoiceNo, fromDate, toDate, pageable);
            TotalResponse totalResponse = repository.getTotalResponseBorrowing(shopId, 1, transCode, redInvoiceNo, fromDate, toDate);
            if(totalResponse.getTotalQuantity() == null) totalResponse.setTotalQuantity(0);
            if(totalResponse.getTotalPrice() == null) totalResponse.setTotalPrice(0.0);
            return new CoverResponse(pageResponse, totalResponse);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage createReceipt(ReceiptCreateRequest request, Long userId, Long shopId) {
        switch (request.getImportType()) {
            case 0:
                return createPoTrans(request, userId, shopId);
            case 1:
                return createAdjustmentTrans(request, userId, shopId);
            case 2:
                return createBorrowingTrans(request, userId, shopId);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage updateReceiptImport(ReceiptUpdateRequest request, Long id,String userName,Long shopId) {
        switch (request.getType()) {
            case 0:
                return updatePoTrans(request, id,userName,shopId);
            case 1:
                return updateAdjustmentTrans(request, id,userName,shopId);
            case 2:
                return  updateBorrowingTrans(request, id,userName,shopId);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage removeReceiptImport( Long id,Integer type,String userName,Long shopId) {
        switch (type) {
            case 0:
                return removePoTrans(id,userName,shopId);
            case 1:
                return removeStockAdjustmentTrans(id,userName,shopId);
            case 2:
                return removeStockBorrowingTrans(id,userName,shopId);
        }
        return ResponseMessage.DELETE_FAILED;
    }

    @Override
    public Object getForUpdate(Integer type, Long id) {
        switch (type) {
            case 0:
                return getPoTransById(id);
            case 1:
                return getStockAdjustmentById(id);
            case 2:
                return getStockBorrowingById(id);
        }
        return null;
    }

    @Override
    public List<PoConfirmDTO> getListPoConfirm(Long shopId) {
        List<PoConfirm> poConfirms = poConfirmRepository.getPoConfirm(shopId);
        List<PoConfirmDTO> rs = new ArrayList<>();
        for (PoConfirm pc : poConfirms) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoConfirmDTO dto = modelMapper.map(pc, PoConfirmDTO.class);
            rs.add(dto);
        }
        return rs;
    }
    @Override
    public List<StockAdjustmentDTO> getListStockAdjustment(Long shopId, Pageable pageable) {
        List<StockAdjustment> stockAdjustments = stockAdjustmentRepository.getStockAdjustment(shopId);
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
        LocalDateTime date1 = LocalDateTime.now().toLocalDate().atTime(LocalTime.MIN);
        LocalDateTime date2 = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);
        List<StockBorrowingDTO> stockBorrowings = stockBorrowingRepository.getStockBorrowingImport(shopId,date1,date2);
        return stockBorrowings;
    }

    @Override
    public CoverResponse<List<PoDetailDTO>, TotalResponseV1> getPoDetailByPoId(Long id, Long shopId) {
        int totalQuantity = 0;
        int countProduct = 0;
        Double totalPrice = 0D;
        Double totalPriceNotVat = 0D;
        List<PoDetail> poDetails = poDetailRepository.getPoDetailByPoIdAndPriceIsGreaterThan(id);
        ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
        List<PoDetailDTO> rs = new ArrayList<>();
        TotalResponseV1 totalResponse = new TotalResponseV1();
        if(!poDetails.isEmpty()){
            List<Product> products = productRepository.getProducts(poDetails.stream().map(item -> item.getProductId()).distinct()
                    .collect(Collectors.toList()), null);
            List<PoConfirm> poConfirms = poConfirmRepository.findAllById(poDetails.stream().map(item -> item.getPoId()).distinct()
                    .collect(Collectors.toList()));
            for (PoDetail pt : poDetails) {
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoDetailDTO dto = modelMapper.map(pt, PoDetailDTO.class);
                if(products != null){
                    for (Product product : products){
                        if(product.getId().equals(pt.getProductId())){
                            dto.setProductCode(product.getProductCode());
                            dto.setProductName(product.getProductName());
                            dto.setUnit(product.getUom1());
                            break;
                        }
                    }
                }
                if(poConfirms != null){
                    for (PoConfirm poConfirm : poConfirms){
                        if(poConfirm.getId().equals(pt.getPoId())){
                            dto.setSoNo(poConfirm.getSaleOrderNumber());
                            break;
                        }
                    }
                }
                dto.setShopName(shopDTO.getShopName());
                dto.setShopAddress(shopDTO.getAddress());
                dto.setShopContact("Tel: " + shopDTO.getPhone() + " Fax: " + shopDTO.getFax());
                dto.setTotalPrice(pt.getPrice() * pt.getQuantity());
                totalPrice +=(pt.getPrice() * pt.getQuantity());
                totalQuantity += pt.getQuantity();
                totalPriceNotVat += (pt.getPriceNotVat() * pt.getQuantity());
                countProduct ++;
                rs.add(dto);
            }
            totalResponse = new TotalResponseV1(totalQuantity, countProduct,totalPrice,totalPriceNotVat);
            CoverResponse<List<PoDetailDTO>, TotalResponseV1> response =
                    new CoverResponse(rs, totalResponse);
            return  response;
        }else return new CoverResponse<>(rs,totalResponse);
    }

    @Override
    public Object getTransDetail(Integer type, Long id, Long shopId) {
        switch (type) {
            case 0:
                    return getPoTransDetail(id);
            case 1:
                    return getStockAdjustmentTransDetail(id);
            case 2:
                    return getStockBorrowingTransDetail(id);
        }
        return null;
    }

    @Override
    public CoverResponse<List<PoDetailDTO>, TotalResponseV1> getPoDetailByPoIdAndPriceIsNull(Long id, Long shopId) {
        int totalQuantity = 0;
        int countProduct = 0;
        Double totalPrice = 0D;
        List<PoDetail> poDetails = poDetailRepository.getPoDetailByPoIdAndPriceIsLessThan(id);
        List<PoDetailDTO> rs = new ArrayList<>();
        TotalResponseV1 totalResponse = new TotalResponseV1();
        if(!poDetails.isEmpty()){
            ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
            List<Long> ids = poDetails.stream().map(item -> item.getProductId()).distinct()
                    .collect(Collectors.toList());
            List<Product> products = productRepository.getProducts(ids, null);
            List<PoConfirm> poConfirms = poConfirmRepository.findAllById(poDetails.stream().map(item -> item.getPoId()).distinct()
                    .collect(Collectors.toList()));
            for (PoDetail pt : poDetails) {
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoDetailDTO dto = modelMapper.map(pt, PoDetailDTO.class);
                if(products != null){
                    for (Product product : products){
                        if(product.getId().equals(pt.getProductId())){
                            dto.setProductCode(product.getProductCode());
                            dto.setProductName(product.getProductName());
                            dto.setUnit(product.getUom1());
                            break;
                        }
                    }
                }
                if(poConfirms != null){
                    for (PoConfirm poConfirm : poConfirms){
                        if(poConfirm.getId().equals(pt.getPoId())){
                            dto.setSoNo(poConfirm.getSaleOrderNumber());
                            break;
                        }
                    }
                }
                dto.setShopName(shopDTO.getShopName());
                dto.setShopAddress(shopDTO.getAddress());
                dto.setShopContact("Tel: " + shopDTO.getPhone() + " Fax: " + shopDTO.getFax());
                dto.setTotalPrice(pt.getPrice() * pt.getQuantity());
                totalPrice  +=(pt.getPrice() * pt.getQuantity());
                totalQuantity += pt.getQuantity();
                countProduct++;
                rs.add(dto);
            }
            totalResponse = new TotalResponseV1(totalQuantity,countProduct, totalPrice,null);
            CoverResponse<List<PoDetailDTO>, TotalResponseV1> response =
                    new CoverResponse(rs, totalResponse);

            return  response;
        }else return new CoverResponse(rs, totalResponse);
    }
    @Override
    public CoverResponse<List<StockAdjustmentDetailDTO>, TotalResponse> getStockAdjustmentDetail(Long id) {
        int totalQuantity = 0;
        Double totalPrice = 0D;
        List<StockAdjustmentDetail> adjustmentDetails = stockAdjustmentDetailRepository.getStockAdjustmentDetailByAdjustmentId(id);
        List<StockAdjustmentDetailDTO> rs = new ArrayList<>();
        List<Product> products = productRepository.getProducts(adjustmentDetails.stream().map(item -> item.getProductId()).distinct()
                .collect(Collectors.toList()), null);
        List<StockAdjustment> stockAdjustments = stockAdjustmentRepository.findAllById(adjustmentDetails.stream().map(item -> item.getAdjustmentId()).distinct()
                .collect(Collectors.toList()));

        for (StockAdjustmentDetail sad : adjustmentDetails) {
            if(sad.getPrice()==null) sad.setPrice(0D);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentDetailDTO dto = modelMapper.map(sad, StockAdjustmentDetailDTO.class);
            if(products != null){
                for (Product product : products){
                    if(product.getId().equals(sad.getProductId())){
                        dto.setProductCode(product.getProductCode());
                        dto.setProductName(product.getProductName());
                        dto.setUnit(product.getUom1());
                        break;
                    }
                }
            }
            if(stockAdjustments != null){
                for (StockAdjustment stockAdjustment : stockAdjustments){
                    if(stockAdjustment.getId().equals(sad.getAdjustmentId())){
                        dto.setLicenseNumber(stockAdjustment.getAdjustmentCode());
                        break;
                    }
                }
            }

            dto.setTotalPrice(sad.getPrice() * sad.getQuantity());
            totalPrice +=(sad.getPrice() * sad.getQuantity());
            totalQuantity += sad.getQuantity();
            rs.add(dto);
        }
        Collections.sort(rs,  Comparator.comparing(StockAdjustmentDetailDTO::getProductCode));
        TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
        CoverResponse<List<StockAdjustmentDetailDTO>, TotalResponse> response =
                new CoverResponse(rs, totalResponse);
        return  response;
    }

    @Override
    public CoverResponse<List<StockBorrowingDetailDTO>, TotalResponse> getStockBorrowingDetail(Long id) {
        int totalQuantity = 0;
        Double totalPrice = 0D;
        List<StockBorrowingDetail> borrowingDetails = stockBorrowingDetailRepository.findByBorrowingId(id);
        List<StockBorrowingDetailDTO> rs = new ArrayList<>();
        List<Product> products = productRepository.getProducts(borrowingDetails.stream().map(item -> item.getProductId()).distinct()
                .collect(Collectors.toList()), null);
        List<StockBorrowing> stockBorrowings = stockBorrowingRepository.findAllById(borrowingDetails.stream().map(item -> item.getBorrowingId()).distinct()
                .collect(Collectors.toList()));
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        for (StockBorrowingDetail sbd : borrowingDetails) {
            StockBorrowingDetailDTO dto = modelMapper.map(sbd, StockBorrowingDetailDTO.class);
            if(products != null){
                for (Product product : products){
                    if(product.getId().equals(sbd.getProductId())){
                        dto.setProductCode(product.getProductCode());
                        dto.setProductName(product.getProductName());
                        dto.setUnit(product.getUom1());
                        break;
                    }
                }
            }
            if(stockBorrowings != null){
                for (StockBorrowing stockBorrowing : stockBorrowings){
                    if(stockBorrowing.getId().equals(sbd.getBorrowingId())){
                        dto.setLicenseNumber(stockBorrowing.getPoBorrowCode());
                        break;
                    }
                }
            }
            dto.setTotalPrice(sbd.getPrice() * sbd.getQuantity());
            totalPrice +=(sbd.getPrice() * sbd.getQuantity());
            totalQuantity += sbd.getQuantity();
            rs.add(dto);
        }
        Collections.sort(rs,  Comparator.comparing(StockBorrowingDetailDTO::getProductCode));
        TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
        CoverResponse<List<StockBorrowingDetailDTO>, TotalResponse> response =
                new CoverResponse(rs, totalResponse);

        return response;
    }
    public Object getPoTransDetail(Long id) {
        List<PoTransDetailDTO> rs = new ArrayList<>();
        List<PoTransDetailDTO> rs1 = new ArrayList<>();
        PoTrans poTrans = repository.findById(id).get();
        Optional<PoConfirm> poConfirm = null;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        if (poTrans.getFromTransId() == null) {
            if(poTrans.getPoId()!=null){
                 poConfirm = poConfirmRepository.findById(poTrans.getPoId());
                 if(!poConfirm.isPresent()) throw new ValidateException(ResponseMessage.RECORD_WRONG);
            }
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetail0(id);
            List<Product> products = productRepository.getProducts(poTransDetails.stream().map(item -> item.getProductId()).distinct()
                    .collect(Collectors.toList()), null);

            for (int i = 0; i < poTransDetails.size(); i++) {
                PoTransDetail ptd = poTransDetails.get(i);
                PoTransDetailDTO dto = modelMapper.map(ptd, PoTransDetailDTO.class);
                if(products != null){
                    for (Product product : products){
                        if(product.getId().equals(ptd.getProductId())){
                            dto.setProductCode(product.getProductCode());
                            dto.setProductName(product.getProductName());
                            dto.setUnit(product.getUom1());
                            break;
                        }
                    }
                }
                dto.setTotalPrice(ptd.getPrice() * ptd.getQuantity());
                dto.setSoNo(poConfirm.get().getSaleOrderNumber());
                dto.setExport(ptd.getReturnAmount());
                rs.add(dto);
            }
            List<PoTransDetail> poTransDetails1 = poTransDetailRepository.getPoTransDetail1(id);
            List<Product> products1 = productRepository.getProducts(poTransDetails1.stream().map(item -> item.getProductId()).distinct()
                    .collect(Collectors.toList()), null);
            for (int i = 0; i < poTransDetails1.size(); i++) {
                PoTransDetail ptd = poTransDetails1.get(i);
                PoTransDetailDTO dto = modelMapper.map(ptd, PoTransDetailDTO.class);
                if(products1 != null){
                    for (Product product : products1){
                        if(product.getId().equals(ptd.getProductId())){
                            dto.setProductCode(product.getProductCode());
                            dto.setProductName(product.getProductName());
                            dto.setUnit(product.getUom1());
                            break;
                        }
                    }
                }
                dto.setTotalPrice(ptd.getPrice() * ptd.getQuantity());
                dto.setSoNo(poConfirm!=null?poConfirm.get().getSaleOrderNumber():null);
                dto.setExport(ptd.getReturnAmount());
                rs1.add(dto);
            }
            Collections.sort(rs,  Comparator.comparing(PoTransDetailDTO::getProductCode));
            Collections.sort(rs1,  Comparator.comparing(PoTransDetailDTO::getProductCode));
            CoverResponse<List<PoTransDetailDTO>, List<PoTransDetailDTO>> response =
                    new CoverResponse(rs, rs1);
            return response;
        } else {
            PoTrans poTransImport = repository.findById(poTrans.getFromTransId()).orElseThrow(()-> new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED));
            List<PoTransDetail> poTransDetailImport = poTransDetailRepository.getPoTransDetail(poTransImport.getId());
            if(poTransDetailImport == null) throw new ValidateException(ResponseMessage.RECORD_WRONG);
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(id);
            List<Product> products = productRepository.getProducts(poTransDetails.stream().map(item -> item.getProductId()).distinct()
                    .collect(Collectors.toList()), null);

            for (int i = 0; i < poTransDetails.size(); i++) {
                PoTransDetail ptd = poTransDetails.get(i);
                PoTransDetailDTO dto = modelMapper.map(ptd, PoTransDetailDTO.class);
                if(products != null){
                    for (Product product : products){
                        if(product.getId().equals(ptd.getProductId())){
                            dto.setProductCode(product.getProductCode());
                            dto.setProductName(product.getProductName());
                            dto.setUnit(product.getUom1());
                            break;
                        }
                    }
                }
                dto.setTotalPrice(ptd.getPrice() * ptd.getQuantity());
                dto.setExport(poTransDetailImport.get(i).getReturnAmount());
                dto.setImportQuantity(poTransDetailImport.get(i).getQuantity());
                rs.add(dto);
            }
            Collections.sort(rs,  Comparator.comparing(PoTransDetailDTO::getProductCode));
            CoverResponse<List<PoTransDetailDTO>, List<PoTransDetailDTO>> response =
                    new CoverResponse(rs, rs1);
            return  response;
        }
    }
    public CoverResponse<List<StockAdjustmentTransDetailDTO>, List<StockAdjustmentTransDetailDTO>> getStockAdjustmentTransDetail(Long id) {
        List<StockAdjustmentTransDetail> adjustmentTransDetails = stockAdjustmentTransDetailRepository.getStockAdjustmentTransDetailsByTransId(id);
        List<StockAdjustmentTransDetailDTO> rs = new ArrayList<>();
        List<Product> products = productRepository.getProducts(adjustmentTransDetails.stream().map(item -> item.getProductId()).distinct()
                .collect(Collectors.toList()), null);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        for (StockAdjustmentTransDetail satd : adjustmentTransDetails) {
            StockAdjustmentTransDetailDTO dto = modelMapper.map(satd, StockAdjustmentTransDetailDTO.class);
            if(products != null){
                for (Product product : products){
                    if(product.getId().equals(satd.getProductId())){
                        dto.setProductCode(product.getProductCode());
                        dto.setProductName(product.getProductName());
                        dto.setUnit(product.getUom1());
                        break;
                    }
                }
            }
            dto.setTotalPrice(satd.getPrice() * satd.getQuantity());
            rs.add(dto);
        }
        Collections.sort(rs,  Comparator.comparing(StockAdjustmentTransDetailDTO::getProductCode));
        CoverResponse<List<StockAdjustmentTransDetailDTO>, List<StockAdjustmentTransDetailDTO>> response =
                new CoverResponse(rs, new ArrayList<>());
        return response;
    }

    public CoverResponse<List<StockBorrowingTransDetailDTO>, List<StockBorrowingTransDetailDTO>> getStockBorrowingTransDetail(Long id) {
        List<StockBorrowingTransDetail> borrowingTransDetails = stockBorrowingTransDetailRepository.getStockBorrowingTransDetailByTransId(id);
        List<StockBorrowingTransDetailDTO> rs = new ArrayList<>();
        List<Product> products = productRepository.getProducts(borrowingTransDetails.stream().map(item -> item.getProductId()).distinct()
                .collect(Collectors.toList()), null);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        for (StockBorrowingTransDetail sbtd : borrowingTransDetails) {
            StockBorrowingTransDetailDTO dto = modelMapper.map(sbtd, StockBorrowingTransDetailDTO.class);
            if(products != null){
                for (Product product : products){
                    if(product.getId().equals(sbtd.getProductId())){
                        dto.setProductCode(product.getProductCode());
                        dto.setProductName(product.getProductName());
                        dto.setUnit(product.getUom1());
                        break;
                    }
                }
            }
            dto.setTotalPrice(sbtd.getPrice() * sbtd.getQuantity());
            rs.add(dto);
        }
        Collections.sort(rs,  Comparator.comparing(StockBorrowingTransDetailDTO::getProductCode));
        CoverResponse<List<StockBorrowingTransDetailDTO>, List<StockBorrowingTransDetailDTO>> response =
                new CoverResponse(rs, new ArrayList<>());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage setNotImport(Long id,String userName, NotImportRequest request) {
        PoConfirm poConfirm = poConfirmRepository.findById(id).get();
        if (poConfirm != null) {
            poConfirm.setStatus(4);
            poConfirm.setDenyReason(request.getReasonDeny());
            poConfirm.setDenyUser(userName);
            poConfirm.setDenyDate(LocalDateTime.now());
            poConfirmRepository.save(poConfirm);
            return ResponseMessage.NOT_IMPORT_SUCCESS;
        }
        return null;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public WareHouseTypeDTO getWareHouseTypeName(Long shopId) {
        Long wareHouseTypeId = customerTypeClient.getWarehouseTypeByShopId(shopId);
        if(wareHouseTypeId == null) throw new ValidateException(ResponseMessage.WAREHOUSE_TYPE_ID_MUST_NOT_BE_NUll);
        Optional<WareHouseType> wareHouseType = wareHouseTypeRepository.findById(wareHouseTypeId);
        if (wareHouseType.isPresent()) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            WareHouseTypeDTO dto = modelMapper.map(wareHouseType.get(), WareHouseTypeDTO.class);
            return dto;
        }
        throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public ResponseMessage createPoTrans(ReceiptCreateRequest request, Long userId, Long shopId) {
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        List<String> lstRedInvoiceNo = repository.getRedInvoiceNo();
        if(lstRedInvoiceNo.contains(request.getRedInvoiceNo().trim())) throw new ValidateException(ResponseMessage.RED_INVOICE_NO_IS_EXIST);
        if(request.getRedInvoiceNo() != null && request.getRedInvoiceNo().length() >50) throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
        checkNoteLength(request.getNote());
        if (request.getPoId() == null) {
            List<String> lstInternalNumber = repository.getInternalNumber();
            List<String> lstPoCoNumber = repository.getPoCoNumber();
            if(request.getInternalNumber().length()>50) throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
            if(request.getPoCoNumber().length()>50) throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
            if(lstPoCoNumber.contains(request.getPoCoNumber())) throw new ValidateException(ResponseMessage.PO_NO_IS_EXIST);
            if(lstInternalNumber.contains(request.getInternalNumber())) throw  new ValidateException(ResponseMessage.INTERNAL_NUMBER_IS_EXIST);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoTrans poRecord = modelMapper.map(request, PoTrans.class);
            poRecord.setTransDate(LocalDateTime.now());
            poRecord.setTransCode(createPoTransCode(shopId));
            poRecord.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
            poRecord.setRedInvoiceNo(request.getRedInvoiceNo().trim());
            poRecord.setPocoNumber(request.getPoCoNumber());
            poRecord.setOrderDate(request.getOrderDate());
            poRecord.setInternalNumber(request.getInternalNumber().trim());
            poRecord.setShopId(shopId);
            poRecord.setStatus(1);
            poRecord.setType(1);
            repository.save(poRecord);
            Integer total = 0;
            if(request.getLst() != null){
                List<Long> productIds = request.getLst().stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList());
                List<Product> products = productRepository.getProducts(productIds, null);
                if(products.size() != productIds.size()) throw new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS);

                for (ReceiptCreateDetailRequest rcdr : request.getLst()) {
                    PoTransDetail poTransDetail = modelMapper.map(rcdr, PoTransDetail.class);
                    poTransDetail.setTransId(poRecord.getId());
                    poTransDetail.setTransDate(poRecord.getTransDate());
                    poTransDetail.setProductId(rcdr.getProductId());
                    poTransDetail.setPrice(0D);
                    poTransDetail.setAmount(0D);
                    poTransDetail.setAmountNotVat(0D);
                    poTransDetail.setReturnAmount(0);
                    poTransDetail.setPriceNotVat(0D);
                    poTransDetail.setShopId(shopId);
                    poTransDetail.setTransDate(poRecord.getTransDate());
                    total += rcdr.getQuantity();
                    poTransDetailRepository.save(poTransDetail);
                    StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(rcdr.getProductId(), customerTypeDTO.getWareHouseTypeId(),shopId);
                    if (stockTotal == null){
                        StockTotal newStockTotal = new StockTotal();
                        newStockTotal.setProductId(rcdr.getProductId());
                        newStockTotal.setQuantity(rcdr.getQuantity());
                        newStockTotal.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
                        newStockTotal.setShopId(shopId);
                        newStockTotal.setStatus(1);
                        stockTotalRepository.save(newStockTotal);
                    }else{
                        if (stockTotal.getQuantity() == null) {
                            stockTotal.setQuantity(0);
                        }
                        stockTotal.setQuantity(stockTotal.getQuantity() + rcdr.getQuantity());
                        stockTotalRepository.save(stockTotal);
                    }
                }
            } else throw  new ValidateException(ResponseMessage.PLEASE_IMPORT_PRODUCTS);
            poRecord.setTotalQuantity(total);
            poRecord.setTotalAmount(0D);
            poRecord.setNumSku(request.getLst().size());
            poRecord = repository.save(poRecord);
            sendSynRequest(JMSType.po_trans, Arrays.asList(poRecord.getId()));
            return ResponseMessage.CREATED_SUCCESSFUL;
        } else {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoTrans poRecord = modelMapper.map(request, PoTrans.class);
            PoConfirm poConfirm = poConfirmRepository.findById(request.getPoId()).get();
            LocalDateTime date = LocalDateTime.now();
            poRecord.setTransDate(date);
            poRecord.setTransCode(createPoTransCode(shopId));
            poRecord.setShopId(shopId);
            poRecord.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
            poRecord.setOrderDate(poConfirm.getOrderDate());
            poRecord.setInternalNumber(poConfirm.getInternalNumber());
            poRecord.setPoId(poConfirm.getId());
            poRecord.setTotalAmount(poConfirm.getTotalAmount());
            poRecord.setTotalQuantity(poConfirm.getTotalQuantity());
            poRecord.setPocoNumber(poConfirm.getPoCoNumber());
            poRecord.setPoNumber(poConfirm.getPoNumber());
            poRecord.setType(1);
            poRecord.setStatus(1);
            poRecord.setIsDebit(false);
            repository.save(poRecord);
            List<PoDetail> poDetails = poDetailRepository.findByPoId(poConfirm.getId());
            Set<Long> countNumSKU = new HashSet<>();
            for (PoDetail pod : poDetails) {
                countNumSKU.add(pod.getProductId());
                PoTransDetail poTransDetail = modelMapper.map(pod, PoTransDetail.class);
                poTransDetail.setTransId(poRecord.getId());
                poTransDetail.setAmount(pod.getQuantity() * pod.getPrice());
                poTransDetail.setReturnAmount(0);
                poTransDetail.setTransDate(date);
                poTransDetailRepository.save(poTransDetail);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(pod.getProductId(), customerTypeDTO.getWareHouseTypeId(),shopId);
                if (stockTotal == null){
                    StockTotal stockTotalNew = new StockTotal();
                    stockTotalNew.setProductId(pod.getProductId());
                    stockTotalNew.setQuantity(pod.getQuantity());
                    stockTotalNew.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
                    stockTotalNew.setShopId(shopId);
                    stockTotalNew.setStatus(1);
                    stockTotalRepository.save(stockTotalNew);
                }else {
                    if (stockTotal.getQuantity() == null) stockTotal.setQuantity(0);
                    stockTotal.setQuantity(stockTotal.getQuantity() + pod.getQuantity());
                    stockTotalRepository.save(stockTotal);
                }
            }
            poRecord.setNumSku(countNumSKU.size());
            poRecord.setNote(request.getNote());
            poConfirm.setStatus(1);
            poRecord = repository.save(poRecord);
            sendSynRequest(JMSType.po_trans, Arrays.asList(poRecord.getId()));
            poConfirm = poConfirmRepository.save(poConfirm);
            sendSynRequest(JMSType.po_confirm, Arrays.asList(poConfirm.getId()));
            return ResponseMessage.CREATED_SUCCESSFUL;
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    public ResponseMessage createAdjustmentTrans(ReceiptCreateRequest request, Long userId, Long shopId) {

        Response<StockAdjustmentTrans> response = new Response<>();
        UserDTO user = userClient.getUserByIdV1(userId);
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        CustomerDTO cus = customerClient.getCusDefault(shopId);
        if(cus.getId() == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        if (request.getImportType() == 1) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentTrans stockAdjustmentRecord = modelMapper.map(request, StockAdjustmentTrans.class);
            StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(request.getPoId()).get();
            Response<ApParamDTO> reason = apparamClient.getReasonV1(stockAdjustment.getReasonId());
            if(reason.getData() == null || reason.getData().getId() == null) throw new ValidateException(ResponseMessage.REASON_NOT_FOUND);
            stockAdjustmentRecord.setTransDate(LocalDateTime.now());
            stockAdjustmentRecord.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
            stockAdjustmentRecord.setTransCode(stockAdjustment.getAdjustmentCode());
            stockAdjustmentRecord.setAdjustmentDate(stockAdjustment.getAdjustmentDate());
            stockAdjustmentRecord.setShopId(shopId);
            stockAdjustmentRecord.setRedInvoiceNo(createRedInvoiceCodeAdjust(shopId));
            stockAdjustmentRecord.setInternalNumber(createInternalCodeAdjust(shopId));
            stockAdjustmentRecord.setType(1);
            stockAdjustmentRecord.setStatus(1);
            stockAdjustmentRecord.setAdjustmentId(request.getPoId());
            stockAdjustmentTransRepository.save(stockAdjustmentRecord);
            List<StockAdjustmentDetail> stockAdjustmentDetails = stockAdjustmentDetailRepository.getStockAdjustmentDetailByAdjustmentId(stockAdjustment.getId());
            Integer totalQuantity = 0;
            Double totalAmount = 0D;
            LocalDateTime date = LocalDateTime.now();
            SaleOrder order = new SaleOrder();
            order.setType(3);
            order.setOrderNumber(stockAdjustmentRecord.getRedInvoiceNo());
            order.setOrderDate(date);
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
            List<Price> prices = productPriceRepository.findProductPrice(stockAdjustmentDetails.stream().map(item -> item.getProductId()).distinct()
                    .collect(Collectors.toList()), customerTypeDTO.getWareHouseTypeId(), LocalDateTime.now());

            for (StockAdjustmentDetail sad : stockAdjustmentDetails) {
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                StockAdjustmentTransDetail stockAdjustmentTransDetail = modelMapper.map(sad, StockAdjustmentTransDetail.class);
                stockAdjustmentTransDetail.setTransId(stockAdjustmentRecord.getId());
                stockAdjustmentTransDetail.setTransDate(LocalDateTime.now());
                totalQuantity += sad.getQuantity();
                totalAmount += sad.getPrice() * sad.getQuantity();
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(sad.getProductId(), customerTypeDTO.getWareHouseTypeId(),shopId);
                if (stockTotal == null)
                    response.setFailure(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                if (stockTotal.getQuantity() == null) {
                    stockTotal.setQuantity(0);
                }
                stockTotal.setUpdatedBy(user.getUserAccount());
                stockAdjustmentTransDetail.setStockQuantity(stockTotal.getQuantity());
                stockAdjustmentTransDetail.setOriginalQuantity(sad.getQuantity());
                stockTotal.setQuantity(stockTotal.getQuantity() + sad.getQuantity());
                stockTotalRepository.save(stockTotal);
                stockAdjustmentTransDetailRepository.save(stockAdjustmentTransDetail);
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
                saleOrderDetail.setOrderDate(date);
                saleOrderDetailRepository.save(saleOrderDetail);
            }
            order.setAmount(totalAmount);
            order.setTotalPromotion(0D);
            order.setTotal(totalAmount);
            order.setTotalPaid(totalAmount);
            saleOrderRepository.save(order);
            stockAdjustmentRecord.setTotalQuantity(totalQuantity);
            stockAdjustmentRecord.setTotalAmount(totalAmount);
            stockAdjustmentRecord.setCreatedBy(user.getUserAccount());
            stockAdjustmentRecord.setNote(request.getNote() == null ? stockAdjustment.getDescription() : request.getNote());
            stockAdjustment.setStatus(3);
            stockAdjustment.setUpdatedBy(user.getUserAccount());
            stockAdjustmentRecord = stockAdjustmentTransRepository.save(stockAdjustmentRecord);
            stockAdjustment = stockAdjustmentRepository.save(stockAdjustment);
            
            sendSynRequest(JMSType.stock_adjustment, Arrays.asList(stockAdjustment.getId()));
            sendSynRequest(JMSType.stock_adjustment_trans, Arrays.asList(stockAdjustmentRecord.getId()));
            return ResponseMessage.CREATED_SUCCESSFUL;
        }
        return null;
    }

    private void sendSynRequest(String type, List<Long> listId) {
        try {
        	if(!listId.isEmpty()) {
        		jmsSender.sendMessage(type, listId);
        	}
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

    public ResponseMessage createBorrowingTrans(ReceiptCreateRequest request, Long userId, Long shopId) {

        Response<StockBorrowingTrans> response = new Response<>();
        UserDTO user = userClient.getUserByIdV1(userId);
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if (request.getImportType() == 2) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingTrans stockBorrowingTrans = modelMapper.map(request, StockBorrowingTrans.class);
            StockBorrowing stockBorrowing = stockBorrowingRepository.findById(request.getPoId()).get();
            stockBorrowingTrans.setTransDate(LocalDateTime.now());
            stockBorrowingTrans.setTransCode(createBorrowingTransCode(shopId));
            stockBorrowingTrans.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
            stockBorrowingTrans.setRedInvoiceNo(stockBorrowing.getPoBorrowCode());
            stockBorrowingTrans.setBorrowDate(stockBorrowing.getBorrowDate());
            stockBorrowingTrans.setFromShopId(stockBorrowing.getShopId());
            stockBorrowingTrans.setToShopId(stockBorrowing.getToShopId());
            stockBorrowingTrans.setShopId(shopId);
            stockBorrowingTrans.setType(1);
            stockBorrowingTrans.setStatus(1);
            stockBorrowingTrans.setStockBorrowingId(request.getPoId());
            stockBorrowingTransRepository.save(stockBorrowingTrans);
            List<StockBorrowingDetail> stockBorrowingDetails = stockBorrowingDetailRepository.findByBorrowingId(stockBorrowing.getId());
            Integer totalQuantity = 0;
            Double totalAmount = 0D;

            for (StockBorrowingDetail sbd : stockBorrowingDetails) {
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                StockBorrowingTransDetail stockBorrowingTransDetail = modelMapper.map(sbd, StockBorrowingTransDetail.class);
                stockBorrowingTransDetail.setTransId(stockBorrowingTrans.getId());
                stockBorrowingTransDetail.setTransDate(LocalDateTime.now());
                totalQuantity += sbd.getQuantity();
                totalAmount += sbd.getPrice() * sbd.getQuantity();
                Product product = productRepository.findById(sbd.getProductId()).get();
                if (product == null) response.setFailure(ResponseMessage.NO_CONTENT);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(sbd.getProductId(), customerTypeDTO.getWareHouseTypeId(),shopId);
                if (stockTotal == null)
                    throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                if (stockTotal.getQuantity() == null) {
                    stockTotal.setQuantity(0);
                }
                stockTotal.setQuantity(stockTotal.getQuantity() + sbd.getQuantity());
                stockTotal.setUpdatedBy(user.getUserAccount());
                stockTotalRepository.save(stockTotal);
                stockBorrowingTransDetailRepository.save(stockBorrowingTransDetail);
            }
            stockBorrowingTrans.setTotalAmount(totalAmount);
            stockBorrowingTrans.setTotalQuantity(totalQuantity);
            stockBorrowingTrans.setNote(request.getNote()==null ? stockBorrowing.getNote() : request.getNote());
            stockBorrowing.setStatusImport(2);
            stockBorrowing = stockBorrowingRepository.save(stockBorrowing);
            if(null != stockBorrowing) {
                sendSynRequest(JMSType.stock_borrowing, Arrays.asList(stockBorrowing.getId()));
            }
            stockBorrowingTrans = stockBorrowingTransRepository.save(stockBorrowingTrans);
            if(null != stockBorrowingTrans) {
                sendSynRequest(JMSType.stock_borrowing_trans, Arrays.asList(stockBorrowingTrans.getId()));
            }
            return ResponseMessage.CREATED_SUCCESSFUL;
        }
        return null;
    }

    private void checkNoteLength(String note){
        if(note != null && note.length()>250)
            throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
    }

    public ResponseMessage updatePoTrans(ReceiptUpdateRequest request, Long id,String userName,Long shopId) {
        checkNoteLength(request.getNote());
        PoTrans poTrans = repository.getPoTransById(id);
        if(poTrans==null) throw new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED);
        List<String> lstRedInvoiceNo = repository.getRedInvoiceNo();
        List<String> lstPoNo = repository.getRedInvoiceNo();
        List<String> lstInternalNumber = repository.getRedInvoiceNo();
        lstRedInvoiceNo.remove(poTrans.getRedInvoiceNo());
        lstPoNo.remove(poTrans.getPocoNumber());
        lstInternalNumber.remove(poTrans.getInternalNumber());
        if (request.getRedInvoiceNo() != null && request.getRedInvoiceNo().getBytes(StandardCharsets.UTF_8).length>50) throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
        if(lstRedInvoiceNo.contains(request.getRedInvoiceNo().trim())) throw new ValidateException(ResponseMessage.RED_INVOICE_NO_IS_EXIST);
        poTrans.setNote(request.getNote());
        poTrans.setRedInvoiceNo(request.getRedInvoiceNo().trim());
        
        if (DateUtils.formatDate2StringDate(poTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))){
            if (poTrans.getPoId() == null) {
                if(request.getInternalNumber() != null && request.getInternalNumber().length()>50) throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
                if(request.getPoCoNumber() == null || request.getPoCoNumber().length()>50) throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
                if(lstPoNo.contains(request.getPoCoNumber())) throw new ValidateException(ResponseMessage.PO_NO_IS_EXIST);
                if(lstInternalNumber.contains(request.getInternalNumber())) throw new ValidateException(ResponseMessage.INTERNAL_NUMBER_IS_EXIST);
                poTrans.setPocoNumber(request.getPoCoNumber());
                poTrans.setOrderDate(request.getOrderDate());
                poTrans.setInternalNumber(request.getInternalNumber());
                if(request.getLstUpdate()==null) throw  new ValidateException(ResponseMessage.NOT_EXISTS);
                if (!request.getLstUpdate().isEmpty()) {
                    List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetail(id);
                    List<Long> pIds = poTransDetails.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList());
                    request.getLstUpdate().stream().map(e-> e.getProductId()).collect(Collectors.toList()).forEach(pIds::add);
                    List<Product> products = productRepository.findAllById(pIds);
                    List<Long> listUpdate = request.getLstUpdate().stream().map(e-> e.getId()).collect(Collectors.toList());
                    /** delete **/
                    for (PoTransDetail podId : poTransDetails) {
                        if (!listUpdate.contains(podId.getId())) {
                            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(podId.getProductId(), poTrans.getWareHouseTypeId(),shopId);
                            if (stockTotal == null) throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                            stockTotal.setQuantity(stockTotal.getQuantity() - podId.getQuantity());
                            if(stockTotal.getQuantity() < 0) {
                                String productName = "";
                                if(products != null){
                                    for(Product product : products){
                                        if(product.getId().equals(podId.getProductId())){
                                            productName = product.getProductName();
                                            break;
                                        }
                                    }
                                }
                                throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SS, productName, poTrans.getTransCode());
                            }
                            stockTotal.setUpdatedBy(userName);
                            stockTotalRepository.save(stockTotal);
                            poTransDetailRepository.delete(podId);
                        }
                    }
                    Integer total = 0;
                    for (int i = 0; i < request.getLstUpdate().size(); i++) {
                        ReceiptCreateDetailRequest rcdr = request.getLstUpdate().get(i);
                        /** update **/
                        if (rcdr.getId() != null) {
                            PoTransDetail po = null;
                            for (PoTransDetail podId : poTransDetails) {
                                if(podId.getId().equals(rcdr.getId())){
                                    po = podId;
                                    break;
                                }
                            }
                            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(rcdr.getProductId(), poTrans.getWareHouseTypeId(),shopId);
                            if (stockTotal == null) throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                            stockTotal.setQuantity(stockTotal.getQuantity() - po.getQuantity() + rcdr.getQuantity());
                            if(stockTotal.getQuantity()<0){
                                String productName = "";
                                if(products != null){
                                    for(Product product : products){
                                        if(product.getId().equals(rcdr.getProductId())){
                                            productName = product.getProductName();
                                            break;
                                        }
                                    }
                                }
                                throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SS,productName,poTrans.getTransCode());
                            }
                            po.setQuantity(rcdr.getQuantity());
                            po.setTransDate(poTrans.getTransDate());
                            total += po.getQuantity();
                            poTransDetailRepository.save(po);
                            stockTotalRepository.save(stockTotal);
                        }
                        /** create new **/
                        else if (rcdr.getId()== -1) {
                            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                            PoTransDetail poTransDetail = modelMapper.map(rcdr, PoTransDetail.class);
                            poTransDetail.setTransId(poTrans.getId());
                            poTransDetail.setPrice(0D);
                            poTransDetail.setPriceNotVat(0D);
                            poTransDetail.setShopId(poTrans.getShopId());
                            poTransDetail.setTransDate(poTrans.getTransDate());
                            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(rcdr.getProductId(), poTrans.getWareHouseTypeId(),shopId);
                            if (stockTotal == null){
                                StockTotal newStockTotal = new StockTotal();
                                newStockTotal.setProductId(rcdr.getProductId());
                                newStockTotal.setQuantity(rcdr.getQuantity());
                                newStockTotal.setWareHouseTypeId(poTrans.getWareHouseTypeId());
                                newStockTotal.setShopId(shopId);
                                newStockTotal.setStatus(1);
                                stockTotalRepository.save(newStockTotal);
                            }else {
                                if (stockTotal.getQuantity() == null) stockTotal.setQuantity(0);
                                stockTotal.setQuantity(stockTotal.getQuantity() + rcdr.getQuantity());
                                stockTotalRepository.save(stockTotal);
                            }
                            total += poTransDetail.getQuantity();
                            poTransDetailRepository.save(poTransDetail);
                        }
                        poTrans.setTotalQuantity(total);
                        repository.save(poTrans);
                    }
                }else throw new ValidateException(ResponseMessage.PLEASE_IMPORT_PRODUCTS);
            }
            poTrans.setUpdatedBy(userName);
            poTrans.setNumSku(request.getLstUpdate().size());
            poTrans = repository.save(poTrans);
            sendSynRequest(JMSType.po_trans, Arrays.asList(poTrans.getId()));
        }
        return ResponseMessage.UPDATE_SUCCESSFUL;
    }
    public ResponseMessage updateAdjustmentTrans(ReceiptUpdateRequest request, Long id,String userName,Long shopId) {

        StockAdjustmentTrans adjustmentTrans = stockAdjustmentTransRepository.getStockAdjustmentTransById(id);
        checkNoteLength(request.getNote());
        if (DateUtils.formatDate2StringDate(adjustmentTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            adjustmentTrans.setNote(request.getNote());
            adjustmentTrans.setUpdatedBy(userName);
            adjustmentTrans.setInternalNumber(request.getInternalNumber());
            stockAdjustmentTransRepository.save(adjustmentTrans);
            sendSynRequest(JMSType.stock_adjustment_trans, Arrays.asList(adjustmentTrans.getId()));
            return ResponseMessage.UPDATE_SUCCESSFUL;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);

    }
    public ResponseMessage updateBorrowingTrans(ReceiptUpdateRequest request, Long id,String userName,Long shopId) {

        StockBorrowingTrans borrowingTrans = stockBorrowingTransRepository.getStockBorrowingTransById(id);
        checkNoteLength(request.getNote());
        if (DateUtils.formatDate2StringDate(borrowingTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            borrowingTrans.setNote(request.getNote());
            borrowingTrans.setUpdatedBy(userName);
            borrowingTrans.setInternalNumber(request.getInternalNumber());
            borrowingTrans = stockBorrowingTransRepository.save(borrowingTrans);
            if(null != borrowingTrans) {
                sendSynRequest(JMSType.stock_borrowing_trans, Arrays.asList(borrowingTrans.getId()));
            }
            return ResponseMessage.UPDATE_SUCCESSFUL;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);

    }
    public ResponseMessage removePoTrans(Long id,String userName,Long shopId) {

        PoTrans poTrans = repository.getPoTransById(id);
        if(poTrans== null) throw new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED);
        if (DateUtils.formatDate2StringDate(poTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
            for (PoTransDetail ptd : poTransDetails) {
                Optional<Product> product = productRepository.findById(ptd.getProductId());
                if(!product.isPresent()) throw  new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(ptd.getProductId(), poTrans.getWareHouseTypeId(),shopId);
                int quantity = stockTotal.getQuantity() - ptd.getQuantity();
                if(quantity<0) throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SS,product.get().getProductName(),poTrans.getTransCode());
                stockTotal.setQuantity(quantity);
                stockTotal.setUpdatedBy(userName);
                stockTotalRepository.save(stockTotal);
            }
            if (poTrans.getPoId() != null) {
                PoConfirm poConfirm = poConfirmRepository.findById(poTrans.getPoId()).get();
                poConfirm.setStatus(0);
                poConfirm.setUpdatedBy(userName);
                poConfirm = poConfirmRepository.save(poConfirm);
                sendSynRequest(JMSType.po_confirm, Arrays.asList(poConfirm.getId()));
            }
            poTrans.setStatus(-1);
            poTrans.setUpdatedBy(userName);
            poTrans = repository.save(poTrans);
            sendSynRequest(JMSType.po_trans, Arrays.asList(poTrans.getId()));
            return ResponseMessage.DELETE_SUCCESSFUL;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }
    public ResponseMessage removeStockAdjustmentTrans(Long id,String userName,Long shopId) {

        StockAdjustmentTrans stockAdjustmentTrans = stockAdjustmentTransRepository.getAdjustTransImportById(id);
        if(stockAdjustmentTrans==null) throw new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);
        if (DateUtils.formatDate2StringDate(stockAdjustmentTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            List<StockAdjustmentTransDetail> stockAdjustmentTransDetails = stockAdjustmentTransDetailRepository.getStockAdjustmentTransDetailsByTransId(stockAdjustmentTrans.getId());
            for (StockAdjustmentTransDetail satd : stockAdjustmentTransDetails) {
                Optional<Product> product = productRepository.findById(satd.getProductId());
                if(!product.isPresent()) throw  new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(satd.getProductId(), stockAdjustmentTrans.getWareHouseTypeId(),shopId);
                if(stockTotal == null) throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                stockTotal.setQuantity(stockTotal.getQuantity() - satd.getQuantity());
                if(stockTotal.getQuantity()<0) throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SS,product.get().getProductName(),stockAdjustmentTrans.getTransCode());
                stockTotal.setUpdatedBy(userName);
                stockTotalRepository.save(stockTotal);
            }
            SaleOrder order = saleOrderRepository.getSaleOrderByOrderNumber(stockAdjustmentTrans.getRedInvoiceNo()).orElseThrow(() -> new ValidateException(ResponseMessage.SALE_ORDER_NOT_FOUND));
            String orderNumber = order.getOrderNumber();
            List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findSaleOrderDetail(order.getId(), null);
            saleOrderDetailRepository.deleteAll(saleOrderDetails);
            saleOrderRepository.delete(order);
            StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(stockAdjustmentTrans.getAdjustmentId()).orElseThrow(()-> new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_DOSE_NOT_EXISTED));
            stockAdjustment.setStatus(1);
            stockAdjustment.setUpdatedBy(userName);
            stockAdjustmentTrans.setUpdatedBy(userName);
            stockAdjustmentTrans.setStatus(-1);
            stockAdjustmentRepository.save(stockAdjustment);
            stockAdjustmentTransRepository.save(stockAdjustmentTrans);
            
            sendSynRequest(JMSType.stock_adjustment, Arrays.asList(stockAdjustment.getId()));
            sendSynRequest(JMSType.stock_adjustment_trans, Arrays.asList(stockAdjustmentTrans.getId()));
            sendSynRequestByCode(JMSType.sale_orders_adjustment, Arrays.asList(orderNumber));
            return ResponseMessage.DELETE_SUCCESSFUL;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }

    public ResponseMessage removeStockBorrowingTrans(Long id,String userName,Long shopId) {

        StockBorrowingTrans stockBorrowingTrans = stockBorrowingTransRepository.getStockBorrowingTransById(id);
        if (DateUtils.formatDate2StringDate(stockBorrowingTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            List<StockBorrowingTransDetail> stockBorrowingTransDetails = stockBorrowingTransDetailRepository.getStockBorrowingTransDetailByTransId(stockBorrowingTrans.getId());
            for (StockBorrowingTransDetail sbtd : stockBorrowingTransDetails) {
                Optional<Product> product = productRepository.findById(sbtd.getProductId());
                if(!product.isPresent()) throw  new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeIdAndShopId(sbtd.getProductId(), stockBorrowingTrans.getWareHouseTypeId(),shopId);
                stockTotal.setQuantity(stockTotal.getQuantity() - sbtd.getQuantity());
                if(stockTotal.getQuantity()<0){
                    throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SS,product.get().getProductName(),stockBorrowingTrans.getTransCode());
                }
                stockTotal.setUpdatedBy(userName);
                stockTotalRepository.save(stockTotal);
            }
            StockBorrowing stockBorrowing = stockBorrowingRepository.findById(stockBorrowingTrans.getStockBorrowingId()).get();

            stockBorrowing.setStatusImport(1);
            stockBorrowing.setUpdatedBy(userName);
            stockBorrowingTrans.setStatus(-1);
            stockBorrowingTrans.setUpdatedBy(userName);
            stockBorrowingTrans = stockBorrowingTransRepository.save(stockBorrowingTrans);
            if(null != stockBorrowingTrans) {
                sendSynRequest(JMSType.stock_borrowing_trans, Arrays.asList(stockBorrowingTrans.getId()));
            }
            stockBorrowing = stockBorrowingRepository.save(stockBorrowing);
            if(null != stockBorrowing) {
                sendSynRequest(JMSType.stock_borrowing, Arrays.asList(stockBorrowing.getId()));
            }
            return ResponseMessage.DELETE_SUCCESSFUL;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public String createPoTransCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        String code = repository.getQuantityPoTrans();
        int reciNum = Integer.valueOf(code.split("\\.")[3]);
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("IMP.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(CreateCodeUtils.formatReceINumber(reciNum));
        return reciCode.toString();
    }
    public String createBorrowingTransCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        int reciNum = stockBorrowingTransRepository.getQuantityStockBorrowingTrans();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EDC.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(CreateCodeUtils.formatReceINumber(reciNum));
        return reciCode.toString();
    }

    public String createRedInvoiceCodeAdjust(Long idShop) {

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

    public String createInternalCodeAdjust(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        int reciNum = stockAdjustmentTransRepository.getQuantityStockAdjustmentTrans();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EDC.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(CreateCodeUtils.formatReceINumber(reciNum));
        return reciCode.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public PoTransDTO getPoTransById(Long transId) {
        PoTrans poTrans = repository.getPoTransById(transId);
        if (!poTrans.getId().equals(transId)) {
            throw new ValidateException(ResponseMessage.VALIDATED_ERROR);
        }
        PoTransDTO poTransDTO = modelMapper.map(poTrans, PoTransDTO.class);
        poTransDTO.setWareHouseTypeName(wareHouseTypeRepository.findById(poTrans.getWareHouseTypeId()).get().getWareHouseTypeName());
        return poTransDTO;
    }
    public StockAdjustmentTransDTO getStockAdjustmentById(Long transId) {
        StockAdjustmentTrans sat = stockAdjustmentTransRepository.getStockAdjustmentTransById(transId);
        if(sat == null) throw new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);
        StockAdjustmentTransDTO stockAdjustmentTransDTO = modelMapper.map(sat, StockAdjustmentTransDTO.class);
        stockAdjustmentTransDTO.setWareHouseTypeName(wareHouseTypeRepository.findById(sat.getWareHouseTypeId()).get().getWareHouseTypeName());
        return stockAdjustmentTransDTO;
    }

    public StockBorrowingTransDTO getStockBorrowingById(Long transId) {
        StockBorrowingTrans sbt = stockBorrowingTransRepository.getStockBorrowingTransById(transId);
        if(sbt == null) throw new ValidateException(ResponseMessage.STOCK_BORROWING_TRANS_IS_NOT_EXISTED);
        StockBorrowingTransDTO stockBorrowingTransDTO = modelMapper.map(sbt, StockBorrowingTransDTO.class);
        stockBorrowingTransDTO.setWareHouseTypeName(wareHouseTypeRepository.findById(sbt.getWareHouseTypeId()).get().getWareHouseTypeName());
        return stockBorrowingTransDTO;
    }

}
