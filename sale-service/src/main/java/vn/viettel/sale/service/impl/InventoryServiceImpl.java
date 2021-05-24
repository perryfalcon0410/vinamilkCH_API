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
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.InventorySpecification;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Override
    public Response<Page<StockCountingDTO>> index(String stockCountingCode, Long warehouseTypeId, Date fromDate, Date toDate, Pageable pageable) {
        Response<Page<StockCountingDTO>> response = new Response<>();
        Page<StockCounting> stockCountings;
        stockCountings = repository.findAll(Specification
                        .where(InventorySpecification.hasCountingCode(stockCountingCode))
                        .and(InventorySpecification.hasFromDateToDate(fromDate, toDate).and(InventorySpecification.hasWareHouse(warehouseTypeId)))
                , pageable);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Page<StockCountingDTO> dtos = stockCountings.map(this::mapStockCountingToStockCountingDTO);
        return response.withData(dtos);
    }

    @Override
    public Object getAll(Pageable pageable, Boolean isPaging) {
        Page<StockTotal> totalInventory = stockTotalRepository.findAll(pageable);
        List<StockCountingDetailDTO> stockCountingList = new ArrayList<>();

        int totalInStock = 0, inventoryTotal = 0, totalPacket = 0, totalUnit = 0;
        float totalAmount = 0;

        for (StockTotal stockTotal : totalInventory) {
            Product product = productRepository.findById(stockTotal.getProductId()).get();
            ProductInfo category = productInfoRepository.findByIdAndType(product.getCatId(), 1);
            Price productPrice = priceRepository.getByASCCustomerType(product.getId()).get();

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
                stockCounting.setPrice(productPrice.getPrice());
            stockCounting.setTotalAmount(stockTotal.getQuantity() * productPrice.getPrice());
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

            totalAmount += stockTotal.getQuantity() * productPrice.getPrice();
            totalInStock += stockTotal.getQuantity();

            stockCountingList.add(stockCounting);
        }
        TotalStockCounting totalStockCounting = setStockTotalInfo(totalInStock, inventoryTotal, totalPacket, totalUnit, totalAmount);

        if (isPaging) {
            Page<StockCountingDetailDTO> pageResponse = new PageImpl<>(stockCountingList);
            CoverResponse<Page<StockCountingDetailDTO>, TotalStockCounting> response =
                    new CoverResponse(pageResponse, totalStockCounting);

            return new Response<CoverResponse<Page<StockCountingDetailDTO>, TotalStockCounting>>()
                    .withData(response);
        } else {
            CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting> response =
                    new CoverResponse(stockCountingList, totalStockCounting);
            return new Response<CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting>>()
                    .withData(response);
        }
    }

    public TotalStockCounting setStockTotalInfo(int totalInStock, int inventoryTotal, int totalPacket, int totalUnit, float totalAmount) {
        TotalStockCounting totalStockCounting = new TotalStockCounting();

        totalStockCounting.setStockTotal(totalInStock);
        totalStockCounting.setInventoryTotal(inventoryTotal);
        totalStockCounting.setChangeQuantity(inventoryTotal - totalInStock);
        totalStockCounting.setTotalAmount(totalAmount);
        totalStockCounting.setTotalPacket(totalPacket);
        totalStockCounting.setTotalUnit(totalUnit);

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
            if (product.getUom2() != null)
                countingDetailDTO.setPacketUnit(product.getUom2());
            if (product.getUom1() != null)
                countingDetailDTO.setUnit(product.getUom1());
            if (product.getConvFact() != null) {
                countingDetailDTO.setPacketQuantity(detail.getStockQuantity() / product.getConvFact());
                countingDetailDTO.setUnitQuantity(detail.getStockQuantity() % product.getConvFact());

                totalPacket += detail.getStockQuantity() / product.getConvFact();
                totalUnit += detail.getStockQuantity() % product.getConvFact();
            }
            countingDetailDTO.setChangeQuantity(detail.getStockQuantity() - detail.getQuantity());

            totalInStock += detail.getStockQuantity();
            inventoryTotal += detail.getQuantity();
            totalAmount += detail.getPrice() * detail.getStockQuantity();

            result.add(countingDetailDTO);
        }

        TotalStockCounting totalStockCounting = setStockTotalInfo(totalInStock, inventoryTotal, totalPacket, totalUnit, totalAmount);
        Page<StockCountingExcel> pageResponse = new PageImpl<>(result);
        CoverResponse<Page<StockCountingExcel>, TotalStockCounting> response =
                new CoverResponse(pageResponse, totalStockCounting);

        return new Response<CoverResponse<Page<StockCountingExcel>, TotalStockCounting>>()
                .withData(response);
    }

    @Override
    public Response<StockCountingImportDTO> importExcel(MultipartFile file, Pageable pageable) throws IOException {
        List<StockCountingExcel> stockCountingExcels = readDataExcel(file);
        List<StockCountingExcel> importFails = new ArrayList<>();

        Response<CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting>> data =
                (Response<CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting>>) getAll(pageable, false);

        List<StockCountingDetailDTO> stockCountingDetails = data.getData().getResponse();

        if (stockCountingDetails.isEmpty())
            throw new ValidateException(ResponseMessage.EMPTY_LIST);

        for (StockCountingDetailDTO countingDetail : stockCountingDetails) {
            for (StockCountingExcel e : stockCountingExcels) {
                if (countingDetail.getProductCode().equals(e.getProductCode())) {
                    if (e.getPacketQuantity() < 0 || e.getUnitQuantity() < 0)
                        throw new ValidateException(ResponseMessage.INVENTORY_QUANTITY_MUST_NOT_BE_NULL);
                    int inventoryQuantity = e.getPacketQuantity() * countingDetail.getConvfact() + e.getUnitQuantity();
                    countingDetail.setPacketQuantity(e.getPacketQuantity());
                    countingDetail.setUnitQuantity(e.getUnitQuantity());
                    countingDetail.setInventoryQuantity(inventoryQuantity);
                    countingDetail.setChangeQuantity(inventoryQuantity - countingDetail.getStockQuantity());
                }

                if (!stockCountingDetails.stream().anyMatch(detail -> detail.getProductCode().equals(e.getProductCode()))) {
                    if (!importFails.contains(e)) {
                        e.setError("Sản phẩm không có trong kho");
                        importFails.add(e);
                    }
                }
            }
        }
        return new Response<StockCountingImportDTO>().withData(new StockCountingImportDTO(stockCountingDetails, importFails));
    }

