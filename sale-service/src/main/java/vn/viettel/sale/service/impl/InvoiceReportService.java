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
        PoReportDTO poReportDTO = new PoReportDTO();

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

        poReportDTO.setType("Xuất kho");
        poReportDTO.setTransCode(poTrans.getTransCode());
        poReportDTO.setPoNumber(poTrans.getPoNumber());
        poReportDTO.setInvoiceNumber(poTrans.getRedInvoiceNo());
        poReportDTO.setTransDate(poTrans.getTransDate());
        poReportDTO.setInternalNumber(poTrans.getInternalNumber());
        poReportDTO.setInvoiceDate(new Date());
        poReportDTO.setNote(poTrans.getNote());

        List<PoTransDetail> poTransDetails = poTransDetailRepo.getPoTransDetailByTransId(poTrans.getId());
        List<PoProductReportDTO> poProductReportDTOS = this.groupProducts(poTransDetails, poReportDTO);

        JRBeanCollectionDataSource productsDataSource = new JRBeanCollectionDataSource(poProductReportDTOS, false);
        poReportDTO.setGroupProductsDataSource(productsDataSource);
    };

    public List<PoProductReportDTO> groupProducts(List<PoTransDetail> poTransDetails, PoReportDTO poReportDTO) {
         List<PoProductReportDTO> poProductReportDTOS = new ArrayList<>();
         int poReportDTOQuantity = 0;
         Float poReportDTOtotalPrice = 0F;

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
            int totalQuantity = 0;
            Float totalPrice = 0F;
            List<Product> productList = entry.getValue();
            // So sánh 2 list PoTransDetal với ProductList để tạo PoProductReportDTO và list PoReportProductDetailDTO;
            // lặp cách này hơi lâu
            PoProductReportDTO poProductReportDTO = new PoProductReportDTO();
                for(PoTransDetail poTransDetail: poTransDetails) {
                    for (Product product: productList) {
                        if(poTransDetail.getProductId() == product.getId()) {
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
            poReportDTOtotalPrice += totalPrice;
            poReportDTOQuantity +=  totalQuantity;
            poProductReportDTO.setType(entry.getKey());
            poProductReportDTO.setTotalQuantity(totalQuantity);
            poProductReportDTO.setTotalPrice(totalPrice);
            poProductReportDTOS.add(poProductReportDTO);
        }

        poReportDTO.setQuantity(poReportDTOQuantity);
        poReportDTO.setTotalPrice(poReportDTOtotalPrice);

        return poProductReportDTOS;
    }


}
