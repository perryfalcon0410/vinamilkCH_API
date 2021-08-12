package vn.viettel.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.gateway.service.UrlService;
import vn.viettel.gateway.service.feign.*;

@Service
public class UrlServiceImpl implements UrlService {

    @Autowired
    private CustomerClient customerClient;
    @Autowired
    private AuthClient authClient;
    @Autowired
    private CommonClient commonClient;
    @Autowired
    private PromotionClient promotionClient;
    @Autowired
    private ReportClient reportClient;
    @Autowired
    private SaleClient saleClient;

    @Override
    public String getAuthorizationUrl() {
        return authClient.getURLValue();
    }

    @Override
    public String getCustomerServiceUrl() {
        return customerClient.getURLValue();
    }

    @Override
    public String getSaleServiceUrl() {
        return saleClient.getURLValue();
    }

    @Override
    public String getCommonServiceUrl() {
        return commonClient.getURLValue();
    }

    @Override
    public String getPromotionServiceUrl() {
        return promotionClient.getURLValue();
    }

    @Override
    public String getReportServiceUrl() {
        return reportClient.getURLValue();
    }
}

