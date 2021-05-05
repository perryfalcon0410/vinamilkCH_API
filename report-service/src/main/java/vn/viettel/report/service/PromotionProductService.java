package vn.viettel.report.service;

import vn.viettel.core.messaging.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface PromotionProductService {
    ByteArrayInputStream exportExcel(Long shopId) throws IOException;

    Response<List<Object>> callStoreProcedure(Long shopId);
}