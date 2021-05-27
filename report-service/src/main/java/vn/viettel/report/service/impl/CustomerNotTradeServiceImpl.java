package vn.viettel.report.service.impl;

import io.swagger.models.auth.In;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.CustomerTradeFilter;
import vn.viettel.report.service.CustomerNotTradeService;
import vn.viettel.report.service.dto.CustomerReportDTO;
import vn.viettel.report.service.dto.CustomerTradeDTO;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CustomerNotTradeServiceImpl implements CustomerNotTradeService {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Object index(Date fromDate, Date toDate, Boolean isPaging, Pageable pageable) {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_CUSTOMER_NOT_TRADE", CustomerReportDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, Date.class, ParameterMode.IN);

        storedProcedure.setParameter(2, fromDate);
        storedProcedure.setParameter(3, toDate);

        List<CustomerReportDTO> result = storedProcedure.getResultList();

        if (result.isEmpty())
            return new Response<Page<CustomerReportDTO>>()
                    .withData(new PageImpl<>(new ArrayList<>()));

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), result.size());
        List<CustomerReportDTO> subList = result.subList(start, end);

        if (isPaging)
            return new Response<Page<CustomerReportDTO>>().withData(new PageImpl<>(subList));
        else
            return new Response<List<CustomerReportDTO>>().withData(result);
    }

    @Override
    public Object findCustomerTrades(CustomerTradeFilter filter, Pageable pageable) {

        return this.callProcedure(filter);
    }


    private List<CustomerTradeDTO> callProcedure(CustomerTradeFilter filter) {
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("P_CUSTOMER_TRADE", CustomerTradeDTO.class );
        query.registerStoredProcedureParameter("results", void.class, ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("shopId", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("customerSearch", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("areaCode", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("customerType", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("customerStatus", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("customerPhone", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromCreateDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toCreateDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromPurchaseDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toPurchaseDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromSaleAmount", Float.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toSaleAmount", Float.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromSaleDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toSaleDate", Date.class, ParameterMode.IN);

        query.setParameter("shopId", filter.getShopId());
        query.setParameter("customerSearch", filter.getKeySearch());
        query.setParameter("areaCode", filter.getAreaCode());
        query.setParameter("customerType", filter.getCustomerType());
        query.setParameter("customerStatus", filter.getCustomerStatus());
        query.setParameter("customerPhone", filter.getCustomerPhone());
        query.setParameter("fromCreateDate", filter.getFromCreateDate());
        query.setParameter("toCreateDate", filter.getToCreateDate());
        query.setParameter("fromPurchaseDate", filter.getFromPurchaseDate());
        query.setParameter("toPurchaseDate", filter.getToPurchaseDate());
        query.setParameter("fromSaleAmount", filter.getFromSaleAmount());
        query.setParameter("toSaleAmount", filter.getToSaleAmount());
        query.setParameter("fromSaleDate", filter.getFromSaleDate());
        query.setParameter("toSaleDate", filter.getToSaleDate());

        List<CustomerTradeDTO> result = query.getResultList();

        return result;
    }

}
