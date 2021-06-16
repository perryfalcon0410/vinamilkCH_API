package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.messaging.OnlineOrderFilter;
import vn.viettel.sale.service.dto.OnlineOrderDTO;
import vn.viettel.sale.xml.DataSet;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface OnlineOrderService extends BaseService {

    Page<OnlineOrderDTO> getOnlineOrders(
            OnlineOrderFilter filter, Pageable pageable);

    OnlineOrderDTO getOnlineOrder(Long id, Long shopId, Long userId);

    String checkOnlineNumber(String code);

    DataSet syncXmlOnlineOrder(MultipartFile file) throws IOException;

    DataSet syncXmlToCancelOnlineOrder(MultipartFile file) throws IOException;

    String exportXmlFile(List<Long> ids);
}
