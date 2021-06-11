package vn.viettel.sale.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.sale.entities.PoDetail;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.PoDetailRepository;
import vn.viettel.sale.service.PoDetailService;

@Service
public class PoDetailServiceImpl extends BaseServiceImpl<PoDetail, PoDetailRepository> implements PoDetailService {

}
