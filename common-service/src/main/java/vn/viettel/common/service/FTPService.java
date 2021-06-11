package vn.viettel.common.service;


import vn.viettel.core.messaging.Response;

import java.io.IOException;

public interface FTPService {
    Response<String> downloadFtp() throws IOException;
    Response<String> uploadFtp() throws IOException;
}
