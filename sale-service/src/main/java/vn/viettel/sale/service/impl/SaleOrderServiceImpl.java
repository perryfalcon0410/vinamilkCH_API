package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDiscountDTO;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.entities.SaleOrderDetail;
import vn.viettel.sale.entities.SaleOrderDiscount;
import vn.viettel.sale.messaging.OrderDetailTotalResponse;
import vn.viettel.sale.messaging.RedInvoiceFilter;
import vn.viettel.sale.messaging.SaleOrderFilter;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.PromotionClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.SaleOderSpecification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleOrderServiceImpl extends BaseServiceImpl<SaleOrder, SaleOrderRepository> implements SaleOrderService {
    @Autowired
    SaleOrderRepository saleOrderRepository;
    @Autowired
    SaleOrderDiscountRepository saleOrderDiscountRepository;
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
    @Autowired
    ShopClient shopClient;


    @Override
    public CoverResponse<Page<SaleOrderDTO>, SaleOrderTotalResponse> getAllSaleOrder(SaleOrderFilter saleOrderFilter, Pageable pageable, Long id) {
        List<Long> customerIds = customerClient.getIdCustomerBySearchKeyWordsV1(saleOrderFilter.getSearchKeyword()).getData();
        Page<SaleOrder> findAll;
        if (customerIds.size() == 0) {
            findAll = repository.findAll(Specification.where(SaleOderSpecification.type(-1)), pageable);
        } else {
            findAll = repository.findAll(Specification.where(SaleOderSpecification.hasNameOrPhone(customerIds))
                    .and(SaleOderSpecification.hasFromDateToDate(saleOrderFilter.getFromDate(), saleOrderFilter.getToDate()))
                    .and(SaleOderSpecification.hasOrderNumber(saleOrderFilter.getOrderNumber().trim()))
                    .and(SaleOderSpecification.type(1))
                    .and(SaleOderSpecification.hasShopId(id))
                    .and(SaleOderSpecification.hasUseRedInvoice(saleOrderFilter.getUsedRedInvoice())), pageable);
        }
        Page<SaleOrderDTO> saleOrderDTOS = findAll.map(this::mapSaleOrderDTO);
        SaleOrderTotalResponse totalResponse = new SaleOrderTotalResponse();
        findAll.forEach(so -> {
            totalResponse.addTotalAmount(so.getAmount()).addAllTotal(so.getTotal());
        });
        CoverResponse coverResponse = new CoverResponse(saleOrderDTOS, totalResponse);
        return coverResponse;
    }

    private SaleOrderDTO mapSaleOrderDTO(SaleOrder saleOrder) {
        String customerName, customerCode, saleManName;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SaleOrderDTO dto = modelMapper.map(saleOrder, SaleOrderDTO.class);
        UserDTO user = userClient.getUserByIdV1(saleOrder.getSalemanId());
        CustomerDTO customer = customerClient.getCustomerByIdV1(saleOrder.getCustomerId()).getData();
        customerName = customer.getLastName() + " " + customer.getFirstName();
        customerCode = customer.getCustomerCode();
        saleManName = user.getLastName() + " " + user.getFirstName();
        dto.setCustomerNumber(customerCode);
        dto.setCustomerName(customerName);
        dto.setSalesManName(saleManName);
        return dto;
    }

    public SaleOrderDetailDTO getSaleOrderDetail(long saleOrderId, String orderNumber) {
        SaleOrderDetailDTO orderDetail = new SaleOrderDetailDTO();
        orderDetail.setInfos(getInfos(saleOrderId, orderNumber));
        orderDetail.setOrderDetail(getDetail(saleOrderId));
        orderDetail.setDiscount(getDiscount(saleOrderId, orderNumber));
        orderDetail.setPromotion(getPromotion(saleOrderId));
        return orderDetail;
    }

    public InfosOrderDetailDTO getInfos(long saleOrderId, String orderNumber) {
        InfosOrderDetailDTO infosOrderDetailDTO = new InfosOrderDetailDTO();
        SaleOrder saleOrder = saleOrderRepository.findById(saleOrderId).get();

        infosOrderDetailDTO.setOrderNumber(orderNumber);//ma hoa don
        CustomerDTO customer = customerClient.getCustomerByIdV1(saleOrder.getCustomerId()).getData();

        infosOrderDetailDTO.setCustomerName(customer.getLastName() + " " + customer.getFirstName());
        infosOrderDetailDTO.setOrderDate(saleOrder.getOrderDate());
        UserDTO user = userClient.getUserByIdV1(saleOrder.getSalemanId());

        infosOrderDetailDTO.setSaleMan(user.getFirstName() + " " + user.getLastName());//nhan vien

        infosOrderDetailDTO.setCurrency("VND");
        infosOrderDetailDTO.setTotal(saleOrder.getTotal());
        infosOrderDetailDTO.setTotalPaid(saleOrder.getTotalPaid());
        infosOrderDetailDTO.setBalance(saleOrder.getBalance());
        return infosOrderDetailDTO;
    }

    public CoverResponse<List<OrderDetailDTO>, OrderDetailTotalResponse> getDetail(long saleOrderId) {
        int totalQuantity = 0;
        double totalAmount = 0, totalDiscount = 0, totalPayment = 0;
        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.getBySaleOrderId(saleOrderId);
        List<OrderDetailDTO> saleOrderDetailList = new ArrayList<>();
        for (SaleOrderDetail saleOrderDetail : saleOrderDetails) {
            Product product = productRepository.findById(saleOrderDetail.getProductId()).get();
            OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
            orderDetailDTO.setProductId(saleOrderDetail.getProductId());
            orderDetailDTO.setProductCode(product.getProductCode());
            orderDetailDTO.setProductName(product.getProductName());
            orderDetailDTO.setUnit(product.getUom1());
            orderDetailDTO.setQuantity(saleOrderDetail.getQuantity());
            orderDetailDTO.setPricePerUnit(saleOrderDetail.getPrice());
            orderDetailDTO.setAmount(saleOrderDetail.getAmount());
            double discount = 0;
            if (saleOrderDetail.getAutoPromotion() == null && saleOrderDetail.getZmPromotion() == null) {
                discount = 0F;
                orderDetailDTO.setDiscount(discount);
            } else if (saleOrderDetail.getAutoPromotion() == null || saleOrderDetail.getZmPromotion() == null) {
                if (saleOrderDetail.getAutoPromotion() == null) {
                    discount = saleOrderDetail.getZmPromotion();
                    orderDetailDTO.setDiscount(discount);
                }
                if (saleOrderDetail.getZmPromotion() == null) {
                    discount = saleOrderDetail.getAutoPromotion();
                    orderDetailDTO.setDiscount(discount);
                }
            } else {
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
                new OrderDetailTotalResponse(totalQuantity, totalAmount, totalDiscount, totalPayment);

        CoverResponse<List<OrderDetailDTO>, OrderDetailTotalResponse> response =
                new CoverResponse(saleOrderDetailList, totalResponse);
        return response;
    }

    public List<DiscountDTO> getDiscount(long saleOrderId, String orderNumber) {
        List<DiscountDTO> discountDTOList = new ArrayList<>();
        List<VoucherDTO> voucherDTOS = promotionClient.getVoucherBySaleOrderIdV1(saleOrderId).getData();
        if (voucherDTOS.size() > 0) {
            for (VoucherDTO voucher : voucherDTOS) {
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
        if (promotionProgramDiscounts.size() > 0) {
            for (PromotionProgramDiscountDTO promotionProgramDiscount : promotionProgramDiscounts) {
                DiscountDTO discountDTO = new DiscountDTO();
                discountDTO.setDiscountPrice(promotionProgramDiscount.getDiscountAmount());
                discountDTO.setDiscountPercent(promotionProgramDiscount.getDiscountPercent());
                discountDTO.setVoucherType(null);
                if (promotionProgramDiscount.getType() == 0)
                    discountDTO.setPromotionType("Chiết khấu tiền");
                if (promotionProgramDiscount.getType() == 2)
                    discountDTO.setPromotionType("Mã giảm giá");
                if (promotionProgramDiscount.getType() == 3)
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
        for (SaleOrderDetail promotionDetail : saleOrderPromotions) {
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

    public PrintSaleOrderDTO printSaleOrder(Long id, Long shopId) {
        Double amountNotVAT = 0D;
        SaleOrder saleOrder = saleOrderRepository.findById(id).get();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PrintSaleOrderDTO print = modelMapper.map(saleOrder, PrintSaleOrderDTO.class);
        List<SaleOrderDetail> detail = saleOrderDetailRepository.getBySaleOrderId(id);
        List<PrintProductSaleOrderDTO> productPrintList = new ArrayList<>();
        UserDTO user = userClient.getUserByIdV1(saleOrder.getSalemanId());
        CustomerDTO customer = customerClient.getCustomerByIdV1(saleOrder.getCustomerId()).getData();
        print.setCustomerName(customer.getLastName() + " " + customer.getFirstName());
        print.setCustomerPhone(customer.getPhone());
        print.setAddress(customer.getAddress());
        print.setUserName(user.getLastName() + " " + user.getFirstName());
        for (SaleOrderDetail sod : detail) {
            Product product = productRepository.findById(sod.getProductId()).get();
            PrintProductSaleOrderDTO productPrint = new PrintProductSaleOrderDTO();
            List<SaleOrderDiscount> dtoList = saleOrderDiscountRepository.findBySaleOrderIdAndProductId(id, sod.getProductId());
            if ((dtoList.size() == 0) || (dtoList.isEmpty())) {
                List<SaleOrderDiscountDTO> discountDTOList = new ArrayList<>();
                productPrint.setDiscountDTOList(discountDTOList);
            } else {
                List<SaleOrderDiscountDTO> discountDTOList = new ArrayList<>();
                for (SaleOrderDiscount discount : dtoList) {
                    SaleOrderDiscountDTO discountDTO = new SaleOrderDiscountDTO();
                    discountDTO.setDiscountAmount(discount.getDiscountAmount());
                    discountDTOList.add(discountDTO);
                }
                productPrint.setDiscountDTOList(discountDTOList);
            }
            productPrint.setProductName(product.getProductName());
            productPrint.setProductCode(product.getProductCode());
            productPrint.setPrice(sod.getPrice());
            productPrint.setQuantity(sod.getQuantity());
            productPrint.setTotalPrice(sod.getQuantity() * sod.getPrice());

            productPrintList.add(productPrint);
            if (sod.getPriceNotVat() == null) {
                amountNotVAT = amountNotVAT + (sod.getQuantity() * 0);
            } else amountNotVAT = amountNotVAT + (sod.getQuantity() * sod.getPriceNotVat());


        }
        ShopDTO shop = shopClient.getByIdV1(shopId).getData();
        print.setNameShop(shop.getShopName());
        print.setShopAddress(shop.getAddress());
        print.setPhone(shop.getPhone());
        print.setProducts(productPrintList);
        print.setAmountNotVAT(amountNotVAT);
        return print;
    }

    @Override
    public Page<SaleOrderDTO> getAllBillOfSaleList(RedInvoiceFilter redInvoiceFilter, Long shopId, Pageable pageable) {
        String customerName, customerCode;

        List<SaleOrderDTO> saleOrdersList = new ArrayList<>();
        List<Long> ids = customerClient.getIdCustomerBySearchKeyWordsV1(redInvoiceFilter.getSearchKeywords()).getData();
        if (ids.size() == 0 || ids.isEmpty()) {
            throw new ValidateException(ResponseMessage.SALE_ORDER_NOT_FOUND);
        }
        List<Long> idr = repository.getFromSaleId();
        List<SaleOrder> saleOrders = new ArrayList<>();
        if (redInvoiceFilter.getFromDate() == null && redInvoiceFilter.getToDate() != null) {
            LocalDateTime dateTime = DateUtils.getFirstDayOfCurrentMonth();
            saleOrders = repository.getAllBillOfSaleList(redInvoiceFilter.getOrderNumber(), ids, dateTime, redInvoiceFilter.getToDate(), idr, shopId);
        } else if (redInvoiceFilter.getFromDate() != null && redInvoiceFilter.getToDate() == null) {
            LocalDateTime dateTime = LocalDateTime.now();
            saleOrders = repository.getAllBillOfSaleList(redInvoiceFilter.getOrderNumber(), ids, redInvoiceFilter.getFromDate(), dateTime, idr, shopId);
        } else if (redInvoiceFilter.getFromDate() == null && redInvoiceFilter.getToDate() == null) {
            LocalDateTime fromDate = DateUtils.getFirstDayOfCurrentMonth();
            LocalDateTime toDate = LocalDateTime.now();
            saleOrders = repository.getAllBillOfSaleList(redInvoiceFilter.getOrderNumber(), ids, fromDate, toDate, idr, shopId);
        } else {
            saleOrders = repository.getAllBillOfSaleList(redInvoiceFilter.getOrderNumber(), ids, redInvoiceFilter.getFromDate(), redInvoiceFilter.getToDate(), idr, shopId);
        }

        CustomerDTO customer;
        if (saleOrders.isEmpty() || saleOrders.size() == 0) {
            throw new ValidateException(ResponseMessage.SALE_ORDER_NOT_FOUND);
        }
        for (SaleOrder so : saleOrders) {
            customer = customerClient.getCustomerByIdV1(so.getCustomerId()).getData();
            if (customer == null) {
                throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
            }
            customerName = customer.getLastName() + " " + customer.getFirstName();
            customerCode = customer.getCustomerCode();


            SaleOrderDTO saleOrder = new SaleOrderDTO();
            saleOrder.setSaleOrderID(so.getId());
            saleOrder.setOrderNumber(so.getOrderNumber());
            saleOrder.setCustomerId(so.getCustomerId());
            saleOrder.setCustomerNumber(customerCode);
            saleOrder.setCustomerName(customerName);
            saleOrder.setOrderDate(so.getOrderDate());

            saleOrder.setTotalPromotion(so.getAutoPromotion() + so.getZmPromotion() + so.getTotalVoucher() + so.getDiscountCodeAmount()); //tiền giảm giá
            saleOrder.setCustomerPurchase(so.getCustomerPurchase());//tiền tích lũy
            saleOrder.setTotal(so.getTotal());//tiền phải trả

            saleOrdersList.add(saleOrder);
        }

        Page<SaleOrderDTO> saleOrderResponse = new PageImpl<>(saleOrdersList);
        return saleOrderResponse;
    }


    @Override
    public SaleOrderDTO getLastSaleOrderByCustomerId(Long customerId) {
        SaleOrder saleOrder = repository.getLastSaleOrderByCustomerId(customerId).orElse(null);
        if (saleOrder == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST_IN_SALE_ORDER);
        SaleOrderDTO saleOrderDTO = modelMapper.map(saleOrder, SaleOrderDTO.class);
        return saleOrderDTO;
    }

    public String checkNull(String value) {
        if (value == null) {
            throw new ValidateException(ResponseMessage.TRANS_DATE_MUST_BE_NOT_NULL);
        } else {
            return value;
        }
    }
}
