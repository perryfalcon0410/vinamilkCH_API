package vn.viettel.authorization.controller;

import vn.viettel.authorization.messaging.member.AddNewMemberRequest;
import vn.viettel.authorization.messaging.member.MemberUpdatingRequest;
import vn.viettel.authorization.service.MemberService;
import vn.viettel.core.db.entity.Member;
import vn.viettel.core.db.entity.MemberCoupon;
import vn.viettel.core.db.entity.SalonMember;
import vn.viettel.core.db.entity.commonEnum.Feature;
import vn.viettel.core.dto.ManagementUserNameResponseDTO;
import vn.viettel.core.dto.MemberBookingHistoryResponseDTO;
import vn.viettel.core.dto.SalonMemberResponseDTO;
import vn.viettel.core.dto.booking.BookingHistoryResponseDTO;
import vn.viettel.core.dto.salon.MemberQRDetailResponseDTO;
import vn.viettel.core.dto.search.CustomPage;
import vn.viettel.core.dto.search.MemberSearchDTO;
import vn.viettel.core.dto.user.MemberManagementListResponseDTO;
import vn.viettel.core.dto.user.MemberProfileInformationResponseDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.Feature.OnFeature;
import vn.viettel.core.security.anotation.RoleFeign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/member")
@OnFeature(feature = Feature.RESERVATION)
public class MemberController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MemberService memberService;

    @RoleFeign
    @GetMapping("/getCustomerMemberRegistrationByQrCode")
    public MemberQRDetailResponseDTO getCustomerMemberRegistrationByQrCode(@RequestParam("qrCode") String qrCode) {
        logger.info("[getExistedMemberById()] - get member by qrCode #qrCode: {}", qrCode);
        return memberService.getMemberCustomerRegistrationByQrCode(qrCode);
    }

    @RoleFeign
    @GetMapping("/getCustomerMemberRegistrationById")
    public MemberQRDetailResponseDTO getCustomerMemberRegistrationById(@RequestParam("id") Long id) {
        logger.info("[getExistedMemberById()] - get member by id #id: {}", id);
        return memberService.getMemberCustomerRegistrationById(id);
    }

    @RoleFeign
    @PostMapping("/addNew")
    public Member addNewMember(@RequestBody Member member) {
        logger.info("[addNewMember()]");
        return memberService.addNewMember(member);
    }

    @RoleFeign
    @GetMapping("/getById")
    public Member getMemberById(@RequestParam("id") Long id) {
        logger.info("[getMemberById() - get member by id #id: {}]", id);
        return memberService.getMemberById(id);
    }

    @RoleFeign
    @PostMapping("/updateMember")
    public Member updateMember(@RequestBody Member member) {
        logger.info("[updateMember()");
        return memberService.updateMember(member);
    }

    @RoleFeign
    @GetMapping("/getByQr")
    public Member getMemberByQr(@RequestParam("qrCode") String qrCode) {
        logger.info("[getMemberByQr() - get member by qr #qr: {}]", qrCode);
        return memberService.getMemberByQr(qrCode);
    }

    @RoleFeign
    @GetMapping("/getSalonMember")
    public SalonMemberResponseDTO getSalonMember(@RequestParam("memberId") Long memberId) {
        logger.info("[getSalonMember()] - memberId #memberId: {}", memberId);
        return memberService.getSalonMember(memberId);
    }

    @RoleFeign
    @GetMapping("/getFromAndToMember")
    public List<Member> getFromAndToMember(@RequestParam("fromMemberId") Long fromMemberId,
                                           @RequestParam("toMemberId") Long toMemberId) {
        logger.info("[getFromAndToMember()] - #fromMemberId: {}", fromMemberId);
        logger.info("[getFromAndToMember()] - #toMemberId: {}", toMemberId);
        return memberService.getFromAndToMember(fromMemberId, toMemberId);
    }

    @RoleFeign
    @GetMapping("/getCouponIdsFromMemberId")
    public List<Long> getCouponIdsFromMemberId(@RequestParam("memberId") Long memberId) {
        logger.info("[getCouponIdsFromMemberId()] - #fromMemberId: {}", memberId);
        return memberService.getCouponIdsFromMemberId(memberId);
    }

    @RoleFeign
    @PostMapping("/feignImportCouponCodeForMemberIdWithoutCheckingCouponCode")
    public Response<String> feignImportCouponCodeForMemberIdWithoutCheckingCouponCode(@RequestBody HashMap<String, Object> body) {
        logger.info("[feignImportCouponCodeForMemberIdWithoutCheckingCouponCode()] - #body: {}", body);
        return memberService.feignImportCouponCodeForMemberIdWithoutCheckingCouponCode(
                Long.parseLong(body.get("id").toString()),
                Long.parseLong(body.get("memberId").toString())
        );
    }

    @OnFeature(feature = Feature.RESERVATION)
    @GetMapping("/getMemberInformation")
    public Response<MemberProfileInformationResponseDTO> getMemberInformation(@RequestParam("memberId") Long memberId) {
        logger.info("[getMemberInformation()] - #memberId: {}", memberId);
        return memberService.getMemberInformation(memberId);
    }

    @RoleFeign
    @GetMapping("/feignGetBookingHistory")
    public MemberBookingHistoryResponseDTO getBookingHistoryByMemberId(@RequestParam("memberId") Long memberId) {
        logger.info("[getBookingHistoryByMemberId()] - memberId #memberId: {}", memberId);
        return memberService.getBookingHistoryByMemberId(memberId);
    }

    @RoleFeign
    @GetMapping("/feignGetManagementUserNameById")
    public List<ManagementUserNameResponseDTO> getManagementUserNameById(@RequestParam("managementIds") List<Long> managementIds) {
        logger.info("[getManagementUserNameById()] - #managementIds: {}", managementIds);
        return memberService.getManagementUserNameById(managementIds);
    }

    @RoleFeign
    @GetMapping("/feignGetBookingHistoryDetail")
    public MemberBookingHistoryResponseDTO getBookingHistoryDetailByMemberId(@RequestParam("memberId") Long memberId) {
        logger.info("[getBookingHistoryDetailByMemberId()] - memberId #memberId: {}", memberId);
        return memberService.getBookingHistoryByMemberId(memberId);
    }

    @OnFeature(feature = Feature.RESERVATION)
    @PostMapping("/precheckUpdateMemberInformation")
    public Response<String> precheckUpdateMemberInformation(@Valid @RequestBody MemberUpdatingRequest request) {
        logger.info("[precheckUpdateMemberInformation()] - memberId #memberId: {}", request);
        return memberService.precheckUpdateMemberInformation(request);
    }

    @OnFeature(feature = Feature.RESERVATION)
    @PostMapping("/updateMemberInformation")
    public Response<String> updateMemberInformation(@Valid @RequestBody MemberUpdatingRequest request) {
        logger.info("[updateMemberInformation()] - memberId #memberId: {}", request);
        return memberService.updateMemberInformation(request);
    }

    @RoleFeign
    @GetMapping("/feignGetBookingHistoryList")
    public MemberBookingHistoryResponseDTO getBookingHistoryDetailListByMemberId(@RequestParam("memberId") Long memberId) {
        logger.info("[getBookingHistoryDetailListByMemberId()] - memberId #memberId: {}", memberId);
        return memberService.getBookingHistoryByMemberId(memberId);
    }

    @RoleFeign
    @GetMapping("/feignGetMySalonList")
    Response<List<SalonMember>> feignGetMySalonList(@RequestParam("memberId") Long memberId) {
        logger.info("[feignGetMySalonList()] - memberId #memberId: {}", memberId);
        return memberService.feignGetMySalonList(memberId);
    }

    @RoleFeign
    @PostMapping("/feignAddMemberSalon")
    Response<String> feignAddMemberSalon(@RequestBody SalonMember salonMember) {
        logger.info("[feignAddMemberSalon()] - salon member #salonMember: {}", salonMember);
        return memberService.feignAddMemberSalon(salonMember);
    }

    @RoleFeign
    @GetMapping("/getMemberCustomerInformation")
    public Response<MemberProfileInformationResponseDTO> getMemberCustomerInformation(@RequestParam("customerId") Long customerId) {
        logger.info("[getMemberCustomerInformation()] - #customerId: {}", customerId);
        return memberService.getMemberCustomerInformation(customerId);
    }

    @RoleFeign
    @GetMapping("/getCouponMemberFromMemberId")
    public List<MemberCoupon> getCouponMemberFromMemberId(@RequestParam("memberId") Long memberId) {
        logger.info("[getCouponMemberFromMemberId()] - #fromMemberId: {}", memberId);
        return memberService.getCouponMemberFromMemberId(memberId);
    }

    @RoleFeign
    @GetMapping("/feignUpdatePointForMember")
    public Response<String> feignUpdatePointForMember(@RequestParam("memberId") Long memberId,
                                                      @RequestParam("point") Double point) {
        logger.info("[feignUpdatePointForMember()] - #fromMemberId: {}", memberId);
        logger.info("[feignUpdatePointForMember()] - #frompoint: {}", point);
        return memberService.updatePointForMember(memberId, point);
    }

    @RoleFeign
    @PostMapping("/feignInsertOrUpdateExistedMemberCoupon")
    public Response<List<MemberCoupon>> feignInsertOrUpdateExistedMemberCoupon(@RequestBody List<MemberCoupon> memberCouponList) {
        logger.info("[feignInsertOrUpdateMemberCoupon()] - #memberCouponList: {}", memberCouponList);
        return memberService.feignInsertOrUpdateExistedMemberCoupon(memberCouponList);
    }

    @GetMapping("/memberAlbumListByMemberId")
    public Response<Page<BookingHistoryResponseDTO>> getMemberAlbumListByMemberId(
            @Valid @RequestParam("memberId") Long memberId, Pageable pageable) {
        logger.info("[getMemberAlbumListByMemberId()] - memberId #memberId: {}", memberId);
        return memberService.getMemberAlbumListByMemberId(memberId, pageable);
    }

    @PostMapping("/list")
    public Response<CustomPage<MemberManagementListResponseDTO>> getMemberList(@RequestBody MemberSearchDTO memberSearchDTO) {
        logger.info("[getMemberList()] - #memberSearchDTO: {}", memberSearchDTO);
        return memberService.getMemberListByCondition(memberSearchDTO);
    }

    @PostMapping("/preCheckAddNewMember")
    public Response<String> preCheckAddNewMember(@RequestBody AddNewMemberRequest request) {
        logger.info("[preCheckAddNewMember()] - ", request);
        return memberService.preCheckAddNewMember(request);
    }

    @PostMapping("/addNewMember")
    public Response<Page<MemberManagementListResponseDTO>> addNewMember(@RequestBody AddNewMemberRequest request) {
        logger.info("[AddNewMember()] - ", request);
        return memberService.addNewMember(request);
    }
}
