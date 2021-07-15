package vn.viettel.sale.util;

import org.springframework.beans.factory.annotation.Autowired;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.feign.ShopClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;

public class CreateCodeUtils {
    @Autowired
    static PoTransRepository poTransRepository;
    @Autowired
    static ShopClient shopClient;
    @Autowired
    static StockBorrowingTransRepository stockBorrowingTransRepository;
    @Autowired
    static StockAdjustmentTransRepository stockAdjustmentTransRepository;
    final static SimpleDateFormat format = new SimpleDateFormat("yy"); // Just the year, with 2 digits
    static LocalDate currentDate = LocalDate.now();
    static String yy = format.format(Calendar.getInstance().getTime());

    static Integer mm = currentDate.getMonthValue();
    static Integer dd = currentDate.getDayOfMonth();

   /* public static String createPoTransCode(Long idShop) {
        int reciNum = poTransRepository.getQuantityPoTrans();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("IMP.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(formatReceINumber(reciNum));
        return reciCode.toString();
    }*/
    public static String createBorrowingTransCode(Long idShop) {
        int reciNum = stockBorrowingTransRepository.getQuantityStockBorrowingTrans();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EDC.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(formatReceINumber(reciNum));
        return reciCode.toString();
    }
    public static String createRedInvoiceCodeAdjust(Long idShop) {
        int reciNum = stockAdjustmentTransRepository.getQuantityAdjustmentTransVer2();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("SAL.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(yy);
        reciCode.append(mm.toString());
        reciCode.append(dd.toString());
        reciCode.append(formatReceINumber(reciNum));
        return reciCode.toString();
    }
    public static String createInternalCodeAdjust(Long idShop) {
        int reciNum = stockAdjustmentTransRepository.getQuantityStockAdjustmentTrans();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EDC.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(formatReceINumber(reciNum));
        return reciCode.toString();
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static String createPoTransExportCode(Long idShop) {
        int reciNum = poTransRepository.getQuantityPoTransExport();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXSP.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(formatReceINumber(reciNum));
        return reciCode.toString();
    }
    public static String createStockAdjustmentExportCode(Long idShop) {
        int reciNum = stockAdjustmentTransRepository.getQuantityStockAdjustTransExport();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXST.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(formatReceINumber(reciNum));
        return reciCode.toString();
    }
    public static String createStockAdjustmentExportRedInvoice(Long idShop) {
        int reciNum = stockAdjustmentTransRepository.getQuantityStockAdjustTransExport();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("SAL.");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append(yy);
        reciCode.append(mm.toString());
        reciCode.append(dd.toString());
        reciCode.append(formatReceINumber(reciNum));
        return reciCode.toString();
    }
    public static String createStockBorrowTransCode(Long idShop) {
        int reciNum = stockBorrowingTransRepository.getQuantityStockBorrowingTransExport();
        String reciCode = "EXSB." +
                shopClient.getByIdV1(idShop).getData().getShopCode() +
                "." +
                yy +
                "." +
                formatReceINumber(reciNum);
        return reciCode;
    }
    public static String createStockBorrowTransExportRedInvoice(Long idShop) {
        int reciNum = stockAdjustmentTransRepository.getQuantityStockAdjustTransExport();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EXP_");
        reciCode.append(shopClient.getByIdV1(idShop).getData().getShopCode());
        reciCode.append("_");
        reciCode.append(yy);
        reciCode.append(mm.toString());
        reciCode.append(dd.toString());
        reciCode.append("_");
        reciCode.append(formatReceINumberVer2(reciNum));
        return reciCode.toString();
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static String formatReceINumber(int number) {
        StringBuilder recei_num = new StringBuilder();
        int num = number + 1;

        if (num < 10) {
            recei_num.append("0000");
        }
        if (num < 100 && num >= 10) {
            recei_num.append("000");
        }
        if (num < 1000 && num >= 100) {
            recei_num.append("00");
        }
        if (num < 10000 && num >= 1000) {
            recei_num.append("0");
        }
        recei_num.append(num);

        return recei_num.toString();
    }
    public static String formatReceINumberVer2(int number) {
        StringBuilder recei_num = new StringBuilder();
        int num = number + 1;

        if (num < 10) {
            recei_num.append("00");
        }
        if (num < 100 && num >= 10) {
            recei_num.append("0");
        }
        recei_num.append(num);

        return recei_num.toString();
    }
}
