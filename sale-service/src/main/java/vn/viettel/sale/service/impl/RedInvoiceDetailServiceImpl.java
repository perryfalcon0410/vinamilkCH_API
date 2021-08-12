package vn.viettel.sale.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.RedInvoiceDetail;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.RedInvoiceDetailRepository;
import vn.viettel.sale.service.RedInvoiceDetailService;
import vn.viettel.sale.service.dto.RedInvoiceDetailDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RedInvoiceDetailServiceImpl extends BaseServiceImpl<RedInvoiceDetail, RedInvoiceDetailRepository> implements RedInvoiceDetailService {
    @Override
    public List<RedInvoiceDetailDTO> getRedInvoiceDetailByRedInvoiceId(Long id) {
        List<RedInvoiceDetail> redInvoiceDetails = repository.getAllByRedInvoiceId(id);
        List<RedInvoiceDetailDTO> redInvoiceDetailDTOS = redInvoiceDetails
                .stream().map(redInvoiceDetail -> modelMapper.map(redInvoiceDetail,RedInvoiceDetailDTO.class))
                .collect(Collectors.toList());

        return redInvoiceDetailDTOS;
    }

}
