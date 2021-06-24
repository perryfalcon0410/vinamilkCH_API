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
    public Response<ReportProductTransDTO> getInvoice(Long shopId, Long id, Integer receiptType) {
        ReportProductTransDTO reportDTO = new ReportProductTransDTO();

        ShopDTO shop = shopClient.getByIdV1(shopId).getData();
        if (shop == null)
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        reportDTO.setShop(shop);

        if(receiptType == 0) {
            PoTrans poTran = poTransRepo.findById(id)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.PO_TRANS_IS_NOT_EXISTED));
            if(poTran.getType() == 1) {
                this.reportPoTransImport(reportDTO, poTran);
            }
            if(poTran.getType() == 2) {
                this.reportPoTransExport(reportDTO, poTran);
            }
            reportDTO.getInfo().setTransType(0);
        }
        else if(receiptType == 1) {
            StockAdjustmentTrans stockTran = stockAdjustmentTransRepo.findById(id)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED));
            this.reportStockAdjustmentTransExport(reportDTO, stockTran);
            reportDTO.getInfo().setTransType(1);
        }
        else if(receiptType == 2) {
            StockBorrowingTrans stockTran = stockBorrowingTransRepo.findById(id)
                    .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_BORROWING_TRANS_IS_NOT_EXISTED));
            this.reportStockBorrowingTransExport(reportDTO, stockTran);
            reportDTO.getInfo().setTransType(2);
        }
         else {
                throw new ValidateException(ResponseMessage.UNKNOWN);
         }

        return new Response<ReportProductTransDTO>().withData(reportDTO);
    }

    public void reportPoTransExport(ReportProductTransDTO reportDTO, PoTrans poTrans) {
        ReportProductTransDetailDTO info = new ReportProductTransDetailDTO();
        this.reportDetailDTOMapping(info, poTrans);
        reportDTO.setInfo(info);
        List<PoTransDetail> poTransDetails = poTransDetailRepo.getPoTransDetail(poTrans.getId());
        List<ReportProductCatDTO> reportProductCatDTOS = this.groupProductsPoTrans(poTransDetails,  reportDTO);
        reportDTO.setSaleProducts(reportProductCatDTOS);
        reportDTO.getInfo().setTotalPriceVat(reportDTO.getInfo().getTotalPrice() - reportDTO.getInfo().getTotalPriceNotVat());
    }

    public void reportPoTransImport(ReportProductTransDTO reportDTO, PoTrans poTrans) {
        ReportProductTransDetailDTO info = new ReportProductTransDetailDTO();
        this.reportDetailDTOMapping(info, poTrans);
        reportDTO.setInfo(info);

        List<PoTransDetail> allPoTransDetails = poTransDetailRepo.getPoTransDetail(poTrans.getId());
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
        reportDTO.getInfo().setTotalPriceVat(reportDTO.getInfo().getTotalPrice() - reportDTO.getInfo().getTotalPriceNotVat());
    }

    public void reportStockAdjustmentTransExport(ReportProductTransDTO reportDTO, StockAdjustmentTrans stockAdjustmentTrans) {
        ReportProductTransDetailDTO info = new ReportProductTransDetailDTO();
        this.reportDetailDTOMapping(info, stockAdjustmentTrans);
        reportDTO.setInfo(info);
        List<StockAdjustmentTransDetail> stockAdjustmentTransDetails
                = stockAdjustmentTransDetailRepo.getStockAdjustmentTransDetail(stockAdjustmentTrans.getId());
        List<ReportProductCatDTO> reportProductCatDTOS = this.groupProductsStockAdjustmentTrans(stockAdjustmentTransDetails, reportDTO);
        reportDTO.setSaleProducts(reportProductCatDTOS);
        if(reportDTO.getInfo().getTotalPriceNotVat() > 0)
            reportDTO.getInfo().setTotalPriceVat(reportDTO.getInfo().getTotalPrice() - reportDTO.getInfo().getTotalPriceNotVat());
    }

    public void reportStockBorrowingTransExport(ReportProductTransDTO reportDTO, StockBorrowingTrans stockBorrowingTrans) {
        ReportProductTransDetailDTO info = new ReportProductTransDetailDTO();
        this.reportDetailDTOMapping(info, stockBorrowingTrans);
        reportDTO.setInfo(info);
        List<StockBorrowingTransDetail> borrowingDetails = stockBorrowingTransDetailRepo.getStockBorrowingTransDetail(stockBorrowingTrans.getId());
        List<ReportProductCatDTO> reportProductCatDTOS = this.groupProductsStockBorrowingTrans(borrowingDetails, reportDTO);
        reportDTO.setSaleProducts(reportProductCatDTOS);
        if(reportDTO.getInfo().getTotalPriceNotVat() > 0)
            reportDTO.getInfo().setTotalPriceVat(reportDTO.getInfo().getTotalPrice() - reportDTO.getInfo().getTotalPriceNotVat());
    }

    public List<ReportProductCatDTO> groupProductsPoTrans(List<PoTransDetail> poTransDetails, ReportProductTransDTO reportDTO) {
        List<ReportProductCatDTO> reportProductCatDTOS = new ArrayList<>();
        List<Product> products = poTransDetails.stream().map(
                transDetail -> this.findProduct(transDetail.getProductId())).collect(Collectors.toList());

        List<Long> catIds = products.stream().map(Product::getCatId).collect(Collectors.toList());
        Set<Long> targetSet = new HashSet<>(catIds);

        Map<String, Set<Product>> groupProducts = this.groupProducts(targetSet, products);

        for (Map.Entry<String, Set<Product>> entry : groupProducts.entrySet()){
            Set<Product> productList = entry.getValue();
            ReportProductCatDTO productCatDTO = new ReportProductCatDTO(entry.getKey());

            List<ReportProductDTO> reportProductDTOS = new ArrayList<>();
            for(PoTransDetail transDetail: poTransDetails) {
                double price = transDetail.getPrice()!=null?transDetail.getPrice():0;
                double priceNotVat = transDetail.getPriceNotVat()!=null?transDetail.getPriceNotVat():0;
                for (Product product: productList) {
                    if(transDetail.getProductId().equals(product.getId())) {
                        ReportProductDTO reportProductDTO = new ReportProductDTO(product.getProductCode(), product.getProductName());
                        reportProductDTO.setUnit(product.getUom1());
                        reportProductDTO.setQuantity(transDetail.getQuantity());
                        reportProductDTO.setPrice(price);
                        reportProductDTO.setPriceNotVat(priceNotVat);

                        reportProductDTO.setTotalPrice(transDetail.getAmount());
                        reportProductDTO.setTotalPriceNotVat(transDetail.getAmountNotVat());

                        productCatDTO.addTotalQuantity(reportProductDTO.getQuantity());
                        productCatDTO.addTotalPrice(reportProductDTO.getTotalPrice());
                        productCatDTO.addTotalPriceNotVar(reportProductDTO.getTotalPriceNotVat());

                        reportProductDTOS.add(reportProductDTO);
                    }
                }
            }

            productCatDTO.setProducts(reportProductDTOS.stream().sorted(Comparator.comparing(ReportProductDTO::getProductCode)).collect(Collectors.toList()));

            reportProductCatDTOS.add(productCatDTO);
            reportDTO.getInfo().addTotalQuantity(productCatDTO.getTotalQuantity());
            reportDTO.getInfo().addTotalPrice(productCatDTO.getTotalPrice());
            reportDTO.getInfo().addTotalPriceNotVat(productCatDTO.getTotalPriceNotVat());
        }

        return reportProductCatDTOS;
    }

    public List<ReportProductCatDTO> groupProductsStockAdjustmentTrans(List<StockAdjustmentTransDetail> stockAdjustmentTransDetails, ReportProductTransDTO reportDTO) {
        List<ReportProductCatDTO> reportProductCatDTOS = new ArrayList<>();

        List<Product> products = stockAdjustmentTransDetails.stream().map(trans -> this.findProduct(trans.getProductId())).collect(Collectors.toList());

        List<Long> catIds = products.stream().map(Product::getCatId).collect(Collectors.toList());
        Set<Long> targetSet = new HashSet<>(catIds);

        Map<String, Set<Product>> groupProducts = this.groupProducts(targetSet, products);
        for (Map.Entry<String, Set<Product>> entry : groupProducts.entrySet()){
            Set<Product> productList = entry.getValue();
            ReportProductCatDTO productCatDTO = new ReportProductCatDTO(entry.getKey());

            List<ReportProductDTO> reportProductDTOS = new ArrayList<>();
            for(StockAdjustmentTransDetail transDetail: stockAdjustmentTransDetails) {
                double price = transDetail.getPrice()!=null?transDetail.getPrice():0;
                double priceNotVat = transDetail.getPriceNotVat()!=null?transDetail.getPriceNotVat():0;
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
                        productCatDTO.addTotalPrice(reportProductDTO.getTotalPrice());
                        productCatDTO.addTotalPriceNotVar(reportProductDTO.getTotalPriceNotVat());

                        reportProductDTOS.add(reportProductDTO);
                    }
                }
            }
            productCatDTO.setProducts(reportProductDTOS.stream().sorted(Comparator.comparing(ReportProductDTO::getProductCode)).collect(Collectors.toList()));

            reportProductCatDTOS.add(productCatDTO);
            reportDTO.getInfo().addTotalQuantity(productCatDTO.getTotalQuantity());
            reportDTO.getInfo().addTotalPrice(productCatDTO.getTotalPrice());
            reportDTO.getInfo().addTotalPriceNotVat(productCatDTO.getTotalPriceNotVat());

        }

        return reportProductCatDTOS;
    }

    public List<ReportProductCatDTO> groupProductsStockBorrowingTrans(List<StockBorrowingTransDetail> borrowingDetails, ReportProductTransDTO reportDTO) {
        List<ReportProductCatDTO> reportProductCatDTOS = new ArrayList<>();
        List<Product> products = borrowingDetails.stream().map(b -> this.findProduct(b.getProductId())).collect(Collectors.toList());

        List<Long> catIds = products.stream().map(Product::getCatId).collect(Collectors.toList());
        Set<Long> targetSet = new HashSet<>(catIds);

        Map<String, Set<Product>> groupProducts = this.groupProducts(targetSet, products);

        for (Map.Entry<String, Set<Product>> entry : groupProducts.entrySet()){
            Set<Product> productList = entry.getValue();
            ReportProductCatDTO productCatDTO = new ReportProductCatDTO(entry.getKey());

            List<ReportProductDTO> reportProductDTOS = new ArrayList<>();
            for(StockBorrowingTransDetail transDetail: borrowingDetails) {
                double price = transDetail.getPrice()!=null?transDetail.getPrice():0;
                double priceNotVat = transDetail.getPriceNotVat()!=null?transDetail.getPriceNotVat():0;
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
                        productCatDTO.addTotalPrice(reportProductDTO.getTotalPrice());
                        productCatDTO.addTotalPriceNotVar(reportProductDTO.getTotalPriceNotVat());

                        reportProductDTOS.add(reportProductDTO);
                    }
                }
            }
            productCatDTO.setProducts(reportProductDTOS.stream().sorted(Comparator.comparing(ReportProductDTO::getProductCode)).collect(Collectors.toList()));

            reportProductCatDTOS.add(productCatDTO);
            reportDTO.getInfo().addTotalQuantity(productCatDTO.getTotalQuantity());
            reportDTO.getInfo().addTotalPrice(productCatDTO.getTotalPrice());
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

    public Map<String, Set<Product>> groupProducts(Set<Long> productInfoIds, List<Product> products) {
        Map<String, Set<Product>> groupProducts = new HashMap<>();
        for(Long id: productInfoIds) {
            ProductInfo productInfo = productInfoRepo.findById(id).orElse(null);
            if(productInfo == null)
                throw new ValidateException(ResponseMessage.PRODUCT_INFO_NOT_EXISTS);
            Set<Product> targetProducts = new HashSet<>();
            for(Product product: products) {
                if(product.getCatId().equals(productInfo.getId())) {
                    targetProducts.add(product);
                }
            }
            groupProducts.put(productInfo.getProductInfoName(), targetProducts);
        }

        return groupProducts;
    }

    private ReportProductTransDetailDTO reportDetailDTOMapping(ReportProductTransDetailDTO reportDTO, PoTrans poTrans) {
        reportDTO.setTransCode(poTrans.getTransCode());
        reportDTO.setPoNumber(poTrans.getPocoNumber());
        reportDTO.setInvoiceNumber(poTrans.getRedInvoiceNo());
        reportDTO.setTransDate(poTrans.getTransDate());
        reportDTO.setInternalNumber(poTrans.getInternalNumber());
        reportDTO.setOrderDate(poTrans.getOrderDate());
        reportDTO.setNote(poTrans.getNote());

        return reportDTO;
    }

    private ReportProductTransDetailDTO reportDetailDTOMapping(ReportProductTransDetailDTO reportDTO, StockAdjustmentTrans stockAdjustmentTrans) {
        reportDTO.setTransCode(stockAdjustmentTrans.getTransCode());
        reportDTO.setInvoiceNumber(stockAdjustmentTrans.getRedInvoiceNo());
        reportDTO.setTransDate(stockAdjustmentTrans.getTransDate());
        reportDTO.setInternalNumber(stockAdjustmentTrans.getInternalNumber());
        reportDTO.setOrderDate(stockAdjustmentTrans.getAdjustmentDate());
        reportDTO.setNote(stockAdjustmentTrans.getNote());
        return reportDTO;
    }

    private ReportProductTransDetailDTO reportDetailDTOMapping(ReportProductTransDetailDTO  reportDTO, StockBorrowingTrans stockBorrowingTrans) {
        reportDTO.setTransCode(stockBorrowingTrans.getTransCode());
        reportDTO.setInvoiceNumber(stockBorrowingTrans.getRedInvoiceNo());
        reportDTO.setTransDate(stockBorrowingTrans.getTransDate());
        reportDTO.setInternalNumber(stockBorrowingTrans.getInternalNumber());
        reportDTO.setOrderDate(stockBorrowingTrans.getBorrowDate());
        reportDTO.setNote(stockBorrowingTrans.getNote());

        return reportDTO;
    }


}