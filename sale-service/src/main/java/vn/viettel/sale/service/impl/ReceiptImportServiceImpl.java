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
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.dto.sale.WareHouseTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ReceiptImportService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.ApparamClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.ReceiptSpecification;
import vn.viettel.sale.util.CreateCodeUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

    @Override
    public Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>> find(String redInvoiceNo, Date fromDate, Date toDate, Integer type, Long shopId, Pageable pageable) {
        int totalQuantity = 0;
        Float totalPrice = 0F;
        if (type == null) {
            if (redInvoiceNo!=null) redInvoiceNo = redInvoiceNo.toUpperCase();
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


            return new Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>>()
                    .withData(response);
        } else if (type == 0) {
            if (redInvoiceNo!=null) redInvoiceNo = redInvoiceNo.toUpperCase();
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
            return new Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>>()
                    .withData(response);
        } else if (type == 1) {
            if (redInvoiceNo!=null) redInvoiceNo = redInvoiceNo.toUpperCase();
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
            return new Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>>()
                    .withData(response);
        } else if (type == 2) {
            if (redInvoiceNo!=null) redInvoiceNo = redInvoiceNo.toUpperCase();
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
            return new Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>>()
                    .withData(response);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Object> createReceipt(ReceiptCreateRequest request, Long userId, Long shopId) {
        switch (request.getImportType()) {
            case 0:
                return new Response<>().withData(createPoTrans(request, userId, shopId));
            case 1:
                return new Response<>().withData(createAdjustmentTrans(request, userId, shopId));
            case 2:
                return new Response<>().withData(createBorrowingTrans(request, userId, shopId));
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Object> updateReceiptImport(ReceiptUpdateRequest request, Long id,String userName) {
        switch (request.getType()) {
            case 0:
                return new Response<>().withData(updatePoTrans(request, id,userName));
            case 1:
                return new Response<>().withData(updateAdjustmentTrans(request, id,userName));
            case 2:
                return new Response<>().withData(updateBorrowingTrans(request, id,userName));
        }
        return null;
    }

    @Override
    public Response<String> removeReceiptImport( Long id,Integer type,String userName) {
        Response<String> response = new Response<>();
        switch (type) {
            case 0:
                removePoTrans(id,userName);
                break;
            case 1:
                return response.withError(ResponseMessage.DO_NOT_HAVE_PERMISSION_TO_DELETE);
            case 2:
                removeStockBorrowingTrans(id,userName);
                break;
        }
        return response.withData(ResponseMessage.DELETE_SUCCESSFUL.statusCodeValue());
    }

    @Override
    public Response<Object> getForUpdate(Integer type, Long id) {
        switch (type) {
            case 0:
                return new Response<>().withData(getPoTransById(id));
            case 1:
                return new Response<>().withData(getStockAdjustmentById(id));
            case 2:
                return new Response<>().withData(getStockBorrowingById(id));
        }
        return null;
    }

    @Override
    public Response<List<PoConfirmDTO>> getListPoConfirm() {
        List<PoConfirm> poConfirms = poConfirmRepository.getPoConfirm();
        List<PoConfirmDTO> rs = new ArrayList<>();
        for (PoConfirm pc : poConfirms) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoConfirmDTO dto = modelMapper.map(pc, PoConfirmDTO.class);
            rs.add(dto);
        }
        Response<List<PoConfirmDTO>> response = new Response<>();
        return response.withData(rs);
    }

    @Override
    public Response<List<StockAdjustmentDTO>> getListStockAdjustment() {
        List<StockAdjustment> stockAdjustments = stockAdjustmentRepository.getStockAdjustment();
        List<StockAdjustmentDTO> rs = new ArrayList<>();
        for (StockAdjustment sa : stockAdjustments) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentDTO dto = modelMapper.map(sa, StockAdjustmentDTO.class);
            rs.add(dto);
        }
        Response<List<StockAdjustmentDTO>> response = new Response<>();
        return response.withData(rs);
    }

    @Override
    public Response<List<StockBorrowingDTO>> getListStockBorrowing(Long toShopId) {
        List<StockBorrowing> stockBorrowings = stockBorrowingRepository.getStockBorrowing(toShopId);
        List<StockBorrowingDTO> rs = new ArrayList<>();
        for (StockBorrowing sb : stockBorrowings) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingDTO dto = modelMapper.map(sb, StockBorrowingDTO.class);
            rs.add(dto);
        }
        Response<List<StockBorrowingDTO>> response = new Response<>();
        return response.withData(rs);
    }

    @Override
    public Response<CoverResponse<List<PoDetailDTO>, TotalResponse>> getPoDetailByPoId(Long id, Long shopId) {
        int totalQuantity = 0;
        Float totalPrice = 0F;
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

        return new Response<CoverResponse<List<PoDetailDTO>, TotalResponse>>()
                .withData(response);
    }
    @Override
    public Response<Object> getTransDetail(Integer type, Long id, Long shopId) {
        Response<Object> response = new Response<>();
        switch (type) {
            case 0:
                try {
                    return new Response<>().withData(getPoTransDetail(id));
                } catch (Exception e) {
                    return response.withError(ResponseMessage.NOT_FOUND);
                }
            case 1:
                try {
                    return new Response<>().withData(getStockAdjustmentTransDetail(id));
                } catch (Exception e) {
                    return response.withError(ResponseMessage.NOT_FOUND);
                }
            case 2:
                try {
                    return new Response<>().withData(getStockBorrowingTransDetail(id));
                } catch (Exception e) {
                    return response.withError(ResponseMessage.NOT_FOUND);
                }
        }
        return null;
    }


    @Override
    public Response<CoverResponse<List<PoDetailDTO>, TotalResponse>> getPoDetailByPoIdAndPriceIsNull(Long id, Long shopId) {
        int totalQuantity = 0;
        Float totalPrice = 0F;
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

        return new Response<CoverResponse<List<PoDetailDTO>, TotalResponse>>()
                .withData(response);
    }
    @Override
    public Response<CoverResponse<List<StockAdjustmentDetailDTO>, TotalResponse>> getStockAdjustmentDetail(Long id) {
        int totalQuantity = 0;
        Float totalPrice = 0F;
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

        return new Response<CoverResponse<List<StockAdjustmentDetailDTO>, TotalResponse>>()
                .withData(response);
    }

    @Override
    public Response<CoverResponse<List<StockBorrowingDetailDTO>, TotalResponse>> getStockBorrowingDetail(Long id) {
        int totalQuantity = 0;
        Float totalPrice = 0F;
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

        return new Response<CoverResponse<List<StockBorrowingDetailDTO>, TotalResponse>>()
                .withData(response);
    }

    public Object getPoTransDetail(Long id) {
        List<PoTransDetailDTO> rs = new ArrayList<>();
        List<PoTransDetailDTO> rs1 = new ArrayList<>();
        PoTrans poTrans = repository.findById(id).get();
        if (poTrans.getFromTransId() == null) {
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
                dto.setSoNo(poTrans.getRedInvoiceNo());
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
    public Response<String> setNotImport(Long id, NotImportRequest request) {
        Date date = new Date();
        Response<String> response = new Response<>();
        PoConfirm poConfirm = poConfirmRepository.findById(id).get();
        if (poConfirm != null) {
            poConfirm.setStatus(4);
            poConfirm.setDenyReason(request.getReasonDeny());
            poConfirm.setDenyDate(date);
            poConfirmRepository.save(poConfirm);
            return response.withData(ResponseMessage.SUCCESSFUL.toString());
        }
        return null;
    }

    @Override
    public Response<WareHouseTypeDTO> getWareHouseTypeName(Long shopId) {
        CustomerTypeDTO cusType = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if(cusType == null) throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);
        WareHouseType wareHouseType = wareHouseTypeRepository.findById(cusType.getWareHouseTypeId()).get();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        WareHouseTypeDTO dto = modelMapper.map(wareHouseType, WareHouseTypeDTO.class);
        return new Response<WareHouseTypeDTO>().withData(dto);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Object createPoTrans(ReceiptCreateRequest request, Long userId, Long shopId) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        Response<PoTrans> response = new Response<>();
        UserDTO user = userClient.getUserByIdV1(userId);
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if (request.getPoId() == null) {
            List<String> lstRedInvoiceNo = repository.getRedInvoiceNo();
            List<String> lstInternalNumber = repository.getRedInvoiceNo();
            List<String> lstPoNumber = repository.getRedInvoiceNo();
            if(lstPoNumber.contains(request.getPoNumber()))
                throw new ValidateException(ResponseMessage.PO_NO_IS_EXIST);
            if(lstInternalNumber.contains(request.getInternalNumber()))
                throw  new ValidateException(ResponseMessage.INTERNAL_NUMBER_IS_EXIST);
            if(lstRedInvoiceNo.contains(request.getRedInvoiceNo()))
                throw new ValidateException(ResponseMessage.RED_INVOICE_NO_IS_EXIST);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoTrans poRecord = modelMapper.map(request, PoTrans.class);
            poRecord.setTransDate(date);
            poRecord.setTransCode(createPoTransCode(shopId));
            poRecord.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
            poRecord.setRedInvoiceNo(request.getRedInvoiceNo());
            poRecord.setPoNumber(request.getPoNumber());
            poRecord.setOrderDate(request.getOrderDate());
            poRecord.setInternalNumber(request.getInternalNumber());
            poRecord.setCreateUser(user.getUserAccount());
            poRecord.setShopId(shopId);
            poRecord.setStatus(1);
            poRecord.setType(1);
            repository.save(poRecord);
            Integer total = 0;
            for (ReceiptCreateDetailRequest rcdr : request.getLst()) {
                List<BigDecimal> productList = productRepository.getProductId();
                if (productList.contains(BigDecimal.valueOf(rcdr.getProductId()))) {
                    PoTransDetail poTransDetail = modelMapper.map(rcdr, PoTransDetail.class);
                    poTransDetail.setTransId(poRecord.getId());
                    poTransDetail.setTransDate(poRecord.getTransDate());
                    poTransDetail.setProductId(rcdr.getProductId());
                    poTransDetail.setPrice(0F);
                    poTransDetail.setAmount(0F);
                    poTransDetail.setAmountNotVat(0F);
                    poTransDetail.setReturnAmount(0);
                    poTransDetail.setPriceNotVat(0F);
                    poTransDetail.setShopId(shopId);
                    total += rcdr.getQuantity();
                    poTransDetailRepository.save(poTransDetail);
                    StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(rcdr.getProductId(), customerTypeDTO.getWareHouseTypeId());
                    if (stockTotal == null)
                        response.setFailure(ResponseMessage.NO_CONTENT);
                    if (stockTotal.getQuantity() == null) {
                        stockTotal.setQuantity(0);
                    }
                    stockTotal.setQuantity(stockTotal.getQuantity() + rcdr.getQuantity());
                    stockTotalRepository.save(stockTotal);
                } else {
                    throw new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS);
                }
            }
            poRecord.setTotalQuantity(total);
            poRecord.setTotalAmount(0F);
            poRecord.setNumSku(request.getLst().size());
            poRecord.setCreatedAt(ts);
            repository.save(poRecord);
            return poRecord;
        } else {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoTrans poRecord = modelMapper.map(request, PoTrans.class);
            PoConfirm poConfirm = poConfirmRepository.findById(request.getPoId()).get();
            poRecord.setTransDate(date);
            poRecord.setTransCode(createPoTransCode(shopId));
            poRecord.setShopId(shopId);
            poRecord.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
            poRecord.setOrderDate(poConfirm.getOrderDate());
            poRecord.setInternalNumber(poConfirm.getInternalNumber());
            poRecord.setPoId(poConfirm.getId());
            poRecord.setTotalAmount(poConfirm.getTotalAmount());
            poRecord.setTotalQuantity(poConfirm.getTotalQuantity());
            poRecord.setCreateUser(user.getUserAccount());
            poRecord.setPoNumber(poConfirm.getPoNumber());
            poRecord.setCreateUser(user.getUserAccount());
            poRecord.setRedInvoiceNo(poConfirm.getSaleOrderNumber());
            poRecord.setCreatedAt(ts);
            poRecord.setType(1);
            poRecord.setStatus(1);
            repository.save(poRecord);
            List<PoDetail> poDetails = poDetailRepository.findByPoId(poConfirm.getId());
            for (PoDetail pod : poDetails) {
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTransDetail poTransDetail = modelMapper.map(pod, PoTransDetail.class);
                poTransDetail.setTransId(poRecord.getId());
                poTransDetail.setAmount(pod.getQuantity() * pod.getPrice());
                poTransDetail.setReturnAmount(0);
                poTransDetailRepository.save(poTransDetail);
                Product product = productRepository.findById(pod.getProductId()).get();
                if (product == null) response.setFailure(ResponseMessage.NO_CONTENT);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(product.getId(), customerTypeDTO.getWareHouseTypeId());
                if (stockTotal == null)
                    response.setFailure(ResponseMessage.NO_CONTENT);
                if (stockTotal.getQuantity() == null) {
                    stockTotal.setQuantity(0);
                }
                stockTotal.setQuantity(stockTotal.getQuantity() + pod.getQuantity());
                stockTotalRepository.save(stockTotal);
            }
            poRecord.setNumSku(poDetails.size());
            poRecord.setNote(request.getNote());
            poConfirm.setStatus(1);
            repository.save(poRecord);
            poConfirmRepository.save(poConfirm);
            return poRecord;
        }
    }
    public StockAdjustmentTrans createAdjustmentTrans(ReceiptCreateRequest request, Long userId, Long shopId) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        Response<StockAdjustmentTrans> response = new Response<>();
        UserDTO user = userClient.getUserByIdV1(userId);
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if (request.getImportType() == 1) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentTrans stockAdjustmentRecord = modelMapper.map(request, StockAdjustmentTrans.class);
            StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(request.getPoId()).get();
            ApParamDTO reason = apparamClient.getReasonV1(stockAdjustment.getReasonId());
            if(reason == null) throw new ValidateException(ResponseMessage.REASON_NOT_FOUND);
            stockAdjustmentRecord.setTransDate(date);
            stockAdjustmentRecord.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
            stockAdjustmentRecord.setTransCode(stockAdjustment.getAdjustmentCode());
            stockAdjustmentRecord.setAdjustmentDate(stockAdjustment.getAdjustmentDate());
            stockAdjustmentRecord.setShopId(shopId);
            stockAdjustmentRecord.setRedInvoiceNo(createRedInvoiceCodeAdjust(shopId));
            stockAdjustmentRecord.setInternalNumber(createInternalCodeAdjust(shopId));
            stockAdjustmentRecord.setCreateUser(user.getUserAccount());
            stockAdjustmentRecord.setType(1);
            stockAdjustmentRecord.setStatus(1);
            stockAdjustmentRecord.setNote(reason.getApParamName());
            stockAdjustmentRecord.setAdjustmentId(request.getPoId());
            stockAdjustmentTransRepository.save(stockAdjustmentRecord);
            SaleOrder order = new SaleOrder();
            order.setType(3);
            order.setOrderNumber(createRedInvoiceCodeAdjust(shopId));
            order.setOrderDate(date);
            saleOrderRepository.save(order);
            List<StockAdjustmentDetail> stockAdjustmentDetails = stockAdjustmentDetailRepository.getStockAdjustmentDetailByAdjustmentId(stockAdjustment.getId());
            Integer totalQuantity = 0;
            Float totalAmount = 0F;
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
                stockAdjustmentTransDetail.setStockQuantity(stockTotal.getQuantity());
                stockAdjustmentTransDetail.setOriginalQuantity(sad.getQuantity());
                stockTotal.setQuantity(stockTotal.getQuantity() + sad.getQuantity());
                stockTotalRepository.save(stockTotal);
                stockAdjustmentTransDetailRepository.save(stockAdjustmentTransDetail);
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                SaleOrderDetail saleOrderDetail = modelMapper.map(sad, SaleOrderDetail.class);
                saleOrderDetailRepository.save(saleOrderDetail);
            }
            stockAdjustmentRecord.setTotalQuantity(totalQuantity);
            stockAdjustmentRecord.setTotalAmount(totalAmount);
            stockAdjustmentRecord.setCreatedAt(ts);
            stockAdjustment.setStatus(3);
            stockAdjustmentTransRepository.save(stockAdjustmentRecord);
            stockAdjustmentRepository.save(stockAdjustment);
            return stockAdjustmentRecord;
        }
        return null;
    }

    public StockBorrowingTrans createBorrowingTrans(ReceiptCreateRequest request, Long userId, Long shopId) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        Response<StockBorrowingTrans> response = new Response<>();
        UserDTO user = userClient.getUserByIdV1(userId);
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if (request.getImportType() == 2) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingTrans stockBorrowingTrans = modelMapper.map(request, StockBorrowingTrans.class);
            StockBorrowing stockBorrowing = stockBorrowingRepository.findById(request.getPoId()).get();
            stockBorrowingTrans.setTransDate(date);
            stockBorrowingTrans.setTransCode(createBorrowingTransCode(shopId));
            stockBorrowingTrans.setWareHouseTypeId(customerTypeDTO.getWareHouseTypeId());
            stockBorrowingTrans.setRedInvoiceNo(stockBorrowing.getPoBorrowCode());
            stockBorrowingTrans.setBorrowDate(stockBorrowing.getBorrowDate());
            stockBorrowingTrans.setFromShopId(stockBorrowing.getShopId());
            stockBorrowingTrans.setToShopId(stockBorrowing.getToShopId());
            stockBorrowingTrans.setShopId(shopId);
            stockBorrowingTrans.setCreateUser(user.getUserAccount());
            stockBorrowingTrans.setType(1);
            stockBorrowingTrans.setStatus(1);
            stockBorrowingTrans.setStockBorrowingId(request.getPoId());
            stockBorrowingTransRepository.save(stockBorrowingTrans);
            List<StockBorrowingDetail> stockBorrowingDetails = stockBorrowingDetailRepository.findByBorrowingId(stockBorrowing.getId());
            Integer totalQuantity = 0;
            Float totalAmount = 0F;
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
                    response.setFailure(ResponseMessage.NO_CONTENT);
                if (stockTotal.getQuantity() == null) {
                    stockTotal.setQuantity(0);
                }
                stockTotal.setQuantity(stockTotal.getQuantity() + sbd.getQuantity());

                stockTotalRepository.save(stockTotal);
                stockBorrowingTransDetailRepository.save(stockBorrowingTransDetail);
            }
            stockBorrowingTrans.setTotalAmount(totalAmount);
            stockBorrowingTrans.setTotalQuantity(totalQuantity);
            stockBorrowingTrans.setCreatedAt(ts);
            stockBorrowing.setStatusImport(2);
            stockBorrowingRepository.save(stockBorrowing);
            stockBorrowingTransRepository.save(stockBorrowingTrans);
            return stockBorrowingTrans;
        }
        return null;
    }
    public Response<String> updatePoTrans(ReceiptUpdateRequest request, Long id,String userName) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        Response<String> response = new Response<>();
        if(request.getNote().getBytes(StandardCharsets.UTF_8).length>100)
            throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
        PoTrans poTrans = repository.getPoTransById(id);
        poTrans.setNote(request.getNote());
        if (formatDate(poTrans.getTransDate()).equals(formatDate(date))){
            if (poTrans.getPoId() == null) {
                if(request.getPoNumber().getBytes(StandardCharsets.UTF_8).length>50)
                    throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
                if (request.getRedInvoiceNo().getBytes(StandardCharsets.UTF_8).length>50)
                    throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
                if(request.getInternalNumber().getBytes(StandardCharsets.UTF_8).length>50)
                    throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
                poTrans.setPoNumber(request.getPoNumber());
                poTrans.setInternalNumber(request.getInternalNumber());
                poTrans.setRedInvoiceNo(request.getRedInvoiceNo());
                poTrans.setOrderDate(request.getOrderDate());
                if (!request.getLstUpdate().isEmpty()) {
                    List<BigDecimal> poDetailId = poTransDetailRepository.getIdByTransId(id);
                    List<BigDecimal> productIds = poTransDetailRepository.getProductByTransId(id);
                    List<BigDecimal> listUpdate = request.getLstUpdate().stream().map(e-> BigDecimal.valueOf(e.getId())).collect(Collectors.toList());
                    // delete
                    for (BigDecimal podId : poDetailId) {
                        if (!listUpdate.contains(podId)) {
                            PoTransDetail poTransDetail = poTransDetailRepository.findById(podId.longValue()).get();
                            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(poTransDetail.getProductId(), poTrans.getWareHouseTypeId());
                            if (stockTotal == null)
                                throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                            stockTotal.setQuantity(stockTotal.getQuantity() - poTransDetail.getQuantity());
                            if(stockTotal.getQuantity()<0)
                                throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE);
                            stockTotalRepository.save(stockTotal);
                            poTransDetailRepository.delete(poTransDetail);
                        }
                    }
                    for (int i = 0; i < request.getLstUpdate().size(); i++) {
                        ReceiptCreateDetailRequest rcdr = request.getLstUpdate().get(i);
                        /// update
                        if (rcdr.getId() != null && poDetailId.contains(BigDecimal.valueOf(rcdr.getId()))) {
                            PoTransDetail po = poTransDetailRepository.findById(rcdr.getId()).get();
                            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(rcdr.getProductId(), poTrans.getWareHouseTypeId());
                            if (stockTotal == null) throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                            stockTotal.setQuantity(stockTotal.getQuantity() - po.getQuantity() + rcdr.getQuantity());
                            if(stockTotal.getQuantity()<0)
                                throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE);
                            po.setQuantity(rcdr.getQuantity());
                            poTransDetailRepository.save(po);
                            stockTotalRepository.save(stockTotal);
                        }
                        /// create new
                        else if (rcdr.getId()== -1 && !productIds.contains(BigDecimal.valueOf(rcdr.getProductId()))) {
                            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                            PoTransDetail poTransDetail = modelMapper.map(rcdr, PoTransDetail.class);
                            poTransDetail.setTransId(poTrans.getId());
                            poTransDetail.setPrice((float) 0);
                            poTransDetail.setPriceNotVat((float) 0);
                            poTransDetail.setShopId(poTrans.getShopId());
                            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(rcdr.getProductId(), poTrans.getWareHouseTypeId());
                            if (stockTotal == null)
                                throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                            stockTotal.setQuantity(stockTotal.getQuantity() + rcdr.getQuantity());
                            poTransDetailRepository.save(poTransDetail);
                            stockTotal.setUpdateUser(userName);
                            stockTotalRepository.save(stockTotal);
                        }
                        else if(rcdr.getId()==null && productIds.contains(BigDecimal.valueOf(rcdr.getProductId())))
                            throw new ValidateException(ResponseMessage.DO_NOT_CHEAT_DATABASE);
                        else if(rcdr.getId()!=null && !productIds.contains(BigDecimal.valueOf(rcdr.getProductId())))
                            throw  new ValidateException(ResponseMessage.DO_NOT_CHEAT_DATABASE);
                        poTrans.setNote(request.getNote());
                        repository.save(poTrans);
                    }
                }
            }
            poTrans.setUpdatedAt(ts);
            poTrans.setUpdateUser(userName);
            repository.save(poTrans);
        } else {
            throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);
        }
        return response.withData(ResponseMessage.UPDATE_SUCCESSFUL.statusCodeValue());
    }
    public Response<String> updateAdjustmentTrans(ReceiptUpdateRequest request, Long id,String userName) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        StockAdjustmentTrans adjustmentTrans = stockAdjustmentTransRepository.getStockAdjustmentTransById(id);
        if (formatDate(adjustmentTrans.getTransDate()).equals(formatDate(date))) {
            adjustmentTrans.setNote(request.getNote());
            adjustmentTrans.setUpdatedAt(ts);
            adjustmentTrans.setUpdateUser(userName);
            stockAdjustmentTransRepository.save(adjustmentTrans);
            return new Response<String>().withData(ResponseMessage.UPDATE_SUCCESSFUL.statusCodeValue());
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);

    }
    public Response<String> updateBorrowingTrans(ReceiptUpdateRequest request, Long id,String userName) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        StockBorrowingTrans borrowingTrans = stockBorrowingTransRepository.getStockBorrowingTransById(id);
        if (formatDate(borrowingTrans.getTransDate()).equals(formatDate(date))) {
            borrowingTrans.setNote(request.getNote());
            borrowingTrans.setUpdatedAt(ts);
            borrowingTrans.setUpdateUser(userName);
            stockBorrowingTransRepository.save(borrowingTrans);
            return new Response<String>().withData(ResponseMessage.UPDATE_SUCCESSFUL.statusCodeValue());
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);

    }

    public Response<String> removePoTrans(Long id,String userName) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        Response<String> response = new Response<>();
        PoTrans poTrans = repository.getPoTransById(id);
        if(poTrans== null) throw new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED);
        if (formatDate(poTrans.getTransDate()).equals(formatDate(date))) {
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
            for (PoTransDetail ptd : poTransDetails) {
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(ptd.getProductId(), poTrans.getWareHouseTypeId());
                int quantity = stockTotal.getQuantity() - ptd.getQuantity();
                if(quantity<0) throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE);
                stockTotal.setQuantity(quantity);
                stockTotal.setUpdateUser(userName);
                stockTotal.setUpdatedAt(ts);
                stockTotalRepository.save(stockTotal);
            }
            if (poTrans.getPoId() != null) {
                PoConfirm poConfirm = poConfirmRepository.findById(poTrans.getPoId()).get();
                poConfirm.setStatus(0);
                poConfirm.setImportUser(userName);
                poConfirm.setUpdatedAt(ts);
                poConfirmRepository.save(poConfirm);
            }
            poTrans.setStatus(-1);
            poTrans.setUpdateUser(userName);
            repository.save(poTrans);
            return response.withData(ResponseMessage.DELETE_SUCCESSFUL.statusCodeValue());
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }
    public Response<String> removeStockBorrowingTrans(Long id,String userName) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        Response<String> response = new Response<>();
        StockBorrowingTrans stockBorrowingTrans = stockBorrowingTransRepository.getStockBorrowingTransById(id);
        if (formatDate(stockBorrowingTrans.getTransDate()).equals(formatDate(date))) {
            List<StockBorrowingTransDetail> stockBorrowingTransDetails = stockBorrowingTransDetailRepository.getStockBorrowingTransDetailByTransId(stockBorrowingTrans.getId());
            for (StockBorrowingTransDetail sbtd : stockBorrowingTransDetails) {
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(sbtd.getProductId(), stockBorrowingTrans.getWareHouseTypeId());
                stockTotal.setQuantity(stockTotal.getQuantity() - sbtd.getQuantity());
                if(stockTotal.getQuantity()<0){
                    throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE);
                }
                stockTotal.setUpdateUser(userName);
                stockTotal.setUpdatedAt(ts);
                stockTotalRepository.save(stockTotal);
            }
            StockBorrowing stockBorrowing = stockBorrowingRepository.findById(stockBorrowingTrans.getStockBorrowingId()).get();
            stockBorrowing.setStatusImport(1);
            stockBorrowing.setUpdatedAt(ts);
            stockBorrowingTrans.setUpdateUser(userName);
            stockBorrowingTrans.setStatus(-1);
            stockBorrowingTransRepository.save(stockBorrowingTrans);
            stockBorrowingRepository.save(stockBorrowing);
            return response.withData(ResponseMessage.DELETE_SUCCESSFUL.statusCodeValue());
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_DELETE);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public String createPoTransCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        int reciNum = repository.getQuantityPoTrans();
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
        if (!sat.getId().equals(transId)) {
            throw new ValidateException(ResponseMessage.VALIDATED_ERROR);
        }
        StockAdjustmentTransDTO stockAdjustmentTransDTO = modelMapper.map(sat, StockAdjustmentTransDTO.class);
        stockAdjustmentTransDTO.setWareHouseTypeName(wareHouseTypeRepository.findById(sat.getWareHouseTypeId()).get().getWareHouseTypeName());

        return stockAdjustmentTransDTO;
    }

    public StockBorrowingTransDTO getStockBorrowingById(Long transId) {

        StockBorrowingTrans sbt = stockBorrowingTransRepository.getStockBorrowingTransById(transId);
        if (!sbt.getId().equals(transId)) {
            throw new ValidateException(ResponseMessage.VALIDATED_ERROR);
        }
        StockBorrowingTransDTO stockBorrowingTransDTO = modelMapper.map(sbt, StockBorrowingTransDTO.class);
        stockBorrowingTransDTO.setWareHouseTypeName(wareHouseTypeRepository.findById(sbt.getWareHouseTypeId()).get().getWareHouseTypeName());
        return stockBorrowingTransDTO;
    }

}
