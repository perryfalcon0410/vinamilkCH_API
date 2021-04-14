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
import vn.viettel.core.db.entity.common.CustomerType;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.db.entity.sale.SaleOrderDetail;
import vn.viettel.core.db.entity.stock.*;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.messaging.ReceiptExportCreateRequest;
import vn.viettel.sale.messaging.ReceiptExportUpdateRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ReceiptExportService;
import vn.viettel.sale.service.dto.PoTransDTO;
import vn.viettel.sale.service.dto.ReceiptImportListDTO;
import vn.viettel.sale.service.dto.StockAdjustmentTransDTO;
import vn.viettel.sale.service.dto.StockBorrowingTransDTO;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.ReceiptSpecification;
import vn.viettel.sale.util.CreateCodeUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    Date date = new Date();
    Timestamp ts =new Timestamp(date.getTime());
    DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
    LocalDate currentDate = LocalDate.now();
    String yy = df.format(Calendar.getInstance().getTime());
    Integer mm = currentDate.getMonthValue();
    Integer dd = currentDate.getDayOfMonth();


    @Override
    public Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>> find(String redInvoiceNo, Date fromDate, Date toDate, Integer type, Long shopId, Pageable pageable) {
        int totalQuantity = 0;
        Float totalPrice = 0F;
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
            for (int i = 0; i < result.size(); i++) {
                totalQuantity += result.get(i).getTotalQuantity();
                totalPrice += result.get(i).getTotalAmount();
            }
            TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(result);
            CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> response =
                    new CoverResponse(pageResponse, totalResponse);
            return new Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>>()
                    .withData(response);
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
            for (int i = 0; i < result.size(); i++) {
                totalQuantity += result.get(i).getTotalQuantity();
                totalPrice += result.get(i).getTotalAmount();
            }
            TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(result);
            CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> response =
                    new CoverResponse(pageResponse, totalResponse);
            return new Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>>()
                    .withData(response);
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
            for (int i = 0; i < result.size(); i++) {
                totalQuantity += result.get(i).getTotalQuantity();
                totalPrice += result.get(i).getTotalAmount();
            }
            TotalResponse totalResponse = new TotalResponse(totalQuantity, totalPrice);
            Page<ReceiptImportListDTO> pageResponse = new PageImpl<>(result);
            CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> response =
                    new CoverResponse(pageResponse, totalResponse);
            return new Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>>()
                    .withData(response);
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
            for (int i = 0; i < result.size(); i++) {
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
    public Response<Object> createReceipt(ReceiptExportCreateRequest request, Long userId,Long shopId) {
        Response<Object> response = new Response<>();
        switch (request.getImportType()){
            case 0:
                try{
                    return new Response<>().withData(createPoTransExport(request,userId,shopId));
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
            case 1:
                try {
                    return new Response<>().withData(createAdjustmentTrans(request,userId,shopId));
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
            case 2:
                try {
                    return new Response<>().withData(createBorrowingTrans(request,userId,shopId));
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
        }
        return response.withData(ResponseMessage.SUCCESSFUL.toString());
    }

    @Override
    public Response<Object> updateReceiptExport(ReceiptExportUpdateRequest request, Long id) {
        Response<Object> response = new Response<>();
        switch (request.getType()){
            case 0:
                try{
                    return new Response<>().withData(updatePoTransExport(request,id));
                }catch (Exception e){
                    return response.withError(ResponseMessage.UPDATE_FAILED);
                }
            case 1:
                try {
                    return new Response<>().withData(updateStockAdjustmentTransExport(request,id));
                }catch (Exception e){
                    return response.withError(ResponseMessage.UPDATE_FAILED);
                }
            case 2:
                try {
                    updateStockBorrowingTransExport(request,id);
                }catch (Exception e){
                    return response.withError(ResponseMessage.UPDATE_FAILED);
                }
                break;
        }
        return null;
    }

    @Override
    public Response<String> removeReceiptExport(ReceiptExportUpdateRequest request, Long id) {
        Response<String> response = new Response<>();
        switch (request.getType()){
            case 0:
                try{
                    removePoTransExport(id);
                }catch (Exception e){
                    return response.withError(ResponseMessage.DELETE_FAILED);
                }
                break;
            case 1:
                return response.withError(ResponseMessage.DELETE_FAILED);
            case 2:
                try {
                    removeStockBorrowingTransExport(id);
                }catch (Exception e){
                    return response.withError(ResponseMessage.DELETE_FAILED);
                }
                break;
        }
        return response.withData(ResponseMessage.SUCCESSFUL.toString());
    }


    @Override
    public Response<Page<PoTransDTO>> getListPoTrans(String transCode, String redInvoiceNo, String internalNumber, String poNo, Date fromDate, Date toDate, Pageable pageable) {
        Response<Page<PoTransDTO>> response = new Response<>();
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
    public Object createPoTransExport(ReceiptExportCreateRequest request, Long userId,Long shopId) {
        Response<PoTrans> response = new Response<>();
        User user = userClient.getUserById(userId);
        CustomerType customerType = customerTypeClient.getCusTypeIdByShopId(shopId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PoTrans poRecord = modelMapper.map(request, PoTrans.class);
        PoTrans poTrans = repository.findById(request.getReceiptImportId()).get();
        poRecord.setTransDate(date);
        poRecord.setWareHouseTypeId(customerType.getWareHoseTypeId());
        poRecord.setTransCode(createPoTransExportCode(shopId));
        poRecord.setShopId(shopId);
        poRecord.setOrderDate(poTrans.getOrderDate());
        poRecord.setRedInvoiceNo(poTrans.getRedInvoiceNo());
        poRecord.setPoNumber(poTrans.getPoNumber());
        poRecord.setInternalNumber(poTrans.getInternalNumber());
        poRecord.setFromTransId(poTrans.getId());
        poRecord.setCreateUser(user.getUserAccount());
        Integer total_quantity =0;
        Float total_amount = 0F;
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
                total_quantity +=poTransDetails.get(i).getQuantity();
                total_amount += poTransDetails.get(i).getAmount();
                poTransDetailRepository.save(poTransDetail);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(poTransDetails.get(i).getProductId(), customerType.getWareHoseTypeId());
                if (stockTotal == null)
                    response.setFailure(ResponseMessage.NO_CONTENT);
                if (stockTotal.getQuantity() == null) {
                    stockTotal.setQuantity(0);
                }
                stockTotal.setQuantity(stockTotal.getQuantity() - poTransDetails.get(i).getQuantity());
                stockTotalRepository.save(stockTotal);
            } else {
                    poTransDetail.setTransId(poRecord.getId());
                    poTransDetail.setProductId(poTransDetails.get(i).getProductId());
                    poTransDetail.setQuantity(request.getLitQuantityRemain().get(i));
                    poTransDetail.setPrice(poTransDetails.get(i).getPrice());
                    poTransDetail.setPriceNotVat(poTransDetails.get(i).getPriceNotVat());
                    poTransDetail.setAmountNotVat(poTransDetails.get(i).getAmountNotVat());
                    poTransDetail.setShopId(shopId);
                    total_quantity +=poTransDetails.get(i).getQuantity();
                    total_amount += poTransDetails.get(i).getAmount();
                    poTransDetailRepository.save(poTransDetail);
                    StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(poTransDetails.get(i).getProductId(), customerType.getWareHoseTypeId());
                    if (stockTotal == null)
                        response.setFailure(ResponseMessage.NO_CONTENT);
                    if (stockTotal.getQuantity() == null) {
                        stockTotal.setQuantity(0);
                    }
                    stockTotalRepository.save(stockTotal);
            }
        }

        poRecord.setTotalQuantity(total_quantity);
        poRecord.setTotalAmount(total_amount);
        repository.save(poRecord);
        return response.withData(poRecord).getData();
    }

    public Object createAdjustmentTrans(ReceiptExportCreateRequest request, Long userId,Long shopId) {
        Response<StockAdjustmentTrans> response = new Response<>();
        User user = userClient.getUserById(userId);
        CustomerType customerType = customerTypeClient.getCusTypeIdByShopId(shopId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        StockAdjustmentTrans poAdjustTrans = modelMapper.map(request, StockAdjustmentTrans.class);
        StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(request.getReceiptImportId()).get();
        poAdjustTrans.setTransDate(date);
        poAdjustTrans.setTransCode(createStockAdjustmentExportCode(shopId));
        poAdjustTrans.setShopId(shopId);
        poAdjustTrans.setRedInvoiceNo(createStockAdjustmentExportRedInvoice(shopId));
        poAdjustTrans.setAdjustmentDate(date);
        poAdjustTrans.setWareHouseTypeId(customerType.getWareHoseTypeId());
        poAdjustTrans.setOrderDate(stockAdjustment.getAdjustmentDate());
        poAdjustTrans.setInternalNumber(createPoTransCode(shopId));
        poAdjustTrans.setAdjustmentId(stockAdjustment.getId());
        poAdjustTrans.setCreateUser(user.getUserAccount());
        poAdjustTrans.setType(2);
        stockAdjustmentTransRepository.save(poAdjustTrans);
        SaleOrder order = new SaleOrder();
        order.setType(4);
        order.setOrderNumber(createStockAdjustmentExportRedInvoice(shopId));
        order.setOrderDate(date);
        saleOrderRepository.save(order);
        List<StockAdjustmentDetail> sads = stockAdjustmentDetailRepository.getStockAdjustmentDetailByAdjustmentId(stockAdjustment.getId());
        Integer totalQuantity =0;
        Float totalAmount = 0F;
        for(StockAdjustmentDetail sad : sads){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentTransDetail satd = modelMapper.map(sad, StockAdjustmentTransDetail.class);
            satd.setTransId(poAdjustTrans.getId());
            totalAmount+=sad.getQuantity();
            totalAmount += sad.getPrice()*sad.getQuantity();
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(sad.getProductId(), customerType.getWareHoseTypeId());
            if(stockTotal == null)
                response.setFailure(ResponseMessage.NO_CONTENT);
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
        poAdjustTrans.setCreatedAt(ts);
        stockAdjustment.setStatus(1);
        stockAdjustmentRepository.save(stockAdjustment);
        stockAdjustmentTransRepository.save(poAdjustTrans);
        return response.withData(poAdjustTrans).getData();
    }

    public Object createBorrowingTrans(ReceiptExportCreateRequest request, Long userId,Long shopId) {
        Response<StockBorrowingTrans> response = new Response<>();
        User user = userClient.getUserById(userId);
        CustomerType customerType = customerTypeClient.getCusTypeIdByShopId(shopId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        StockBorrowingTrans poBorrowTransRecord = modelMapper.map(request, StockBorrowingTrans.class);
        StockBorrowing stockBorrowing = stockBorrowingRepository.findById(request.getReceiptImportId()).get();
        poBorrowTransRecord.setTransDate(date);
        poBorrowTransRecord.setShopId(shopId);
        poBorrowTransRecord.setWareHouseTypeId(customerType.getWareHoseTypeId());
        poBorrowTransRecord.setTransCode(createStockBorrowTransCode(shopId));
        poBorrowTransRecord.setRedInvoiceNo(createStockBorrowTransExportRedInvoice(shopId));
        poBorrowTransRecord.setBorrowDate(stockBorrowing.getBorrowDate());
        //Inernal Number default null
        //Po no default null
        poBorrowTransRecord.setStockBorrowingId(stockBorrowing.getId());
        poBorrowTransRecord.setCreateUser(user.getUserAccount());
        poBorrowTransRecord.setType(2);
        stockBorrowingTransRepository.save(poBorrowTransRecord);
        List<StockBorrowingDetail> sbds = stockBorrowingDetailRepository.findByBorrowingId(stockBorrowing.getId());
        Integer totalQuantity = 0;
        Float totalAmount = 0F;
        for(StockBorrowingDetail sbd : sbds){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingTransDetail sbtd = modelMapper.map(sbd, StockBorrowingTransDetail.class);
            sbtd.setTransId(poBorrowTransRecord.getId());
            totalAmount+= sbd.getPrice()*sbd.getQuantity();
            totalQuantity += sbd.getQuantity();
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(sbd.getProductId(), customerType.getWareHoseTypeId());
            if(stockTotal == null)
                response.setFailure(ResponseMessage.NO_CONTENT);
            if(stockTotal.getQuantity() == null){
                stockTotal.setQuantity(0);
            }
            stockTotal.setQuantity(stockTotal.getQuantity()- sbd.getQuantity());

            stockTotalRepository.save(stockTotal);
            stockBorrowingTransDetailRepository.save(sbtd);
        }
        poBorrowTransRecord.setTotalQuantity(totalQuantity);
        poBorrowTransRecord.setTotalAmount(totalAmount);
        poBorrowTransRecord.setCreatedAt(ts);
        stockBorrowing.setStatus(4);
        stockBorrowingRepository.save(stockBorrowing);
        stockBorrowingTransRepository.save(poBorrowTransRecord);
        return response.withData(poBorrowTransRecord).getData();
    }
    public Object updatePoTransExport(ReceiptExportUpdateRequest request, Long id) {
        Response<PoTrans> response = new Response<>();
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
        return response.withData(poTrans).getData();
    }

    public Object updateStockAdjustmentTransExport(ReceiptExportUpdateRequest request, Long id) {
        Response<StockAdjustmentTrans> response = new Response<>();
        StockAdjustmentTrans stockAdjustmentTrans  = stockAdjustmentTransRepository.findById(id).get();
        if (stockAdjustmentTrans != null){
            stockAdjustmentTrans.setNote(request.getNote());
            stockAdjustmentTransRepository.save(stockAdjustmentTrans);
            return response.withData(stockAdjustmentTrans).getData();
        }
        return null;
    }
    public Object updateStockBorrowingTransExport(ReceiptExportUpdateRequest request, Long id) {
        Response<StockBorrowingTrans> response = new Response<>();
        StockBorrowingTrans stockBorrowingTrans  = stockBorrowingTransRepository.findById(id).get();
        if (stockBorrowingTrans != null){
            stockBorrowingTrans.setNote(request.getNote());
            stockBorrowingTransRepository.save(stockBorrowingTrans);
            return response.withData(stockBorrowingTrans).getData();
        }
        return null;
    }
    public Response<String> removePoTransExport(Long id) {
        Response<String> response = new Response<>();
        PoTrans poTrans = repository.findById(id).get();

            if(formatDate(poTrans.getTransDate()).equals(formatDate(date))){
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
        return response.withData(ResponseMessage.DELETE_FAILED.toString());
    }
    public Response<String> removeStockBorrowingTransExport(Long id) {
        Response<String> response = new Response<>();
        StockBorrowingTrans stockBorrowingTrans = stockBorrowingTransRepository.getStockBorrowingTransByIdAndDeletedAtIsNull(id);
        if(formatDate(stockBorrowingTrans.getTransDate()).equals(formatDate(date))){
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
        return response.withData(ResponseMessage.DELETE_FAILED.toString());
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public  String createPoTransExportCode(Long idShop) {
        int reciNum = repository.getQuantityPoTransExport();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXSP.");
        reciCode.append(shopClient.getById(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(CreateCodeUtils.formatReceINumber(reciNum));
        return reciCode.toString();
    }
    public  String createStockAdjustmentExportCode(Long idShop) {
        int reciNum = stockAdjustmentTransRepository.getQuantityStockAdjustTransExport();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXST.");
        reciCode.append(shopClient.getById(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(CreateCodeUtils.formatReceINumber(reciNum));
        return reciCode.toString();
    }
    public  String createStockAdjustmentExportRedInvoice(Long idShop) {
        int reciNum = stockAdjustmentTransRepository.getQuantityStockAdjustTransExport();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("SAL.");
        reciCode.append(shopClient.getById(idShop).getData().getShopCode());
        reciCode.append(yy);
        reciCode.append(mm.toString());
        reciCode.append(dd.toString());
        reciCode.append(CreateCodeUtils.formatReceINumber(reciNum));
        return reciCode.toString();
    }
    public  String createStockBorrowTransCode(Long idShop) {
        int reciNum = stockBorrowingTransRepository.getQuantityStockBorrowingTransExport();
        String reciCode = "EXSB." +
                shopClient.getById(idShop).getData().getShopCode() +
                "." +
                yy +
                "." +
                CreateCodeUtils.formatReceINumber(reciNum);
        return reciCode;
    }
    public  String createStockBorrowTransExportRedInvoice(Long idShop) {
        int reciNum = stockAdjustmentTransRepository.getQuantityStockAdjustTransExport();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXP_");
        reciCode.append(shopClient.getById(idShop).getData().getShopCode());
        reciCode.append("_");
        reciCode.append(yy);
        reciCode.append(mm.toString());
        reciCode.append(dd.toString());
        reciCode.append("_");
        reciCode.append(CreateCodeUtils.formatReceINumberVer2(reciNum));
        return reciCode.toString();
    }
    public  String createPoTransCode(Long idShop) {
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
}
