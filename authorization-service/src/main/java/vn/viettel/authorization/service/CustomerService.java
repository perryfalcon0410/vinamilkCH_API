package vn.viettel.authorization.service;

import vn.viettel.core.db.entity.Customer;
import vn.viettel.core.dto.CustomerRegisteringRequest;
import vn.viettel.core.dto.MemberCustomerInfoResponseDTO;
import vn.viettel.core.dto.salon.CustomerQRDetailResponseDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;
import java.util.Optional;

public interface CustomerService extends BaseService {
    /**
     * Find customers by phone
     *
     * @param tel telephone
     * @return Optional<List < Customer>>
     */
    Optional<List<Customer>> getCustomerByPhone(String tel);

    /**
     * add new customer (without membership)
     *
     * @param request CustomerRegisteringRequest
     * @return Response<String>
     */
    Response<String> addNewCustomer(CustomerRegisteringRequest request);

    /**
     * get customer information based on qrCode
     *
     * @param qrCode qr code
     * @return Response<CustomerQRDetailResponseDTO>
     */
    Response<CustomerQRDetailResponseDTO> getCustomerByQRCode(String qrCode);

    /**
     * Get customer By customer id
     *
     * @param id customer id
     * @return Customer
     */
    Customer getCustomerByCustomerId(Long id);

    /**
     * Update customer
     *
     * @param customer Customer entity
     * @return Customer entity
     */
    Customer updateCustomer(Customer customer);

    /**
     * Get customer entities by qr code
     *
     * @param qrCode qr code
     * @return Optional<Customer>
     */
    Optional<Customer> getCustomerEntityByQRCode(String qrCode);

    /**
     * Get customer list by member id
     *
     * @param memberId member id
     * @return Optional<List < Customer>>
     */
    Optional<List<Customer>> getCustomerListByMemberId(Long memberId);

    /**
     * Get customer list by customer ids (for feign client)
     *
     * @param customerIds ids of customer
     * @return List<Customer>
     */
    List<Customer> feignGetCustomerListByCustomerIds(List<Long> customerIds);

    /**
     * Get valid customer and member info (for feign client)
     *
     * @param customerId id of customer
     * @return MemberCustomerInfoResponseDTO
     */
    MemberCustomerInfoResponseDTO feignGetCustomerMemberRegistrationInfoByCustomerId(Long customerId);

    /**
     * Get list of valid customer and member info (for feign client)
     *
     * @param customerIds list id of customer
     * @return List<MemberCustomerInfoResponseDTO>
     */
    List<MemberCustomerInfoResponseDTO> feignGetCustomerMemberRegistrationInfoListByCustomerIds(List<Long> customerIds);

    /**
     * add new customer
     *
     * @param customer
     * @return Response<String>
     */
    Customer addCustomer(Customer customer);

    /**
     * add new customer
     *
     * @param tel
     * @return Response<String>
     */
    Customer getCustomerByTelAndDeletedAtIsNotNull(String tel);


}
