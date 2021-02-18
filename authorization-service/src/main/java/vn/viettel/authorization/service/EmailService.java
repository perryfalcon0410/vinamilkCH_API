package vn.viettel.authorization.service;

import vn.viettel.authorization.messaging.member.MemberSendEmailRegistrationRequest;
import vn.viettel.core.db.entity.AdminUser;

import vn.viettel.core.db.entity.ManagementUsers;

import vn.viettel.core.db.entity.Member;

import vn.viettel.core.db.entity.User;

public interface EmailService {

    void sendEmailCustomerUserActive(User user, String objectCode);

    void sendEmailCustomerUserForgotPassword(User user, String token, String objectCode);

    void sendEmailShopOwnerUserForgotPassword(User user, String token);
    
    void sendEmailAdministratorForgotPassword(AdminUser user, String token);

    void sendEmailManagementUserForgotPassword(ManagementUsers user, String token);

    void sendEmailMemberForgotPassword(Member user, String token, String companySlug, String salonSlug) throws Exception;

    void sendEmailManagementUserForgotReception(ManagementUsers user, String token);

    void sendEmailMemberUserActive(Member member, String token, MemberSendEmailRegistrationRequest request);
}
