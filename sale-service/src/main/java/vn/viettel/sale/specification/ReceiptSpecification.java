package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;

import vn.viettel.sale.entities.*;

import java.util.Date;

public class ReceiptSpecification {

    public static Specification<PoTrans> hasTypeImport() {
          return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PoTrans_.type), 1);
    }
    public static Specification<PoTrans> hasTypeExport() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PoTrans_.type), 2);
    }
    public static Specification<PoTrans> hasDeletedAtIsNull() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get(PoTrans_.deletedAt));
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
    public static Specification<StockAdjustmentTrans> hasDeletedAtIsNullA() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get(StockAdjustmentTrans_.deletedAt));
    }
    public static Specification<StockBorrowingTrans> hasTypeExportB() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(StockBorrowingTrans_.type), 2);
    }
    public static Specification<StockBorrowingTrans> hasTypeImportB() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(StockBorrowingTrans_.type), 1);
    }
    public static Specification<StockBorrowingTrans> hasDeletedAtIsNullB() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get(StockBorrowingTrans_.deletedAt));
    }
    public static Specification<PoTrans> hasFromDateToDate(Date fromDate, Date toDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null) {
                return criteriaBuilder.conjunction();
            }if (toDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(PoTrans_.transDate), fromDate, toDate);
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
            return criteriaBuilder.like(root.get(PoTrans_.redInvoiceNo), "%" + redInvoiceNo + "%");
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
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null) {
                return criteriaBuilder.conjunction();
            }if (toDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(StockAdjustmentTrans_.transDate), fromDate, toDate);
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
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null) {
                return criteriaBuilder.conjunction();
            }if (toDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(StockBorrowingTrans_.transDate), fromDate, toDate);
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