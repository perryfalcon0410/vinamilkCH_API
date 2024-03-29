package vn.viettel.sale.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;

import oracle.security.crypto.cert.ValidationException;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.Price;
import vn.viettel.sale.entities.StockCounting;
import vn.viettel.sale.entities.StockCountingDetail;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.sale.excel.StockCountingFilledExcel;
import vn.viettel.sale.repository.ProductInfoRepository;
import vn.viettel.sale.repository.ProductPriceRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.StockCountingDetailRepository;
import vn.viettel.sale.repository.StockCountingRepository;
import vn.viettel.sale.repository.StockTotalRepository;
import vn.viettel.sale.repository.WareHouseTypeRepository;
import vn.viettel.sale.service.InventoryService;
import vn.viettel.sale.service.ReceiptImportService;
import vn.viettel.sale.service.dto.InventoryImportInfo;
import vn.viettel.sale.service.dto.StockCountingDTO;
import vn.viettel.sale.service.dto.StockCountingDetailDTO;
import vn.viettel.sale.service.dto.StockCountingExcel;
import vn.viettel.sale.service.dto.StockCountingExcelDTO;
import vn.viettel.sale.service.dto.StockCountingImportDTO;
import vn.viettel.sale.service.dto.StockCountingUpdateDTO;
import vn.viettel.sale.service.dto.TotalStockCounting;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.util.CreateCodeUtils;

@Service
public class InventoryServiceImpl extends BaseServiceImpl<StockCounting, StockCountingRepository> implements InventoryService {

    @Autowired
    StockCountingDetailRepository countingDetailRepository;

    @Autowired
    StockTotalRepository stockTotalRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductInfoRepository productInfoRepository;

    @Autowired
    ProductPriceRepository priceRepository;

    @Autowired
    UserClient userClient;

    @Autowired
    ReceiptImportService receiptImportService;

    @Autowired
    CustomerTypeClient customerTypeClient;

    @Autowired
    WareHouseTypeRepository wareHouseTypeRepository;

    @Autowired
    CustomerClient customerClient;

    @Autowired
    ShopClient shopClient;

    @Override
    public Page<StockCountingDTO> index(String stockCountingCode, Long warehouseTypeId, LocalDateTime fromDate, LocalDateTime toDate, Long shopId, Pageable pageable) {
        if(stockCountingCode != null) stockCountingCode.trim().toUpperCase();
        LocalDateTime tsFromDate = DateUtils.convertFromDate(fromDate);
        LocalDateTime tsToDate = DateUtils.convertToDate(toDate);
        Page<StockCountingDTO> stockCountings = repository.findStockCounting(stockCountingCode, warehouseTypeId, tsFromDate, tsToDate, shopId, pageable);

        return stockCountings;
    }

