package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.db.entity.stock.PoTrans;
import vn.viettel.core.db.entity.stock.PoTrans_;
import vn.viettel.core.db.entity.stock.StockCounting;
import vn.viettel.core.db.entity.stock.StockCounting_;

import java.util.Date;

public class InventorySpecification {

    public static Specification<StockCounting> hasCountingCode(String countingCode) {
        return (root, query, criteriaBuilder) -> {
            if (countingCode == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(StockCounting_.stockCountingCode), "%" + countingCode + "%");
        };
    }
    public static Specification<StockCounting> hasFromDateToDate(Date fromDate, Date toDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null) {
                return criteriaBuilder.conjunction();
            }if (toDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(StockCounting_.countingDate), fromDate, toDate);
        };

    }
}
