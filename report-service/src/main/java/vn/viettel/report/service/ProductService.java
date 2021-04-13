package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.stock.PoConfirm;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ProductImportRequest;
import vn.viettel.report.service.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    Response<Page<ProductDTO>> findProduct(List<String> productCodes, Pageable pageable);
    Response<List<ProductImportRequest>> importExcel( String filePath);
}
