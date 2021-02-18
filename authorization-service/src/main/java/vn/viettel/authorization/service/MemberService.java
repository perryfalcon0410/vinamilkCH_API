package vn.viettel.authorization.service;

import vn.viettel.authorization.messaging.member.AddNewMemberRequest;
import vn.viettel.authorization.messaging.member.MemberUpdatingRequest;
import vn.viettel.core.db.entity.Member;
import vn.viettel.core.db.entity.MemberCoupon;
import vn.viettel.core.db.entity.SalonMember;
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
import vn.viettel.core.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberService extends BaseService {
    /**
     * get member which is registered by qrCode
     *
     * @param qrCode qr code
     * @return MemberQRDetailResponseDTO
     */
    MemberQRDetailResponseDTO getMemberCustomerRegistrationByQrCode(String qrCode);

    /**
     * get member which is registered by Id
     *
     * @param id id of member
     * @return MemberQRDetailResponseDTO
     */
    MemberQRDetailResponseDTO getMemberCustomerRegistrationById(Long id);

    /**
     * Add new member
     *
     * @param member member
     * @return member after regist
     */
    Member addNewMember(Member member);

    /**
     * get member by id
     *
     * @param id id of member
     * @return Member
     */
    Member getMemberById(Long id);

    /**
     * Update member
     *
     * @param member member entity
     * @return Member after update
     */
    Member updateMember(Member member);

    /**
     * get member by qr code
     *
     * @param qrCode qr code
     * @return Member entity
     */
    Member getMemberByQr(String qrCode);

    /**
     * get salon of member
     *
     * @param memberId member id
     * @return salon entity of member
     */
    SalonMemberResponseDTO getSalonMember(Long memberId);

    /**
     * Get from member and to member
     *
     * @param fromMemberId member id source
     * @param toMemberId   member id destination
     * @return List<Member> 0: source, 1: destination
     */
    List<Member> getFromAndToMember(Long fromMemberId, Long toMemberId);

    /**
     * Get coupon ids from member id
     *
     * @param memberId member id
     * @return List<Long> of coupon id
     */
    List<Long> getCouponIdsFromMemberId(Long memberId);

    /**
     * Get coupon ids from member id
     *
     * @param memberId member id
     * @return List<MemberCoupon> of coupon id
     */
    List<MemberCoupon> getCouponMemberFromMemberId(Long memberId);

    /**
     * Import coupon id for member id (without checking coupon code)
     *
     * @param couponId id of coupon
     * @param memberId id of member
     * @return Response<String>
     */
    Response<String> feignImportCouponCodeForMemberIdWithoutCheckingCouponCode(Long couponId, Long memberId);

    /**
     * Get member profile information in the profile screen
     *
     * @param memberId id of member
     * @return Response<MemberProfileInformationResponseDTO>
     */
    Response<MemberProfileInformationResponseDTO> getMemberInformation(Long memberId);

    /**
     * get booking history by member
     *
     * @param memberId id of member
     * @return MemberBookingHistoryResponseDTO
     */
    MemberBookingHistoryResponseDTO getBookingHistoryByMemberId(Long memberId);

    /**
     * get list management user name by salon id
     *
     * @param managementIds
     * @return ManagementUserNameResponseDTO
     */
    List<ManagementUserNameResponseDTO> getManagementUserNameById(List<Long> managementIds);

    /**
     * Precheck Update member information at member profile page
     *
     * @param request MemberUpdatingRequest
     * @return Response<String>
     */
    Response<String> precheckUpdateMemberInformation(MemberUpdatingRequest request);

    /**
     * Update member information at member profile page
     *
     * @param request MemberUpdatingRequest
     * @return Response<String>
     */
    Response<String> updateMemberInformation(MemberUpdatingRequest request);

    /**
     * Get member List salon from memberId
     *
     * @param memberId id member
     * @return Response<List < SalonMember>>
     */
    Response<List<SalonMember>> feignGetMySalonList(Long memberId);

    /**
     * Add new salon member
     *
     * @param salonMember salon member information
     * @return Response<String>
     */
    Response<String> feignAddMemberSalon(SalonMember salonMember);

    /**
     * Get member information from customer id
     *
     * @param customerId id of customer
     * @return Response<MemberProfileInformationResponseDTO>
     */
    Response<MemberProfileInformationResponseDTO> getMemberCustomerInformation(Long customerId);

    /**
     * Update point for member
     *
     * @param memberId id of member
     * @param point    point
     * @return here
     */
    Response<String> updatePointForMember(Long memberId, Double point);

    /**
     * Member coupon update insert
     *
     * @param memberCouponList list member coupon
     * @return Response<List < MemberCoupon>>
     */
    Response<List<MemberCoupon>> feignInsertOrUpdateExistedMemberCoupon(List<MemberCoupon> memberCouponList);

    /**
     * Get member album list by member id
     *
     * @param memberId
     * @return Response<Page < BookingHistoryResponseDTO>>
     */
    Response<Page<BookingHistoryResponseDTO>> getMemberAlbumListByMemberId(Long memberId, Pageable pageable);

    /**
     * Get member list by condition list
     *
     * @param memberSearchDTO condition list
     * @return data page by page
     */
    Response<CustomPage<MemberManagementListResponseDTO>> getMemberListByCondition(MemberSearchDTO memberSearchDTO);

    /**
     * PreCheck before add new member to member-list
     *
     * @param request
     * @return data page by page
     */
    Response<String> preCheckAddNewMember(AddNewMemberRequest request);

    /**
     * Add new member to member-list
     *
     * @param request
     * @return data page by page
     */
    Response<Page<MemberManagementListResponseDTO>> addNewMember(AddNewMemberRequest request);



}
