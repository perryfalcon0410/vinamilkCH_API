package vn.viettel.saleservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.Product;
import vn.viettel.core.db.entity.ProductType;
import vn.viettel.core.db.entity.ReceiptOnline;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.repository.ProductRepository;
import vn.viettel.saleservice.repository.ProductTypeRepository;
import vn.viettel.saleservice.repository.ReceiptOnlineRepository;
import vn.viettel.saleservice.service.SearchService;
import vn.viettel.saleservice.service.dto.ReceiptSearch;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    ProductTypeRepository proTypeRepo;

    @Autowired
    ProductRepository proRepo;

    @Autowired
    ReceiptOnlineRepository receiptRepo;

    @Override
    public Response<List<ProductType>> getAllProductType() {
        List<ProductType> listType = proTypeRepo.findAll();
        Response<List<ProductType>> response = new Response<>();
        response.setData(listType);
        return response;
    }

    @Override
    public Response<List<Product>> getProductByProductTypeId(long proTypeId) {
        List<Product> productList = proRepo.findByProductTypeId(proTypeId);
        Response<List<Product>> response = new Response<>();

        response.setData(productList);
        return response;
    }

    @Override
    public Response<List<Product>> getProductByNameOrCode(String input) {
        List<Product> productList = new ArrayList<>();
        if(input.startsWith("PO")) {
            Product product = proRepo.findByProductCode(input);
            productList.add(product);
        }
        else
            productList = proRepo.findByProductName(input);

        Response<List<Product>> response = new Response<>();
        response.setData(productList);
        return response;
    }

    @Override
    public Response<List<Product>> getTopProduct() {
        List<Product> productList = proRepo.findTopProduct();
        Response<List<Product>> response = new Response<>();
        response.setData(productList);
        return response;
    }

    @Override
    public Response<List<ReceiptOnline>> searchReceiptOnline(ReceiptSearch searchInfo) {
        Response<List<ReceiptOnline>> response = new Response<>();

        String receiptCode = searchInfo.getInvoiceNumber();
        Integer receiptStatus = searchInfo.getReceiptType();
        String fromDate = searchInfo.getFromDate();
        String toDate = searchInfo.getToDate();
        if (receiptCode != null && receiptStatus == null && (fromDate == null && toDate == null)) {
            ReceiptOnline result = getReceiptOnlineByCode(receiptCode).getData();
            List<ReceiptOnline> listResult = new ArrayList<>();
            listResult.add(result);
            response.setData(listResult);
        }
        else if (receiptCode == null && receiptStatus != null && (fromDate == null && toDate == null))
            response = getReceiptOnlineByStatus(receiptStatus);
        else if (receiptCode == null && receiptStatus == null && (fromDate != null && toDate != null))
            response = getReceiptOnlineByStatusAndReceiptDate(0, fromDate, toDate);
        else
            response = getReceiptOnlineByStatusAndReceiptDate(receiptStatus, fromDate, toDate);

        return response;
    }

    public Response<ReceiptOnline> getReceiptOnlineByCode(String code) {
        Response<ReceiptOnline> response = new Response<>();
        try {
            ReceiptOnline receiptOnline = receiptRepo.findByReceiptCode(code);
            response.setData(receiptOnline);
        } catch (Exception e) {
            response.setFailure(ResponseMessage.DATA_NOT_FOUND);
        }
        return response;
    }

    public Response<List<ReceiptOnline>> getReceiptOnlineByStatus(int status) {
        Response<List<ReceiptOnline>> response = new Response<>();
        try {
            List<ReceiptOnline> receiptOnline = receiptRepo.findByStatus(status);
            response.setData(receiptOnline);
        } catch (Exception e) {
            response.setFailure(ResponseMessage.DATA_NOT_FOUND);
        }
        return response;
    }

    public Response<List<ReceiptOnline>> getReceiptOnlineByStatusAndReceiptDate(int status, String fromDate, String toDate) {
        Response<List<ReceiptOnline>> response = new Response<>();
        try {
            List<ReceiptOnline> receiptOnline = receiptRepo.findByStatusAndCreatedAtBetween(status, fromDate, toDate);
            response.setData(receiptOnline);
        } catch (Exception e) {
            response.setFailure(ResponseMessage.DATA_NOT_FOUND);
        }
        return response;
    }
}