    @Override
    public Object getAll(Long shopId, String searchKeywords, Long wareHouseTypeId) {
        if (searchKeywords != null) searchKeywords = searchKeywords.trim().toUpperCase();
        List<StockCountingDetailDTO> countingDetails = stockTotalRepository.getStockCountingDetail(shopId, wareHouseTypeId, searchKeywords);
        if (countingDetails == null || countingDetails.isEmpty()) return new ArrayList<>();

        List<Long> customerTypeIds = Arrays.asList(-1L);
        List<CustomerTypeDTO> customerTypes = customerTypeClient.getCusTypeByWarehouse(wareHouseTypeId);
        if(!customerTypes.isEmpty()) customerTypeIds = customerTypes.stream().map(item -> item.getId()).distinct().collect(Collectors.toList());

        List<Price> prices = priceRepository.findProductPriceWithTypes(countingDetails.stream().map(item -> item.getProductId())
                .collect(Collectors.toList()), customerTypeIds, DateUtils.convertToDate(LocalDateTime.now()));

        Map<Long, Double> priceMaps = new HashMap<>();
        if (prices != null) {
            for (Price price : prices) {
                if (!priceMaps.containsKey(price.getProductId())) priceMaps.put(price.getProductId(), price.getPrice());
            }
        }

        TotalStockCounting totalStockCounting = new TotalStockCounting();
        totalStockCounting.setStockTotal(0);
        totalStockCounting.setInventoryTotal(0);
        totalStockCounting.setTotalAmount(0.0);
        totalStockCounting.setTotalPacket(0);
        totalStockCounting.setTotalUnit(0);
        totalStockCounting.setCountingCode(null);
        totalStockCounting.setCountingDate(LocalDateTime.now().toString());
        totalStockCounting.setWarehouseType(wareHouseTypeId);
        List<StockCountingDetailDTO> rs = new ArrayList<>();
        for (StockCountingDetailDTO stockCounting : countingDetails) {
            stockCounting.setWarehouseTypeId(wareHouseTypeId);
            stockCounting.setShopId(shopId);
           stockCounting.setPrice(priceMaps.get(stockCounting.getProductId()));
            if (stockCounting.getPrice() != null) {
                stockCounting.setTotalAmount(stockCounting.getStockQuantity() * stockCounting.getPrice());
                stockCounting.setPacketQuantity(0);
                stockCounting.setUnitQuantity(0);
                stockCounting.setInventoryQuantity(0);
                stockCounting.setChangeQuantity((-1) * stockCounting.getStockQuantity());
                totalStockCounting.setStockTotal(totalStockCounting.getStockTotal() + stockCounting.getStockQuantity());
                totalStockCounting.setChangeQuantity((-1) * totalStockCounting.getStockTotal());
                totalStockCounting.setTotalAmount(totalStockCounting.getTotalAmount() + (stockCounting.getStockQuantity() * stockCounting.getPrice()));
                rs.add(stockCounting);
            }

        }
        CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting> response =
                new CoverResponse(rs, totalStockCounting);
        return response;
    }

