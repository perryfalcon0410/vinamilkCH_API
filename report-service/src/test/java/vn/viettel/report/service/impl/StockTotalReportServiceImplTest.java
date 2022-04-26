package vn.viettel.report.service.impl;

import org.hibernate.Session;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.StockTotalFilter;
import vn.viettel.report.service.dto.StockTotalReportDTO;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import java.io.IOException;

public class StockTotalReportServiceImplTest extends BaseTest {

    @InjectMocks
    StockTotalReportServiceImpl service;

    @Mock
    ShopClient shopClient;

    @Mock
    public EntityManager entityManager;

    @Mock
    Session session;

    @Mock
    StoredProcedureQuery storedProcedure;

    @Test
    public void print() {
        StockTotalFilter filter = new StockTotalFilter();
        filter.setShopId(1L);
        Mockito.when(entityManager.createStoredProcedureQuery("P_STOCK_COUNTING",
                StockTotalReportDTO.class)).thenReturn(storedProcedure);
        Response response = new Response();
        ShopDTO shopDTO = new ShopDTO();
        response.setData(shopDTO);
        Mockito.when(shopClient.getShopByIdV1(filter.getShopId())).thenReturn(response);

        service.print(filter);
    }

    @Test
    public void exportExcel() throws IOException {
        StockTotalFilter filter = new StockTotalFilter();
        filter.setShopId(1L);
//        Mockito.when(entityManager.unwrap(Session.class)).thenReturn(session);
        Mockito.when(entityManager.createStoredProcedureQuery("P_STOCK_COUNTING",
                StockTotalReportDTO.class)).thenReturn(storedProcedure);

        Response response = new Response();
        ShopDTO shopDTO = new ShopDTO();
        response.setData(shopDTO);
        Mockito.when(shopClient.getShopByIdV1(filter.getShopId())).thenReturn(response);

        service.exportExcel(filter);
    }

    @Test
    public void callProcedure() {
        StockTotalFilter filter = new StockTotalFilter();
        filter.setShopId(1L);
        Mockito.when(entityManager.createStoredProcedureQuery("P_STOCK_COUNTING",
                StockTotalReportDTO.class)).thenReturn(storedProcedure);
        service.callProcedure(filter);
    }
}