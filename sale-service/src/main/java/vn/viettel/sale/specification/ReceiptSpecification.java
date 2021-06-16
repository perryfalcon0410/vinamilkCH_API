package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;

import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.entities.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class ReceiptSpecification {

    public static Specification<PoTrans> hasTypeImport() {
          return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PoTrans_.type), 1);
    }
    public static Specification<PoTrans> hasShopId(Long shopId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PoTrans_.shopId), shopId);
    }
    public static Specification<StockAdjustmentTrans> hasShopIdA(Long shopId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(StockAdjustmentTrans_.shopId), shopId);
    }
    public static Specification<StockBorrowingTrans> hasToShopId(Long shopId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(StockBorrowingTrans_.toShopId), shopId);
    }
    public static Specification<StockBorrowingTrans> hasFromShopId(Long shopId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(StockBorrowingTrans_.fromShopId), shopId);
    }
    public static Specification<PoTrans> hasTypeExport() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PoTrans_.type), 2);
    }
    public static Specification<PoTrans> hasStatus() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PoTrans_.status), 1);
    }
    public static Specification<PoTrans> hasPoIdIsNull() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get(PoTrans_.poId));
    }
    public static Specification<PoTrans> hasPoIdIsNotNull() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get(PoTrans_.poId));
    }
    public static Specification<StockAdjustmentTrans> hasTypeImportA() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(StockAdjustmentTrans_.type), 1);
    }
    public static Specification<StockAdjustmentTrans> hasTypeExportA() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(StockAdjustmentTrans_.type), 2);
    }
    public static Specification<StockAdjustmentTrans> hasStatusA() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(StockAdjustmentTrans_.status), 1);
    }
    public static Specification<StockAdjustmentTrans> hasTransCodeA(String transCode) {
        return (root, query, criteriaBuilder) -> {
            if (transCode == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.upper(root.get(StockAdjustmentTrans_.transCode)), "%" + transCode.toUpperCase() + "%");
        };
    }
    public static Specification<StockBorrowingTrans> hasTransCodeB(String transCode) {
        return (root, query, criteriaBuilder) -> {
            if (transCode == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.upper(root.get(StockBorrowingTrans_.transCode)), "%" + transCode.toUpperCase() + "%");
        };
    }
    public static Specification<StockBorrowingTrans> hasTypeExportB() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(StockBorrowingTrans_.type), 2);
    }
    public static Specification<StockBorrowingTrans> hasTypeImportB() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(StockBorrowingTrans_.type), 1);
    }
    public static Specification<StockBorrowingTrans> hasStatusB() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(StockBorrowingTrans_.status), 1);
    }
    public static Specification<PoTrans> hasFromDateToDate(LocalDateTime fromDate, LocalDateTime toDate) {
        LocalDateTime tsFromDate = DateUtils.convertFromDate(fromDate);
        LocalDateTime tsToDate = DateUtils.convertToDate(toDate);
        return (root, query, criteriaBuilder) ->{
            if (fromDate == null && toDate != null) {
                 return criteriaBuilder.lessThanOrEqualTo(root.get(PoTrans_.transDate), tsToDate);
            }
            if (toDate == null && fromDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(PoTrans_.transDate), tsFromDate);
            }
            if(fromDate == null && toDate == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(PoTrans_.transDate), tsFromDate, tsToDate);
        };
    }
    public static Specification<PoTrans> hasFromDateToDateRedInvoice(LocalDateTime fromDate, LocalDateTime toDate) {
        LocalDateTime tsFromDate = DateUtils.convertFromDate(fromDate);
        LocalDateTime tsToDate = DateUtils.convertToDate(toDate);
        return (root, query, criteriaBuilder) ->{
            if (fromDate == null && toDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(PoTrans_.orderDate), tsToDate);
            }
            if (toDate == null && fromDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(PoTrans_.orderDate), tsFromDate);
            }
            if(fromDate == null && toDate == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(PoTrans_.orderDate), tsFromDate, tsToDate);
        };
    }
    public static Specification<PoTrans> hasTransCode(String transCode) {
        return (root, query, criteriaBuilder) -> {

            if (transCode == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.upper(root.get(PoTrans_.transCode)), "%" + transCode.toUpperCase() + "%");
        };
    }
    public static Specification<PoTrans> hasRedInvoiceNo(String redInvoiceNo) {
        return (root, query, criteriaBuilder) -> {
            if (redInvoiceNo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.upper(root.get(PoTrans_.redInvoiceNo)), "%" + redInvoiceNo.toUpperCase() + "%");
        };
    }
    public static Specification<PoTrans> hasInternalNumber(String internalNumber) {
        return (root, query, criteriaBuilder) -> {
            if (internalNumber == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.upper(root.get(PoTrans_.internalNumber)), "%" + internalNumber.toUpperCase() + "%");
        };
    }
    public static Specification<PoTrans> hasPoCoNo(String poCoNo) {
        return (root, query, criteriaBuilder) -> {
            if (poCoNo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.upper(root.get(PoTrans_.pocoNumber)), "%" + poCoNo.toUpperCase() + "%");
        };
    }

    public static Specification<StockAdjustmentTrans> hasFromDateToDateA(LocalDateTime fromDate, LocalDateTime toDate) {
        LocalDateTime tsFromDate = DateUtils.convertFromDate(fromDate);
        LocalDateTime tsToDate = DateUtils.convertToDate(toDate);
        return (root, query, criteriaBuilder) ->{
            if (fromDate == null && toDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(StockAdjustmentTrans_.transDate), tsToDate);
            }
            if (toDate == null && fromDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(StockAdjustmentTrans_.transDate), tsFromDate);
            }
            if(fromDate == null && toDate == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(StockAdjustmentTrans_.transDate), tsFromDate, tsToDate);
        };

    }
    public static Specification<StockAdjustmentTrans> hasRedInvoiceNoA(String redInvoiceNo) {

        return (root, query, criteriaBuilder) -> {
            if (redInvoiceNo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(StockAdjustmentTrans_.redInvoiceNo), "%" + redInvoiceNo.toUpperCase() + "%");
        };
    }

    public static Specification<StockBorrowingTrans> hasFromDateToDateB(LocalDateTime fromDate, LocalDateTime toDate) {
        LocalDateTime tsFromDate = DateUtils.convertFromDate(fromDate);
        LocalDateTime tsToDate = DateUtils.convertToDate(toDate);
        return (root, query, criteriaBuilder) ->{
            if (fromDate == null && toDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(StockBorrowingTrans_.transDate), tsToDate);
            }
            if (toDate == null && fromDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(StockBorrowingTrans_.transDate), tsFromDate);
            }
            if(fromDate == null && toDate == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(StockBorrowingTrans_.transDate), tsFromDate, tsToDate);
        };
    }
    public static Specification<PoTrans> hasGreaterDay(LocalDateTime dateTime) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(PoTrans_.orderDate),dateTime);
    }
    public static Specification<StockBorrowingTrans> hasRedInvoiceNoB(String redInvoiceNo) {
        return (root, query, criteriaBuilder) -> {
            if (redInvoiceNo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(StockBorrowingTrans_.redInvoiceNo), "%" + redInvoiceNo.toUpperCase() + "%");
        };


    }
}