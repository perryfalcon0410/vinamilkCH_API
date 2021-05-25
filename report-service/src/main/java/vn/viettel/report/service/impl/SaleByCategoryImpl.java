package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.util.ConvertDateToSearch;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.report.messaging.SaleCategoryFilter;
import vn.viettel.report.messaging.SaleDeliveryTypeFilter;
import vn.viettel.report.service.dto.SaleByDeliveryTypeDTO;
import vn.viettel.report.service.dto.SalesByCategoryReportDTO;
import vn.viettel.report.service.feign.CustomerTypeClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.Date;
import java.util.List;

public class SaleByCategoryImpl {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    CustomerTypeClient customerTypeClient;

    private List<SalesByCategoryReportDTO> callStoreProcedure(SaleCategoryFilter filter) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_SALES_BY_CATEGORY", SaleByDeliveryTypeDTO.class);
        query.registerStoredProcedureParameter("compare", void.class,  ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("fromDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("shopId", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("orderNumber", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("customerKW", String.class, ParameterMode.IN);

        query.setParameter("fromDate", ConvertDateToSearch.convertFromDate(filter.getFromDate()));
        query.setParameter("toDate", ConvertDateToSearch.convertToDate(filter.getToDate()));
        query.setParameter("shopId", Integer.valueOf(filter.getShopId().toString()));
        if(filter.getCustomerKW() == null)
            query.setParameter("customerKW", null);
        else query.setParameter("customerKW", VNCharacterUtils.removeAccent(filter.getCustomerKW()));
        query.execute();
        List<SalesByCategoryReportDTO> reportDTOS = query.getResultList();
        return reportDTOS;
    }

    List<CustomerTypeDTO> getCustomerTypes(){
        List<CustomerTypeDTO> list = customerTypeClient.getCusTypesV1();
        return list;
    }
}
