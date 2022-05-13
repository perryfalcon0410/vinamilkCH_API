package vn.viettel.sale.schedule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.OnlineOrder;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.feign.ApparamClient;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.impl.OnlineOrderServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class SchedulerManagerTest extends BaseTest {

    @InjectMocks
    OnlineOrderServiceImpl onlineOrderServiceImpl;

    @Mock
    OnlineOrderService onlineOrderService;

    @Mock
    OnlineOrderDetailRepository onlineOrderDetailRepo;

    @InjectMocks
    SchedulerManager manager;

    @Mock
    ShopClient shopClient;

    @Mock
    CustomerClient customerClient;

    @Mock
    ApparamClient apparamClient;

    @Mock
    CustomerTypeClient customerTypeClient;

    @Mock
    ProductRepository productRepo;

    @Mock
    ProductPriceRepository productPriceRepo;

    @Mock
    StockTotalRepository stockTotalRepo;

    @Mock
    SaleOrderRepository saleOrderRepository;
    @Mock
    OnlineOrderRepository repository;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getOnlineOrder() throws InterruptedException {
        List<ApParamDTO> apParamDTOList = new ArrayList<>();
        ApParamDTO apParamDTO = new ApParamDTO();
        apParamDTO.setValue("11");
        apParamDTOList.add(apParamDTO);
        Response response = new Response();
        response.setData(apParamDTOList);
        BDDMockito.given(apparamClient.getApParamByTypeV1("FTP")).willReturn(response);
        onlineOrderServiceImpl.getOnlineOrderSchedule();

        manager.getOnlineOrder();

        assertNotNull("123");
    }

    @Test
    public void uploadOnlineOrder() throws InterruptedException {
        List<ApParamDTO> apParamDTOList = new ArrayList<>();
        ApParamDTO apParamDTO = new ApParamDTO();
        apParamDTO.setValue("11");
        apParamDTOList.add(apParamDTO);
        Response response = new Response();
        response.setData(apParamDTOList);
//        BDDMockito.given(apparamClient.getApParamByTypeV1("FTP")).willReturn(response);

        List<OnlineOrder> onlineOrders = new ArrayList<>();
        OnlineOrder onlineOrder = new OnlineOrder();
        onlineOrder.setSaleOrderId(1L);
        onlineOrders.add(onlineOrder);
//        BDDMockito.given(repository.findOnlineOrderExportXml(1L)).willReturn(onlineOrders);

//        BDDMockito.given(saleOrderRepository.findById(onlineOrder.getSaleOrderId())).willReturn(java.util.Optional.of(new SaleOrder()));

        ShopDTO shopDTO = new ShopDTO();
        Response response1 = new Response();
        response1.setData(shopDTO);
//        BDDMockito.given(shopClient.getByIdV1(1L)).willReturn(response1);

        onlineOrderServiceImpl.uploadOnlineOrderSchedule();

        manager.uploadOnlineOrder();

        assertNotNull("123");
    }
}