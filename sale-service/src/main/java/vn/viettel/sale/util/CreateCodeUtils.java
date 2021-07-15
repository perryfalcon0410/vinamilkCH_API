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
