package vn.viettel.authorization.service.impl;

import vn.viettel.authorization.repository.CustomerRepository;
import vn.viettel.authorization.repository.MemberRepository;
import vn.viettel.authorization.service.CustomerService;
import vn.viettel.core.db.entity.Customer;
import vn.viettel.core.dto.CustomerRegisteringRequest;
import vn.viettel.core.dto.MemberCustomerInfoResponseDTO;
import vn.viettel.core.dto.salon.CustomerQRDetailResponseDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.ResponseMessage;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer, CustomerRepository> implements CustomerService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public Optional<List<Customer>> getCustomerByPhone(String tel) {
        return repository.findByTelAndDeletedAtIsNull(tel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<String> addNewCustomer(CustomerRegisteringRequest request) {
        Response<String> result = new Response<>();

        Optional<List<Customer>> alreadyCustomerWithPhone = repository.findByTelAndDeletedAtIsNull(request.getTel());
        if (alreadyCustomerWithPhone.isPresent()) {
            return result.withError(ResponseMessage.CUSTOMER_PHONE_NUMBER_IS_ALREADY_USED);
        }

        Customer customer = new Customer();
        customer.setSalonId(request.getSalonId());
        customer.setLastName(request.getLastName());
        customer.setFirstName(request.getFirstName());
        customer.setBirthday(LocalDate.of(Integer.valueOf(request.getBirthYear()),
                Integer.valueOf(request.getBirthMonth()),
                Integer.valueOf(request.getBirthDay())).atStartOfDay());
        customer.setTel(request.getTel());
        customer.setStatus((short) 1);
        customer.setKatakanaFirstName(request.getKatakanaFirstName());
        customer.setKatakanaLastName(request.getKatakanaLastName());
        if (request.getGender() != null) customer.setGender(request.getGender());
        if (!StringUtils.isEmpty(request.getZipCode())) customer.setZipcode(request.getZipCode());
        if (!StringUtils.isEmpty(request.getAddress())) customer.setAddress(request.getAddress());
        if (request.getPrefectureId() != null) customer.setPrefectureId(request.getPrefectureId());
        if (!StringUtils.isEmpty(request.getCity())) customer.setCity(request.getCity());
        if (request.getMemberId() != null) customer.setMemberId(request.getMemberId());
        repository.save(customer);

        // get qr code
        return result.withData(repository.updateCustomerQrCode(customer.getId()));
    }

    @Override
    public Response<CustomerQRDetailResponseDTO> getCustomerByQRCode(String qrCode) {
        Response<CustomerQRDetailResponseDTO> result = new Response<>();
        Optional<Customer> customer = repository.findByQrCodeAndDeletedAtIsNull(qrCode);
        CustomerQRDetailResponseDTO data = new CustomerQRDetailResponseDTO();

        if (!customer.isPresent()) {
            result.withError(ResponseMessage.CUSTOMER_QR_CODE_IS_INVALID);
        } else {
            data = customer.map(cus -> modelMapper.map(cus, CustomerQRDetailResponseDTO.class)).get();
        }

        result.withData(data);
        return result;
    }

    @Override
    public Customer getCustomerByCustomerId(Long id) {
        return repository.findByIdAndDeletedAtIsNull(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Customer updateCustomer(Customer customer) {
        repository.save(customer);
        return customer;
    }

    @Override
    public Optional<Customer> getCustomerEntityByQRCode(String qrCode) {
        return repository.findByQrCodeAndDeletedAtIsNull(qrCode);
    }

    @Override
    public Optional<List<Customer>> getCustomerListByMemberId(Long memberId) {
        return repository.findAllByMemberIdAndDeletedAtIsNull(memberId);
    }

    @Override
    public List<Customer> feignGetCustomerListByCustomerIds(List<Long> customerIds) {
        return repository.findByIdInAndDeletedAtIsNull(customerIds);
    }

    @Override
    public MemberCustomerInfoResponseDTO feignGetCustomerMemberRegistrationInfoByCustomerId(Long customerId) {
        return modelMapper.map(repository.getMemberCustomerRegistrationInfo(customerId),
                MemberCustomerInfoResponseDTO.class);
    }

    @Override
    public List<MemberCustomerInfoResponseDTO> feignGetCustomerMemberRegistrationInfoListByCustomerIds(
            List<Long> customerIds) {
        return repository.getMemberCustomerRegistrationInfoList(customerIds).stream().map(
                item -> modelMapper.map(item, MemberCustomerInfoResponseDTO.class)
        ).collect(Collectors.toList());
    }

    @Override
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer getCustomerByTelAndDeletedAtIsNotNull(String tel){
        return customerRepository.getCustomerByTelAndDeletedAtIsNotNull(tel);
    }

}
