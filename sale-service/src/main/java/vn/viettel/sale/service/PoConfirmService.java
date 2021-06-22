package vn.viettel.sale.service;

import org.springframework.web.multipart.MultipartFile;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.entities.PoConfirm;
import vn.viettel.sale.service.dto.PoConfirmXmlDTO;
import vn.viettel.sale.xml.NewDataSet;

import java.io.IOException;

public interface PoConfirmService extends BaseService {
    Response<PoConfirm> getPoConfirmById(Long id);
    NewDataSet syncXmlPo(MultipartFile file) throws IOException;
    PoConfirmXmlDTO updatePoCofirm();

}
