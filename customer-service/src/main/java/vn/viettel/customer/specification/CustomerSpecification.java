package vn.viettel.customer.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.db.entity.common.*;

import javax.persistence.criteria.Expression;
import java.util.Date;

public final class CustomerSpecification {

    public static Specification<Customer> hasStatus(Long status) {

        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(Customer_.status), status);
        };
    }

        public static Specification<Customer> hasGenderId(Long genderId) {
        return (root, query, criteriaBuilder) -> {
            if (genderId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(Customer_.genderId), genderId);
        };
    }

    public static Specification<Customer> hasCustomerTypeId(Long customerTypeId) {
        return (root, query, criteriaBuilder) -> {
            if (customerTypeId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(Customer_.customerTypeId), customerTypeId);
        };
    }

    public static Specification<Customer> hasAreaId(Long areaId) {
        return (root, query, criteriaBuilder) -> {
            if (areaId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(Customer_.areaId), areaId);
        };
    }

    public static Specification<Customer> hasPhone(String phone) {
        return (root, query, criteriaBuilder) -> {
            if (phone == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(Customer_.phone), "%" + phone + "%");
        };
    }

    public static Specification<Customer> hasIdNo(String idNo) {
        return (root, query, criteriaBuilder) -> {
            if (idNo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(Customer_.idNo), "%" + idNo + "%");
        };
    }

    public static Specification<Customer> hasFullNameOrCodeOrPhone(String searchKeywords) {
        return (root, query, criteriaBuilder) -> {
            Expression<String> expression = criteriaBuilder.concat(criteriaBuilder.concat(root.get(Customer_.lastName), " "), String.valueOf(root.get(Customer_.firstName)));
            return criteriaBuilder.or(criteriaBuilder.like(expression, "%" + searchKeywords + "%"),
                    criteriaBuilder.like(root.get(Customer_.customerCode), "%" + searchKeywords + "%"),
                    criteriaBuilder.like(root.get(Customer_.phone), "%" + searchKeywords + "%"),
                    criteriaBuilder.like(root.get(Customer_.mobiPhone), "%" + searchKeywords + "%"));
        };
    }

    public static Specification<Customer> hasFromDateToDate(Date sFromDate, Date sToDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(Customer_.createdAt), sFromDate, sToDate);
    }


}
