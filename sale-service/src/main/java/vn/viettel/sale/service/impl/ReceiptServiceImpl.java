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
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.db.entity.sale.SaleOrderDetail;
import vn.viettel.core.db.entity.stock.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.messaging.ReceiptCreateDetailRequest;
import vn.viettel.sale.messaging.ReceiptCreateRequest;
import vn.viettel.sale.messaging.ReceiptFilter;
import vn.viettel.sale.messaging.ReceiptUpdateRequest;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ReceiptService;
import vn.viettel.sale.service.dto.*;
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
public class ReceiptServiceImpl extends BaseServiceImpl<PoTrans, PoTransRepository> implements ReceiptService {
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
    public Response<Page<ReceiptImportListDTO>> find(String redInvoiceNo, Date fromDate, Date toDate, Integer type,Long shopId, Pageable pageable) {
        if(type == null){
            Page<PoTrans> list1 = repository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNull()).and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo).and(ReceiptSpecification.hasFromDateToDate(fromDate,toDate)).and(ReceiptSpecification.hasTypeImport())), pageable);
            Page<StockAdjustmentTrans> list2 = stockAdjustmentTransRepository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNullA()).and(ReceiptSpecification.hasRedInvoiceNoA(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateA(fromDate, toDate)).and(ReceiptSpecification.hasTypeImportA()),pageable);
            Page<StockBorrowingTrans> list3 = stockBorrowingTransRepository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNullB().and(ReceiptSpecification.hasRedInvoiceNoB(redInvoiceNo)).and(ReceiptSpecification.hasFromDateToDateB(fromDate, toDate)).and(ReceiptSpecification.hasTypeImportB())),pageable);
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
            Page<PoTrans> list1 = repository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNull()).
                    and(ReceiptSpecification.hasRedInvoiceNo(redInvoiceNo).and(ReceiptSpecification.hasFromDateToDate(fromDate,toDate)).
                            and(ReceiptSpecification.hasTypeImport())), pageable);
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
            Page<StockAdjustmentTrans> list2 = stockAdjustmentTransRepository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNullA()).and(ReceiptSpecification.hasRedInvoiceNoA(redInvoiceNo)).
                    and(ReceiptSpecification.hasFromDateToDateA(fromDate, toDate)).and(ReceiptSpecification.hasTypeImportA()),pageable);
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
            Page<StockBorrowingTrans> list3 = stockBorrowingTransRepository.findAll(Specification.where(ReceiptSpecification.hasDeletedAtIsNullB().and(ReceiptSpecification.hasRedInvoiceNoB(redInvoiceNo)).
                    and(ReceiptSpecification.hasFromDateToDateB(fromDate, toDate)).and(ReceiptSpecification.hasTypeImportB())),pageable);
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
    public Response<Object> createReceipt(ReceiptCreateRequest request,Long userId,Long shopId) {
        Response<Object> response = new Response<>();
            switch (request.getImportType()){
            case 0:
                try{
                    return new Response<>().withData(createPoTrans(request,userId,shopId));
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
        return null;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Object> updateReceiptImport(ReceiptUpdateRequest request, Long id) {
        Response<Object> response = new Response<>();
        switch (request.getType()){
            case 0:
                try{
                    return new Response<>().withData(updatePoTrans(request,id));
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
            case 1:
                try {
                    return new Response<>().withData(updateStockAdjustmentTrans(request,id));
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
            case 2:
                try {
                    return new Response<>().withData(updateStockBorrowingTrans(request,id));
                }catch (Exception e){
                    return response.withError(ResponseMessage.CREATE_FAILED);
                }
        }
        return null;
    }

    @Override
    public Response<String> removeReceiptImport(ReceiptUpdateRequest request,Long id) {
        Response<String> response = new Response<>();
        switch (request.getType()){

            case 0:
                try{
                    removePoTrans(request,id);
                }catch (Exception e){
                    return response.withError(ResponseMessage.DELETE_FAILED);
                }
                break;
            case 1:
                return response.withError(ResponseMessage.DELETE_FAILED);
            case 2:
                try {
                    removeStockBorrowingTrans(request,id);
                }catch (Exception e){
                    return response.withError(ResponseMessage.DELETE_FAILED);
                }
                break;
        }
        return response.withData(ResponseMessage.SUCCESSFUL.toString());
    }

    @Override
    public Response<Object> getForUpdate(Integer type, Long id) {
        Response<Object> response = new Response<>();
        switch (type){
            case 0:
                try{
                    return new Response<>().withData(getPoTransById(id));
                }catch (Exception e){
                    return response.withError(ResponseMessage.NO_CONTENT);
                }
            case 1:
                try {
                    return new Response<>().withData(getStockAdjustmentById(id));
                }catch (Exception e){
                    return response.withError(ResponseMessage.NO_CONTENT);
                }
            case 2:
                try {
                    return new Response<>().withData(getStockBorrowingById(id));
                }catch (Exception e){
                    return response.withError(ResponseMessage.NO_CONTENT);
                }
        }
        return null;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<List<PoConfirmDTO>> getListPoConfirm() {
        List<PoConfirm> poConfirms = poConfirmRepository.getPoConfirm();
        List<PoConfirmDTO> rs = new ArrayList<>();
        for (PoConfirm pc : poConfirms){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoConfirmDTO dto = modelMapper.map(pc, PoConfirmDTO.class);
            rs.add(dto);
        }
        Response<List<PoConfirmDTO>> response = new Response<>();
        return response.withData(rs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<List<StockAdjustmentDTO>> getListStockAdjustment() {
        List<StockAdjustment> stockAdjustments = stockAdjustmentRepository.getStockAdjustment();
        List<StockAdjustmentDTO> rs = new ArrayList<>();
        for (StockAdjustment sa : stockAdjustments){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentDTO dto = modelMapper.map(sa, StockAdjustmentDTO.class);
            rs.add(dto);
        }
        Response<List<StockAdjustmentDTO>> response = new Response<>();
        return response.withData(rs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<List<StockBorrowingDTO>> getListStockBorrowing() {
        List<StockBorrowing> stockBorrowings = stockBorrowingRepository.getStockBorrowing();
        List<StockBorrowingDTO> rs = new ArrayList<>();
        for (StockBorrowing sb : stockBorrowings){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingDTO dto = modelMapper.map(sb, StockBorrowingDTO.class);
            rs.add(dto);
        }
        Response<List<StockBorrowingDTO>> response = new Response<>();
        return response.withData(rs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<List<PoDetailDTO>> getPoDetailByPoId(Long id,Long shopId) {
        List<PoDetail> poDetails = poDetailRepository.getPoDetailByPoIdAndPriceIsNotNull(id);
        List<PoDetailDTO> rs = new ArrayList<>();
        for (PoDetail pt : poDetails){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoDetailDTO dto = modelMapper.map(pt, PoDetailDTO.class);
            dto.setProductCode(productRepository.findById(pt.getProductId()).get().getProductCode());
            dto.setProductName(productRepository.findById(pt.getProductId()).get().getProductName());
            dto.setShopName(shopClient.getById(shopId).getData().getShopName());
            dto.setShopAddress(shopClient.getById(shopId).getData().getAddress());
            dto.setShopContact("Tel: "+ shopClient.getById(shopId).getData().getPhone()+" Fax: "+shopClient.getById(shopId).getData().getFax());
            dto.setSoNo(poConfirmRepository.findById(pt.getPoId()).get().getSaleOrderNumber());
            dto.setUnit(productRepository.findById(pt.getProductId()).get().getUom1());
            dto.setTotalPrice(pt.getPrice() * pt.getQuantity());
            rs.add(dto);
        }
        Response<List<PoDetailDTO>> response = new Response<>();
        return response.withData(rs);
    }

    @Override
    public Response<List<PoDetailDTO>> getPoDetailByPoIdAndPriceIsNull(Long id,Long shopId) {
        List<PoDetail> poDetails = poDetailRepository.getPoDetailByTrans(id);
        List<PoDetailDTO> rs = new ArrayList<>();
        for (PoDetail pt : poDetails){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoDetailDTO dto = modelMapper.map(pt, PoDetailDTO.class);
            dto.setProductCode(productRepository.findById(pt.getProductId()).get().getProductCode());
            dto.setProductName(productRepository.findById(pt.getProductId()).get().getProductName());
            dto.setShopName(shopClient.getById(shopId).getData().getShopName());
            dto.setShopAddress(shopClient.getById(shopId).getData().getAddress());
            dto.setShopContact("Tel: "+ shopClient.getById(shopId).getData().getPhone()+" Fax: "+shopClient.getById(shopId).getData().getFax());
            dto.setSoNo(poConfirmRepository.findById(pt.getPoId()).get().getSaleOrderNumber());
            dto.setUnit(productRepository.findById(pt.getProductId()).get().getUom1());
            dto.setTotalPrice(pt.getPrice() * pt.getQuantity());
            rs.add(dto);
        }
        Response<List<PoDetailDTO>> response = new Response<>();
        return response.withData(rs);
    }

    @Override
    public Response<List<StockAdjustmentDetailDTO>> getStockAdjustmentDetail(Long id) {
        List<StockAdjustmentDetail> adjustmentDetails = stockAdjustmentDetailRepository.getStockAdjustmentDetailByAdjustmentId(id);
        List<StockAdjustmentDetailDTO> rs = new ArrayList<>();
        for (StockAdjustmentDetail sad : adjustmentDetails){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentDetailDTO dto = modelMapper.map(sad, StockAdjustmentDetailDTO.class);
            dto.setProductCode(productRepository.findById(sad.getProductId()).get().getProductCode());
            dto.setProductCode(productRepository.findById(sad.getProductId()).get().getProductName());
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
        for (StockBorrowingDetail sbd : borrowingDetails){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingDetailDTO dto = modelMapper.map(sbd, StockBorrowingDetailDTO.class);
            dto.setProductCode(productRepository.findById(sbd.getProductId()).get().getProductCode());
            dto.setProductCode(productRepository.findById(sbd.getProductId()).get().getProductName());
            dto.setLicenseNumber(stockBorrowingRepository.findById(sbd.getBorrowingId()).get().getPoBorrowCode());
            dto.setUnit(productRepository.findById(sbd.getProductId()).get().getUom1());
            dto.setTotalPrice(sbd.getPrice() * sbd.getQuantity());
            rs.add(dto);
        }
        Response<List<StockBorrowingDetailDTO>> response = new Response<>();
        return response.withData(rs);
    }
    @Override
    public Response<List<PoTransDetailDTO>> getPoTransDetail(Long id) {

        List<PoTransDetailDTO> rs = new ArrayList<>();
        PoTrans poTrans = repository.findById(id).get();
        if(poTrans.getFromTransId() == null){
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(id);
            for (int i=0;i<poTransDetails.size();i++){
                PoTransDetail ptd = poTransDetails.get(i);
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTransDetailDTO dto = modelMapper.map(ptd, PoTransDetailDTO.class);
                dto.setProductCode(productRepository.findById(ptd.getProductId()).get().getProductCode());
                dto.setProductCode(productRepository.findById(ptd.getProductId()).get().getProductName());
                dto.setUnit(productRepository.findById(ptd.getProductId()).get().getUom1());
                dto.setTotalPrice(ptd.getPrice() * ptd.getQuantity());
                dto.setExport(0);
                rs.add(dto);
            }
            Response<List<PoTransDetailDTO>> response = new Response<>();
            return response.withData(rs);
        }else{
            PoTrans poTransExport = repository.findById(poTrans.getFromTransId()).get();
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(id);
            List<PoTransDetail> poTransDetailsExport = poTransDetailRepository.getPoTransDetailByTransId(poTransExport.getId());
            for (int i=0;i<poTransDetails.size();i++){
                PoTransDetail ptd = poTransDetails.get(i);
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                PoTransDetailDTO dto = modelMapper.map(ptd, PoTransDetailDTO.class);
                dto.setProductCode(productRepository.findById(ptd.getProductId()).get().getProductCode());
                dto.setProductCode(productRepository.findById(ptd.getProductId()).get().getProductName());
                dto.setUnit(productRepository.findById(ptd.getProductId()).get().getUom1());
                dto.setTotalPrice(ptd.getPrice() * ptd.getQuantity());
                dto.setExport(poTransDetailsExport.get(i).getQuantity());
                rs.add(dto);
            }
            Response<List<PoTransDetailDTO>> response = new Response<>();
            return response.withData(rs);
        }
    }

    @Override
    public Response<List<StockAdjustmentTransDetailDTO>> getStockAdjustmentTransDetail(Long id) {
        List<StockAdjustmentTransDetail> adjustmentTransDetails = stockAdjustmentTransDetailRepository.getStockAdjustmentTransDetailsByTransId(id);
        List<StockAdjustmentTransDetailDTO> rs = new ArrayList<>();
        for (StockAdjustmentTransDetail satd :adjustmentTransDetails ){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentTransDetailDTO dto = modelMapper.map(satd, StockAdjustmentTransDetailDTO.class);
            dto.setProductCode(productRepository.findById(satd.getProductId()).get().getProductCode());
            dto.setProductCode(productRepository.findById(satd.getProductId()).get().getProductName());
            dto.setTotalPrice(satd.getPrice() * satd.getQuantity());
            dto.setUnit(productRepository.findById(satd.getProductId()).get().getUom1());
            rs.add(dto);
        }
        Response<List<StockAdjustmentTransDetailDTO>> response = new Response<>();
        return response.withData(rs);
    }

    @Override
    public Response<List<StockBorrowingTransDetailDTO>> getStockBorrowingTransDetail(Long id) {
        List<StockBorrowingTransDetail> borrowingTransDetails = stockBorrowingTransDetailRepository.getStockBorrowingTransDetailByTransId(id);
        List<StockBorrowingTransDetailDTO> rs = new ArrayList<>();
        for (StockBorrowingTransDetail sbtd :borrowingTransDetails ){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingTransDetailDTO dto = modelMapper.map(sbtd, StockBorrowingTransDetailDTO.class);
            dto.setProductCode(productRepository.findById(sbtd.getProductId()).get().getProductCode());
            dto.setProductCode(productRepository.findById(sbtd.getProductId()).get().getProductName());
            dto.setTotalPrice(sbtd.getPrice() * sbtd.getQuantity());
            dto.setUnit(productRepository.findById(sbtd.getProductId()).get().getUom1());
            rs.add(dto);
        }
        Response<List<StockBorrowingTransDetailDTO>> response = new Response<>();
        return response.withData(rs);
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
    public Object createPoTrans(ReceiptCreateRequest request, Long userId,Long shopId) {
        Response<PoTrans> response = new Response<>();
        User user = userClient.getUserById(userId);
        CustomerType customerType = customerTypeClient.getCusTypeIdByShopId(shopId);
        if(request.getPoNumber() != null){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoTrans poRecord = modelMapper.map(request, PoTrans.class);
            poRecord.setTransDate(date);
            poRecord.setTransCode(createPoTransCode(shopId));
            poRecord.setWareHouseTypeId(customerType.getWareHoseTypeId());
            poRecord.setRedInvoiceNo(request.getRedInvoiceNo());
            poRecord.setCreateUser(user.getUserAccount());
            poRecord.setShopId(shopId);
            poRecord.setType(1);
            repository.save(poRecord);
            Integer total = 0;
            for (ReceiptCreateDetailRequest rcdr : request.getLst()) {
                PoTransDetail poTransDetail = modelMapper.map(rcdr, PoTransDetail.class);
                poTransDetail.setTransId(poRecord.getId());
                Product product =productRepository.getProductByProductCode(rcdr.getProductCode());
                poTransDetail.setProductId(product.getId());
                poTransDetail.setPrice(0F);
                poTransDetail.setShopId(shopId);
                total += rcdr.getQuantity();
                poTransDetailRepository.save(poTransDetail);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(product.getId(), customerType.getWareHoseTypeId());
                if (stockTotal == null)
                    response.setFailure(ResponseMessage.NO_CONTENT);
                if (stockTotal.getQuantity() == null) {
                    stockTotal.setQuantity(0);
                }
                stockTotal.setQuantity(stockTotal.getQuantity() + rcdr.getQuantity());
                stockTotalRepository.save(stockTotal);
            }
            poRecord.setTotalQuantity(total);
            poRecord.setNumSku(request.getLst().size());
            poRecord.setCreatedAt(ts);
            repository.save(poRecord);
            return poRecord;
        } else{
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PoTrans poRecord = modelMapper.map(request, PoTrans.class);
            PoConfirm poConfirm = poConfirmRepository.findById(request.getPoId()).get();
            poRecord.setTransDate(date);
            poRecord.setTransCode(createPoTransCode(shopId));
            poRecord.setShopId(shopId);
            poRecord.setWareHouseTypeId(customerType.getWareHoseTypeId());
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
                poTransDetail.setAmount(pod.getQuantity()*pod.getPrice());
                poTransDetailRepository.save(poTransDetail);
                Product product = productRepository.findById(pod.getProductId()).get();
                if (product == null) response.setFailure(ResponseMessage.NO_CONTENT);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(product.getId(), customerType.getWareHoseTypeId());
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


    public StockAdjustmentTrans createAdjustmentTrans(ReceiptCreateRequest request, Long userId,Long shopId) {
        Response<StockAdjustmentTrans> response = new Response<>();
        User user = userClient.getUserById(userId);
        CustomerType customerType = customerTypeClient.getCusTypeIdByShopId(shopId);
        if (request.getImportType() == 1) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockAdjustmentTrans stockAdjustmentRecord = modelMapper.map(request, StockAdjustmentTrans.class);
            StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(request.getPoId()).get();
            stockAdjustmentRecord.setTransDate(date);
            stockAdjustmentRecord.setWareHouseTypeId(customerType.getWareHoseTypeId());
            stockAdjustmentRecord.setTransCode(stockAdjustment.getAdjustmentCode());
            stockAdjustmentRecord.setAdjustmentDate(stockAdjustment.getAdjustmentDate());
            stockAdjustmentRecord.setShopId(shopId);
            stockAdjustmentRecord.setRedInvoiceNo(createRedInvoiceCodeAdjust(shopId));
            stockAdjustmentRecord.setInternalNumber(createInternalCodeAdjust(shopId));
            stockAdjustmentRecord.setCreateUser(user.getUserAccount());
            stockAdjustmentRecord.setType(1);
            stockAdjustmentRecord.setAdjustmentId(request.getPoId());
            stockAdjustmentTransRepository.save(stockAdjustmentRecord);
            SaleOrder order = new SaleOrder();
            order.setType(3);
            order.setOrderNumber(createRedInvoiceCodeAdjust(shopId));
            order.setOrderDate(date);
            saleOrderRepository.save(order);
            List<StockAdjustmentDetail> stockAdjustmentDetails = stockAdjustmentDetailRepository.getStockAdjustmentDetailByAdjustmentId(stockAdjustment.getId());
            Integer totalQuantity = 0;
            Float totalAmount= 0F;
            for (StockAdjustmentDetail sad : stockAdjustmentDetails) {
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                StockAdjustmentTransDetail stockAdjustmentTransDetail = modelMapper.map(sad, StockAdjustmentTransDetail.class);
                stockAdjustmentTransDetail.setTransId(stockAdjustmentRecord.getId());
                totalQuantity += sad.getQuantity();
                totalAmount +=sad.getPrice()*sad.getProductId();
                Product product = productRepository.findById(sad.getProductId()).get();
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(product.getId(), customerType.getWareHoseTypeId());
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

    public StockBorrowingTrans createBorrowingTrans(ReceiptCreateRequest request, Long userId,Long shopId) {
        Response<StockBorrowingTrans> response = new Response<>();
        User user = userClient.getUserById(userId);
        CustomerType customerType = customerTypeClient.getCusTypeIdByShopId(shopId);
        if (request.getImportType() == 2) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            StockBorrowingTrans stockBorrowingTrans = modelMapper.map(request, StockBorrowingTrans.class);
            StockBorrowing stockBorrowing = stockBorrowingRepository.findById(request.getPoId()).get();
            stockBorrowingTrans.setTransDate(date);
            stockBorrowingTrans.setTransCode(createBorrowingTransCode(shopId));
            stockBorrowingTrans.setWareHouseTypeId(customerType.getWareHoseTypeId());
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
                totalAmount += sbd.getPrice()*sbd.getQuantity();
                Product product = productRepository.findById(sbd.getProductId()).get();
                if (product == null) response.setFailure(ResponseMessage.NO_CONTENT);
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(product.getId(), customerType.getWareHoseTypeId());
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
                    Product product = productRepository.getProductByProductCode(rcdr.getProductCode());
                    StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(product.getId(),poTrans.getWareHouseTypeId());
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
    public Response<String> removePoTrans(ReceiptUpdateRequest request,Long id) {
        Response<String> response = new Response<>();
        PoTrans poTrans = repository.getPoTransByIdAndDeletedAtIsNull(id);
        if(formatDate(poTrans.getTransDate()).equals(formatDate(date))){
            List<PoTransDetail> poTransDetails = poTransDetailRepository.getPoTransDetailByTransId(poTrans.getId());
            for (PoTransDetail ptd :poTransDetails ){
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(ptd.getProductId(),poTrans.getWareHouseTypeId());
                stockTotal.setQuantity(stockTotal.getQuantity() - ptd.getQuantity());
                stockTotalRepository.save(stockTotal);
            }
            if (poTrans.getPoId() != null){
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



    public Response<String> removeStockBorrowingTrans(ReceiptUpdateRequest request,Long id) {
        Response<String> response = new Response<>();
        StockBorrowingTrans stockBorrowingTrans = stockBorrowingTransRepository.getStockBorrowingTransByIdAndDeletedAtIsNull(id);
        if(formatDate(stockBorrowingTrans.getTransDate()).equals(formatDate(date))){
            List<StockBorrowingTransDetail> stockBorrowingTransDetails = stockBorrowingTransDetailRepository.getStockBorrowingTransDetailByTransId(stockBorrowingTrans.getId());
            for (StockBorrowingTransDetail sbtd :stockBorrowingTransDetails ){
                StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(sbtd.getProductId(),stockBorrowingTrans.getWareHouseTypeId());
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
    public  String createBorrowingTransCode(Long idShop) {
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
    public  String createRedInvoiceCodeAdjust(Long idShop) {
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
    public  String createInternalCodeAdjust(Long idShop) {
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
    public Response<PoTransDTO> getPoTransById(Long transId) {
        Response<PoTransDTO> response = new Response<>();
        PoTrans poTrans = repository.getPoTransByIdAndDeletedAtIsNull(transId);
        if (!poTrans.getId().equals(transId)) {
            return response.withError(ResponseMessage.PO_TRANS_IS_NOT_EXISTED);
        }
        PoTransDTO poTransDTO = modelMapper.map(poTrans, PoTransDTO.class);

        return response.withData(poTransDTO);
    }


    public Response<StockAdjustmentTransDTO> getStockAdjustmentById(Long transId) {
        Response<StockAdjustmentTransDTO> response = new Response<>();
        StockAdjustmentTrans sat = stockAdjustmentTransRepository.getStockAdjustmentTransByIdAndDeletedAtIsNull(transId);
        if (!sat.getId().equals(transId)) {
            return response.withError(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);
        }
        StockAdjustmentTransDTO stockAdjustmentTransDTO = modelMapper.map(sat, StockAdjustmentTransDTO.class);

        return response.withData(stockAdjustmentTransDTO);
    }

    public Response<StockBorrowingTransDTO> getStockBorrowingById(Long transId) {
        Response<StockBorrowingTransDTO> response = new Response<>();
        StockBorrowingTrans sbt = stockBorrowingTransRepository.getStockBorrowingTransByIdAndDeletedAtIsNull(transId);
        if (!sbt.getId().equals(transId)) {
            return response.withError(ResponseMessage.STOCK_BORROWING_TRANS_IS_NOT_EXISTED);
        }
        StockBorrowingTransDTO stockBorrowingTransDTO = modelMapper.map(sbt, StockBorrowingTransDTO.class);

        return response.withData(stockBorrowingTransDTO);
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
