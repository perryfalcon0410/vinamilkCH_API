package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;

import vn.viettel.sale.entities.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
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
    public static Specification<StockAdjustmentTrans> hasTypeImportA() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(StockAdjustmentTrans_.type), 1);
    }
    public static Specification<StockAdjustmentTrans> hasTypeExportA() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(StockAdjustmentTrans_.type), 2);
    }
    public static Specification<StockAdjustmentTrans> hasStatusA() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(StockAdjustmentTrans_.status), 1);
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
    public static Specification<PoTrans> hasFromDateToDate(Date fromDate, Date toDate) {

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
                 return criteriaBuilder.lessThanOrEqualTo(root.get(PoTrans_.transDate), finalTsToDate);
            }
            if (toDate == null && fromDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(PoTrans_.transDate), finalTsFromDate);
            }
            if(fromDate == null && toDate == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(PoTrans_.transDate), finalTsFromDate, finalTsToDate);
        };

    }
    public static Specification<PoTrans> hasTransCode(String transCode) {
        return (root, query, criteriaBuilder) -> {
            if (transCode == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(PoTrans_.transCode), "%" + transCode + "%");
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
            return criteriaBuilder.like(root.get(PoTrans_.internalNumber), "%" + internalNumber + "%");
        };
    }
    public static Specification<PoTrans> hasPoNo(String poNo) {
        return (root, query, criteriaBuilder) -> {
            if (poNo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(PoTrans_.poNumber), "%" + poNo + "%");
        };
    }

    public static Specification<StockAdjustmentTrans> hasFromDateToDateA(Date fromDate, Date toDate) {
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
                return criteriaBuilder.lessThanOrEqualTo(root.get(StockAdjustmentTrans_.transDate), finalTsToDate);
            }
            if (toDate == null && fromDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(StockAdjustmentTrans_.transDate), finalTsFromDate);
            }
            if(fromDate == null && toDate == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(StockAdjustmentTrans_.transDate), finalTsFromDate, finalTsToDate);
        };

    }
    public static Specification<StockAdjustmentTrans> hasRedInvoiceNoA(String redInvoiceNo) {

        return (root, query, criteriaBuilder) -> {
            if (redInvoiceNo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(StockAdjustmentTrans_.redInvoiceNo), "%" + redInvoiceNo + "%");
        };
    }

    public static Specification<StockBorrowingTrans> hasFromDateToDateB(Date fromDate, Date toDate) {
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
                return criteriaBuilder.lessThanOrEqualTo(root.get(StockBorrowingTrans_.transDate), finalTsToDate);
            }
            if (toDate == null && fromDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(StockBorrowingTrans_.transDate), finalTsFromDate);
            }
            if(fromDate == null && toDate == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(StockBorrowingTrans_.transDate), finalTsFromDate, finalTsToDate);
        };
    }
    public static Specification<StockBorrowingTrans> hasRedInvoiceNoB(String redInvoiceNo) {
        return (root, query, criteriaBuilder) -> {
            if (redInvoiceNo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(StockBorrowingTrans_.redInvoiceNo), "%" + redInvoiceNo + "%");
        };


    }
}