package vn.viettel.sale.specification;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.jpa.domain.Specification;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.ComboProductTrans;
import vn.viettel.sale.entities.ExchangeTrans;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ExchangeTransSpecificationTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void hasShopId() {
        Specification<ExchangeTrans> result = ExchangeTransSpecification.hasShopId(1L);
        assertNotNull(result);
    }

    @Test
    public void hasWareHouseType() {
        Specification<ExchangeTrans> result = ExchangeTransSpecification.hasWareHouseType(1L);
        assertNotNull(result);
    }

    @Test
    public void hasTranCode() {
        Specification<ExchangeTrans> result = ExchangeTransSpecification.hasTranCode("23");
        assertNotNull(result);
    }

    @Test
    public void hasReasonId() {
        Specification<ExchangeTrans> result = ExchangeTransSpecification.hasReasonId(1L);
        assertNotNull(result);
    }

    @Test
    public void hasStatus() {
        Specification<ExchangeTrans> result = ExchangeTransSpecification.hasStatus();
        assertNotNull(result);
    }

    @Test
    public void hasFromDateToDate() {
        Specification<ExchangeTrans> result = ExchangeTransSpecification.hasFromDateToDate(
                null, null
        );
        assertNotNull(result);
    }

    @Test
    public void hasDetail() {
        Specification<ExchangeTrans> result = ExchangeTransSpecification.hasDetail();
        assertNotNull(result);
    }
}