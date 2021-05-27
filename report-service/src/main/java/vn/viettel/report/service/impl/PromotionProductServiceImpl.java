package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.report.messaging.PromotionProductFilter;
import vn.viettel.report.service.PromotionProductService;
import vn.viettel.report.service.dto.PromotionProductCatDTO;
import vn.viettel.report.service.dto.PromotionProductDTO;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PromotionProductServiceImpl implements PromotionProductService {

    @Autowired
    ShopClient shopClient;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public ByteArrayInputStream exportExcel(PromotionProductFilter filter) throws IOException {
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        List<PromotionProductDTO> promotions = this.callStoreProcedure(filter);
        PromotionProductDTO promotionTotal = new PromotionProductDTO();
        if(!promotions.isEmpty()) {
            promotionTotal = promotions.get(promotions.size() -1);
            this.removeDataList(promotions);
        }
        PromotionProductExcel excel = new PromotionProductExcel(shopDTO, promotions, promotionTotal, filter);

        return excel.export();
    }

    @Override
    public PromotionProductReportDTO getDataPrint(PromotionProductFilter filter) {
        List<PromotionProductDTO> promotions = this.callStoreProcedure(filter);
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        PromotionProductReportDTO reportDTO = new PromotionProductReportDTO(filter.getFromDate(), filter.getToDate(), shopDTO);

        if(!promotions.isEmpty()) {
            PromotionProductDTO reportTotal = promotions.get(promotions.size() -1);
            reportDTO.setTotalQuantity(reportTotal.getQuantity());
            reportDTO.setTotalPrice(reportTotal.getTotalPrice());
            this.removeDataList(promotions);
            Set<String> productCats =  promotions.stream().map(PromotionProductDTO::getProductCatName).collect(Collectors.toSet());
            for (String catName: productCats) {
                PromotionProductCatDTO productCatDTO = new PromotionProductCatDTO(catName);
                for(PromotionProductDTO product: promotions) {
                    if(product.getProductCatName().equals(catName)) {
                        productCatDTO.addProduct(product);
                        productCatDTO.addTotalQuantity(product.getQuantity());
                        productCatDTO.addTotalTotalPrice(product.getTotalPrice());
                    }
                }
                reportDTO.addProductCat(productCatDTO);
            }
        }

        return reportDTO;
    }

    @Override
    public CoverResponse<Page<PromotionProductDTO>, PromotionProductTotalDTO> getReportPromotionProducts(
                                                                            PromotionProductFilter filter, Pageable pageable) {
        List<PromotionProductDTO> promotions = this.callStoreProcedure(filter);
        PromotionProductTotalDTO totalDTO = new PromotionProductTotalDTO();
        List<PromotionProductDTO> subList = new ArrayList<>();

        if(!promotions.isEmpty()) {
            PromotionProductDTO total = promotions.get(promotions.size() -1);
            totalDTO.setTotalQuantity(total.getQuantity());
            totalDTO.setTotalPrice(total.getTotalPrice());

            this.removeDataList(promotions);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), promotions.size());
            subList = promotions.subList(start, end);
        }
        Page<PromotionProductDTO> page = new PageImpl<>( subList, pageable, promotions.size());
        CoverResponse response = new CoverResponse(page, totalDTO);

        return response;
    }

    private List<PromotionProductDTO> callStoreProcedure(PromotionProductFilter filter) {

        String keySearchUpper = VNCharacterUtils.removeAccent(filter.getOrderNumber().toUpperCase(Locale.ROOT));

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_PROMOTION_PRODUCTS", PromotionProductDTO.class);
        query.registerStoredProcedureParameter("promotionDetails", void.class,  ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("shopId", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("orderNumber", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("productCodes", String.class, ParameterMode.IN);

        query.setParameter("shopId", filter.getShopId());
        query.setParameter("orderNumber", keySearchUpper);
        query.setParameter("fromDate", filter.getFromDate());
        query.setParameter("toDate", filter.getToDate());
        query.setParameter("productCodes", filter.getProductCodes());

        query.execute();

        List<PromotionProductDTO> reportDTOS = query.getResultList();
        return reportDTOS;
    }

    private void removeDataList(List<PromotionProductDTO> promotions) {
        promotions.remove(promotions.size()-1);
        promotions.remove(promotions.size()-1);
    }

}
