package vn.viettel.authorization.controller;

import vn.viettel.authorization.messaging.customer.CustomerCardCreateRequest;
import vn.viettel.authorization.messaging.customer.CustomerChangePasswordRequest;
import vn.viettel.authorization.messaging.customer.CustomerInfomationResponse;
import vn.viettel.authorization.service.*;
import vn.viettel.authorization.service.*;
import vn.viettel.authorization.service.dto.CardInfoDTO;
import vn.viettel.core.db.entity.Customer;
import vn.viettel.core.dto.CustomerRegisteringRequest;
import vn.viettel.core.dto.MemberCustomerInfoResponseDTO;
import vn.viettel.core.dto.common.BodyDTO;
import vn.viettel.core.dto.salon.CustomerQRDetailResponseDTO;
import vn.viettel.core.handler.HandlerException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleCustomer;
import vn.viettel.core.security.anotation.RoleFeign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer")
public class CustomerController extends HandlerException {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    CustomerInfomationService customerInfomationService;

    @Autowired
    UserService userService;

    @Autowired
    PayjpService payjpService;

    @Autowired
    CustomerAuthenticateService customerAuthenticateService;

    @Autowired
    CustomerService customerService;

    @RoleCustomer
    @PostMapping("/change-password")
    public Response<String> changePassword(@Valid @RequestBody CustomerChangePasswordRequest request) {
        logger.info("[changePassword()] - change password");
        return customerAuthenticateService.changePassword(request);
    }

    @RoleCustomer
    @GetMapping("/card")
    public Response<CardInfoDTO> getCustomerCard() {
        logger.info("[getCard()] - get Card #user_id: {}");
        return customerInfomationService.getCustomerCard();
    }

    @RoleCustomer
    @PostMapping("/card/create")
    public CustomerInfomationResponse<String> createCustomerCard(@Valid @RequestBody CustomerCardCreateRequest request) {
        logger.info("[createCustomerCard()] - create card of customer");
        return customerInfomationService.createCustomerCard(request);
    }

    @RoleFeign
    @GetMapping("/information/exist/{id}")
    public Boolean customerInfomationExist(@PathVariable("id") Long id) {
        logger.info("[customerInfomationExist()] - check exist customer information #id: {}", id);
        return customerInfomationService.exists(id);
    }

    @RoleFeign
    @GetMapping("/get-all-user-id-by-customer-name")
    public Response<List<Long>> getAllUserIdByCustomerName(@RequestParam("name") String name) {
        logger.info("[getAllUserIdByCustomerName()] - get all user id by customer name." + name);
        return customerInfomationService.getAllByCustomerName(name);
    }

    @RoleFeign
    @GetMapping("/getCustomerByPhone")
    public Optional<List<Customer>> getCustomerByPhone(@RequestParam("tel") String tel) {
        logger.info("[getCustomerByPhone()] - getCustomerByPhone #tel: {}", tel);
        return customerService.getCustomerByPhone(tel);
    }

    @RoleFeign
    @PostMapping("/addNewCustomer")
    public Response<String> addNewCustomer(@Valid @RequestBody CustomerRegisteringRequest request) {
        logger.info("[register()] - add new customer #lastname: {}", request.getLastName());
        logger.info("[register()] - add new customer #firstname: {}", request.getFirstName());
        logger.info("[register()] - add new customer #lastkatakananame: {}", request.getKatakanaLastName());
        logger.info("[register()] - add new customer #firstkatakananame: {}", request.getKatakanaFirstName());
        logger.info("[register()] - add new customer #salonId: {}", request.getSalonId());
        logger.info("[register()] - add new customer #birthday: {}", request.getBirthDay()
                + "/" + request.getBirthMonth()
                + "/" + request.getBirthYear());
        logger.info("[register()] - add new customer #tel: {}", request.getTel());
        return customerService.addNewCustomer(request);
    }

    @RoleFeign
    @PostMapping("/getCustomerByQrCode")
    public Response<CustomerQRDetailResponseDTO> getCustomerByQrCode(@RequestBody Map<String, Object> body) {
        logger.info("[getCustomerByQrCode()] - get customer by #qrCode: {}", body.get("qrCode").toString());
        return customerService.getCustomerByQRCode(body.get("qrCode").toString());
    }

    @RoleFeign
    @GetMapping("/getCustomerById")
    public Customer getCustomerByCustomerId(@RequestParam("id") Long id) {
        logger.info("[getCustomerByCustomerId()] - get customer by #id: {}", id);
        return customerService.getCustomerByCustomerId(id);
    }

    @RoleFeign
    @PostMapping("/updateCustomer")
    public Customer updateCustomer(@RequestBody Customer customer) {
        logger.info("[updateCustomer()]");
        return customerService.updateCustomer(customer);
    }

    @RoleFeign
    @PostMapping("/getCustomerEntityByQrCode")
    public Optional<Customer> getCustomerEntityByQRCode(@RequestParam("qrCode") String qrCode) {
        logger.info("[getCustomerEntityByQrCode()] - get customer entity by #qrCode: {}", qrCode);
        return customerService.getCustomerEntityByQRCode(qrCode);
    }

    @RoleFeign
    @GetMapping("/getCustomersByMemberId")
    public Optional<List<Customer>> getCustomerListByMemberId(@RequestParam("memberId") Long memberId) {
        logger.info("[getCustomerListByMemberId()] - get customer list by member id #memberId: {}", memberId);
        return customerService.getCustomerListByMemberId(memberId);
    }

    @RoleFeign
    @PostMapping("/feignGetCustomerListByCustomerIds")
    public List<Customer> feignGetCustomerListByCustomerIds(@RequestBody BodyDTO body) {
        logger.info("[feignGetCustomerListByCustomerIds()] - get customer list by customer ids #customerIds: {}", body.getIds());
        return customerService.feignGetCustomerListByCustomerIds(body.getIds());
    }

    @RoleFeign
    @GetMapping("/feignGetCustomerMemberRegistrationInfoByCustomerId")
    public MemberCustomerInfoResponseDTO feignGetCustomerMemberRegistrationInfoByCustomerId(
            @RequestParam("customerId") Long customerId) {
        logger.info("[feignGetCustomerListByCustomerIds()] - get customer (with member info) #customerId: {}", customerId);
        return customerService.feignGetCustomerMemberRegistrationInfoByCustomerId(customerId);
    }

    @RoleFeign
    @PostMapping("/feignGetCustomerMemberRegistrationInfoListByCustomerIds")
    public List<MemberCustomerInfoResponseDTO> feignGetCustomerMemberRegistrationInfoListByCustomerIds(
            @RequestBody BodyDTO body) {
        logger.info("[feignGetCustomerMemberRegistrationInfoListByCustomerIds()] - " +
                "get customer (with member info) list #customerIds: {}", body.getIds());
        return customerService.feignGetCustomerMemberRegistrationInfoListByCustomerIds(body.getIds());
    }
}
