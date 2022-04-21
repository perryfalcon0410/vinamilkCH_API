package vn.viettel.report.service.impl;

import org.hibernate.Session;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.messaging.QuantitySalesReceiptFilter;
import vn.viettel.report.service.feign.CommonClient;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ExchangeTransReportServiceImplTest extends BaseTest {
    @InjectMocks
    ExchangeTransReportServiceImpl service;

    @Mock
    ShopClient shopClient;

    @Mock
    public EntityManager entityManager;

    @Mock
    Session session;

    @Mock
    CommonClient commonClient;

    @Test
    public void exportExcel() throws IOException {
        ExchangeTransFilter filter = new ExchangeTransFilter();
        filter.setShopId(1L);
        filter.setFromDate(LocalDateTime.now());
        filter.setToDate(LocalDateTime.now());

        Mockito.when(entityManager.unwrap(Session.class)).thenReturn(session);

        Response response = new Response();
        ShopDTO shopDTO = new ShopDTO();
        response.setData(shopDTO);
        Mockito.when(shopClient.getShopByIdV1(filter.getShopId())).thenReturn(response);

        service.exportExcel(filter);
    }

    @Test
    public void callProcedure() {
        ExchangeTransFilter filter = new ExchangeTransFilter();
        filter.setShopId(1L);
        filter.setFromDate(LocalDateTime.now());
        filter.setToDate(LocalDateTime.now());

        Mockito.when(entityManager.unwrap(Session.class)).thenReturn(session);

        service.callProcedure(filter);
    }

    @Test
    public void getExchangeTransReport() {
        ExchangeTransFilter filter = new ExchangeTransFilter();
        filter.setShopId(1L);
        filter.setFromDate(LocalDateTime.now());
        filter.setToDate(LocalDateTime.now());

        Mockito.when(entityManager.unwrap(Session.class)).thenReturn(session);

        service.getExchangeTransReport(filter, Pageable.unpaged());
    }

    @Test
    public void validMonth() {
        ExchangeTransFilter filter = new ExchangeTransFilter();
        filter.setShopId(1L);
        filter.setFromDate(LocalDateTime.now());
        filter.setToDate(LocalDateTime.now());

        service.validMonth(filter);
    }

    @Test
    public void listReasonExchange() {
        Response response = new Response();
        CategoryDataDTO shopDTO = new CategoryDataDTO();
        List<CategoryDataDTO> lst = Arrays.asList(shopDTO);
        response.setData(lst);
        Mockito.when(commonClient.getReasonExchangeV1()).thenReturn(response);
        service.listReasonExchange();
    }
}