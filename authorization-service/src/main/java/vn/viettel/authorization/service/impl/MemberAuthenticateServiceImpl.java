package vn.viettel.authorization.service.impl;

import com.google.gson.Gson;
import vn.viettel.authorization.messaging.member.*;
import vn.viettel.authorization.messaging.member.*;
import vn.viettel.authorization.repository.MemberRepository;
import vn.viettel.authorization.repository.PrefectureRepository;
import vn.viettel.authorization.repository.SalonMemberRepository;
import vn.viettel.authorization.security.ClaimsTokenBuilder;
import vn.viettel.authorization.security.JwtTokenCreate;
import vn.viettel.authorization.service.*;
import vn.viettel.authorization.service.*;
import vn.viettel.authorization.service.dto.PasswordResetDTO;
import vn.viettel.authorization.service.dto.authorization.MemberUserLoginDTO;
import vn.viettel.authorization.service.feign.CompanyClient;
import vn.viettel.authorization.service.feign.SalonClient;
import vn.viettel.authorization.utils.UserUtils;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.db.entity.commonEnum.Feature;
import vn.viettel.core.db.entity.role.UserRole;
import vn.viettel.core.dto.CustomerRegisteringRequest;
import vn.viettel.core.dto.company.CompanyFeatureListDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.GenerateUtils;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.util.ValidationUtils;
import vn.viettel.core.exception.FeatureNotAvailableException;
import io.jsonwebtoken.Claims;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.viettel.authorization.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MemberAuthenticateServiceImpl extends BaseServiceImpl<Member, MemberRepository> implements MemberAuthenticateService {

    private final int FORGOT_PASSWORD_TOKEN_EXPIRED_DAYS = 1;

    @Autowired
    RoleService roleService;

    @Autowired
    PayjpService payjpService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PasswordResetService passwordResetService;

    @Autowired
    EmailService emailService;

    @Autowired
    JwtTokenCreate jwtTokenCreate;

    @Autowired
    SalonMemberRepository salonMemberRepository;

    @Autowired
    SalonClient salonClient;

    @Autowired
    CompanyClient companyClient;

    @Autowired
    ChannelService channelService;

    @Autowired
    CustomerService customerService;

    @Autowired
    PrefectureRepository prefectureRepository;

    private void checkReservationFeature(Long companyId) throws FeatureNotAvailableException {
        List<CompanyFeatureListDTO> featureList =
                companyClient.feignGetAvailableFeatureListByCompanyId(companyId);

        // check if the feature is currently on
        CompanyFeatureListDTO companyFeature =
                featureList.stream().filter(item -> item.getName().contentEquals(Feature.RESERVATION.getName())).findAny().orElse(null);
        if (companyFeature == null || companyFeature.getStatus()==null || !companyFeature.getStatus()) {
            throw new FeatureNotAvailableException(ResponseMessage.COMPANY_FEATURE_NOT_AVAILABLE);
        }
    }

    /* LOGIN */
    @Override
    public Response<MemberUserLoginDTO> login(MemberLoginRequest request) throws FeatureNotAvailableException {
        Company company = companyClient.getCompanyBySlug(request.getCompanySlug());

        checkReservationFeature(company.getId());

        Response<MemberUserLoginDTO> response = new Response<>();
        MemberUserLoginDTO userLogin = memberLogin(request.getEmail(), request.getPassword(), company.getId());
        if (userLogin != null) {
            response.setData(userLogin);
            return response;
        }

        response.setFailure(ResponseMessage.LOGIN_FAILED);
        return response;
    }

    /* FORGOT PASSWORD */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<String> forgotPassword(MemberForgotPasswordRequest request) throws Exception {
        Company company = companyClient.getCompanyBySlug(request.getCompanySlug());

        checkReservationFeature(company.getId());

        Response<String> response = new Response<String>();
        Member user = getMemberActivatedByEmail(request.getEmail(), company.getId());
        if (user == null) {
            response.setFailure(ResponseMessage.NOT_EXISTS_EMAIL);
            return response;
        }
        PasswordResetDTO passwordResetOld = passwordResetService.getByMemberIdAndCompanyId(user.getId(), company.getId()).getData();
        removeForgotPasswordToken(passwordResetOld);
        String token = UUID.randomUUID().toString();
        PasswordResetDTO passwordResetNew = UserUtils.createPasswordResetDTOForMember(token, user.getId(), company.getId());
        passwordResetService.save(passwordResetNew, PasswordResetDTO.class);
        emailService.sendEmailMemberForgotPassword(user, token, request.getCompanySlug(), request.getSalonSlug());
        return response;
    }

    /* CHECK VALID RESET PASSWORD TOKEN */
    @Override
    public Response<String> checkValidResetPasswordToken(String token, String companySlug, String salonSlug) {
        Company company = companyClient.getCompanyBySlug(companySlug);
        Response<String> response = new Response<String>();
        PasswordResetDTO passwordReset = passwordResetService.getByTokenAndCompanyId(token, company.getId()).getData();
        if (passwordReset == null) {
            response.setFailure(ResponseMessage.INVALID_TOKEN);
            return response;
        }
        if (!isForgotPasswordTokenBelongsToMember(passwordReset, company.getId())) {
            response.setFailure(ResponseMessage.INVALID_TOKEN);
            return response;
        }
        if (isExpiredToken(passwordReset.getCreatedAt(), FORGOT_PASSWORD_TOKEN_EXPIRED_DAYS)) {
            response.setFailure(ResponseMessage.USER_FORGOT_PASSWORD_TOKEN_HAS_EXPIRED);
            return response;
        }
        return response;
    }

    /* UPDATE FORGOT PASSWORD */
    @Override
    public Response<String> updateForgotPassword(MemberUpdateForgotPasswordRequest request) {
        Company company = companyClient.getCompanyBySlug(request.getCompanySlug());
        Response<String> response = new Response<String>();
        PasswordResetDTO passwordReset = passwordResetService.getByTokenAndCompanyId(request.getToken(), company.getId()).getData();
        if (passwordReset == null) {
            response.setFailure(ResponseMessage.INVALID_TOKEN);
            return response;
        }
        if (!isForgotPasswordTokenBelongsToMember(passwordReset, company.getId())) {
            response.setFailure(ResponseMessage.INVALID_TOKEN);
            return response;
        }
        if (isExpiredToken(passwordReset.getCreatedAt(), FORGOT_PASSWORD_TOKEN_EXPIRED_DAYS)) {
            response.setFailure(ResponseMessage.USER_FORGOT_PASSWORD_TOKEN_HAS_EXPIRED);
            return response;
        }
        Optional<Member> optUser = repository.getMemberById(passwordReset.getMemberId(), company.getId());
        if (!optUser.isPresent()) {
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
            return response;
        }
        Member user = optUser.get();
        removeForgotPasswordTokenForMember(user.getId());
        user.setEncryptedPassword(passwordEncoder.encode(request.getPassword()));
        repository.save(user);
        return response;
    }

    @Override
    public MemberUserLoginDTO memberLogin(String email, String password, Long companyId) {
        Member user = getMemberActivatedByEmail(email, companyId);
        if (user == null) {
            return null;
        }
        if (!passwordEncoder.matches(password, user.getEncryptedPassword())) {
            return null;
        }

        String role = UserRole.MEMBER.value();
        Claims claims = ClaimsTokenBuilder.build(role)
                .withUserId(user.getId())
                .withCompanyId(user.getCompanyId()).get();
        String token = jwtTokenCreate.createToken(claims);

        MemberUserLoginDTO userLogin = modelMapper.map(user, MemberUserLoginDTO.class);
        userLogin.setToken(token);
        userLogin.setRole(role);
        return userLogin;
    }

    /* GET Admin USER ACTIVATED BY EMAIL */
    private Member getMemberActivatedByEmail(String email, Long companyId) {
        try {
            Optional<Member> optUser = repository.getMemberByEmail(email, companyId);
            if (optUser.isPresent()) {
                if (optUser.get().isStatus()) {
                    return optUser.get();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private Member getMemberByEmail(String email, Long companyId) {
        try {
            Optional<Member> optUser = repository.getMemberByEmail(email, companyId);
            if (optUser.isPresent()) {
                return optUser.get();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private Member getMemberByEmail(String email) {
        try {
            Optional<Member> optUser = repository.getMemberByEmail(email);
            if (optUser.isPresent()) {
                return optUser.get();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /* GET Admin ROLE */
    private Role getAdministratorRole() {
        return roleService.getByRoleName(UserRole.ADMIN);
    }


    /* REMOVE FORGOT PASSWORD TOKEN */
    private void removeForgotPasswordToken(PasswordResetDTO passwordReset) {
        if (passwordReset != null) {
            passwordReset.setDeletedAt(LocalDateTime.now());
            passwordResetService.save(passwordReset, PasswordResetDTO.class);
        }
    }

    private void removeForgotPasswordTokenForMember(Long memberId) {
        List passwordResets = passwordResetService.getByMember(memberId);
        passwordResets.forEach(passwordReset -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PasswordResetDTO dto = modelMapper.map(passwordReset, PasswordResetDTO.class);
            dto.setDeletedAt(LocalDateTime.now());
            passwordResetService.save(dto, PasswordResetDTO.class);
        });
    }

    /* CHECK FORGOT PASSWORD TOKEN BELONGS TO ADMIN */
    private boolean isForgotPasswordTokenBelongsToMember(PasswordResetDTO passwordReset, Long companyId) {
        Long userId = passwordReset.getMemberId();
        try {
            Optional<Member> optUser = repository.getMemberById(userId, companyId);
            return optUser.isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    /* CHECK EXPIRED TOKEN */
    private boolean isExpiredToken(LocalDateTime dateCreateToken, int numberOfDayExpired) {
        return dateCreateToken.plusDays(numberOfDayExpired)
                .isBefore(LocalDateTime.now());
    }

    @Override
    public Response<String> sendActivationCode(MemberSendEmailRegistrationRequest request) {
        Response<String> response = new Response();
        Member member = this.getMemberByEmail(request.getEmail());
        if (member != null) {
            if (member.isStatus()) {
                return response.withError(ResponseMessage.ALREADY_EXISTS_EMAIL);
            }
            Member memberUpdate = this.updateActivationCode(request);
            this.emailService.sendEmailMemberUserActive(memberUpdate, member.getActivationCode(), request);
            return response.withData("Update activation code");
        }
        Member memberCreate = this.createActivationCode(request);
        this.emailService.sendEmailMemberUserActive(memberCreate, memberCreate.getActivationCode(), request);
        return response.withData("Create member and activation code");
    }

    private Member createActivationCode(MemberSendEmailRegistrationRequest request) {
        String activationCode = UUID.randomUUID().toString();

        Member member = new Member();

        member.setEmail(request.getEmail());
        member.setCompanyId(request.getCompanyId());
        member.setStatus(false);
        member.setActivationCode(activationCode);
        member.setName(request.getEmail());
        member.setEncryptedPassword(passwordEncoder.encode("1"));

        return repository.save(member);
    }

    private Member updateActivationCode(MemberSendEmailRegistrationRequest request) {
        String activationCode = UUID.randomUUID().toString();

        Member member = this.getMemberByEmail(request.getEmail());
        member.setCompanyId(request.getCompanyId());
        member.setActivationCode(activationCode);

        return repository.save(member);
    }

    @Override
    public Response<String> checkValidActivationCode(String email, String companySlug, String salonSlug, String token) {
        Company company = companyClient.getCompanyBySlug(companySlug);
        Response<String> response = new Response<String>();
        Member member = this.getMemberByEmail(email);
        if (member == null) {
            return response.withError(ResponseMessage.INVALID_TOKEN);
        }

        if (member.isStatus()) {
            return response.withError(ResponseMessage.INVALID_TOKEN);
        }

        if (!(member.getActivationCode() != null
                && member.getActivationCode().equals(token)
                && member.getCompanyId().equals(company.getId()))) {
            return response.withError(ResponseMessage.INVALID_TOKEN);
        }

        return response;
    }

    @Override
    public Response<MemberRegisterResponse> precheckRegisterAccount(MemberRegisterRequest request) {
        Response<MemberRegisterResponse> response = new Response<MemberRegisterResponse>();

        Member memberByEmail = this.getMemberByEmail(request.getEmail());
        if (memberByEmail == null) {
            return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);
        }

        if (!(memberByEmail.getActivationCode() != null && memberByEmail.getActivationCode().equals(request.getToken()))) {
            return response.withError(ResponseMessage.INVALID_TOKEN);
        }

        if (memberByEmail.isStatus()) {
            return response.withError(ResponseMessage.ALREADY_EXISTS_EMAIL);
        }

        // creating new salon_member
        Salon salon = salonClient.getExistedSalonBySlug(request.getSalonSlug());
        if (salon == null) {
            return response.withError(ResponseMessage.SALON_DOES_NOT_EXIST);
        }

        Company company = companyClient.getCompanyBySlug(request.getCompanySlug());
        if (company == null) {
            return response.withError(ResponseMessage.COMPANY_DOES_NOT_EXIST);
        }

        Optional<List<Member>> alreadyExistTel = repository.getAllByTelAndDeletedAtIsNull(request.getTel());
        if (alreadyExistTel.isPresent()) {
            return response.withError(ResponseMessage.MEMBER_PHONE_NUMBER_IS_ALREADY_USED);
        }

        Prefecture prefecture = prefectureRepository.findByIdAndDeletedAtIsNull(request.getPrefectureId());
        if (prefecture == null) {
            return response.withError(ResponseMessage.PREFECTURE_NOT_EXISTED);
        }

        if (StringUtils.isEmpty(request.getEncryptedPassword())) {
            return response.withError(ResponseMessage.USER_PASSWORD_MUST_BE_NOT_NULL);
        }
        if (!ValidationUtils.checkPassword(request.getEncryptedPassword())) {
            return response.withError(ResponseMessage.USER_PASSWORD_MUST_BE_GREATER_THAN_SIX_CHARACTER);
        }


        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<MemberRegisterResponse> registerAccount(MemberRegisterRequest request) {
        Response<MemberRegisterResponse> response = new Response<MemberRegisterResponse>();
        Member memberResult = new Member();

        Member memberByEmail = this.getMemberByEmail(request.getEmail());
        if (memberByEmail == null) {
            return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);
        }

        if (!(memberByEmail.getActivationCode() != null && memberByEmail.getActivationCode().equals(request.getToken()))) {
            return response.withError(ResponseMessage.INVALID_TOKEN);
        }

        if (memberByEmail.isStatus()) {
            return response.withError(ResponseMessage.ALREADY_EXISTS_EMAIL);
        }

        Optional<List<Member>> alreadyExistTel = repository.getAllByTelAndDeletedAtIsNull(request.getTel());
        if (alreadyExistTel.isPresent()) {
            return response.withError(ResponseMessage.MEMBER_PHONE_NUMBER_IS_ALREADY_USED);
        }

        Response<Member> memberResponse = mapRequestDataToMember(memberByEmail, request);
        if (memberResponse.getStatusCode() != 200) {
            return response.withError(ResponseMessage.getByStatus(memberResponse.getStatusCode()));
        }
        Member member = memberResponse.getData();
        memberResult = repository.save(member);

        // creating new salon_member
        Salon salon = salonClient.getExistedSalonBySlug(request.getSalonSlug());
        if (salon == null) {
            return response.withError(ResponseMessage.SALON_DOES_NOT_EXIST);
        }
        SalonMember salonMember = new SalonMember();
        salonMember.setMemberId(member.getId());
        salonMember.setSalonId(salon.getId());
        salonMemberRepository.save(salonMember);

        // saving customer
        CustomerRegisteringRequest customerRegisteringRequest = new CustomerRegisteringRequest();
        customerRegisteringRequest.setSalonId(salon.getId());
        customerRegisteringRequest.setLastName(member.getLastName());
        customerRegisteringRequest.setFirstName(member.getFirstName());
        customerRegisteringRequest.setKatakanaFirstName(member.getKatakanaFirstName());
        customerRegisteringRequest.setKatakanaLastName(member.getKatakanaLastName());
        customerRegisteringRequest.setTel(request.getTel());
        customerRegisteringRequest.setBirthDay(request.getBirthDate());
        customerRegisteringRequest.setBirthMonth(request.getBirthMonth());
        customerRegisteringRequest.setBirthYear(request.getBirthYear());
        customerRegisteringRequest.setGender(request.getGender());
        customerRegisteringRequest.setAddress(request.getAddress());
        customerRegisteringRequest.setPrefectureId(request.getPrefectureId());
        customerRegisteringRequest.setCityId(request.getCityId());
        customerRegisteringRequest.setPlaceId(request.getPlaceId());
        customerRegisteringRequest.setZipCode(request.getZipcode());
        customerRegisteringRequest.setCity(request.getCity());
        customerRegisteringRequest.setMemberId(member.getId());
        customerService.addNewCustomer(customerRegisteringRequest);

        return response.withData(modelMapper.map(memberResult, MemberRegisterResponse.class));
    }

    private Response<Member> mapRequestDataToMember(Member member, MemberRegisterRequest request) {
        Response<Member> result = new Response<Member>();
        Company company = companyClient.getCompanyBySlug(request.getCompanySlug());
        if (company == null) {
            return result.withError(ResponseMessage.COMPANY_DOES_NOT_EXIST);
        }
        member.setCompanyId(company.getId());
        member.setEmail(request.getEmail());
        if (StringUtils.isEmpty(request.getEncryptedPassword())) {
            return result.withError(ResponseMessage.USER_PASSWORD_MUST_BE_NOT_NULL);
        }
        if (!ValidationUtils.checkPassword(request.getEncryptedPassword())) {
            return result.withError(ResponseMessage.USER_PASSWORD_MUST_BE_GREATER_THAN_SIX_CHARACTER);
        }
        member.setEncryptedPassword(passwordEncoder.encode(request.getEncryptedPassword()));
        member.setStatus(true);
        member.setGender(request.getGender());
        member.setBirthday(LocalDate.of(Integer.valueOf(request.getBirthYear()),
                Integer.valueOf(request.getBirthMonth()),
                Integer.valueOf(request.getBirthDate())));
        member.setKatakanaFirstName(request.getKatakanaFirstName());
        member.setKatakanaLastName(request.getKatakanaLastName());
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setTel(request.getTel());
        member.setZipcode(request.getZipcode());
        member.setCity(request.getCity());
        member.setAddress(request.getAddress());
        Prefecture prefecture = prefectureRepository.findByIdAndDeletedAtIsNull(request.getPrefectureId());
        if (prefecture == null) {
            return result.withError(ResponseMessage.PREFECTURE_NOT_EXISTED);
        }
        member.setPrefectureId(request.getPrefectureId());
        member.setCityId(request.getCityId());
        member.setPlaceId(request.getPlaceId());
        member.setActivationCode(null);
        member.setPhotoUrl(request.getPhotoUrl());
        // member channel
        member.setChannel(request.getChannel() == null ? null : new Gson().toJson(request.getChannel()));
        member.setQrCode(GenerateUtils.generateQRCode(member.getId()));
        return result.withData(member);
    }
}
