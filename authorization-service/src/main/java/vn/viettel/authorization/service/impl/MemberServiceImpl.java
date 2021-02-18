package vn.viettel.authorization.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import vn.viettel.authorization.messaging.member.AddNewMemberRequest;
import vn.viettel.authorization.messaging.member.MemberUpdatingRequest;
import vn.viettel.authorization.repository.*;
import vn.viettel.authorization.repository.*;
import vn.viettel.authorization.repository.specification.MemberSRepository;
import vn.viettel.authorization.service.ChannelService;
import vn.viettel.authorization.service.CustomerService;
import vn.viettel.authorization.service.MemberService;
import vn.viettel.authorization.service.feign.CompanyClient;
import vn.viettel.authorization.service.feign.ReceptionClient;
import vn.viettel.authorization.service.feign.SalonClient;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.db.entity.commonEnum.Channel;
import vn.viettel.core.db.entity.commonEnum.MemberListColumn;
import vn.viettel.core.dto.ManagementUserNameResponseDTO;
import vn.viettel.core.dto.MemberBookingHistoryResponseDTO;
import vn.viettel.core.dto.SalonMemberResponseDTO;
import vn.viettel.core.dto.booking.BookingHistoryResponseDTO;
import vn.viettel.core.dto.salon.MemberQRDetailResponseDTO;
import vn.viettel.core.dto.search.CustomPage;
import vn.viettel.core.dto.search.MemberSearchDTO;
import vn.viettel.core.dto.search.Param;
import vn.viettel.core.dto.user.ChannelTypeResponseDTO;
import vn.viettel.core.dto.user.FilledPlacesResponseDTO;
import vn.viettel.core.dto.user.MemberManagementListResponseDTO;
import vn.viettel.core.dto.user.MemberProfileInformationResponseDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.GenerateUtils;
import vn.viettel.core.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl extends BaseServiceImpl<Member, MemberRepository> implements MemberService {

    @Autowired
    MemberCouponRepository memberCouponRepository;

    @Autowired
    SalonMemberRepository salonMemberRepository;

    @Autowired
    SalonClient salonClient;

    @Autowired
    CompanyClient companyClient;

    @Autowired
    PrefectureRepository prefectureRepository;

    @Autowired
    PlaceRepository placeRepository;

    @Autowired
    ChannelService channelService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CustomerService customerService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberSRepository memberSRepository;

    @Autowired
    ReceptionClient receptionClient;

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public MemberQRDetailResponseDTO getMemberCustomerRegistrationByQrCode(String qrCode) {

        return modelMapper.map(repository.getMemberCustomerRegistrationByQrCode(qrCode),
                MemberQRDetailResponseDTO.class);
    }

    @Override
    public MemberQRDetailResponseDTO getMemberCustomerRegistrationById(Long id) {
        return modelMapper.map(repository.getMemberCustomerRegistrationById(id),
                MemberQRDetailResponseDTO.class);
    }

    @Override
    public Member addNewMember(Member member) {
        repository.save(member);
        return member;
    }

    @Override
    public Member getMemberById(Long id) {
        return repository.findByIdAndDeletedAtIsNull(id);
    }

    @Override
    public Member updateMember(Member member) {
        repository.save(member);
        return member;
    }

    @Override
    public Member getMemberByQr(String qrCode) {
        return repository.findByQrCodeAndDeletedAtIsNull(qrCode);
    }

    @Override
    public SalonMemberResponseDTO getSalonMember(Long memberId) {
        SalonMemberResponseDTO data = modelMapper.map(repository.getSalonMember(memberId), SalonMemberResponseDTO.class);
        data.setSalonIdList(
                Arrays.asList(data.getSalonIds().split(","))
                        .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList()));
        return data;
    }

    @Override
    public List<Member> getFromAndToMember(Long fromMemberId, Long toMemberId) {
        return repository.getFromAndToMember(fromMemberId, toMemberId);
    }

    @Override
    public List<Long> getCouponIdsFromMemberId(Long memberId) {
        List<MemberCoupon> memberCouponList = memberCouponRepository.findAllByMemberIdAndDeletedAtIsNull(memberId).orElse(null);
        if (!CollectionUtils.isEmpty(memberCouponList)) {
            return memberCouponList.stream().map(
                    MemberCoupon::getCouponId
            ).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<MemberCoupon> getCouponMemberFromMemberId(Long memberId) {
        return memberCouponRepository.findAllByMemberIdAndDeletedAtIsNull(memberId).orElse(null);
    }

    @Override
    public Response<String> feignImportCouponCodeForMemberIdWithoutCheckingCouponCode(Long couponId, Long memberId) {
        Response<String> result = new Response<>();

        // 1. check valid memberId
        Member member = repository.findByIdAndDeletedAtIsNull(memberId);
        if (member == null) {
            return result.withError(ResponseMessage.MEMBER_IS_NOT_EXISTED);
        }

        // 2. insert to member coupon table
        MemberCoupon memberCoupon = memberCouponRepository.findByCouponIdAndMemberIdAndDeletedAtIsNull(couponId, memberId);
        if (memberCoupon == null) memberCoupon = new MemberCoupon();
        memberCoupon.setCouponId(couponId);
        memberCoupon.setMemberId(memberId);
        memberCoupon.setUsed(false);
        memberCouponRepository.save(memberCoupon);

        return result;
    }

    @Override
    public Response<MemberProfileInformationResponseDTO> getMemberInformation(Long memberId) {
        Response<MemberProfileInformationResponseDTO> result = new Response<>();
        MemberProfileInformationResponseDTO data = new MemberProfileInformationResponseDTO();

        // 1. check valid member
        Member member = repository.findByIdAndDeletedAtIsNull(memberId);
        if (member == null) {
            return result.withError(ResponseMessage.MEMBER_IS_NOT_EXISTED);
        }

        // 2. get main data of member
        data.setMemberId(memberId);
        data.setMemberQr(member.getQrCode());
        data.setFirstName(member.getFirstName());
        data.setLastName(member.getLastName());
        data.setKatakanaFirstName(member.getKatakanaFirstName());
        data.setKatakanaLastName(member.getKatakanaLastName());
        data.setBirthday(member.getBirthday());
        data.setTel(member.getTel());
        data.setEmail(member.getEmail());
        data.setLongEPassword(member.getEncryptedPassword().length() > 10 ? 10 : member.getEncryptedPassword().length());
        data.setZipcode(member.getZipcode());

        if (member.getPlaceId() != null) {
            FilledPlacesResponseDTO place = placeRepository.getByPlaceId(member.getPlaceId());
            data.setAddress(member.getAddress());
            if (place != null) {
                data.setFullAddress(place.getPlaceName() + " , " + place.getCityName() + " " + data.getAddress());
                data.setInitialAddress(place.getPlaceName() + " , " + place.getCityName());
            }
        }


        data.setCity(member.getCity());
        data.setCityId(member.getCityId());
        data.setPlaceId(member.getPlaceId());
        data.setGender(member.getGender());
        data.setPoint(member.getPoint());
        // find salon member
        Optional<List<SalonMember>> salonMembers = salonMemberRepository.findAllByMemberIdAndDeletedAtIsNull(memberId);
        if (!salonMembers.isPresent()) {
            return result.withError(ResponseMessage.RESERVATION_MEMBER_HAS_NO_MY_SALON);
        }
        Salon salon = salonClient.getExistedSalon(salonMembers.get().get(0).getSalonId());
        if (salon == null) {
            return result.withError(ResponseMessage.RESERVATION_MEMBER_HAS_NO_MY_SALON);
        }
        data.setSalonId(salon.getId());
        data.setSalonName(salon.getName());
        data.setSalonSlug(salon.getSlug());
        // find prefecture
        if (member.getPrefectureId() != null) {
            Prefecture prefecture = prefectureRepository.findByIdAndDeletedAtIsNull(member.getPrefectureId());
            if (prefecture == null) {
                return result.withError(ResponseMessage.PREFECTURE_NOT_EXISTED);
            }
            data.setPrefectureId(prefecture.getId());
            data.setPrefecture(prefecture.getName());
        }
        // reading channel data
        List<ChannelTypeResponseDTO> channelMasterData = channelService.getChannelListInformationMaster();
        data.setChannels(channelMasterData);

        if (!StringUtils.isEmpty(member.getChannel())) {
            Type channelListType = new TypeToken<ArrayList<ChannelTypeResponseDTO>>() {
            }.getType();
            List<ChannelTypeResponseDTO> channelsData = new Gson().fromJson(member.getChannel(), channelListType);

            // 2.1 finding knownSources
            List<Long> parentIds = Arrays.asList(
                    Channel.MEMBER_FAMILY_STRUCTURE.getId(),
                    Channel.MEMBER_FAVORITE_STYLE.getId(),
                    Channel.MEMBER_OCCUPATION.getId(),
                    Channel.MEMBER_PROFESSION.getId(),
                    Channel.MEMBER_WORRIES.getId(),
                    Channel.MEMBER_KNOWN_SOURCE.getId()
            );

            parentIds.forEach(parentId -> {
                Optional<ChannelTypeResponseDTO> channel =
                        channelsData.stream().filter(channelData -> channelData.getId().equals(parentId)).findAny();
                if (channel.isPresent()) {
                    ChannelTypeResponseDTO master = channelMasterData.stream().filter(channelMaster -> channelMaster.getId().equals(parentId))
                            .findAny().get();

                    channel.get().getTypeIds().forEach(childChannel -> {
                        master.getTypeIds().stream().filter(item -> item.getId().equals(childChannel.getId()))
                                .findAny().get().setCheck(true);
                    });
                }
            });

            // 2.2 filling data
            data.setKnownSourceStr(
                    channelMasterData.stream()
                            .filter(item -> item.getId().equals(
                                    Channel.MEMBER_KNOWN_SOURCE.getId()))
                            .findAny().get().getTypeIds().stream().filter(item -> item.getCheck() != null && item.getCheck())
                            .map(ChannelTypeResponseDTO::getName).collect(Collectors.joining(", "))
            );
            data.setTroubleStr(
                    channelMasterData.stream()
                            .filter(item -> item.getId().equals(
                                    Channel.MEMBER_WORRIES.getId()))
                            .findAny().get().getTypeIds().stream().filter(item -> item.getCheck() != null && item.getCheck())
                            .map(ChannelTypeResponseDTO::getName).collect(Collectors.joining(", "))
            );
            data.setProfessionStr(
                    channelMasterData.stream()
                            .filter(item -> item.getId().equals(
                                    Channel.MEMBER_PROFESSION.getId()))
                            .findAny().get().getTypeIds().stream().filter(item -> item.getCheck() != null && item.getCheck())
                            .map(ChannelTypeResponseDTO::getName).collect(Collectors.joining(", "))
            );
            data.setFavoriteStyleStr(
                    channelMasterData.stream()
                            .filter(item -> item.getId().equals(
                                    Channel.MEMBER_FAVORITE_STYLE.getId()))
                            .findAny().get().getTypeIds().stream().filter(item -> item.getCheck() != null && item.getCheck())
                            .map(ChannelTypeResponseDTO::getName).collect(Collectors.joining(", "))
            );
            data.setOccupationStr(
                    channelMasterData.stream()
                            .filter(item -> item.getId().equals(
                                    Channel.MEMBER_OCCUPATION.getId()))
                            .findAny().get().getTypeIds().stream().filter(item -> item.getCheck() != null && item.getCheck())
                            .map(ChannelTypeResponseDTO::getName).collect(Collectors.joining(", "))
            );

            data.setFamilyStructureStr(
                    channelMasterData.stream()
                            .filter(item -> item.getId().equals(
                                    Channel.MEMBER_FAMILY_STRUCTURE.getId()))
                            .findAny().get().getTypeIds().stream().filter(item -> item.getCheck() != null && item.getCheck())
                            .map(ChannelTypeResponseDTO::getName).collect(Collectors.joining(", "))
            );

        }

        return result.withData(data);
    }

    @Override
    public MemberBookingHistoryResponseDTO getBookingHistoryByMemberId(Long memberId) {
        MemberBookingHistoryResponseDTO data = modelMapper.map(repository.getBookingHistoryByMember(memberId), MemberBookingHistoryResponseDTO.class);
        data.setCustomerIds(Arrays.asList(data.getCustomerId().split(","))
                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList()));
        return data;
    }

    @Override
    public List<ManagementUserNameResponseDTO> getManagementUserNameById(List<Long> managementIds) {
        return repository.getManagementUserNameById(managementIds).stream()
                .map(x -> modelMapper.map(x, ManagementUserNameResponseDTO.class))
                .collect(Collectors.toList());
    }

    private Response<HashMap<String, Object>> validateUpdateMemberInformation(MemberUpdatingRequest request) {
        Response<HashMap<String, Object>> result = new Response<>();
        HashMap<String, Object> data = new HashMap<>();

        Member member = repository.findByIdAndDeletedAtIsNull(request.getMemberId());
        if (member == null) {
            return result.withError(ResponseMessage.MEMBER_IS_NOT_EXISTED);
        }

        Optional<Member> memberCheckEmail = repository.getMemberByEmail(request.getEmail());
        if (memberCheckEmail.isPresent() && !memberCheckEmail.get().getId().equals(member.getId())) {
            return result.withError(ResponseMessage.ALREADY_EXISTS_EMAIL);
        }

        Optional<List<Member>> memberCheck = repository.getAllByTelAndDeletedAtIsNull(request.getTel());
        if (memberCheck.isPresent() && !memberCheck.get().get(0).getId().equals(member.getId())) {
            return result.withError(ResponseMessage.MEMBER_PHONE_NUMBER_IS_ALREADY_USED);
        }

        Prefecture prefecture = prefectureRepository.findByIdAndDeletedAtIsNull(request.getPrefectureId());
        if (prefecture == null) {
            return result.withError(ResponseMessage.PREFECTURE_NOT_EXISTED);
        }

        Salon salonCheck = salonClient.getExistedSalonBySlug(request.getSalonSlug());
        if (salonCheck == null) {
            return result.withError(ResponseMessage.SALON_DOES_NOT_EXIST);
        }

        Optional<List<Customer>> customer = customerService.getCustomerListByMemberId(request.getMemberId());
        if (!customer.isPresent()) {
            return result.withError(ResponseMessage.CUSTOMER_NOT_EXIST);
        }

        if (request.isPasswordChanged() && request.isPasswordChanged() == true) {
            if (StringUtils.isEmpty(request.getPassword())) {
                return result.withError(ResponseMessage.USER_PASSWORD_MUST_BE_NOT_NULL);
            }
            if (!ValidationUtils.checkPassword(request.getPassword())) {
                return result.withError(ResponseMessage.USER_PASSWORD_MUST_BE_GREATER_THAN_SIX_CHARACTER);
            }
        }

        data.put("member", member);
        data.put("customer", customer);
        data.put("salon", salonCheck);

        return result.withData(data);
    }

    @Override
    public Response<String> precheckUpdateMemberInformation(MemberUpdatingRequest request) {
        Response<String> result = new Response<>();

        Response<HashMap<String, Object>> checkResult = validateUpdateMemberInformation(request);

        if (checkResult.getStatusCode() != 200) {
            return result.withError(ResponseMessage.getByStatus(checkResult.getStatusCode()));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response<String> updateMemberInformation(MemberUpdatingRequest request) {
        Response<String> result = new Response<>();

        Response<HashMap<String, Object>> checkResult = validateUpdateMemberInformation(request);

        if (checkResult.getStatusCode() != 200) {
            return result.withError(ResponseMessage.getByStatus(checkResult.getStatusCode()));
        }

        Member member = (Member) checkResult.getData().get("member");
        Optional<List<Customer>> customer = (Optional<List<Customer>>) checkResult.getData().get("customer");
        Salon salonCheck = (Salon) checkResult.getData().get("salon");

        // 2. Update member info
        member.setEmail(request.getEmail());
        if (request.isPasswordChanged() != null && request.isPasswordChanged() == true) {
            member.setEncryptedPassword(passwordEncoder.encode(request.getPassword()));
        }

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
        member.setPrefectureId(request.getPrefectureId());
        member.setCityId(request.getCityId());
        member.setPlaceId(request.getPlaceId());
        member.setPhotoUrl(request.getPhotoUrl());
        // member channel
        member.setChannel(request.getChannel() == null ? null : new Gson().toJson(request.getChannel()));
        Optional<SalonMember> salonMemberCheck =
                salonMemberRepository.getByMemberIdAndSalonIdAndDeletedAtIsNull(member.getId(), salonCheck.getId());
        if (!salonMemberCheck.isPresent()) {
            SalonMember salonMember = new SalonMember();
            salonMember.setMemberId(member.getId());
            salonMember.setSalonId(salonCheck.getId());
            salonMemberRepository.save(salonMember);
        }

        // 2.5 saving member
        repository.save(member);

        // 3. Updating customer
        Customer updatingCustomerObj = customer.get().get(0);
        updatingCustomerObj.setLastName(request.getLastName());
        updatingCustomerObj.setFirstName(request.getFirstName());
        updatingCustomerObj.setBirthday(member.getBirthday().atStartOfDay());
        updatingCustomerObj.setTel(request.getTel());
        updatingCustomerObj.setKatakanaFirstName(request.getKatakanaFirstName());
        updatingCustomerObj.setKatakanaLastName(request.getKatakanaLastName());
        updatingCustomerObj.setGender(request.getGender() != null && request.getGender() == 1 ? (byte) 1 : 0);
        updatingCustomerObj.setZipcode(request.getZipcode());
        updatingCustomerObj.setAddress(request.getAddress());
        updatingCustomerObj.setPrefectureId(request.getPrefectureId());
        updatingCustomerObj.setCityId(request.getCityId());
        updatingCustomerObj.setPlaceId(request.getPlaceId());
        updatingCustomerObj.setCity(request.getCity());
        customerService.updateCustomer(updatingCustomerObj);

        return result;
    }

    @Override
    public Response<List<SalonMember>> feignGetMySalonList(Long memberId) {
        Response<List<SalonMember>> result = new Response<>();

        // 1. Check exist member id
        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId);
        if (member == null) {
            return result.withError(ResponseMessage.MEMBER_IS_NOT_EXISTED);
        }

        Optional<List<SalonMember>> salonMemberList =
                salonMemberRepository.findAllByMemberIdAndDeletedAtIsNull(memberId);
        if (salonMemberList.isPresent()) {
            return result.withData(salonMemberList.get());
        }
        return null;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Response<String> feignAddMemberSalon(SalonMember salonMember) {
        Response<String> result = new Response<>();

        // saving salon member info
        Optional<SalonMember> salonMemberCheck =
                salonMemberRepository.getByMemberIdAndSalonIdAndDeletedAtIsNull(salonMember.getMemberId(), salonMember.getSalonId());
        if (!salonMemberCheck.isPresent()) {
            salonMemberRepository.save(salonMember);
        }

        return result;
    }

    @Override
    public Response<MemberProfileInformationResponseDTO> getMemberCustomerInformation(Long customerId) {
        Response<MemberProfileInformationResponseDTO> result = new Response<>();
        Customer customer = customerService.getCustomerByCustomerId(customerId);
        if (customer == null) {
            result.withError(ResponseMessage.CUSTOMER_NOT_EXIST);
        }
        return this.getMemberInformation(customer.getMemberId());
    }

    @Override
    public Response<String> updatePointForMember(Long memberId, Double point) {
        Response<String> result = new Response<>();

        // 1.  Check existed member
        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId);
        if (member == null) {
            return result.withError(ResponseMessage.MEMBER_IS_NOT_EXISTED);
        }

        // 2. update member point
        member.setPoint(point);
        memberRepository.save(member);

        return result;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Response<List<MemberCoupon>> feignInsertOrUpdateExistedMemberCoupon(List<MemberCoupon> memberCouponList) {
        Response<List<MemberCoupon>> result = new Response<>();

        if (!CollectionUtils.isEmpty(memberCouponList)) {
            memberCouponList.forEach(item -> {
                MemberCoupon memberCoupon =
                        memberCouponRepository.findByCouponIdAndMemberIdAndDeletedAtIsNull(item.getCouponId(), item.getMemberId());
                if (memberCoupon == null) {
                    memberCoupon.setMemberId(item.getMemberId());
                    memberCoupon.setCouponId(item.getCouponId());
                }
                memberCoupon.setUsed(item.getUsed());
                memberCouponRepository.save(memberCoupon);
            });
        }

        return result;
    }

//    private Response<Page<MemberManagementListResponseDTO>> getMemberListByCondition(MemberSearchDTO memberSearchDTO) {
//        Response<Page<MemberManagementListResponseDTO>> result = new Response<>();
//
//        // 1. Check existed company
//        Company company = companyClient.getCompanyById(memberSearchDTO.getCompanyId());
//        if (company == null) {
//            return result.withError(ResponseMessage.COMPANY_DOES_NOT_EXIST);
//        }
//
//        // 2. Now get member list of company id
//        Specification<Member> memberSpecifications = MemberSpecification.hasIdLike(memberSearchDTO.getKeyword());
//        List<Member> test = memberSRepository.findAll(memberSpecifications);
//
//        return result;
//    }

    @Override
    public Response<Page<BookingHistoryResponseDTO>> getMemberAlbumListByMemberId(Long memberId, Pageable pageable) {
        Response<Page<BookingHistoryResponseDTO>> response = new Response<>();
        Member member = repository.findByIdAndDeletedAtIsNull(memberId);
        if (member == null) {
            return response.withError(ResponseMessage.MEMBER_IS_NOT_EXISTED);
        }

        MemberBookingHistoryResponseDTO result = modelMapper.map(this.getBookingHistoryByMemberId(memberId), MemberBookingHistoryResponseDTO.class);

        Map<String, Object> body = new HashMap<>();
        body.put("customerIds", result.getCustomerIds());

        List<BookingHistoryResponseDTO> data = receptionClient.feignFindBookingHairPhotoByCustomerIds(body);
        Page<BookingHistoryResponseDTO> dataResponse = new PageImpl<>(data, pageable, data.size());
        return response.withData(dataResponse);
    }

    @Override
    public Response<CustomPage<MemberManagementListResponseDTO>> getMemberListByCondition(MemberSearchDTO memberSearchDTO) {
        Response<CustomPage<MemberManagementListResponseDTO>> result = new Response<>();

        // 1. Check existed company
        Company company = companyClient.getCompanyById(memberSearchDTO.getCompanyId());
        if (company == null) {
            return result.withError(ResponseMessage.COMPANY_DOES_NOT_EXIST);
        }

        // 2. Now get member list of company id
        // 2.1 Check the search columns
        List<String> keywordSearchColumnOnly = Arrays.asList(
                MemberListColumn.ID_STR.getName(),
                MemberListColumn.SALON_NAME.getName(),
                MemberListColumn.MEMBER_TYPE_NAME.getName(),
                MemberListColumn.FULL_NAME.getName(),
                MemberListColumn.SEX.getName()
        );
        CustomPage<MemberManagementListResponseDTO> memberList = new CustomPage<>();
        if (keywordSearchColumnOnly.containsAll(memberSearchDTO.getSearch().getColumns())) {
            // All the search column is fit inside keyword column search
            // meaning we are just filtering
            memberList = getProcedurePagination(
                    repository.sqlQueryPGetMemberAndCustomer,
                    repository.sqlCountQueryPGetMemberAndCustomer,
                    MemberManagementListResponseDTO.class,
                    memberSearchDTO.getSearch().getColumns(),
                    memberSearchDTO.getSearch().getPageable(),
                    new Param("keyword", memberSearchDTO.getSearch().getKeyword())
            );
        }

        return result.withData(memberList);
    }

    @Override
    public  Response<String> preCheckAddNewMember(AddNewMemberRequest request){
        Response<String> response = new Response<>();
        // check salon
        Salon salon = salonClient.getExistedSalon(request.getSalonId());
        if (salon == null) {
            return response.withError(ResponseMessage.SALON_DOES_NOT_EXIST);
        }
        Customer alreadyCustomerWithPhone =
                customerRepository.getCustomerByTelAndDeletedAtIsNotNull(request.getTel());
        if (alreadyCustomerWithPhone != null ) {
            return response.withError(ResponseMessage.CUSTOMER_PHONE_NUMBER_IS_ALREADY_USED);
        }
        return response;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Response<Page<MemberManagementListResponseDTO>> addNewMember(AddNewMemberRequest request) {
        Response<Page<MemberManagementListResponseDTO>> response = new Response<>();
        // 1. Save to member table
        // Check existed salon by salonId from request
        Salon salonRequest = salonClient.getExistedSalon(request.getSalonId());
        if( salonRequest == null){
            return response.withError(ResponseMessage.SALON_DOES_NOT_EXIST);
        };
        // Save all the request
        Member newMember = new Member();
        newMember.setCompanyId(salonRequest.getCompanyId());
        newMember.setFirstName(request.getFirstName());
        newMember.setLastName(request.getLastName());
        newMember.setKatakanaFirstName(request.getKatakanaFirstName());
        newMember.setKatakanaLastName(request.getKatakanaLastName());
        newMember.setBirthday(LocalDate.of(Integer.valueOf(request.getBirthYear()),
                Integer.valueOf(request.getBirthMonth()),
                Integer.valueOf(request.getBirthDate())));
        newMember.setGender(request.getGender());
        newMember.setTel(request.getTel());
        newMember.setEmail("guest@email.com");
        newMember.setEncryptedPassword(passwordEncoder.encode("1"));
        newMember.setStatus(true);
        memberRepository.save(newMember);
        Long memberId = (memberRepository.getMemberByTelAndDeletedAtIsNull(request.getTel())).getId();
        // update member qrCode
        String newMemberQRCode = GenerateUtils.hashidEncode(memberId);
        Member memberUpdateQRCode = memberRepository.getOne(memberId);
        memberUpdateQRCode.setQrCode(newMemberQRCode);
        memberRepository.save(memberUpdateQRCode);

        // 2. Save to customer table
        Customer newCustomer = new Customer();
        newCustomer.setSalonId(request.getSalonId());
        newCustomer.setMemberId(memberId);
        newCustomer.setFirstName(request.getFirstName());
        newCustomer.setLastName(request.getLastName());
        newCustomer.setKatakanaFirstName(request.getKatakanaFirstName());
        newCustomer.setKatakanaLastName(request.getKatakanaLastName());
        newCustomer.setGender(request.getGender());
        newCustomer.setBirthday(LocalDateTime.of
                (Integer.valueOf(request.getBirthYear()),
                        Integer.valueOf(request.getBirthMonth()),
                        Integer.valueOf(request.getBirthDate()),00,00));
        newCustomer.setTel(request.getTel());
        newCustomer.setStatus((short)1);
        customerService.addCustomer(newCustomer);
        // update customer qrCode
        Customer customerUpdateQRCode = customerService.getCustomerByTelAndDeletedAtIsNotNull(request.getTel());
        String newCustomerQRCode = GenerateUtils.hashidEncode(customerUpdateQRCode.getId());
        customerUpdateQRCode.setQrCode(newMemberQRCode);
        customerService.updateCustomer(customerUpdateQRCode);

        // 3. Save to salon_members table
        SalonMember newSalonMember = new SalonMember();
        newSalonMember.setMemberId(memberId);
        newSalonMember.setSalonId(request.getSalonId());
        salonMemberRepository.save(newSalonMember);

        return response;
    }


}
