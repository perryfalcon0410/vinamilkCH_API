package vn.viettel.sale.service.dto;

import java.time.LocalDateTime;

public interface ReceiptImportDTO{

    Long getId();

    String getTransCode();

    String getRedInvoiceNo();

    String getInternalNumber();

    Integer getTotalQuantity();

    Float getTotalAmount();

    LocalDateTime getTransDate();

    String getNote();

    Long getPoId();

    Integer getReceiptType();
}
