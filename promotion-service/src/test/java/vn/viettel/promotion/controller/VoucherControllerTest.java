package vn.viettel.promotion.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.voucher.VoucherDTO;

import vn.viettel.core.messaging.Response;
import vn.viettel.promotion.BaseTest;
import vn.viettel.promotion.entities.*;
import vn.viettel.promotion.repository.*;
import vn.viettel.promotion.service.VoucherService;
import vn.viettel.promotion.service.feign.CustomerClient;
import vn.viettel.promotion.service.feign.ShopClient;
import vn.viettel.promotion.service.impl.VoucherServiceImpl;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VoucherControllerTest extends BaseTest {
    private final String root = "/promotions/vouchers";
    private final String uri = V1 + root;

    private final String secretKey = "Feign dMV6xG4narokclMfpuJkOCFW1XYJoCsX";
    private final String headerType = "Authorization";

    @InjectMocks
    private VoucherServiceImpl voucherService;

    @Mock
    VoucherRepository repository;

    @Mock
    VoucherService service;

    @Mock
    ShopClient shopClient;

    @Mock
    VoucherProgramRepository voucherProgramRepository;

    @Mock
    CustomerClient customerClient;

    @Mock
    VoucherShopMapRepostiory voucherShopMapRepo;

    @Mock
    VoucherCustomerMapRepository voucherCustomerMapRepo;

    @Mock
    VoucherSaleProductRepository voucherSaleProductRepo;

    private List<Voucher> lstEntities;

    private List<VoucherProgram> voucherPrograms;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        voucherService.setModelMapper(this.modelMapper);
        final VoucherController controller = new VoucherController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        lstEntities = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            final Voucher entity = new Voucher();
            entity.setId((long) i);
            entity.setActivated(true);
            entity.setCustomerTypeId(1L);
            entity.setStatus(1);
            entity.setChangeUser("Tuan");
            entity.setCustomerId(1L);
            entity.setIsUsed(true);
            entity.setOrderAmount(100000F);
            entity.setVoucherProgramId(1L);
            lstEntities.add(entity);
        }

        voucherPrograms = new ArrayList<>();
        final VoucherProgram voucherProgram = new VoucherProgram();
        voucherProgram.setId(1L);
        voucherPrograms.add(voucherProgram);

    }

    @Test
    public void getVoucherBySerialTest() throws Exception {
        Map<String, String> voucher = new HashMap<>();
        voucher.put("serial","ABC");
        List<Long> productIds = new ArrayList<>();
        productIds.add(1l);
        productIds.add(2l);
        String url = uri + "/code";

        ShopParamDTO shopParamDTO = new ShopParamDTO();
        shopParamDTO.setId(1L);
        shopParamDTO.setName("10");

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setCustomerTypeId(1L);

        ShopDTO shopDTO = new ShopDTO();
        shopDTO.setId(1L);

        Mockito.when(shopClient.getShopParamV1("SALEMT_LIMITVC", "LIMITVC", 1L)).thenReturn(new Response<ShopParamDTO>().withData(shopParamDTO));
        Mockito.when(repository.getBySerial(any(),any(),any())).thenReturn(Optional.ofNullable(lstEntities.get(0)));
        Mockito.when(customerClient.getCustomerByIdV1(any())).thenReturn(new Response<CustomerDTO>().withData(customerDTO));
        Mockito.when(shopClient.getByIdV1(any())).thenReturn(new Response<ShopDTO>().withData(shopDTO));
        List<Long> lst = new ArrayList<>();
        Mockito.when(voucherShopMapRepo.findShopIds(lstEntities.get(0).getVoucherProgramId(), 1)).thenReturn(lst);
        Mockito.when(voucherCustomerMapRepo.findCustomerIds(lstEntities.get(0).getVoucherProgramId(), 1)).thenReturn(lst);
        Mockito.when(voucherSaleProductRepo.findProductIds(lstEntities.get(0).getVoucherProgramId(), 1)).thenReturn(lst);
        VoucherDTO result = voucherService.getVoucherBySerial(voucher.get("serial"),1L,1L,productIds);

        assertEquals(lstEntities.get(0).getId(), result.getId());

        ResultActions resultActions = mockMvc.perform(get(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
    //-------------------------------updateVoucher-------------------------------
    @Test
    public void updateVoucherTest() throws Exception {

        VoucherDTO voucherDTO = lstEntities.stream().map(voucher -> modelMapper.map(voucher, VoucherDTO.class))
                .collect(Collectors.toList()).get(0);

        Mockito.when(repository.getById(voucherDTO.getId())).thenReturn(lstEntities.get(0));

        Mockito.when(voucherProgramRepository.findById(voucherDTO.getId()))
                .thenReturn(Optional.ofNullable(voucherPrograms.get(0)));

        VoucherDTO result = voucherService.updateVoucher(voucherDTO);

        assertEquals(voucherDTO.getId(), result.getId());

        ResultActions resultActions = mockMvc.perform(put(uri)
                        .content(mapToJson(voucherDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------getVoucherBySaleOrderId-------------------------------
    @Test
    public void getVoucherBySaleOrderIdTest() throws Exception {
        Long saleOrderId = 1L;
        String url = uri + "/get-by-sale-order-id/" + saleOrderId.toString();

        Mockito.when(repository.getVoucherBySaleOrderId(saleOrderId)).thenReturn(lstEntities);

        List<VoucherDTO> list = voucherService.getVoucherBySaleOrderId(saleOrderId);

        assertEquals(lstEntities.size(), list.size());

        ResultActions resultActions = mockMvc.perform(get(url)
                        .header(headerType, secretKey)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void returnVoucherTest() throws Exception {
        Long saleOrderId = 1L;
        Boolean flag = false;
        String url = uri + "/return";
        Mockito.when(repository.getVoucherBySaleOrderId(saleOrderId)).thenReturn(lstEntities);
        Boolean result = voucherService.returnVoucher(saleOrderId);
        if(lstEntities.size()>0)
            flag = true;
        else flag = false;
        assertEquals(flag, result);

        ResultActions resultActions = mockMvc.perform(get(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}
