package vn.viettel.saleservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.Product;
import vn.viettel.core.db.entity.ProductType;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.repository.ProductRepository;
import vn.viettel.saleservice.repository.ProductTypeRepository;
import vn.viettel.saleservice.service.SearchProductService;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchProductServiceImpl implements SearchProductService {
    @Autowired
    ProductTypeRepository proTypeRepo;

    @Autowired
    ProductRepository proRepo;

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
}
