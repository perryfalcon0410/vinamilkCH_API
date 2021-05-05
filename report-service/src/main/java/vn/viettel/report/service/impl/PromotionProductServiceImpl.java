package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.service.PromotionProductService;
import vn.viettel.report.service.dto.PromotionProductReportDTO;
import vn.viettel.report.service.excel.PromotionProductExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class PromotionProductServiceImpl implements PromotionProductService {

    @Autowired
    ShopClient shopClient;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public ByteArrayInputStream exportExcel(Long shopId) throws IOException {
        ShopDTO shopDTO = shopClient.getShopByIdV1(shopId).getData();

        PromotionProductExcel excel = new PromotionProductExcel(shopDTO);
        return excel.export();
    }

    public Response<List<PromotionProductReportDTO>> callStoreProcedure(Long shopId, String onlineNumber, Date fromDate, Date toDate, String productIds) {

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_PROMOTION_PRODUCTS", PromotionProductReportDTO.class);
        query.registerStoredProcedureParameter("promotionDetails", void.class,  ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("shopId", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("onlineNumber", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("productIds", String.class, ParameterMode.IN);

        query.setParameter("shopId", 1);
        query.setParameter("onlineNumber", onlineNumber);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        query.setParameter("productIds", productIds);

        query.execute();

        List<PromotionProductReportDTO> reportDTOS = query.getResultList();
        return new Response<List<PromotionProductReportDTO>>().withData(reportDTOS);
    }
}
