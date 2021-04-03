package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;

import vn.viettel.core.db.entity.stock.*;

import java.util.Date;

public class ReceiptSpecification {



  /*  public static Specification<PoTrans> hasType(Integer type) {

        return (root, query, criteriaBuilder) -> {
            if (type == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(PoTrans_.type), type);
        };
    }*/

    public static Specification<PoTrans> hasFromDateToDate(Date fromDate, Date toDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(PoTrans_.transDate), fromDate, toDate);
    }
    public static Specification<PoTrans> hasTransCode(String transCode) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(PoTrans_.transCode), "%" + transCode + "%");
    }
    public static Specification<PoTrans> hasRedInvoiceNo(String redInvoiceNo) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(PoTrans_.redInvoiceNo), "%" + redInvoiceNo + "%");
    }
    public static Specification<PoTrans> hasInternalNumber(String internalNumber) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(PoTrans_.internalNumber), "%" + internalNumber + "%");
    }
    public static Specification<PoTrans> hasPoNo(String poNo) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(PoTrans_.poNumber), "%" + poNo + "%");
    }

    public static Specification<StockAdjustmentTrans> hasFromDateToDateA(Date fromDate, Date toDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(StockAdjustmentTrans_.transDate), fromDate, toDate);
    }
    public static Specification<StockAdjustmentTrans> hasRedInvoiceNoA(String redInvoiceNo) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(StockAdjustmentTrans_.redInvoiceNo), "%" + redInvoiceNo + "%");
    }

    public static Specification<StockBorrowingTrans> hasFromDateToDateB(Date fromDate, Date toDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(StockBorrowingTrans_.transDate), fromDate, toDate);
    }
    public static Specification<StockBorrowingTrans> hasRedInvoiceNoB(String redInvoiceNo) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(StockBorrowingTrans_.redInvoiceNo), "%" + redInvoiceNo + "%");
    }
}