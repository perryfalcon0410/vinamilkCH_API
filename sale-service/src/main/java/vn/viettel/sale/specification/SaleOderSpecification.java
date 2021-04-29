package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.sale.entities.RedInvoice;
import vn.viettel.sale.entities.RedInvoice_;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.entities.SaleOrder_;


import java.util.Date;

public class SaleOderSpecification {
    public static Specification<SaleOrder> hasFromDateToDate(Date sFromDate, Date sToDate) {
        return (root, query, criteriaBuilder) ->{
            if (sFromDate == null || sToDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(SaleOrder_.createdAt), sFromDate, sToDate);
        };
    }

    public static Specification<SaleOrder> hasCustomerName(String customerName) {
        return (root, query, criteriaBuilder) -> {
            if (customerName == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(SaleOrder_.customerId.getName()), "%" +  customerName + "%");
        };
    }

    public static Specification<SaleOrder> hasOrderNumber(String orderNumber) {
        return (root, query, criteriaBuilder) -> {
            if (orderNumber == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(SaleOrder_.orderNumber), "%" + orderNumber + "%");
        };
    }
    public static Specification<SaleOrder> hasInvoiceNumber(String orderNumber) {
        return (root, query, criteriaBuilder) -> {
            if (orderNumber == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(SaleOrder_.orderNumber), "%" + orderNumber + "%");
        };
    }
    public static Specification<SaleOrder> hasCustomerId(Long id) {
        return (root, query, criteriaBuilder) -> {
            if (id == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(SaleOrder_.customerId), id);
        };
    }
}
