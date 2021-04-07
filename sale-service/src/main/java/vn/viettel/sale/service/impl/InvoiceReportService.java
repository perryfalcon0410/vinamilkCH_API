package vn.viettel.sale.service.impl;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.common.ProductInfo;
import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.db.entity.stock.*;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.dto.PoProductReportDTO;
import vn.viettel.sale.service.dto.PoReportDTO;
import vn.viettel.sale.service.dto.PoReportProductDetailDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InvoiceReportService extends BaseServiceImpl<PoTrans, PoTransRepository> {

    @Autowired
    ShopRepository shopRepo;

    @Autowired
    PoTransRepository poTransRepo;

    @Autowired
    PoTransDetailRepository poTransDetailRepo;

    @Autowired
    StockAdjustmentTransRepository stockAdjustmentTransRepo;

    @Autowired
    StockAdjustmentTransDetailRepository stockAdjustmentTransDetailRepo;

    @Autowired
    StockBorrowingTransRepository stockBorrowingTransRepo;

    @Autowired
    StockBorrowingTransDetailRepository stockBorrowingTransDetailRepo;

    @Autowired
    ProductInfoRepository productInfoRepo;

    @Autowired
    ProductRepository productRepo;

    public ByteArrayInputStream invoiceReport(Long shopId, String transCode) throws FileNotFoundException, JRException {
        File file;
        PoReportDTO poReportDTO = new PoReportDTO();
        final String invoiceExportPath = "classpath:invoice-export.jrxml";
        Shop shop = shopRepo.findByIdAndDeletedAtIsNull(shopId);
        if(shop ==  null)
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        poReportDTO.setShopName(shop.getShopName());
        poReportDTO.setShopAddress(shop.getAddress());
        poReportDTO.setPhoneNumber(shop.getMobiPhone());
        poReportDTO.setFaxNumber(shop.getFax());

        if(transCode.startsWith("EXSP")) {
            PoTrans poTrans = poTransRepo.getPoTransByTransCodeAndDeletedAtIsNull(transCode);
            if(poTrans == null)
                throw new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED);
            this.reportPoTransExport(poReportDTO, poTrans);
            file = ResourceUtils.getFile(invoiceExportPath);
        }
        else if(transCode.startsWith("EXST")) {
            StockAdjustmentTrans stockTrans = stockAdjustmentTransRepo.getStockAdjustmentTransByTransCodeAndDeletedAtIsNull(transCode);
                if(stockTrans == null)
                    throw new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);
            this.reportStockAdjustmentTransExport(poReportDTO, stockTrans);
            file = ResourceUtils.getFile(invoiceExportPath);
        }
        else if(transCode.startsWith("EXSB")) {
            StockBorrowingTrans stockTrans = stockBorrowingTransRepo.getStockBorrowingTransByTransCodeAndDeletedAtIsNull(transCode);
            if(stockTrans == null)
                throw new ValidateException(ResponseMessage.STOCK_BORROWING_TRANS_IS_NOT_EXISTED);

            this.reportStockBorrowingTransExport(poReportDTO, stockTrans);
            file = ResourceUtils.getFile(invoiceExportPath);
        }
        else{
            throw new ValidateException(ResponseMessage.UNKNOWN);
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRMapArrayDataSource dataSource = new JRMapArrayDataSource(new Object[]{poReportDTO.getDataSources()});
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
                poReportDTO.getParameters(), dataSource);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

        return new ByteArrayInputStream(baos.toByteArray());
    }

    // Report PoTrans Export
    public void reportPoTransExport(PoReportDTO poReportDTO, PoTrans poTrans) {

        poReportDTO.setType("Trả hàng PO");
        poReportDTO.setTransCode(poTrans.getTransCode());
        poReportDTO.setPoNumber(poTrans.getPoNumber());
        poReportDTO.setInvoiceNumber(poTrans.getRedInvoiceNo());
        poReportDTO.setTransDate(poTrans.getTransDate());
        poReportDTO.setInternalNumber(poTrans.getInternalNumber());
        poReportDTO.setInvoiceDate(new Date());
        poReportDTO.setNote(poTrans.getNote());
        poReportDTO.setQuantity(poTrans.getTotalQuantity());
        poReportDTO.setTotalPrice(poTrans.getTotalAmount());

        List<PoTransDetail> poTransDetails = poTransDetailRepo.getPoTransDetailAndDeleteAtIsNull(poTrans.getId());
        List<PoProductReportDTO> poProductReportDTOS = this.groupProductsPoTrans(poTransDetails);

        JRBeanCollectionDataSource productsDataSource = new JRBeanCollectionDataSource(poProductReportDTOS, false);
        poReportDTO.setGroupProductsDataSource(productsDataSource);
    };


    public void reportStockAdjustmentTransExport(PoReportDTO poReportDTO, StockAdjustmentTrans stockAdjustmentTrans) {
        poReportDTO.setType("Xuất điều chỉnh tồn kho");
        poReportDTO.setTransCode(stockAdjustmentTrans.getTransCode());
        poReportDTO.setPoNumber("");
        poReportDTO.setInvoiceNumber(stockAdjustmentTrans.getRedInvoiceNo());
        poReportDTO.setTransDate(stockAdjustmentTrans.getTransDate());
        poReportDTO.setInternalNumber(stockAdjustmentTrans.getInternalNumber());
        poReportDTO.setInvoiceDate(new Date());
        poReportDTO.setQuantity(stockAdjustmentTrans.getTotalQuantity());
        poReportDTO.setTotalPrice(stockAdjustmentTrans.getTotalAmount());
        poReportDTO.setNote(stockAdjustmentTrans.getNote());

        List<StockAdjustmentTransDetail> stockAdjustmentTransDetails
            = stockAdjustmentTransDetailRepo.getStockAdjustmentTransDetail(stockAdjustmentTrans.getId());
        List<PoProductReportDTO> poProductReportDTOS = this.groupProductsStockAdjustmentTrans(stockAdjustmentTransDetails);

        JRBeanCollectionDataSource productsDataSource = new JRBeanCollectionDataSource(poProductReportDTOS, false);
        poReportDTO.setGroupProductsDataSource(productsDataSource);
    };

    public void reportStockBorrowingTransExport(PoReportDTO poReportDTO, StockBorrowingTrans stockBorrowingTrans) {

        poReportDTO.setType("Xuất vay mượn tồn kho");
        poReportDTO.setTransCode(stockBorrowingTrans.getTransCode());
        poReportDTO.setPoNumber("");
        poReportDTO.setInvoiceNumber(stockBorrowingTrans.getRedInvoiceNo());
        poReportDTO.setTransDate(stockBorrowingTrans.getTransDate());
        poReportDTO.setInternalNumber(stockBorrowingTrans.getInternalNumber());
        poReportDTO.setInvoiceDate(new Date());
        poReportDTO.setNote(stockBorrowingTrans.getNote());
        poReportDTO.setQuantity(stockBorrowingTrans.getTotalQuantity());
        poReportDTO.setTotalPrice(stockBorrowingTrans.getTotalAmount());

        List<StockBorrowingTransDetail> borrowingDetails = stockBorrowingTransDetailRepo.getStockBorrowingTransDetail(stockBorrowingTrans.getId());
        List<PoProductReportDTO> poProductReportDTOS = this.groupProductsStockBorrowingTrans(borrowingDetails);

        JRBeanCollectionDataSource productsDataSource = new JRBeanCollectionDataSource(poProductReportDTOS, false);
        poReportDTO.setGroupProductsDataSource(productsDataSource);
    };


    public List<PoProductReportDTO> groupProductsPoTrans(List<PoTransDetail> poTransDetails) {
         List<PoProductReportDTO> poProductReportDTOS = new ArrayList<>();

        // All products of PoTrans
        List<Product> products = poTransDetails.stream().map(transDetail -> this.findProduct(transDetail.getProductId())).collect(Collectors.toList());

        // Lọc các ngành hàng
        List<Long> catIds = products.stream().map(p -> p.getCatId()).collect(Collectors.toList());
        Set<Long> targetSet = new HashSet<>(catIds);

        // Map gồm tên ngành hàng và các sản phẩm của ngành hàng đó:
        Map<String, List<Product>> groupProducts = this.groupProducts(targetSet, products);

        // khởi tạo các PoProductReportDTO và list PoReportProductDetailDTO;
        for (Map.Entry<String, List<Product>> entry : groupProducts.entrySet()){
            int totalQuantity = 0;
            float totalPrice = 0F;
            List<Product> productList = entry.getValue();
            // So sánh 2 list PoTransDetal với ProductList để tạo PoProductReportDTO và list PoReportProductDetailDTO;
            PoProductReportDTO poProductReportDTO = new PoProductReportDTO();
                for(PoTransDetail poTransDetail: poTransDetails) {
                    for (Product product: productList) {
                        if(poTransDetail.getProductId().equals(product.getId())) {
                            PoReportProductDetailDTO productDetail = new PoReportProductDetailDTO();
                                productDetail.setProductCode(product.getProductCode());
                                productDetail.setProductName(product.getProductName());
                                productDetail.setUnit(product.getUom1());
                                productDetail.setQuantity(poTransDetail.getQuantity());
                                productDetail.setPrice(poTransDetail.getPrice());
                                productDetail.setTotalPrice(poTransDetail.getAmount());
                           // add list PoReportProductDetailDTO;
                            totalPrice += poTransDetail.getAmount();
                            totalQuantity += poTransDetail.getQuantity();
                            poProductReportDTO.addProduct(productDetail);
                        }
                    }
                }

            poProductReportDTO.setType(entry.getKey());
            poProductReportDTO.setTotalQuantity(totalQuantity);
            poProductReportDTO.setTotalPrice(totalPrice);
            poProductReportDTOS.add(poProductReportDTO);
        }

        return poProductReportDTOS;
    }

    public List<PoProductReportDTO>
        groupProductsStockAdjustmentTrans(List<StockAdjustmentTransDetail> stockAdjustmentTransDetails) {
        List<PoProductReportDTO> poProductReportDTOS = new ArrayList<>();

        List<Product> products = stockAdjustmentTransDetails.stream().map(trans -> this.findProduct(trans.getProductId())).collect(Collectors.toList());

        List<Long> catIds = products.stream().map(p -> p.getCatId()).collect(Collectors.toList());
        Set<Long> targetSet = new HashSet<>(catIds);

        Map<String, List<Product>> groupProducts = this.groupProducts(targetSet, products);


        for (Map.Entry<String, List<Product>> entry : groupProducts.entrySet()){
            int totalQuantity = 0;
            float totalPrice = 0F;
            List<Product> productList = entry.getValue();

            PoProductReportDTO poProductReportDTO = new PoProductReportDTO();
            for(StockAdjustmentTransDetail stockTransDetail: stockAdjustmentTransDetails) {
                for (Product product: productList) {
                    if(stockTransDetail.getProductId().equals(product.getId())) {
                        PoReportProductDetailDTO productDetail = new PoReportProductDetailDTO();
                        productDetail.setProductCode(product.getProductCode());
                        productDetail.setProductName(product.getProductName());
                        productDetail.setUnit(product.getUom1());
                        productDetail.setQuantity(stockTransDetail.getQuantity());
                        productDetail.setPrice(stockTransDetail.getPrice());
                        productDetail.setTotalPrice(stockTransDetail.getQuantity()*stockTransDetail.getPrice());

                        totalPrice += (stockTransDetail.getQuantity()*stockTransDetail.getPrice());
                        totalQuantity += stockTransDetail.getQuantity();
                        poProductReportDTO.addProduct(productDetail);
                    }
                }
            }

            poProductReportDTO.setType(entry.getKey());
            poProductReportDTO.setTotalQuantity(totalQuantity);
            poProductReportDTO.setTotalPrice(totalPrice);
            poProductReportDTOS.add(poProductReportDTO);
        }

        return poProductReportDTOS;
    }

    public List<PoProductReportDTO> groupProductsStockBorrowingTrans(List<StockBorrowingTransDetail> borrowingDetails) {
        List<PoProductReportDTO> poProductReportDTOS = new ArrayList<>();

        List<Product> products = borrowingDetails.stream().map(b -> this.findProduct(b.getProductId())).collect(Collectors.toList());

        List<Long> catIds = products.stream().map(p -> p.getCatId()).collect(Collectors.toList());
        Set<Long> targetSet = new HashSet<>(catIds);

        Map<String, List<Product>> groupProducts = this.groupProducts(targetSet, products);

        for (Map.Entry<String, List<Product>> entry : groupProducts.entrySet()){
            int totalQuantity = 0;
            float totalPrice = 0F;
            List<Product> productList = entry.getValue();

            PoProductReportDTO poProductReportDTO = new PoProductReportDTO();
            for(StockBorrowingTransDetail stockBorrowingTransDetail: borrowingDetails) {
                for (Product product: productList) {
                    if(stockBorrowingTransDetail.getProductId().equals(product.getId())) {
                        PoReportProductDetailDTO productDetail = new PoReportProductDetailDTO();
                        productDetail.setProductCode(product.getProductCode());
                        productDetail.setProductName(product.getProductName());
                        productDetail.setUnit(product.getUom1());
                        productDetail.setQuantity(stockBorrowingTransDetail.getQuantity());
                        productDetail.setPrice(stockBorrowingTransDetail.getPrice());
                        productDetail.setTotalPrice(stockBorrowingTransDetail.getQuantity()*stockBorrowingTransDetail.getPrice());

                        totalPrice += (stockBorrowingTransDetail.getQuantity()*stockBorrowingTransDetail.getPrice());
                        totalQuantity += stockBorrowingTransDetail.getQuantity();
                        poProductReportDTO.addProduct(productDetail);
                    }
                }
            }

            poProductReportDTO.setType(entry.getKey());
            poProductReportDTO.setTotalQuantity(totalQuantity);
            poProductReportDTO.setTotalPrice(totalPrice);
            poProductReportDTOS.add(poProductReportDTO);
        }

        return poProductReportDTOS;
    }

    public Product findProduct(Long id) {
        Product product = productRepo.findById(id).orElse(null);
        if(product == null)
            throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
        return product;
    }

    public Map<String, List<Product>> groupProducts(Set<Long> productInfoIds, List<Product> products) {
        Map<String, List<Product>> groupProducts = new HashMap<>();
        for(Long id: productInfoIds) {
            ProductInfo productInfo = productInfoRepo.findById(id).orElse(null);
            if(productInfo == null)
                throw new ValidateException(ResponseMessage.PRODUCT_INFO_NOT_EXISTS);
            List<Product> targetProducts = new ArrayList<>();
            for(Product product: products) {
                if(product.getCatId().equals(productInfo.getId())) {
                    targetProducts.add(product);
                }
            }
            groupProducts.put(productInfo.getApParamName(), targetProducts);
        }

        return groupProducts;
    }


}
