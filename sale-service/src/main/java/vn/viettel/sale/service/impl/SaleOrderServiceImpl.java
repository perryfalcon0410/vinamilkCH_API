package vn.viettel.sale.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
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
import vn.viettel.sale.messaging.OrderDetailTotalResponse;
import vn.viettel.sale.messaging.SaleOrderFilter;
import vn.viettel.sale.messaging.RedInvoiceFilter;
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
import vn.viettel.sale.specification.SaleOderSpecification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SaleOrderServiceImpl extends BaseServiceImpl<SaleOrder, SaleOrderRepository> implements SaleOrderService {
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
    public Response<CoverResponse<Page<SaleOrderDTO>, SaleOrderTotalResponse>> getAllSaleOrder(SaleOrderFilter saleOrderFilter, Pageable pageable) {
        String customerName, customerCode, companyName, companyAddress, taxCode, saleManName;
        Float totalAmount = 0F, allTotal = 0F;
        List<SaleOrderDTO> saleOrdersList = new ArrayList<>();
        List<Long> customerIds = customerClient.getIdCustomerBySearchKeyWordsV1(saleOrderFilter.getSearchKeyword()).getData();
        List<SaleOrder> findAll  = new ArrayList<>();
        findAll = repository.findAll(Specification.where(SaleOderSpecification.hasNameOrPhone(customerIds))
                            .and(SaleOderSpecification.hasFromDateToDate(saleOrderFilter.getFromDate(), saleOrderFilter.getToDate()))
                            .and(SaleOderSpecification.hasOrderNumber(saleOrderFilter.getOrderNumber()))
                            .and(SaleOderSpecification.type(1))
                            .and(SaleOderSpecification.hasUseRedInvoice(saleOrderFilter.getUsedRedInvoice())));

        for(SaleOrder so: findAll) {
            UserDTO user = userClient.getUserByIdV1(so.getSalemanId());
            CustomerDTO customer = customerClient.getCustomerByIdV1(so.getCustomerId()).getData();
            customerName = customer.getLastName() +" "+ customer.getFirstName();
            customerCode = customer.getCustomerCode();
            taxCode = customer.getTaxCode();
            companyName = customer.getWorkingOffice();
            companyAddress = customer.getOfficeAddress();
            saleManName = user.getLastName() + " " + user.getFirstName();
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
            saleOrder.setSalesManName(saleManName);
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
        orderDetail.setInfos(getInfos(saleOrderId, orderNumber));
        try {
            orderDetail.setOrderDetail(getDetail(saleOrderId).getData());
        } catch (Exception e) {
            response.setFailure(ResponseMessage.SALE_ORDER_DETAIL_DOES_NOT_EXISTS);
            return response;
        }
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

    public InfosOrderDetailDTO getInfos(long saleOrderId, String orderNumber) {
        InfosOrderDetailDTO infosOrderDetailDTO = new InfosOrderDetailDTO();
        SaleOrder saleOrder = saleOrderRepository.findById(saleOrderId).get();

        infosOrderDetailDTO.setOrderNumber(orderNumber);//ma hoa don
        CustomerDTO customer = customerClient.getCustomerByIdV1(saleOrder.getCustomerId()).getData();

        infosOrderDetailDTO.setCustomerName(customer.getLastName() +" "+ customer.getFirstName());
        infosOrderDetailDTO.setOrderDate(saleOrder.getOrderDate());
        UserDTO user = userClient.getUserByIdV1(saleOrder.getSalemanId());

        infosOrderDetailDTO.setSaleMan(user.getFirstName()+ " " +user.getLastName());//nhan vien

        infosOrderDetailDTO.setCurrency("VND");
        infosOrderDetailDTO.setTotal(saleOrder.getTotal());
        infosOrderDetailDTO.setTotalPaid(saleOrder.getTotalPaid());
        infosOrderDetailDTO.setBalance(saleOrder.getBalance());
        return infosOrderDetailDTO;
    }

    public Response<CoverResponse<List<OrderDetailDTO>,OrderDetailTotalResponse>> getDetail(long saleOrderId) {
        int totalQuantity = 0;
        float totalAmount = 0, totalDiscount = 0, totalPayment = 0;
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
            orderDetailDTO.setAmount(saleOrderDetail.getAmount());
            float discount = 0;
            if(saleOrderDetail.getAutoPromotion() == null && saleOrderDetail.getZmPromotion() == null){
                discount = 0F;
                orderDetailDTO.setDiscount(discount);
            }
            else if(saleOrderDetail.getAutoPromotion() == null || saleOrderDetail.getZmPromotion() == null) {
                if(saleOrderDetail.getAutoPromotion() == null) {
                    discount = saleOrderDetail.getZmPromotion();
                    orderDetailDTO.setDiscount(discount);
                }
                if(saleOrderDetail.getZmPromotion() == null) {
                    discount = saleOrderDetail.getAutoPromotion();
                    orderDetailDTO.setDiscount(discount);
                }
            }else {
                discount = saleOrderDetail.getAutoPromotion() + saleOrderDetail.getZmPromotion();
                orderDetailDTO.setDiscount(discount);
            }
            orderDetailDTO.setPayment(saleOrderDetail.getTotal());
            totalQuantity = totalQuantity + saleOrderDetail.getQuantity();
            totalAmount = totalAmount + saleOrderDetail.getAmount();
            totalDiscount = totalDiscount + discount;
            totalPayment = totalPayment + saleOrderDetail.getTotal();
            saleOrderDetailList.add(orderDetailDTO);
        }
        OrderDetailTotalResponse totalResponse =
                new OrderDetailTotalResponse(totalQuantity,totalAmount,totalDiscount,totalPayment);

        CoverResponse<List<OrderDetailDTO>, OrderDetailTotalResponse> response =
                new CoverResponse(saleOrderDetailList, totalResponse);

        return new Response<CoverResponse<List<OrderDetailDTO>, OrderDetailTotalResponse>>()
                .withData(response);
    }

    public List<DiscountDTO> getDiscount(long saleOrderId, String orderNumber) {
        List<DiscountDTO> discountDTOList = new ArrayList<>();
        List<VoucherDTO> voucherDTOS = promotionClient.getVoucherBySaleOrderIdV1(saleOrderId).getData();
        if(voucherDTOS.size() > 0) {
            for(VoucherDTO voucher:voucherDTOS){
                DiscountDTO discountDTO = new DiscountDTO();
                discountDTO.setPromotionName(voucher.getVoucherCode());
                discountDTO.setPromotionType(null);
                discountDTO.setVoucherType(voucher.getVoucherName());
                discountDTO.setDiscountPrice(voucher.getPrice());
                discountDTOList.add(discountDTO);
            }
        }

        List<PromotionProgramDiscountDTO> promotionProgramDiscounts =
                promotionClient.listPromotionProgramDiscountByOrderNumberV1(orderNumber).getData();
        if(promotionProgramDiscounts.size() > 0) {
            for (PromotionProgramDiscountDTO promotionProgramDiscount:promotionProgramDiscounts) {
                DiscountDTO discountDTO = new DiscountDTO();
                discountDTO.setDiscountPrice(promotionProgramDiscount.getDiscountAmount());
                discountDTO.setDiscountPercent(promotionProgramDiscount.getDiscountPercent());
                discountDTO.setVoucherType(null);
                if(promotionProgramDiscount.getType() == 0)
                    discountDTO.setPromotionType("Chiết khấu tiền");
                if(promotionProgramDiscount.getType() == 2)
                    discountDTO.setPromotionType("Mã giảm giá");
                if(promotionProgramDiscount.getType() == 3)
                    discountDTO.setPromotionType("Giảm giá khách hàng");
                PromotionProgramDTO promotionProgram =
                        promotionClient.getByIdV1(promotionProgramDiscount.getId()).getData();
                discountDTO.setPromotionName(promotionProgram.getPromotionProgramName());
                discountDTOList.add(discountDTO);
            }
        }
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

    @Override
    public Response<Page<SaleOrderDTO>> getAllBillOfSaleList(RedInvoiceFilter redInvoiceFilter, Pageable pageable) {
        String customerName, customerCode, companyName, companyAddress, taxCode;
        String searchKeywords = redInvoiceFilter.getSearchKeywords();
        String orderNumber = redInvoiceFilter.getOrderNumber();
        Date fromDate = redInvoiceFilter.getFromDate();
        Date toDate = redInvoiceFilter.getToDate();

        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);

        if (fromDate == null || toDate == null) {
            LocalDate initial = LocalDate.now();
            fromDate = Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            toDate = Date.from(initial.withDayOfMonth(initial.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        List<Long> ids = customerClient.getIdCustomerBySearchKeyWords(searchKeywords).getData();

        Page<SaleOrder> saleOrders = null;

        if (searchKeywords.equals("")) {
            saleOrders = repository.findAll(Specification.where(SaleOderSpecification.hasFromDateToDate(fromDate, toDate))
                    .and(SaleOderSpecification.hasInvoiceNumber(redInvoiceFilter.getOrderNumber())), pageable);
        }else {
            if (ids.size() == 0)
                saleOrders = repository.findAll(Specification.where(SaleOderSpecification.hasCustomerId(-1L)), pageable);
            else {
                for (Long id : ids) {
                    saleOrders = repository.findAll(Specification.where(SaleOderSpecification.hasCustomerId(id))
                            .and(SaleOderSpecification.hasFromDateToDate(fromDate, toDate))
                            .and(SaleOderSpecification.hasOrderNumber(orderNumber)), pageable);
                }
            }
        }
        List<SaleOrderDTO> saleOrdersList = new ArrayList<>();
        Response<Page<SaleOrderDTO>> response = new Response<>();
        CustomerDTO customer;
        for (SaleOrder so : saleOrders) {
            try {
                customer = customerClient.getCustomerByIdV1(so.getCustomerId()).getData();
            } catch (Exception e) {
                response.setFailure(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
                return response;
            }
            customerName = customer.getLastName() + " " + customer.getFirstName();
            customerCode = customer.getCustomerCode();
//            taxCode = customer.getTaxCode();
//            companyName = customer.getWorkingOffice();
//            companyAddress = customer.getOfficeAddress();

            SaleOrderDTO saleOrder = new SaleOrderDTO();
//            saleOrder.setId(so.getId());
            saleOrder.setOrderNumber(so.getOrderNumber());
            saleOrder.setCustomerId(so.getCustomerId());
            saleOrder.setCustomerNumber(customerCode);
            saleOrder.setCustomerName(customerName);
            saleOrder.setOrderDate(so.getOrderDate());

//            saleOrder.setAmount(so.getAmount());
            saleOrder.setDiscount(so.getAutoPromotion() + so.getZmPromotion() + so.getTotalVoucher() + so.getDiscountCodeAmount()); //tiền giảm giá
            saleOrder.setAccumulation(so.getCustomerPurchase());//tiền tích lũy
            saleOrder.setTotal(so.getTotal());//tiền phải trả

//            saleOrder.setNote(so.getNote());
//            saleOrder.setRedReceipt(so.getUsedRedInvoice());
//            saleOrder.setComName(companyName);
//            saleOrder.setTaxCode(taxCode);
//            saleOrder.setAddress(companyAddress);
//            saleOrder.setNoteRed(so.getRedInvoiceRemark());
            saleOrdersList.add(saleOrder);
        }
        Page<SaleOrderDTO> saleOrderResponse = new PageImpl<>(saleOrdersList);
        response.withData(saleOrderResponse);
        return response;
    }

    @Override
    public Response<SaleOrderDTO> getLastSaleOrderByCustomerId(Long customerId) {
        SaleOrder saleOrder = repository.getLastSaleOrderByCustomerId(customerId).orElse(null);
        if(saleOrder == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST_IN_SALE_ORDER);
        SaleOrderDTO saleOrderDTO = modelMapper.map(saleOrder,SaleOrderDTO.class);
        return new Response<SaleOrderDTO>().withData(saleOrderDTO);
    }
}
