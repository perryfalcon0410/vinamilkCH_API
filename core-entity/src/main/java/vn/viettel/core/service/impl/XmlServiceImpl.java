package vn.viettel.core.service.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import vn.viettel.core.dto.mail.MailStructure;
import vn.viettel.core.service.XmlService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class XmlServiceImpl implements XmlService {
    private static final String varStart = "%@=";
    private static final String varEnd = "=@%";
    private static final String varBr = "%br%";
    private static final String varSpace = "%nbsp%";

    private MailStructure putVariables(MailStructure mailStructure, HashMap<String, Object> params) throws Exception {
        List<Field> fields = Arrays.asList(MailStructure.class.getDeclaredFields());
        fields.forEach(field -> {
            try {
                if (field.get(mailStructure) != null) {
                    String str = field.get(mailStructure).toString();

                    while (str.indexOf(varStart) > -1) {
                        String variableNeedToInput = str.substring(
                                str.indexOf(varStart) + 3, str.indexOf(varEnd)
                        );
                        str = str.replaceAll(varStart + variableNeedToInput + varEnd,
                                params.get(variableNeedToInput).toString());

                    }

                    // replace all <br>
                    str = str.replaceAll(varBr, "<br>");
                    // replace all space
                    str = str.replaceAll(varSpace, "&nbsp;");
                    field.set(mailStructure, str);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        return mailStructure;
    }

    @Override
    public MailStructure readXmlMail(String path, HashMap<String, Object> params) throws Exception {
        InputStream input = this.getClass().getClassLoader()
                .getResourceAsStream(path);
        XmlMapper xmlMapper = new XmlMapper();
        String xml = inputStreamToString(input);
        MailStructure mail = xmlMapper.readValue(xml, MailStructure.class);

        return putVariables(mail, params);
    }

    public String inputStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}
