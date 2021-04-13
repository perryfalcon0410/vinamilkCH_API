package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.common.ProductInfo;
import vn.viettel.core.db.entity.stock.StockCountingDetail;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.report.repository.ProductInfoRepository;
import vn.viettel.report.repository.ProductRepository;
import vn.viettel.report.repository.StockCountingDetailRepository;
import vn.viettel.report.service.StockCountingReportService;
import vn.viettel.report.service.dto.StockCountingReportDTO;
import vn.viettel.report.specification.StockCountingSpecification;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockCountingReportServiceImpl extends BaseServiceImpl<StockCountingDetail, StockCountingDetailRepository> implements StockCountingReportService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductInfoRepository productInfoRepository;

    @Override
    public Response<Page<StockCountingReportDTO>> find(Date countingDate, Long productId, Pageable pageable) {

        if (countingDate == null)
            countingDate = new Date();

        Page<StockCountingDetail> stockCountingList = repository.findAll(Specification
                .where(StockCountingSpecification.hasProductId(productId)
                        .and(StockCountingSpecification.hasCountingDate(countingDate))), pageable);

        List<StockCountingReportDTO> result = stockCountingList.stream().map(
                item -> stockCountingMapper(item)
        ).collect(Collectors.toList());

        return new Response<Page<StockCountingReportDTO>>().withData(new PageImpl<>(result));
    }

    public StockCountingReportDTO stockCountingMapper(StockCountingDetail stockCountingDetail) {
        StockCountingReportDTO countingReportDTO = new StockCountingReportDTO();

        Product product = productRepository.findById(stockCountingDetail.getProductId()).get();
        ProductInfo category = productInfoRepository.findByIdAndType(product.getCatId(), 1);
        ProductInfo productGroup = productInfoRepository.findByIdAndType(product.getCatId(), 6);

        countingReportDTO.setProductCode(product.getProductCode());
        countingReportDTO.setProductName(product.getProductName());
        countingReportDTO.setConvfact(product.getConvFact());
        countingReportDTO.setPrice(stockCountingDetail.getPrice());
        countingReportDTO.setProductCategory(category.getProductInfoName());
        if (productGroup != null)
            countingReportDTO.setProductGroup(productGroup.getProductInfoName());
        countingReportDTO.setStockQuantity(stockCountingDetail.getQuantity());
        countingReportDTO.setPacketQuantity(stockCountingDetail.getQuantity() / product.getConvFact());
        countingReportDTO.setUnitQuantity(stockCountingDetail.getQuantity() % product.getConvFact());
        countingReportDTO.setTotalAmount(stockCountingDetail.getQuantity()*stockCountingDetail.getPrice());
        countingReportDTO.setMinInventory(0);
        countingReportDTO.setMaxInventory(0);

        return countingReportDTO;
    }
}
