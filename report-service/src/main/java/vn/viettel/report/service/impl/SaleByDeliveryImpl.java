package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.service.BaseReportServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.report.messaging.SaleDeliveryTypeFilter;
import vn.viettel.report.service.SaleDeliveryTypeService;
import vn.viettel.report.service.dto.SaleByDeliveryTypeDTO;
import vn.viettel.report.service.dto.SaleDeliTypeTotalDTO;
import vn.viettel.report.service.excel.SaleDeliveryTypeExcel;
import vn.viettel.report.service.feign.CommonClient;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class SaleByDeliveryImpl extends BaseReportServiceImpl implements SaleDeliveryTypeService {
    @Autowired
    ShopClient shopClient;
    @Autowired
    CommonClient commonClient;

    @Override
    public ByteArrayInputStream exportExcel(SaleDeliveryTypeFilter filter) throws IOException {
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        List<SaleByDeliveryTypeDTO> salesDeli = this.callStoreProcedure(filter);
        SaleDeliveryTypeExcel excel = new SaleDeliveryTypeExcel(shopDTO, shopDTO.getParentShop(), salesDeli);
        excel.setFromDate(filter.getFromDate());
        excel.setToDate(filter.getToDate());
        return excel.export();
    }

    public List<SaleByDeliveryTypeDTO> callStoreProcedure(SaleDeliveryTypeFilter filter) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_SALES_BY_DELIVERY", SaleByDeliveryTypeDTO.class);
        query.registerStoredProcedureParameter("DELIVERY_TYPE", void.class,  ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("fromDate", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toDate", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("shopId", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("orderNumber", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("apValue", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("customerKW", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("phoneText", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromTotal", Float.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toTotal", Float.class, ParameterMode.IN);

        query.setParameter("fromDate", filter.getFromDate());
        query.setParameter("toDate", filter.getToDate());
        query.setParameter("shopId", filter.getShopId());
        query.setParameter("orderNumber", VNCharacterUtils.removeAccent(filter.getOrderNumber()).toUpperCase(Locale.ROOT).trim());
        query.setParameter("apValue", filter.getApValue());
        query.setParameter("customerKW", VNCharacterUtils.removeAccent(filter.getCustomerKW()).trim().toUpperCase(Locale.ROOT));
        query.setParameter("phoneText", filter.getPhoneText().trim());
        query.setParameter("fromTotal", filter.getFromTotal());
        query.setParameter("toTotal", filter.getToTotal());

        this.executeQuery(query, "P_SALES_BY_DELIVERY", filter.toString());
        List<SaleByDeliveryTypeDTO> reportDTOS = query.getResultList();

        return reportDTOS;
    }

    @Override
    public CoverResponse<Page<SaleByDeliveryTypeDTO>, SaleDeliTypeTotalDTO> getSaleDeliType(SaleDeliveryTypeFilter filter, Pageable pageable) {
        List<SaleByDeliveryTypeDTO> saleByDeliveryTypeList = this.callStoreProcedure(filter);
        SaleDeliTypeTotalDTO totalDTO = new SaleDeliTypeTotalDTO();
        List<SaleByDeliveryTypeDTO> subList = new ArrayList<>();
        if(!saleByDeliveryTypeList.isEmpty()) {
            SaleByDeliveryTypeDTO total = saleByDeliveryTypeList.get(saleByDeliveryTypeList.size() -1);
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
