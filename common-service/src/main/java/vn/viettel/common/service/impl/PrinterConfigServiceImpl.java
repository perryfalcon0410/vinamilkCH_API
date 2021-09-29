package vn.viettel.common.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.common.entities.PrinterConfig;
import vn.viettel.common.messaging.PrinterConfigRequest;
import vn.viettel.common.repository.PrinterConfigRepository;
import vn.viettel.common.service.PrinterConfigService;
import vn.viettel.common.service.dto.PrinterConfigDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;

import java.util.List;
import java.util.Locale;

@Service
public class PrinterConfigServiceImpl extends BaseServiceImpl<PrinterConfig, PrinterConfigRepository> implements PrinterConfigService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrinterConfigDTO create(PrinterConfigRequest request) {
        if(request.getClientIp() == null) request.setClientIp("");
        if(request.getRemoveAccent() == null) request.setRemoveAccent(false);
        String clientIp = request.getClientIp().toLowerCase() + "_" + request.getUserName().toLowerCase();

        Page<PrinterConfig> configs = repository.getPrinterConfigs(clientIp, PageRequest.of(0,1));
        if (!configs.getContent().isEmpty()) {
            PrinterConfig config = configs.getContent().get(0);
            config.setClientIp(clientIp);
            config.setBillPrinterName(request.getBillPrinterName());
            config.setReportPrinterName(request.getReportPrinterName());
            config.setDefaultPrinterName(request.getDefaultPrinterName());
            config.setRemoveAccent(request.getRemoveAccent());
            repository.save(config);
            return modelMapper.map(config, PrinterConfigDTO.class);
        }

        PrinterConfig printer = modelMapper.map(request, PrinterConfig.class);
        printer.setClientIp(clientIp);
        PrinterConfig printerDB = repository.save(printer);
        return modelMapper.map(printerDB, PrinterConfigDTO.class);
    }

    @Override
    public PrinterConfigDTO update(Long id, PrinterConfigRequest request) {
        if(!repository.existsById(id)) throw new ValidateException(ResponseMessage.PRINTER_CONFIG_NOT_FOUND);
        PrinterConfig printer = modelMapper.map(request, PrinterConfig.class);
        printer.setId(id);
        if(printer.getRemoveAccent() == null) printer.setRemoveAccent(false);
        if(request.getClientIp() == null) request.setClientIp("");
        printer.setClientIp(request.getClientIp().toLowerCase() + "_" + request.getUserName().toLowerCase());
        PrinterConfig printerDB = repository.save(printer);
        return modelMapper.map(printerDB, PrinterConfigDTO.class);
    }

    @Override
    public PrinterConfigDTO getPrinter(String clientIp) {
        Page<PrinterConfig> configs = repository.getPrinterConfigs(clientIp, PageRequest.of(0,1));
        if(configs.getContent().isEmpty()) return null;
        PrinterConfig config = configs.getContent().get(0);
        if(config.getRemoveAccent() == null) config.setRemoveAccent(false);
        return modelMapper.map(config, PrinterConfigDTO.class);
    }
}
