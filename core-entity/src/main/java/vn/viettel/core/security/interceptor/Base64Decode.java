package vn.viettel.core.security.interceptor;

import vn.viettel.core.util.StreamUtils;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Base64;

public final class Base64Decode extends ResponseEntityDecoder {

    public Base64Decode(Decoder decoder) {
        super(decoder);
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        InputStream is = response.body().asInputStream();

        byte[] bytes = StreamUtils.readStream(is);
        String body = this.removeDoubleQuoteString(new String(bytes));

        byte[] bytesDecode = Base64.getDecoder().decode(body);

        Response modifyResponse = response.toBuilder().body(bytesDecode).build();
        return super.decode(modifyResponse, type);
    }

    private String removeDoubleQuoteString(String s) {
        s = StringUtils.removeEnd(s, "\"");
        s = StringUtils.removeStart(s, "\"");
        return s;
    }
}
