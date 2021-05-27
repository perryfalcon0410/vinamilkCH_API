package vn.viettel.report.service.impl;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.messaging.InventoryImportExportFilter;
import vn.viettel.report.service.InventoryService;
import vn.viettel.report.service.dto.ImportExportInventoryDTO;
import vn.viettel.report.service.dto.ImportExportInventoryTotalDTO;
import vn.viettel.report.service.dto.PrintInventoryDTO;
import vn.viettel.report.service.excel.ImportExportInventoryExcel;
import vn.viettel.report.service.feign.CustomerTypeClient;
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

    @Autowired
    CustomerTypeClient customerTypeClient;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public ByteArrayInputStream exportImportExcel(InventoryImportExportFilter filter) throws IOException {
        PrintInventoryDTO inventoryDTO = this.getDataPrint(filter);
        ImportExportInventoryExcel excel = new ImportExportInventoryExcel(inventoryDTO, filter);
        return excel.export();
    }

    @Override
    public CoverResponse<Page<ImportExportInventoryDTO>, ImportExportInventoryTotalDTO> getReportInventoryImportExport(InventoryImportExportFilter filter, Pageable pageable) {
        List<ImportExportInventoryDTO> inventoryDTOS = this.callStoreProcedure(filter);
        ImportExportInventoryTotalDTO inventoryTotalDTO = new ImportExportInventoryTotalDTO();
        List<ImportExportInventoryDTO> subList = new ArrayList<>();

        if (!inventoryDTOS.isEmpty()) {
            ImportExportInventoryDTO inventoryDTO = inventoryDTOS.get(inventoryDTOS.size() - 1);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            inventoryTotalDTO = modelMapper.map(inventoryDTO, ImportExportInventoryTotalDTO.class);
            this.removeDataList(inventoryDTOS);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), inventoryDTOS.size());
            subList = inventoryDTOS.subList(start, end);
        }

        Page<ImportExportInventoryDTO> page = new PageImpl<>(subList, pageable, inventoryDTOS.size());
        CoverResponse response = new CoverResponse(page, inventoryTotalDTO);

        return response;
    }

    @Override
    public PrintInventoryDTO getDataPrint(InventoryImportExportFilter filter) {

        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        PrintInventoryDTO printInventoryDTO = new PrintInventoryDTO(filter.getFromDate(), filter.getToDate(), shopDTO);
        List<ImportExportInventoryDTO> inventoryDTOS = this.callStoreProcedure(filter);
        if (!inventoryDTOS.isEmpty()) {
            ImportExportInventoryDTO total = inventoryDTOS.get(inventoryDTOS.size() - 1);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            printInventoryDTO.setTotal(modelMapper.map(total, ImportExportInventoryTotalDTO.class));
            this.removeDataList(inventoryDTOS);
            printInventoryDTO.setProducts(inventoryDTOS);
        }

        return printInventoryDTO;
    }

    private List<ImportExportInventoryDTO> callStoreProcedure(InventoryImportExportFilter filter) {

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_INVENTORY", ImportExportInventoryDTO.class);
        query.registerStoredProcedureParameter("results", void.class, ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("shopId", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toDate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("productCodes", String.class, ParameterMode.IN);

        query.setParameter("shopId", filter.getShopId());
        query.setParameter("fromDate", filter.getFromDate());
        query.setParameter("toDate", filter.getToDate());
        query.setParameter("productCodes", filter.getProductCodes());

        query.execute();

        List<ImportExportInventoryDTO> reportDTOS = query.getResultList();
        return reportDTOS;
    }

    private void removeDataList(List<ImportExportInventoryDTO> inventoryDTOS) {
        inventoryDTOS.remove(inventoryDTOS.size() - 1);
        inventoryDTOS.remove(inventoryDTOS.size() - 1);
    }
}
