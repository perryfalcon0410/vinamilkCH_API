package vn.viettel.core.service;

import vn.viettel.core.dto.mail.MailStructure;

import java.util.HashMap;

public interface XmlService {
    MailStructure readXmlMail(String path, HashMap<String, Object> params) throws Exception;
}
