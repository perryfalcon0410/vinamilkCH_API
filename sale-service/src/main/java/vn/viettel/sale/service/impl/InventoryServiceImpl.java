package vn.viettel.sale.service.impl;

import com.google.common.collect.Lists;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import oracle.security.crypto.cert.ValidationException;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.sale.WareHouseTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.InventoryService;
import vn.viettel.sale.service.ReceiptImportService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.InventorySpecification;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public Page<StockCountingDTO> index(String stockCountingCode, Long warehouseTypeId, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        Page<StockCounting> stockCountings = repository.findAll(Specification
                        .where(InventorySpecification.hasCountingCode(stockCountingCode))
                        .and(InventorySpecification.hasFromDateToDate(fromDate, toDate).and(InventorySpecification.hasWareHouse(warehouseTypeId)))
                , pageable);
        List<Long> ids = stockCountings.stream().map(item -> item.getWareHouseTypeId()).distinct().collect(Collectors.toList());
        List<WareHouseType> wareHouseTypes = wareHouseTypeRepository.findAllById((ids != null && !ids.isEmpty()) ? ids : Arrays.asList(0L));
        return stockCountings.map(e->{
            StockCountingDTO dto =  modelMapper.map(e,StockCountingDTO.class);
            if(wareHouseTypes != null){
                for(WareHouseType wareHouseType : wareHouseTypes){
                    if(wareHouseType.getId().equals(e.getWareHouseTypeId())){
                        dto.setWareHouseTypeName(wareHouseType.getWareHouseTypeName());
                        break;
                    }
                }
            }
            return dto;
        });
    }

    @Override
    public Object getAll(Long shopId, String searchKeywords) {
        Long wareHouseTypeId = customerTypeClient.getCustomerTypeDefaultV1().getData().getWareHouseTypeId();
        if(searchKeywords != null) searchKeywords = searchKeywords.trim().toUpperCase();
        List<StockCountingDetailDTO> countingDetails = stockTotalRepository.getStockCountingDetail(shopId, wareHouseTypeId, searchKeywords);
        if(countingDetails == null || countingDetails.isEmpty()) return new ArrayList<>();
        Long customerTypeId = null;
        CustomerDTO customerDTO = customerClient.getCusDefault(shopId);
        if(customerDTO != null) customerTypeId = customerDTO.getCustomerTypeId();
        List<Price> prices = priceRepository.findProductPrice(countingDetails.stream().map(item -> item.getProductId())
                .collect(Collectors.toList()), customerTypeId, LocalDateTime.now());
        TotalStockCounting totalStockCounting = new TotalStockCounting();
        totalStockCounting.setStockTotal(0);
        totalStockCounting.setInventoryTotal(0);
        totalStockCounting.setTotalAmount(0.0);
        totalStockCounting.setTotalPacket(0);
        totalStockCounting.setTotalUnit(0);
        totalStockCounting.setCountingCode(null);
        totalStockCounting.setCountingDate(LocalDateTime.now().toString());
        totalStockCounting.setWarehouseType(wareHouseTypeId);

        for (StockCountingDetailDTO stockCounting : countingDetails) {
            stockCounting.setWarehouseTypeId(wareHouseTypeId);
            stockCounting.setShopId(shopId);
            if(prices != null){
                for(Price price : prices){
                    if(price.getProductId().equals(stockCounting.getProductId())){
                        stockCounting.setPrice(price.getPrice());
                        break;
                    }
                }
            }
            if(stockCounting.getPrice() == null) throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);
            stockCounting.setTotalAmount(stockCounting.getStockQuantity() * stockCounting.getPrice());
            stockCounting.setPacketQuantity(0);
            stockCounting.setUnitQuantity(0);
            stockCounting.setInventoryQuantity(0);
            stockCounting.setChangeQuantity(0 - stockCounting.getStockQuantity());

            totalStockCounting.setStockTotal(totalStockCounting.getStockTotal() + stockCounting.getStockQuantity());
            totalStockCounting.setChangeQuantity(0 - totalStockCounting.getStockTotal());
            totalStockCounting.setTotalAmount(totalStockCounting.getTotalAmount() + (stockCounting.getStockQuantity() * stockCounting.getPrice()));
        }

        CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting> response =
                    new CoverResponse(countingDetails, totalStockCounting);
        return response;
    }

    @Override
    public CoverResponse<List<StockCountingExcel>, TotalStockCounting> getByStockCountingId(Long id) {
        StockCounting stockCounting = repository.findById(id).get();
        if (stockCounting == null) throw new ValidateException(ResponseMessage.EXCHANGE_TRANS_DETAIL_NOT_FOUND);
        List<StockCountingExcel> result = countingDetailRepository.getStockCountingExcel(id);
        TotalStockCounting totalStockCounting = new TotalStockCounting();
        totalStockCounting.setStockTotal(0);
        totalStockCounting.setInventoryTotal(0);
        totalStockCounting.setTotalAmount(0.0);
        totalStockCounting.setTotalPacket(0);
        totalStockCounting.setTotalUnit(0);
        totalStockCounting.setCountingCode(stockCounting.getStockCountingCode());
        totalStockCounting.setCountingDate(stockCounting.getCountingDate().toString());
        totalStockCounting.setWarehouseType(stockCounting.getWareHouseTypeId());

        for (StockCountingExcel countingExcel : result) {
            if(countingExcel.getInventoryQuantity() == null) countingExcel.setInventoryQuantity(0);
            if(countingExcel.getStockQuantity() == null) countingExcel.setStockQuantity(0);
            countingExcel.setTotalAmount(countingExcel.getPrice() * countingExcel.getStockQuantity());
            countingExcel.setPacketQuantity(countingExcel.getInventoryQuantity() / countingExcel.getConvfact());
            countingExcel.setUnitQuantity(countingExcel.getInventoryQuantity() % countingExcel.getConvfact());
            countingExcel.setChangeQuantity(countingExcel.getStockQuantity() - countingExcel.getInventoryQuantity());

            totalStockCounting.setStockTotal(totalStockCounting.getStockTotal() + countingExcel.getStockQuantity());
            totalStockCounting.setInventoryTotal(totalStockCounting.getInventoryTotal() + countingExcel.getInventoryQuantity());
            totalStockCounting.setChangeQuantity(totalStockCounting.getInventoryTotal() - totalStockCounting.getStockTotal());
            totalStockCounting.setTotalAmount(totalStockCounting.getTotalAmount() + (countingExcel.getStockQuantity() * countingExcel.getPrice()));
            totalStockCounting.setTotalPacket(totalStockCounting.getTotalPacket() + countingExcel.getPacketQuantity());
            totalStockCounting.setTotalUnit(totalStockCounting.getTotalUnit() + countingExcel.getUnitQuantity());
        }
        CoverResponse<List<StockCountingExcel>, TotalStockCounting> response = new CoverResponse(result, totalStockCounting);
        return response;
    }

    @Override
    public CoverResponse<StockCountingImportDTO, InventoryImportInfo> importExcel(Long shopId, MultipartFile file, Pageable pageable, String searchKeywords) throws IOException {
        List<StockCountingExcel> stockCountingExcels = readDataExcel(file);
        if(stockCountingExcels == null ) throw new ValidationException(ResponseMessage.THE_EXCEL_FILE_IS_NOT_IN_THE_CORRECT_FORMAT.statusCodeValue());
        if(stockCountingExcels.size()>5000) throw new ValidationException(ResponseMessage.INVALID_STRING_LENGTH.statusCodeValue());
        List<StockCountingExcel> importFails = new ArrayList<>();

        CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting> data =
                (CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting>) getAll(shopId, searchKeywords );

        List<StockCountingDetailDTO> stockCountingDetails = data.getResponse();

        if (stockCountingDetails.isEmpty())
            throw new ValidateException(ResponseMessage.EMPTY_LIST);

        int importSuccessNumber = 0;
        for (StockCountingDetailDTO countingDetail : stockCountingDetails) {
            for (StockCountingExcel e : stockCountingExcels) {
                if(e.getProductCode() == null) throw new ValidationException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS.statusCodeValue());
                if(e.getPacketQuantity() == null || e.getUnitQuantity() == null) throw new ValidationException(ResponseMessage.PACKAGE_OR_UINT_QUANTITY_MUST_NOT_BE_NULL.statusCodeValue());
                if(e.getProductCode().length()>4000||e.getPacketQuantity().toString().length()>7||e.getUnitQuantity().toString().length()>7)
                    throw new ValidateException(ResponseMessage.INVALID_STRING_LENGTH);
                if (countingDetail.getProductCode().equals(e.getProductCode()) && (e.getPacketQuantity() > 0 && e.getUnitQuantity() > 0)) {
                    int inventoryQuantity = e.getPacketQuantity() * countingDetail.getConvfact() + e.getUnitQuantity();
                    countingDetail.setPacketQuantity(e.getPacketQuantity());
                    countingDetail.setUnitQuantity(e.getUnitQuantity());
                    countingDetail.setInventoryQuantity(inventoryQuantity);
                    countingDetail.setChangeQuantity(inventoryQuantity - countingDetail.getStockQuantity());
                    importSuccessNumber++;
                }

                if (!stockCountingDetails.stream().anyMatch(detail -> detail.getProductCode().equals(e.getProductCode()))
                        || (e.getPacketQuantity() < 0 || e.getUnitQuantity() < 0)) {
                    if( !checkDataType(e) &&!importFails.contains(e) ){
                        e.setError("Số nhập vào phải là số nguyên dương");
                        importFails.add(e);
                    }
                    if (!importFails.contains(e)) {
                        e.setError("Sản phẩm không có trong kho");
                        importFails.add(e);
                    }
                }
            }
        }
        return new CoverResponse<>(
                new StockCountingImportDTO(stockCountingDetails, importFails), new InventoryImportInfo(importSuccessNumber, importFails.size()));
    }

    public boolean checkDataType(StockCountingExcel stockCountingExcel) {
        if (stockCountingExcel.getUnitQuantity().toString().contains("-") ||
                stockCountingExcel.getPacketQuantity().toString().contains("-")) /*||
                stockCountingExcel.getStockQuantity().toString().contains("-") ||
                stockCountingExcel.getInventoryQuantity().toString().contains("-"))*/
            return false;

        if (stockCountingExcel.getUnitQuantity() != (int) stockCountingExcel.getUnitQuantity() ||
                stockCountingExcel.getPacketQuantity() != (int) stockCountingExcel.getPacketQuantity()) /*||
                stockCountingExcel.getStockQuantity() != (int) stockCountingExcel.getStockQuantity() ||
                stockCountingExcel.getInventoryQuantity() != (int) stockCountingExcel.getInventoryQuantity())*/
            return false;
        return true;
    }

    @Override
    public List<StockCountingDetail> updateStockCounting(Long stockCountingId, String userAccount,
                                                                   List<StockCountingUpdateDTO> details) {
        StockCounting stockCounting = repository.findById(stockCountingId).get();
        if (stockCounting == null)
            throw new ValidateException(ResponseMessage.STOCK_COUNTING_NOT_FOUND);

        List<StockCountingDetail> stockCountingDetails = countingDetailRepository.findByStockCountingId(stockCountingId);

        if (stockCountingDetails.isEmpty())
            throw new ValidateException(ResponseMessage.NO_PRODUCT_IN_STOCK_COUNTING);

        for (int i = 0; i < details.size(); i++) {
            for (StockCountingDetail stockCountingDetail : stockCountingDetails) {
                if (stockCountingDetail.getProductId() == details.get(i).getProductId()) {
                    stockCountingDetail.setQuantity(details.get(i).getPacketQuantity() * details.get(i).getConvfact() + details.get(i).getUnitQuantity());
                }
                countingDetailRepository.save(stockCountingDetail);
            }
        }
        return stockCountingDetails;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long createStockCounting(List<StockCountingDetailDTO> stockCountingDetails, Long userId, Long shopId, Boolean override) {
        if (stockCountingDetails.isEmpty())
            throw new ValidateException(ResponseMessage.EMPTY_LIST);
        WareHouseTypeDTO wareHouseType = receiptImportService.getWareHouseTypeName(shopId);
        List<StockCounting> countingNumberInDay = repository.findByWareHouseTypeId(wareHouseType.getId());
        StockCounting stockCounting = new StockCounting();

        if (countingNumberInDay.size() > 0) {
            if (override == null || override == false)
                throw new ValidateException(ResponseMessage.CREATE_CANCEL);
            else {
                countingDetailRepository.deleteAll(countingDetailRepository.findByStockCountingId(countingNumberInDay.get(0).getId()));
                stockCounting = countingNumberInDay.get(0);
            }
        }

        stockCounting.setStockCountingCode(createStockCountingCode(countingNumberInDay));
        stockCounting.setCountingDate(LocalDateTime.now());
        stockCounting.setShopId(shopId);
        stockCounting.setWareHouseTypeId(wareHouseType.getId());

        repository.save(stockCounting);

        for (StockCountingDetailDTO detail : stockCountingDetails) {
            StockCountingDetail stockCountingDetail = new StockCountingDetail();
            stockCountingDetail.setStockCountingId(stockCounting.getId());
            stockCountingDetail.setStockQuantity(detail.getStockQuantity());
            stockCountingDetail.setQuantity(detail.getInventoryQuantity());
            stockCountingDetail.setPrice(detail.getPrice());
            stockCountingDetail.setShopId(shopId);
            stockCountingDetail.setProductId(detail.getProductId());
            stockCountingDetail.setCountingDate(LocalDateTime.now());

            countingDetailRepository.save(stockCountingDetail);
        }
        return stockCounting.getId();
    }

    @Override
    public Boolean checkInventoryInDay(Long shopId) {
        WareHouseTypeDTO wareHouseType = receiptImportService.getWareHouseTypeName(shopId);
        List<StockCounting> countingNumberInDay = repository.findByWareHouseTypeId(wareHouseType.getId());

        if (countingNumberInDay.size() > 0)
            return false;
        return true;
    }

    public List<StockCountingExcel> readDataExcel(MultipartFile file) throws IOException {
        String path = file.getOriginalFilename();

        InputStream stream = file.getInputStream();
        PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().headerStart(8).build();

        if (path.split("\\.")[1].equals("xlsx"))
            return Poiji.fromExcel(stream, PoijiExcelType.XLSX, StockCountingExcel.class, options);
        if (path.split("\\.")[1].equals("xls"))
            return Poiji.fromExcel(stream, PoijiExcelType.XLS, StockCountingExcel.class, options);
        return null;
    }

    public String createStockCountingCode(List<StockCounting> countingInDay) {
        LocalDate myLocal = LocalDate.now();
        StringBuilder code = new StringBuilder("KK");
        String codeNum = "00000";
        code.append(myLocal.get(IsoFields.QUARTER_OF_YEAR));
        code.append(".");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String strDate = formatter.format(date);
        code.append(strDate);
        code.append(".");
        code.append(codeNum.substring(String.valueOf(countingInDay.size()).length()));
        if (countingInDay.size() == 0)
            code.append(1);
        else
            code.append(countingInDay.size());

        return code.toString();
    }
}
