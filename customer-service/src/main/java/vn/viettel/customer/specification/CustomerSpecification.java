package vn.viettel.customer.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.customer.entities.Customer;
import vn.viettel.customer.entities.Customer_;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public final class CustomerSpecification {

    public static Specification<Customer> hasStatus(Integer status) {

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

    public static Specification<Customer> hasShopId(Long shopId, Boolean isShop) {
        return (root, query, criteriaBuilder) -> {
            if (shopId == null || isShop == false) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(Customer_.shopId), shopId);
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

    public static Specification<Customer> hasPhoneToCustomer(String phone) {
        return (root, query, criteriaBuilder) -> {
            if (phone == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(Customer_.mobiPhone), "%" + phone);
        };
    }

    public static Specification<Customer> hasPhone(String phone) {
        return (root, query, criteriaBuilder) -> {
            if (phone == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(Customer_.mobiPhone), "%" + phone);
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
            if (searchKeywords == null) {
                return criteriaBuilder.conjunction();
            }
            return  criteriaBuilder.or(
                    criteriaBuilder.like(root.get(Customer_.nameText), "%" + VNCharacterUtils.removeAccent(searchKeywords.toUpperCase(Locale.ROOT)) + "%"),
                    criteriaBuilder.like(root.get(Customer_.customerCode), "%" + searchKeywords.toUpperCase(Locale.ROOT) + "%"),
                    criteriaBuilder.like(root.get(Customer_.phone), "%" + searchKeywords),
                    criteriaBuilder.like(root.get(Customer_.mobiPhone), "%" + searchKeywords)
            );
        };
    }

    public static Specification<Customer> haskeySearchForSale(String searchKeywords) {
        return (root, query, criteriaBuilder) -> {
            if (searchKeywords == null) {
                return criteriaBuilder.conjunction();
            }
            return  criteriaBuilder.or(
                    criteriaBuilder.like(root.get(Customer_.nameText), "%" + VNCharacterUtils.removeAccent(searchKeywords.toUpperCase(Locale.ROOT)) + "%"),
                    criteriaBuilder.like(root.get(Customer_.customerCode), "%" + searchKeywords.toUpperCase(Locale.ROOT) + "%"),
                    criteriaBuilder.like(root.get(Customer_.phone), "%" + searchKeywords),
                    criteriaBuilder.like(root.get(Customer_.mobiPhone), "%" + searchKeywords),
                    criteriaBuilder.like(root.get(Customer_.addressText), "%" + VNCharacterUtils.removeAccent(searchKeywords.toUpperCase(Locale.ROOT)) + "%")
            );
        };
    }

    public static Specification<Customer> hasFullNameOrCode(String searchKeywords) {
        return (root, query, criteriaBuilder) -> {
            if (searchKeywords == null) {
                return criteriaBuilder.conjunction();
            }
            Expression<String> fullNameAccent = criteriaBuilder.concat(criteriaBuilder.concat(root.get(Customer_.lastName), " "), root.get(Customer_.firstName));
            return criteriaBuilder.or(criteriaBuilder.like(fullNameAccent, "%" + searchKeywords + "%"),
                    criteriaBuilder.like(root.get(Customer_.nameText), "%" + VNCharacterUtils.removeAccent(searchKeywords.toUpperCase(Locale.ROOT)) + "%"),
                    criteriaBuilder.like(root.get(Customer_.customerCode), "%" + searchKeywords.toUpperCase(Locale.ROOT) + "%"));
        };
    }

    public static Specification<Customer> hasFromDateToDate(LocalDateTime sFromDate, LocalDateTime sToDate) {
        return (root, query, criteriaBuilder) ->{
            LocalDateTime tsFromDate = DateUtils.convertFromDate(sFromDate);
            LocalDateTime tsToDate = DateUtils.convertToDate(sToDate);

            if (sFromDate == null && sToDate == null) {
                return criteriaBuilder.conjunction();
            }
            if(sFromDate == null && sToDate != null)
            {
                return criteriaBuilder.lessThanOrEqualTo(root.get(Customer_.createdAt),tsToDate);
            }
            if(sFromDate != null && sToDate == null)
            {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(Customer_.createdAt),tsFromDate);
            }
            return criteriaBuilder.between(root.get(Customer_.createdAt), tsFromDate, tsToDate);
        };
    }


}
