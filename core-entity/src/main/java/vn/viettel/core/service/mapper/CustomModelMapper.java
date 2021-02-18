package vn.viettel.core.service.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;

@Service
public class CustomModelMapper extends ModelMapper {

    public Object mapIfExist(Object source, Object destination) {
        if (source == null || destination == null) {
            return null;
        }
        map(source, destination);
        return destination;
    }

    public <D> D mapIfExist(Object source, Type destinationType) {
        if (source == null || destinationType == null) {
            return null;
        }
        return map(source, destinationType);
    }

}
