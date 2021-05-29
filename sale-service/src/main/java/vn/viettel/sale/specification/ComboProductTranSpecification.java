package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.sale.entities.ComboProductTrans;
import vn.viettel.sale.entities.ComboProductTrans_;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

public class ComboProductTranSpecification {

    public static Specification<ComboProductTrans> hasShopId(Long shopId) {

        return (root, query, criteriaBuilder) -> {
            if (shopId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(ComboProductTrans_.shopId), shopId);
        };
    }

    public  static  Specification<ComboProductTrans> hasTransCode(String transCode){
        String transCodeUPPER = VNCharacterUtils.removeAccent(transCode).toUpperCase(Locale.ROOT);
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get(ComboProductTrans_.transCode), "%" + transCodeUPPER + "%");
    }

    public static Specification<ComboProductTrans> hasTransType(Integer transType) {

        return (root, query, criteriaBuilder) -> {
            if (transType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(ComboProductTrans_.transType), transType);
        };
    }

    public static Specification<ComboProductTrans> hasFromDateToDate(Date fromDate, Date toDate) {

        return (root, query, criteriaBuilder) -> {
            Timestamp tsFromDate = DateUtils.convertFromDate(fromDate);
            Timestamp tsToDate = DateUtils.convertToDate(toDate);
            if (tsFromDate == null && tsToDate == null) return criteriaBuilder.conjunction();

            if(tsFromDate == null && tsToDate != null)
                return criteriaBuilder.lessThanOrEqualTo(root.get(ComboProductTrans_.transDate), tsToDate);

            if(tsFromDate != null && tsToDate == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get(ComboProductTrans_.transDate), tsFromDate);

            return criteriaBuilder.between(root.get(ComboProductTrans_.transDate), tsFromDate, tsToDate);
        };

    }

}
