package vn.viettel.sale.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.entities.PoConfirm;
import vn.viettel.sale.entities.WareHouseType;
import vn.viettel.sale.service.dto.PoConfirmXmlDTO;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface PoConfirmService extends BaseService {
    Response<PoConfirm> getPoConfirmById(Long id);
    void syncXmlPo(InputStream input, WareHouseType wareHouseType,List<Long> poConfirmIds) throws IOException;
    PoConfirmXmlDTO updatePoCofirm(Long shopId);

}
