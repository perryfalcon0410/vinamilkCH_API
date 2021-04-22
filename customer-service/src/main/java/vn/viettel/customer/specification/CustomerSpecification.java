package vn.viettel.customer.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.customer.entities.Customer;
import vn.viettel.customer.entities.Customer_;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    public static Specification<Customer> hasAreaId(List<AreaDTO> precincts) {
        return (root, query, criteriaBuilder) -> {
            if (precincts == null) {
                return criteriaBuilder.conjunction();
            }

            if(precincts.size() == 0)
                return criteriaBuilder.equal(root.get(Customer_.areaId),-1);

            CriteriaBuilder.In<Long> in = criteriaBuilder.in(root.get(Customer_.areaId));
            precincts.forEach(area -> in.value(area.getId()));
            return in;
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
            Expression<String> fullNameAccent = criteriaBuilder.concat(criteriaBuilder.concat(root.get(Customer_.lastName), " "), root.get(Customer_.firstName));
//            Expression<String> fullNameNotAccent = criteriaBuilder.concat(criteriaBuilder.concat(root.get(Customer_.lastNameText), " "), root.get(Customer_.firstNameText));
            return criteriaBuilder.or(criteriaBuilder.like(fullNameAccent, "%" + searchKeywords + "%"),
//                    criteriaBuilder.like(fullNameNotAccent, "%" + VNCharacterUtils.removeAccent(searchKeywords.toUpperCase(Locale.ROOT)) + "%"),
                    criteriaBuilder.like(root.get(Customer_.customerCode), "%" + searchKeywords.toUpperCase(Locale.ROOT) + "%"),
                    criteriaBuilder.like(root.get(Customer_.phone), "%" + searchKeywords + "%"),
                    criteriaBuilder.like(root.get(Customer_.mobiPhone), "%" + searchKeywords + "%"));
        };
    }

    public static Specification<Customer> hasFromDateToDate(Date sFromDate, Date sToDate) {
        return (root, query, criteriaBuilder) ->{
            if (sFromDate == null && sToDate == null) {
                return criteriaBuilder.conjunction();
            }
            if(sFromDate == null && sToDate != null)
            {
                return criteriaBuilder.lessThan(root.get(Customer_.createdAt),sToDate);
            }
            if(sFromDate != null && sToDate == null)
            {
                return criteriaBuilder.greaterThan(root.get(Customer_.createdAt),sFromDate);
            }
            return criteriaBuilder.between(root.get(Customer_.createdAt), sFromDate, sToDate);
        };
    }


}
