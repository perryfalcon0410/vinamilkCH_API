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
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.ReceiptCreateDetailRequest;
import vn.viettel.sale.messaging.ReceiptCreateRequest;
import vn.viettel.sale.messaging.ReceiptUpdateRequest;
import vn.viettel.sale.messaging.TotalResponse;
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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

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
            Page<PoTrans> list1 = repository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNull()).and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo).and(ReceiptSpecification.hasFromDateToDate(fromDate, toDate)).and(ReceiptSpecification.hasTypeImport())), pageable);
            Page<StockAdjustmentTrans> list2 = stockAdjustmentTransRepository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNullA()).and(ReceiptSpecification.hasRedInvoiceNoA(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateA(fromDate, toDate)).and(ReceiptSpecification.hasTypeImportA()), pageable);
            Page<StockBorrowingTrans> list3 = stockBorrowingTransRepository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNullB().and(ReceiptSpecification.hasRedInvoiceNoB(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateB(fromDate, toDate)).and(ReceiptSpecification.hasTypeImportB())), pageable);
            List<PoTransDTO> listAddDTO1 = new ArrayList<>();
            for (PoTrans poTrans : list1) {
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTransDTO poRecord = modelMapper.map(poTrans, PoTransDTO.class);
                poRecord.setReceiptType(0);
                if (poTrans.getFromTransId() != null) {
                    PoTrans poTransReturn = repository.findById(poTrans.getFromTransId()).get();
                }
                listAddDTO1.add(poRecord);
            }
            Page<PoTransDTO> page1 = new PageImpl<>(listAddDTO1);
            List<ReceiptImportListDTO> listDTO1 = page1.getContent().stream().map(
                    item -> modelMapper.map(item, ReceiptImportListDTO.class)
            ).collect(Collectors.toList());
            List<StockAdjustmentTransDTO> listAddDTO2 = new ArrayList<>();
            for (StockAdjustmentTrans stockAdjustmentTrans : list2) {
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
            for (StockBorrowingTrans stockBorrowingTrans : list3) {
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

            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getTotalQuantity() == null)
                    throw new ValidateException(ResponseMessage.QUANTITY_CAN_NOT_BE_NULL);
                if (result.get(i).getTotalAmount() == null)
                    throw new ValidateException(ResponseMessage.AMOUNT_CAN_NOT_BE_NULL);
                totalQuantity += result.get(i).getTotalQuantity();
                totalPrice += result.get(i).getTotalAmount();
            }
            TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(result);
            CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> response =
                    new CoverResponse(pageResponse, totalResponse);
            return new Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>>()
                    .withData(response);
        } else if (type == 0) {
            Page<PoTrans> list1 = repository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNull()).
                    and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo).and(ReceiptSpecification.hasFromDateToDate(fromDate, toDate)).
                            and(ReceiptSpecification.hasTypeImport())), pageable);
            List<PoTransDTO> listAddDTO1 = new ArrayList<>();
            for (PoTrans poTrans : list1) {
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
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getTotalQuantity() == null)
                    throw new ValidateException(ResponseMessage.QUANTITY_CAN_NOT_BE_NULL);
                if (result.get(i).getTotalAmount() == null)
                    throw new ValidateException(ResponseMessage.AMOUNT_CAN_NOT_BE_NULL);
                totalQuantity += result.get(i).getTotalQuantity();
                totalPrice += result.get(i).getTotalAmount();
            }
            TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(result);
            CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> response =
                    new CoverResponse(pageResponse, totalResponse);
            return new Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>>()
                    .withData(response);
        } else if (type == 1) {
            Page<StockAdjustmentTrans> list2 = stockAdjustmentTransRepository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNullA()).and(ReceiptSpecification.hasRedInvoiceNoA(redInvoiceNo)).
                    and(ReceiptSpecification.hasFromDateToDateA(fromDate, toDate)).and(ReceiptSpecification.hasTypeImportA()), pageable);
            List<StockAdjustmentTransDTO> listAddDTO2 = new ArrayList<>();
            for (StockAdjustmentTrans stockAdjustmentTrans : list2) {
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
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getTotalQuantity() == null)
                    throw new ValidateException(ResponseMessage.QUANTITY_CAN_NOT_BE_NULL);
                if (result.get(i).getTotalAmount() == null)
                    throw new ValidateException(ResponseMessage.AMOUNT_CAN_NOT_BE_NULL);
                totalQuantity += result.get(i).getTotalQuantity();
                totalPrice += result.get(i).getTotalAmount();
            }
            TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(result);
            CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> response =
                    new CoverResponse(pageResponse, totalResponse);
            return new Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>>()
                    .withData(response);
        } else if (type == 2) {
            Page<StockBorrowingTrans> list3 = stockBorrowingTransRepository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNullB().and(ReceiptSpecification.hasRedInvoiceNoB(redInvoiceNo)).
                    and(ReceiptSpecification.hasFromDateToDateB(fromDate, toDate)).and(ReceiptSpecification.hasTypeImportB())), pageable);
            List<StockBorrowingTransDTO> listAddDTO3 = new ArrayList<>();
            for (StockBorrowingTrans stockBorrowingTrans : list3) {
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
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getTotalQuantity() == null)
                    throw new ValidateException(ResponseMessage.QUANTITY_CAN_NOT_BE_NULL);
                if (result.get(i).getTotalAmount() == null)
                    throw new ValidateException(ResponseMessage.AMOUNT_CAN_NOT_BE_NULL);
                totalQuantity += result.get(i).getTotalQuantity();
                totalPrice += result.get(i).getTotalAmount();
            }
            TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(result);
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
    public Response<Object> updateReceiptImport(ReceiptUpdateRequest request, Long id) {
        Response<Object> response = new Response<>();
        switch (request.getType()) {
            case 0:
                return new Response<>().withData(updatePoTrans(request, id));
            case 1:
                return new Response<>().withData(updateAdjustmentTrans(request, id));
            case 2:
                return new Response<>().withData(updateBorrowingTrans(request, id));
        }
        return null;
    }

    @Override
    public Response<String> removeReceiptImport( Long id,Integer type) {
        Response<String> response = new Response<>();
        switch (type) {
            case 0:
                removePoTrans(id);
                break;
            case 1:
                return response.withError(ResponseMessage.DELETE_FAILED);
            case 2:
                removeStockBorrowingTrans(id);
                break;
        }
        return response.withData(ResponseMessage.SUCCESSFUL.toString());
    }

    @Override
    public Response<Object> getForUpdate(Integer type, Long id) {
        Response<Object> response = new Response<>();
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
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoDetailDTO dto = modelMapper.map(pt, PoDetailDTO.class);
            dto.setProductCode(productRepository.findById(pt.getProductId()).get().getProductCode());
            dto.setProductName(productRepository.findById(pt.getProductId()).get().getProductName());
            dto.setShopName(shopClient.getById(shopId).getData().getShopName());
            dto.setShopAddress(shopClient.getById(shopId).getData().getAddress());
            dto.setShopContact("Tel: " + shopClient.getById(shopId).getData().getPhone() + " Fax: " + shopClient.getById(shopId).getData().getFax());
            dto.setSoNo(poConfirmRepository.findById(pt.getPoId()).get().getSaleOrderNumber());
            dto.setUnit(productRepository.findById(pt.getProductId()).get().getUom1());
            dto.setTotalPrice(pt.getPrice() * pt.getQuantity());
            totalPrice = +(pt.getPrice() * pt.getQuantity());
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
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoDetailDTO dto = modelMapper.map(pt, PoDetailDTO.class);
            dto.setProductCode(productRepository.findById(pt.getProductId()).get().getProductCode());
            dto.setProductName(productRepository.findById(pt.getProductId()).get().getProductName());
            dto.setShopName(shopClient.getById(shopId).getData().getShopName());
            dto.setShopAddress(shopClient.getById(shopId).getData().getAddress());
            dto.setShopContact("Tel: " + shopClient.getById(shopId).getData().getPhone() + " Fax: " + shopClient.getById(shopId).getData().getFax());
            dto.setSoNo(poConfirmRepository.findById(pt.getPoId()).get().getSaleOrderNumber());
            dto.setUnit(productRepository.findById(pt.getProductId()).get().getUom1());
            dto.setTotalPrice(pt.getPrice() * pt.getQuantity());
            totalPrice = +(pt.getPrice() * pt.getQuantity());
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
    public Response<List<StockAdjustmentDetailDTO>> getStockAdjustmentDetail(Long id) {
        List<StockAdjustmentDetail> adjustmentDetails = stockAdjustmentDetailRepository.getStockAdjustmentDetailByAdjustmentId(id);
        List<StockAdjustmentDetailDTO> rs = new ArrayList<>();
        for (StockAdjustmentDetail sad : adjustmentDetails) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentDetailDTO dto = modelMapper.map(sad, StockAdjustmentDetailDTO.class);
            dto.setProductCode(productRepository.findById(sad.getProductId()).get().getProductCode());
            dto.setProductName(productRepository.findById(sad.getProductId()).get().getProductName());
            dto.setLicenseNumber(stockAdjustmentRepository.findById(sad.getAdjustmentId()).get().getAdjustmentCode());
            dto.setUnit(productRepository.findById(sad.getProductId()).get().getUom1());
            dto.setTotalPrice(sad.getPrice() * sad.getQuantity());
            rs.add(dto);
        }
        Response<List<StockAdjustmentDetailDTO>> response = new Response<>();
        return response.withData(rs);
    }

    @Override
    public Response<List<StockBorrowingDetailDTO>> getStockBorrowingDetail(Long id) {
        List<StockBorrowingDetail> borrowingDetails = stockBorrowingDetailRepository.findByBorrowingId(id);
        List<StockBorrowingDetailDTO> rs = new ArrayList<>();
        for (StockBorrowingDetail sbd : borrowingDetails) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingDetailDTO dto = modelMapper.map(sbd, StockBorrowingDetailDTO.class);
            dto.setProductCode(productRepository.findById(sbd.getProductId()).get().getProductCode());
            dto.setProductName(productRepository.findById(sbd.getProductId()).get().getProductName());
            dto.setLicenseNumber(stockBorrowingRepository.findById(sbd.getBorrowingId()).get().getPoBorrowCode());
            dto.setUnit(productRepository.findById(sbd.getProductId()).get().getUom1());
            dto.setTotalPrice(sbd.getPrice() * sbd.getQuantity());
            rs.add(dto);
        }
        Response<List<StockBorrowingDetailDTO>> response = new Response<>();
        return response.withData(rs);
    }

    public CoverResponse<List<PoTransDetailDTO>, List<PoTransDetailDTO>> getPoTransDetail(Long id) {
        List<PoTransDetailDTO> rs = new ArrayList<>();
        List<PoTransDetailDTO> rs1 = new ArrayList<>();
        PoTrans poTrans = repository.findById(id).get();
        if (poTrans.getFromTransId() == null) {
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetail0(id);
            for (int i = 0; i < poTransDetails.size(); i++) {
                PoTransDetail ptd = poTransDetails.get(i);
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTransDetailDTO dto = modelMapper.map(ptd, PoTransDetailDTO.class);
                dto.setProductCode(productRepository.findById(ptd.getProductId()).get().getProductCode());
                dto.setProductName(productRepository.findById(ptd.getProductId()).get().getProductName());
                dto.setUnit(productRepository.findById(ptd.getProductId()).get().getUom1());
                dto.setTotalPrice(ptd.getPrice() * ptd.getQuantity());
                dto.setSoNo(poTrans.getRedInvoiceNo());
                dto.setExport(0);
                rs.add(dto);
            }
            List<PoTransDetail> poTransDetails1 = poTransDetailRepository.getPoTransDetail1(id);
            for (int i = 0; i < poTransDetails1.size(); i++) {
                PoTransDetail ptd = poTransDetails1.get(i);
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTransDetailDTO dto = modelMapper.map(ptd, PoTransDetailDTO.class);
                dto.setProductCode(productRepository.findById(ptd.getProductId()).get().getProductCode());
                dto.setProductName(productRepository.findById(ptd.getProductId()).get().getProductName());
                dto.setUnit(productRepository.findById(ptd.getProductId()).get().getUom1());
                dto.setTotalPrice(ptd.getPrice() * ptd.getQuantity());
                dto.setExport(0);
                rs1.add(dto);
            }
            CoverResponse<List<PoTransDetailDTO>, List<PoTransDetailDTO>> response =
                    new CoverResponse(rs, rs1);
            return response;
        } else {
            PoTrans poTransExport = repository.findById(poTrans.getFromTransId()).get();
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransIdAndDeletedAtIsNull(id);
            List<PoTransDetail> poTransDetailsExport = poTransDetailRepository.getPoTransDetailByTransIdAndDeletedAtIsNull(poTransExport.getId());
            for (int i = 0; i < poTransDetails.size(); i++) {
                PoTransDetail ptd = poTransDetails.get(i);
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTransDetailDTO dto = modelMapper.map(ptd, PoTransDetailDTO.class);
                dto.setProductCode(productRepository.findById(ptd.getProductId()).get().getProductCode());
                dto.setProductName(productRepository.findById(ptd.getProductId()).get().getProductName());
                dto.setUnit(productRepository.findById(ptd.getProductId()).get().getUom1());
                dto.setTotalPrice(ptd.getPrice() * ptd.getQuantity());
                dto.setExport(poTransDetailsExport.get(i).getQuantity());
                rs.add(dto);
            }
            CoverResponse<List<PoTransDetailDTO>, List<PoTransDetailDTO>> response =
                    new CoverResponse(rs, rs1);
            return (CoverResponse<List<PoTransDetailDTO>, List<PoTransDetailDTO>>) response.getResponse();
        }
    }

    public CoverResponse<List<StockAdjustmentTransDetailDTO>, List<StockAdjustmentTransDetailDTO>> getStockAdjustmentTransDetail(Long id) {
        List<StockAdjustmentTransDetail> adjustmentTransDetails = stockAdjustmentTransDetailRepository.getStockAdjustmentTransDetailsByTransId(id);
        List<StockAdjustmentTransDetailDTO> rs = new ArrayList<>();
        for (StockAdjustmentTransDetail satd : adjustmentTransDetails) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentTransDetailDTO dto = modelMapper.map(satd, StockAdjustmentTransDetailDTO.class);
            dto.setProductCode(productRepository.findById(satd.getProductId()).get().getProductCode());
            dto.setProductName(productRepository.findById(satd.getProductId()).get().getProductName());
            dto.setTotalPrice(satd.getPrice() * satd.getQuantity());
            dto.setUnit(productRepository.findById(satd.getProductId()).get().getUom1());
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
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingTransDetailDTO dto = modelMapper.map(sbtd, StockBorrowingTransDetailDTO.class);
            dto.setProductCode(productRepository.findById(sbtd.getProductId()).get().getProductCode());
            dto.setProductName(productRepository.findById(sbtd.getProductId()).get().getProductName());
            dto.setTotalPrice(sbtd.getPrice() * sbtd.getQuantity());
            dto.setUnit(productRepository.findById(sbtd.getProductId()).get().getUom1());
            rs.add(dto);
        }
        CoverResponse<List<StockBorrowingTransDetailDTO>, List<StockBorrowingTransDetailDTO>> response =
                new CoverResponse(rs, new ArrayList<>());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<String> setNotImport(Long id) {
        Response<String> response = new Response<>();
        PoConfirm poConfirm = poConfirmRepository.findById(id).get();
        if (poConfirm != null) {
            poConfirm.setStatus(4);
            poConfirmRepository.save(poConfirm);
            return response.withData(ResponseMessage.SUCCESSFUL.toString());
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Object createPoTrans(ReceiptCreateRequest request, Long userId, Long shopId) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        Response<PoTrans> response = new Response<>();
        UserDTO user = userClient.getUserById(userId);
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopId(shopId);
        if (request.getPoNumber() != null) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoTrans poRecord = modelMapper.map(request, PoTrans.class);
            poRecord.setTransDate(date);
            poRecord.setTransCode(createPoTransCode(shopId));
            poRecord.setWareHouseTypeId(customerTypeDTO.getWareHoseTypeId());
            poRecord.setRedInvoiceNo(request.getRedInvoiceNo());
            poRecord.setCreateUser(user.getUserAccount());
            poRecord.setShopId(shopId);
            poRecord.setType(1);
            repository.save(poRecord);
            Integer total = 0;
            for (ReceiptCreateDetailRequest rcdr : request.getLst()) {
                List<BigDecimal> productList = productRepository.getProductId();
                if (productList.contains(rcdr.getProductId())) {
                    PoTransDetail poTransDetail = modelMapper.map(rcdr, PoTransDetail.class);
                    poTransDetail.setTransId(poRecord.getId());
                    poTransDetail.setProductId(rcdr.getProductId());
                    poTransDetail.setPrice(0F);
                    poTransDetail.setShopId(shopId);
                    total += rcdr.getQuantity();
                    poTransDetailRepository.save(poTransDetail);
                    StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(rcdr.getProductId(), customerTypeDTO.getWareHoseTypeId());
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
            poRecord.setWareHouseTypeId(customerTypeDTO.getWareHoseTypeId());
            poRecord.setOrderDate(poConfirm.getOrderDate());
            poRecord.setInternalNumber(poConfirm.getInternalNumber());
            poRecord.setPoId(poConfirm.getId());
            poRecord.setTotalAmount(poConfirm.getTotalAmount());
            poRecord.setTotalQuantity(poConfirm.getTotalQuantity());
            poRecord.setCreateUser(user.getUserAccount());
            poRecord.setPoNumber(poConfirm.getPoNumber());
            poRecord.setCreateUser(user.getUserAccount());
            poRecord.setCreatedAt(ts);
            poRecord.setType(1);
            repository.save(poRecord);
            List<PoDetail> poDetails = poDetailRepository.findByPoId(poConfirm.getId());
            for (PoDetail pod : poDetails) {
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTransDetail poTransDetail = modelMapper.map(pod, PoTransDetail.class);
                poTransDetail.setTransId(poRecord.getId());
                poTransDetail.setAmount(pod.getQuantity() * pod.getPrice());
                poTransDetailRepository.save(poTransDetail);
                Product product = productRepository.findById(pod.getProductId()).get();
                if (product == null) response.setFailure(ResponseMessage.NO_CONTENT);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(product.getId(), customerTypeDTO.getWareHoseTypeId());
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
        UserDTO user = userClient.getUserById(userId);
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopId(shopId);
        if (request.getImportType() == 1) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentTrans stockAdjustmentRecord = modelMapper.map(request, StockAdjustmentTrans.class);
            StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(request.getPoId()).get();
            ApParamDTO reason = apparamClient.getReason(stockAdjustment.getReasonId());
            if(reason == null) throw new ValidateException(ResponseMessage.REASON_NOT_FOUND);
            stockAdjustmentRecord.setTransDate(date);
            stockAdjustmentRecord.setWareHouseTypeId(customerTypeDTO.getWareHoseTypeId());
            stockAdjustmentRecord.setTransCode(stockAdjustment.getAdjustmentCode());
            stockAdjustmentRecord.setAdjustmentDate(stockAdjustment.getAdjustmentDate());
            stockAdjustmentRecord.setShopId(shopId);
            stockAdjustmentRecord.setRedInvoiceNo(createRedInvoiceCodeAdjust(shopId));
            stockAdjustmentRecord.setInternalNumber(createInternalCodeAdjust(shopId));
            stockAdjustmentRecord.setCreateUser(user.getUserAccount());
            stockAdjustmentRecord.setType(1);
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
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(product.getId(), customerTypeDTO.getWareHoseTypeId());
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
        UserDTO user = userClient.getUserById(userId);
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopId(shopId);
        if (request.getImportType() == 2) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingTrans stockBorrowingTrans = modelMapper.map(request, StockBorrowingTrans.class);
            StockBorrowing stockBorrowing = stockBorrowingRepository.findById(request.getPoId()).get();
            stockBorrowingTrans.setTransDate(date);
            stockBorrowingTrans.setTransCode(createBorrowingTransCode(shopId));
            stockBorrowingTrans.setWareHouseTypeId(customerTypeDTO.getWareHoseTypeId());
            stockBorrowingTrans.setRedInvoiceNo(stockBorrowing.getPoBorrowCode());
            stockBorrowingTrans.setBorrowDate(stockBorrowing.getBorrowDate());
            stockBorrowingTrans.setShopId(shopId);
            stockBorrowingTrans.setCreateUser(user.getUserAccount());
            stockBorrowingTrans.setType(1);
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
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(product.getId(), customerTypeDTO.getWareHoseTypeId());
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
            stockBorrowing.setStatus(5);
            stockBorrowingRepository.save(stockBorrowing);
            stockBorrowingTransRepository.save(stockBorrowingTrans);
            return stockBorrowingTrans;
        }
        return null;
    }
    public Response<String> updatePoTrans(ReceiptUpdateRequest request, Long id) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        Response<String> response = new Response<>();
        PoTrans poTrans = repository.getPoTransByIdAndDeletedAtIsNull(id);
        if (poTrans.getTransDate().equals(date)) {
            if (poTrans.getPoId() == null) {
                if (!request.getLstUpdate().isEmpty()) {
                    List<BigDecimal> poDetailId = poTransDetailRepository.getIdByTransId(id);
                    List<BigDecimal> productIds = poTransDetailRepository.getProductByTransId(id);
                    // delete
                    for (BigDecimal podId : poDetailId) {
                        if (!request.getLstUpdate().contains(podId.longValue())) {
                            PoTransDetail poTransDetail = poTransDetailRepository.findById(podId.longValue()).get();
                            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(poTransDetail.getProductId(), poTrans.getWareHouseTypeId());
                            if (stockTotal == null) throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                            stockTotal.setQuantity(stockTotal.getQuantity() - poTransDetail.getQuantity());
                            poTransDetail.setDeletedAt(ts);
                            poTransDetailRepository.save(poTransDetail);
                            stockTotalRepository.save(stockTotal);
                        }
                    }
                    for (int i = 0; i < request.getLstUpdate().size(); i++) {
                        ReceiptCreateDetailRequest rcdr = request.getLstUpdate().get(i);
                        /// update
                        if (rcdr.getId() != null && productIds.contains(rcdr.getProductId())) {
                            PoTransDetail po = poTransDetailRepository.findById(rcdr.getId()).get();
                            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(rcdr.getProductId(), poTrans.getWareHouseTypeId());
                            if (stockTotal == null) throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                            stockTotal.setQuantity(stockTotal.getQuantity() - po.getQuantity() + rcdr.getQuantity());
                            po.setQuantity(rcdr.getQuantity());
                            poTransDetailRepository.save(po);
                            stockTotalRepository.save(stockTotal);
                        }
                        /// create new
                        if (!productIds.contains(rcdr.getProductId())) {
                            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                            PoTransDetail poTransDetail = modelMapper.map(rcdr, PoTransDetail.class);
                            poTransDetail.setPrice((float) 0);
                            poTransDetail.setPriceNotVat((float) 0);
                            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(rcdr.getProductId(), poTrans.getWareHouseTypeId());
                            if (stockTotal == null)
                                throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                            stockTotal.setQuantity(stockTotal.getQuantity() + rcdr.getQuantity());
                            poTransDetailRepository.save(poTransDetail);
                            stockTotalRepository.save(stockTotal);
                        }
                    }
                }
                return response.withData(ResponseMessage.SUCCESSFUL.toString());
            } else {
                poTrans.setNote(request.getNote());
                repository.save(poTrans);
                return response.withData(ResponseMessage.SUCCESSFUL.toString());
            }
        } else {
            throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);
        }
    }
    public Response<String> updateAdjustmentTrans(ReceiptUpdateRequest request, Long id) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        StockAdjustmentTrans adjustmentTrans = stockAdjustmentTransRepository.getStockAdjustmentTransByIdAndDeletedAtIsNull(id);
        if (adjustmentTrans.getTransDate().equals(date)) {
            adjustmentTrans.setNote(request.getNote());
            adjustmentTrans.setUpdatedAt(ts);
            stockAdjustmentTransRepository.save(adjustmentTrans);
            return new Response<String>().withData(ResponseMessage.SUCCESSFUL.toString());
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);

    }
    public Response<String> updateBorrowingTrans(ReceiptUpdateRequest request, Long id) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        StockBorrowingTrans borrowingTrans = stockBorrowingTransRepository.getStockBorrowingTransByIdAndDeletedAtIsNull(id);
        if (borrowingTrans.getTransDate().equals(date)) {
            borrowingTrans.setNote(request.getNote());
            borrowingTrans.setUpdatedAt(ts);
            stockBorrowingTransRepository.save(borrowingTrans);
            return new Response<String>().withData(ResponseMessage.SUCCESSFUL.toString());
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);

    }

    public Response<String> removePoTrans(Long id) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        Response<String> response = new Response<>();
        PoTrans poTrans = repository.getPoTransByIdAndDeletedAtIsNull(id);
        if (formatDate(poTrans.getTransDate()).equals(formatDate(date))) {
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransIdAndDeletedAtIsNull(poTrans.getId());
            for (PoTransDetail ptd : poTransDetails) {
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(ptd.getProductId(), poTrans.getWareHouseTypeId());
                int quantity = stockTotal.getQuantity() - ptd.getQuantity();
                if(quantity<0) throw new ValidateException(ResponseMessage.STOCK_TOTAL_MUST_GREATER_THAN_0);
                stockTotal.setQuantity(quantity);
                stockTotalRepository.save(stockTotal);
            }
            if (poTrans.getPoId() != null) {
                PoConfirm poConfirm = poConfirmRepository.findById(poTrans.getPoId()).get();
                poConfirm.setStatus(0);
                poTrans.setDeletedAt(ts);
                repository.save(poTrans);
                poConfirmRepository.save(poConfirm);

            }
            return response.withData(ResponseMessage.SUCCESSFUL.toString());
        }
        return null;
    }


    public Response<String> removeStockBorrowingTrans(Long id) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        Response<String> response = new Response<>();
        StockBorrowingTrans stockBorrowingTrans = stockBorrowingTransRepository.getStockBorrowingTransByIdAndDeletedAtIsNull(id);
        if (formatDate(stockBorrowingTrans.getTransDate()).equals(formatDate(date))) {
            List<StockBorrowingTransDetail> stockBorrowingTransDetails = stockBorrowingTransDetailRepository.getStockBorrowingTransDetailByTransId(stockBorrowingTrans.getId());
            for (StockBorrowingTransDetail sbtd : stockBorrowingTransDetails) {
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(sbtd.getProductId(), stockBorrowingTrans.getWareHouseTypeId());
                stockTotal.setQuantity(stockTotal.getQuantity() - sbtd.getQuantity());
                stockTotalRepository.save(stockTotal);
            }
            StockBorrowing stockBorrowing = stockBorrowingRepository.findById(stockBorrowingTrans.getStockBorrowingId()).get();
            stockBorrowing.setStatus(1);
            stockBorrowingTrans.setDeletedAt(ts);
            stockBorrowingTransRepository.save(stockBorrowingTrans);
            stockBorrowingRepository.save(stockBorrowing);
            return response.withData(ResponseMessage.SUCCESSFUL.toString());
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public String createPoTransCode(Long idShop) {
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String yy = df.format(Calendar.getInstance().getTime());
        int reciNum = repository.getQuantityPoTrans();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("IMP.");
        reciCode.append(shopClient.getById(idShop).getData().getShopCode());
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
        reciCode.append(shopClient.getById(idShop).getData().getShopCode());
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
        reciCode.append(shopClient.getById(idShop).getData().getShopCode());
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
        reciCode.append(shopClient.getById(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(CreateCodeUtils.formatReceINumber(reciNum));
        return reciCode.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public PoTransDTO getPoTransById(Long transId) {
        PoTrans poTrans = repository.getPoTransByIdAndDeletedAtIsNull(transId);
        if (!poTrans.getId().equals(transId)) {
            throw new ValidateException(ResponseMessage.VALIDATED_ERROR);
        }
        PoTransDTO poTransDTO = modelMapper.map(poTrans, PoTransDTO.class);
        poTransDTO.setWareHouseTypeName(wareHouseTypeRepository.findById(poTrans.getWareHouseTypeId()).get().getWareHouseTypeName());
        return poTransDTO;
    }


    public StockAdjustmentTransDTO getStockAdjustmentById(Long transId) {

        StockAdjustmentTrans sat = stockAdjustmentTransRepository.getStockAdjustmentTransByIdAndDeletedAtIsNull(transId);
        if (!sat.getId().equals(transId)) {
            throw new ValidateException(ResponseMessage.VALIDATED_ERROR);
        }
        StockAdjustmentTransDTO stockAdjustmentTransDTO = modelMapper.map(sat, StockAdjustmentTransDTO.class);
        stockAdjustmentTransDTO.setWareHouseTypeName(wareHouseTypeRepository.findById(sat.getWareHouseTypeId()).get().getWareHouseTypeName());

        return stockAdjustmentTransDTO;
    }

    public StockBorrowingTransDTO getStockBorrowingById(Long transId) {

        StockBorrowingTrans sbt = stockBorrowingTransRepository.getStockBorrowingTransByIdAndDeletedAtIsNull(transId);
        if (!sbt.getId().equals(transId)) {
            throw new ValidateException(ResponseMessage.VALIDATED_ERROR);
        }
        StockBorrowingTransDTO stockBorrowingTransDTO = modelMapper.map(sbt, StockBorrowingTransDTO.class);
        stockBorrowingTransDTO.setWareHouseTypeName(wareHouseTypeRepository.findById(sbt.getWareHouseTypeId()).get().getWareHouseTypeName());
        return stockBorrowingTransDTO;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
