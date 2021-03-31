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
import vn.viettel.core.db.entity.promotion.PromotionSaleProduct;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.db.entity.sale.SaleOrderDetail;
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
        List<SaleOrder> saleOrders = saleOrderRepository.findAll();
        CustomerDTO customerDTO = new CustomerDTO();
        for(SaleOrder so: saleOrders) {
            try {
                customerDTO = customerClient.getCustomerDTO(so.getCustomerId()).getData();
            }catch (Exception e) {
                response.setFailure(ResponseMessage.CUSTOMER_NOT_EXIST);
                return response;
            }
            customerName = customerDTO.getLastName() +" "+ customerDTO.getFirstName();
            customerCode = customerDTO.getCustomerCode();
            taxCode = customerDTO.getTaxCode();
            companyName = customerDTO.getWorkingOffice();
            companyAddress = customerDTO.getOfficeAddress();

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

    public Response<List<SaleOrder>> getSaleOrders(){
        List<SaleOrder> saleOrders = saleOrderRepository.findAll();

        Response<List<SaleOrder>> response = new Response<>();
        response.setData(saleOrders);
        return response;
    }

    public Response<List<PromotionProgramDiscount>> getListPromotion(String orderNumber){
        List<PromotionProgramDiscount> promotionProgramDiscounts = promotionClient.listPromotionProgramDiscountByOrderNumber(orderNumber).getData();
        Response<List<PromotionProgramDiscount>> response = new Response<>();
        response.setData(promotionProgramDiscounts);
        return response;
    }

    @Override
    public Response<CustomerDTO> getCustomerDTO(Long id) {
        Response<CustomerDTO> response = customerClient.getCustomerDTO(id);
        response.setData(response.getData());
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

        CustomerDTO customerDTO = new CustomerDTO();
        try {
            customerDTO = customerClient.getCustomerDTO(saleOrder.getCustomerId()).getData();
        }catch (Exception e) {
            response.setFailure(ResponseMessage.CUSTOMER_NOT_EXIST);
            return response;
        }
        orderDetail.setCustomerName(customerDTO.getLastName() +" "+ customerDTO.getFirstName());

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
        orderDetail.setTotalPaid(saleOrder.getTotalPaid());//?
        orderDetail.setBalance(saleOrder.getBalance());

        orderDetail.setDiscount(getDiscount(request.getOrderNumber()));

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
        float discount, totalPrice;
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
            orderDetailDTO.setPrice(saleOrderDetail.getPrice());
            totalPrice = saleOrderDetail.getQuantity() * saleOrderDetail.getPrice();
            orderDetailDTO.setTotalPrice(totalPrice);

            discount = saleOrderDetail.getAutoPromotion() + saleOrderDetail.getZmPromotion();
            orderDetailDTO.setDiscount(discount);

            orderDetailDTO.setTotalPrice(totalPrice - discount);
            orderDetailDTO.setOrderDate(saleOrderDetail.getOrderDate());// ngay thanh toan
            saleOrderDetailList.add(orderDetailDTO);
        }
        Response<List<OrderDetailDTO>> response = new Response<>();
        response.setData(saleOrderDetailList);
        return response;
    }

    public List<DiscountDTO> getDiscount(String orderNumber) {
        List<PromotionProgramDiscount> promotionProgramDiscounts = promotionClient.listPromotionProgramDiscountByOrderNumber(orderNumber).getData();
        List<DiscountDTO> discountDTOList = new ArrayList<>();
        for (PromotionProgramDiscount promotionProgramDiscount:promotionProgramDiscounts) {
            PromotionProgram promotionProgram = promotionClient.getById(promotionProgramDiscount.getPromotionProgramId()).getData();
            DiscountDTO discountDTO = new DiscountDTO();
            discountDTO.setPromotionName(promotionProgram.getPromotionProgramName());
            discountDTO.setPromotionType(promotionProgramDiscount.getType());
            discountDTO.setDiscount(promotionProgramDiscount.getDiscountAmount());
            discountDTO.setDiscountPercent(promotionProgramDiscount.getDiscountPercent());
            discountDTOList.add(discountDTO);
        }
        return discountDTOList;
    }

    public List<PromotionDTO> getPromotion(long saleOrderId) {
        List<PromotionDTO> promotionDTOList = new ArrayList<>();

        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.getBySaleOrderId(saleOrderId);
        for(SaleOrderDetail saleOrderDetail:saleOrderDetails) {
            List<PromotionSaleProduct> promotionSaleProducts =
                    promotionClient.getPromotionSaleProductsByProductId(saleOrderDetail.getProductId()).getData();
            for(PromotionSaleProduct promotionSaleProduct:promotionSaleProducts) {
                Product product = productRepository.findById(saleOrderDetail.getProductId()).get();
                PromotionProgram promotionProgram =
                        promotionClient.getById(promotionSaleProduct.getPromotionProgramId()).getData();
                PromotionDTO promotionDTO = new PromotionDTO();
                promotionDTO.setProductNumber(product.getProductCode());
                promotionDTO.setProductName(product.getProductName());
                promotionDTO.setQuantity(promotionSaleProduct.getQuantity());
                promotionDTO.setPromotionProgramName(promotionProgram.getPromotionProgramName());
                promotionDTOList.add(promotionDTO);
            }
        }
        return promotionDTOList;
    }
}
