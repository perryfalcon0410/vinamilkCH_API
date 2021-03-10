package vn.viettel.saleservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.POConfirm;
import vn.viettel.core.db.entity.SaleOrder;
import vn.viettel.core.db.entity.SaleOrderDetail;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.repository.SaleOrderDetailRepository;
import vn.viettel.saleservice.repository.SaleOrderRepository;
import vn.viettel.saleservice.service.SaleOrderService;
import vn.viettel.saleservice.service.dto.POConfirmDTO;
import vn.viettel.saleservice.service.dto.SaleOrderDTO;
import vn.viettel.saleservice.service.dto.SaleOrderDetailDTO;

import java.util.ArrayList;
import java.util.List;

// Bat buoc phai co @Service
@Service
public class SaleOrderServiceImpl implements SaleOrderService {
    @Autowired
    SaleOrderRepository saleOrderRepository;

    @Autowired
    SaleOrderDetailRepository saleOrderDetailRepository;

    @Override
    public Response<List<SaleOrderDTO>> getAllSaleOrder() {
        List<SaleOrder> saleOrders = saleOrderRepository.findAll();
        // sysout o day, roi chay api tren postman, roi vo console de coi ketqua:))
        System.out.println("TEST: " + saleOrders.get(0).getShopCode());
        List<SaleOrderDTO> saleOrdersList = new ArrayList<>();
        for (SaleOrder so : saleOrders) {
            SaleOrderDTO saleOrder = new SaleOrderDTO();
            saleOrder.setId(so.getId());
            saleOrder.setSaleOrderId(so.getSaleOrderId());
            saleOrder.setShopId(so.getShopId());
            saleOrder.setShopCode(so.getShopCode());
            saleOrder.setStaffId(so.getStaffId());
            saleOrder.setCustomerId(so.getCustomerId());
            saleOrder.setOrderNumber(so.getOrderNumber());
            saleOrder.setOrderDate(so.getOrderDate());
            saleOrder.setTotalDetail(so.getTotalDetail());
            saleOrder.setOrderType(so.getOrderType());
            saleOrder.setAmount(so.getAmount());
            saleOrder.setDiscount(so.getDiscount());
            saleOrder.setTotal(so.getTotal());
            saleOrder.setCashierId(so.getCashierId());
            saleOrder.setDescription(so.getDescription());
            saleOrder.setNote(so.getNote());
            saleOrder.setTotalWeight(so.getTotalWeight());
            saleOrder.setTotalDetail(so.getTotalDetail());
            saleOrder.setTimePrint(so.getTimePrint());
            saleOrder.setStockDate(so.getStockDate());

            saleOrdersList.add(saleOrder);
        }
        Response<List<SaleOrderDTO>> response = new Response<>();
        response.setData(saleOrdersList);
        return response;
    }

    public Response<List<SaleOrderDetailDTO>> getAllSaleOrderDetail() {
        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findAll();
        // sysout o day, roi chay api tren postman, roi vo console de coi ketqua:))
        System.out.println("TEST: " + saleOrderDetails.get(0).getAmount());
        List<SaleOrderDetailDTO> saleOrdersDetailList = new ArrayList<>();
        for (SaleOrderDetail sod : saleOrderDetails) {
            SaleOrderDetailDTO saleOrderDetail = new SaleOrderDetailDTO();
            saleOrderDetail.setId(sod.getId());
            saleOrderDetail.setSaleOrderDetailId(sod.getSaleOrderDetailId());
            saleOrderDetail.setSaleOrderId(sod.getSaleOrderId());
            saleOrderDetail.setOrderDate(sod.getOrderDate());
            saleOrderDetail.setShopId(sod.getShopId());
            saleOrderDetail.setStaffId(sod.getStaffId());
            saleOrderDetail.setProductId(sod.getProductId());
            saleOrderDetail.setConvfact(sod.getConvfact());
            saleOrderDetail.setCatId(sod.getCatId());
            saleOrderDetail.setQuantity(sod.getQuantity());
            saleOrderDetail.setQuantityRetail(sod.getQuantityRetail());
            saleOrderDetail.setQuantityPackage(sod.getQuantityPackage());
            saleOrderDetail.setIsFreeItem(sod.getIsFreeItem());
            saleOrderDetail.setDiscountPercent(sod.getDiscountPercent());
            saleOrderDetail.setDiscountAmount(sod.getDiscountAmount());
            saleOrderDetail.setAmount(sod.getAmount());
            saleOrderDetail.setPriceId(sod.getPriceId());
            saleOrderDetail.setPrice(sod.getPrice());
            saleOrderDetail.setPriceNotVat(sod.getPriceNotVat());
            saleOrderDetail.setVat(sod.getVat());
            saleOrderDetail.setTotalWeight(sod.getTotalWeight());
            saleOrderDetail.setProgrameTypeCode(sod.getProgrameTypeCode());
            saleOrdersDetailList.add(saleOrderDetail);
        }
        Response<List<SaleOrderDetailDTO>> response = new Response<>();
        response.setData(saleOrdersDetailList);
        return response;
    }
}
