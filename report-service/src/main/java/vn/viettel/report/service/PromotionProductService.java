package vn.viettel.report.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface PromotionProductService {
    ByteArrayInputStream exportExcel(Long shopId) throws IOException;
}