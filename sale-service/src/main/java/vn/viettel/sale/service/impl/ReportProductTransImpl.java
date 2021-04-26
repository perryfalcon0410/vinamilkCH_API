package vn.viettel.sale.service.impl;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.ShopClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportProductTransImpl extends BaseServiceImpl<PoTrans, PoTransRepository> {
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

    public Response<ReportProductTransDTO> getReport(Long shopId, String transCode) {
        ReportProductTransDTO reportDTO = new ReportProductTransDTO();

        ShopDTO shop = shopClient.getById(shopId).getData();
        if (shop == null)
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        reportDTO.setShop(shop);

        if (transCode.startsWith("EXSP")) {
            PoTrans poTrans = poTransRepo.getPoTransByTransCodeAndStatus(transCode, 1)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED));
            this.reportPoTransExport(reportDTO, poTrans);
        } else if (transCode.startsWith("EXST")) {
            StockAdjustmentTrans stockTrans = stockAdjustmentTransRepo.getStockAdjustmentTransByTransCodeAndDeletedAtIsNull(transCode)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED));
              this.reportStockAdjustmentTransExport(reportDTO, stockTrans);
        } else if (transCode.startsWith("EXSB")) {
            StockBorrowingTrans stockTrans = stockBorrowingTransRepo.getStockBorrowingTransByTransCodeAndDeletedAtIsNull(transCode)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_BORROWING_TRANS_IS_NOT_EXISTED));
            //  this.reportStockBorrowingTransExport(poReportDTO, stockTrans);
        } else if (transCode.startsWith("IMP")) {
            PoTrans poTrans = poTransRepo.getPoTransByTransCodeAndStatus(transCode, 1)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED));
             this.reportPoTransImport(reportDTO, poTrans);
        } else if (transCode.startsWith("DCT")) {
            StockAdjustmentTrans stockTrans = stockAdjustmentTransRepo.getStockAdjustmentTransByTransCodeAndDeletedAtIsNull(transCode)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED));
            // this.reportStockAdjustmentTransImport(poReportDTO, stockTrans);
        } else if (transCode.startsWith("EDC")) {
            StockBorrowingTrans stockTrans = stockBorrowingTransRepo.getStockBorrowingTransByTransCodeAndDeletedAtIsNull(transCode)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_BORROWING_TRANS_IS_NOT_EXISTED));
            //   this.reportStockBorrowingTransImport(poReportDTO, stockTrans);
        } else {
            throw new ValidateException(ResponseMessage.UNKNOWN);
        }

        return new Response<ReportProductTransDTO>().withData(reportDTO);
    }


    public void reportPoTransExport(ReportProductTransDTO reportDTO, PoTrans poTrans) {
        ReportProductTransDetailDTO info = new ReportProductTransDetailDTO();
        info.setType("Trả hàng");
        this.poReportDTOMapping(info, poTrans);
        reportDTO.setInfo(info);
        List<PoTransDetail> poTransDetails = poTransDetailRepo.getPoTransDetailAndDeleteAtIsNull(poTrans.getId());
        List<ReportProductCatDTO> reportProductCatDTOS = this.groupProductsPoTrans(poTransDetails,  reportDTO);
        reportDTO.setSaleProducts(reportProductCatDTOS);
    }

    public void reportPoTransImport(ReportProductTransDTO reportDTO, PoTrans poTrans) {
        ReportProductTransDetailDTO info = new ReportProductTransDetailDTO();
        info.setType("Nhập hàng");
        this.poReportDTOMapping(info, poTrans);
        reportDTO.setInfo(info);

        List<PoTransDetail> allPoTransDetails = poTransDetailRepo.getPoTransDetailAndDeleteAtIsNull(poTrans.getId());
        List<PoTransDetail> poTransProducts = new ArrayList<>();
        List<PoTransDetail> poTransProductsPromotions = new ArrayList<>();
        for (PoTransDetail detail: allPoTransDetails) {
            if(detail.getPrice() != null && detail.getPrice() > 0) {
                poTransProducts.add(detail);
            }else{
                poTransProductsPromotions.add(detail);
            }
        }
        List<ReportProductCatDTO> products = this.groupProductsPoTrans(poTransProducts, reportDTO);
        List<ReportProductCatDTO> promotionProducts = this.groupProductsPoTrans(poTransProductsPromotions,  reportDTO);
        reportDTO.setSaleProducts(products);
        reportDTO.setPromotionProducts(promotionProducts);
    }

    public void reportStockAdjustmentTransExport(ReportProductTransDTO reportDTO, StockAdjustmentTrans stockAdjustmentTrans) {
        ReportProductTransDetailDTO info = new ReportProductTransDetailDTO();
        info.setType("Xuất điều chỉnh tồn kho");
        this.poReportDTOMapping(info, stockAdjustmentTrans);
        List<StockAdjustmentTransDetail> stockAdjustmentTransDetails
                = stockAdjustmentTransDetailRepo.getStockAdjustmentTransDetail(stockAdjustmentTrans.getId());
        List<ReportProductCatDTO> reportProductCatDTOS = this.groupProductsStockAdjustmentTrans(stockAdjustmentTransDetails, reportDTO);
        reportDTO.setSaleProducts(reportProductCatDTOS);
    }


    public List<ReportProductCatDTO> groupProductsStockAdjustmentTrans(List<StockAdjustmentTransDetail> stockAdjustmentTransDetails, ReportProductTransDTO reportDTO) {
        List<ReportProductCatDTO> poProductReportDTOS = new ArrayList<>();

        List<Product> products = stockAdjustmentTransDetails.stream().map(trans -> this.findProduct(trans.getProductId())).collect(Collectors.toList());

        List<Long> catIds = products.stream().map(p -> p.getCatId()).collect(Collectors.toList());
        Set<Long> targetSet = new HashSet<>(catIds);

        Map<String, List<Product>> groupProducts = this.groupProducts(targetSet, products);
        for (Map.Entry<String, List<Product>> entry : groupProducts.entrySet()){
//            float totalPrice = 0;
//            float totalPriceNotVat = 0;
            List<Product> productList = entry.getValue();
            ReportProductCatDTO productCatDTO = new ReportProductCatDTO(entry.getKey());

            for(StockAdjustmentTransDetail transDetail: stockAdjustmentTransDetails) {
                float price = transDetail.getPrice()!=null?transDetail.getPrice():0;
                float priceNotVat = transDetail.getPriceNotVat()!=null?transDetail.getPriceNotVat():0;
                for (Product product: productList) {
                    if(transDetail.getProductId().equals(product.getId())) {
                        ReportProductDTO reportProductDTO = new ReportProductDTO(product.getProductCode(), product.getProductName());
                            reportProductDTO.setQuantity(transDetail.getQuantity());
                            reportProductDTO.setPrice(price);
                            reportProductDTO.setTotalPrice(reportProductDTO.getPrice()*reportProductDTO.getQuantity());
                            reportProductDTO.setPriceNotVat(priceNotVat);
                            reportProductDTO.setTotalPriceNotVat(reportProductDTO.getPriceNotVat()*reportProductDTO.getQuantity());

                        productCatDTO.addTotalQuantity(reportProductDTO.getQuantity());
                        productCatDTO.addTotalTotalPrice(reportProductDTO.getTotalPrice());
                        productCatDTO.addTotalPriceNotVar(reportProductDTO.getTotalPriceNotVat());
                        productCatDTO.addProduct(reportProductDTO);

                    }
                }
            }
            poProductReportDTOS.add(productCatDTO);
            reportDTO.getInfo().addTotalPriceNotVat(productCatDTO.getTotalPriceNotVat());

//            sumTotalPriceNotVat += totalPriceNotVat;
//            poProductReportDTO.setType(entry.getKey());
//            poProductReportDTO.setTotalPrice(totalPrice);
//            poProductReportDTO.setTotalPriceNotVar(totalPriceNotVat);
//            poProductReportDTOS.add(poProductReportDTO);
        }
        // for invoice import calculate VAT
//        if(poReportDTO != null) {
//            poReportDTO.setTotalPriceVar(poReportDTO.getSumPrice() - sumTotalPriceNotVat);
//            poReportDTO.setTotalPriceNotVar(sumTotalPriceNotVat);
//        }

        return poProductReportDTOS;
    }




    public List<ReportProductCatDTO> groupProductsPoTrans(List<PoTransDetail> poTransDetails, ReportProductTransDTO reportDTO) {
        List<ReportProductCatDTO> poProductReportDTOS = new ArrayList<>();
        List<Product> products = poTransDetails.stream().map(
                transDetail -> this.findProduct(transDetail.getProductId())).collect(Collectors.toList());

        List<Long> catIds = products.stream().map(p -> p.getCatId()).collect(Collectors.toList());
        Set<Long> targetSet = new HashSet<>(catIds);

        Map<String, List<Product>> groupProducts = this.groupProducts(targetSet, products);

        for (Map.Entry<String, List<Product>> entry : groupProducts.entrySet()){
            List<Product> productList = entry.getValue();
            ReportProductCatDTO productCatDTO = new ReportProductCatDTO(entry.getKey());

            for(PoTransDetail transDetail: poTransDetails) {
                float price = transDetail.getPrice()!=null?transDetail.getPrice():0;
                float priceNotVat = transDetail.getPriceNotVat()!=null?transDetail.getPriceNotVat():0;
                for (Product product: productList) {
                    if(transDetail.getProductId().equals(product.getId())) {
                        ReportProductDTO reportProductDTO = new ReportProductDTO(product.getProductCode(), product.getProductName());
                            reportProductDTO.setQuantity(transDetail.getQuantity());
                            reportProductDTO.setPrice(price);
                            reportProductDTO.setTotalPrice(transDetail.getAmount());
                            reportProductDTO.setPriceNotVat(priceNotVat);
                            reportProductDTO.setTotalPriceNotVat(transDetail.getAmountNotVat());

                        productCatDTO.addTotalQuantity(reportProductDTO.getQuantity());
                        productCatDTO.addTotalTotalPrice(reportProductDTO.getTotalPrice());
                        productCatDTO.addTotalPriceNotVar(reportProductDTO.getTotalPriceNotVat());
                        productCatDTO.addProduct(reportProductDTO);
                    }
                }
            }
            poProductReportDTOS.add(productCatDTO);
            reportDTO.getInfo().addTotalPriceNotVat(productCatDTO.getTotalPriceNotVat());
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

    public ReportProductTransDetailDTO poReportDTOMapping(ReportProductTransDetailDTO reportDTO, PoTrans poTrans) {

        reportDTO.setTransCode(poTrans.getTransCode());
        reportDTO.setPoNumber(poTrans.getPoNumber());
        reportDTO.setInvoiceNumber(poTrans.getRedInvoiceNo());
        reportDTO.setTransDate(poTrans.getTransDate());
        reportDTO.setInternalNumber(poTrans.getInternalNumber());
        reportDTO.setInvoiceDate(new Date());
        reportDTO.setTotalQuantity(poTrans.getTotalQuantity());
        reportDTO.setTotalPrice(poTrans.getTotalAmount());
        reportDTO.setNote(poTrans.getNote());

        return reportDTO;
    }

    public ReportProductTransDetailDTO poReportDTOMapping(ReportProductTransDetailDTO reportDTO, StockAdjustmentTrans stockAdjustmentTrans) {
        reportDTO.setTransCode(stockAdjustmentTrans.getTransCode());
       // reportDTO.setPoNumber("");
        reportDTO.setInvoiceNumber(stockAdjustmentTrans.getRedInvoiceNo());
        reportDTO.setTransDate(stockAdjustmentTrans.getTransDate());
        reportDTO.setInternalNumber(stockAdjustmentTrans.getInternalNumber());
        reportDTO.setInvoiceDate(new Date());
        reportDTO.setTotalQuantity(stockAdjustmentTrans.getTotalQuantity());
        reportDTO.setTotalPrice(stockAdjustmentTrans.getTotalAmount());
        reportDTO.setNote(stockAdjustmentTrans.getNote());

        return reportDTO;
    }

}