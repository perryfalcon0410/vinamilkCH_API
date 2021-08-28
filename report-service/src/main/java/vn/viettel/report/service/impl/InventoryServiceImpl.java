package vn.viettel.report.service.impl;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.report.messaging.InventoryImportExportFilter;
import vn.viettel.report.service.InventoryService;
import vn.viettel.report.service.dto.ImportExportInventoryDTO;
import vn.viettel.report.service.dto.ImportExportInventoryTotalDTO;
import vn.viettel.report.service.dto.PrintInventoryCatDTO;
import vn.viettel.report.service.dto.PrintInventoryDTO;
import vn.viettel.report.service.excel.ImportExportInventoryExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    ShopClient shopClient;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public ByteArrayInputStream exportImportExcel(InventoryImportExportFilter filter) throws IOException {
        PrintInventoryDTO inventoryDTO = this.getDataExcel(filter);
        ImportExportInventoryExcel excel = new ImportExportInventoryExcel( inventoryDTO.getShop().getParentShop(), inventoryDTO, filter);
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
    public PrintInventoryDTO getDataPrint(InventoryImportExportFilter filter) throws ParseException {

        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        PrintInventoryDTO printInventoryDTO = new PrintInventoryDTO(filter.getFromDate(), filter.getToDate(), shopDTO);
        List<ImportExportInventoryDTO> inventoryDTOS = this.callStoreProcedure(filter);
        if (inventoryDTOS.isEmpty()) return printInventoryDTO;

        ImportExportInventoryDTO total = inventoryDTOS.get(inventoryDTOS.size() - 1);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        printInventoryDTO.setTotal(modelMapper.map(total, ImportExportInventoryTotalDTO.class));

        this.removeDataList(inventoryDTOS);

        Map<Long, List<ImportExportInventoryDTO>> maps = inventoryDTOS.stream().collect(Collectors.groupingBy(ImportExportInventoryDTO::getCatId));
        List<PrintInventoryCatDTO> cats = new ArrayList<>();

        for (Map.Entry<Long, List<ImportExportInventoryDTO>> entry : maps.entrySet()) {
            PrintInventoryCatDTO cat = new PrintInventoryCatDTO();
            ImportExportInventoryDTO defaultDTO = entry.getValue().get(0);
            cat.setCatId(defaultDTO.getCatId());
            cat.setCatName(defaultDTO.getCatName());
            for(ImportExportInventoryDTO inventoryDTO: entry.getValue()) {
                cat.addBeginningQty(inventoryDTO.getBeginningQty());
                cat.addBeginningAmount(inventoryDTO.getBeginningAmount());
                cat.addImpTotalQty(inventoryDTO.getImpTotalQty());
                cat.addImpQty(inventoryDTO.getImpQty());
                cat.addImpAmount(inventoryDTO.getImpAmount());
                cat.addImpAdjustmentQty(inventoryDTO.getImpAdjustmentQty());
                cat.addImpAdjustmentAmount(inventoryDTO.getImpAdjustmentAmount());
                cat.addExpTotalQty(inventoryDTO.getExpTotalQty());
                cat.addExpSalesQty(inventoryDTO.getExpSalesQty());
                cat.addExpSalesAmount(inventoryDTO.getExpSalesAmount());
                cat.addExpPromotionQty(inventoryDTO.getExpPromotionQty());
                cat.addExpPromotionAmount(inventoryDTO.getExpPromotionAmount());
                cat.addExpAdjustmentQty(inventoryDTO.getExpAdjustmentQty());
                cat.addExpAdjustmentAmount(inventoryDTO.getExpAdjustmentAmount());
                cat.addExpExchangeQty(inventoryDTO.getExpExchangeQty());
                cat.addExpExchangeAmount(inventoryDTO.getExpExchangeAmount());
                cat.addEndingQty(inventoryDTO.getEndingQty());
                cat.addEndingAmount(inventoryDTO.getEndingAmount());
            }
            cat.setProducts(entry.getValue());
            cats.add(cat);
        }

        Collections.sort(cats, Comparator.comparing(PrintInventoryCatDTO::getCatName));

        printInventoryDTO.setCats(cats);
        return printInventoryDTO;
    }

    private List<ImportExportInventoryDTO> callStoreProcedure(InventoryImportExportFilter filter) {

        String upperCode = filter.getProductCodes()==null?filter.getProductCodes():filter.getProductCodes().toUpperCase();

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_INVENTORY", ImportExportInventoryDTO.class);
        query.registerStoredProcedureParameter("results", void.class, ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("shopId", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("warehouseTypeId", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromDate", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toDate", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("productCodes", String.class, ParameterMode.IN);

        query.setParameter("shopId", filter.getShopId());
        query.setParameter("warehouseTypeId", filter.getWarehouseTypeId());
        query.setParameter("fromDate", filter.getFromDate());
        query.setParameter("toDate", filter.getToDate());
        query.setParameter("productCodes", upperCode);

        query.execute();

        List<ImportExportInventoryDTO> reportDTOS = query.getResultList();
        entityManager.close();
        return reportDTOS;
    }

    public PrintInventoryDTO getDataExcel(InventoryImportExportFilter filter) {
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
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

    private void removeDataList(List<ImportExportInventoryDTO> inventoryDTOS) {
        inventoryDTOS.remove(inventoryDTOS.size() - 1);
        inventoryDTOS.remove(inventoryDTOS.size() - 1);
    }

}
