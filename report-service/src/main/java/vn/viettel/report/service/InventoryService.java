package vn.viettel.report.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface InventoryService {
    ByteArrayInputStream exportImportExcel(Long shopId) throws IOException;
}
