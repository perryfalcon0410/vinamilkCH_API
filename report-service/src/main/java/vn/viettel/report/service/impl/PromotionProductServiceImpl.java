package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.service.BaseReportServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.report.messaging.PromotionProductFilter;
import vn.viettel.report.service.PromotionProductService;
import vn.viettel.report.service.dto.PromotionProductCatDTO;
import vn.viettel.report.service.dto.PromotionProductDTO;
import vn.viettel.report.service.dto.PromotionProductReportDTO;
import vn.viettel.report.service.dto.PromotionProductTotalDTO;
import vn.viettel.report.service.excel.PromotionProductExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PromotionProductServiceImpl extends BaseReportServiceImpl implements PromotionProductService {

    @Autowired
    ShopClient shopClient;

    @Override
    public ByteArrayInputStream exportExcel(PromotionProductFilter filter) throws IOException, CloneNotSupportedException {
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        List<PromotionProductDTO> promotionDetails = this.callStoreProcedure(filter);
        PromotionProductDTO promotionTotal = new PromotionProductDTO();
        if(!promotionDetails.isEmpty()) {
            promotionTotal = promotionDetails.get(promotionDetails.size() -1);
            this.removeDataList(promotionDetails);
        }
        PromotionProductExcel excel = new PromotionProductExcel(shopDTO, shopDTO.getParentShop(), promotionTotal, filter);
        excel.setPromotionDetails(promotionDetails);
        excel.setPromotionIndays(this.promotionProductsDay(promotionDetails));
        excel.setPromotionproducts(this.promotionProducts(promotionDetails));

        return excel.export();
    }


    //data sheet 2
    private List<PromotionProductDTO> promotionProductsDay(List<PromotionProductDTO> promotions) throws CloneNotSupportedException {
        Map<String, PromotionProductDTO> maps = new HashMap<>();
        for(PromotionProductDTO promotion: promotions) {
            LocalDateTime dateTime = promotion.getOrderDate();
            String key = String.valueOf(dateTime.getYear()) + dateTime.getMonth() + dateTime.getDayOfMonth() + promotion.getProductCode();
            if(maps.containsKey(key)){
                PromotionProductDTO dto = maps.get(key);
                dto.setQuantity(dto.getQuantity() + promotion.getQuantity());
                dto.setTotalPrice(dto.getTotalPrice() + promotion.getTotalPrice());
                maps.put(key, dto);
            }else {
                maps.put(key, (PromotionProductDTO) promotion.clone());
            }
        }
        List<PromotionProductDTO> results = new ArrayList<>(maps.values()).stream().filter(e -> e.getQuantity() != null && e.getQuantity() > 0).collect(Collectors.toList());
        Collections.sort(results, Comparator.comparing(PromotionProductDTO::getOrderDate, Comparator.reverseOrder())
                .thenComparing(PromotionProductDTO::getProductCatName).thenComparing(PromotionProductDTO::getProductCode));

        return results;
    }

    //data sheet 3
    private List<PromotionProductDTO> promotionProducts(List<PromotionProductDTO> promotions) throws CloneNotSupportedException {
        Map<String, PromotionProductDTO> maps = new HashMap<>();
        for(PromotionProductDTO promotion: promotions) {
            if(maps.containsKey(promotion.getProductCode())) {
                PromotionProductDTO dto = maps.get(promotion.getProductCode());
                dto.setQuantity(dto.getQuantity() + promotion.getQuantity());
                dto.setTotalPrice(dto.getTotalPrice() + promotion.getTotalPrice());
                maps.put(promotion.getProductCode(), dto);
            }else{
                maps.put(promotion.getProductCode(), (PromotionProductDTO) promotion.clone());
            }
        }

        List<PromotionProductDTO> results = new ArrayList<>(maps.values()).stream().filter(e -> e.getQuantity() != null && e.getQuantity() > 0).collect(Collectors.toList());
        Collections.sort(results, Comparator.comparing(PromotionProductDTO::getProductCatName).thenComparing(PromotionProductDTO::getProductCode));
        return results;
    }


    @Override
    public PromotionProductReportDTO getDataPrint(PromotionProductFilter filter) throws ParseException {
        List<PromotionProductDTO> promotions = this.callStoreProcedure(filter);
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        PromotionProductReportDTO reportDTO = new PromotionProductReportDTO(DateUtils.convertToDate(filter.getFromDate()), DateUtils.convertToDate(filter.getToDate()), shopDTO);

        if(!promotions.isEmpty()) {
            PromotionProductDTO reportTotal = promotions.get(promotions.size() -1);
            reportDTO.setTotalQuantity(reportTotal.getQuantity());
            reportDTO.setTotalPrice(reportTotal.getTotalPrice());
            this.removeDataList(promotions);
            Set<String> productCats =  promotions.stream().map(PromotionProductDTO::getProductCatName).collect(Collectors.toSet());
            List<PromotionProductCatDTO> cats = new ArrayList<>();
            for (String catName: productCats) {
                PromotionProductCatDTO productCatDTO = new PromotionProductCatDTO(catName);
                for(PromotionProductDTO product: promotions) {
                    if(product.getProductCatName().equals(catName)) {
                        productCatDTO.addProduct(product);
                        productCatDTO.addTotalQuantity(product.getQuantity());
                        productCatDTO.addTotalTotalPrice(product.getTotalPrice());
                    }
                }
                cats.add(productCatDTO);
            }
            Collections.sort(cats, Comparator.comparing(PromotionProductCatDTO::getProductCatName));
            reportDTO.setProductCats(cats);
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
        String upperCode = filter.getProductCodes()==null?filter.getProductCodes():filter.getProductCodes().toUpperCase();

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_PROMOTION_PRODUCTS", PromotionProductDTO.class);
        query.registerStoredProcedureParameter("promotionDetails", void.class,  ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("shopId", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("orderNumber", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromDate", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toDate", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("productCodes", String.class, ParameterMode.IN);

        query.setParameter("shopId", filter.getShopId());
        query.setParameter("orderNumber", keySearchUpper);
        query.setParameter("fromDate", filter.getFromDate());
        query.setParameter("toDate", filter.getToDate());
        query.setParameter("productCodes", upperCode);

        this.executeQuery(query, "P_PROMOTION_PRODUCTS", filter.toString());

        List<PromotionProductDTO> reportDTOS = query.getResultList();

        return reportDTOS;
    }

    private void removeDataList(List<PromotionProductDTO> promotions) {
        promotions.remove(promotions.size() -1);
        promotions.remove(promotions.size() -1);
    }

}
