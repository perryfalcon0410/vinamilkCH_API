package vn.viettel.sale.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.sale.repository.StockTotalRepository;
import vn.viettel.sale.service.StockTotalService;

@Service
public class StockTotalServiceImpl extends BaseServiceImpl<StockTotal, StockTotalRepository> implements StockTotalService {
}
