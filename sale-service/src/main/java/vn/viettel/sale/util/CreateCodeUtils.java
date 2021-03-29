package vn.viettel.sale.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import vn.viettel.sale.repository.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;

public class CreateCodeUtils {
    @Autowired
    static PoTransRepository poTransRepository;
    static ShopRepository shopRepository;
    static StockBorrowingTransRepository stockBorrowingTransRepository;
    static StockAdjustmentTransRepository stockAdjustmentTransRepository;
    static DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
    static LocalDate currentDate = LocalDate.now();
    static String yy = df.format(Calendar.getInstance().getTime());

    static Integer mm = currentDate.getMonthValue();
    static Integer dd = currentDate.getDayOfMonth();

    public static String createPoTransCode(Long idShop) {
        int reciNum = poTransRepository.getQuantityPoTrans();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("IMP.");
        reciCode.append(shopRepository.findById(idShop).get().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(formatReceINumber(reciNum));
        return reciCode.toString();
    }
    public static String createBorrowingTransCode(Long idShop) {
        int reciNum = stockBorrowingTransRepository.getQuantityStockBorrowingTrans();
        StringBuilder reciCode = new StringBuilder();
        reciCode.append("EDC.");
        reciCode.append(shopRepository.findById(idShop).get().getShopCode());
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
        reciCode.append(shopRepository.findById(idShop).get().getShopCode());
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
        reciCode.append(shopRepository.findById(idShop).get().getShopCode());
        reciCode.append(".");
        reciCode.append(yy);
        reciCode.append(".");
        reciCode.append(formatReceINumber(reciNum));
        return reciCode.toString();
    }
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
}