//    public Response<List<StockCountingExcel>> listStockCountingExport(Long id) {
//        List<StockCountingDetail> details = countingDetailRepository.findByStockCountingId(id);
//        List<StockCountingExcel> listExport = new ArrayList<>();
//        for(StockCountingDetail scd:details) {
//            StockCountingExcel stock = new StockCountingExcel();
//            Product product = productRepository.findProductById(scd.getProductId());
//            ProductInfo category = productInfoRepository.findByIdAndType(product.getCatId(), 1);
//            ProductInfo group = productInfoRepository.findByIdAndType(product.getGroupCatId(), 6);
//            stock.setProductCategory(category.getProductInfoName());
//            stock.setProductGroup(group.getProductInfoName());
//            stock.setProductCode(product.getProductCode());
//            stock.setProductName(product.getProductName());
//            stock.setStockQuantity(scd.getStockQuantity());
//            stock.setPrice(scd.getPrice());
//            stock.setTotalAmount(scd.getStockQuantity() * scd.getPrice());
//            if (product.getUom2() != null)
//                stock.setPacketUnit(product.getUom2());
//            if (product.getUom1() != null)
//                stock.setUnit(product.getUom1());
//            if (product.getConvFact() != null) {
//                stock.setPacketQuantity(scd.getStockQuantity() / product.getConvFact());
//                stock.setUnitQuantity(scd.getStockQuantity() % product.getConvFact());
//            }
//            stock.setInventoryQuantity(scd.getQuantity());
//            stock.setChangeQuantity(scd.getStockQuantity() - scd.getQuantity());
//            stock.setConvfact(product.getConvFact());
//            listExport.add(stock);
//        }
//        return new Response<List<StockCountingExcel>>().withData(listExport);
//    }

    @Override
    public Response<List<StockCountingDetail>> updateStockCounting(Long stockCountingId, Long userId,
                                                                   List<StockCountingDetailDTO> details) {
        StockCounting stockCounting = repository.findById(stockCountingId).get();
        if (stockCounting == null)
            return new Response<List<StockCountingDetail>>().withError(ResponseMessage.STOCK_COUNTING_NOT_FOUND);

        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        List<StockCountingDetail> stockCountingDetails = countingDetailRepository.findByStockCountingId(stockCountingId);
        stockCounting.setUpdatedAt(time);
        stockCounting.setUpdateUser(userClient.getUserByIdV1(userId).getUserAccount());
        repository.save(stockCounting);

        for (int i = 0; i < details.size(); i++) {
            for (StockCountingDetail stockCountingDetail : stockCountingDetails) {
                if (stockCountingDetail.getProductId() == details.get(i).getProductId())
                    stockCountingDetail.setQuantity(details.get(i).getInventoryQuantity());
                countingDetailRepository.save(stockCountingDetail);
            }
        }
        return new Response<List<StockCountingDetail>>().withData(stockCountingDetails);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object createStockCounting(List<StockCountingDetailDTO> stockCountingDetails, Long userId, Long shopId, Boolean override) {
        if (stockCountingDetails.isEmpty())
            throw new ValidateException(ResponseMessage.EMPTY_LIST);
        WareHouseTypeDTO wareHouseType = receiptImportService.getWareHouseTypeName(shopId).getData();
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
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        stockCounting.setStockCountingCode(createStockCountingCode(countingNumberInDay));
        stockCounting.setCountingDate(time);
        stockCounting.setCreatedAt(time);
        stockCounting.setCreateUser(userClient.getUserByIdV1(userId).getUserAccount());
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
            stockCountingDetail.setCreatedAt(time);

            countingDetailRepository.save(stockCountingDetail);
        }
        return new Response<StockCounting>().withData(stockCounting);
    }

    @Override
    public Response<Boolean> checkInventoryInDay(Long shopId) {
        WareHouseTypeDTO wareHouseType = receiptImportService.getWareHouseTypeName(shopId).getData();
        List<StockCounting> countingNumberInDay = repository.findByWareHouseTypeId(wareHouseType.getId());

        if (countingNumberInDay.size() > 0)
            return new Response<Boolean>().withData(false);
        return new Response<Boolean>().withData(true);
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
        code.append(countingInDay.size());

        return code.toString();
    }
}
