package vn.viettel.report.service.dto;


public interface SaleOrderDetailRs {
    Long getId();
    String getOrderNumber();
    Long getFromSaleOrderNumberId();
    Long getCustomerId();
    Integer getQuantity();
    Float getPrice();
    Float getAmount();
    Float getTotal();

}
