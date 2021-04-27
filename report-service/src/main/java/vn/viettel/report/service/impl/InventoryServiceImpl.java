package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.service.InventoryService;
import vn.viettel.report.service.excel.ImportExportInventoryExcel;
import vn.viettel.report.service.feign.ShopClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    ShopClient shopClient;

    @Override
    public ByteArrayInputStream exportImportExcel(Long shopId) throws IOException {
        ShopDTO shopDTO = shopClient.getById(shopId).getData();
        ImportExportInventoryExcel excel = new ImportExportInventoryExcel(shopDTO);
        return excel.export();
    }
}
