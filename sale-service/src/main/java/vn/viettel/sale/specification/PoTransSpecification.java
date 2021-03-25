package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;

import vn.viettel.core.db.entity.stock.PoTrans;
import vn.viettel.core.db.entity.stock.PoTrans_;

import java.util.Date;

public class PoTransSpecification {



    public static Specification<PoTrans> hasType(Integer type) {

        return (root, query, criteriaBuilder) -> {
            if (type == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(PoTrans_.type), type);
        };
    }

    public static Specification<PoTrans> hasFromDateToDate(Date fromDate, Date toDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(PoTrans_.createdAt), fromDate, toDate);
    }
    public static Specification<PoTrans> hasRedInvoiceNo(String redInvoiceNo) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like( criteriaBuilder.like(root.get(PoTrans_.redInvoiceNo), "%" + redInvoiceNo + "%"));
        };
    }
}