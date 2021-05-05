package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.service.PromotionProductService;
import vn.viettel.report.service.excel.PromotionProductExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
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

    public Response<List<Object>> callStoreProcedure(Long shopId) {

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_PROMOTION_PRODUCTS");
        query.registerStoredProcedureParameter("promotionDetails", Class.class,  ParameterMode.REF_CURSOR);
//        query.registerStoredProcedureParameter("onlineNumber", String.class, ParameterMode.IN);
////        query.registerStoredProcedureParameter("fromDate", Date.class, ParameterMode.IN);
////        query.registerStoredProcedureParameter("toDate", Date.class, ParameterMode.IN);
////        query.registerStoredProcedureParameter("productIds", String.class, ParameterMode.IN);
//
////        query.setParameter("onlineNumber", "");
////        query.setParameter("fromDate", new Date());
////        query.setParameter("toDate", new Date());
////        query.setParameter("productIds", "1,7");

        query.execute();
//
//        List<Object[]> postComments = query.getResultList();
        return new Response<List<Object>>().withData(null);
    }
}
