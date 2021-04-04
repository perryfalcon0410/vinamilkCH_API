package vn.viettel.sale.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.sale.OnlineOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.OnlineOrderRepository;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.specification.OnlineOrderSpecification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
public class OnlineOrderServiceImpl extends BaseServiceImpl<OnlineOrder, OnlineOrderRepository> implements OnlineOrderService {

    @Override
    public Response<Page<OnlineOrder>> getOnlineOrders(
            String orderNumber, Long shopId, Integer sysStatus, Date fromDate, Date toDate, Pageable pageable) {
        if (fromDate == null || toDate == null) {
            LocalDate initial = LocalDate.now();
            fromDate = Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            toDate = Date.from(initial.withDayOfMonth(initial.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        Page<OnlineOrder> onlineOrders = repository.findAll(
                Specification.where(OnlineOrderSpecification.hasOrderNumber(orderNumber))
                             .and(OnlineOrderSpecification.hasShopId(shopId))
                             .and(OnlineOrderSpecification.hasSysStatus(sysStatus))
                             .and(OnlineOrderSpecification.hasFromDateToDate(fromDate, toDate)), pageable);

        return new Response<Page<OnlineOrder>>().withData(onlineOrders);
    }
}
