package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.entities.ComboProduct;
import vn.viettel.sale.entities.ComboProductTrans;
import vn.viettel.sale.entities.ComboProductTransDetail;
import vn.viettel.sale.messaging.ComboProductTranFilter;
import vn.viettel.sale.messaging.ComboProductTranRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.repository.ComboProductRepository;
import vn.viettel.sale.repository.ComboProductTransDetailRepository;
import vn.viettel.sale.repository.ComboProductTransRepository;
import vn.viettel.sale.service.ComboProductTransService;
import vn.viettel.sale.service.dto.ComboProductTranDTO;
import vn.viettel.sale.service.dto.ComboProductTransDetailDTO;
import vn.viettel.sale.specification.ComboProductTranSpecification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ComboProductTransServiceImpl
    extends BaseServiceImpl<ComboProductTrans, ComboProductTransRepository> implements ComboProductTransService {

    @Autowired
    ComboProductTransDetailRepository comboProductTransDetailRepo;

    @Autowired
    ComboProductRepository comboProductRepo;

    @Override
    public Response<CoverResponse<Page<ComboProductTranDTO>, TotalResponse>> findAll(ComboProductTranFilter filter, Pageable pageable) {

        if (filter.getFromDate() == null || filter.getToDate() == null) {
            LocalDate initial = LocalDate.now();
            filter.setFromDate(Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            filter.setToDate(new Date());
        }

        Page<ComboProductTrans> comboProductTrans = repository.findAll(Specification.where(
                ComboProductTranSpecification.hasTransCode(filter.getTransCode())
                .and(ComboProductTranSpecification.hasTransType(filter.getTransType()))
                .and(ComboProductTranSpecification.hasShopId(filter.getShopId()))
                .and(ComboProductTranSpecification.hasFromDateToDate(filter.getFromDate(), filter.getToDate()))
            ), pageable);

        Page<ComboProductTranDTO> pageProductTranDTOS = comboProductTrans.map(this::mapOnlineOrderToOnlineOrderDTO);

        List<ComboProductTrans> transList = repository.findAll();
        TotalResponse totalResponse = new TotalResponse();
        transList.forEach(trans -> {
            totalResponse.addTotalPrice(trans.getTotalAmount()).addTotalQuantity(trans.getTotalQuantity());
        });

        CoverResponse coverResponse = new CoverResponse(pageProductTranDTOS, totalResponse);
        return new Response<CoverResponse<Page<ComboProductTranDTO>, TotalResponse>>().withData(coverResponse);
    }

    @Override
    public Response<ComboProductTranDTO> create(ComboProductTranRequest request, Long shopId) {
        ComboProductTranDTO dto =  new ComboProductTranDTO();
        dto.setTransCode(this.createComboProductTranCode(shopId));

        return new Response<ComboProductTranDTO>().withData(dto);
    }



    public String createComboProductTranCode(Long shopId) {
        DateFormat df = new SimpleDateFormat("yy");
        LocalDate currentDate = LocalDate.now();
        String yy = df.format(Calendar.getInstance().getTime());
        Integer mm = currentDate.getMonthValue();
        Integer dd = currentDate.getDayOfMonth();
        ComboProductTrans comboProductTrans = repository.getComboProductTranTop1(shopId);

        return  "CUS." + comboProductTrans.getTransCode() + "." + Integer.toString( 1 + 100000).substring(1);
    }

    @Override
    public Response<ComboProductTranDTO> getComboProductTrans(Long id) {
        ComboProductTrans comboProductTran = repository.findById(id)
            .orElseThrow(() -> new ValidateException(ResponseMessage.COMBO_PRODUCT_TRANS_NOT_EXISTS));
        ComboProductTranDTO dto = modelMapper.map(comboProductTran, ComboProductTranDTO.class);

        List<ComboProductTransDetail> transDetails = comboProductTransDetailRepo.findByTransId(id);

        transDetails.forEach(detal -> {
            ComboProductTransDetailDTO combo = new ComboProductTransDetailDTO();
            ComboProduct comboProduct = comboProductRepo.findById(detal.getComboProductId()).
                orElseThrow(() -> new ValidateException(ResponseMessage.COMBO_PRODUCT_NOT_EXISTS));
            combo.setComboProductCode(comboProduct.getProductCode());
            combo.setComboProductName(comboProduct.getProductName());
        });

        return new Response<ComboProductTranDTO>().withData(dto);
    }

    private ComboProductTranDTO mapOnlineOrderToOnlineOrderDTO(ComboProductTrans tran) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ComboProductTranDTO dto = modelMapper.map(tran, ComboProductTranDTO.class);
        return dto;
    }



}
