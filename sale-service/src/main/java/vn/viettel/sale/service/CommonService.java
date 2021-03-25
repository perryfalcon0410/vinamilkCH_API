package vn.viettel.sale.service;

import vn.viettel.sale.service.dto.ImportTypeDTO;
import vn.viettel.sale.service.dto.PoConfirmStatusDTO;
import vn.viettel.sale.service.dto.StockAdjustmentStatusDTO;

import java.util.List;

public interface CommonService {
    List<ImportTypeDTO> getList();
    List<PoConfirmStatusDTO> getListPoConfirmStatusDTO();
    List<StockAdjustmentStatusDTO> getListStockAdjustmentTypeDTO();
}
