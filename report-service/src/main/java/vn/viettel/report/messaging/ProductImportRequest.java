package vn.viettel.report.messaging;

import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelCellName;
import com.poiji.annotation.ExcelRow;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductImportRequest {
    @ExcelRow
    private int rowIndex;
    @ExcelCell(0)
    private String productCode;
    @ExcelCell(1)
    private String productName;
}
