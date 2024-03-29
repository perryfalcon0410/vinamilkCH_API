package vn.viettel.core.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.util.ResponseMessage;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

public class BaseReportServiceImpl implements BaseReportService{
    @PersistenceContext
    public EntityManager entityManager;

    @Value("${spring.application.name}")
    public String appName;

    @Autowired
    public ModelMapper modelMapper;

    public void setModelMapper(ModelMapper modelMapper){
        if(this.modelMapper == null) this.modelMapper = modelMapper;
    }

    @Override
    public void executeQuery(StoredProcedureQuery query, String procedureName, String parram ) {
        try {
            query.execute();
        }catch (Exception e) {
            LogFile.logErrorToFile(appName, "[Call Procedure " + procedureName + " ] " + parram, e);
            throw new ValidateException(ResponseMessage.CONNECT_DATABASE_FAILED);
        }finally {
            entityManager.close();
        }
    }

}
