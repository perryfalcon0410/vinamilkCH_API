package specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.db.entity.sale.SaleOrder_;

import java.util.Date;

public class SaleOrderReturnSpecification {
    public static Specification<SaleOrder> hasFromDateToDate(Date fromDate, Date toDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null) {
                return criteriaBuilder.conjunction();
            }if (toDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(SaleOrder_.orderDate), fromDate, toDate);
        };

    }
}
