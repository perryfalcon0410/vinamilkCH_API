package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.stock.PoConfirm;
import vn.viettel.core.db.entity.stock.PoDetail;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.PoDetailRepository;
import vn.viettel.sale.service.PoConfirmService;
import vn.viettel.sale.service.PoDetailService;
import vn.viettel.sale.specification.PoDetailSpecification;

@Service
public class PoDetailServiceImpl extends BaseServiceImpl<PoDetail, PoDetailRepository> implements PoDetailService {

    @Autowired
    PoConfirmService poConfirmService;

    @Override
    public Response<Page<PoDetail>> getAllByPoConfirmId(Long id, Pageable pageable) {
        PoConfirm poConfirm = poConfirmService.getPoConfirmById(id).getData();
        Page<PoDetail> poDetails  = repository.findAll(Specification.where(PoDetailSpecification.hashPoId(id)),pageable);
        return new Response<Page<PoDetail>>().withData(poDetails);
    }
}
