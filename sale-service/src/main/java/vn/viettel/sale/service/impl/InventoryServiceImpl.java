package vn.viettel.sale.service.impl;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.db.entity.common.CustomerType;
import vn.viettel.core.db.entity.stock.PoTrans;
import vn.viettel.core.db.entity.stock.StockCounting;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.service.dto.CustomerDTO;
import vn.viettel.customer.specification.CustomerSpecification;
import vn.viettel.sale.messaging.StockCountingFilter;
import vn.viettel.sale.repository.PoTransRepository;
import vn.viettel.sale.repository.StockCountingRepository;
import vn.viettel.sale.service.InventoryService;
import vn.viettel.sale.service.ReceiptExportService;
import vn.viettel.sale.service.dto.StockCountingDTO;
import vn.viettel.sale.specification.InventorySpecification;

@Service
public class InventoryServiceImpl extends BaseServiceImpl<StockCounting, StockCountingRepository> implements InventoryService {

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
    private StockCountingDTO mapStockCountingToStockCountingDTO(StockCounting stockCounting) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        StockCountingDTO dto = modelMapper.map(stockCounting, StockCountingDTO.class);
        return dto;
    }
}
