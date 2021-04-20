package vn.viettel.sale.service.impl;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.ProductInfo;
import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.InvoiceReportService;
import vn.viettel.sale.service.dto.PoProductReportDTO;
import vn.viettel.sale.service.dto.PoReportDTO;
import vn.viettel.sale.service.dto.PoReportProductDetailDTO;
import vn.viettel.sale.service.feign.ShopClient;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InvoiceReportServiceImpl extends BaseServiceImpl<PoTrans, PoTransRepository> implements InvoiceReportService {

    @Autowired
    ShopClient shopClient;

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

    public ByteArrayInputStream invoiceReport(Long shopId, String transCode) throws JRException, IOException {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext();
        final String invoiceExportPath = "classpath:/invoice-export.jrxml";
        final String invoiceImportPath = "classpath:/invoice-import.jrxml";
        InputStream inputStream;

        PoReportDTO poReportDTO = new PoReportDTO();

        Shop shop = shopClient.getById(shopId).getData();
        if(shop ==  null)
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        poReportDTO.setShopName(shop.getShopName());
        poReportDTO.setShopAddress(shop.getAddress());
        poReportDTO.setPhoneNumber(shop.getMobiPhone());
        poReportDTO.setFaxNumber(shop.getFax());

        if(transCode.startsWith("EXSP")){
            PoTrans poTrans = poTransRepo.getPoTransByTransCodeAndDeletedAtIsNull(transCode)
                    .orElseThrow( () -> new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED));
            this.reportPoTransExport(poReportDTO, poTrans);
            Resource resource = classPathXmlApplicationContext.getResource(invoiceExportPath);
            inputStream = resource.getInputStream();
        }
        else if(transCode.startsWith("EXST")){
            StockAdjustmentTrans stockTrans = stockAdjustmentTransRepo.getStockAdjustmentTransByTransCodeAndDeletedAtIsNull(transCode)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED));
            this.reportStockAdjustmentTransExport(poReportDTO, stockTrans);
            Resource resource = classPathXmlApplicationContext.getResource(invoiceExportPath);
            inputStream = resource.getInputStream();
        }
        else if(transCode.startsWith("EXSB")){
            StockBorrowingTrans stockTrans = stockBorrowingTransRepo.getStockBorrowingTransByTransCodeAndDeletedAtIsNull(transCode)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_BORROWING_TRANS_IS_NOT_EXISTED));
            this.reportStockBorrowingTransExport(poReportDTO, stockTrans);
            Resource resource = classPathXmlApplicationContext.getResource(invoiceExportPath);
            inputStream = resource.getInputStream();
        }
        else if(transCode.startsWith("IMP")){
            PoTrans poTrans = poTransRepo.getPoTransByTransCodeAndDeletedAtIsNull(transCode)
                    .orElseThrow( () -> new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED));;
            this.reportPoTransImport(poReportDTO, poTrans);
            Resource resource = classPathXmlApplicationContext.getResource(invoiceImportPath);
            inputStream = resource.getInputStream();
        }
        else if(transCode.startsWith("DCT")){
            StockAdjustmentTrans stockTrans = stockAdjustmentTransRepo.getStockAdjustmentTransByTransCodeAndDeletedAtIsNull(transCode)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED));
            this.reportStockAdjustmentTransImport(poReportDTO, stockTrans);
            Resource resource = classPathXmlApplicationContext.getResource(invoiceImportPath);
            inputStream = resource.getInputStream();
        }
        else if(transCode.startsWith("EDC")) {
            StockBorrowingTrans stockTrans = stockBorrowingTransRepo.getStockBorrowingTransByTransCodeAndDeletedAtIsNull(transCode)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_BORROWING_TRANS_IS_NOT_EXISTED));
            this.reportStockBorrowingTransImport(poReportDTO, stockTrans);
            Resource resource = classPathXmlApplicationContext.getResource(invoiceImportPath);
            inputStream = resource.getInputStream();
        }
        else{
            throw new ValidateException(ResponseMessage.UNKNOWN);
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        JRMapArrayDataSource dataSource = new JRMapArrayDataSource(new Object[]{poReportDTO.getDataSources()});
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, poReportDTO.getParameters(), dataSource);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

        return new ByteArrayInputStream(baos.toByteArray());
    }

    // Report PoTrans
    public void reportPoTransImport(PoReportDTO poReportDTO, PoTrans poTrans) {
        poReportDTO.setType("Nhập hàng");
        this.poReportDTOMapping(poReportDTO, poTrans);
        List<PoTransDetail> allPoTransDetails = poTransDetailRepo.getPoTransDetailAndDeleteAtIsNull(poTrans.getId());
        List<PoTransDetail> poTransProducts = new ArrayList<>();
        List<PoTransDetail> poTransProductsPromotion = new ArrayList<>();
        for (PoTransDetail detail: allPoTransDetails) {
            if(detail.getPrice() != null && detail.getPrice() > 0) {
                poTransProducts.add(detail);
            }else{
                poTransProductsPromotion.add(detail);
            }
        }
        List<PoProductReportDTO> poProductReportDTOS = this.groupProductsPoTrans(poTransProducts, 1, poReportDTO);
        List<PoProductReportDTO> poProductPromotionReportDTOS = this.groupProductsPoTrans(poTransProductsPromotion, poTransProducts.size() + 1, null);

        JRBeanCollectionDataSource groupProductsDataSource = new JRBeanCollectionDataSource(poProductReportDTOS, false);
        JRBeanCollectionDataSource groupProductsPromotionDataSource = new JRBeanCollectionDataSource(poProductPromotionReportDTOS, false);

        poReportDTO.setGroupProductsDataSource(groupProductsDataSource);
        poReportDTO.setGroupProductsPromotionDataSource(groupProductsPromotionDataSource);
    }

    public void reportPoTransExport(PoReportDTO poReportDTO, PoTrans poTrans) {
        poReportDTO.setType("Trả hàng");
        this.poReportDTOMapping(poReportDTO, poTrans);
        List<PoTransDetail> poTransDetails = poTransDetailRepo.getPoTransDetailAndDeleteAtIsNull(poTrans.getId());
        List<PoProductReportDTO> poProductReportDTOS = this.groupProductsPoTrans(poTransDetails, 1, null);

        JRBeanCollectionDataSource productsDataSource = new JRBeanCollectionDataSource(poProductReportDTOS, false);
        poReportDTO.setGroupProductsDataSource(productsDataSource);
    };

    // Report StockAdjustmentTrans
    public void reportStockAdjustmentTransImport(PoReportDTO poReportDTO, StockAdjustmentTrans stockAdjustmentTrans) {
        poReportDTO.setType("Nhập điều chỉnh tồn kho");
        this.poReportDTOMapping(poReportDTO, stockAdjustmentTrans);
        List<StockAdjustmentTransDetail> stockAdjustmentTransDetails
                = stockAdjustmentTransDetailRepo.getStockAdjustmentTransDetail(stockAdjustmentTrans.getId());
        List<StockAdjustmentTransDetail> stockTransProducts = new ArrayList<>();
        List<StockAdjustmentTransDetail> stockTransProductsPromotion = new ArrayList<>();
        for (StockAdjustmentTransDetail detail: stockAdjustmentTransDetails) {
            if(detail.getPrice() != null && detail.getPrice() > 0) {
                stockTransProducts.add(detail);
            }else{
                stockTransProductsPromotion.add(detail);
            }
        }
        List<PoProductReportDTO> poProductReportDTOS = this.groupProductsStockAdjustmentTrans(stockTransProducts, 1, poReportDTO);
        List<PoProductReportDTO> poProductPromotionReportDTOS = this.groupProductsStockAdjustmentTrans(stockTransProductsPromotion, stockTransProducts.size() +1, null);

        JRBeanCollectionDataSource productsDataSource = new JRBeanCollectionDataSource(poProductReportDTOS, false);
        JRBeanCollectionDataSource groupProductsPromotionDataSource = new JRBeanCollectionDataSource(poProductPromotionReportDTOS, false);

        poReportDTO.setGroupProductsDataSource(productsDataSource);
        poReportDTO.setGroupProductsPromotionDataSource(groupProductsPromotionDataSource);
    }

    public void reportStockAdjustmentTransExport(PoReportDTO poReportDTO, StockAdjustmentTrans stockAdjustmentTrans) {
        poReportDTO.setType("Xuất điều chỉnh tồn kho");
        this.poReportDTOMapping(poReportDTO, stockAdjustmentTrans);
        List<StockAdjustmentTransDetail> stockAdjustmentTransDetails
                = stockAdjustmentTransDetailRepo.getStockAdjustmentTransDetail(stockAdjustmentTrans.getId());
        List<PoProductReportDTO> poProductReportDTOS = this.groupProductsStockAdjustmentTrans(stockAdjustmentTransDetails, 1, null);

        JRBeanCollectionDataSource productsDataSource = new JRBeanCollectionDataSource(poProductReportDTOS, false);
        poReportDTO.setGroupProductsDataSource(productsDataSource);
    }

    //Report StockBorrowingTrans
    public void reportStockBorrowingTransImport(PoReportDTO poReportDTO, StockBorrowingTrans stockBorrowingTrans) {
        poReportDTO.setType("Nhập vay mượn");
        this.poReportDTOMapping(poReportDTO, stockBorrowingTrans);
        List<StockBorrowingTransDetail> borrowingDetails = stockBorrowingTransDetailRepo.getStockBorrowingTransDetail(stockBorrowingTrans.getId());
        List<StockBorrowingTransDetail> stockTransProducts = new ArrayList<>();
        List<StockBorrowingTransDetail> stockTransProductsPromotion = new ArrayList<>();
        for (StockBorrowingTransDetail detail: borrowingDetails) {
            if(detail.getPrice() != null && detail.getPrice() > 0) {
                stockTransProducts.add(detail);
            }else{
                stockTransProductsPromotion.add(detail);
            }
        }

        List<PoProductReportDTO> poProductReportDTOS = this.groupProductsStockBorrowingTrans(stockTransProducts, 1, poReportDTO);
        List<PoProductReportDTO> poProductPromotionReportDTOS = this.groupProductsStockBorrowingTrans(stockTransProductsPromotion, stockTransProducts.size() + 1,null);

        JRBeanCollectionDataSource productsDataSource = new JRBeanCollectionDataSource(poProductReportDTOS, false);
        JRBeanCollectionDataSource groupProductsPromotionDataSource = new JRBeanCollectionDataSource(poProductPromotionReportDTOS, false);

        poReportDTO.setGroupProductsDataSource(productsDataSource);
        poReportDTO.setGroupProductsPromotionDataSource(groupProductsPromotionDataSource);
    }

    public void reportStockBorrowingTransExport(PoReportDTO poReportDTO, StockBorrowingTrans stockBorrowingTrans) {
        poReportDTO.setType("Xuất vay mượn");
        this.poReportDTOMapping(poReportDTO, stockBorrowingTrans);
        List<StockBorrowingTransDetail> borrowingDetails = stockBorrowingTransDetailRepo.getStockBorrowingTransDetail(stockBorrowingTrans.getId());
        List<PoProductReportDTO> poProductReportDTOS = this.groupProductsStockBorrowingTrans(borrowingDetails, 1, null);
        JRBeanCollectionDataSource productsDataSource = new JRBeanCollectionDataSource(poProductReportDTOS, false);
        poReportDTO.setGroupProductsDataSource(productsDataSource);
    };


    //Util
    // list promotion product dont need add poReport
    public List<PoProductReportDTO> groupProductsPoTrans(List<PoTransDetail> poTransDetails, int orderNumber, PoReportDTO poReportDTO) {
        List<PoProductReportDTO> poProductReportDTOS = new ArrayList<>();
        float sumTotalPriceNotVat = 0;

        // All products of PoTrans
        List<Product> products = poTransDetails.stream().map(transDetail -> this.findProduct(transDetail.getProductId())).collect(Collectors.toList());

        //filter product info
        List<Long> catIds = products.stream().map(p -> p.getCatId()).collect(Collectors.toList());
        Set<Long> targetSet = new HashSet<>(catIds);

        Map<String, List<Product>> groupProducts = this.groupProducts(targetSet, products);

        //  PoProductReportDTO and list PoReportProductDetailDTO;
        for (Map.Entry<String, List<Product>> entry : groupProducts.entrySet()){
            float totalPrice = 0;
            float totalPriceNotVat = 0;
            List<Product> productList = entry.getValue();

            PoProductReportDTO poProductReportDTO = new PoProductReportDTO();
            for(PoTransDetail transDetail: poTransDetails) {
                float price = transDetail.getPrice()!=null?transDetail.getPrice():0;
                float priceNotVat = transDetail.getPriceNotVat()!=null?transDetail.getPriceNotVat():0;
                for (Product product: productList) {
                    if(transDetail.getProductId().equals(product.getId())) {
                        PoReportProductDetailDTO productDetail =
                                new PoReportProductDetailDTO(product.getProductCode(), product.getProductName(), product.getUom1());
                        productDetail.setOrderNumber(orderNumber++);
                        productDetail.setQuantity(transDetail.getQuantity());
                        // invoice export
                        productDetail.setPrice(price);
                        productDetail.setTotalPrice(transDetail.getAmount());
                        // invoice import
                        productDetail.setPriceNotVat(priceNotVat);
                        productDetail.setTotalPriceNotVat(transDetail.getAmountNotVat());
                        poProductReportDTO.addProduct(productDetail);
                        poProductReportDTO.addTotalQuantity( transDetail.getQuantity());
                        // not product promotion
                        if(transDetail.getAmount()!=null) totalPrice += transDetail.getAmount();
                        if(transDetail.getAmountNotVat()!=null) totalPriceNotVat += transDetail.getAmountNotVat();
                    }
                }
            }
            sumTotalPriceNotVat += totalPriceNotVat;
            poProductReportDTO.setType(entry.getKey());
            poProductReportDTO.setTotalPrice(totalPrice);
            poProductReportDTO.setTotalPriceNotVar(totalPriceNotVat);
            poProductReportDTOS.add(poProductReportDTO);
        }
        // for invoice import calculate VAT
        if(poReportDTO != null) {
            poReportDTO.setTotalPriceVar(poReportDTO.getSumPrice() - sumTotalPriceNotVat);
            poReportDTO.setTotalPriceNotVar(sumTotalPriceNotVat);
        }

        return poProductReportDTOS;
    }

    // list promotion product dont need add poReport
    public List<PoProductReportDTO> groupProductsStockAdjustmentTrans(List<StockAdjustmentTransDetail> stockAdjustmentTransDetails, int orderNumber, PoReportDTO poReportDTO) {
        float sumTotalPriceNotVat = 0;
        List<PoProductReportDTO> poProductReportDTOS = new ArrayList<>();

        List<Product> products = stockAdjustmentTransDetails.stream().map(trans -> this.findProduct(trans.getProductId())).collect(Collectors.toList());

        List<Long> catIds = products.stream().map(p -> p.getCatId()).collect(Collectors.toList());
        Set<Long> targetSet = new HashSet<>(catIds);

        Map<String, List<Product>> groupProducts = this.groupProducts(targetSet, products);
        for (Map.Entry<String, List<Product>> entry : groupProducts.entrySet()){
            float totalPrice = 0;
            float totalPriceNotVat = 0;
            List<Product> productList = entry.getValue();

            PoProductReportDTO poProductReportDTO = new PoProductReportDTO();
            for(StockAdjustmentTransDetail transDetail: stockAdjustmentTransDetails) {
                float price = transDetail.getPrice()!=null?transDetail.getPrice():0;
                float priceNotVat = transDetail.getPriceNotVat()!=null?transDetail.getPriceNotVat():0;
                for (Product product: productList) {
                    if(transDetail.getProductId().equals(product.getId())) {
                        PoReportProductDetailDTO productDetail =
                                new PoReportProductDetailDTO(product.getProductCode(), product.getProductName(), product.getUom1());
                        productDetail.setOrderNumber(orderNumber++);
                        productDetail.setQuantity(transDetail.getQuantity());
                        //invoice export
                        productDetail.setPrice(price);
                        productDetail.setTotalPrice(transDetail.getQuantity()*price);
                        // invoice import
                        productDetail.setPriceNotVat(priceNotVat);
                        productDetail.setTotalPriceNotVat(transDetail.getQuantity()*priceNotVat);
                        poProductReportDTO.addProduct(productDetail);
                        poProductReportDTO.addTotalQuantity(transDetail.getQuantity());

                        totalPrice += (transDetail.getQuantity()*price);
                        totalPriceNotVat += (transDetail.getQuantity()*priceNotVat);
                    }
                }
            }
            sumTotalPriceNotVat += totalPriceNotVat;
            poProductReportDTO.setType(entry.getKey());
            poProductReportDTO.setTotalPrice(totalPrice);
            poProductReportDTO.setTotalPriceNotVar(totalPriceNotVat);
            poProductReportDTOS.add(poProductReportDTO);
        }
        // for invoice import calculate VAT
        if(poReportDTO != null) {
            poReportDTO.setTotalPriceVar(poReportDTO.getSumPrice() - sumTotalPriceNotVat);
            poReportDTO.setTotalPriceNotVar(sumTotalPriceNotVat);
        }

        return poProductReportDTOS;
    }

    public List<PoProductReportDTO> groupProductsStockBorrowingTrans(List<StockBorrowingTransDetail> borrowingDetails, int orderNumber, PoReportDTO poReportDTO) {
        float sumTotalPriceNotVat = 0;
        List<PoProductReportDTO> poProductReportDTOS = new ArrayList<>();

        List<Product> products = borrowingDetails.stream().map(b -> this.findProduct(b.getProductId())).collect(Collectors.toList());

        List<Long> catIds = products.stream().map(p -> p.getCatId()).collect(Collectors.toList());
        Set<Long> targetSet = new HashSet<>(catIds);

        Map<String, List<Product>> groupProducts = this.groupProducts(targetSet, products);

        for (Map.Entry<String, List<Product>> entry : groupProducts.entrySet()){
            float totalPrice = 0;
            float totalPriceNotVat = 0;
            List<Product> productList = entry.getValue();

            PoProductReportDTO poProductReportDTO = new PoProductReportDTO();
            for(StockBorrowingTransDetail transDetail: borrowingDetails) {
                float price = transDetail.getPrice()!=null?transDetail.getPrice():0;
                float priceNotVat = transDetail.getPriceNotVat()!=null?transDetail.getPriceNotVat():0;
                for (Product product: productList) {
                    if(transDetail.getProductId().equals(product.getId())) {
                        PoReportProductDetailDTO productDetail =
                                new PoReportProductDetailDTO(product.getProductCode(), product.getProductName(), product.getUom1());
                        productDetail.setOrderNumber(orderNumber++);
                        productDetail.setQuantity(transDetail.getQuantity());
                        productDetail.setPrice(price);
                        productDetail.setTotalPrice(transDetail.getQuantity()*price);

                        productDetail.setPriceNotVat(priceNotVat);
                        productDetail.setTotalPriceNotVat(transDetail.getQuantity()*priceNotVat);
                        poProductReportDTO.addProduct(productDetail);
                        poProductReportDTO.addTotalQuantity(transDetail.getQuantity());

                        totalPrice += (transDetail.getQuantity()*price);
                        totalPriceNotVat += (transDetail.getQuantity()*priceNotVat);
                    }
                }
            }
            sumTotalPriceNotVat += totalPriceNotVat;
            poProductReportDTO.setType(entry.getKey());
            poProductReportDTO.setTotalPrice(totalPrice);
            poProductReportDTO.setTotalPriceNotVar(totalPriceNotVat);
            poProductReportDTOS.add(poProductReportDTO);
        }
        // for invoice import calculate VAT
        if(poReportDTO != null) {
            poReportDTO.setTotalPriceVar(poReportDTO.getSumPrice() - sumTotalPriceNotVat);
            poReportDTO.setTotalPriceNotVar(sumTotalPriceNotVat);
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
            groupProducts.put(productInfo.getProductInfoName(), targetProducts);
        }

        return groupProducts;
    }

    public PoReportDTO poReportDTOMapping(PoReportDTO poReportDTO, PoTrans poTrans) {
        poReportDTO.setTransCode(poTrans.getTransCode());
        poReportDTO.setPoNumber(poTrans.getPoNumber());
        poReportDTO.setInvoiceNumber(poTrans.getRedInvoiceNo());
        poReportDTO.setTransDate(poTrans.getTransDate());
        poReportDTO.setInternalNumber(poTrans.getInternalNumber());
        poReportDTO.setInvoiceDate(new Date());
        poReportDTO.setNote(poTrans.getNote());
        poReportDTO.setQuantity(poTrans.getTotalQuantity());
        poReportDTO.setTotalPrice(poTrans.getTotalAmount());
        return poReportDTO;
    }

    public PoReportDTO poReportDTOMapping(PoReportDTO poReportDTO, StockAdjustmentTrans stockAdjustmentTrans) {
        poReportDTO.setTransCode(stockAdjustmentTrans.getTransCode());
        poReportDTO.setPoNumber("");
        poReportDTO.setInvoiceNumber(stockAdjustmentTrans.getRedInvoiceNo());
        poReportDTO.setTransDate(stockAdjustmentTrans.getTransDate());
        poReportDTO.setInternalNumber(stockAdjustmentTrans.getInternalNumber());
        poReportDTO.setInvoiceDate(new Date());
        poReportDTO.setQuantity(stockAdjustmentTrans.getTotalQuantity());
        poReportDTO.setTotalPrice(stockAdjustmentTrans.getTotalAmount());
        poReportDTO.setNote(stockAdjustmentTrans.getNote());
        return poReportDTO;
    }

    public PoReportDTO poReportDTOMapping(PoReportDTO poReportDTO, StockBorrowingTrans stockBorrowingTrans) {
        poReportDTO.setTransCode(stockBorrowingTrans.getTransCode());
        poReportDTO.setPoNumber("");
        poReportDTO.setInvoiceNumber(stockBorrowingTrans.getRedInvoiceNo());
        poReportDTO.setTransDate(stockBorrowingTrans.getTransDate());
        poReportDTO.setInternalNumber(stockBorrowingTrans.getInternalNumber());
        poReportDTO.setInvoiceDate(new Date());
        poReportDTO.setNote(stockBorrowingTrans.getNote());
        poReportDTO.setQuantity(stockBorrowingTrans.getTotalQuantity());
        poReportDTO.setTotalPrice(stockBorrowingTrans.getTotalAmount());
        return poReportDTO;
    }

}
