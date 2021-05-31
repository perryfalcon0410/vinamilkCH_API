package vn.viettel.sale.specification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.sale.entities.SaleOrder_;
import vn.viettel.sale.entities.SaleOrder;

import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SaleOderSpecification {
    @Autowired
    public static Specification<SaleOrder> hasFromDateToDate(Date fromDate, Date toDate) {
        Timestamp tsFromDate = DateUtils.convertFromDate(fromDate);
        Timestamp tsToDate = DateUtils.convertToDate(toDate);
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null && toDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(SaleOrder_.orderDate), tsToDate);
            }
            if (toDate == null && fromDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(SaleOrder_.orderDate), tsFromDate);
            }
            if (fromDate == null && toDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(SaleOrder_.orderDate), tsFromDate, tsToDate);
        };
    }


    public static Specification<SaleOrder> hasCustomerName(String customerName) {
        return (root, query, criteriaBuilder) -> {
            if (customerName == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(SaleOrder_.customerId.getName()), "%" + customerName + "%");
        };
    }

    public static Specification<SaleOrder> hasOrderNumber(String orderNumber) {
        String orderNumberUPPER = VNCharacterUtils.removeAccent(orderNumber).toUpperCase(Locale.ROOT);
        return (root, query, criteriaBuilder) -> {
            if (orderNumber == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(SaleOrder_.orderNumber), "%" + VNCharacterUtils.removeAccent(orderNumber.toUpperCase(Locale.ROOT)) + "%");
        };
    }

    public static Specification<SaleOrder> hasUseRedInvoice(Integer status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(SaleOrder_.usedRedInvoice), status);
        };
    }

    public static Specification<SaleOrder> hasNameOrPhone(List<Long> Ids) {
        return (root, query, criteriaBuilder) -> {
            if (Ids == null) {
                return criteriaBuilder.conjunction();
            }
            return root.get(SaleOrder_.customerId).in(Ids);
        };
    }

    public static Specification<SaleOrder> type(Integer type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(SaleOrder_.type), type);
        };
    }

    public static Specification<SaleOrder> hasShopId(Long id) {
        return (root, query, criteriaBuilder) -> {
            if (id == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(SaleOrder_.shopId), id);
        };
    }

    public static Specification<SaleOrder> hasUsedRedInvoice(Integer number) {
        return (root, query, criteriaBuilder) -> {
            if (number == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(SaleOrder_.usedRedInvoice), number);
        };
    }
}

