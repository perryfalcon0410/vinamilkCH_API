package vn.viettel.common.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.common.BaseTest;
import vn.viettel.common.entities.PrinterConfig;
import vn.viettel.common.messaging.PrinterConfigRequest;
import vn.viettel.common.repository.ApParamRepository;
import vn.viettel.common.repository.PrinterConfigRepository;
import vn.viettel.common.service.ApParamService;
import vn.viettel.common.service.PrinterConfigService;
import vn.viettel.common.service.dto.PrinterConfigDTO;
import vn.viettel.common.service.impl.ApParamServiceImpl;
import vn.viettel.common.service.impl.PrinterConfigServiceImpl;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.security.context.SecurityContexHolder;
import vn.viettel.core.security.context.UserContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrinterConfigControllerTest extends BaseTest {

    private final String root = "/commons/printer-client";

    @InjectMocks
    PrinterConfigServiceImpl serviceImp;

    @Mock
    PrinterConfigService service;

    @Mock
    PrinterConfigRepository repository;

    @Mock
    private SecurityContexHolder securityContexHolder;

    ModelMapper modelMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        modelMapper = mock(ModelMapper.class);
        serviceImp.setModelMapper(modelMapper);
        final PrinterConfigController controller = new PrinterConfigController();
        controller.setService(service);
        setupAction(controller);
    }

    @Test
    public void create() throws Exception {
        String uri = V1 + root;

        PrinterConfigRequest request = new PrinterConfigRequest();
        request.setUserName("phuongkt");
        if(request.getClientIp() == null) request.setClientIp("");
        if(request.getRemoveAccent() == null) request.setRemoveAccent(false);
        String clientIp = request.getClientIp().toLowerCase() + "_" + request.getUserName().toLowerCase();

        List<PrinterConfig> lst = new ArrayList<>();
        PrinterConfig printerConfig = new PrinterConfig();
        lst.add(printerConfig);
        Page<PrinterConfig> configs = new PageImpl<>(lst, PageRequest.of(0,1), lst.size());
        given(repository.getPrinterConfigs(clientIp, PageRequest.of(0,1)))
                .willReturn(configs);
        PrinterConfigDTO result = serviceImp.create(request);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(super.mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
    @Mock
    PrinterConfig printer;
    @Test
    public void update() throws Exception {
        Long id = 1l;
        String uri = V1 + root + "/" + id.toString();

        PrinterConfigRequest request = new PrinterConfigRequest();
        request.setUserName("phuongkt");
        if(request.getClientIp() == null) request.setClientIp("");
        if(request.getRemoveAccent() == null) request.setRemoveAccent(false);
        String clientIp = request.getClientIp().toLowerCase() + "_" + request.getUserName().toLowerCase();

        given(repository.existsById(id)).willReturn(true);
        printer.setId(id);
        if(printer.getRemoveAccent() == null) printer.setRemoveAccent(false);
        printer.setClientIp(request.getClientIp().toLowerCase() + "_" + request.getUserName().toLowerCase());
        given(modelMapper.map(request, PrinterConfig.class)).willReturn(printer);

        given(repository.save(printer)).willReturn(new PrinterConfig());
        PrinterConfigDTO result = serviceImp.update(id, request);

        ResultActions resultActions = mockMvc.perform(put(uri)
                .content(super.mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getClient() throws Exception {
        String clientId = "phuongkt";
        String uri = V1 + root + "/" + clientId.toString();

        List<PrinterConfig> lst = new ArrayList<>();
        PrinterConfig printerConfig = new PrinterConfig();
        lst.add(printerConfig);
        Page<PrinterConfig> configs = new PageImpl<>(lst, PageRequest.of(0,1), lst.size());
        given(repository.getPrinterConfigs(clientId, PageRequest.of(0,1)))
                .willReturn(configs);

        serviceImp.getPrinter(clientId);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getClientIp() {
    }
}