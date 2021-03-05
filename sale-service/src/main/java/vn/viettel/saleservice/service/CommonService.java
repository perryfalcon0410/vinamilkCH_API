package vn.viettel.saleservice.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.dto.ReasonDTO;


import java.util.List;

public interface CommonService {
    Response<List<ReasonDTO>> getAllReason();

}
