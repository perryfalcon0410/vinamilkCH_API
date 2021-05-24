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
    public ApParamDTO getApParamById(Long id) {
        Optional<ApParam> apParam = repository.findById(id);

        if(!apParam.isPresent())
        {
            throw new ValidateException(ResponseMessage.AP_PARAM_NOT_EXISTS);
        }
        return modelMapper.map(apParam.get(), ApParamDTO.class);
    }

    @Override
    public List<ApParamDTO> getCardTypes() {
        List<ApParam> cardTypes = repository.findAll().stream()
                .filter(ap->ap.getType().equals("SALEMT_CUSTOMER_CARD")).collect(Collectors.toList());
        return cardTypes.stream().map(
                item -> modelMapper.map(item, ApParamDTO.class)).collect(Collectors.toList());
    }

    @Override
    public ApParamDTO getReason(Long id) {
        ApParam reason = repository.getApParamByIdAndType(id,"SALEMT_LY_DO_DC_KHO");
        if(reason == null)
        {
            throw new ValidateException(ResponseMessage.AP_PARAM_NOT_EXISTS);
        }
        return modelMapper.map(reason,ApParamDTO.class);
    }


    @Override
    public List<ApParamDTO> getCloselytypes() {
        List<ApParam> cardTypes = repository.findAll().stream()
                .filter(ap->ap.getType().equals("SALEMT_CLOSELY_CUSTOMER")).collect(Collectors.toList());
        return cardTypes.stream().map(
                item -> modelMapper.map(item, ApParamDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<ApParamDTO> getByType(String type) {
        List<ApParam> apParams = repository.findByTypeAndStatus(type, 1);
        return apParams.stream().map(
                item -> modelMapper.map(item, ApParamDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<ApParamDTO> getReasonNotImport() {
        List<ApParam> reasons = repository.getApParamByType("SALEMT_PO_DENY");
        if(reasons == null)
        {
            throw new ValidateException(ResponseMessage.AP_PARAM_NOT_EXISTS);
        }
        return reasons.stream().map(
                item -> modelMapper.map(item, ApParamDTO.class)).collect(Collectors.toList());
    }

    public List<ApParamDTO> findAll() {
        List<ApParam> apParams = repository.findAll();
        ApParamDTO apParamDTO = modelMapper.map(apParams, ApParamDTO.class);
        List<ApParamDTO> apParamDTOList = new ArrayList<>();
        apParamDTOList.add(apParamDTO);
        return apParamDTOList;
    }

    public ApParamDTO getByCode(String code) {
        Optional<ApParam> apParam = repository.findByCode(code);
        if(!apParam.isPresent())
            throw new ValidateException(ResponseMessage.AP_PARAM_NOT_EXISTS);
        return modelMapper.map(apParam.get(), ApParamDTO.class);
    }
}

