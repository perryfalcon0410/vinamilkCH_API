package vn.viettel.gateway.service;

public interface UrlService {

    String getAuthorizationUrl();

    String getCustomerServiceUrl();

    String getSaleServiceUrl();

    String getCommonServiceUrl();

    String getPromotionServiceUrl();

    String getReportServiceUrl();
}
