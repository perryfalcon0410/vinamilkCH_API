package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDiscountDTO;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.entities.SaleOrderDetail;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.repository.ProductPriceRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.SaleOrderDetailRepository;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.PromotionClient;
import vn.viettel.sale.service.feign.UserClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class SaleOrderServiceImpl implements SaleOrderService {
    @Autowired
    SaleOrderRepository saleOrderRepository;
    @Autowired
    CustomerClient customerClient;
    @Autowired
    SaleOrderDetailRepository saleOrderDetailRepository;
    @Autowired
    UserClient userClient;
    @Autowired
    ProductPriceRepository productPriceRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PromotionClient promotionClient;


    @Override
    public Response<CoverResponse<Page<SaleOrderDTO>, SaleOrderTotalResponse>> getAllSaleOrder(Pageable pageable) {
        String customerName, customerCode, companyName, companyAddress, taxCode;
        Float totalAmount = 0F, allTotal = 0F;
        List<SaleOrderDTO> saleOrdersList = new ArrayList<>();
        List<SaleOrder> saleOrders = saleOrderRepository.getListSaleOrder();
        for(SaleOrder so: saleOrders) {
            CustomerDTO customer = customerClient.getCustomerById(so.getCustomerId()).getData();
            customerName = customer.getLastName() +" "+ customer.getFirstName();
            customerCode = customer.getCustomerCode();
            taxCode = customer.getTaxCode();
            companyName = customer.getWorkingOffice();
            companyAddress = customer.getOfficeAddress();

            SaleOrderDTO saleOrder = new SaleOrderDTO();
            saleOrder.setId(so.getId()); //soId
            saleOrder.setOrderNumber(so.getOrderNumber()); //soNumber
            saleOrder.setCustomerId(so.getCustomerId()); //cusId;
            saleOrder.setCustomerNumber(customerCode);
            saleOrder.setCustomerName(customerName);
            saleOrder.setOrderDate(so.getOrderDate());

            saleOrder.setAmount(so.getAmount());
            saleOrder.setDiscount(so.getTotalPromotion());
            saleOrder.setAccumulation(so.getCustomerPurchase());
            saleOrder.setTotal(so.getTotal());

            saleOrder.setNote(so.getNote());
            saleOrder.setRedReceipt(so.getUsedRedInvoice());
            saleOrder.setComName(companyName);
            saleOrder.setTaxCode(taxCode);
            saleOrder.setAddress(companyAddress);
            saleOrder.setNoteRed(so.getRedInvoiceRemark());
            totalAmount = totalAmount + so.getAmount();
            allTotal = allTotal + so.getTotal();
            saleOrdersList.add(saleOrder);
        }
        SaleOrderTotalResponse totalResponse = new SaleOrderTotalResponse(totalAmount, allTotal);
        Page<SaleOrderDTO> saleOrderResponse = new PageImpl<>(saleOrdersList);
        CoverResponse<Page<SaleOrderDTO>, SaleOrderTotalResponse> response =
                new CoverResponse(saleOrderResponse, totalResponse);
        return new Response<CoverResponse<Page<SaleOrderDTO>, SaleOrderTotalResponse>>()
                .withData(response);
    }

    public Response<SaleOrderDetailDTO> getSaleOrderDetail(long saleOrderId, String orderNumber) {
        Response<SaleOrderDetailDTO> response = new Response<>();
        SaleOrderDetailDTO orderDetail = new SaleOrderDetailDTO();
        try {
            orderDetail.setOrderDetail(getDetail(saleOrderId).getData());
        } catch (Exception e) {
            response.setFailure(ResponseMessage.SALE_ORDER_DETAIL_DOES_NOT_EXISTS);
            return response;
        }
        SaleOrder saleOrder = saleOrderRepository.findById(saleOrderId).get();

        orderDetail.setOrderNumber(orderNumber);//ma hoa don

        CustomerDTO customer = new CustomerDTO();
        try {
            customer = customerClient.getCustomerById(saleOrder.getCustomerId()).getData();
        }catch (Exception e) {
            response.setFailure(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
            return response;
        }
        orderDetail.setCustomerName(customer.getLastName() +" "+ customer.getFirstName());
        orderDetail.setOrderDate(saleOrder.getOrderDate());
        UserDTO user = new UserDTO();
        try {
            user = userClient.getUserById(saleOrder.getSalemanId());
        } catch (Exception e) {
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
            return response;
        }
        orderDetail.setSaleMan(user.getFirstName()+ " " +user.getLastName());//nhan vien

        orderDetail.setCurrency("VND");
        orderDetail.setTotal(saleOrder.getTotal());
        orderDetail.setTotalPaid(saleOrder.getTotalPaid());
        orderDetail.setBalance(saleOrder.getBalance());

        orderDetail.setDiscount(getDiscount(saleOrderId, orderNumber));

        try {
            orderDetail.setPromotion(getPromotion(saleOrderId));
        }catch (Exception e) {
            response.setFailure(ResponseMessage.PROMOTION_DOSE_NOT_EXISTS);
            return response;
        }

        response.setData(orderDetail);
        return response;
    }

    public Response<List<OrderDetailDTO>> getDetail(long saleOrderId) {
        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.getBySaleOrderId(saleOrderId);
        List<OrderDetailDTO> saleOrderDetailList = new ArrayList<>();
        for (SaleOrderDetail saleOrderDetail: saleOrderDetails) {
            Product product = productRepository.findById(saleOrderDetail.getProductId()).get();
            OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
            orderDetailDTO.setProductId(saleOrderDetail.getProductId());
            orderDetailDTO.setProductCode(product.getProductCode());
            orderDetailDTO.setProductName(product.getProductName());
            orderDetailDTO.setUnit(product.getUom1());
            orderDetailDTO.setQuantity(saleOrderDetail.getQuantity());
            orderDetailDTO.setPricePerUnit(saleOrderDetail.getPrice());
            orderDetailDTO.setTotalPrice(saleOrderDetail.getAmount());
            if(saleOrderDetail.getAutoPromotion() == null && saleOrderDetail.getZmPromotion() == null){
                orderDetailDTO.setDiscount(0F);
            }
            else if(saleOrderDetail.getAutoPromotion() == null || saleOrderDetail.getZmPromotion() == null) {
                if(saleOrderDetail.getAutoPromotion() == null)
                    orderDetailDTO.setDiscount(saleOrderDetail.getZmPromotion());
                if(saleOrderDetail.getZmPromotion() == null)
                    orderDetailDTO.setDiscount(saleOrderDetail.getAutoPromotion());
            }else {
                float discount = saleOrderDetail.getAutoPromotion() + saleOrderDetail.getZmPromotion();
                orderDetailDTO.setDiscount(discount);
            }
            orderDetailDTO.setPayment(saleOrderDetail.getTotal());
            saleOrderDetailList.add(orderDetailDTO);
        }
        Response<List<OrderDetailDTO>> response = new Response<>();
        response.setData(saleOrderDetailList);
        return response;
    }

    public List<DiscountDTO> getDiscount(long saleOrderId, String orderNumber) {
        List<DiscountDTO> discountDTOList = new ArrayList<>();
        DiscountDTO discountDTO = new DiscountDTO();
        List<VoucherDTO> voucherDTOS = promotionClient.getVoucherBySaleOrderId(saleOrderId).getData();
        if(voucherDTOS.size() > 0) {
            discountDTO.setVouchers(voucherDTOS);
        } else return null;

        List<PromotionProgramDiscountDTO> promotionProgramDiscounts =
                promotionClient.listPromotionProgramDiscountByOrderNumber(orderNumber).getData();
        if(promotionProgramDiscounts.size() > 0) {
            List<PromotionDiscountDTO> promotionDiscountDTOList = new ArrayList<>();
            for (PromotionProgramDiscountDTO promotionProgramDiscount:promotionProgramDiscounts) {
                PromotionDiscountDTO promotionDiscountDTO = new PromotionDiscountDTO();
                promotionDiscountDTO.setDiscount(promotionProgramDiscount.getDiscountAmount());
                promotionDiscountDTO.setDiscountPercent(promotionProgramDiscount.getDiscountPercent());
                promotionDiscountDTO.setPromotionType(promotionProgramDiscount.getType());

                PromotionProgramDTO promotionProgram =
                        promotionClient.getById(promotionProgramDiscount.getId()).getData();
                promotionDiscountDTO.setPromotionName(promotionProgram.getPromotionProgramName());
                promotionDiscountDTOList.add(promotionDiscountDTO);
            }
            discountDTO.setPromotionDiscount(promotionDiscountDTOList);
        } else return null;
        discountDTOList.add(discountDTO);
        return discountDTOList;
    }

    public List<PromotionDTO> getPromotion(long saleOrderId) {
        List<PromotionDTO> promotionDTOList = new ArrayList<>();
        List<SaleOrderDetail> saleOrderPromotions = saleOrderDetailRepository.getSaleOrderDetailPromotion(saleOrderId);
        for(SaleOrderDetail promotionDetail:saleOrderPromotions) {
                Product product = productRepository.findById(promotionDetail.getProductId()).get();
                PromotionDTO promotionDTO = new PromotionDTO();
                promotionDTO.setProductNumber(product.getProductCode());
                promotionDTO.setProductName(product.getProductName());
                promotionDTO.setQuantity(promotionDetail.getQuantity());
                promotionDTO.setPromotionProgramName(promotionDetail.getPromotionName());
                promotionDTOList.add(promotionDTO);
            }
        return promotionDTOList;
    }
}
