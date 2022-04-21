package vn.viettel.report.service.impl;

import org.hibernate.Session;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.QuantitySalesReceiptFilter;
import vn.viettel.report.messaging.ReportVoucherFilter;
import vn.viettel.report.service.dto.ReportVoucherDTO;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ReportVoucherServiceImplTest extends BaseTest {

    @InjectMocks
    ReportVoucherServiceImpl service;

    @Mock
    ShopClient shopClient;

    @Mock
    public EntityManager entityManager;

    @Mock
    Session session;

    @Mock
    StoredProcedureQuery storedProcedure;

    @Test
    public void exportExcel() throws IOException {
        ReportVoucherFilter filter = new ReportVoucherFilter();
        filter.setShopId(1L);
        Mockito.when(entityManager.createStoredProcedureQuery("P_VOUCHER",
                ReportVoucherDTO.class)).thenReturn(storedProcedure);

        Response response = new Response();
        ShopDTO shopDTO = new ShopDTO();
        response.setData(shopDTO);
        Mockito.when(shopClient.getShopByIdV1(filter.getShopId())).thenReturn(response);

        service.exportExcel(filter);
    }

    @Test
    public void callReportVoucherDTO() {
        ReportVoucherFilter filter = new ReportVoucherFilter();
        filter.setShopId(1L);
        Mockito.when(entityManager.createStoredProcedureQuery("P_VOUCHER",
                ReportVoucherDTO.class)).thenReturn(storedProcedure);
        service.callReportVoucherDTO(filter);
    }
}