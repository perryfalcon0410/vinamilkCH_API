package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.*;
import vn.viettel.sale.specification.ReceiptSpecification;
import vn.viettel.sale.util.CreateCodeUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Override
    public CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> find(String redInvoiceNo, LocalDateTime fromDate, LocalDateTime toDate, Integer type, Long shopId, Pageable pageable) {
        int totalQuantity = 0;
        Double totalPrice = 0D;

        if (type == null) {
            List<PoTrans> list1 = repository.findAll(Specification.where(ReceiptSpecification.hasStatus()).and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo).and(ReceiptSpecification.hasFromDateToDate(fromDate, toDate)).and(ReceiptSpecification.hasTypeImport())).and(ReceiptSpecification.hasShopId(shopId)));
            List<StockAdjustmentTrans> list2 = stockAdjustmentTransRepository.findAll(Specification.where(ReceiptSpecification.hasStatusA()).and(ReceiptSpecification.hasRedInvoiceNoA(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateA(fromDate, toDate)).and(ReceiptSpecification.hasTypeImportA()).and(ReceiptSpecification.hasShopIdA(shopId)));
            List<StockBorrowingTrans> list3 = stockBorrowingTransRepository.findAll(Specification.where(ReceiptSpecification.hasStatusB().and(ReceiptSpecification.hasRedInvoiceNoB(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateB(fromDate, toDate)).and(ReceiptSpecification.hasTypeImportB())).and(ReceiptSpecification.hasToShopId(shopId)));
            List<ReceiptImportListDTO> listAddDTO1 = new ArrayList<>();
            for (PoTrans poTrans : list1) {
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                ReceiptImportListDTO poRecord = modelMapper.map(poTrans, ReceiptImportListDTO.class);
                poRecord.setReceiptType(0);
                listAddDTO1.add(poRecord);
            }
            List<ReceiptImportListDTO> listAddDTO2 = new ArrayList<>();
            for (StockAdjustmentTrans stockAdjustmentTrans : list2) {
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                ReceiptImportListDTO poARecord = modelMapper.map(stockAdjustmentTrans, ReceiptImportListDTO.class);
                poARecord.setReceiptType(1);
                listAddDTO2.add(poARecord);
            }
            List<ReceiptImportListDTO> listAddDTO3 = new ArrayList<>();
            for (StockBorrowingTrans stockBorrowingTrans : list3) {
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
                if (result.get(i).getTotalQuantity() == null)
                    throw new ValidateException(ResponseMessage.QUANTITY_CAN_NOT_BE_NULL);
                if (result.get(i).getTotalAmount() == null)
                    throw new ValidateException(ResponseMessage.AMOUNT_CAN_NOT_BE_NULL);
                totalQuantity += result.get(i).getTotalQuantity();
                totalPrice += result.get(i).getTotalAmount();
            }
            TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), result.size());
            subList = result.subList(start, end);
            //////////////////////////////////
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(subList,pageable,result.size());
            CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> response =
                    new CoverResponse(pageResponse, totalResponse);
            return response;
        } else if (type == 0) {
            List<PoTrans> list1 = repository.findAll(Specification.where(ReceiptSpecification.hasStatus()).
                    and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo).and(ReceiptSpecification.hasFromDateToDate(fromDate, toDate)).
                            and(ReceiptSpecification.hasTypeImport())).and(ReceiptSpecification.hasShopId(shopId)));
            List<ReceiptImportListDTO> listAddDTO1 = new ArrayList<>();
            for (PoTrans poTrans : list1) {
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                ReceiptImportListDTO poRecord = modelMapper.map(poTrans, ReceiptImportListDTO.class);
                poRecord.setReceiptType(0);
                listAddDTO1.add(poRecord);
            }
            List<ReceiptImportListDTO> subList;
            for (int i = 0; i < listAddDTO1.size(); i++) {
                if (listAddDTO1.get(i).getTotalQuantity() == null)
                    throw new ValidateException(ResponseMessage.QUANTITY_CAN_NOT_BE_NULL);
                if (listAddDTO1.get(i).getTotalAmount() == null)
                    throw new ValidateException(ResponseMessage.AMOUNT_CAN_NOT_BE_NULL);
                totalQuantity += listAddDTO1.get(i).getTotalQuantity();
                totalPrice += listAddDTO1.get(i).getTotalAmount();
            }
            TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), listAddDTO1.size());
            subList = listAddDTO1.subList(start, end);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(subList,pageable,listAddDTO1.size());
            CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> response =
                    new CoverResponse(pageResponse, totalResponse);
            return response;
        } else if (type == 1) {
            List<StockAdjustmentTrans> list2 = stockAdjustmentTransRepository.findAll(Specification.where(ReceiptSpecification.hasStatusA()).and(ReceiptSpecification.hasRedInvoiceNoA(redInvoiceNo)).
                    and(ReceiptSpecification.hasFromDateToDateA(fromDate, toDate)).and(ReceiptSpecification.hasTypeImportA()).and(ReceiptSpecification.hasShopIdA(shopId)));
            List<ReceiptImportListDTO> listAddDTO2 = new ArrayList<>();
            for (StockAdjustmentTrans stockAdjustmentTrans : list2) {
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                ReceiptImportListDTO poARecord = modelMapper.map(stockAdjustmentTrans, ReceiptImportListDTO.class);
                poARecord.setReceiptType(1);
                listAddDTO2.add(poARecord);
            }
            List<ReceiptImportListDTO> subList;
            for (int i = 0; i < listAddDTO2.size(); i++) {
                if (listAddDTO2.get(i).getTotalQuantity() == null)
                    throw new ValidateException(ResponseMessage.QUANTITY_CAN_NOT_BE_NULL);
                if (listAddDTO2.get(i).getTotalAmount() == null)
                    throw new ValidateException(ResponseMessage.AMOUNT_CAN_NOT_BE_NULL);
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
        } else if (type == 2) {
            List<StockBorrowingTrans> list3 = stockBorrowingTransRepository.findAll(Specification.where(ReceiptSpecification.hasStatusB().and(ReceiptSpecification.hasRedInvoiceNoB(redInvoiceNo)).
                    and(ReceiptSpecification.hasFromDateToDateB(fromDate, toDate)).and(ReceiptSpecification.hasTypeImportB())).and(ReceiptSpecification.hasToShopId(shopId)));

            List<ReceiptImportListDTO> listAddDTO3 = new ArrayList<>();
            for (StockBorrowingTrans stockBorrowingTrans : list3) {
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                ReceiptImportListDTO poBRecord = modelMapper.map(stockBorrowingTrans, ReceiptImportListDTO.class);
                poBRecord.setReceiptType(2);
                listAddDTO3.add(poBRecord);
            }
            List<ReceiptImportListDTO> subList;
            for (int i = 0; i < listAddDTO3.size(); i++) {
                if (listAddDTO3.get(i).getTotalQuantity() == null)
                    throw new ValidateException(ResponseMessage.QUANTITY_CAN_NOT_BE_NULL);
                if (listAddDTO3.get(i).getTotalAmount() == null)
                    throw new ValidateException(ResponseMessage.AMOUNT_CAN_NOT_BE_NULL);
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
    public ResponseMessage updateReceiptImport(ReceiptUpdateRequest request, Long id,String userName) {
        switch (request.getType()) {
            case 0:
                return updatePoTrans(request, id,userName);
            case 1:
                return updateAdjustmentTrans(request, id,userName);
            case 2:
                return  updateBorrowingTrans(request, id,userName);
        }
        return null;
    }

    @Override
    public ResponseMessage removeReceiptImport( Long id,Integer type,String userName) {
        switch (type) {
            case 0:
                removePoTrans(id,userName);
                break;
            case 1:
                removeStockAdjustmentTrans(id,userName);
            case 2:
                removeStockBorrowingTrans(id,userName);
                break;
        }
        return ResponseMessage.DELETE_SUCCESSFUL;
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
    public List<PoConfirmDTO> getListPoConfirm() {
        List<PoConfirm> poConfirms = poConfirmRepository.getPoConfirm();
        List<PoConfirmDTO> rs = new ArrayList<>();
        for (PoConfirm pc : poConfirms) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoConfirmDTO dto = modelMapper.map(pc, PoConfirmDTO.class);
            rs.add(dto);
        }
        return rs;
    }
    @Override
    public List<StockAdjustmentDTO> getListStockAdjustment(Pageable pageable) {
        List<StockAdjustment> stockAdjustments = stockAdjustmentRepository.getStockAdjustment();
        List<StockAdjustmentDTO> rs = new ArrayList<>();
        for (StockAdjustment sa : stockAdjustments) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentDTO dto = modelMapper.map(sa, StockAdjustmentDTO.class);
            rs.add(dto);
        }
        return rs;
    }
    @Override
    public List<StockBorrowingDTO> getListStockBorrowing(Long toShopId,Pageable pageable) {
        List<StockBorrowing> stockBorrowings = stockBorrowingRepository.getStockBorrowing(toShopId);
        List<StockBorrowingDTO> rs = new ArrayList<>();
        for (StockBorrowing sb : stockBorrowings) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingDTO dto = modelMapper.map(sb, StockBorrowingDTO.class);
            rs.add(dto);
        }
        return rs;
    }

    @Override
    public CoverResponse<List<PoDetailDTO>, TotalResponse> getPoDetailByPoId(Long id, Long shopId) {
        int totalQuantity = 0;
        Double totalPrice = 0D;
        List<PoDetail> poDetails = poDetailRepository.getPoDetailByPoIdAndPriceIsGreaterThan(id);
        List<PoDetailDTO> rs = new ArrayList<>();
        for (PoDetail pt : poDetails) {
            Product product = productRepository.findById(pt.getProductId()).get();
            ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
            PoConfirm poConfirm = poConfirmRepository.findById(pt.getPoId()).get();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoDetailDTO dto = modelMapper.map(pt, PoDetailDTO.class);
            dto.setProductCode(product.getProductCode());
            dto.setProductName(product.getProductName());
            dto.setShopName(shopDTO.getShopName());
            dto.setShopAddress(shopDTO.getAddress());
            dto.setShopContact("Tel: " + shopDTO.getPhone() + " Fax: " + shopDTO.getFax());
            dto.setSoNo(poConfirm.getSaleOrderNumber());
            dto.setUnit(product.getUom1());
            dto.setTotalPrice(pt.getPrice() * pt.getQuantity());
            totalPrice +=(pt.getPrice() * pt.getQuantity());
            totalQuantity += pt.getQuantity();
            rs.add(dto);
        }
        TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
        CoverResponse<List<PoDetailDTO>, TotalResponse> response =
                new CoverResponse(rs, totalResponse);
        return  response;
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
    public CoverResponse<List<PoDetailDTO>, TotalResponse> getPoDetailByPoIdAndPriceIsNull(Long id, Long shopId) {
        int totalQuantity = 0;
        Double totalPrice = 0D;
        List<PoDetail> poDetails = poDetailRepository.getPoDetailByPoIdAndPriceIsLessThan(id);
        List<PoDetailDTO> rs = new ArrayList<>();
        for (PoDetail pt : poDetails) {
            Product product = productRepository.findById(pt.getProductId()).get();
            ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
            PoConfirm poConfirm = poConfirmRepository.findById(pt.getPoId()).get();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoDetailDTO dto = modelMapper.map(pt, PoDetailDTO.class);
            dto.setProductCode(product.getProductCode());
            dto.setProductName(product.getProductName());
            dto.setShopName(shopDTO.getShopName());
            dto.setShopAddress(shopDTO.getAddress());
            dto.setShopContact("Tel: " + shopDTO.getPhone() + " Fax: " + shopDTO.getFax());
            dto.setSoNo(poConfirm.getSaleOrderNumber());
            dto.setUnit(product.getUom1());
            dto.setTotalPrice(pt.getPrice() * pt.getQuantity());
            totalPrice  +=(pt.getPrice() * pt.getQuantity());
            totalQuantity += pt.getQuantity();
            rs.add(dto);
        }
        TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
        CoverResponse<List<PoDetailDTO>, TotalResponse> response =
                new CoverResponse(rs, totalResponse);

       return  response;
    }
    @Override
    public CoverResponse<List<StockAdjustmentDetailDTO>, TotalResponse> getStockAdjustmentDetail(Long id) {
        int totalQuantity = 0;
        Double totalPrice = 0D;
        List<StockAdjustmentDetail> adjustmentDetails = stockAdjustmentDetailRepository.getStockAdjustmentDetailByAdjustmentId(id);
        List<StockAdjustmentDetailDTO> rs = new ArrayList<>();
        for (StockAdjustmentDetail sad : adjustmentDetails) {
            Product product = productRepository.findById(sad.getProductId()).get();
            StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(sad.getAdjustmentId()).get();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentDetailDTO dto = modelMapper.map(sad, StockAdjustmentDetailDTO.class);
            dto.setProductCode(product.getProductCode());
            dto.setProductName(product.getProductName());
            dto.setLicenseNumber(stockAdjustment.getAdjustmentCode());
            dto.setUnit(product.getUom1());
            dto.setTotalPrice(sad.getPrice() * sad.getQuantity());
            totalPrice +=(sad.getPrice() * sad.getQuantity());
            totalQuantity += sad.getQuantity();
            rs.add(dto);
        }
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
        for (StockBorrowingDetail sbd : borrowingDetails) {
            Product product = productRepository.findById(sbd.getProductId()).get();
            StockBorrowing  stockBorrowing = stockBorrowingRepository.findById(sbd.getBorrowingId()).get();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingDetailDTO dto = modelMapper.map(sbd, StockBorrowingDetailDTO.class);
            dto.setProductCode(product.getProductCode());
            dto.setProductName(product.getProductName());
            dto.setLicenseNumber(stockBorrowing.getPoBorrowCode());
            dto.setUnit(product.getUom1());
            dto.setTotalPrice(sbd.getPrice() * sbd.getQuantity());
            totalPrice +=(sbd.getPrice() * sbd.getQuantity());
            totalQuantity += sbd.getQuantity();
            rs.add(dto);
        }
        TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
        CoverResponse<List<StockBorrowingDetailDTO>, TotalResponse> response =
                new CoverResponse(rs, totalResponse);

        return response;
    }
    public Object getPoTransDetail(Long id) {
        List<PoTransDetailDTO> rs = new ArrayList<>();
        List<PoTransDetailDTO> rs1 = new ArrayList<>();
        PoTrans poTrans = repository.findById(id).get();
        PoConfirm poConfirm;

        if (poTrans.getFromTransId() == null) {
            if(poTrans.getPoId()!=null){
                poConfirm= poConfirmRepository.findById(poTrans.getPoId()).get();
            }else{
                poConfirm =null;
            }
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetail0(id);
            for (int i = 0; i < poTransDetails.size(); i++) {
                PoTransDetail ptd = poTransDetails.get(i);
                Product product = productRepository.findById(ptd.getProductId()).get();
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTransDetailDTO dto = modelMapper.map(ptd, PoTransDetailDTO.class);
                dto.setProductCode(product.getProductCode());
                dto.setProductName(product.getProductName());
                dto.setUnit(product.getUom1());
                dto.setTotalPrice(ptd.getPrice() * ptd.getQuantity());
                dto.setSoNo(poConfirm.getSaleOrderNumber());
                dto.setExport(0);
                rs.add(dto);
            }
            List<PoTransDetail> poTransDetails1 = poTransDetailRepository.getPoTransDetail1(id);
            for (int i = 0; i < poTransDetails1.size(); i++) {
                PoTransDetail ptd = poTransDetails1.get(i);
                Product product = productRepository.findById(ptd.getProductId()).get();
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTransDetailDTO dto = modelMapper.map(ptd, PoTransDetailDTO.class);
                dto.setProductCode(product.getProductCode());
                dto.setProductName(product.getProductName());
                dto.setUnit(product.getUom1());
                dto.setTotalPrice(ptd.getPrice() * ptd.getQuantity());
                dto.setSoNo(poConfirm!=null?poConfirm.getSaleOrderNumber():null);
                dto.setExport(0);
                rs1.add(dto);
            }
            CoverResponse<List<PoTransDetailDTO>, List<PoTransDetailDTO>> response =
                    new CoverResponse(rs, rs1);
            return response;
        } else {
            PoTrans poTransExport = repository.findById(poTrans.getFromTransId()).get();
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(id);
            List<PoTransDetail> poTransDetailsExport = poTransDetailRepository.getPoTransDetailByTransId(poTransExport.getId());
            for (int i = 0; i < poTransDetails.size(); i++) {
                PoTransDetail ptd = poTransDetails.get(i);
                Product product = productRepository.findById(ptd.getProductId()).get();
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTransDetailDTO dto = modelMapper.map(ptd, PoTransDetailDTO.class);
                dto.setProductCode(product.getProductCode());
                dto.setProductName(product.getProductName());
                dto.setUnit(product.getUom1());
                dto.setTotalPrice(ptd.getPrice() * ptd.getQuantity());
                dto.setExport(poTransDetailsExport.get(i).getQuantity());
                rs.add(dto);
            }
            CoverResponse<List<PoTransDetailDTO>, List<PoTransDetailDTO>> response =
                    new CoverResponse(rs, rs1);
            return  response.getResponse();
        }
    }

    public CoverResponse<List<StockAdjustmentTransDetailDTO>, List<StockAdjustmentTransDetailDTO>> getStockAdjustmentTransDetail(Long id) {
        List<StockAdjustmentTransDetail> adjustmentTransDetails = stockAdjustmentTransDetailRepository.getStockAdjustmentTransDetailsByTransId(id);
        List<StockAdjustmentTransDetailDTO> rs = new ArrayList<>();
        for (StockAdjustmentTransDetail satd : adjustmentTransDetails) {
            Product product = productRepository.findById(satd.getProductId()).get();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentTransDetailDTO dto = modelMapper.map(satd, StockAdjustmentTransDetailDTO.class);
            dto.setProductCode(product.getProductCode());
            dto.setProductName(product.getProductName());
            dto.setTotalPrice(satd.getPrice() * satd.getQuantity());
            dto.setUnit(product.getUom1());
            rs.add(dto);
        }
        CoverResponse<List<StockAdjustmentTransDetailDTO>, List<StockAdjustmentTransDetailDTO>> response =
                new CoverResponse(rs, new ArrayList<>());
        return response;
    }

    public CoverResponse<List<StockBorrowingTransDetailDTO>, List<StockBorrowingTransDetailDTO>> getStockBorrowingTransDetail(Long id) {
        List<StockBorrowingTransDetail> borrowingTransDetails = stockBorrowingTransDetailRepository.getStockBorrowingTransDetailByTransId(id);
        List<StockBorrowingTransDetailDTO> rs = new ArrayList<>();
        for (StockBorrowingTransDetail sbtd : borrowingTransDetails) {
            Product product = productRepository.findById(sbtd.getProductId()).get();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingTransDetailDTO dto = modelMapper.map(sbtd, StockBorrowingTransDetailDTO.class);
            dto.setProductCode(product.getProductCode());
            dto.setProductName(product.getProductName());
            dto.setTotalPrice(sbtd.getPrice() * sbtd.getQuantity());
            dto.setUnit(product.getUom1());
            rs.add(dto);
        }
        CoverResponse<List<StockBorrowingTransDetailDTO>, List<StockBorrowingTransDetailDTO>> response =
                new CoverResponse(rs, new ArrayList<>());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage setNotImport(Long id, NotImportRequest request) {
        PoConfirm poConfirm = poConfirmRepository.findById(id).get();
        if (poConfirm != null) {
            poConfirm.setStatus(4);
            poConfirm.setDenyReason(request.getReasonDeny());
            poConfirm.setDenyDate(LocalDateTime.now());
            poConfirmRepository.save(poConfirm);
            return ResponseMessage.NOT_IMPORT_SUCCESS;
        }
        return null;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public WareHouseTypeDTO getWareHouseTypeName(Long shopId) {
        CustomerTypeDTO cusType = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if(cusType == null) throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);
        if (cusType != null & cusType.getWareHouseTypeId() == null)
            throw new ValidateException(ResponseMessage.WAREHOUSE_TYPE_ID_MUST_NOT_BE_NUll);
        Optional<WareHouseType> wareHouseType = wareHouseTypeRepository.findById(cusType.getWareHouseTypeId());

        if (wareHouseType.isPresent()) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            WareHouseTypeDTO dto = modelMapper.map(wareHouseType.get(), WareHouseTypeDTO.class);
            return dto;
        }
        throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public ResponseMessage createPoTrans(ReceiptCreateRequest request, Long userId, Long shopId) {

        Response<PoTrans> response = new Response<>();
        UserDTO user = userClient.getUserByIdV1(userId);
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        List<String> lstRedInvoiceNo = repository.getRedInvoiceNo();
        if(lstRedInvoiceNo.contains(request.getRedInvoiceNo())) throw new ValidateException(ResponseMessage.RED_INVOICE_NO_IS_EXIST);
        if(request.getRedInvoiceNo() != null && request.getRedInvoiceNo().length() >50) throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
        checkNoteLength(request.getNote());
        if (request.getPoId() == null) {
            List<String> lstInternalNumber = repository.getRedInvoiceNo();
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
            poRecord.setRedInvoiceNo(request.getRedInvoiceNo());
            poRecord.setPocoNumber(request.getPoCoNumber());
            poRecord.setOrderDate(request.getOrderDate());
            poRecord.setInternalNumber(request.getInternalNumber());
            poRecord.setShopId(shopId);
            poRecord.setStatus(1);
            poRecord.setType(1);
            repository.save(poRecord);
            Integer total = 0;
            if(request.getLst() != null){
                for (ReceiptCreateDetailRequest rcdr : request.getLst()) {
                    List<BigDecimal> productList = productRepository.getProductId();
                    if (productList.contains(BigDecimal.valueOf(rcdr.getProductId()))) {
                        PoTransDetail poTransDetail = modelMapper.map(rcdr, PoTransDetail.class);
                        if (String.valueOf(rcdr.getQuantity()).length()>10)
                            throw new ValidateException(ResponseMessage.QUANTITY_INVALID_STRING_LENGTH);
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
                        StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(rcdr.getProductId(), customerTypeDTO.getWareHouseTypeId());
                        if (stockTotal == null)
                            throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                        if (stockTotal.getQuantity() == null) {
                            stockTotal.setQuantity(0);
                        }
                        stockTotal.setQuantity(stockTotal.getQuantity() + rcdr.getQuantity());
                        stockTotal.setUpdatedBy(user.getUserAccount());
                        stockTotalRepository.save(stockTotal);
                    } else {
                        throw new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS);
                    }
                }
            } else throw  new ValidateException(ResponseMessage.PLEASE_IMPORT_PRODUCTS);
            poRecord.setTotalQuantity(total);
            poRecord.setTotalAmount(0D);
            poRecord.setNumSku(request.getLst().size());
            repository.save(poRecord);
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
            poRecord.setType(1);
            poRecord.setStatus(1);
            poRecord.setIsDebit(false);
            repository.save(poRecord);
            List<PoDetail> poDetails = poDetailRepository.findByPoId(poConfirm.getId());
            for (PoDetail pod : poDetails) {
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTransDetail poTransDetail = modelMapper.map(pod, PoTransDetail.class);
                poTransDetail.setTransId(poRecord.getId());
                poTransDetail.setAmount(pod.getQuantity() * pod.getPrice());
                poTransDetail.setReturnAmount(0);
                poTransDetail.setTransDate(date);
                poTransDetailRepository.save(poTransDetail);
                Product product = productRepository.findById(pod.getProductId()).get();
                if (product == null) response.setFailure(ResponseMessage.NO_CONTENT);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(product.getId(), customerTypeDTO.getWareHouseTypeId());
                if (stockTotal == null)
                    throw  new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                if (stockTotal.getQuantity() == null) {
                    stockTotal.setQuantity(0);
                }
                stockTotal.setQuantity(stockTotal.getQuantity() + pod.getQuantity());
                stockTotalRepository.save(stockTotal);
            }
            poRecord.setNumSku(poDetails.size());
            poRecord.setNote(request.getNote());
            poRecord.setCreatedBy(user.getUserAccount());
            poConfirm.setStatus(1);
            repository.save(poRecord);
            poConfirmRepository.save(poConfirm);
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
            ApParamDTO reason = apparamClient.getReasonV1(stockAdjustment.getReasonId());
            if(reason == null) throw new ValidateException(ResponseMessage.REASON_NOT_FOUND);
            stockAdjustmentRecord.setTransDate(LocalDateTime.now());
            stockAdjustmentRecord.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
            stockAdjustmentRecord.setTransCode(stockAdjustment.getAdjustmentCode());
            stockAdjustmentRecord.setAdjustmentDate(stockAdjustment.getAdjustmentDate());
            stockAdjustmentRecord.setShopId(shopId);
            stockAdjustmentRecord.setRedInvoiceNo(createRedInvoiceCodeAdjust(shopId));
            stockAdjustmentRecord.setInternalNumber(createInternalCodeAdjust(shopId));
            stockAdjustmentRecord.setType(1);
            stockAdjustmentRecord.setStatus(1);
            stockAdjustmentRecord.setNote(reason.getApParamName());
            stockAdjustmentRecord.setAdjustmentId(request.getPoId());
            stockAdjustmentTransRepository.save(stockAdjustmentRecord);
            List<StockAdjustmentDetail> stockAdjustmentDetails = stockAdjustmentDetailRepository.getStockAdjustmentDetailByAdjustmentId(stockAdjustment.getId());
            Integer totalQuantity = 0;
            Double totalAmount = 0D;
            LocalDateTime date = LocalDateTime.now();
            SaleOrder order = new SaleOrder();
            order.setType(3);
            order.setOrderNumber(createRedInvoiceCodeAdjust(shopId));
            order.setOrderDate(date);
            order.setShopId(shopId);
            order.setSalemanId(userId);
            order.setCustomerId(cus.getId());
            order.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
            order.setBalance(0D);
            order.setNote(reason.getApParamName());
            order.setMemberCardAmount(0D);
            order.setTotalVoucher(0D);
            order.setPaymentType(1);
            order.setDeliveryType(0);
            order.setTotalCustomerPurchase(cus.getTotalBill());
            order.setOrderType(1);
            order.setAutoPromotionNotVat(0D);
            order.setAutoPromotion(0D);
            order.setZmPromotion(0D);
            order.setTotalPromotionNotVat(0D);
            order.setAutoPromotionVat(0D);
            order.setCustomerPurchase(0D);
            order.setDiscountCodeAmount(0D);
            order.setUsedRedInvoice(false);
            saleOrderRepository.save(order);
            for (StockAdjustmentDetail sad : stockAdjustmentDetails) {
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                StockAdjustmentTransDetail stockAdjustmentTransDetail = modelMapper.map(sad, StockAdjustmentTransDetail.class);
                stockAdjustmentTransDetail.setTransId(stockAdjustmentRecord.getId());
                totalQuantity += sad.getQuantity();
                totalAmount += sad.getPrice() * sad.getProductId();
                Product product = productRepository.findById(sad.getProductId()).get();
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(product.getId(), customerTypeDTO.getWareHouseTypeId());
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
                Optional<Price> price = productPriceRepository.getByASCCustomerType(sad.getProductId());
                if(!price.isPresent()) throw  new ValidateException(ResponseMessage.NO_PRICE_APPLIED);
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
            stockAdjustmentRecord.setTotalQuantity(totalQuantity);
            stockAdjustmentRecord.setTotalAmount(totalAmount);
            stockAdjustmentRecord.setCreatedBy(user.getUserAccount());
            stockAdjustmentRecord.setNote(stockAdjustment.getDescription());
            stockAdjustment.setStatus(3);
            stockAdjustment.setUpdatedBy(user.getUserAccount());
            stockAdjustmentTransRepository.save(stockAdjustmentRecord);
            stockAdjustmentRepository.save(stockAdjustment);
            return ResponseMessage.CREATED_SUCCESSFUL;
        }
        return null;
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
                totalQuantity += sbd.getQuantity();
                totalAmount += sbd.getPrice() * sbd.getQuantity();
                Product product = productRepository.findById(sbd.getProductId()).get();
                if (product == null) response.setFailure(ResponseMessage.NO_CONTENT);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(product.getId(), customerTypeDTO.getWareHouseTypeId());
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
            stockBorrowingTrans.setNote(stockBorrowing.getNote());
            stockBorrowing.setStatusImport(2);
            stockBorrowingRepository.save(stockBorrowing);
            stockBorrowingTransRepository.save(stockBorrowingTrans);
            return ResponseMessage.CREATED_SUCCESSFUL;
        }
        return null;
    }

    private void checkNoteLength(String note){
        if(note != null && note.length()>250)
            throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
    }

    public ResponseMessage updatePoTrans(ReceiptUpdateRequest request, Long id,String userName) {

        checkNoteLength(request.getNote());
        PoTrans poTrans = repository.getPoTransById(id);
        if(poTrans==null) throw new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED);
        List<String> lstRedInvoiceNo = repository.getRedInvoiceNo();
        lstRedInvoiceNo.remove(poTrans.getRedInvoiceNo());
        if (request.getRedInvoiceNo() != null && request.getRedInvoiceNo().getBytes(StandardCharsets.UTF_8).length>50) throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
        if(lstRedInvoiceNo.contains(request.getRedInvoiceNo())) throw new ValidateException(ResponseMessage.RED_INVOICE_NO_IS_EXIST);
        poTrans.setNote(request.getNote());
        poTrans.setRedInvoiceNo(request.getRedInvoiceNo());
        if (DateUtils.formatDate2StringDate(poTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))){
            if (poTrans.getPoId() == null) {
                if(request.getInternalNumber() != null && request.getInternalNumber().length()>50) throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
                if(request.getPoCoNumber() == null || request.getPoCoNumber().length()>50) throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
                poTrans.setPocoNumber(request.getPoCoNumber());
                poTrans.setOrderDate(request.getOrderDate());
                poTrans.setInternalNumber(request.getInternalNumber());
                if(request.getLstUpdate()==null) throw  new ValidateException(ResponseMessage.NOT_EXISTS);
                if (!request.getLstUpdate().isEmpty()) {
                    List<BigDecimal> poDetailId = poTransDetailRepository.getIdByTransId(id);
                    List<BigDecimal> productIds = poTransDetailRepository.getProductByTransId(id);
                    List<BigDecimal> listUpdate = request.getLstUpdate().stream().map(e-> BigDecimal.valueOf(e.getId())).collect(Collectors.toList());
                    // delete
                    for (BigDecimal podId : poDetailId) {
                        if (!listUpdate.contains(podId)) {
                            Optional<PoTransDetail> poTransDetail = poTransDetailRepository.findById(podId.longValue());
                            if(!poTransDetail.isPresent()) throw new ValidateException(ResponseMessage.NOT_EXISTS);
                            Optional<Product> product = productRepository.findById((poTransDetail.get().getProductId()));
                            if(!product.isPresent()) throw new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS);
                            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(poTransDetail.get().getProductId(), poTrans.getWareHouseTypeId());
                            if (stockTotal == null) throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                            stockTotal.setQuantity(stockTotal.getQuantity() - poTransDetail.get().getQuantity());
                            if(stockTotal.getQuantity()<0) throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SS,product.get().getProductName(),poTrans.getTransCode());
                            stockTotal.setUpdatedBy(userName);
                            stockTotalRepository.save(stockTotal);
                            poTransDetailRepository.delete(poTransDetail.get());
                        }
                    }
                    Integer total = 0;
                    for (int i = 0; i < request.getLstUpdate().size(); i++) {
                        ReceiptCreateDetailRequest rcdr = request.getLstUpdate().get(i);
                        Optional<Product> product = productRepository.findById(rcdr.getProductId());
                        if(rcdr.getQuantity()==null ) throw new ValidateException(ResponseMessage.QUANTITY_CAN_NOT_BE_NULL);
                        if(rcdr.getQuantity().toString().length()>7) throw new ValidateException(ResponseMessage.QUANTITY_INVALID_STRING_LENGTH);
                        /// update
                        if (rcdr.getId() != null && poDetailId.contains(BigDecimal.valueOf(rcdr.getId()))) {
                            PoTransDetail po = poTransDetailRepository.findById(rcdr.getId()).get();
                            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(rcdr.getProductId(), poTrans.getWareHouseTypeId());
                            if (stockTotal == null) throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                            stockTotal.setQuantity(stockTotal.getQuantity() - po.getQuantity() + rcdr.getQuantity());
                            if(stockTotal.getQuantity()<0) throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SS,product.get().getProductName(),poTrans.getTransCode());
                            po.setQuantity(rcdr.getQuantity());
                            po.setTransDate(poTrans.getTransDate());
                            total += po.getQuantity();
                            poTransDetailRepository.save(po);
                            stockTotalRepository.save(stockTotal);
                        }
                        /// create new
                        else if (rcdr.getId()== -1 && !productIds.contains(BigDecimal.valueOf(rcdr.getProductId()))) {
                            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                            PoTransDetail poTransDetail = modelMapper.map(rcdr, PoTransDetail.class);
                            poTransDetail.setTransId(poTrans.getId());
                            poTransDetail.setPrice(0D);
                            poTransDetail.setPriceNotVat(0D);
                            poTransDetail.setShopId(poTrans.getShopId());
                            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(rcdr.getProductId(), poTrans.getWareHouseTypeId());
                            if (stockTotal == null) throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                            stockTotal.setQuantity(stockTotal.getQuantity() + rcdr.getQuantity());
                            poTransDetail.setTransDate(poTrans.getTransDate());
                            total += poTransDetail.getQuantity();
                            poTransDetailRepository.save(poTransDetail);
                            stockTotalRepository.save(stockTotal);
                        }
                        else if(rcdr.getId()==null && productIds.contains(BigDecimal.valueOf(rcdr.getProductId()))) throw new ValidateException(ResponseMessage.DO_NOT_CHEAT_DATABASE);
                        else if(rcdr.getId()!=null && !productIds.contains(BigDecimal.valueOf(rcdr.getProductId()))) throw  new ValidateException(ResponseMessage.DO_NOT_CHEAT_DATABASE);
                        poTrans.setTotalQuantity(total);
                        repository.save(poTrans);
                    }
                }else throw new ValidateException(ResponseMessage.PLEASE_IMPORT_PRODUCTS);
            }
            poTrans.setUpdatedBy(userName);
            repository.save(poTrans);
        }
        return ResponseMessage.UPDATE_SUCCESSFUL;
    }
    public ResponseMessage updateAdjustmentTrans(ReceiptUpdateRequest request, Long id,String userName) {

        StockAdjustmentTrans adjustmentTrans = stockAdjustmentTransRepository.getStockAdjustmentTransById(id);
        checkNoteLength(request.getNote());
        if (DateUtils.formatDate2StringDate(adjustmentTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            adjustmentTrans.setNote(request.getNote());
            adjustmentTrans.setUpdatedBy(userName);
            adjustmentTrans.setInternalNumber(request.getInternalNumber());
            stockAdjustmentTransRepository.save(adjustmentTrans);
            return ResponseMessage.UPDATE_SUCCESSFUL;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);

    }
    public ResponseMessage updateBorrowingTrans(ReceiptUpdateRequest request, Long id,String userName) {

        StockBorrowingTrans borrowingTrans = stockBorrowingTransRepository.getStockBorrowingTransById(id);
        checkNoteLength(request.getNote());
        if (DateUtils.formatDate2StringDate(borrowingTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            borrowingTrans.setNote(request.getNote());
            borrowingTrans.setUpdatedBy(userName);
            borrowingTrans.setInternalNumber(request.getInternalNumber());
            stockBorrowingTransRepository.save(borrowingTrans);
            return ResponseMessage.UPDATE_SUCCESSFUL;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);

    }
    public ResponseMessage removePoTrans(Long id,String userName) {

        PoTrans poTrans = repository.getPoTransById(id);
        if(poTrans== null) throw new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED);
        if (DateUtils.formatDate2StringDate(poTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
            for (PoTransDetail ptd : poTransDetails) {
                Optional<Product> product = productRepository.findById(ptd.getProductId());
                if(!product.isPresent()) throw  new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(ptd.getProductId(), poTrans.getWareHouseTypeId());
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
                poConfirmRepository.save(poConfirm);
            }
            poTrans.setStatus(-1);
            poTrans.setUpdatedBy(userName);
            repository.save(poTrans);
            return ResponseMessage.DELETE_SUCCESSFUL;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }
    public ResponseMessage removeStockAdjustmentTrans(Long id,String userName) {

        StockAdjustmentTrans stockAdjustmentTrans = stockAdjustmentTransRepository.getAdjustTransImportById(id);
        if(stockAdjustmentTrans==null) throw new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);
        if (DateUtils.formatDate2StringDate(stockAdjustmentTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            List<StockAdjustmentTransDetail> stockAdjustmentTransDetails = stockAdjustmentTransDetailRepository.getStockAdjustmentTransDetailsByTransId(stockAdjustmentTrans.getId());
            for (StockAdjustmentTransDetail satd : stockAdjustmentTransDetails) {
                Optional<Product> product = productRepository.findById(satd.getProductId());
                if(!product.isPresent()) throw  new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(satd.getProductId(), stockAdjustmentTrans.getWareHouseTypeId());
                stockTotal.setQuantity(stockTotal.getQuantity() - satd.getQuantity());
                if(stockTotal.getQuantity()<0){
                    throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SS,product.get().getProductName(),stockAdjustmentTrans.getTransCode());
                }
                stockTotal.setUpdatedBy(userName);
                stockTotalRepository.save(stockTotal);
            }
            SaleOrder order = saleOrderRepository.findSaleOrderByOrderNumber(stockAdjustmentTrans.getRedInvoiceNo());
            if(order == null) throw  new ValidateException(ResponseMessage.SALE_ORDER_ID_MUST_NOT_BE_NULL);
            List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.getSaleOrderDetailBySaleOrderId(order.getId());
            saleOrderDetailRepository.deleteAll(saleOrderDetails);
            saleOrderRepository.delete(order);
            StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(stockAdjustmentTrans.getAdjustmentId()).get();
            stockAdjustment.setStatus(1);
            stockAdjustment.setUpdatedBy(userName);
            stockAdjustmentTrans.setUpdatedBy(userName);
            stockAdjustmentTrans.setStatus(-1);
            stockAdjustmentRepository.save(stockAdjustment);
            stockAdjustmentTransRepository.save(stockAdjustmentTrans);
            return ResponseMessage.DELETE_SUCCESSFUL;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }
    public ResponseMessage removeStockBorrowingTrans(Long id,String userName) {

        StockBorrowingTrans stockBorrowingTrans = stockBorrowingTransRepository.getStockBorrowingTransById(id);
        if (DateUtils.formatDate2StringDate(stockBorrowingTrans.getTransDate()).equals(DateUtils.formatDate2StringDate(LocalDateTime.now()))) {
            List<StockBorrowingTransDetail> stockBorrowingTransDetails = stockBorrowingTransDetailRepository.getStockBorrowingTransDetailByTransId(stockBorrowingTrans.getId());
            for (StockBorrowingTransDetail sbtd : stockBorrowingTransDetails) {
                Optional<Product> product = productRepository.findById(sbtd.getProductId());
                if(!product.isPresent()) throw  new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(sbtd.getProductId(), stockBorrowingTrans.getWareHouseTypeId());
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
            stockBorrowingTransRepository.save(stockBorrowingTrans);
            stockBorrowingRepository.save(stockBorrowing);
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
        Integer mm = currentDate.getMonthValue();
        Integer dd = currentDate.getDayOfMonth();
        int reciNum = stockAdjustmentTransRepository.getQuantityAdjustmentTransVer2();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("SAL.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(yy);
        reciCode.append(mm.toString());
        reciCode.append(dd.toString());
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
