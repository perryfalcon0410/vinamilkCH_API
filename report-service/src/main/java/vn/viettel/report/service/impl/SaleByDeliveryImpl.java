package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.report.service.dto.*;
import vn.viettel.report.messaging.SaleDeliveryTypeFilter;
import vn.viettel.report.service.SaleDeliveryTypeService;
import vn.viettel.report.service.excel.SaleDeliveryTypeExcel;
import vn.viettel.report.service.feign.CommonClient;
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
import java.util.Locale;

@Service
public class SaleByDeliveryImpl implements SaleDeliveryTypeService {
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    ShopClient shopClient;
    @Autowired
    CommonClient commonClient;

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
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_SALES_BY_DELIVERY", SaleByDeliveryTypeDTO.class);
        query.registerStoredProcedureParameter("DELIVERY_TYPE", void.class,  ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("fromDate", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toDate", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("shopId", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("orderNumber", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("apValue", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("customerKW", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("phoneText", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromTotal", Float.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toTotal", Float.class, ParameterMode.IN);

        query.setParameter("fromDate", DateUtils.convertFromDate(filter.getFromDate()));
        query.setParameter("toDate", DateUtils.convertToDate(filter.getToDate()));
        query.setParameter("shopId", Integer.valueOf(filter.getShopId().toString()));
        query.setParameter("orderNumber", filter.getOrderNumber());
        query.setParameter("apValue", filter.getApValue());
        query.setParameter("customerKW", VNCharacterUtils.removeAccent(filter.getCustomerKW()).trim().toUpperCase(Locale.ROOT));
        query.setParameter("phoneText", filter.getPhoneText());
        query.setParameter("fromTotal", filter.getFromTotal());
        query.setParameter("toTotal", filter.getToTotal());
        query.execute();
        List<SaleByDeliveryTypeDTO> reportDTOS = query.getResultList();
        return reportDTOS;
    }

    @Override
    public CoverResponse<Page<SaleByDeliveryTypeDTO>, SaleDeliTypeTotalDTO> getSaleDeliType(SaleDeliveryTypeFilter filter, Pageable pageable) {
        List<SaleByDeliveryTypeDTO> saleByDeliveryTypeList = this.callStoreProcedure(filter);
        SaleDeliTypeTotalDTO totalDTO = new SaleDeliTypeTotalDTO();
        List<SaleByDeliveryTypeDTO> subList = new ArrayList<>();
        if(!saleByDeliveryTypeList.isEmpty()) {
            SaleByDeliveryTypeDTO total = saleByDeliveryTypeList.get(saleByDeliveryTypeList.size()-1);
            totalDTO.setSaleOrder(total.getCountOrderNumber());
            totalDTO.setTotalAmount(total.getAmount());
            totalDTO.setAllTotal(total.getTotal());

            this.removeDataList(saleByDeliveryTypeList);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), saleByDeliveryTypeList.size());
            subList = saleByDeliveryTypeList.subList(start, end);
        }
        Page<SaleByDeliveryTypeDTO> page = new PageImpl<>( subList, pageable, saleByDeliveryTypeList.size());
        CoverResponse response = new CoverResponse(page, totalDTO);
        return response;
    }

    public List<ApParamDTO> deliveryType(){
        List<ApParamDTO> list = commonClient.getApParamByTypeV1("SALEMT_DELIVERY_TYPE").getData();
        return list;
    }

    private void removeDataList(List<SaleByDeliveryTypeDTO> listRemove) {
        listRemove.remove(listRemove.size()-1);
        listRemove.remove(listRemove.size()-1);
    }
}
