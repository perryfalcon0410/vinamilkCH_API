package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.entities.SaleOrder_;
import vn.viettel.sale.entities.StockCounting;
import vn.viettel.sale.entities.StockCounting_;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class InventorySpecification {

    public static Specification<StockCounting> hasCountingCode(String countingCode) {
        return (root, query, criteriaBuilder) -> {
            if (countingCode == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like( criteriaBuilder.upper(root.get(StockCounting_.stockCountingCode)), "%" + countingCode.trim().toUpperCase() + "%");
        };
    }
    public static Specification<StockCounting> hasWareHouse(Long warehouseTypeId) {
        return (root, query, criteriaBuilder) -> {
            if (warehouseTypeId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(StockCounting_.wareHouseTypeId),  warehouseTypeId);
        };
    }
    public static Specification<StockCounting> hasFromDateToDate(Date fromDate, Date toDate) {
        Timestamp tsFromDate = DateUtils.convertFromDate(fromDate);
        Timestamp tsToDate = DateUtils.convertToDate(toDate);

        return (root, query, criteriaBuilder) -> {
            if (fromDate == null && toDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(StockCounting_.countingDate), tsToDate);
            }
            if (toDate == null && fromDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(StockCounting_.countingDate), tsFromDate);
            }
            if (fromDate == null && toDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(StockCounting_.countingDate), tsFromDate, tsToDate);
        };

    }
}
