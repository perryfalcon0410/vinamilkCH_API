package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;

import vn.viettel.sale.entities.StockCounting;
import vn.viettel.sale.entities.StockCounting_;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
    public static Specification<StockCounting> hasWareHouse(Long warehouseTypeId) {
        return (root, query, criteriaBuilder) -> {
            if (warehouseTypeId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(StockCounting_.wareHouseTypeId),  warehouseTypeId);
        };
    }
    public static Specification<StockCounting> hasFromDateToDate(Date fromDate, Date toDate) {
        Timestamp tsFromDate = null;
        Timestamp tsToDate = null;
        if(fromDate != null) tsFromDate = new Timestamp(fromDate.getTime());
        if(toDate != null){
            LocalDateTime localDateTime = LocalDateTime
                    .of(toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
            tsToDate = Timestamp.valueOf(localDateTime);
        }

        Timestamp finalTsFromDate = tsFromDate;
        Timestamp finalTsToDate = tsToDate;
        return (root, query, criteriaBuilder) ->{
            if (fromDate == null && toDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(StockCounting_.countingDate), finalTsToDate);
            }
            if (toDate == null && fromDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(StockCounting_.countingDate), finalTsFromDate);
            }
            if(fromDate == null && toDate == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(StockCounting_.countingDate), finalTsFromDate, finalTsToDate);
        };

    }
}
