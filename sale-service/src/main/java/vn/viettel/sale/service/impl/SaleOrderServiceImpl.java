package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.promotion.PromotionProgram;
import vn.viettel.core.db.entity.promotion.PromotionProgramDiscount;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.db.entity.sale.SaleOrderDetail;
import vn.viettel.core.db.entity.voucher.Voucher;
import vn.viettel.core.messaging.Response;
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
    public Response<Page<SaleOrderDTO>> getAllSaleOrder(Pageable pageable) {
        String customerName, customerCode, companyName, companyAddress, taxCode;
        Response<Page<SaleOrderDTO>> response = new Response<>();
        List<SaleOrderDTO> saleOrdersList = new ArrayList<>();
        List<SaleOrder> saleOrders = saleOrderRepository.getListSaleOrder();
        CustomerDTO customer = new CustomerDTO();
        for(SaleOrder so: saleOrders) {
            try {
                customer = customerClient.getCustomerById(so.getCustomerId()).getData();
            }catch (Exception e) {
                response.setFailure(ResponseMessage.CUSTOMER_NOT_EXIST);
                return response;
            }
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
            saleOrdersList.add(saleOrder);
        }
        Page<SaleOrderDTO> saleOrderResponse = new PageImpl<>(saleOrdersList);
        response.setData(saleOrderResponse);
        return response;
    }

    public Response<SaleOrderDetailDTO> getSaleOrderDetail(GetOrderDetailRequest request) {
        Response<SaleOrderDetailDTO> response = new Response<>();
        SaleOrderDetailDTO orderDetail = new SaleOrderDetailDTO();
        try {
            orderDetail.setOrderDetail(getDetail(request.getSaleOrderId()).getData());
        } catch (Exception e) {
            response.setFailure(ResponseMessage.SALE_ORDER_DETAIL_DOES_NOT_EXISTS);
            return response;
        }
        SaleOrder saleOrder = saleOrderRepository.findById(request.getSaleOrderId()).get();

        orderDetail.setOrderNumber(request.getOrderNumber());//ma hoa don

        CustomerDTO customer;
        try {
            customer = customerClient.getCustomerById(saleOrder.getCustomerId()).getData();
        }catch (Exception e) {
            response.setFailure(ResponseMessage.CUSTOMER_NOT_EXIST);
            return response;
        }
        orderDetail.setCustomerName(customer.getLastName() +" "+ customer.getFirstName());

        User user = new User();
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

        orderDetail.setDiscount(getDiscount(request.getSaleOrderId(), request.getOrderNumber()));

        try {
            orderDetail.setPromotion(getPromotion(request.getSaleOrderId()));
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
            float totalPrice = saleOrderDetail.getQuantity() * saleOrderDetail.getPrice();
            orderDetailDTO.setTotalPrice(totalPrice);
            float discount = saleOrderDetail.getAutoPromotion() + saleOrderDetail.getZmPromotion();
            orderDetailDTO.setDiscount(discount);
            orderDetailDTO.setTotalPrice(totalPrice - discount);
            orderDetailDTO.setOrderDate(saleOrderDetail.getOrderDate());// ngay thanh toan
            saleOrderDetailList.add(orderDetailDTO);
        }
        Response<List<OrderDetailDTO>> response = new Response<>();
        response.setData(saleOrderDetailList);
        return response;
    }

    @Override
    public Response<SaleOrder> getLastSaleOrderByCustomerId(Long id) {
        SaleOrder saleOrder = saleOrderRepository.getSaleOrderByCustomerIdAndDeletedAtIsNull(id);
        return new Response<SaleOrder>().withData(saleOrder);
    }

    public List<DiscountDTO> getDiscount(long saleOrderId, String orderNumber) {
        List<DiscountDTO> discountDTOList = new ArrayList<>();
        DiscountDTO discountDTO = new DiscountDTO();
        List<Voucher> vouchers = promotionClient.getVoucherBySaleOrderId(saleOrderId).getData();
        if(vouchers.size() > 0) {
            List<VoucherDTO> voucherDTOList = new ArrayList<>();
            for(Voucher voucher: vouchers) {
                VoucherDTO voucherDTO = new VoucherDTO();
                voucherDTO.setVoucherCode(voucher.getVoucherCode());
                voucherDTO.setVoucherName(voucher.getVoucherName());
                voucherDTO.setVoucherPrice(voucher.getPrice());
                voucherDTOList.add(voucherDTO);
            }
            discountDTO.setVouchers(voucherDTOList);
        } else return null;

        List<PromotionProgramDiscount> promotionProgramDiscounts =
                promotionClient.listPromotionProgramDiscountByOrderNumber(orderNumber).getData();
        if(promotionProgramDiscounts.size() > 0) {
            List<PromotionDiscountDTO> promotionDiscountDTOList = new ArrayList<>();
            for (PromotionProgramDiscount promotionProgramDiscount:promotionProgramDiscounts) {
                PromotionDiscountDTO promotionDiscountDTO = new PromotionDiscountDTO();
                promotionDiscountDTO.setDiscount(promotionProgramDiscount.getDiscountAmount());
                promotionDiscountDTO.setDiscountPercent(promotionProgramDiscount.getDiscountPercent());
                promotionDiscountDTO.setPromotionType(promotionProgramDiscount.getType());

                PromotionProgram promotionProgram =
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
        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.getSaleOrderDetailPromotion(saleOrderId);
        for(SaleOrderDetail saleOrderDetail:saleOrderDetails) {
                Product product = productRepository.findById(saleOrderDetail.getProductId()).get();
                PromotionDTO promotionDTO = new PromotionDTO();
                promotionDTO.setProductNumber(product.getProductCode());
                promotionDTO.setProductName(product.getProductName());
                promotionDTO.setQuantity(saleOrderDetail.getQuantity());
                promotionDTO.setPromotionProgramName(saleOrderDetail.getPromotionName());
                promotionDTOList.add(promotionDTO);
            }
        return promotionDTOList;
    }
}
