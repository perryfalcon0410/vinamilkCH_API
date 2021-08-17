package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;

import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.entities.StockCounting;
import vn.viettel.sale.entities.StockCounting_;
import java.time.LocalDateTime;

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
    public static Specification<StockCounting> hasShopId(Long shopId) {
        return (root, query, criteriaBuilder) -> {
            if (shopId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(StockCounting_.shopId),  shopId);
        };
    }
    public static Specification<StockCounting> hasFromDateToDate(LocalDateTime fromDate, LocalDateTime toDate) {
        LocalDateTime tsFromDate = DateUtils.convertFromDate(fromDate);
        LocalDateTime tsToDate = DateUtils.convertToDate(toDate);
        return (root, query, criteriaBuilder) ->{
            if (fromDate == null && toDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(StockCounting_.countingDate), tsToDate);
            }
            if (toDate == null && fromDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(StockCounting_.countingDate), tsFromDate);
            }
            if(fromDate == null && toDate == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(StockCounting_.countingDate), tsFromDate, tsToDate);
        };

    }
}
