package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ReportProductTransService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.ShopClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportProductTransServiceImpl extends BaseServiceImpl<PoTrans, PoTransRepository> implements ReportProductTransService {
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

    @Override
    public Response<ReportProductTransDTO> getInvoice(Long shopId, String transCode) {
        ReportProductTransDTO reportDTO = new ReportProductTransDTO();

        ShopDTO shop = shopClient.getById(shopId).getData();
        if (shop == null)
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        reportDTO.setShop(shop);

        if (transCode.startsWith("EXSP")) {
            PoTrans poTrans = poTransRepo.getByTransCodeAndStatusAndDeletedAtIsNull(transCode, 1)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED));
            this.reportPoTransExport(reportDTO, poTrans);
        } else if (transCode.startsWith("EXST")) {
            StockAdjustmentTrans stockTrans = stockAdjustmentTransRepo.getByTransCodeAndStatusAndDeletedAtIsNull(transCode, 1)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED));
              this.reportStockAdjustmentTransExport(reportDTO, stockTrans);
        } else if (transCode.startsWith("EXSB")) {
            StockBorrowingTrans stockTrans = stockBorrowingTransRepo.getByTransCodeAndStatusAndDeletedAtIsNull(transCode, 1)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_BORROWING_TRANS_IS_NOT_EXISTED));
              this.reportStockBorrowingTransExport(reportDTO, stockTrans);
        } else if (transCode.startsWith("IMP")) {
            PoTrans poTrans = poTransRepo.getByTransCodeAndStatusAndDeletedAtIsNull(transCode, 1)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED));
             this.reportPoTransImport(reportDTO, poTrans);
        } else if (transCode.startsWith("DCT")) {
            StockAdjustmentTrans stockTrans = stockAdjustmentTransRepo.getByTransCodeAndStatusAndDeletedAtIsNull(transCode, 1)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED));
             this.reportStockAdjustmentTransImport(reportDTO, stockTrans);
        } else if (transCode.startsWith("EDC")) {
            StockBorrowingTrans stockTrans = stockBorrowingTransRepo.getByTransCodeAndStatusAndDeletedAtIsNull(transCode, 1)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_BORROWING_TRANS_IS_NOT_EXISTED));
            this.reportStockBorrowingTransImport(reportDTO, stockTrans);
        } else {
            throw new ValidateException(ResponseMessage.UNKNOWN);
        }

        return new Response<ReportProductTransDTO>().withData(reportDTO);
    }


    public void reportPoTransExport(ReportProductTransDTO reportDTO, PoTrans poTrans) {
        ReportProductTransDetailDTO info = new ReportProductTransDetailDTO();
        info.setType("Trả hàng");
        this.reportDetailDTOMapping(info, poTrans);
        reportDTO.setInfo(info);
        List<PoTransDetail> poTransDetails = poTransDetailRepo.getPoTransDetailAndDeleteAtIsNull(poTrans.getId());
        List<ReportProductCatDTO> reportProductCatDTOS = this.groupProductsPoTrans(poTransDetails,  reportDTO);
        reportDTO.setSaleProducts(reportProductCatDTOS);
    }

    public void reportPoTransImport(ReportProductTransDTO reportDTO, PoTrans poTrans) {
        ReportProductTransDetailDTO info = new ReportProductTransDetailDTO();
        info.setType("Nhập hàng");
        this.reportDetailDTOMapping(info, poTrans);
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
        List<ReportProductCatDTO> saleProducts = this.groupProductsPoTrans(poTransProducts, reportDTO);
        List<ReportProductCatDTO> promotionProducts = this.groupProductsPoTrans(poTransProductsPromotions,  reportDTO);
        reportDTO.setSaleProducts(saleProducts);
        reportDTO.setPromotionProducts(promotionProducts);
    }

    public void reportStockAdjustmentTransExport(ReportProductTransDTO reportDTO, StockAdjustmentTrans stockAdjustmentTrans) {
        ReportProductTransDetailDTO info = new ReportProductTransDetailDTO();
        info.setType("Xuất điều chỉnh tồn kho");
        this.reportDetailDTOMapping(info, stockAdjustmentTrans);
        reportDTO.setInfo(info);
        List<StockAdjustmentTransDetail> stockAdjustmentTransDetails
                = stockAdjustmentTransDetailRepo.getStockAdjustmentTransDetail(stockAdjustmentTrans.getId());
        List<ReportProductCatDTO> reportProductCatDTOS = this.groupProductsStockAdjustmentTrans(stockAdjustmentTransDetails, reportDTO);
        reportDTO.setSaleProducts(reportProductCatDTOS);
    }

    public void reportStockAdjustmentTransImport(ReportProductTransDTO reportDTO, StockAdjustmentTrans stockAdjustmentTrans) {
        ReportProductTransDetailDTO info = new ReportProductTransDetailDTO();
        info.setType("Nhập điều chỉnh tồn kho");
        this.reportDetailDTOMapping(info, stockAdjustmentTrans);
        reportDTO.setInfo(info);
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
        List<ReportProductCatDTO> saleProducts = this.groupProductsStockAdjustmentTrans(stockTransProducts, reportDTO);
        List<ReportProductCatDTO> promotionProducts = this.groupProductsStockAdjustmentTrans(stockTransProductsPromotion, reportDTO);
        reportDTO.setSaleProducts(saleProducts);
        reportDTO.setPromotionProducts(promotionProducts);
    }

    public void reportStockBorrowingTransExport(ReportProductTransDTO reportDTO, StockBorrowingTrans stockBorrowingTrans) {
        ReportProductTransDetailDTO info = new ReportProductTransDetailDTO();
        info.setType("Xuất vay mượn");
        this.reportDetailDTOMapping(info, stockBorrowingTrans);
        reportDTO.setInfo(info);
        List<StockBorrowingTransDetail> borrowingDetails = stockBorrowingTransDetailRepo.getStockBorrowingTransDetail(stockBorrowingTrans.getId());
        List<ReportProductCatDTO> reportProductCatDTOS = this.groupProductsStockBorrowingTrans(borrowingDetails, reportDTO);
        reportDTO.setSaleProducts(reportProductCatDTOS);
    }

    public void reportStockBorrowingTransImport(ReportProductTransDTO reportDTO, StockBorrowingTrans stockBorrowingTrans) {
        ReportProductTransDetailDTO info = new ReportProductTransDetailDTO();
        info.setType("Nhập vay mượn");
        this.reportDetailDTOMapping(info, stockBorrowingTrans);
        reportDTO.setInfo(info);
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

        List<ReportProductCatDTO> saleProducts = this.groupProductsStockBorrowingTrans(stockTransProducts, reportDTO);
        List<ReportProductCatDTO> promotionProducts = this.groupProductsStockBorrowingTrans(stockTransProductsPromotion, reportDTO);
        reportDTO.setSaleProducts(saleProducts);
        reportDTO.setPromotionProducts(promotionProducts);
    }

    public List<ReportProductCatDTO> groupProductsPoTrans(List<PoTransDetail> poTransDetails, ReportProductTransDTO reportDTO) {
        List<ReportProductCatDTO> reportProductCatDTOS = new ArrayList<>();
        List<Product> products = poTransDetails.stream().map(
                transDetail -> this.findProduct(transDetail.getProductId())).collect(Collectors.toList());

        List<Long> catIds = products.stream().map(Product::getCatId).collect(Collectors.toList());
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
                        reportProductDTO.setUnit(product.getUom1());
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
            reportProductCatDTOS.add(productCatDTO);
            reportDTO.getInfo().addTotalPriceNotVat(productCatDTO.getTotalPriceNotVat());
        }

        return reportProductCatDTOS;
    }

    public List<ReportProductCatDTO> groupProductsStockAdjustmentTrans(List<StockAdjustmentTransDetail> stockAdjustmentTransDetails, ReportProductTransDTO reportDTO) {
        List<ReportProductCatDTO> reportProductCatDTOS = new ArrayList<>();

        List<Product> products = stockAdjustmentTransDetails.stream().map(trans -> this.findProduct(trans.getProductId())).collect(Collectors.toList());

        List<Long> catIds = products.stream().map(Product::getCatId).collect(Collectors.toList());
        Set<Long> targetSet = new HashSet<>(catIds);

        Map<String, List<Product>> groupProducts = this.groupProducts(targetSet, products);
        for (Map.Entry<String, List<Product>> entry : groupProducts.entrySet()){
            List<Product> productList = entry.getValue();
            ReportProductCatDTO productCatDTO = new ReportProductCatDTO(entry.getKey());

            for(StockAdjustmentTransDetail transDetail: stockAdjustmentTransDetails) {
                float price = transDetail.getPrice()!=null?transDetail.getPrice():0;
                float priceNotVat = transDetail.getPriceNotVat()!=null?transDetail.getPriceNotVat():0;
                for (Product product: productList) {
                    if(transDetail.getProductId().equals(product.getId())) {
                        ReportProductDTO reportProductDTO = new ReportProductDTO(product.getProductCode(), product.getProductName());
                            reportProductDTO.setUnit(product.getUom1());
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
            reportProductCatDTOS.add(productCatDTO);
            reportDTO.getInfo().addTotalPriceNotVat(productCatDTO.getTotalPriceNotVat());
        }

        return reportProductCatDTOS;
    }

    public List<ReportProductCatDTO> groupProductsStockBorrowingTrans(List<StockBorrowingTransDetail> borrowingDetails, ReportProductTransDTO reportDTO) {
        List<ReportProductCatDTO> reportProductCatDTOS = new ArrayList<>();
        List<Product> products = borrowingDetails.stream().map(b -> this.findProduct(b.getProductId())).collect(Collectors.toList());

        List<Long> catIds = products.stream().map(Product::getCatId).collect(Collectors.toList());
        Set<Long> targetSet = new HashSet<>(catIds);

        Map<String, List<Product>> groupProducts = this.groupProducts(targetSet, products);

        for (Map.Entry<String, List<Product>> entry : groupProducts.entrySet()){
            List<Product> productList = entry.getValue();
            ReportProductCatDTO productCatDTO = new ReportProductCatDTO(entry.getKey());

            for(StockBorrowingTransDetail transDetail: borrowingDetails) {
                float price = transDetail.getPrice()!=null?transDetail.getPrice():0;
                float priceNotVat = transDetail.getPriceNotVat()!=null?transDetail.getPriceNotVat():0;
                for (Product product: productList) {
                    if(transDetail.getProductId().equals(product.getId())) {
                        ReportProductDTO reportProductDTO = new ReportProductDTO(product.getProductCode(), product.getProductName());
                        reportProductDTO.setUnit(product.getUom1());
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
            reportProductCatDTOS.add(productCatDTO);
            reportDTO.getInfo().addTotalPriceNotVat(productCatDTO.getTotalPriceNotVat());
        }

        return reportProductCatDTOS;
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

    public ReportProductTransDetailDTO reportDetailDTOMapping(ReportProductTransDetailDTO reportDTO, PoTrans poTrans) {

        reportDTO.setTransCode(poTrans.getTransCode());
        reportDTO.setPoNumber(poTrans.getPoNumber());
        reportDTO.setInvoiceNumber(poTrans.getRedInvoiceNo());
        reportDTO.setTransDate(poTrans.getTransDate());
        reportDTO.setInternalNumber(poTrans.getInternalNumber());
        reportDTO.setOrderDate(poTrans.getOrderDate());
        reportDTO.setTotalQuantity(poTrans.getTotalQuantity());
        reportDTO.setTotalPrice(poTrans.getTotalAmount());
        reportDTO.setNote(poTrans.getNote());

        return reportDTO;
    }

    public ReportProductTransDetailDTO reportDetailDTOMapping(ReportProductTransDetailDTO reportDTO, StockAdjustmentTrans stockAdjustmentTrans) {
        reportDTO.setTransCode(stockAdjustmentTrans.getTransCode());
        reportDTO.setInvoiceNumber(stockAdjustmentTrans.getRedInvoiceNo());
        reportDTO.setTransDate(stockAdjustmentTrans.getTransDate());
        reportDTO.setInternalNumber(stockAdjustmentTrans.getInternalNumber());
        reportDTO.setOrderDate(stockAdjustmentTrans.getOrderDate());
        reportDTO.setTotalQuantity(stockAdjustmentTrans.getTotalQuantity());
        reportDTO.setTotalPrice(stockAdjustmentTrans.getTotalAmount());
        reportDTO.setNote(stockAdjustmentTrans.getNote());

        return reportDTO;
    }

    public ReportProductTransDetailDTO reportDetailDTOMapping(ReportProductTransDetailDTO  reportDTO, StockBorrowingTrans stockBorrowingTrans) {
        reportDTO.setTransCode(stockBorrowingTrans.getTransCode());
        reportDTO.setInvoiceNumber(stockBorrowingTrans.getRedInvoiceNo());
        reportDTO.setTransDate(stockBorrowingTrans.getTransDate());
        reportDTO.setInternalNumber(stockBorrowingTrans.getInternalNumber());
        reportDTO.setOrderDate(stockBorrowingTrans.getOrderDate());
        reportDTO.setTotalQuantity(stockBorrowingTrans.getTotalQuantity());
        reportDTO.setTotalPrice(stockBorrowingTrans.getTotalAmount());

        reportDTO.setNote(stockBorrowingTrans.getNote());

        return reportDTO;
    }

}