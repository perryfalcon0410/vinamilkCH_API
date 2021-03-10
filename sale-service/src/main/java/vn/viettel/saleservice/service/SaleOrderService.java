package vn.viettel.saleservice.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.dto.SaleOrderDTO;
import vn.viettel.saleservice.service.dto.SaleOrderDetailDTO;

import java.util.List;
public interface SaleOrderService {
    public Response<List<SaleOrderDTO>> getAllSaleOrder();
    public Response<List<SaleOrderDetailDTO>> getAllSaleOrderDetail();
}
