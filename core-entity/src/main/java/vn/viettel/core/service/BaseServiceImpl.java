package vn.viettel.core.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import vn.viettel.core.db.entity.BaseEntity;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.core.security.context.SecurityContexHolder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public abstract class BaseServiceImpl<E extends BaseEntity, R extends BaseRepository<E>> implements BaseService {


    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    private SecurityContexHolder securityContexHolder;

    @PersistenceContext
    EntityManager entityManager;

    // date format
    public String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    public String formatDatetime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return formatter.format(dateTime);
    }
}
