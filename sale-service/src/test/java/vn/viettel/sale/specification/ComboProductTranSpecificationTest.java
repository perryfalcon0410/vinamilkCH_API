package vn.viettel.sale.specification;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.jpa.domain.Specification;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.ComboProduct;
import vn.viettel.sale.entities.ComboProductTrans;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ComboProductTranSpecificationTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void hasShopId() {
        Specification<ComboProductTrans> result = ComboProductTranSpecification.hasShopId(1L);
        assertNotNull(result);
    }

    @Test
    public void hasTransCode() {
        Specification<ComboProductTrans> result = ComboProductTranSpecification.hasTransCode("1");
        assertNotNull(result);
    }

    @Test
    public void hasTransType() {
        Specification<ComboProductTrans> result = ComboProductTranSpecification.hasTransType(1);
        assertNotNull(result);
    }

    @Test
    public void hasFromDateToDate() {
        Specification<ComboProductTrans> result = ComboProductTranSpecification.hasFromDateToDate(
                LocalDateTime.now(), LocalDateTime.now()
        );
        assertNotNull(result);
    }
}