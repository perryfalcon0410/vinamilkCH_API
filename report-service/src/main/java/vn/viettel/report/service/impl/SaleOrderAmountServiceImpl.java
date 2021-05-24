package vn.viettel.report.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.report.messaging.SaleOrderAmountFilter;
import vn.viettel.report.service.SaleOrderAmountService;
import vn.viettel.report.service.dto.TableDynamicDTO;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SaleOrderAmountServiceImpl implements SaleOrderAmountService {

    @PersistenceContext
    EntityManager entityManager;


    @Override
    public TableDynamicDTO findAmounts(SaleOrderAmountFilter filter) {
       return this.callProcedure(filter);
//        return null;
    }


    public TableDynamicDTO callProcedure(SaleOrderAmountFilter filter) {

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_CUSTOMERS_SALE_ORDER_TOTAL");
        query.registerStoredProcedureParameter("results", void.class,  ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("dateResults", void.class,  ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("shopId", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("customerTypeId", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("nameOrCodeCusUpper", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("phoneNumber", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromNumber", Float.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toNumber", Float.class, ParameterMode.IN);

        query.setParameter("shopId", filter.getShopId());
        query.setParameter("fromDate", filter.getFromDate());
        query.setParameter("toDate", filter.getToDate());
        query.setParameter("customerTypeId", filter.getCustomerTypeId());
        query.setParameter("nameOrCodeCusUpper", filter.getNameOrCodeCustomer());
        query.setParameter("phoneNumber", filter.getPhoneNumber());
        query.setParameter("fromNumber", filter.getFromAmount());
        query.setParameter("toNumber", filter.getToAmount());

        query.execute();

        List<Object[]> reportDTOS = query.getResultList();
        List<Object> reportDTOS1 = new ArrayList<>();
        if(query.hasMoreResults()) {
            reportDTOS1 = query.getResultList();
        }

//        TableDynamicDTO tableDynamicDTO = new TableDynamicDTO();
//        tableDynamicDTO.setDataset(reportDTOS);

        return null;
    }




}
