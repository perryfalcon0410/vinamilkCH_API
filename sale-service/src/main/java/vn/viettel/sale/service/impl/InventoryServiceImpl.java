package vn.viettel.sale.service.impl;

import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Response<Page<StockCountingDTO>> index(String stockCountingCode, Long warehouseTypeId, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        Response<Page<StockCountingDTO>> response = new Response<>();
        Page<StockCounting> stockCountings = repository.findAll(Specification
                        .where(InventorySpecification.hasCountingCode(stockCountingCode))
                        .and(InventorySpecification.hasFromDateToDate(fromDate, toDate).and(InventorySpecification.hasWareHouse(warehouseTypeId)))
                , pageable);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return response.withData(stockCountings.map(this::mapStockCountingToStockCountingDTO));
    }

    @Override
    public Object getAll(Pageable pageable, Boolean isPaging) {
        Long wareHouseType = customerTypeClient.getCustomerTypeDefaultV1().getData().getWareHouseTypeId();
        Page<StockTotal> totalInventory = stockTotalRepository.findAll(pageable, wareHouseType);
        List<StockCountingDetailDTO> stockCountingList = new ArrayList<>();

        int totalInStock = 0, inventoryTotal = 0, totalPacket = 0, totalUnit = 0;
        double totalAmount = 0;

        for (StockTotal stockTotal : totalInventory) {
            Product product = productRepository.findById(stockTotal.getProductId()).get();
            if (product == null)
                throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
            ProductInfo category = productInfoRepository.findByIdAndType(product.getCatId(), 1);
            if (category == null)
                throw new ValidateException(ResponseMessage.CATEGORY_DATA_NOT_EXISTS);
            Optional<Price> productPrice = priceRepository.getByASCCustomerType(product.getId());
            if (!productPrice.isPresent())
                throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);

            StockCountingDetailDTO stockCounting = new StockCountingDetailDTO();

            stockCounting.setWarehouseTypeId(stockTotal.getWareHouseTypeId());
            stockCounting.setProductCategory(category.getProductInfoName());
            stockCounting.setProductGroup(productInfoRepository.findByIdAndType(product.getGroupCatId(), 6).getProductInfoName());
            stockCounting.setProductName(product.getProductName());
            stockCounting.setProductCode(product.getProductCode());
            stockCounting.setProductId(product.getId());
            stockCounting.setShopId(stockTotal.getShopId());
            stockCounting.setStockQuantity(stockTotal.getQuantity());
            if (productPrice != null)
                stockCounting.setPrice(productPrice.get().getPrice());
            stockCounting.setTotalAmount(stockTotal.getQuantity() * productPrice.get().getPrice());
            stockCounting.setPacketQuantity(0);
            stockCounting.setUnitQuantity(0);
            stockCounting.setInventoryQuantity(0);
            stockCounting.setChangeQuantity(0 - stockTotal.getQuantity());
            if (product.getConvFact() != null)
                stockCounting.setConvfact(product.getConvFact());
            if (product.getUom2() != null)
                stockCounting.setPacketUnit(product.getUom2());
            if (product.getUom1() != null)
                stockCounting.setUnit(product.getUom1());

            totalAmount += stockTotal.getQuantity() * productPrice.get().getPrice();
            totalInStock += stockTotal.getQuantity();

            stockCountingList.add(stockCounting);
        }
        TotalStockCounting totalStockCounting =
                setStockTotalInfo(totalInStock, inventoryTotal, totalPacket, totalUnit, totalAmount, totalInventory.getContent().get(0).getWareHouseTypeId(), null, null);

        if (isPaging) {
            Page<StockCountingDetailDTO> pageResponse = new PageImpl<>(stockCountingList, pageable, totalInventory.getTotalElements());
            CoverResponse<Page<StockCountingDetailDTO>, TotalStockCounting> response =
                    new CoverResponse(pageResponse, totalStockCounting);

            return response;
        } else {
            CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting> response =
                    new CoverResponse(stockCountingList, totalStockCounting);
            return response;
        }
    }

    public TotalStockCounting setStockTotalInfo(int totalInStock, int inventoryTotal, int totalPacket, int totalUnit, double totalAmount, Long wareHouseTypeId, String countingCode, String countingDate) {
        TotalStockCounting totalStockCounting = new TotalStockCounting();

        totalStockCounting.setStockTotal(totalInStock);
        totalStockCounting.setInventoryTotal(inventoryTotal);
        totalStockCounting.setChangeQuantity(inventoryTotal - totalInStock);
        totalStockCounting.setTotalAmount(totalAmount);
        totalStockCounting.setTotalPacket(totalPacket);
        totalStockCounting.setTotalUnit(totalUnit);
        totalStockCounting.setCountingCode(countingCode);
        totalStockCounting.setCountingDate(countingDate);
        totalStockCounting.setWarehouseType(wareHouseTypeId);

        return totalStockCounting;
    }

    @Override
    public Response<CoverResponse<Page<StockCountingExcel>, TotalStockCounting>> getByStockCountingId(Long id, Pageable pageable) {
        StockCounting stockCounting = repository.findById(id).get();
        List<StockCountingExcel> result = new ArrayList<>();
        if (stockCounting == null)
            return new Response<CoverResponse<Page<StockCountingExcel>, TotalStockCounting>>()
                    .withError(ResponseMessage.STOCK_COUNTING_NOT_FOUND);
        Page<StockCountingDetail> stockCountingDetails = countingDetailRepository.findByStockCountingId(id, pageable);

        int totalInStock = 0, inventoryTotal = 0, totalPacket = 0, totalUnit = 0;
        float totalAmount = 0;

        for (StockCountingDetail detail : stockCountingDetails) {
            StockCountingExcel countingDetailDTO = new StockCountingExcel();

            Product product = productRepository.findById(detail.getProductId()).orElseThrow(() -> new ValidateException(ResponseMessage.NO_PRICE_APPLIED));
            if (product == null)
                return new Response<CoverResponse<Page<StockCountingExcel>, TotalStockCounting>>()
                        .withError(ResponseMessage.PRODUCT_NOT_FOUND);
            ProductInfo category = productInfoRepository.findByIdAndType(product.getCatId(), 1);
            ProductInfo group = productInfoRepository.findByIdAndType(product.getGroupCatId(), 6);
            if (category == null)
                return new Response<CoverResponse<Page<StockCountingExcel>, TotalStockCounting>>()
                        .withError(ResponseMessage.PRODUCT_INFO_NOT_FOUND);

            countingDetailDTO.setProductCategory(category.getProductInfoName());
            countingDetailDTO.setProductGroup(group.getProductInfoName());
            countingDetailDTO.setProductCode(product.getProductCode());
            countingDetailDTO.setProductName(product.getProductName());
            countingDetailDTO.setInventoryQuantity(detail.getQuantity());
            countingDetailDTO.setStockQuantity(detail.getStockQuantity());
            countingDetailDTO.setPrice(detail.getPrice());
            countingDetailDTO.setTotalAmount(detail.getPrice() * detail.getStockQuantity());
            countingDetailDTO.setConvfact(product.getConvFact());
            countingDetailDTO.setProductId(detail.getProductId());
            if (product.getUom2() != null)
                countingDetailDTO.setPacketUnit(product.getUom2());
            if (product.getUom1() != null)
                countingDetailDTO.setUnit(product.getUom1());
            if (product.getConvFact() != null) {
                countingDetailDTO.setPacketQuantity(detail.getQuantity() / product.getConvFact());
                countingDetailDTO.setUnitQuantity(detail.getQuantity() % product.getConvFact());

                totalPacket += detail.getQuantity() / product.getConvFact();
                totalUnit += detail.getQuantity() % product.getConvFact();
            }
            int stockQuantity = detail.getStockQuantity() != null ? detail.getStockQuantity() : 0;
            int quantity = detail.getQuantity() != null ? detail.getQuantity() : 0;

            countingDetailDTO.setChangeQuantity(stockQuantity - quantity);

            totalInStock += stockQuantity;
            inventoryTotal += quantity;
            totalAmount += detail.getPrice() * stockQuantity;

            result.add(countingDetailDTO);
        }

        TotalStockCounting totalStockCounting =
                setStockTotalInfo(totalInStock, inventoryTotal, totalPacket, totalUnit, totalAmount, stockCounting.getWareHouseTypeId(),
                        stockCounting.getStockCountingCode(), stockCounting.getCountingDate().toString());

        Page<StockCountingExcel> pageResponse = new PageImpl<>(result);
        CoverResponse<Page<StockCountingExcel>, TotalStockCounting> response =
                new CoverResponse(pageResponse, totalStockCounting);

        return new Response<CoverResponse<Page<StockCountingExcel>, TotalStockCounting>>()
                .withData(response);
    }

    @Override
    public CoverResponse<StockCountingImportDTO, InventoryImportInfo> importExcel(MultipartFile file, Pageable pageable) throws IOException {
        List<StockCountingExcel> stockCountingExcels = readDataExcel(file);
        List<StockCountingExcel> importFails = new ArrayList<>();

        Response<CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting>> data =
                (Response<CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting>>) getAll(pageable, false);

        List<StockCountingDetailDTO> stockCountingDetails = data.getData().getResponse();

        if (stockCountingDetails.isEmpty())
            throw new ValidateException(ResponseMessage.EMPTY_LIST);

        int importSuccessNumber = 0;
        for (StockCountingDetailDTO countingDetail : stockCountingDetails) {
            for (StockCountingExcel e : stockCountingExcels) {
                if (countingDetail.getProductCode().equals(e.getProductCode()) && (e.getPacketQuantity() > 0 && e.getUnitQuantity() > 0)) {
                    int inventoryQuantity = e.getPacketQuantity() * countingDetail.getConvfact() + e.getUnitQuantity();
                    countingDetail.setPacketQuantity(e.getPacketQuantity());
                    countingDetail.setUnitQuantity(e.getUnitQuantity());
                    countingDetail.setInventoryQuantity(inventoryQuantity);
                    countingDetail.setChangeQuantity(inventoryQuantity - countingDetail.getStockQuantity());
                    importSuccessNumber++;
                }

                if (!stockCountingDetails.stream().anyMatch(detail -> detail.getProductCode().equals(e.getProductCode()))
                        || !checkDataType(e) || (e.getPacketQuantity() < 0 || e.getUnitQuantity() < 0)) {
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
                stockCountingExcel.getPacketQuantity().toString().contains("-") ||
                stockCountingExcel.getStockQuantity().toString().contains("-") ||
                stockCountingExcel.getInventoryQuantity().toString().contains("-"))
            return false;

        if (stockCountingExcel.getUnitQuantity() != (int) stockCountingExcel.getUnitQuantity() ||
                stockCountingExcel.getPacketQuantity() != (int) stockCountingExcel.getPacketQuantity() ||
                stockCountingExcel.getStockQuantity() != (int) stockCountingExcel.getStockQuantity() ||
                stockCountingExcel.getInventoryQuantity() != (int) stockCountingExcel.getInventoryQuantity())
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
    public Object createStockCounting(List<StockCountingDetailDTO> stockCountingDetails, Long userId, Long shopId, Boolean override) {
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
        return new Response<StockCounting>().withData(stockCounting);
    }

    @Override
    public Boolean checkInventoryInDay(Long shopId) {
        WareHouseTypeDTO wareHouseType = receiptImportService.getWareHouseTypeName(shopId);
        List<StockCounting> countingNumberInDay = repository.findByWareHouseTypeId(wareHouseType.getId());

        if (countingNumberInDay.size() > 0)
            return false;
        return true;
    }

    private StockCountingDTO mapStockCountingToStockCountingDTO(StockCounting stockCounting) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        StockCountingDTO dto = modelMapper.map(stockCounting, StockCountingDTO.class);
        return dto;
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
