package vn.viettel.common.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.common.entities.ApParam;
import vn.viettel.common.repository.ApParamRepository;
import vn.viettel.common.service.ApParamService;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApParamServiceImpl extends BaseServiceImpl<ApParam, ApParamRepository> implements ApParamService {

    @Override
    public ApParamDTO getApParamById(Long id) {
        Optional<ApParam> apParam = repository.findById(id);

        if (!apParam.isPresent()) {
            throw new ValidateException(ResponseMessage.AP_PARAM_NOT_EXISTS);
        }
        return modelMapper.map(apParam.get(), ApParamDTO.class);
    }

    @Override
    public List<ApParamDTO> getCardTypes() {
        List<ApParam> cardTypes = repository.getApParamByType("SALEMT_CUSTOMER_CARD");
        return cardTypes.stream().map(
                item -> modelMapper.map(item, ApParamDTO.class)).collect(Collectors.toList());
    }

    @Override
    public ApParamDTO getReason(Long id) {
        ApParam reason = repository.getApParamByIdAndType(id, "SALEMT_LY_DO_DC_KHO");
        if (reason == null) {
            throw new ValidateException(ResponseMessage.AP_PARAM_NOT_EXISTS);
        }
        return modelMapper.map(reason, ApParamDTO.class);
    }


    @Override
    public List<ApParamDTO> getCloselytypes() {
        List<ApParam> cardTypes = repository.getApParamByType("SALEMT_CLOSELY_CUSTOMER");
        return cardTypes.stream().map(
                item -> modelMapper.map(item, ApParamDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<ApParamDTO> getByType(String type) {
        List<ApParam> apParams = repository.findByTypeAndStatus(type, 1);

        try {
            if(type.equals("SALEMT_PAYMENT_TYPE")) {
                Collections.sort(apParams, new Comparator<ApParam>() {
                    @Override
                    public int compare(ApParam o1, ApParam o2) {
                        return new Integer(Integer.parseInt(o1.getValue())).compareTo(Integer.parseInt(o2.getValue()));
                    }
                });
                Collections.reverse(apParams);
            }

            if(type.equals("SALEMT_DELIVERY_TYPE") || type.equals("SALEMT_PROMOTION_OBJECT")) {
                Collections.sort(apParams, new Comparator<ApParam>() {
                    @Override
                    public int compare(ApParam o1, ApParam o2) {
                        return new Integer(Integer.parseInt(o1.getValue())).compareTo(Integer.parseInt(o2.getValue()));
                    }
                });
            }
        }catch (Exception e) {
//            e.printStackTrace();
        }

        return apParams.stream().map(
                item -> modelMapper.map(item, ApParamDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<ApParamDTO> getReasonNotImport() {
        List<ApParam> reasons = repository.getApParamByTypeAndStatus("SALEMT_PO_DENY",1);
        if (reasons == null) { new ArrayList<>();
//            throw new ValidateException(ResponseMessage.AP_PARAM_NOT_EXISTS);
        }
        return reasons.stream().map(
                item -> modelMapper.map(item, ApParamDTO.class)).collect(Collectors.toList());
    }

    public List<ApParamDTO> findAll() {
        List<ApParam> apParams = repository.findAll();

        List<ApParamDTO> apParamDTOList = new ArrayList<>();
        if(apParams != null){
            for(ApParam ap : apParams){
                ApParamDTO dto = modelMapper.map(ap, ApParamDTO.class);
                apParamDTOList.add(dto);
            }
        }

        return apParamDTOList;
    }

    public ApParamDTO getByCode(String code) {
        List<ApParam> apParam = repository.findByCode(code);
        if (apParam.isEmpty()) return null;
//            throw new ValidateException(ResponseMessage.AP_PARAM_NOT_EXISTS);
        return modelMapper.map(apParam.get(0), ApParamDTO.class);
    }

    @Override
    public List<ApParamDTO> getSalesChannel() {
        List<ApParam> apParam = repository.getSalesChannel();
        if (apParam.size() == 0) return new ArrayList<>();
        return apParam.stream().map(apParam1 -> modelMapper.map(apParam1 , ApParamDTO.class)).collect(Collectors.toList());
    }

    @Override
    public ApParamDTO getApParamByTypeAndvalue(String type, String value) {
        ApParam apParam = repository.findByTypeAndValueAndStatus(type, value, 1).orElse(null);
        if (apParam == null) return null;
        return modelMapper.map(apParam , ApParamDTO.class);
    }

    /*
       nếu k có cai nào thỏa thì order by theo value lấy đầu tiên
       nếu có nhiều hơn 1 cũng order by theo value lấy đầu tiên
     */
    @Override
    public ApParamDTO getApParamOnlineOrder(String discription) {
        List<ApParam> apParams = repository.getOnlineOrderType();
        if (apParams.isEmpty()) return null;
        //Do value là String mà của loại đơn là số nến sort số theo String bị sai nên convert sang số rồi sort.
        List<ApParamDTO> apParamDTOS = apParams.stream().map(a -> {
            ApParamDTO dto = modelMapper.map(a , ApParamDTO.class);
            if(a.getValue()!=null) dto.setIntValue(Integer.valueOf(a.getValue()));
            return dto;
        }).collect(Collectors.toList());

        Collections.sort(apParamDTOS, Comparator.comparing(ApParamDTO::getIntValue,Comparator.nullsLast(Comparator.naturalOrder())));

        if(discription != null) {
            discription = discription.trim().toUpperCase(Locale.ROOT);
            for(ApParamDTO app: apParamDTOS) {
                if(app.getDescription()!=null) {
                    String[] splitted = Arrays.stream(app.getDescription().split(",")).map(String::trim).toArray(String[]::new);
                    List<String> strings = new ArrayList<String>(Arrays.asList(splitted));
                    strings.replaceAll(String::toUpperCase);
                    if(strings.contains(discription)) return app;
                }
            }
        }

        return apParamDTOS.get(0);
    }

    @Override
    public ApParamDTO getByCode(String code, String type) {
        List<ApParam> apParam = repository.findByCode(code, type);
        if (apParam.isEmpty()) return null;
        return modelMapper.map(apParam.get(0), ApParamDTO.class);
    }

    @Override
    public List<ApParamDTO> getApParams(List<String> values, String type) {
        if(values == null  || values.isEmpty()) return null;
        List<ApParam> apParams = repository.getApParams(values, type);
        List<ApParamDTO> apParamDTOS = apParams.stream().map(a -> modelMapper.map(a , ApParamDTO.class)).collect(Collectors.toList());
        return apParamDTOS;
    }
}




