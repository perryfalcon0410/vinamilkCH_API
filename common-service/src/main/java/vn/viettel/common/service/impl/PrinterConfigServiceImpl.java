package vn.viettel.common.service.impl;

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

@Service
public class PrinterConfigServiceImpl extends BaseServiceImpl<PrinterConfig, PrinterConfigRepository> implements PrinterConfigService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrinterConfigDTO create(PrinterConfigRequest request) {
        PrinterConfig printerConfig = repository.getByClientIp(request.getClientIp()).orElse(null);
        if (printerConfig!=null) throw new ValidateException(ResponseMessage.CLIENT_IP_IS_EXITS);
        PrinterConfig printer = modelMapper.map(request, PrinterConfig.class);
        if(printer.getRemoveAccent() == null) printer.setRemoveAccent(false);
        PrinterConfig printerDB = repository.save(printer);
        return modelMapper.map(printerDB, PrinterConfigDTO.class);
    }

    @Override
    public PrinterConfigDTO update(Long id, PrinterConfigRequest request) {
        if(!repository.existsById(id)) throw new ValidateException(ResponseMessage.PRINTER_CONFIG_NOT_FOUND);
        PrinterConfig printer = modelMapper.map(request, PrinterConfig.class);
        printer.setId(id);
        if(printer.getRemoveAccent() == null) printer.setRemoveAccent(false);
        PrinterConfig printerDB = repository.save(printer);
        return modelMapper.map(printerDB, PrinterConfigDTO.class);
    }

    @Override
    public PrinterConfigDTO getPrinter(String clientIp) {
        PrinterConfig config = repository.getByClientIp(clientIp).orElse(null);
        if(config == null) return null;
        if(config.getRemoveAccent() == null) config.setRemoveAccent(false);
        return modelMapper.map(config, PrinterConfigDTO.class);
    }
}
