//package vn.viettel.customer.specification;
//
//import org.springframework.data.jpa.domain.Specification;
//import vn.viettel.core.db.entity.common.Customer_;
//import vn.viettel.core.db.entity.common.Customer;
//
//import javax.persistence.criteria.Expression;
//import java.util.Date;
//
//public final class CustomerSpecification {
//
//    public static Specification<Customer> hasStatus(Long status) {
//
//        return (root, query, criteriaBuilder) -> {
//            if (status == null) {
//                return criteriaBuilder.conjunction();
//            }
//            return criteriaBuilder.equal(root.get(Customer_.status), status);
//        };
//    }
//
//    public static Specification<Customer> hasGroupId(Long groupId) {
//        return (root, query, criteriaBuilder) -> {
//            if (groupId == null) {
//                return criteriaBuilder.conjunction();
//            }
//            return criteriaBuilder.equal(root.get(Customer_.customerGroupId), groupId);
//        };
//    }
//
//    public static Specification<Customer> hasGender(Long gender) {
//        return (root, query, criteriaBuilder) -> {
//            if (gender == null) {
//                return criteriaBuilder.conjunction();
//            }
//            return criteriaBuilder.equal(root.get(Customer_.gender), gender);
//        };
//    }
//
//    public static Specification<Customer> hasFullNameOrCode(String searchKeywords) {
//        return (root, query, criteriaBuilder) -> {
//            Expression<String> expression = criteriaBuilder.concat(criteriaBuilder.concat(root.get(Customer_.lastName), " "), root.get(Customer_.firstName));
//            return criteriaBuilder.or(criteriaBuilder.like(expression, "%" + searchKeywords + "%"), criteriaBuilder.like(root.get(Customer_.customerCode), "%" + searchKeywords + "%"));
//        };
//    }
//
//    public static Specification<Customer> hasFromDateToDate(Date sFromDate, Date sToDate) {
//        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(Customer_.createdAt), sFromDate, sToDate);
//    }
//
//    public static Specification<Customer> hasDeletedAtIsNull() {
//        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get(Customer_.deletedAt));
//    }
//
//    public static Specification<Customer> hasAreaAddress(String areaAddress) {
//        return (root, query, criteriaBuilder) -> {
//            if (areaAddress == null) {
//                return criteriaBuilder.conjunction();
//            } else {
//                String[] list = areaAddress.split(",");
//                Long provinceId = Long.parseLong(list[0]);
//                Long districtId = Long.parseLong(list[1]);
//
//                return criteriaBuilder.and(criteriaBuilder.equal(root.get(Customer_.provinceId), provinceId),criteriaBuilder.equal(root.get(Customer_.districtId), districtId));
//
//            }
//        };
//    }
//
//}
