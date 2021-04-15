package vn.viettel.promotion.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.voucher.MemberCard;
import vn.viettel.core.db.entity.voucher.MemberCustomer;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.promotion.repository.MemBerCustomerRepository;
import vn.viettel.promotion.service.MemberCustomerService;
import vn.viettel.promotion.service.dto.MemberCustomerDTO;
import vn.viettel.promotion.service.feign.UserClient;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MemberCustomerServiceImpl extends BaseServiceImpl<MemberCustomer, MemBerCustomerRepository> implements MemberCustomerService {

    @Autowired
    UserClient userClient;

    @Override
    public Response<MemberCustomer> getMemberCustomerById(Long id) {
        Optional<MemberCustomer> memberCustomer = repository.findById(id);
        if(!memberCustomer.isPresent())
        {
            throw new ValidateException(ResponseMessage.MEMBER_CUSTOMER_NOT_EXIT);
        }
        return new Response<MemberCustomer>().withData(memberCustomer.get());
    }

    @Override
    public Response<MemberCustomer> create(MemberCustomerDTO memberCustomerDTO, Long userId) {

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        MemberCustomer memberCustomerRecord = modelMapper.map(memberCustomerDTO, MemberCustomer.class);
        memberCustomerRecord.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        if(userId != null)
        {
            memberCustomerRecord.setUpdateUser(userClient.getUserById(userId).getUserAccount());
        }
        repository.save(memberCustomerRecord);
        return new Response<MemberCustomer>().withData(memberCustomerRecord);
    }

    @Override
    public Response<MemberCustomer> getMemberCustomerByCustomerId(Long id) {
        Optional<MemberCustomer> memberCustomer = repository.getMemberCustomerByCustomerIdAndDeletedAtIsNull(id);

        if(!memberCustomer.isPresent())
        {
            throw new ValidateException(ResponseMessage.MEMBER_CUSTOMER_NOT_EXIT);
        }
        return new Response<MemberCustomer>().withData(memberCustomer.get());
    }

    @Override
    public Response<MemberCustomer> getMemberCustomerByIdCustomer(long id) {
        Optional<MemberCustomer> memberCustomer = repository.findByCustomerId(id);
        if(!memberCustomer.isPresent())
        {
            throw new ValidateException(ResponseMessage.MEMBER_CUSTOMER_NOT_EXIT);
        }
        return new Response<MemberCustomer>().withData(memberCustomer.get());
    }
}
