package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.util.ConvertDateToSearch;
import vn.viettel.sale.entities.RedInvoice;
import vn.viettel.sale.entities.RedInvoice_;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class RedInvoiceSpecification {
    public static Specification<RedInvoice> hasFromDateToDate(Date sFromDate, Date sToDate) {
        return (root, query, criteriaBuilder) -> {
            Timestamp tsFromDate = ConvertDateToSearch.convertFromDate(sFromDate);
            Timestamp tsToDate = ConvertDateToSearch.convertToDate(sToDate);
            if (sFromDate == null && sToDate == null) {
                return criteriaBuilder.conjunction();
            }
            if(sFromDate == null && sToDate != null)
            {
                return criteriaBuilder.lessThanOrEqualTo(root.get(RedInvoice_.createdAt),tsToDate);
            }
            if(sFromDate != null && sToDate == null)
            {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(RedInvoice_.createdAt),tsFromDate);
            }
            return criteriaBuilder.between(root.get(RedInvoice_.createdAt), tsFromDate, tsToDate);
        };

    }

    public static Specification<RedInvoice> hasCustomerId(List<Long> ids) {
        return (root, query, criteriaBuilder) -> {
            if (ids == null) {
                return criteriaBuilder.conjunction();
            }
            return root.get(RedInvoice_.customerId).in(ids);
        };
    }

    public static Specification<RedInvoice> hasShopId(Long shopId) {
        return (root, query, criteriaBuilder) -> {
            if (shopId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(RedInvoice_.shopId), shopId);
        };
    }

    public static Specification<RedInvoice> hasInvoiceNumber(String invoiceNumber) {
        return (root, query, criteriaBuilder) -> {
            if (invoiceNumber == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(RedInvoice_.invoiceNumber), "%" + invoiceNumber + "%");
        };
    }
}
