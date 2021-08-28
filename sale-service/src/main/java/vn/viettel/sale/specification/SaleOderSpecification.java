package vn.viettel.sale.specification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.entities.SaleOrder_;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class SaleOderSpecification {
    @Autowired
    public static Specification<SaleOrder> hasFromDateToDate(LocalDateTime fromDate, LocalDateTime toDate) {
        LocalDateTime tsFromDate = DateUtils.convertFromDate(fromDate);
        LocalDateTime tsToDate = DateUtils.convertToDate(toDate);
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

    public static Specification<SaleOrder> hasCustomerId(Long customerId) {
        return (root, query, criteriaBuilder) -> {
            if (customerId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(SaleOrder_.customerId),customerId) ;
        };
    }

    public static Specification<SaleOrder> hasOrderNumber(String orderNumber) {

        return (root, query, criteriaBuilder) -> {
            if (orderNumber == null) {
                return criteriaBuilder.conjunction();
            }
            String orderNumberUPPER = VNCharacterUtils.removeAccent(orderNumber.trim()).toUpperCase(Locale.ROOT);
            return criteriaBuilder.like(root.get(SaleOrder_.orderNumber), "%" + orderNumberUPPER + "%");
        };
    }


    public static Specification<SaleOrder> hasUseRedInvoice(Integer status) {
        return (root, query, criteriaBuilder) -> {
            // status:  null tìm tất cả
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            // 1 là đã in
            if(status == 1){
                return criteriaBuilder.equal(root.get(SaleOrder_.usedRedInvoice), status);
            }
            // 0 là chưa in (DB = 0, null)
            Predicate predicate = criteriaBuilder.equal(root.get(SaleOrder_.usedRedInvoice), status);
            Predicate predicate1 = criteriaBuilder.isNull(root.get(SaleOrder_.usedRedInvoice));
            return criteriaBuilder.or(predicate, predicate1);
        };
    }

    public static Specification<SaleOrder> hasNameOrPhone(List<Long> Ids) {
        return (root, query, criteriaBuilder) -> {
            if (Ids == null || Ids.isEmpty()) {
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

