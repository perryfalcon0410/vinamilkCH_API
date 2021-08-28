package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.report.messaging.ReportVoucherFilter;
import vn.viettel.report.service.ReportVoucherService;
import vn.viettel.report.service.dto.ReportVoucherDTO;
import vn.viettel.report.service.excel.ReportVoucherExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ReportVoucherServiceImpl implements ReportVoucherService {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ShopClient shopClient;

    @Override
    public Page<ReportVoucherDTO> index(ReportVoucherFilter filter, Pageable pageable) {
        List<ReportVoucherDTO> lst = callReportVoucherDTO(filter);
        List<ReportVoucherDTO> subList = new ArrayList<>();

        if(!lst.isEmpty())
        {
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), lst.size());
            subList = lst.subList(start, end);
        }

        Page<ReportVoucherDTO>  page = new PageImpl<>(subList, pageable, lst.size());
        return page;
    }

    @Override
    public ByteArrayInputStream exportExcel(ReportVoucherFilter filter) throws IOException {
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        List<ReportVoucherDTO> lst = this.callReportVoucherDTO(filter);
        ReportVoucherExcel reportVoucherExcel = new ReportVoucherExcel(shopDTO, shopDTO.getParentShop(), lst);
        reportVoucherExcel.setFromDate(filter.getFromProgramDate());
        reportVoucherExcel.setToDate(filter.getToProgramDate());

        return reportVoucherExcel.export();
    }

    public List<ReportVoucherDTO> callReportVoucherDTO(ReportVoucherFilter filter)
    {
        if(filter.getCustomerKeywords()!=null) filter.setCustomerKeywords(VNCharacterUtils.removeAccent(filter.getCustomerKeywords()).toUpperCase(Locale.ROOT));

        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_VOUCHER", ReportVoucherDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(4, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(5, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(7, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(8, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(9, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(10, Long.class, ParameterMode.IN);

        storedProcedure.setParameter(2, filter.getFromProgramDate());
        storedProcedure.setParameter(3, filter.getToProgramDate());
        storedProcedure.setParameter(4, filter.getFromUseDate());
        storedProcedure.setParameter(5, filter.getToUseDate());
        storedProcedure.setParameter(6, filter.getVoucherProgramName());
        storedProcedure.setParameter(7, filter.getVoucherKeywords());
        storedProcedure.setParameter(8, filter.getCustomerKeywords());
        storedProcedure.setParameter(9, filter.getCustomerMobiPhone());
        storedProcedure.setParameter(10, filter.getShopId());
        storedProcedure.execute();
        List<ReportVoucherDTO> response = storedProcedure.getResultList();
        entityManager.close();
        return response;
    }
}
