package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.sale.entities.ComboProduct;
import vn.viettel.sale.entities.ComboProduct_;

import java.util.Locale;

public class ComboProductSpecification {

    public static Specification<ComboProduct> hasStatus(Integer status) {

        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(ComboProduct_.status), status);
        };
    }

    public  static  Specification<ComboProduct> hasKeyWord(String keyWord){
        String nameLowerCase = VNCharacterUtils.removeAccent(keyWord).toUpperCase(Locale.ROOT);
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(root.get(ComboProduct_.productName), "%" + keyWord + "%"),
                criteriaBuilder.like(root.get(ComboProduct_.productNameText), "%" + nameLowerCase + "%"),
                criteriaBuilder.like(root.get(ComboProduct_.productCode), "%" + nameLowerCase + "%")
        );
    }

}