    @Override
    public CoverResponse<List<StockCountingExcelDTO>, TotalStockCounting> getByStockCountingId(Long id, Long shopId) {
        StockCounting stockCounting = repository.getByIdAndShopId(id, shopId);
        if (stockCounting == null) throw new ValidateException(ResponseMessage.EXCHANGE_TRANS_DETAIL_NOT_FOUND);
        TotalStockCounting totalStockCounting = new TotalStockCounting();
        totalStockCounting.setStockTotal(0);
        totalStockCounting.setInventoryTotal(0);
        totalStockCounting.setTotalAmount(0.0);
        totalStockCounting.setTotalPacket(0);
        totalStockCounting.setTotalUnit(0);
        totalStockCounting.setCountingCode(stockCounting.getStockCountingCode());
        totalStockCounting.setCountingDate(stockCounting.getCountingDate().toString());
        totalStockCounting.setWarehouseType(stockCounting.getWareHouseTypeId());

        List<StockCountingExcelDTO> result = countingDetailRepository.getStockCountingDetail(id);

        if(DateUtils.isToday(stockCounting.getCountingDate().toLocalDate())) {
            List<StockCountingDetailDTO> countingDetails = stockTotalRepository.getStockCountingDetail(shopId, stockCounting.getWareHouseTypeId(), null);
            if (countingDetails == null || countingDetails.isEmpty()) return null;
            List<StockCountingExcelDTO> dtos = new ArrayList<>();
            List<StockCountingDetailDTO> newProducts = new ArrayList<>();
            Map<Long, StockCountingExcelDTO> resultMap = result.stream().collect(Collectors.toMap(StockCountingExcelDTO::getProductId, Function.identity()));

            for (StockCountingDetailDTO st : countingDetails) {
                 if(resultMap.containsKey(st.getProductId())) {
                     StockCountingExcelDTO countingExcel = resultMap.get(st.getProductId());
                        StockCountingExcelDTO dto = modelMapper.map(countingExcel, StockCountingExcelDTO.class);
                        if (dto.getInventoryQuantity() == null) dto.setInventoryQuantity(0);
                        if (st.getStockQuantity() == null){
                            dto.setStockQuantity(0);
                        }else{
                            dto.setStockQuantity(st.getStockQuantity());
                        }
                        if(dto.getPacketQuantity() == null) dto.setPacketQuantity(0);
                        if(dto.getUnitQuantity() == null) dto.setUnitQuantity(0);

                        dto.setTotalAmount(dto.getPrice() == null ? 0D : dto.getPrice() * dto.getStockQuantity());
                        dto.setInventoryQuantity(dto.getPacketQuantity()*dto.getConvfact() + dto.getUnitQuantity());
                        dto.setChangeQuantity(dto.getInventoryQuantity() - dto.getStockQuantity());

                        totalStockCounting.setStockTotal(totalStockCounting.getStockTotal() + dto.getStockQuantity());
                        totalStockCounting.setInventoryTotal(totalStockCounting.getInventoryTotal() + dto.getInventoryQuantity());
                        totalStockCounting.setChangeQuantity(totalStockCounting.getInventoryTotal() - totalStockCounting.getStockTotal());
                        totalStockCounting.setTotalAmount(totalStockCounting.getTotalAmount() + (dto.getStockQuantity() * (dto.getPrice() == null ? 0D : dto.getPrice())));
                        totalStockCounting.setTotalPacket((totalStockCounting.getTotalPacket() + dto.getPacketQuantity()));
                        totalStockCounting.setTotalUnit((totalStockCounting.getTotalUnit() +dto.getUnitQuantity()));

                        if(dto.getInventoryQuantity() == 0) dto.setInventoryQuantity(null);
                        if(dto.getPacketQuantity() == 0) dto.setPacketQuantity(null);
                        if(dto.getUnitQuantity() == 0) dto.setUnitQuantity(null);
                        dtos.add(dto);

                    } else{
                        newProducts.add(st);
                    }
            }

            if(!newProducts.isEmpty()) {
                List<Long> customerTypeIds = Arrays.asList(-1L);
                List<CustomerTypeDTO> customerTypes = customerTypeClient.getCusTypeByWarehouse(stockCounting.getWareHouseTypeId());
                if(!customerTypes.isEmpty()) customerTypeIds = customerTypes.stream().map(item -> item.getId()).distinct().collect(Collectors.toList());

                List<Price> prices = priceRepository.findProductPriceWithTypes(newProducts.stream().map(item -> item.getProductId()).distinct()
                        .collect(Collectors.toList()), customerTypeIds, DateUtils.convertToDate(LocalDateTime.now()));

                Map<Long, Double> priceMaps = new HashMap<>();
                if (prices != null) {
                    for (Price price : prices) {
                        if (!priceMaps.containsKey(price.getProductId())) priceMaps.put(price.getProductId(), price.getPrice());
                    }
                }

                for (StockCountingDetailDTO detail : newProducts) {
                    if(priceMaps.containsKey(detail.getProductId())) {
                        StockCountingExcelDTO dto = new StockCountingExcelDTO();
                        dto.setProductId(detail.getProductId());
                        dto.setProductCategory(detail.getProductCategory());
                        dto.setProductCategoryCode(detail.getProductCategoryCode());
                        dto.setProductGroup(detail.getProductGroup());
                        dto.setProductCode(detail.getProductCode());
                        dto.setProductName(detail.getProductName());
                        dto.setStockQuantity(detail.getStockQuantity());
                        dto.setPrice(priceMaps.get(detail.getProductId()));
                        dto.setConvfact(detail.getConvfact());
                        dto.setPacketUnit(detail.getPacketUnit());
                        dto.setUnit(detail.getUnit());

                        dto.setTotalAmount(detail.getStockQuantity() * dto.getPrice());
                        dto.setPacketQuantity(0);
                        dto.setUnitQuantity(0);
                        dto.setInventoryQuantity(0);
                        dto.setChangeQuantity((-1) * dto.getStockQuantity());

                        totalStockCounting.setStockTotal(totalStockCounting.getStockTotal() + dto.getStockQuantity());
                        totalStockCounting.setInventoryTotal(totalStockCounting.getInventoryTotal() + dto.getInventoryQuantity());
                        totalStockCounting.setChangeQuantity(totalStockCounting.getInventoryTotal() - totalStockCounting.getStockTotal());
                        totalStockCounting.setTotalAmount(totalStockCounting.getTotalAmount() + (dto.getStockQuantity() * (dto.getPrice() == null ? 0D : dto.getPrice())));
                        totalStockCounting.setTotalPacket((totalStockCounting.getTotalPacket() + dto.getPacketQuantity()));
                        totalStockCounting.setTotalUnit((totalStockCounting.getTotalUnit() +dto.getUnitQuantity()));

                        if(dto.getInventoryQuantity() == 0) dto.setInventoryQuantity(null);
                        if(dto.getPacketQuantity() == 0) dto.setPacketQuantity(null);
                        if(dto.getUnitQuantity() == 0) dto.setUnitQuantity(null);

                        dtos.add(dto);

                    }
                }
            }

            Collections.sort(dtos, Comparator.comparing(StockCountingExcelDTO::getProductCategoryCode).thenComparing(StockCountingExcelDTO::getProductCode));

            CoverResponse<List<StockCountingExcelDTO>, TotalStockCounting> response = new CoverResponse(dtos, totalStockCounting);
            return response;
        }else {

            List<StockCountingExcelDTO> dtos = new ArrayList<>();
            for (StockCountingExcelDTO countingExcel : result) {
                StockCountingExcelDTO dto = modelMapper.map(countingExcel, StockCountingExcelDTO.class);

                if(dto.getInventoryQuantity() == null) dto.setInventoryQuantity(0);
                if(dto.getStockQuantity() == null) dto.setStockQuantity(0);
                if(dto.getPacketQuantity() == null) dto.setPacketQuantity(0);
                if(dto.getUnitQuantity() == null) dto.setUnitQuantity(0);

                dto.setTotalAmount(dto.getPrice() == null ? 0D : dto.getPrice() * dto.getStockQuantity());
                dto.setInventoryQuantity(dto.getPacketQuantity()*dto.getConvfact() + dto.getUnitQuantity());
                dto.setChangeQuantity(dto.getInventoryQuantity() - dto.getStockQuantity());

                totalStockCounting.setStockTotal(totalStockCounting.getStockTotal() + dto.getStockQuantity());
                totalStockCounting.setInventoryTotal(totalStockCounting.getInventoryTotal() + dto.getInventoryQuantity());
                totalStockCounting.setChangeQuantity(totalStockCounting.getInventoryTotal() - totalStockCounting.getStockTotal());
                totalStockCounting.setTotalAmount(totalStockCounting.getTotalAmount() + (dto.getStockQuantity() * (dto.getPrice() == null ? 0D : dto.getPrice())));
                totalStockCounting.setTotalPacket((totalStockCounting.getTotalPacket() + dto.getPacketQuantity()));
                totalStockCounting.setTotalUnit((totalStockCounting.getTotalUnit() +dto.getUnitQuantity()));

                if(dto.getInventoryQuantity() == 0) dto.setInventoryQuantity(null);
                if(dto.getPacketQuantity() == 0) dto.setPacketQuantity(null);
                if(dto.getUnitQuantity() == 0) dto.setUnitQuantity(null);

                dtos.add(dto);
            }
            CoverResponse<List<StockCountingExcelDTO>, TotalStockCounting> response = new CoverResponse(dtos, totalStockCounting);
            return response;
        }
    }

