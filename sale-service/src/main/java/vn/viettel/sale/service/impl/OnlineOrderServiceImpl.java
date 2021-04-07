package vn.viettel.sale.service.impl;

import liquibase.pro.packaged.D;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.sale.OnlineOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.messaging.OnlineOrderFilter;
import vn.viettel.sale.repository.OnlineOrderRepository;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.dto.OnlineOrderDTO;
import vn.viettel.sale.specification.OnlineOrderSpecification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
public class OnlineOrderServiceImpl extends BaseServiceImpl<OnlineOrder, OnlineOrderRepository> implements OnlineOrderService {

    @Override
    public Response<Page<OnlineOrderDTO>> getOnlineOrders(
            OnlineOrderFilter filter, Pageable pageable) {

        if (filter.getFromDate() == null || filter.getToDate() == null) {
            LocalDate initial = LocalDate.now();
            filter.setFromDate(Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            filter.setToDate(new Date());
        }
        Page<OnlineOrder> onlineOrders = repository.findAll(
                Specification.where(
                            OnlineOrderSpecification.hasOrderNumber(filter.getOrderNumber()))
                             .and(OnlineOrderSpecification.hasShopId(filter.getShopId()))
                             .and(OnlineOrderSpecification.hasSynStatus(filter.getSynStatus()))
                             .and(OnlineOrderSpecification.hasFromDateToDate(filter.getFromDate(), filter.getToDate())), pageable);
        Page<OnlineOrderDTO> onlineOrderDTOS = onlineOrders.map(this::mapOnlineOrderToOnlineOrderDTO);

        return new Response<Page<OnlineOrderDTO>>().withData(onlineOrderDTOS);
    }


    private OnlineOrderDTO mapOnlineOrderToOnlineOrderDTO(OnlineOrder order) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OnlineOrderDTO dto = modelMapper.map(order, OnlineOrderDTO.class);
        return dto;
    }
}
