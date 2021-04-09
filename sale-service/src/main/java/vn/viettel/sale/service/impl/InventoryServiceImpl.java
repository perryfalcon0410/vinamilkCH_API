package vn.viettel.sale.service.impl;

import com.poiji.bind.Poiji;
import com.poiji.option.PoijiOptions;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.common.ProductInfo;
import vn.viettel.core.db.entity.stock.StockCounting;
import vn.viettel.core.db.entity.stock.StockCountingDetail;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.messaging.StockCountingFilter;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.InventoryService;
import vn.viettel.sale.service.dto.ExchangeTransExcel;
import vn.viettel.sale.service.dto.ExchangeTransImportDTO;
import vn.viettel.sale.service.dto.StockCountingDTO;
import vn.viettel.sale.service.dto.StockCountingDetailDTO;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.InventorySpecification;

import java.io.File;
import java.sql.Timestamp;
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
    UserClient userClient;

    private final Date date = new Date();
    private final Timestamp time = new Timestamp(date.getTime());

    @Override
    public Response<Page<StockCountingDTO>> find(StockCountingFilter filter, Pageable pageable) {
        Response<Page<StockCountingDTO>> response = new Response<>();

        Page<StockCounting> stockCountings;
        stockCountings = repository.findAll(Specification
                .where(InventorySpecification.hasCountingCode(filter.getStockCountingCode()))
                .and(InventorySpecification.hasFromDateToDate(filter.getFromDate(), filter.getToDate()))
                , pageable);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Page<StockCountingDTO> dtos = stockCountings.map(this::mapStockCountingToStockCountingDTO);
        return response.withData(dtos);
    }

    @Override
    public Response<Page<StockCountingDetailDTO>> getByStockCountingId(Long id, Pageable pageable) {
        StockCounting stockCounting = repository.findById(id).get();
        List<StockCountingDetailDTO> result = new ArrayList<>();
        if (stockCounting == null)
            return new Response<Page<StockCountingDetailDTO>>().withError(ResponseMessage.STOCK_COUNTING_NOT_FOUND);
        List<StockCountingDetail> stockCountingDetails = countingDetailRepository.findByStockCountingId(id);

        for (StockCountingDetail detail : stockCountingDetails) {
            StockCountingDetailDTO countingDetailDTO = new StockCountingDetailDTO();

            Product product = productRepository.findById(detail.getProductId()).get();
            if (product == null)
                return new Response<Page<StockCountingDetailDTO>>().withError(ResponseMessage.PRODUCT_NOT_FOUND);
            ProductInfo category = productInfoRepository.findByIdAndType(product.getCatId(), 1);
            if (category == null)
                return new Response<Page<StockCountingDetailDTO>>().withError(ResponseMessage.PRODUCT_INFO_NOT_FOUND);

            countingDetailDTO.setProductCategory(category.getProductInfoName());
            countingDetailDTO.setProductCode(product.getProductCode());
            countingDetailDTO.setProductName(product.getProductName());
            countingDetailDTO.setInventoryQuantity(detail.getStockQuantity());
            countingDetailDTO.setStockQuantity(detail.getStockQuantity());
            countingDetailDTO.setPrice(detail.getPrice());
            countingDetailDTO.setTotalAmount(String.format("%.0f", (detail.getPrice()*detail.getStockQuantity())));
            countingDetailDTO.setConvfact(product.getConvFact());
            countingDetailDTO.setPacketUnit(product.getUom2());
            countingDetailDTO.setUnit(product.getUom1());
            countingDetailDTO.setPacketQuantity(detail.getStockQuantity()/product.getConvFact());
            countingDetailDTO.setUnitQuantity(detail.getStockQuantity()%product.getConvFact());
            countingDetailDTO.setChangeQuantity(detail.getStockQuantity() - detail.getStockQuantity());

            result.add(countingDetailDTO);
        }
        return new Response<Page<StockCountingDetailDTO>>().withData(new PageImpl<>(result));
    }

    @Override
    public Response<ExchangeTransImportDTO> importExcel(List<StockCountingDetailDTO> stockCountingDetails, String filePath) {
        List<ExchangeTransExcel> exchangeTransExcels = readDataExcel(filePath);
        List<ExchangeTransExcel> importFails = new ArrayList<>();

        if (stockCountingDetails.isEmpty())
            throw new ValidateException(ResponseMessage.EMPTY_LIST);

        for (StockCountingDetailDTO countingDetail : stockCountingDetails) {
            exchangeTransExcels.forEach(e -> {
                if (countingDetail.getProductCode().equals(e.getProductCode())) {
                    int inventoryQuantity = e.getPacketQuantity()*e.getConvfact() + e.getUnitQuantity();
                    countingDetail.setPacketQuantity(e.getPacketQuantity());
                    countingDetail.setUnitQuantity(e.getUnitQuantity());
                    countingDetail.setInventoryQuantity(inventoryQuantity);
                    countingDetail.setChangeQuantity(inventoryQuantity - countingDetail.getStockQuantity());
                }

                if (!stockCountingDetails.stream().anyMatch(detail -> detail.getProductCode().equals(e.getProductCode()))) {
                    e.setEror("Sản phẩm không có trong kho");
                    importFails.add(e);
                }
            });
        }
        return new Response<ExchangeTransImportDTO>().withData(new ExchangeTransImportDTO(stockCountingDetails, importFails));
    }

    @Override
    public Response<List<StockCountingDetail>> updateStockCounting(Long stockCountingId, Long userId,
                                                                      List<StockCountingDetailDTO> details) {
        StockCounting stockCounting = repository.findById(stockCountingId).get();
        if (stockCounting == null)
            return new Response<List<StockCountingDetail>>().withError(ResponseMessage.STOCK_COUNTING_NOT_FOUND);

        List<StockCountingDetail> stockCountingDetails = countingDetailRepository.findByStockCountingId(stockCountingId);
        stockCounting.setUpdatedAt(time);
        stockCounting.setUpdateUser(userClient.getUserById(userId).getUserAccount());
        repository.save(stockCounting);

        for (int i = 0; i < stockCountingDetails.size(); i++) {
            stockCountingDetails.get(i).setQuantity(details.get(i).getInventoryQuantity());
            countingDetailRepository.save(stockCountingDetails.get(i));
        }
        return new Response<List<StockCountingDetail>>().withData(stockCountingDetails);
    }

    private StockCountingDTO mapStockCountingToStockCountingDTO(StockCounting stockCounting) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        StockCountingDTO dto = modelMapper.map(stockCounting, StockCountingDTO.class);
        return dto;
    }

    public List<ExchangeTransExcel> readDataExcel(String path) {
        if (!path.split("\\.")[1].equals("xlsx") && !path.split("\\.")[1].equals("xls"))
            throw new ValidateException(ResponseMessage.NOT_AN_EXCEL_FILE);

        File file = new File(path);
        PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings(1).headerStart(8).build();
        return Poiji.fromExcel(file, ExchangeTransExcel.class, options);
    }
}