    @Override
    public CoverResponse<StockCountingImportDTO, InventoryImportInfo> importExcel(Long shopId, MultipartFile file, Pageable pageable, String searchKeywords, Long wareHouseTypeId) throws IOException {
        List<StockCountingExcel> stockCountingExcels = readDataExcel(file);
        if (stockCountingExcels == null)
            throw new ValidationException(ResponseMessage.THE_EXCEL_FILE_IS_NOT_IN_THE_CORRECT_FORMAT.statusCodeValue());
        if (stockCountingExcels.size() > 5000)
            throw new ValidationException(ResponseMessage.INVALID_STRING_LENGTH.statusCodeValue());
        List<StockCountingExcel> importFails = new ArrayList<>();
        CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting> data =
                (CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting>) getAll(shopId, searchKeywords, wareHouseTypeId);
        List<StockCountingDetailDTO> stockCountingDetails = data.getResponse();
        List<String> productCodes = stockCountingDetails.stream().map(e -> e.getProductCode()).distinct().collect(Collectors.toList());
        if (stockCountingDetails.isEmpty())
            throw new ValidateException(ResponseMessage.EMPTY_LIST);
        int importSuccessNumber = 0;
        for (StockCountingExcel e : stockCountingExcels) {
            for (StockCountingDetailDTO countingDetail : stockCountingDetails) {
                if (e.getUnitQuantity() == null || e.getUnitQuantity()=="") e.setUnitQuantity("0");
                if (e.getPacketQuantity() == null || e.getPacketQuantity()=="") e.setPacketQuantity("0");
                if (e.getProductCode() == null)
                    throw new ValidationException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS.statusCodeValue());
                if (e.getProductCode().length() > 4000 || e.getPacketQuantity().toString().length() > 7 || e.getUnitQuantity().toString().length() > 7)
                    throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
                if (!productCodes.contains(e.getProductCode().toUpperCase()) && !importFails.contains(e)) {
                    e.setError("Sản phẩm không có trong kho");
                    importFails.add(e);
                    break;
                }
                if (countingDetail.getProductCode().equals(e.getProductCode().toUpperCase())) {
                    if (!checkDataType(e) && !importFails.contains(e)) {
                        e.setError("Số nhập vào phải là số nguyên dương");
                        importFails.add(e);
                        break;
                    } else {
                        /*if (countingDetail.getProductCode().equals(e.getProductCode().toUpperCase()) ) {*/
                        int inventoryQuantity = (int) (Integer.valueOf(e.getPacketQuantity()) * countingDetail.getConvfact() + Integer.valueOf(e.getUnitQuantity()));
                        countingDetail.setPacketQuantity(Integer.valueOf(e.getPacketQuantity()));
                        countingDetail.setUnitQuantity(Integer.valueOf(e.getUnitQuantity()));
                        countingDetail.setInventoryQuantity(inventoryQuantity);
                        countingDetail.setChangeQuantity(inventoryQuantity - countingDetail.getStockQuantity());
                        importSuccessNumber++;
                        break;
                    }
                }
            }
        }
        return new CoverResponse<>(
                new StockCountingImportDTO(stockCountingDetails, importFails), new InventoryImportInfo(importSuccessNumber, importFails.size()));
    }

    public boolean checkDataType(StockCountingExcel stockCountingExcel) {
        if (stockCountingExcel.getUnitQuantity().contains("-") || stockCountingExcel.getPacketQuantity().contains("-"))
            return false;
        Double c,d;
        try{
             c = Double.valueOf(stockCountingExcel.getUnitQuantity());

        }catch (NumberFormatException e){
             c = -1D;
        }
        try{
             d = Double.valueOf(stockCountingExcel.getPacketQuantity());
        }catch (NumberFormatException e){
             d = -1D;
        }

        if( c == -1|| d==-1)
            return false;
        String a = String.valueOf(c).split("\\.")[1];
        String b = String.valueOf(d).split("\\.")[1];
        if (!a.equals("0") || !b.equals("0"))
            return false;
        return true;
    }

    @Override
    public ResponseMessage updateStockCounting(Long stockCountingId, Long shopId, String userAccount, List<StockCountingUpdateDTO> details) {
        StockCounting stockCounting = repository.getByIdAndShopId(stockCountingId, shopId);

        if (stockCounting == null)
            throw new ValidateException(ResponseMessage.STOCK_COUNTING_NOT_FOUND);
        if (stockCounting.getCountingDate().isBefore(DateUtils.convertFromDate(LocalDateTime.now())))
            throw new ValidateException(ResponseMessage.INVENTORY_OVER_DATE);

        List<StockCountingDetail> stockCountingDetails = countingDetailRepository.findByStockCountingId(stockCountingId);
        if (stockCountingDetails.isEmpty())
            throw new ValidateException(ResponseMessage.NO_PRODUCT_IN_STOCK_COUNTING);
        List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, stockCounting.getWareHouseTypeId());

        if(stockTotals.size()==0) throw new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS_IN_WAREHOUSE);

       Map<Long, Integer> stockTotalMaps = new HashMap<>();
        for (StockTotal stock: stockTotals) {
            if(!stockTotalMaps.containsKey(stock.getProductId())) stockTotalMaps.put(stock.getProductId(), stock.getQuantity());
        }

        Map<Long, StockCountingDetail>  stockCountingDetailMap = stockCountingDetails.stream().collect(Collectors.toMap(StockCountingDetail::getProductId, Function.identity()));

        List<StockCountingUpdateDTO> newProducts = new ArrayList<>();

        for (StockCountingUpdateDTO update: details) {
            if(stockCountingDetailMap.containsKey(update.getProductId())) {
                StockCountingDetail stockCountingDetail = stockCountingDetailMap.get(update.getProductId());
                stockCountingDetail.setPackageQuantity(update.getPacketQuantity());
                stockCountingDetail.setQuantity(update.getUnitQuantity());
                stockCountingDetail.setStockQuantity(stockTotalMaps.get(stockCountingDetail.getProductId()));
                countingDetailRepository.save(stockCountingDetail);
            }else {
                newProducts.add(update);
            }
        }

        if(!newProducts.isEmpty()) {
            List<Long> customerTypeIds = Arrays.asList(-1L);
            List<CustomerTypeDTO> customerTypes = customerTypeClient.getCusTypeByWarehouse(stockCounting.getWareHouseTypeId());
            if(!customerTypes.isEmpty()) customerTypeIds = customerTypes.stream().map(item -> item.getId()).distinct().collect(Collectors.toList());

            List<Price> prices = priceRepository.findProductPriceWithTypes(newProducts.stream().map(item -> item.getProductId()).distinct()
                    .collect(Collectors.toList()), customerTypeIds, DateUtils.convertToDate(LocalDateTime.now()));

            Map<Long, Double> priceMaps = new HashMap<>();
            if (prices != null) {
                for (Price price : prices) {
                    if (!priceMaps.containsKey(price.getProductId())) priceMaps.put(price.getProductId(), price.getPrice());
                }
            }

            for(StockCountingUpdateDTO product: newProducts) {
                if(priceMaps.containsKey(product.getProductId())) {
                    StockCountingDetail stockCountingDetail = new StockCountingDetail();
                    stockCountingDetail.setStockCountingId(stockCounting.getId());
                    stockCountingDetail.setStockQuantity(stockTotalMaps.get(product.getProductId()));
                    stockCountingDetail.setPackageQuantity(product.getPacketQuantity());
                    stockCountingDetail.setQuantity(product.getUnitQuantity());
                    //  stockCountingDetail.setQuantity(detail.getInventoryQuantity());
                    stockCountingDetail.setPrice(priceMaps.get(product.getProductId()));
                    stockCountingDetail.setShopId(shopId);
                    stockCountingDetail.setProductId(product.getProductId());
                    stockCountingDetail.setCountingDate(LocalDateTime.now());

                    countingDetailRepository.save(stockCountingDetail);
                }
            }
        }

        return ResponseMessage.UPDATE_SUCCESSFUL;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long createStockCounting(List<StockCountingDetailDTO> stockCountingDetails, Long userId, Long shopId, Long wareHouseTypeId, Boolean override) {
        if (stockCountingDetails.isEmpty())
            throw new ValidateException(ResponseMessage.EMPTY_LIST);
        List<StockCounting> countingNumberInDay = repository.findByWareHouseTypeId(wareHouseTypeId,shopId,
                DateUtils.convertFromDate(LocalDateTime.now()), DateUtils.convertToDate(LocalDateTime.now()));
        StockCounting stockCounting = new StockCounting();

        if (countingNumberInDay.size() > 0) {
            if (override == null || override == false)
                throw new ValidateException(ResponseMessage.CREATE_CANCEL);
            else {
                countingDetailRepository.deleteAll(countingDetailRepository.findByStockCountingId(countingNumberInDay.get(0).getId()));
                stockCounting = countingNumberInDay.get(0);
            }
        }else{
            stockCounting.setStockCountingCode(createStockCountingCode(shopId));
        }

        stockCounting.setCountingDate(LocalDateTime.now());
        stockCounting.setShopId(shopId);
        stockCounting.setWareHouseTypeId(wareHouseTypeId);

        stockCounting = repository.save(stockCounting);

        for (StockCountingDetailDTO detail : stockCountingDetails) {
            StockCountingDetail stockCountingDetail = new StockCountingDetail();
            stockCountingDetail.setStockCountingId(stockCounting.getId());
            stockCountingDetail.setStockQuantity(detail.getStockQuantity());
            stockCountingDetail.setPackageQuantity(detail.getPacketQuantity());
            stockCountingDetail.setQuantity(detail.getUnitQuantity());
          //  stockCountingDetail.setQuantity(detail.getInventoryQuantity());
            stockCountingDetail.setPrice(detail.getPrice());
            stockCountingDetail.setShopId(shopId);
            stockCountingDetail.setProductId(detail.getProductId());
            stockCountingDetail.setCountingDate(LocalDateTime.now());

            countingDetailRepository.save(stockCountingDetail);
        }
        return stockCounting.getId();
    }

    @Override
    public Boolean checkInventoryInDay(Long wareHouseTypeId, Long shopId) {
        List<StockCounting> countingNumberInDay = repository.findByWareHouseTypeId(wareHouseTypeId, shopId,
                DateUtils.convertFromDate(LocalDateTime.now()), DateUtils.convertToDate(LocalDateTime.now()));
        if (countingNumberInDay.size() > 0)
            return false;
        return true;
    }

    @Override
    public StockCounting getStockCountingById(Long id) {
        StockCounting stockCounting = repository.findById(id).get();
        return stockCounting;
    }

    @Override
    public ByteArrayInputStream exportExcel(Long id, Long shopId) throws IOException{
        List<StockCountingExcelDTO> export = this.getDataExportExcel(id).getResponse();
        ShopDTO shop = shopClient.getByIdV1(shopId).getData();
        LocalDateTime countingDate = this.getStockCountingById(id).getCountingDate();
        StockCountingFilledExcel stockCountingFilledExcel =
                new StockCountingFilledExcel(export, shop, shop.getParentShop(), countingDate);
        return stockCountingFilledExcel.export();
    }

    public CoverResponse<List<StockCountingExcelDTO>, TotalStockCounting> getDataExportExcel(Long id) {
        StockCounting stockCounting = repository.findById(id).get();
        if (stockCounting == null) throw new ValidateException(ResponseMessage.EXCHANGE_TRANS_DETAIL_NOT_FOUND);
        List<StockCountingExcelDTO> result = countingDetailRepository.getStockCountingExportExcel(id);
        TotalStockCounting totalStockCounting = new TotalStockCounting();
        totalStockCounting.setStockTotal(0);
        totalStockCounting.setInventoryTotal(0);
        totalStockCounting.setTotalAmount(0.0);
        totalStockCounting.setTotalPacket(0);
        totalStockCounting.setTotalUnit(0);
        totalStockCounting.setCountingCode(stockCounting.getStockCountingCode());
        totalStockCounting.setCountingDate(stockCounting.getCountingDate().toString());
        totalStockCounting.setWarehouseType(stockCounting.getWareHouseTypeId());
        List<StockCountingExcelDTO> dtos = new ArrayList<>();
        for (StockCountingExcelDTO countingExcel : result) {
            StockCountingExcelDTO dto = modelMapper.map(countingExcel, StockCountingExcelDTO.class);
            if (dto.getInventoryQuantity() == null) dto.setInventoryQuantity(0);
            if (dto.getStockQuantity() == null) dto.setStockQuantity(0);
            dto.setTotalAmount(dto.getPrice() == null ? 0D : dto.getPrice() * dto.getStockQuantity());

            if(dto.getInventoryQuantity() == null) dto.setInventoryQuantity(0);
            if(dto.getStockQuantity() == null) dto.setStockQuantity(0);
            if(dto.getPacketQuantity() == null) dto.setPacketQuantity(0);
            if(dto.getUnitQuantity() == null) dto.setUnitQuantity(0);

            dto.setInventoryQuantity(dto.getPacketQuantity()*dto.getConvfact() + dto.getUnitQuantity());
            dto.setChangeQuantity(dto.getInventoryQuantity() - dto.getStockQuantity());

            totalStockCounting.setStockTotal(totalStockCounting.getStockTotal() + dto.getStockQuantity());
            totalStockCounting.setInventoryTotal(totalStockCounting.getInventoryTotal() + dto.getInventoryQuantity());
            totalStockCounting.setChangeQuantity(totalStockCounting.getInventoryTotal() - totalStockCounting.getStockTotal());
            totalStockCounting.setTotalAmount(totalStockCounting.getTotalAmount() + (dto.getStockQuantity() * (dto.getPrice() == null ? 0D : dto.getPrice())));
            totalStockCounting.setTotalPacket((totalStockCounting.getTotalPacket() + dto.getPacketQuantity()));
            totalStockCounting.setTotalUnit((totalStockCounting.getTotalUnit() +dto.getUnitQuantity()));
            dtos.add(dto);
        }

        CoverResponse<List<StockCountingExcelDTO>, TotalStockCounting> response = new CoverResponse(dtos, totalStockCounting);
        return response;
    }

    public List<StockCountingExcel> readDataExcel(MultipartFile file) throws IOException {
        String path = file.getOriginalFilename();

        InputStream stream = file.getInputStream();
        PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().headerStart(0).build();

        List<StockCountingExcel> response = null;
        if (path.split("\\.")[1].equals("xlsx"))
            response =  Poiji.fromExcel(stream, PoijiExcelType.XLSX, StockCountingExcel.class, options);
        if (path.split("\\.")[1].equals("xls"))
            response = Poiji.fromExcel(stream, PoijiExcelType.XLS, StockCountingExcel.class, options);

        stream.close();
        return response;
    }

    public String createStockCountingCode(Long shopId) {
        LocalDate myLocal = LocalDate.now();
        StringBuilder code = new StringBuilder("KK");
        code.append(myLocal.get(IsoFields.QUARTER_OF_YEAR));
        code.append(".");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String strDate = formatter.format(date);
        code.append(strDate);
        code.append(".");
        Pageable pageable = PageRequest.of(0,2);
        Page<StockCounting> lst = repository.getLastStockCounting(shopId,
                DateUtils.convertFromDate(LocalDateTime.now()), pageable);
        int STT = 0;
        if(!lst.getContent().isEmpty()) {
            String str = lst.getContent().get(0).getStockCountingCode();
            String numberString = str.substring(str.length() - 5);
            STT = Integer.valueOf(numberString);
        }

        code.append(CreateCodeUtils.formatReceINumber(STT));

        return code.toString();
    }
}
