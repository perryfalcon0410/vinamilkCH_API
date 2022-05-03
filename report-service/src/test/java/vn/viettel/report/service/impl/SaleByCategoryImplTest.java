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
import vn.viettel.report.messaging.ReportVoucherFilter;
import vn.viettel.report.messaging.SaleCategoryFilter;
import vn.viettel.report.service.dto.ReportVoucherDTO;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class SaleByCategoryImplTest  extends BaseTest {
    @InjectMocks
    SaleByCategoryImpl service;

    @Mock
    ShopClient shopClient;

    @Mock
    public EntityManager entityManager;

    @Mock
    Session session;

    @Mock
    StoredProcedureQuery storedProcedure;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void exportExcel() throws IOException {
        SaleCategoryFilter filter = new SaleCategoryFilter();
        filter.setShopId(1L);
        Mockito.when(entityManager.unwrap(Session.class)).thenReturn(session);

        Response response = new Response();
        ShopDTO shopDTO = new ShopDTO();
        response.setData(shopDTO);
        Mockito.when(shopClient.getShopByIdV1(filter.getShopId())).thenReturn(response);

        service.exportExcel(filter);
    }

    @Test
    public void callProcedure() {
        SaleCategoryFilter filter = new SaleCategoryFilter();
        filter.setShopId(1L);
        Mockito.when(entityManager.unwrap(Session.class)).thenReturn(session);
        service.callProcedure(filter);
    }

    @Test
    public void getSaleByCategoryReport() {
        SaleCategoryFilter filter = new SaleCategoryFilter();
        filter.setShopId(1L);
        Mockito.when(entityManager.unwrap(Session.class)).thenReturn(session);
        service.getSaleByCategoryReport(filter, Pageable.unpaged());
    }

    @Test
    public void print() {
        SaleCategoryFilter filter = new SaleCategoryFilter();
        filter.setShopId(1L);
        filter.setFromDate(LocalDateTime.now());
        filter.setToDate(LocalDateTime.now());
        Mockito.when(entityManager.unwrap(Session.class)).thenReturn(session);
//        Response response = new Response();
//        ShopDTO shopDTO = new ShopDTO();
//        response.setData(shopDTO);
//        Mockito.when(shopClient.getShopByIdV1(filter.getShopId())).thenReturn(response);

        service.print(filter);
    }
}