package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.messaging.SaleDeliveryTypeFilter;
import vn.viettel.report.service.SaleDeliveryTypeService;
import vn.viettel.report.service.dto.ExchangeTransReportDTO;
import vn.viettel.report.service.dto.ExchangeTransReportFullDTO;
import vn.viettel.report.service.dto.ExchangeTransReportRate;
import vn.viettel.report.service.dto.SaleByDeliveryTypeDTO;
import vn.viettel.report.service.excel.ExchangeTransExcel;
import vn.viettel.report.service.excel.SaleDeliveryTypeExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.*;
import java.util.Date;
import java.util.List;

@Service
public class SaleByDeliveryImpl implements SaleDeliveryTypeService {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ShopClient shopClient;

    @Override
    public ByteArrayInputStream exportExcel(SaleDeliveryTypeFilter filter) throws IOException {
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        List<SaleByDeliveryTypeDTO> salesDeli = this.callStoreProcedure(filter);
        SaleDeliveryTypeExcel excel = new SaleDeliveryTypeExcel(shopDTO, salesDeli);
        excel.setFromDate(filter.getFromDate());
        excel.setToDate(filter.getToDate());
        return excel.export();
    }

    private List<SaleByDeliveryTypeDTO> callStoreProcedure(SaleDeliveryTypeFilter filter) {
        Instant inst = filter.getFromDate().toInstant();
        LocalDate localDate = inst.atZone(ZoneId.systemDefault()).toLocalDate();
        Instant dayInst = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Date startDate = Date.from(dayInst);
        LocalDateTime localDateTime = LocalDateTime
                .of(filter.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
        Date endDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_SALE_BY_DELIVERY", SaleByDeliveryTypeDTO.class);
        query.registerStoredProcedureParameter("DELIVERY_TYPE", void.class,  ParameterMode.REF_CURSOR);
//        query.registerStoredProcedureParameter("transCode", String.class,  ParameterMode.IN);
        query.registerStoredProcedureParameter("fromDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toDate", Date.class, ParameterMode.IN);
//        query.registerStoredProcedureParameter("reason", String.class, ParameterMode.IN);
//        query.registerStoredProcedureParameter("productKW", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("shopId", Integer.class, ParameterMode.IN);


//        query.setParameter("transCode",filter.getTransCode());
        query.setParameter("fromDate", startDate);
        query.setParameter("toDate", endDate);
//        query.setParameter("reason", filter.getReason());
//        query.setParameter("productKW", filter.getProductKW());
        query.setParameter("shopId", Integer.valueOf(filter.getShopId().toString()));
        query.execute();
        List<SaleByDeliveryTypeDTO> reportDTOS = query.getResultList();
        return reportDTOS;
    }
}
