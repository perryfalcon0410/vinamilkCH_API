package vn.viettel.sale.service.impl;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.db.entity.stock.*;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.dto.PoProductReportDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;

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
    ProductRepository productRepo;

    /// invoiceType: 0 - Trả hàng PO, 1 - Xuất điều chỉnh, 2 - xuất vay mượn
    public ByteArrayInputStream testInvoice(Long shopId, Long invoiceId, Integer invoiceType) throws FileNotFoundException, JRException {
        int totalQuantity = 0;
        int totalPrice = 0;
        Map<String,Object> parameters = new HashMap<>();
        List<PoProductReportDTO> products = new ArrayList<>();
        DecimalFormat formatter = new DecimalFormat("###,###,###");

        Shop shop = shopRepo.findById(shopId).orElse(null);
        if(shop == null)
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        parameters.put("shopName", shop.getShopName());
        parameters.put("shopAddress", shop.getAddress());
        parameters.put("phoneNumber", shop.getPhone());
        parameters.put("faxNumber", shop.getFax());

        if(invoiceType == 1) {
            StockAdjustmentTrans stockAdjustment = stockAdjustmentTransRepo.findById(invoiceId).orElse(null);
            if(stockAdjustment == null)
                throw new ValidateException(ResponseMessage.STOCK_ADJUSTMENT_TRANS_IS_NOT_EXISTED);

            parameters.put("type","Xuất điều chỉnh tồn kho");
            parameters.put("transCode", stockAdjustment.getTransCode());
            parameters.put("poNumber", ""); //poNumber is not exited
            parameters.put("invoiceNumber", stockAdjustment.getRedInvoiceNo());
            parameters.put("transDate", stockAdjustment.getTransDate());
            parameters.put("internalNumber", stockAdjustment.getInternalNumber());
            parameters.put("invoiceDate", new Date());
            parameters.put("note", stockAdjustment.getNote());

            List<StockAdjustmentTransDetail> stockdetails =
                stockAdjustmentTransDetailRepo.getStockAdjustmentTransDetailsByTransId(stockAdjustment.getId());
            for(StockAdjustmentTransDetail stockdetail: stockdetails) {
                Product product = productRepo.findByIdAndDeletedAtIsNull(stockdetail.getProductId());
                PoProductReportDTO poProduct =
                        new PoProductReportDTO(product.getProductCode(), product.getProductName(), product.getUom1())
                                .withQuantityAndPrice(stockdetail.getQuantity(), stockdetail.getPrice());
                totalQuantity += stockdetail.getQuantity();
                totalPrice += (stockdetail.getQuantity()*stockdetail.getPrice());
                products.add(poProduct);
            }

            parameters.put("totalQuantity", totalQuantity);
            parameters.put("totalPrice", formatter.format(totalPrice));

        }else if(invoiceType == 2) {
            StockBorrowingTrans borrowingTran = stockBorrowingTransRepo.findById(invoiceId).orElse(null);
            if(borrowingTran == null)
                throw new ValidateException(ResponseMessage.STOCK_BORROWING_TRANS_IS_NOT_EXISTED);

            parameters.put("type","Xuất vay mượn");
            parameters.put("transCode", borrowingTran.getTransCode());
            parameters.put("poNumber", ""); //poNumber is not exited
            parameters.put("invoiceNumber", borrowingTran.getRedInvoiceNo());
            parameters.put("transDate", borrowingTran.getTransDate());
            parameters.put("internalNumber", borrowingTran.getInternalNumber());
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
                PoProductReportDTO poProduct =
                        new PoProductReportDTO(product.getProductCode(), product.getProductName(), product.getUom1())
                                .withQuantityAndPrice(transDetail.getQuantity(), transDetail.getPrice());
                totalQuantity += transDetail.getQuantity();
                totalPrice += transDetail.getAmount();
                products.add(poProduct);
            }

            parameters.put("totalQuantity", totalQuantity);
            parameters.put("totalPrice", formatter.format(totalPrice));
        }

        File file = ResourceUtils.getFile("classpath:invoice-export.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

        JRBeanCollectionDataSource productJRB = new JRBeanCollectionDataSource(products);
        parameters.put("productDetail", productJRB);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperPrint jprint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        JasperExportManager.exportReportToPdfStream(jprint, baos);

        return new ByteArrayInputStream(baos.toByteArray());
    }

}
