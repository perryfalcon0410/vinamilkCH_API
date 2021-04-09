package service.dto;


import java.util.Date;

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
