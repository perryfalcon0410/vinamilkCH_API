package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.core.db.entity.stock.ExchangeTrans;
import vn.viettel.core.db.entity.stock.ExchangeTransDetail;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.service.dto.PermissionDTO;
import vn.viettel.sale.repository.CategoryDataRepository;
import vn.viettel.sale.repository.ExchangeTransDetailRepository;
import vn.viettel.sale.repository.ExchangeTransRepository;
import vn.viettel.sale.service.ExchangeTranService;
import vn.viettel.sale.service.dto.ExchangeTransDTO;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.ExchangeTransSpecification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExchangeTranServiceImpl extends BaseServiceImpl<ExchangeTrans, ExchangeTransRepository> implements ExchangeTranService {
    @Autowired
    CategoryDataRepository categoryDataRepository;

    @Autowired
    ExchangeTransDetailRepository transDetailRepository;

    @Autowired
    UserClient userClient;


    @Override
    public Response<List<CategoryData>> getReasons() {
        List<CategoryData> reasons = categoryDataRepository.findByCategoryGroupCode();
        return new Response<List<CategoryData>>().withData(reasons);
    }

    @Override
    public Response<Page<ExchangeTransDTO>> getAllExchange(Long roleId, Long shopId, Long formId,
                                                           Long ctrlId, String transCode, Date fromDate,
                                                           Date toDate, Long reasonId, Pageable pageable) {
        if (fromDate == null || toDate == null) {
            LocalDate initial = LocalDate.now();
            fromDate = Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            toDate = Date.from(initial.withDayOfMonth(initial.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        Page<ExchangeTrans> exchangeTransList = repository.findAll(Specification
                .where(ExchangeTransSpecification.hasTranCode(transCode))
                .and(ExchangeTransSpecification.hasFromDateToDate(fromDate, toDate))
                .and(ExchangeTransSpecification.hasReasonId(reasonId)), pageable);

        List<ExchangeTransDTO> listResult = new ArrayList<>();
        exchangeTransList.forEach(exchangeTrans -> {
            listResult.add(mapExchangeToDTO(exchangeTrans));
        });

        // check user privilege
        if (!listResult.isEmpty() && listResult.get(0).getShopId() != shopId)
            return new Response<Page<ExchangeTransDTO>>().withError(ResponseMessage.USER_HAVE_NO_PRIVILEGE_ON_THIS_SHOP);
        List<PermissionDTO> permissionList = userClient.getUserPermission(roleId);
        if (!checkUserPermission(permissionList, formId, ctrlId))
            return new Response<Page<ExchangeTransDTO>>().withError(ResponseMessage.NO_FUNCTIONAL_PERMISSION);

        Page<ExchangeTransDTO> pageResult = new PageImpl<>(listResult);
        return new Response<Page<ExchangeTransDTO>>().withData(pageResult);
    }

    public CategoryData getReasonById(Long id) {
        return categoryDataRepository.getReasonById(id);
    }

    private ExchangeTransDTO mapExchangeToDTO(ExchangeTrans exchangeTrans) {
        List<ExchangeTransDetail> details = transDetailRepository.findByTransId(exchangeTrans.getId());

        ExchangeTransDTO result = modelMapper.map(exchangeTrans, ExchangeTransDTO.class);

        if (!details.isEmpty()) {
            int quantity = 0;
            float totalAmount = 0;
            for (ExchangeTransDetail detail : details) {
                quantity += detail.getQuantity();
                totalAmount += detail.getPrice()*detail.getQuantity();
            }
            result.setReason(getReasonById(exchangeTrans.getReasonId()).getCategoryName());
            result.setQuantity(quantity);
            result.setTotalAmount(totalAmount);
        }
        return result;
    }
}
