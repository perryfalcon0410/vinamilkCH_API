package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.common.ProductInfo;
import vn.viettel.core.db.entity.stock.PoConfirm;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ProductImportRequest;
import vn.viettel.report.service.dto.ProductDTO;
import vn.viettel.report.service.dto.ProductInfoDTO;

import java.io.FileNotFoundException;
import java.util.List;

public interface ProductService {
    Response<Page<ProductDTO>> findProduct(List<String> productCodes,String productName,Long catId, Pageable pageable);
    Response<List<ProductInfoDTO>> getAllProductCat();
}
