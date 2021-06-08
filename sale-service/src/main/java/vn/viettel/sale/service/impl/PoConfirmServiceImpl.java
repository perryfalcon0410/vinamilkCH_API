package vn.viettel.sale.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.convert.XStreamTranslator;
import vn.viettel.sale.entities.PoConfirm;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.PoConfirmRepository;
import vn.viettel.sale.service.PoConfirmService;
import vn.viettel.sale.xml.NewDataSet;

import java.io.IOException;
import java.util.Optional;

@Service
public class PoConfirmServiceImpl extends BaseServiceImpl<PoConfirm, PoConfirmRepository> implements PoConfirmService {
    XStreamTranslator xStream = XStreamTranslator.getInstance();

    @Override
    public Response<PoConfirm> getPoConfirmById(Long id) {
        Optional<PoConfirm> poConfirm = repository.findById(id);
        if(!poConfirm.isPresent())
        {
            throw new ValidateException(ResponseMessage.PO_CONFIRM_NOT_EXISTS);
        }
        return new Response<PoConfirm>().withData(poConfirm.get());
    }

    @Override
    public NewDataSet syncXmlPo(MultipartFile file) throws IOException {
        NewDataSet newDataSet = (NewDataSet) xStream.toObject(file);
        return newDataSet;
    }
}
