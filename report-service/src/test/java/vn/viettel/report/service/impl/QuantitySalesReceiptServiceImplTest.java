package vn.viettel.report.service.impl;

import org.hibernate.Session;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.QuantitySalesReceiptFilter;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

public class QuantitySalesReceiptServiceImplTest extends BaseTest {
    @InjectMocks
    QuantitySalesReceiptServiceImpl service;

    @Mock
    ShopClient shopClient;

    @Mock
    public EntityManager entityManager;

    @Mock
    Session session;

    @Test
    public void exportExcel() throws IOException {
        QuantitySalesReceiptFilter filter = new QuantitySalesReceiptFilter();
        filter.setShopId(1L);
        filter.setFromDate(LocalDateTime.now());
        filter.setToDate(LocalDateTime.now());
        filter.setNameOrCodeCustomer("123");

        Response response = new Response();
        ShopDTO shopDTO = new ShopDTO();
        response.setData(shopDTO);
        Mockito.when(shopClient.getShopByIdV1(filter.getShopId())).thenReturn(response);

        Mockito.when(entityManager.unwrap(Session.class)).thenReturn(session);
        service.exportExcel(filter);
    }

    @Test
    public void callProcedure() {
        QuantitySalesReceiptFilter filter = new QuantitySalesReceiptFilter();
        filter.setShopId(1L);
        filter.setFromDate(LocalDateTime.now());
        filter.setToDate(LocalDateTime.now());
        filter.setNameOrCodeCustomer("123");

        Mockito.when(entityManager.unwrap(Session.class)).thenReturn(session);

        service.callProcedure(filter);
    }
}