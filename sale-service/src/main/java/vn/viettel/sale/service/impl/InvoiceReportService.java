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
import vn.viettel.core.db.entity.stock.PoTrans;
import vn.viettel.core.db.entity.stock.PoTransDetail;
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
import java.text.DecimalFormat;
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
        File file = null;
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        PoReportDTO poReportDTO = new PoReportDTO();

        if(transCode.startsWith("EXSP")) {
            PoTrans poTrans = poTransRepo.getPoTransByTransCodeAndType(transCode, 2);
            if(poTrans == null)
                throw new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED);
            this.reportPoTransExport(poReportDTO, poTrans);
            file = ResourceUtils.getFile("classpath:invoice-export.jrxml");
        }
        else if(transCode.startsWith("EXSA")) {

        }
        else if(transCode.startsWith("EXSB")) {

        }
        else{
            throw new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED);
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
        List<PoProductReportDTO> poProductReportDTOS = new ArrayList<>();
        poReportDTO.setType("Xuất kho");
        poReportDTO.setTransCode("1213");
        poReportDTO.setPoNumber(poTrans.getPoNumber());
        poReportDTO.setInvoiceNumber(poTrans.getRedInvoiceNo());
        poReportDTO.setTransDate(poTrans.getTransDate().toString());
        poReportDTO.setInternalNumber(poTrans.getInternalNumber());
        poReportDTO.setInvoiceDate(new Date());

        List<PoTransDetail> poTransDetails = poTransDetailRepo.getPoTransDetailByTransId(poTrans.getId());
        List<Product> products = new ArrayList<>();
        for(PoTransDetail transDetail: poTransDetails) {
            Product product = productRepo.findByIdAndDeletedAtIsNull(transDetail.getProductId());
            products.add(product);
        }
        this.groupProducts(poTransDetails);
        List<PoReportProductDetailDTO> productDetailDTOSNH1 = new ArrayList<>();
        PoReportProductDetailDTO poReportProductDetailDTO = new PoReportProductDetailDTO();
        poReportProductDetailDTO.setProductCode("Product121332");
        poReportProductDetailDTO.setProductName("Sản phẩm nhanh ăn chóng lớn dành cho người gầy");
        poReportProductDetailDTO.setUnit("HỘP");
        poReportProductDetailDTO.setQuantity(10);
        poReportProductDetailDTO.setPrice("12,000");
        poReportProductDetailDTO.setTotalPrice("120,000");
        productDetailDTOSNH1.add(poReportProductDetailDTO);

        List<PoReportProductDetailDTO> productDetailDTOSNH2 = new ArrayList<>();
        PoReportProductDetailDTO poReportProductDetailDTO2_1 = new PoReportProductDetailDTO();
        poReportProductDetailDTO2_1.setProductCode("Product121332");
        poReportProductDetailDTO2_1.setProductName("Sản phẩm nhanh mới");
        poReportProductDetailDTO2_1.setUnit("HỘP");
        poReportProductDetailDTO2_1.setQuantity(10);
        poReportProductDetailDTO2_1.setPrice("15,000");
        poReportProductDetailDTO2_1.setTotalPrice("150,000");
        productDetailDTOSNH2.add(poReportProductDetailDTO);


        PoProductReportDTO poProductReportDTONH1 = new PoProductReportDTO();
        poProductReportDTONH1.setType("Nganh hang A");
        poProductReportDTONH1.setTotalQuantity(1);
        poProductReportDTONH1.setTotalPrice("10,000,000");
        poProductReportDTONH1.setProducts(productDetailDTOSNH1);

        PoProductReportDTO poProductReportDTOSNH2 = new PoProductReportDTO();
        poProductReportDTOSNH2.setType("Nganh hang B");
        poProductReportDTOSNH2.setTotalQuantity(2);
        poProductReportDTOSNH2.setTotalPrice("15,000,000");
        poProductReportDTOSNH2.setProducts(productDetailDTOSNH1);

        poProductReportDTOS.add(poProductReportDTONH1);
        poProductReportDTOS.add(poProductReportDTOSNH2);


        JRBeanCollectionDataSource productsDataSource = new JRBeanCollectionDataSource(poProductReportDTOS, false);



        poReportDTO.setGroupProductsDataSource(productsDataSource);
    };

    public List<PoProductReportDTO> groupProducts(List<PoTransDetail> poTransDetails) {
         List<PoProductReportDTO> poProductReportDTOS = new ArrayList<>();
        // danh sách các sản phẩm
        List<Product> products = new ArrayList<>();
        for(PoTransDetail transDetail: poTransDetails) {
            Product product = productRepo.getOne(transDetail.getProductId());
            products.add(product);
        }
        // Lọc các ngành hàng
        List<Long> catIds = products.stream().map(product -> product.getCatId()).collect(Collectors.toList());
        Set<Long> targetSet = new HashSet<>(catIds);

        // Map gồm tên ngành hàng và các sản phẩm của ngành hàng đó:
        Map<String, List<Product>> groupProducts1 = new HashMap<>();
        for(Long id: targetSet) {
            ProductInfo productInfo = productInfoRepo.findById(id).orElse(null);
            // lỗi ko tìm thấy null
            List<Product> targetProducts = new ArrayList<>();
            for(Product product: products) {
                if(product.getCatId() == productInfo.getId()) {
                    targetProducts.add(product);
                }
            }
            groupProducts1.put(productInfo.getApParamName(), targetProducts);
        }

        // khởi tạo các PoProductReportDTO và list PoReportProductDetailDTO;
        for (Map.Entry<String, List<Product>> entry : groupProducts1.entrySet()){
            System.out.println("-----------------------------------------------------------------------------");
            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());
        }

            PoProductReportDTO poProductReportDTO = new PoProductReportDTO();

        return poProductReportDTOS;
    }




    /// invoiceType: 0 - Trả hàng PO, 1 - Xuất điều chỉnh, 2 - xuất vay mượn
    public ByteArrayInputStream invoiceExport(Long shopId, String transCode) throws FileNotFoundException, JRException {
        int totalQuantity = 0;
        int totalPrice = 0;
        Map<String,Object> parameters = new HashMap<>();
        List<PoReportProductDetailDTO> products = new ArrayList<>();
        DecimalFormat formatter = new DecimalFormat("###,###,###");

        Shop shop = shopRepo.findById(shopId).orElse(null);
        if(shop == null)
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        parameters.put("shopName", shop.getShopName());
        parameters.put("shopAddress", shop.getAddress());
        parameters.put("phoneNumber", shop.getPhone());
        parameters.put("faxNumber", shop.getFax());

        if(transCode.startsWith("EXSP")) {
            PoTrans poTrans = poTransRepo.getPoTransByTransCodeAndType(transCode, 2);
            if(poTrans == null)
                throw new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED);
            parameters.put("type","Xuất trả hàng PO");
            parameters.put("transCode", poTrans.getTransCode());
            parameters.put("poNumber",poTrans.getPoNumber());
            parameters.put("invoiceNumber", poTrans.getRedInvoiceNo());
            parameters.put("transDate", poTrans.getTransDate());
            parameters.put("internalNumber", poTrans.getInternalNumber());
            parameters.put("invoiceDate", new Date());
            parameters.put("note", poTrans.getNote());

            List<PoTransDetail> poTransDetails = poTransDetailRepo.getPoTransDetailByTransId(poTrans.getId());
            for(PoTransDetail transDetail: poTransDetails) {
                Product product = productRepo.findByIdAndDeletedAtIsNull(transDetail.getProductId());
                PoReportProductDetailDTO poProduct =
                        new PoReportProductDetailDTO(product.getProductCode(), product.getProductName(), product.getUom1())
                                .withQuantityAndPrice(transDetail.getQuantity(), transDetail.getPrice());
                totalQuantity += transDetail.getQuantity();
                totalPrice += transDetail.getAmount();
                products.add(poProduct);
            }

            parameters.put("totalQuantity", totalQuantity);
            parameters.put("totalPrice", formatter.format(totalPrice));
        }

       else if(transCode.startsWith("EXSA")) {
//            StockAdjustmentTrans stockAdjustment = stockAdjustmentTransRepo.findById(invoiceId).orElse(null);
//            if(stockAdjustment == null)
//                throw new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);
//
//            parameters.put("type","Xuất điều chỉnh tồn kho");
//            parameters.put("transCode", stockAdjustment.getTransCode());
//            parameters.put("poNumber", ""); //poNumber is not exited
//            parameters.put("invoiceNumber", stockAdjustment.getRedInvoiceNo());
//            parameters.put("transDate", stockAdjustment.getTransDate());
//            parameters.put("internalNumber", "");
//            parameters.put("invoiceDate", new Date());
//            parameters.put("note", stockAdjustment.getNote());
//
//            List<StockAdjustmentTransDetail> stockdetails =
//                    stockAdjustmentTransDetailRepo.getStockAdjustmentTransDetailsByTransId(stockAdjustment.getId());
//            for(StockAdjustmentTransDetail stockdetail: stockdetails) {
//                Product product = productRepo.findByIdAndDeletedAtIsNull(stockdetail.getProductId());
//                PoProductReportDTO poProduct =
//                        new PoProductReportDTO(product.getProductCode(), product.getProductName(), product.getUom1())
//                                .withQuantityAndPrice(stockdetail.getQuantity(), stockdetail.getPrice());
//                totalQuantity += stockdetail.getQuantity();
//                totalPrice += (stockdetail.getQuantity()*stockdetail.getPrice());
//                products.add(poProduct);
//            }
//
//            parameters.put("totalQuantity", totalQuantity);
//            parameters.put("totalPrice", formatter.format(totalPrice));
        }

        else if(transCode.startsWith("EXSB")) {

        }
        else{
            throw new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED);
        }
       /*
        if(invoiceType == 1) {


        }else if(invoiceType == 2) {
            StockBorrowingTrans borrowingTran = stockBorrowingTransRepo.findById(invoiceId).orElse(null);
            if(borrowingTran == null)
                throw new ValidateException(ResponseMessage.STOCK_BORROWING_TRANS_IS_NOT_EXISTED);

            parameters.put("type","Xuất vay mượn");
            parameters.put("transCode", borrowingTran.getTransCode());
            parameters.put("poNumber", ""); //poNumber is not exited
            parameters.put("invoiceNumber", borrowingTran.getRedInvoiceNo());
            parameters.put("transDate", borrowingTran.getTransDate());
            parameters.put("internalNumber", "");
            parameters.put("invoiceDate", new Date());
            parameters.put("note", borrowingTran.getNote());

            List<StockBorrowingTransDetail> borrowingdetails =
                    stockBorrowingTransDetailRepo.getStockBorrowingTransDetailByTransId(borrowingTran.getId());
            for(StockBorrowingTransDetail borrowingdetail: borrowingdetails) {
                Product product = productRepo.findByIdAndDeletedAtIsNull(borrowingdetail.getProductId());
                PoProductReportDTO poProduct =
                        new PoProductReportDTO(product.getProductCode(), product.getProductName(), product.getUom1())
                                .withQuantityAndPrice(borrowingdetail.getQuantity(), borrowingdetail.getPrice());
                totalQuantity += borrowingdetail.getQuantity();
                totalPrice += (borrowingdetail.getQuantity()*borrowingdetail.getPrice());
                products.add(poProduct);
            }

            parameters.put("totalQuantity", totalQuantity);
            parameters.put("totalPrice", formatter.format(totalPrice));

        }else{

        }
        */


        File file = ResourceUtils.getFile("classpath:invoice-export.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

        JRBeanCollectionDataSource productJRB = new JRBeanCollectionDataSource(products);

        List<Object> list = new ArrayList<>();
        list.add(productJRB);
        parameters.put("productDetail", productJRB);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperPrint jprint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        JasperExportManager.exportReportToPdfStream(jprint, baos);

        return new ByteArrayInputStream(baos.toByteArray());
    }

    /// invoiceType: 0 - Trả hàng PO, 1 - Xuất điều chỉnh, 2 - xuất vay mượn
    public ByteArrayInputStream invoiceImport(Long shopId, Long invoiceId, Integer invoiceType) throws FileNotFoundException, JRException {
        int totalQuantity = 0;
        int totalPrice = 0;
        Map<String,Object> parameters = new HashMap<>();
        List<PoReportProductDetailDTO> productDDTOS = new ArrayList<>();
        List<PoReportProductDetailDTO> promotionProudctDTOS = new ArrayList<>();
        DecimalFormat formatter = new DecimalFormat("###,###,###");

        Shop shop = shopRepo.findById(shopId).orElse(null);
        if(shop == null)
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        parameters.put("shopName", shop.getShopName());
        parameters.put("shopAddress", shop.getAddress());
        parameters.put("phoneNumber", shop.getPhone());
        parameters.put("faxNumber", shop.getFax());

        if(invoiceType == 1) {

        }else if(invoiceType == 2) {

        }else{
            PoTrans poTrans = poTransRepo.getPoTransByIdAndDeletedAtIsNull(invoiceId);
            if(poTrans == null)
                throw new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED);
            parameters.put("type","Xuất trả hàng PO");
            parameters.put("transCode", poTrans.getTransCode());
            parameters.put("poNumber",poTrans.getPoNumber());
            parameters.put("invoiceNumber", poTrans.getRedInvoiceNo());
            parameters.put("transDate", poTrans.getTransDate());
            parameters.put("internalNumber", poTrans.getInternalNumber());
            parameters.put("invoiceDate", new Date());
            parameters.put("note", poTrans.getNote());

            List<PoTransDetail> poTransDetails = poTransDetailRepo.getPoTransDetailByTransId(invoiceId);
            for(PoTransDetail transDetail: poTransDetails) {
                Product product = productRepo.findByIdAndDeletedAtIsNull(transDetail.getProductId());
                PoReportProductDetailDTO poProduct =
                        new PoReportProductDetailDTO(product.getProductCode(), product.getProductName(), product.getUom1())
                                .withQuantityAndPrice(transDetail.getQuantity(), transDetail.getPrice());
                totalQuantity += transDetail.getQuantity();
                totalPrice += transDetail.getAmount();
                productDDTOS.add(poProduct);
            }

            parameters.put("totalQuantity", totalQuantity);
            parameters.put("totalPrice", formatter.format(totalPrice));
        }
        JRBeanCollectionDataSource productJRB = new JRBeanCollectionDataSource(productDDTOS);
        parameters.put("productDetail", productJRB);

        promotionProudctDTOS.add(
            new PoReportProductDetailDTO("Code12323423627", "Sản phẩm ăn nhanh chóng lớn", "Hộp")
                .withQuantityAndPrice(1, 120000F));
        promotionProudctDTOS.add(
                new PoReportProductDetailDTO("Code123234", "Sản phẩm ăn nhanh chóng lớn", "Hộp")
                        .withQuantityAndPrice(1, 120000F));
        promotionProudctDTOS.add(
                new PoReportProductDetailDTO("Code123", "Sản phẩm ăn nhanh chóng lớn 2", "Hộp")
                        .withQuantityAndPrice(1, 100000F));
        promotionProudctDTOS.add(
                new PoReportProductDetailDTO("Code123", "Sản phẩm ăn nhanh chóng lớn 2", "Hộp")
                        .withQuantityAndPrice(1, 100000F));
        promotionProudctDTOS.add(
                new PoReportProductDetailDTO("Code123", "Sản phẩm ăn nhanh chóng lớn 2", "Hộp")
                        .withQuantityAndPrice(1, 100000F));
        promotionProudctDTOS.add(
                new PoReportProductDetailDTO("Code123", "Sản phẩm ăn nhanh chóng lớn 2", "Hộp")
                        .withQuantityAndPrice(1, 100000F));
        if(!promotionProudctDTOS.isEmpty()) {
            JRBeanCollectionDataSource promotionProductJRB = new JRBeanCollectionDataSource(promotionProudctDTOS);
            parameters.put("promotionProducts", promotionProductJRB);
        }

        File file = ResourceUtils.getFile("classpath:invoice-import.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperPrint jprint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        JasperExportManager.exportReportToPdfStream(jprint, baos);

        return new ByteArrayInputStream(baos.toByteArray());
    }



}
