package vn.viettel.sale.specification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.util.ConvertDateToSearch;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.sale.entities.OnlineOrder_;
import vn.viettel.sale.entities.SaleOrderDetail;
import vn.viettel.sale.entities.SaleOrder_;
import vn.viettel.sale.entities.SaleOrder;


import javax.persistence.criteria.*;
import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SaleOderSpecification {
    @Autowired
    public static Specification<SaleOrder> hasFromDateToDate(Date sFromDate, Date sToDate) {
        return (root, query, criteriaBuilder) -> {
            Timestamp tsFromDate = ConvertDateToSearch.convertFromDate(sFromDate);
            Timestamp tsToDate = ConvertDateToSearch.convertToDate(sToDate);

            if (tsFromDate == null && tsToDate == null) return criteriaBuilder.conjunction();

            if(tsFromDate == null && tsToDate != null)
                return criteriaBuilder.lessThanOrEqualTo(root.get(SaleOrder_.orderDate), tsToDate);

            if(tsFromDate != null && tsToDate == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get(SaleOrder_.orderDate), tsFromDate);

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
            return criteriaBuilder.like(root.get(SaleOrder_.orderNumber), "%" + orderNumberUPPER + "%");
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

