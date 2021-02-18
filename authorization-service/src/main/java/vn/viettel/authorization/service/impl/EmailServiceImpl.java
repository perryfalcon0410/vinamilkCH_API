package vn.viettel.authorization.service.impl;

import vn.viettel.authorization.messaging.member.MemberSendEmailRegistrationRequest;
import vn.viettel.authorization.service.EmailService;
import vn.viettel.core.db.entity.AdminUser;
import vn.viettel.core.db.entity.ManagementUsers;
import vn.viettel.core.db.entity.Member;

import vn.viettel.core.db.entity.User;
import vn.viettel.core.dto.mail.MailStructure;
import vn.viettel.core.service.XmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.name}")
    private String EMAIL_NAME;

    @Value("${spring.mail.username}")
    private String EMAIL_USERNAME;

    @Value("${frontend.url}")
    private String URL;

    @Value("${frontend.customer.url}")
    private String CUSTOMER_URL;

    @Value("${frontend.admin.url}")
    private String ADMIN_URL;

    @Value("${frontend.reception.url}")
    private String RECEPTION_URL;



    @Autowired
    XmlService xmlService;

    private final static String TITLE_CUSTOMER_USER_ACTIVE = "本登録手続き(まちかつ)";
    private final static String BODY_CUSTOMER_USER_ACTIVE = "%s 様"
            + "<br><br>仮登録頂きありがとうございます。"
            + "<br>以下のリンク先をクリックして本登録の手続きを進めて下さい。"
            + "<br>よろしくお願い申し上げます。"
            + "<br><br><a href=\"%s\">%s</a>"
            + "<br>(注)有効期限は24時間となります。有効時間を過ぎるともう一度仮登録が必要になります。";
    private final static String TITLE_CUSTOMER_USER_FORGOT_PASSWORD = "パスワード更新（まちかつサポート）";
    private final static String BODY_CUSTOMER_USER_FORGOT_PASSWORD = "以下のURLにアクセスしてパスワードを変更してください。: "
            + "<br><a href=\"%s\">%s</a>";

    private final static String TITLE_SHOP_OWNER_USER_FORGOT_PASSWORD = "管理者パスワード更新(まちかつ)";
    private final static String BODY_SHOP_OWNER_USER_FORGOT_PASSWORD = "以下のURLにアクセスしてパスワードを変更してください。:"
            + "<br><a href=\"%s\">%s</a>";

    private final static String TITLE_ADMIN_USER_FORGOT_PASSWORD = "管理者パスワード更新(まちかつ)";
    private final static String BODY_ADMIN_USER_FORGOT_PASSWORD = "以下のURLにアクセスしてパスワードを変更してください。:"
            + "<br><a href=\"%s\">%s</a>";

    private final static String TITLE_MANAGEMENT_USER_FORGOT_PASSWORD = "管理者パスワード更新(まちかつ)";
    private final static String BODY_MANAGEMENT_USER_FORGOT_PASSWORD = "以下のURLにアクセスしてパスワードを変更してください。:"
            + "<br><a href=\"%s\">%s</a>";

    private final static String TITLE_RECEPTION_USER_FORGOT_PASSWORD = "管理者パスワード更新(まちかつ)";
    private final static String BODY_RECEPTION_USER_FORGOT_PASSWORD = "以下のURLにアクセスしてパスワードを変更してください。:"
            + "<br><a href=\"%s\">%s</a>";

    private final static String TITLE_MEMBER_USER_VALIDATION_ACCOUNT = "本登録手続き";
    private final static String BODY_MEMBER_USER_VALIDATION_ACCOUNT = "<br>%s 様"
            + "<br>"
            + "<br>仮登録頂ぎありがとうございます。"
            + "<br>以下のリンク先をクリックして本登録の手続きを進めて下さい。"
            + "<br>よろしくお願願いい申し上げます。"
            + "<br>"
            + "<br><a href=\"%s\">%s</a>"
            + "<br>注）有効期限は24時間となります。有効時間を過ぎるともう一度仮登録が必要になります。";

    private String FORGOT_PASSWORD_MEMBER_MAIL_PATH = "mailTemplate/forgot_password_member.xml";

    public void sendEmailCustomerUserActive(User user, String objectCode) {
        String url = CUSTOMER_URL + "/" + objectCode + "/user/register?token=" + user.getActivationCode();
        String content = String.format(BODY_CUSTOMER_USER_ACTIVE, user.getEmail(), url, CUSTOMER_URL);
        String body = getBody(content);
        sendEmail(user.getEmail(), TITLE_CUSTOMER_USER_ACTIVE, body);
    }

    public void sendEmailCustomerUserForgotPassword(User user, String token, String objectCode) {
        String url = CUSTOMER_URL + "/" + objectCode + "/user/reset-password?token=" + token;
        String content = String.format(BODY_CUSTOMER_USER_FORGOT_PASSWORD, url, CUSTOMER_URL);
        String body = getBody(content);
        sendEmail(user.getEmail(), TITLE_CUSTOMER_USER_FORGOT_PASSWORD, body);
    }

    public void sendEmailShopOwnerUserForgotPassword(User user, String token) {
        String url = URL + "/reset?token=" + token;
        String content = String.format(BODY_SHOP_OWNER_USER_FORGOT_PASSWORD, url, URL);
        String body = getBody(content);
        sendEmail(user.getEmail(), TITLE_SHOP_OWNER_USER_FORGOT_PASSWORD, body);
    }

    public void sendEmailAdministratorForgotPassword(AdminUser user, String token) {
        String url = ADMIN_URL + "/reset?token=" + token;
        String content = String.format(BODY_ADMIN_USER_FORGOT_PASSWORD, url, ADMIN_URL);
        String body = getBody(content);
        sendEmail(user.getEmail(), TITLE_ADMIN_USER_FORGOT_PASSWORD, body);
    }

    public void sendEmailMemberUserActive(Member member, String token, MemberSendEmailRegistrationRequest request) {
        String url = CUSTOMER_URL + "/" + request.getCompanySlug() + "/" + request.getSalonSlug() + "/user/register"
                + "?token=" + token
                + "&email=" + request.getEmail()
                + "&companySlug=" + request.getCompanySlug()
                + "&salonSlug=" + request.getSalonSlug();

        String content = String.format(BODY_MEMBER_USER_VALIDATION_ACCOUNT, request.getEmail(), url, CUSTOMER_URL);
        String body = getBody(content);
        sendEmail(member.getEmail(), TITLE_MEMBER_USER_VALIDATION_ACCOUNT, body);
    }

    public void sendEmailManagementUserForgotPassword(ManagementUsers user, String token) {
        String url = URL + "/reset?token=" + token;
        String content = String.format(BODY_MANAGEMENT_USER_FORGOT_PASSWORD, url, URL);
        String body = getBody(content);
        sendEmail(user.getEmail(), TITLE_MANAGEMENT_USER_FORGOT_PASSWORD, body);
    }

    public void sendEmailManagementUserForgotReception(ManagementUsers user, String token) {
        String url = RECEPTION_URL + "/reset?token=" + token;
        String content = String.format(BODY_RECEPTION_USER_FORGOT_PASSWORD, url, URL);
        String body = getBody(content);
        sendEmail(user.getEmail(), TITLE_RECEPTION_USER_FORGOT_PASSWORD, body);
    }

    @Override
    public void sendEmailMemberForgotPassword(Member user, String token, String companySlug, String salonSlug) throws Exception {
        String url = CUSTOMER_URL + "/" + companySlug + "/" + salonSlug + "/user/login-forgot-password-reset?token=" + token;
        HashMap<String, Object> params = new HashMap<>();
        params.put("urlForgot",
                String.format("<a href=\"%s\">%s</a>", url, CUSTOMER_URL)
        );
        params.put("lastName", user.getLastName());
        MailStructure mailStructure = xmlService.readXmlMail(FORGOT_PASSWORD_MEMBER_MAIL_PATH, params);
        InternetAddress internetAddress = new InternetAddress(EMAIL_USERNAME, mailStructure.fromName);

        String body = getBodyWithFooter(mailStructure.body, mailStructure.footer);
        sendEmail(internetAddress, user.getEmail(), mailStructure.title, body);
    }

    @Async
    public void sendEmail(String toEmail, String title, String body) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            helper.setFrom(getFrom());
            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setText(body, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendEmail(InternetAddress from, String to, String title, String body) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(body, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /* GET FROM EMAIL */
    /* pattern: NAME<EMAIL@DOMAIN.COM> */
    private String getFrom() {
        return EMAIL_NAME + "<" + EMAIL_USERNAME + ">";
    }

    /* GET SIGNATURE */
    private String getSignature() {
        StringBuilder sb = new StringBuilder();
        sb.append("<br><br>---------------------------------");
        sb.append("<br>株式会社L.I.T");
        sb.append("<br>まちかつサポート");
        return sb.toString();
    }

    /* GET BODY EMAIL */
    private String getBody(String content) {
        StringBuilder sb = new StringBuilder();
        sb.append(content);
        sb.append(getSignature());
        return sb.toString();
    }

    private String getBodyWithFooter(String content, String footer) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><div>" + content + footer + "</div></body></html>");
        return sb.toString();
    }

}
