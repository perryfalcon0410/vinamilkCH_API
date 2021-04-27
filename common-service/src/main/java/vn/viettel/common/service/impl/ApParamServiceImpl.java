package vn.viettel.common.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.common.entities.ApParam;
import vn.viettel.common.entities.Area;
import vn.viettel.common.repository.ApParamRepository;
import vn.viettel.common.service.ApParamService;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApParamServiceImpl extends BaseServiceImpl<ApParam, ApParamRepository> implements ApParamService {

    @Override
    public Response<ApParamDTO> getApParamById(Long id) {
        Optional<ApParam> apParam = repository.findById(id);

        if(!apParam.isPresent())
        {
            throw new ValidateException(ResponseMessage.AP_PARAM_NOT_EXISTS);
        }
        return new Response<ApParamDTO>().withData(modelMapper.map(apParam.get(), ApParamDTO.class));
    }

    @Override
    public Response<List<ApParamDTO>> getCardTypes() {
        List<ApParam> cardTypes = repository.findAll().stream()
                .filter(ap->ap.getType().equals("SALEMT_CUSTOMER_CARD")).collect(Collectors.toList());
        return new Response<List<ApParamDTO>>().withData(cardTypes.stream().map(
                item -> modelMapper.map(item, ApParamDTO.class)).collect(Collectors.toList()));
    }

    @Override
    public Response<ApParamDTO> getReason(Long id) {
        ApParam reason = repository.getApParamByIdAndType(id,"SALEMT_LY_DO_DC_KHO");
        if(reason == null)
        {
            throw new ValidateException(ResponseMessage.AP_PARAM_NOT_EXISTS);
        }
        return new Response<ApParamDTO>().withData(modelMapper.map(reason,ApParamDTO.class));
    }


    @Override
    public Response<List<ApParamDTO>> getCloselytypes() {
        List<ApParam> cardTypes = repository.findAll().stream()
                .filter(ap->ap.getType().equals("SALEMT_CLOSELY_CUSTOMER")).collect(Collectors.toList());
        return new Response<List<ApParamDTO>>().withData(cardTypes.stream().map(
                item -> modelMapper.map(item, ApParamDTO.class)).collect(Collectors.toList()));
    }

    @Override
    public Response<List<ApParamDTO>> getSaleMTPromotionObject() {
        List<ApParam> apParams = repository.findByTypeAndStatus("SALEMT_PROMOTION_OBJECT", 1);

        return new Response<List<ApParamDTO>>().withData(apParams.stream().map(
                item -> modelMapper.map(item, ApParamDTO.class)).collect(Collectors.toList()));
    }

    @Override
    public Response<List<ApParamDTO>> getReasonNotImport() {
        List<ApParam> reasons = repository.getApParamByType("SALEMT_PO_DENY");
        if(reasons == null)
        {
            throw new ValidateException(ResponseMessage.AP_PARAM_NOT_EXISTS);
        }
        return new Response<List<ApParamDTO>>().withData(reasons.stream().map(
                item -> modelMapper.map(item, ApParamDTO.class)).collect(Collectors.toList()));
    }

    public Response<List<ApParamDTO>> findAll() {
        List<ApParam> apParams = repository.findAll();
        ApParamDTO apParamDTO = modelMapper.map(apParams, ApParamDTO.class);
        List<ApParamDTO> apParamDTOList = new ArrayList<>();
        apParamDTOList.add(apParamDTO);
        Response<List<ApParamDTO>> response = new Response<>();
        response.setData(apParamDTOList);
        return response;
    }

    public Response<ApParamDTO> getByCode(String code) {
        Optional<ApParam> apParam = repository.findByCode(code);
        if(!apParam.isPresent())
            throw new ValidateException(ResponseMessage.AP_PARAM_NOT_EXISTS);
        return new Response<ApParamDTO>().withData(modelMapper.map(apParam.get(), ApParamDTO.class));
    }
}

