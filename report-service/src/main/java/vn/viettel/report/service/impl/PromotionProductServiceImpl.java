package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.PromotionProductFilter;
import vn.viettel.report.service.PromotionProductService;
import vn.viettel.report.service.dto.PromotionProductReportDTO;
import vn.viettel.report.service.dto.PromotionProductTotalDTO;
import vn.viettel.report.service.excel.PromotionProductExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PromotionProductServiceImpl implements PromotionProductService {

    @Autowired
    ShopClient shopClient;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public ByteArrayInputStream exportExcel(PromotionProductFilter filter) throws IOException {
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        List<PromotionProductReportDTO> promotions = this.callStoreProcedure(
                filter.getShopId(), filter.getOnlineNumber(), filter.getFromDate(), filter.getToDate(), filter.getProductIds());
        PromotionProductReportDTO promotionTotal = new PromotionProductReportDTO();
        if(!promotions.isEmpty()) {
            promotionTotal = promotions.get(promotions.size() -1);
            this.removeDataList(promotions);
        }
        PromotionProductExcel excel = new PromotionProductExcel(shopDTO, promotions, promotionTotal);
            excel.setFromDate(filter.getFromDate());
            excel.setToDate(filter.getToDate());
        return excel.export();
    }

    @Override
    public Response<CoverResponse<Page<PromotionProductReportDTO>, PromotionProductTotalDTO>> getReportPromotionProducts(
                                                                            PromotionProductFilter filter, Pageable pageable) {
        List<PromotionProductReportDTO> promotions = this.callStoreProcedure(
                filter.getShopId(), filter.getOnlineNumber(), filter.getFromDate(), filter.getToDate(), filter.getProductIds());
        PromotionProductTotalDTO totalDTO = new PromotionProductTotalDTO();
        List<PromotionProductReportDTO> subList = new ArrayList<>();

        if(!promotions.isEmpty()) {
            PromotionProductReportDTO total = promotions.get(promotions.size() -1);
            totalDTO.setTotalQuantity(total.getQuantity());
            totalDTO.setTotalPrice(total.getTotalPrice());

            this.removeDataList(promotions);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), promotions.size());
            subList = promotions.subList(start, end);
        }

        Page<PromotionProductReportDTO> page = new PageImpl<>( subList, pageable, promotions.size());
        CoverResponse response = new CoverResponse(page, totalDTO);

        return new Response<CoverResponse<Page<PromotionProductReportDTO>, PromotionProductTotalDTO>>().withData(response);
    }

    private List<PromotionProductReportDTO> callStoreProcedure(Long shopId, String onlineNumber, Date fromDate, Date toDate, String productIds) {

        Instant inst = fromDate.toInstant();
        LocalDate localDate = inst.atZone(ZoneId.systemDefault()).toLocalDate();
        Instant dayInst = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Date startDate = Date.from(dayInst);

        LocalDateTime localDateTime = LocalDateTime
                .of(toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
        Date endDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_PROMOTION_PRODUCTS", PromotionProductReportDTO.class);
        query.registerStoredProcedureParameter("promotionDetails", void.class,  ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("shopId", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("onlineNumber", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("productIds", String.class, ParameterMode.IN);

        query.setParameter("shopId", Integer.valueOf(shopId.toString()));
        query.setParameter("onlineNumber", onlineNumber);
        query.setParameter("fromDate", startDate);
        query.setParameter("toDate", endDate);
        query.setParameter("productIds", productIds);

        query.execute();

        List<PromotionProductReportDTO> reportDTOS = query.getResultList();
        return reportDTOS;
    }

    private void removeDataList(List<PromotionProductReportDTO> promotions) {
        promotions.remove(promotions.size()-1);
        promotions.remove(promotions.size()-1);
    }


}
