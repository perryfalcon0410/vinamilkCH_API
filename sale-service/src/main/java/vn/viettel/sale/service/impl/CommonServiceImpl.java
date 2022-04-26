package vn.viettel.sale.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.enums.receipt.ImportType;
import vn.viettel.core.db.entity.enums.receipt.PoComfirmStatus;
import vn.viettel.core.db.entity.enums.receipt.StockAdjustmentStatus;
import vn.viettel.core.db.entity.enums.receipt.StockBorrowingStatus;
import vn.viettel.sale.service.CommonService;
import vn.viettel.sale.service.dto.ImportTypeDTO;
import vn.viettel.sale.service.dto.PoConfirmStatusDTO;
import vn.viettel.sale.service.dto.StockAdjustmentStatusDTO;
import vn.viettel.sale.service.dto.StockBorrowingStatusDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommonServiceImpl implements CommonService {
    @Override
    public List<ImportTypeDTO> getList() {
        List<ImportTypeDTO> importTypeDTOS = new ArrayList<>();
        for (int i = 0; i < ImportType.values().length; i++) {
            ImportTypeDTO importTypeDTO = new ImportTypeDTO(i, ImportType.getValueOf(i));
            importTypeDTOS.add(importTypeDTO);
        }
        return importTypeDTOS;
    }

    @Override
    public List<PoConfirmStatusDTO> getListPoConfirmStatusDTO() {
        List<PoConfirmStatusDTO> poConfirmStatusDTOS = new ArrayList<>();
        for (int i = 0; i < PoComfirmStatus.values().length; i++) {
            PoConfirmStatusDTO poConfirmStatusDTO = new PoConfirmStatusDTO(i, PoComfirmStatus.getValueOf(i));
            poConfirmStatusDTOS.add(poConfirmStatusDTO);
        }
        return poConfirmStatusDTOS;
    }

    @Override
    public List<StockAdjustmentStatusDTO> getListStockAdjustmentTypeDTO() {
        List<StockAdjustmentStatusDTO> stockAdjustmentTypeDTOS = new ArrayList<>();
        for (int i = 0; i < StockAdjustmentStatus.values().length; i++) {
            StockAdjustmentStatusDTO stockAdjustmentTypeDTO = new StockAdjustmentStatusDTO(i, StockAdjustmentStatus.getValueOf(i));
            stockAdjustmentTypeDTOS.add(stockAdjustmentTypeDTO);
        }
        return stockAdjustmentTypeDTOS;
    }

    @Override
    public List<StockBorrowingStatusDTO> getListStockBorrowingTypeDTO() {
        List<StockBorrowingStatusDTO> stockBorrowingStatusDTOS = new ArrayList<>();
        for (int i = 1; i <= StockBorrowingStatus.values().length; i++) {
            StockBorrowingStatusDTO stockBorrowingStatusDTO = new StockBorrowingStatusDTO(i, StockBorrowingStatus.getValueOf(i));
            stockBorrowingStatusDTOS.add(stockBorrowingStatusDTO);
        }
        return stockBorrowingStatusDTOS;
    }
}
