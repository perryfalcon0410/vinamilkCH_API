package vn.viettel.report.service.impl;

import com.poiji.bind.Poiji;
import com.poiji.option.PoijiOptions;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.report.messaging.ProductImportRequest;
import vn.viettel.report.repository.ProductRepository;
import vn.viettel.report.service.ProductService;
import vn.viettel.report.service.dto.ProductDTO;
import vn.viettel.report.specification.ProductsSpecification;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl extends BaseServiceImpl<Product, ProductRepository> implements ProductService {

    private ProductDTO mapProductToProductDTO(Product product) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ProductDTO dto = modelMapper.map(product, ProductDTO.class);
        return dto;
    }
    @Override
    public Response<Page<ProductDTO>> findProduct(List<String> productCodes, Pageable pageable) {
        Page<Product> products = repository.findAll(Specification.where(
                ProductsSpecification.hasProductCode(productCodes)), pageable);
        Page<ProductDTO> productDTOS = products.map(this::mapProductToProductDTO);
        return new Response< Page<ProductDTO>>().withData(productDTOS);
    }

    @Override
    public Response<List<ProductImportRequest>> importExcel(String filePath) {
        List<ProductImportRequest> productImportRequests = readDataExcel(filePath);
        if (productImportRequests.isEmpty())
            throw new ValidateException(ResponseMessage.EMPTY_LIST);

        List<String> products = repository.getProductCode();
        for (ProductImportRequest e : productImportRequests) {
            if(e.getProductCode().equals("")){
                throw new ValidateException(ResponseMessage.EMPTY_LIST);
            }
            if(!products.stream().anyMatch(pro -> pro.equals(e.getProductCode()))){
                productImportRequests.remove(e);
            }
        }
        return new Response<List<ProductImportRequest>>().withData(productImportRequests);
    }
    public List<ProductImportRequest> readDataExcel(String path) {
        if (!path.split("\\.")[1].equals("xlsx") && !path.split("\\.")[1].equals("xls"))
            throw new ValidateException(ResponseMessage.NOT_AN_EXCEL_FILE);

        File file = new File(path);
            return Poiji.fromExcel(file, ProductImportRequest.class);
    }
}
