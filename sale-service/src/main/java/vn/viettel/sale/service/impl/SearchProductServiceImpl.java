package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.ProductTypeRepository;
import vn.viettel.sale.service.SearchProductService;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchProductServiceImpl extends BaseServiceImpl<Product, ProductRepository> implements SearchProductService {
    @Autowired
    ProductTypeRepository productTypeRepository;

    @Override
    public Response<Page<ProductType>> getAllProductType(Pageable pageable) {
        Page<ProductType> listType = productTypeRepository.findAll(pageable);
        Response<Page<ProductType>> response = new Response<>();

        return response.withData(listType);
    }

    @Override
    public Response<Page<Product>> getProductByProductTypeId(long proTypeId, Pageable pageable) {
        Page<Product> productList = repository.findByProductTypeId(proTypeId, pageable);
        Response<Page<Product>> response = new Response<>();

        return response.withData(productList);
    }

    @Override
    public Response<Page<Product>> getProductByNameOrCode(String input, Pageable pageable) {
        Page<Product> productList;
        List<Product> products = new ArrayList<>();
        if(input.startsWith("PO")) {
            Product product = repository.findByProductCode(input);
            products.add(product);
            productList = new PageImpl<>(products);
        }
        else
            productList = repository.findByProductName(input, pageable);

        Response<Page<Product>> response = new Response<>();
        return response.withData(productList);
    }

    @Override
    public Response<Page<Product>> getTopProduct(Pageable pageable) {
        Page<Product> productList = repository.findTopProduct(pageable);
        Response<Page<Product>> response = new Response<>();

        return response.withData(productList);
    }
}
