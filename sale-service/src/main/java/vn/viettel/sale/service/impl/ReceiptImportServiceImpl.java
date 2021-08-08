package vn.viettel.sale.service.impl;

import org.apache.logging.log4j.util.Strings;
import org.apache.regexp.RE;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.dto.sale.WareHouseTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ReceiptImportService;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.StockTotalService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.*;
import vn.viettel.sale.util.CreateCodeUtils;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
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
    StockTotalService stockTotalService;

    @Autowired
    SaleService saleService;

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
    public List<Long> createReceipt(ReceiptCreateRequest request, Long userId, Long shopId) {
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
    public List<Long> updateReceiptImport(ReceiptUpdateRequest request, Long id,String userName,Long shopId) {
        switch (request.getType()) {
            case 0:
                return updatePoTrans(request, id,userName,shopId);
            case 1:
                return updateAdjustmentTrans(request, id,userName,shopId);
            case 2:
                return updateBorrowingTrans(request, id,userName,shopId);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<List<String>> removeReceiptImport( Long id,Integer type,String userName,Long shopId) {
        switch (type) {
            case 0:
                return removePoTrans(id,userName,shopId);
            case 1:
                return removeStockAdjustmentTrans(id,userName,shopId);
            case 2:
                return removeStockBorrowingTrans(id,userName,shopId);
        }
        return null;
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
                dto.setShopName(shopDTO.getShopName()==null?"":shopDTO.getShopName());
                dto.setShopAddress(shopDTO.getAddress()==null?"":shopDTO.getAddress());
                dto.setShopContact("Tel: " + (shopDTO.getPhone()==null?"":shopDTO.getPhone()) + " Fax: " + (shopDTO.getFax()==null?"":shopDTO.getFax()));
                dto.setTotalPrice(pt.getPrice() * pt.getQuantity());
                totalPrice +=(pt.getPrice() * pt.getQuantity());
                totalQuantity += pt.getQuantity();
                totalPriceNotVat += (pt.getPriceNotVat() * pt.getQuantity());
                countProduct ++;
                rs.add(dto);
            }
            Collections.sort(rs, Comparator.comparing(PoDetailDTO::getProductCode));
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
                dto.setShopName(shopDTO.getShopName()==null?"":shopDTO.getShopName());
                dto.setShopAddress(shopDTO.getAddress()==null?"":shopDTO.getAddress());
                dto.setShopContact("Tel: " + (shopDTO.getPhone()==null?"":shopDTO.getPhone()) + " Fax: " + (shopDTO.getFax()==null?"":shopDTO.getFax()));
                dto.setTotalPrice((pt.getPrice()==null?0D:pt.getPrice()) * (pt.getQuantity()==null?0:pt.getQuantity()));
                totalPrice  +=((pt.getPrice()==null?0D:pt.getPrice()) * (pt.getQuantity()==null?0:pt.getQuantity()));
                totalQuantity += pt.getQuantity()==null?0:pt.getQuantity();
                countProduct++;
                rs.add(dto);
            }
            totalResponse = new TotalResponseV1(totalQuantity,countProduct, totalPrice,null);

            return (CoverResponse<List<PoDetailDTO>, TotalResponseV1>) new CoverResponse(rs, totalResponse);
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
        Collections.sort(rs,  Comparator.comparing(StockAdjustmentDetailDTO::getProductCode,Comparator.nullsLast(Comparator.naturalOrder())));
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
        List<Product> products = new ArrayList<>();
        List<StockBorrowing> stockBorrowings = new ArrayList<>();
        if(!borrowingDetails.isEmpty()) {
           products = productRepository.getProducts(borrowingDetails.stream().map(item -> item.getProductId()).distinct()
                    .collect(Collectors.toList()), null);
           stockBorrowings = stockBorrowingRepository.findAllById(borrowingDetails.stream().map(item -> item.getBorrowingId()).distinct()
                    .collect(Collectors.toList()));
        }

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
        Collections.sort(rs,  Comparator.comparing(StockBorrowingDetailDTO::getProductCode,Comparator.nullsLast(Comparator.naturalOrder())));
        TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
        CoverResponse<List<StockBorrowingDetailDTO>, TotalResponse> response =
                new CoverResponse(rs, totalResponse);

        return response;
    }

    public Object getPoTransDetail(Long id) {
        List<PoTransDetailDTO> rs = new ArrayList<>();
        List<PoTransDetailDTO> rs1 = new ArrayList<>();
        PoTrans poTrans = repository.getById(id);
        Optional<PoConfirm> poConfirm = null;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        if (poTrans.getFromTransId() == null) {
            if(poTrans.getPoId()!=null){
                 poConfirm = poConfirmRepository.findById(poTrans.getPoId());
                 if(!poConfirm.isPresent()) throw new ValidateException(ResponseMessage.PO_ORIGINAL_NOT_FOUND);
            }
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetail0(id);
            if(!poTransDetails.isEmpty()){
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
                    if(poConfirm != null && poConfirm.get() != null)
                        dto.setSoNo(poConfirm.get().getSaleOrderNumber());
                    dto.setExport((ptd.getReturnAmount()==null?0:ptd.getReturnAmount()));
                    rs.add(dto);
                }
            }
            List<PoTransDetail> poTransDetails1 = poTransDetailRepository.getPoTransDetail1(id);
            if(!poTransDetails1.isEmpty()){
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
                    dto.setExport((ptd.getReturnAmount()==null?0:ptd.getReturnAmount()));
                    rs1.add(dto);
                }
            }
            Collections.sort(rs,  Comparator.comparing(PoTransDetailDTO::getProductCode,Comparator.nullsLast(Comparator.naturalOrder())));
            Collections.sort(rs1,  Comparator.comparing(PoTransDetailDTO::getProductCode,Comparator.nullsLast(Comparator.naturalOrder())));
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
                for(int j= 0; j < poTransDetailImport.size(); j++){
                    if(poTransDetailImport.get(j).getProductId().equals(poTransDetails.get(i).getProductId())
                        &&poTransDetailImport.get(j).getPrice().equals(poTransDetails.get(i).getPrice()))
                    {
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
                        dto.setImportQuantity(poTransDetailImport.get(j).getQuantity());
                        rs.add(dto);
                    }
                }

            }
            Collections.sort(rs,  Comparator.comparing(PoTransDetailDTO::getProductCode,Comparator.nullsLast(Comparator.naturalOrder())));
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
        Collections.sort(rs,  Comparator.comparing(StockAdjustmentTransDetailDTO::getProductCode,Comparator.nullsLast(Comparator.naturalOrder())));
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
        Collections.sort(rs,  Comparator.comparing(StockBorrowingTransDetailDTO::getProductCode,Comparator.nullsLast(Comparator.naturalOrder())));
        CoverResponse<List<StockBorrowingTransDetailDTO>, List<StockBorrowingTransDetailDTO>> response =
                new CoverResponse(rs, new ArrayList<>());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage setNotImport(Long id,String userName, NotImportRequest request) {
        PoConfirm poConfirm = poConfirmRepository.getById(id);
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
    @Transactional(rollbackFor = Exception.class)
    public List<Long> createPoTrans(ReceiptCreateRequest request, Long userId, Long shopId) {
        List<String> lstRedInvoiceNo = repository.getRedInvoiceNo();
        if(lstRedInvoiceNo.contains(request.getRedInvoiceNo().trim())) throw new ValidateException(ResponseMessage.RED_INVOICE_NO_IS_EXIST);
        if(request.getRedInvoiceNo() != null && request.getRedInvoiceNo().length() >50) throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
        checkNoteLength(request.getNote());
        List<Long> syncIds = new ArrayList<Long>();
        LocalDateTime transDate = LocalDateTime.now();

        if (request.getPoId() == null) {
            List<String> lstInternalNumber = repository.getInternalNumber();
            List<String> lstPoCoNumber = repository.getPoCoNumber();
            if(request.getInternalNumber().length()>50) throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
            if(request.getPoCoNumber().length()>50) throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
            if(lstPoCoNumber.contains(request.getPoCoNumber())) throw new ValidateException(ResponseMessage.PO_NO_IS_EXIST);
            if(lstInternalNumber.contains(request.getInternalNumber())) throw  new ValidateException(ResponseMessage.INTERNAL_NUMBER_IS_EXIST);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoTrans poRecord = modelMapper.map(request, PoTrans.class);
            poRecord.setTransDate(transDate);
            poRecord.setTransCode(createPoTransCode(shopId));
            poRecord.setWareHouseTypeId(request.getWareHouseTypeId());
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
                    stockTotalService.updateWithLock(shopId, request.getWareHouseTypeId(), rcdr.getProductId(),rcdr.getQuantity());
                }
            } else throw  new ValidateException(ResponseMessage.PLEASE_IMPORT_PRODUCTS);
            poRecord.setTotalQuantity(total);
            poRecord.setTotalAmount(0D);
            poRecord.setNumSku(request.getLst().size());
            poRecord = repository.save(poRecord);
            syncIds.add(poRecord.getId());
        } else {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoTrans poRecord = modelMapper.map(request, PoTrans.class);
            PoConfirm poConfirm = poConfirmRepository.getById(request.getPoId());
            if(poConfirm == null || poConfirm.getStatus()==1) throw new ValidateException(ResponseMessage.RECEIPT_HAS_BEEN_IMPORTED);
            if(poConfirm.getStatus()==1) throw new ValidateException(ResponseMessage.RECEIPT_HAS_BEEN_IMPORTED);
            if(poConfirm.getWareHouseTypeId()==null) throw new ValidateException(ResponseMessage.DID_NOT_FIND_WARE_HOUSE_OF_RECEIPT);
            poRecord.setTransDate(transDate);
            poRecord.setTransCode(createPoTransCode(shopId));
            poRecord.setShopId(shopId);
            poRecord.setWareHouseTypeId(poConfirm.getWareHouseTypeId());
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
                if(pod.getPrice()==null) pod.setPrice(0D);
                countNumSKU.add(pod.getProductId());
                PoTransDetail poTransDetail = modelMapper.map(pod, PoTransDetail.class);
                poTransDetail.setId(null);
                poTransDetail.setTransId(poRecord.getId());
                poTransDetail.setAmount(pod.getQuantity() * pod.getPrice());
                poTransDetail.setReturnAmount(0);
                poTransDetail.setTransDate(transDate);
                poTransDetailRepository.save(poTransDetail);
                stockTotalService.updateWithLock(shopId, poConfirm.getWareHouseTypeId(), pod.getProductId(),pod.getQuantity());
            }
            poRecord.setNumSku(countNumSKU.size());
            poRecord.setNote(request.getNote());
            repository.save(poRecord);

            poConfirm.setStatus(1);
            poConfirm.setImportDate(poRecord.getTransDate());
            poConfirm.setImportCode(poRecord.getTransCode());
            poConfirm.setImportUser(poRecord.getCreatedBy());
            poConfirm.setUpdatedAt(poRecord.getUpdatedAt());
            poConfirm.setUpdatedBy(poRecord.getUpdatedBy());
            poConfirmRepository.save(poConfirm);
        }
        return syncIds;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Transactional(rollbackFor = Exception.class)
    public List<Long> createAdjustmentTrans(ReceiptCreateRequest request, Long userId, Long shopId) {

        UserDTO user = userClient.getUserByIdV1(userId);
        CustomerDTO cus = customerClient.getCusDefault(shopId);
        List<Long> syncIds = new ArrayList<Long>();
        if(cus.getId() == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        LocalDateTime transDate = LocalDateTime.now();

        if (request.getImportType() == 1) {
            ShopDTO shop = shopClient.getByIdV1(shopId).getData();
            if (shop == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentTrans stockAdjustmentRecord = modelMapper.map(request, StockAdjustmentTrans.class);
            StockAdjustment stockAdjustment = stockAdjustmentRepository.getById(request.getPoId());
            if(stockAdjustment == null) throw new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);
            if(stockAdjustment.getWareHouseTypeId()==null) throw new ValidateException(ResponseMessage.DID_NOT_FIND_WARE_HOUSE_OF_RECEIPT);
            if(stockAdjustment.getStatus() ==3) throw new ValidateException(ResponseMessage.RECEIPT_HAS_BEEN_IMPORTED);
            Response<ApParamDTO> reason = apparamClient.getReasonV1(stockAdjustment.getReasonId());
            if(reason.getData() == null || reason.getData().getId() == null) throw new ValidateException(ResponseMessage.REASON_NOT_FOUND);
            stockAdjustmentRecord.setTransDate(transDate);
            stockAdjustmentRecord.setWareHouseTypeId(stockAdjustment.getWareHouseTypeId());
            stockAdjustmentRecord.setTransCode(stockAdjustment.getAdjustmentCode());
            stockAdjustmentRecord.setAdjustmentDate(stockAdjustment.getAdjustmentDate());
            stockAdjustmentRecord.setShopId(shopId);
            stockAdjustmentRecord.setRedInvoiceNo(saleService.createOrderNumber(shop));
            stockAdjustmentRecord.setInternalNumber(createInternalCodeAdjust(shopId));
            stockAdjustmentRecord.setType(1);
            stockAdjustmentRecord.setStatus(1);
            stockAdjustmentRecord.setAdjustmentId(request.getPoId());
            stockAdjustmentTransRepository.save(stockAdjustmentRecord);
            List<StockAdjustmentDetail> stockAdjustmentDetails = stockAdjustmentDetailRepository.getStockAdjustmentDetailByAdjustmentId(stockAdjustment.getId());
            Integer totalQuantity = 0;
            Double totalAmount = 0D;
            SaleOrder order = new SaleOrder();
            order.setType(3);
            order.setOrderNumber(stockAdjustmentRecord.getRedInvoiceNo());
            order.setOrderDate(transDate);
            order.setShopId(shopId);
            order.setSalemanId(userId);
            order.setCustomerId(cus.getId());
            order.setWareHouseTypeId(stockAdjustment.getWareHouseTypeId());
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
            List<Price> prices = productPriceRepository.findProductPriceWithType(stockAdjustmentDetails.stream().map(item -> item.getProductId()).distinct()
                    .collect(Collectors.toList()), stockAdjustment.getWareHouseTypeId(), DateUtils.convertToDate(transDate));

            for (StockAdjustmentDetail sad : stockAdjustmentDetails) {
                if(sad.getPrice() == null) sad.setPrice(0D);
                if(sad.getQuantity()==null) sad.setQuantity(0);
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                StockAdjustmentTransDetail stockAdjustmentTransDetail = modelMapper.map(sad, StockAdjustmentTransDetail.class);
                stockAdjustmentTransDetail.setId(null);
                stockAdjustmentTransDetail.setTransId(stockAdjustmentRecord.getId());
                stockAdjustmentTransDetail.setTransDate(transDate);
                totalQuantity += sad.getQuantity();
                totalAmount += sad.getPrice() * sad.getQuantity();
                stockTotalService.updateWithLock(shopId, stockAdjustment.getWareHouseTypeId(),sad.getProductId(),sad.getQuantity());
                stockAdjustmentTransDetail.setStockQuantity(sad.getQuantity());
                stockAdjustmentTransDetail.setOriginalQuantity(sad.getQuantity());
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
                saleOrderDetail.setOrderDate(transDate);
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
            syncIds.add(stockAdjustment.getId());
            syncIds.add(stockAdjustmentRecord.getId());
            return syncIds;
        }
        return null;
    }
    @Transactional(rollbackFor = Exception.class)
    public List<Long> createBorrowingTrans(ReceiptCreateRequest request, Long userId, Long shopId) {
        LocalDateTime transDate = LocalDateTime.now();
        List<Long> syncIds = new ArrayList<Long>();
        if (request.getImportType() == 2) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingTrans stockBorrowingTrans = modelMapper.map(request, StockBorrowingTrans.class);
            StockBorrowing stockBorrowing = stockBorrowingRepository.findById(request.getPoId()).get();
            if(stockBorrowing.getStatusImport()==2) throw new ValidateException(ResponseMessage.RECEIPT_HAS_BEEN_IMPORTED);
            stockBorrowingTrans.setTransDate(transDate);
            stockBorrowingTrans.setTransCode(createBorrowingTransCode(shopId));
            stockBorrowingTrans.setWareHouseTypeId(request.getWareHouseTypeId());
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
                stockBorrowingTransDetail.setId(null);
                stockBorrowingTransDetail.setTransId(stockBorrowingTrans.getId());
                stockBorrowingTransDetail.setTransDate(transDate);
                stockBorrowingTransDetail.setShopId(stockBorrowing.getToShopId());
                totalQuantity += sbd.getQuantity();
                totalAmount += sbd.getPrice() * sbd.getQuantity();
                stockTotalService.updateWithLock(shopId, request.getWareHouseTypeId(), sbd.getProductId(),sbd.getQuantity());
                stockBorrowingTransDetailRepository.save(stockBorrowingTransDetail);
            }
            stockBorrowingTrans.setTotalAmount(totalAmount);
            stockBorrowingTrans.setTotalQuantity(totalQuantity);
            stockBorrowingTrans.setNote(request.getNote()==null ? stockBorrowing.getNote() : request.getNote());
            stockBorrowing.setStatusImport(2);
            stockBorrowing = stockBorrowingRepository.save(stockBorrowing);
            syncIds.add(stockBorrowing.getId());
            stockBorrowingTrans = stockBorrowingTransRepository.save(stockBorrowingTrans);
            syncIds.add(stockBorrowingTrans.getId());
            return syncIds;
        }
        return null;
    }
    private void checkNoteLength(String note){
        if(note != null && note.length()>250)
            throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Long> updatePoTrans(ReceiptUpdateRequest request, Long id,String userName,Long shopId) {
        checkNoteLength(request.getNote());
        PoTrans poTrans = repository.findById(id).get();
        if(poTrans==null) throw new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED);
        List<String> lstRedInvoiceNo = repository.getRedInvoiceNo();
        List<String> lstPoNo = repository.getRedInvoiceNo();
        List<String> lstInternalNumber = repository.getRedInvoiceNo();
        lstRedInvoiceNo.remove(poTrans.getRedInvoiceNo());
        lstPoNo.remove(poTrans.getPocoNumber());
        lstInternalNumber.remove(poTrans.getInternalNumber());
        if(lstRedInvoiceNo.contains(request.getRedInvoiceNo().trim())) throw new ValidateException(ResponseMessage.RED_INVOICE_NO_IS_EXIST);
        poTrans.setNote(request.getNote());
        poTrans.setRedInvoiceNo(request.getRedInvoiceNo().trim());
        if (DateUtils.formatDate2StringDate(poTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))){
            List<StockTotal> stockTotals = new ArrayList<>();
            List<StockTotal> newStockTotals = new ArrayList<>();
            List<PoTransDetail> deletedPoTransDetails = new ArrayList<>();
            List<PoTransDetail> savedPoTransDetails = new ArrayList<>();
            HashMap<Long,Integer> idAndValues = new HashMap<>();
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
                    stockTotals = stockTotalRepository.getStockTotal(shopId, poTrans.getWareHouseTypeId(), pIds);

                    /** delete **/
                    for (PoTransDetail podId : poTransDetails) {
                        if (!listUpdate.contains(podId.getId())) {
                            if((podId.getReturnAmount()==null?0:podId.getReturnAmount())>0) throw new ValidateException(ResponseMessage.RECEIPT_IMPORT_HAS_BEEN_RETURNED);
                            StockTotal stockTotal = null;
                            if(stockTotals != null){
                                for(StockTotal st : stockTotals){
                                    if(st.getProductId().equals(podId.getProductId())){
                                        stockTotal = st;
                                        int value = (-1) * podId.getQuantity();
                                        if(idAndValues.containsKey(st.getId())){
                                            value += idAndValues.get(st.getId());
                                        }
                                        idAndValues.put(st.getId(), value);
                                        if(st.getQuantity() + value < 0) {
                                            stockTotalService.showMessage(podId.getProductId(), false);
                                        }
                                        break;
                                    }
                                }
                            }
                            if (stockTotal == null) stockTotalService.showMessage(podId.getProductId(), true);
                            deletedPoTransDetails.add(podId);
                        }
                    }
                    Integer total = 0;
                    for (int i = 0; i < request.getLstUpdate().size(); i++) {
                        ReceiptCreateDetailRequest rcdr = request.getLstUpdate().get(i);
                        /** update **/
                        if (rcdr.getId() != null && rcdr.getId()!=-1) {
                            PoTransDetail po = null;
                            for (PoTransDetail podId : poTransDetails) {
                                if(podId.getId().equals(rcdr.getId())){
                                    po = podId;
                                    break;
                                }
                            }
                            if(po == null) continue;
                            StockTotal stockTotal = null;
                            if(stockTotals != null){
                                for(StockTotal st : stockTotals){
                                    if(st.getProductId().equals(rcdr.getProductId())){
                                        stockTotal = st;
                                        int value = (rcdr.getQuantity() - po.getQuantity());
                                        if(idAndValues.containsKey(st.getId())){
                                            value += idAndValues.get(st.getId());
                                        }
                                        idAndValues.put(st.getId(), value);
                                        if(st.getQuantity() + value < 0) {
                                            stockTotalService.showMessage(rcdr.getProductId(), false);
                                        }
                                        break;
                                    }
                                }
                            }
                            if (stockTotal == null) stockTotalService.showMessage(rcdr.getProductId(), true);
                            po.setQuantity(rcdr.getQuantity());
                            po.setTransDate(poTrans.getTransDate());
                            total += po.getQuantity();
                            savedPoTransDetails.add(po);
                        }
                        /** create new **/
                        else if (rcdr.getId()== -1) {
                            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                            PoTransDetail poTransDetail = modelMapper.map(rcdr, PoTransDetail.class);
                            poTransDetail.setTransId(poTrans.getId());
                            poTransDetail.setPrice(0D);
                            poTransDetail.setPriceNotVat(0D);
                            poTransDetail.setReturnAmount(0);
                            poTransDetail.setShopId(poTrans.getShopId());
                            poTransDetail.setTransDate(poTrans.getTransDate());
                            StockTotal stockTotal = null;
                            if(stockTotals != null){
                                for(StockTotal st : stockTotals){
                                    if(st.getProductId().equals(rcdr.getProductId())){
                                        if (st.getQuantity() == null) st.setQuantity(0);
                                        stockTotal = st;
                                        int value = rcdr.getQuantity();
                                        if(idAndValues.containsKey(st.getId())){
                                            value += idAndValues.get(st.getId());
                                        }
                                        idAndValues.put(st.getId(), value);
                                        break;
                                    }
                                }
                            }
                            if (stockTotal == null){
                                StockTotal newStockTotal = stockTotalService.createStockTotal(shopId, poTrans.getWareHouseTypeId(), rcdr.getProductId(), rcdr.getQuantity(), false);
                                newStockTotals.add(newStockTotal);
                            }
                            total += poTransDetail.getQuantity();
                            savedPoTransDetails.add(poTransDetail);
                        }
                        poTrans.setTotalQuantity(total);
                    }
                }else throw new ValidateException(ResponseMessage.PLEASE_IMPORT_PRODUCTS);
            }
            poTrans.setNumSku(request.getLstUpdate().size());
            poTrans = repository.save(poTrans);
            for(PoTransDetail poTransDetail : deletedPoTransDetails){
                poTransDetailRepository.delete(poTransDetail);
            }
            for(PoTransDetail poTransDetail : savedPoTransDetails){
                poTransDetailRepository.save(poTransDetail);
            }
            if(stockTotals != null){
                stockTotalService.updateWithLock(idAndValues);
            }
            for(StockTotal st : newStockTotals){
                if(st != null) stockTotalRepository.save(st);
            }
        }
        return Arrays.asList(poTrans.getId());
    }
    /**Up date stock adjustment trans**/
    public List<Long> updateAdjustmentTrans(ReceiptUpdateRequest request, Long id,String userName,Long shopId) {
        StockAdjustmentTrans adjustmentTrans = stockAdjustmentTransRepository.findById(id).get();
        if(adjustmentTrans == null) throw new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);
        checkNoteLength(request.getNote());
        if (DateUtils.formatDate2StringDate(adjustmentTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            adjustmentTrans.setNote(request.getNote());
            adjustmentTrans.setUpdatedBy(userName);
            adjustmentTrans.setInternalNumber(request.getInternalNumber());
            stockAdjustmentTransRepository.save(adjustmentTrans);
            return Arrays.asList(adjustmentTrans.getId());
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);

    }
    /** Up date stock borrowing trans **/
    public List<Long> updateBorrowingTrans(ReceiptUpdateRequest request, Long id,String userName,Long shopId) {
        StockBorrowingTrans borrowingTrans = stockBorrowingTransRepository.findById(id).get();
        if(borrowingTrans == null) throw new ValidateException(ResponseMessage.STOCK_BORROWING_TRANS_IS_NOT_EXISTED);
        checkNoteLength(request.getNote());
        if (DateUtils.formatDate2StringDate(borrowingTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            borrowingTrans.setNote(request.getNote());
            borrowingTrans.setUpdatedBy(userName);
            borrowingTrans.setInternalNumber(request.getInternalNumber());
            borrowingTrans = stockBorrowingTransRepository.save(borrowingTrans);
            return Arrays.asList(borrowingTrans.getId());
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);

    }
    /** Remove Po Trans **/
    @Transactional(rollbackFor = Exception.class)
    public List<List<String>> removePoTrans(Long id,String userName,Long shopId) {
        PoTrans poTrans = repository.findById(id).get();
        List<List<String>> syncIds = new ArrayList<List<String>>();
        if(poTrans== null) throw new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED);
        if(poTrans.getStatus()==-1) throw new ValidateException(ResponseMessage.RECEIPT_HAS_BEEN_DELETED);
        if (DateUtils.formatDate2StringDate(poTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
            List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, poTrans.getWareHouseTypeId(),
                    poTransDetails.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()));
            HashMap<Long,Integer> idAndValues = new HashMap<>();

            for (PoTransDetail ptd : poTransDetails) {
                if(ptd.getReturnAmount()>0) throw new ValidateException(ResponseMessage.RECEIPT_IMPORT_HAS_BEEN_RETURNED);
                Integer qty = null;
                if(stockTotals != null){
                    for(StockTotal st: stockTotals){
                        if(st.getProductId().equals(ptd.getProductId())) {
                            int value = (-1) * ptd.getQuantity();
                            if(idAndValues.containsKey(st.getId())){
                                value += idAndValues.get(st.getId());
                            }
                            qty = st.getQuantity() + value;
                            idAndValues.put(st.getId(), value);
                            break;
                        }
                    }
                }
                if(qty == null) stockTotalService.showMessage(ptd.getProductId(),true);
                if(qty < 0) {
                    Optional<Product> product = productRepository.findById(ptd.getProductId());
                    if(!product.isPresent()) throw  new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS);
                    throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SS,product.get().getProductName(),poTrans.getTransCode());
                }
            }
            String poConfirmTemp = Strings.EMPTY;
            if (poTrans.getPoId() != null) {
                PoConfirm poConfirm = poConfirmRepository.findById(poTrans.getPoId()).get();
                poConfirm.setStatus(0);
                poConfirm.setImportDate(null);
                poConfirm.setImportCode(null);
                poConfirm.setImportUser(null);
                poConfirmRepository.save(poConfirm);
            }
            poTrans.setStatus(-1);
            poTrans = repository.save(poTrans);
            if(stockTotals != null){
                stockTotalService.updateWithLock(idAndValues);
            }
            syncIds.add(Arrays.asList(poTrans.getId().toString()));
            syncIds.add(Arrays.asList(poConfirmTemp));
            return syncIds;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<List<String>> removeStockAdjustmentTrans(Long id,String userName,Long shopId) {
        StockAdjustmentTrans stockAdjustmentTrans = stockAdjustmentTransRepository.getById(id);
        List<List<String>> syncIds = new ArrayList<List<String>>();
        if(stockAdjustmentTrans==null) throw new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);
        if(stockAdjustmentTrans.getStatus()==-1) throw new ValidateException(ResponseMessage.RECEIPT_HAS_BEEN_DELETED);

        if (DateUtils.formatDate2StringDate(stockAdjustmentTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            List<StockAdjustmentTransDetail> stockAdjustmentTransDetails = stockAdjustmentTransDetailRepository.getStockAdjustmentTransDetailsByTransId(stockAdjustmentTrans.getId());
            List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, stockAdjustmentTrans.getWareHouseTypeId(),
                    stockAdjustmentTransDetails.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()));
            HashMap<Long,Integer> idAndValues = new HashMap<>();
            List<String> orderNumbers = new ArrayList<String>();
            for (StockAdjustmentTransDetail satd : stockAdjustmentTransDetails) {
                Integer qty = null;
                if(stockTotals != null){
                    for(StockTotal st: stockTotals){
                        if(st.getProductId().equals(satd.getProductId())) {
                            int value = (-1) * satd.getQuantity();
                            if(idAndValues.containsKey(st.getId())){
                                value += idAndValues.get(st.getId());
                            }
                            idAndValues.put(st.getId(), value);
                            qty = st.getQuantity() + value;
                            break;
                        }
                    }
                }
                if(qty == null) stockTotalService.showMessage(satd.getProductId(),true);
                if(qty < 0) {
                    Optional<Product> product = productRepository.findById(satd.getProductId());
                    if(!product.isPresent()) throw  new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS);
                    throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SS,product.get().getProductName(),stockAdjustmentTrans.getTransCode());
                }
            }
            if(stockAdjustmentTrans.getRedInvoiceNo() != null && !stockAdjustmentTrans.getRedInvoiceNo().isEmpty()) {
                List<SaleOrder> orders = saleOrderRepository.findSaleOrderByOrderCode(Arrays.asList(stockAdjustmentTrans.getRedInvoiceNo()));
                if(orders != null && !orders.isEmpty()){
                    for (SaleOrder order : orders){
                    	orderNumbers.add(order.getOrderNumber());
                        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findSaleOrderDetail(order.getId(), null);
                        saleOrderDetailRepository.deleteAll(saleOrderDetails);
                    }
                    saleOrderRepository.deleteAll(orders);
                }
            }
            StockAdjustment stockAdjustment = stockAdjustmentRepository.getById(stockAdjustmentTrans.getAdjustmentId());
            if(stockAdjustment != null) {
                stockAdjustment.setStatus(1);
                stockAdjustmentRepository.save(stockAdjustment);
            }
            stockAdjustmentTrans.setStatus(-1);
            stockAdjustmentTransRepository.save(stockAdjustmentTrans);
            syncIds.add(Arrays.asList(stockAdjustment.getId().toString()));
            syncIds.add(Arrays.asList(stockAdjustmentTrans.getId().toString()));
            syncIds.add(orderNumbers);
            if(stockTotals != null){
                stockTotalService.updateWithLock(idAndValues);
            }
            return syncIds;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<List<String>> removeStockBorrowingTrans(Long id,String userName,Long shopId) {
        StockBorrowingTrans stockBorrowingTrans = stockBorrowingTransRepository.getById(id);
        List<List<String>> syncIds = new ArrayList<List<String>>();
        if(stockBorrowingTrans == null || stockBorrowingTrans.getStatus()==-1) throw new ValidateException(ResponseMessage.RECEIPT_HAS_BEEN_DELETED);
        if (DateUtils.formatDate2StringDate(stockBorrowingTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            List<StockBorrowingTransDetail> stockBorrowingTransDetails = stockBorrowingTransDetailRepository.getStockBorrowingTransDetailByTransId(stockBorrowingTrans.getId());
            List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, stockBorrowingTrans.getWareHouseTypeId(),
                    stockBorrowingTransDetails.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()));
            HashMap<Long,Integer> idAndValues = new HashMap<>();
            for (StockBorrowingTransDetail sbtd : stockBorrowingTransDetails) {
                Integer qty = null;
                if(stockTotals != null){
                    for(StockTotal st: stockTotals){
                        if(st.getProductId().equals(sbtd.getProductId())) {
                            int value = (-1) * sbtd.getQuantity();
                            if(idAndValues.containsKey(st.getId())){
                                value += idAndValues.get(st.getId());
                            }
                            idAndValues.put(st.getId(), value);
                            qty = st.getQuantity() + value;
                            break;
                        }
                    }
                }
                if(qty == null) stockTotalService.showMessage(sbtd.getProductId(),true);
                if(qty < 0){
                    Optional<Product> product = productRepository.findById(sbtd.getProductId());
                    if(!product.isPresent()) throw  new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS);
                    throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SS,product.get().getProductName(),stockBorrowingTrans.getTransCode());
                }
            }
            StockBorrowing stockBorrowing = stockBorrowingRepository.getById(stockBorrowingTrans.getStockBorrowingId());
            if(stockBorrowing != null){
                stockBorrowing.setStatusImport(1);
                stockBorrowingRepository.save(stockBorrowing);
            }

            stockBorrowingTrans.setStatus(-1);
            stockBorrowingTrans.setUpdatedBy(userName);
            stockBorrowingTrans = stockBorrowingTransRepository.save(stockBorrowingTrans);
            syncIds.add(Arrays.asList(stockBorrowingTrans.getId().toString()));
            stockBorrowing = stockBorrowingRepository.save(stockBorrowing);
            syncIds.add(Arrays.asList(stockBorrowing.getId().toString()));
            stockBorrowingTransRepository.save(stockBorrowingTrans);

            if(stockTotals != null){
                stockTotalService.updateWithLock(idAndValues);
            }
            return syncIds;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String createPoTransCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());

        StringBuilder reciCode = new StringBuilder();
        reciCode.append("IMPP.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        Page<PoTrans> pos = repository.getLastTransCode(1, reciCode.toString(),
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
    private String createBorrowingTransCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());

        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EDCB.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        Page<StockBorrowingTrans> borrTrans = stockBorrowingTransRepository.getLastTransCode(1,
                reciCode.toString(), LocalDateTime.now().with(firstDayOfYear()), PageRequest.of(0,1));
        int STT = 0;
        if(!borrTrans.getContent().isEmpty()) {
            String str = borrTrans.getContent().get(0).getTransCode();
            String numberString = str.substring(str.length() - 5);
            STT = Integer.valueOf(numberString);
        }
        reciCode.append(CreateCodeUtils.formatReceINumber(STT));
        return reciCode.toString();
    }

    private String createInternalCodeAdjust(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EDCT.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        Pageable pageable = PageRequest.of(0,2);
        Page<StockAdjustmentTrans> stockAdjustmentTrans = stockAdjustmentTransRepository.getLastInternalCode(1,
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private PoTransDTO getPoTransById(Long transId) {
        PoTrans poTrans = repository.getById(transId);
        if (poTrans == null) throw new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED);
        PoTransDTO poTransDTO = modelMapper.map(poTrans, PoTransDTO.class);
        if(poTrans.getWareHouseTypeId()==null)
            throw new ValidateException(ResponseMessage.DID_NOT_FIND_WARE_HOUSE_OF_RECEIPT);
        Optional<WareHouseType> wareHouseType = wareHouseTypeRepository.findById(poTrans.getWareHouseTypeId());
        poTransDTO.setWareHouseTypeName(wareHouseType.get().getWareHouseTypeName());
        return poTransDTO;
    }

    private StockAdjustmentTransDTO getStockAdjustmentById(Long transId) {
        StockAdjustmentTrans sat = stockAdjustmentTransRepository.getById(transId);
        if(sat == null) throw new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);
        StockAdjustmentTransDTO stockAdjustmentTransDTO = modelMapper.map(sat, StockAdjustmentTransDTO.class);
        stockAdjustmentTransDTO.setWareHouseTypeName(wareHouseTypeRepository.findById(sat.getWareHouseTypeId()).get().getWareHouseTypeName());
        return stockAdjustmentTransDTO;
    }

    private StockBorrowingTransDTO getStockBorrowingById(Long transId) {
        StockBorrowingTrans sbt = stockBorrowingTransRepository.getById(transId);
        if(sbt == null) throw new ValidateException(ResponseMessage.STOCK_BORROWING_TRANS_IS_NOT_EXISTED);
        StockBorrowingTransDTO stockBorrowingTransDTO = modelMapper.map(sbt, StockBorrowingTransDTO.class);
        stockBorrowingTransDTO.setWareHouseTypeName(wareHouseTypeRepository.findById(sbt.getWareHouseTypeId()).get().getWareHouseTypeName());
        return stockBorrowingTransDTO;
    }

}
