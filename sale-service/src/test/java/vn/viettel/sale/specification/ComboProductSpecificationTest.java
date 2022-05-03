package vn.viettel.sale.specification;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.jpa.domain.Specification;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.ComboProduct;

import static org.junit.Assert.*;

public class ComboProductSpecificationTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void hasStatus() {
        Specification<ComboProduct> result = ComboProductSpecification.hasStatus(1);
        assertNotNull(result);
    }

    @Test
    public void hasKeyWord() {
        Specification<ComboProduct> result = ComboProductSpecification.hasKeyWord("1");
        assertNotNull(result);
    }
}