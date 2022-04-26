package vn.viettel.report.service.impl;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.QuantitySalesReceiptFilter;
import vn.viettel.report.messaging.SaleOrderAmountFilter;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class SaleOrderAmountServiceImplTest extends BaseTest {

    @InjectMocks
    SaleOrderAmountServiceImpl saleOrderAmountService;

    @Mock
    ShopClient shopClient;

    @Mock
    public EntityManager entityManager;

    @Mock
    Session session;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void exportExcel() throws IOException {
        SaleOrderAmountFilter filter = new SaleOrderAmountFilter();
        filter.setShopId(1L);
        filter.setFromDate(LocalDateTime.now());
        filter.setToDate(LocalDateTime.now());
        filter.setNameOrCodeCustomer("123");

        Response response = new Response();
        ShopDTO shopDTO = new ShopDTO();
        response.setData(shopDTO);
        Mockito.when(shopClient.getShopByIdV1(filter.getShopId())).thenReturn(response);

        Mockito.when(entityManager.unwrap(Session.class)).thenReturn(session);

        saleOrderAmountService.exportExcel(filter);
    }

    @Test
    public void validMonth() {
        SaleOrderAmountFilter filter = new SaleOrderAmountFilter();
        filter.setShopId(1L);
        filter.setFromDate(LocalDateTime.now());
        filter.setToDate(LocalDateTime.now());
        filter.setNameOrCodeCustomer("123");
        saleOrderAmountService.validMonth(filter);
    }

    @Test
    public void callProcedure() {
        SaleOrderAmountFilter filter = new SaleOrderAmountFilter();
        filter.setShopId(1L);
        filter.setFromDate(LocalDateTime.now());
        filter.setToDate(LocalDateTime.now());
        filter.setNameOrCodeCustomer("123");

        Mockito.when(entityManager.unwrap(Session.class)).thenReturn(session);
        saleOrderAmountService.callProcedure(filter);
    }
}