package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.InventoryImportExportFilter;
import vn.viettel.report.service.InventoryService;
import vn.viettel.report.service.dto.ImportExportInventoryDTO;
import vn.viettel.report.service.dto.ImportExportInventoryTotalDTO;
import vn.viettel.report.service.excel.ImportExportInventoryExcel;
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
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    ShopClient shopClient;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public ByteArrayInputStream exportImportExcel(Long shopId) throws IOException {
        ShopDTO shopDTO = shopClient.getShopByIdV1(shopId).getData();
        ImportExportInventoryExcel excel = new ImportExportInventoryExcel(shopDTO);
        return excel.export();
    }

    @Override
    public Response<CoverResponse<Page<ImportExportInventoryDTO>, ImportExportInventoryTotalDTO>> getReportInventoryImportExport(InventoryImportExportFilter filter, Pageable pageable) {
        List<ImportExportInventoryDTO> inventoryDTOS = this.callStoreProcedure(filter);
        ImportExportInventoryTotalDTO inventoryTotalDTO = new ImportExportInventoryTotalDTO();
        List<ImportExportInventoryDTO> subList = new ArrayList<>();

        if(!inventoryDTOS.isEmpty()) {
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), inventoryDTOS.size());
            subList = inventoryDTOS.subList(start, end);
        }

        Page<ImportExportInventoryDTO> page = new PageImpl<>( subList, pageable, inventoryDTOS.size());
        CoverResponse response = new CoverResponse(page, inventoryTotalDTO);

        return new Response<CoverResponse<Page<ImportExportInventoryDTO>, ImportExportInventoryTotalDTO>>().withData(response);
    }

    private List<ImportExportInventoryDTO> callStoreProcedure(InventoryImportExportFilter filter) {

        Instant inst = filter.getFromDate().toInstant();
        LocalDate localDate = inst.atZone(ZoneId.systemDefault()).toLocalDate();
        Instant dayInst = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Date startDate = Date.from(dayInst);

        LocalDateTime localDateTime = LocalDateTime
                .of(filter.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
        Date endDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_INVENTORY", ImportExportInventoryDTO.class);
        query.registerStoredProcedureParameter("results", void.class,  ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("shopId", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("warehouseTypeId", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("productIds", String.class, ParameterMode.IN);

        query.setParameter("shopId", Integer.valueOf(filter.getShopId().toString()));
        query.setParameter("warehouseTypeId", 1);
        query.setParameter("fromDate", startDate);
        query.setParameter("toDate", endDate);
        query.setParameter("productIds", filter.getProductIds());

        query.execute();

        List<ImportExportInventoryDTO> reportDTOS = query.getResultList();
        return reportDTOS;
    }

}
