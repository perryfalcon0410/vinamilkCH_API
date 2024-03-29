package vn.viettel.sale.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.SortDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDTO;
import vn.viettel.core.dto.promotion.RPT_ZV23DTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.RPT_ZV23Request;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.utils.JMSType;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.entities.SaleOrderComboDetail;
import vn.viettel.sale.entities.SaleOrderComboDiscount;
import vn.viettel.sale.entities.SaleOrderDetail;
import vn.viettel.sale.entities.SaleOrderDiscount;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.sale.messaging.OrderReturnRequest;
import vn.viettel.sale.messaging.SaleOrderChosenFilter;
import vn.viettel.sale.messaging.SaleOrderFilter;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.messaging.TotalOrderChoose;
import vn.viettel.sale.messaging.TotalOrderReturnDetail;
import vn.viettel.sale.repository.ComboProductDetailRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.SaleOrderComboDetailRepository;
import vn.viettel.sale.repository.SaleOrderComboDiscountRepository;
import vn.viettel.sale.repository.SaleOrderDetailRepository;
import vn.viettel.sale.repository.SaleOrderDiscountRepository;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.repository.StockTotalRepository;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.StockTotalService;
import vn.viettel.sale.service.dto.InfosReturnDetailDTO;
import vn.viettel.sale.service.dto.NewOrderReturnDTO;
import vn.viettel.sale.service.dto.NewOrderReturnDetailDTO;
import vn.viettel.sale.service.dto.OrderDiscountReturnDTO;
import vn.viettel.sale.service.dto.OrderReturnDTO;
import vn.viettel.sale.service.dto.OrderReturnDetailDTO;
import vn.viettel.sale.service.dto.ProductReturnDTO;
import vn.viettel.sale.service.dto.PromotionReturnDTO;
import vn.viettel.sale.service.dto.ReasonReturnDTO;
import vn.viettel.sale.service.dto.SaleComboDetailDTO;
import vn.viettel.sale.service.dto.SaleComboDiscountDTO;
import vn.viettel.sale.service.dto.SaleOrderDTO;
import vn.viettel.sale.service.feign.ApparamClient;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.PromotionClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;

@Service
public class OrderReturnImpl extends BaseServiceImpl<SaleOrder, SaleOrderRepository> implements OrderReturnService {
    @Autowired
    SaleOrderDetailRepository saleOrderDetailRepository;
    @Autowired
    UserClient userClient;
    @Autowired
    CustomerClient customerClient;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ShopClient shopClient;
    @Autowired
    ApparamClient apparamClient;
    @Autowired
    StockTotalRepository stockTotalRepository;
    @Autowired
    ComboProductDetailRepository comboDetailRepository;
    @Autowired
    PromotionClient promotionClient;
    @Autowired
    SaleService saleService;
    @Autowired
    SaleOrderDiscountRepository saleDiscount;
    @Autowired
    StockTotalService stockTotalService;
    @Autowired
    SaleOrderComboDiscountRepository SaleComboDiscount;
    @Autowired
    SaleOrderComboDetailRepository SaleComboDetail;

    @Override
    public CoverResponse<Page<OrderReturnDTO>, SaleOrderTotalResponse> getAllOrderReturn(SaleOrderFilter saleOrderFilter, Pageable pageable, Long shopId) {
        List<Long> customerIds = null;
        int type = 2;
        List<SortDTO> customerSorts = new ArrayList<>();
        Sort orderSort = null;
        List<Long> customerIdsSort = null;
        if(pageable.getSort() != null) {
            for (Sort.Order order : pageable.getSort()) {
                if(order.getProperty().equals("customerNumber")){
                    customerSorts.add(new SortDTO("customerCode", order.getDirection().toString()));
                }else if(order.getProperty().equals("customerName")){
                    customerSorts.add(new SortDTO("nameText", order.getDirection().toString()));
                }else{
                    Sort sorted = Sort.by(order.getDirection(), order.getProperty());
                    if(orderSort == null) orderSort = sorted;
                    else orderSort.and(sorted);
                }
            }
        }

        if (saleOrderFilter.getOrderNumber() != null)
            saleOrderFilter.setOrderNumber(saleOrderFilter.getOrderNumber().trim().toUpperCase());

        if(saleOrderFilter.getSearchKeyword() != null || saleOrderFilter.getCustomerPhone() != null){
            List<Long> cusIds = repository.getCustomerIds(saleOrderFilter.getFromDate(), saleOrderFilter.getToDate(), saleOrderFilter.getOrderNumber(), type, shopId, null);
            customerIds = customerClient.getIdCustomerByV1(saleOrderFilter.getSearchKeyword(), saleOrderFilter.getCustomerPhone(), cusIds).getData();
            if (customerIds == null || customerIds.isEmpty()) {
                customerIds = Arrays.asList(-1L);
            }
        }

        Pageable orderPage = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        if(orderSort != null) orderPage = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), orderSort);

        Page<SaleOrder> findAll = null;
        List<Long> saleCusIds = null;
        if(customerSorts == null || customerSorts.isEmpty()) {
            findAll = repository.findALlSales(customerIds, saleOrderFilter.getFromDate(), saleOrderFilter.getToDate(), saleOrderFilter.getOrderNumber(), type, shopId, null, orderPage);
            saleCusIds = findAll.stream().map(item -> item.getCustomerId()).distinct().collect(Collectors.toList());
        }else{
            saleCusIds = repository.getCustomerIds(saleOrderFilter.getFromDate(), saleOrderFilter.getToDate(), saleOrderFilter.getOrderNumber(), type, shopId, null);
        }

        if(saleCusIds.isEmpty()) return null;
        List<CustomerDTO> customers = customerClient.getCustomerInfoV1(customerSorts,null,saleCusIds);
        if(customerSorts != null && !customerSorts.isEmpty()) {
            customerIdsSort = new ArrayList<>();
            long i =  0;
            for(CustomerDTO customer : customers) {
                customerIdsSort.add(customer.getId());
                customerIdsSort.add(i);
                i++;
            }
            findAll = repository.findALlSales(customerIds, saleOrderFilter.getFromDate(), saleOrderFilter.getToDate(), saleOrderFilter.getOrderNumber(), type, shopId, null, customerIdsSort, orderPage);
        }

        SaleOrderTotalResponse totalResponse = repository.getSumSaleOrderReturn(shopId, saleOrderFilter.getOrderNumber(),
                customerIds, saleOrderFilter.getFromDate(), saleOrderFilter.getToDate());

        List<SaleOrder> saleOrders = repository.findAllById(findAll.getContent().stream().map(item -> item.getFromSaleOrderId()).collect(Collectors.toList()));
        Page<OrderReturnDTO> orderReturnDTOS = findAll.map(item -> mapOrderReturnDTO(item, customers, saleOrders));
        CoverResponse coverResponse = new CoverResponse(orderReturnDTOS, totalResponse);
        return coverResponse;


    }

    private OrderReturnDTO mapOrderReturnDTO(SaleOrder orderReturn, List<CustomerDTO> customers, List<SaleOrder> saleOrders) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderReturnDTO dto = modelMapper.map(orderReturn, OrderReturnDTO.class);
        if (saleOrders != null) {
            for (SaleOrder saleOrder : saleOrders) {
                if (saleOrder.getId().equals(orderReturn.getFromSaleOrderId())) {
                    dto.setOrderNumberRef(saleOrder.getOrderNumber());
                    break;
                }
            }
        }
        if (customers != null) {
            for (CustomerDTO customer : customers) {
                if (customer.getId().equals(orderReturn.getCustomerId())) {
                    dto.setCustomerNumber(customer.getCustomerCode());
                    dto.setCustomerName(customer.getFullName());
                    break;
                }
            }
        }
        if (orderReturn.getTotalPromotionVat() >= 0) {
            dto.setTotalPromotion(orderReturn.getTotalPromotionVat());
        } else dto.setTotalPromotion(orderReturn.getTotalPromotionVat() * -1);

        dto.setAmount(orderReturn.getAmount() * -1);
        dto.setTotal(orderReturn.getTotal() * -1);
        dto.setDateReturn(orderReturn.getOrderDate());
        return dto;
    }

    @Override
    public OrderReturnDetailDTO getOrderReturnDetail(Long orderReturnId) {
        SaleOrder orderReturn = repository.findById(orderReturnId).get();
        OrderReturnDetailDTO orderReturnDetailDTO = new OrderReturnDetailDTO();
        orderReturnDetailDTO.setInfos(getInfos(orderReturn));
        orderReturnDetailDTO.setProductReturn(getProductReturn(orderReturnId));
        orderReturnDetailDTO.setPromotionReturn(getPromotionReturn(orderReturnId));
        return orderReturnDetailDTO;
    }

    private InfosReturnDetailDTO getInfos(SaleOrder orderReturn) {
        InfosReturnDetailDTO infosReturnDetailDTO = new InfosReturnDetailDTO();
//        SaleOrder orderReturn = repository.findById(orderReturnId).get();
        if (orderReturn.getFromSaleOrderId() != null) {
            SaleOrder saleOrder = repository.findById(orderReturn.getFromSaleOrderId()).get();
            infosReturnDetailDTO.setOrderDate(saleOrder.getOrderDate()); //order date
        }
        CustomerDTO customer =
                customerClient.getCustomerByIdV1(orderReturn.getCustomerId()).getData();
        infosReturnDetailDTO.setCustomerName(customer.getLastName() + " " + customer.getFirstName());
        ApParamDTO apParamDTO = new ApParamDTO();
        if (orderReturn.getReasonId() != null) {
            apParamDTO = apparamClient.getApParamByCodeV1(orderReturn.getReasonId()).getData();
        }
        if(apParamDTO != null)
            infosReturnDetailDTO.setReason(apParamDTO.getApParamName());
        infosReturnDetailDTO.setReasonDesc(orderReturn.getReasonDesc());
        infosReturnDetailDTO.setReturnDate(orderReturn.getOrderDate()); //order return
        infosReturnDetailDTO.setReturnNumber(orderReturn.getOrderNumber());
        UserDTO user = userClient.getUserByIdV1(orderReturn.getSalemanId());
        infosReturnDetailDTO.setUserName(user.getLastName() + " " + user.getFirstName());
        return infosReturnDetailDTO;
    }

    private CoverResponse<List<ProductReturnDTO>, TotalOrderReturnDetail> getProductReturn(long orderReturnId) {
        List<SaleOrderDetail> productReturns = saleOrderDetailRepository.findSaleOrderDetail(orderReturnId, false);
        if (productReturns.size() == 0) return null;
        List<ProductReturnDTO> productReturnDTOList = new ArrayList<>();
        List<Product> products = productRepository.getProducts(productReturns.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()), null);
        TotalOrderReturnDetail totalResponse = new TotalOrderReturnDetail();
        for (SaleOrderDetail productReturn : productReturns) {
            ProductReturnDTO productReturnDTO = new ProductReturnDTO();
            if (products != null) {
                for (Product product : products) {
                    if (product.getId().equals(productReturn.getProductId())) {
                        productReturnDTO.setProductCode(product.getProductCode());
                        productReturnDTO.setProductName(product.getProductName());
                        productReturnDTO.setUnit(product.getUom1());
                        break;
                    }
                }
            }
            productReturnDTO.setPricePerUnit(productReturn.getPrice());
            double discount = 0;
            if (productReturn.getAutoPromotionVat() != null) discount = productReturn.getAutoPromotionVat();
            if (productReturn.getZmPromotionVat() != null) discount += productReturn.getZmPromotionVat();

            if(discount < 0) discount = discount * -1;
            productReturnDTO.setDiscount(discount);

            if (productReturn.getQuantity() < 0) {
                productReturnDTO.setTotalPrice(productReturn.getAmount() * -1);
                productReturnDTO.setQuantity(productReturn.getQuantity() * -1);
            } else {
                productReturnDTO.setTotalPrice(productReturn.getAmount());
                productReturnDTO.setQuantity(productReturn.getQuantity());
            }
            productReturnDTO.setPricePerUnit(productReturn.getPrice());
            productReturnDTO.setPaymentReturn(roundValue(productReturnDTO.getTotalPrice() - productReturnDTO.getDiscount()));
            productReturnDTOList.add(productReturnDTO);
            totalResponse.setTotalQuantity(totalResponse.getTotalQuantity() + productReturnDTO.getQuantity());
            totalResponse.setTotalAmount(roundValue(totalResponse.getTotalAmount() + productReturnDTO.getTotalPrice()));
            totalResponse.setTotalDiscount(roundValue(totalResponse.getTotalDiscount() + productReturnDTO.getDiscount()));
            totalResponse.setAllTotal(roundValue(totalResponse.getAllTotal() + productReturnDTO.getPaymentReturn()));
        }

        CoverResponse<List<ProductReturnDTO>, TotalOrderReturnDetail> coverResponse =
                new CoverResponse<>(productReturnDTOList, totalResponse);
        return coverResponse;
    }

    private CoverResponse<List<PromotionReturnDTO>, TotalOrderReturnDetail> getPromotionReturn(long orderReturnId) {
        List<SaleOrderDetail> promotionReturns = saleOrderDetailRepository.findSaleOrderDetail(orderReturnId, true);
        List<PromotionReturnDTO> promotionReturnsDTOList = new ArrayList<>();
        TotalOrderReturnDetail totalResponse = new TotalOrderReturnDetail();
        if (!promotionReturns.isEmpty()) {
            List<Product> products = productRepository.getProducts(promotionReturns.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()), null);
            for (SaleOrderDetail promotionReturn : promotionReturns) {
                PromotionReturnDTO promotionReturnDTO = new PromotionReturnDTO();
                if (products != null) {
                    for (Product product : products) {
                        if (product.getId().equals(promotionReturn.getProductId())) {
                            promotionReturnDTO.setProductCode(product.getProductCode());
                            promotionReturnDTO.setProductName(product.getProductName());
                            promotionReturnDTO.setUnit(product.getUom1());
                            break;
                        }
                    }
                }
                if (promotionReturn.getQuantity() < 0) {
                    promotionReturnDTO.setQuantity(promotionReturn.getQuantity() * (-1));
                } else {
                    promotionReturnDTO.setQuantity(promotionReturn.getQuantity());
                }
                promotionReturnDTO.setPricePerUnit(promotionReturn.getPrice());
                promotionReturnDTO.setPaymentReturn(0D);
                promotionReturnsDTOList.add(promotionReturnDTO);
                totalResponse.setTotalQuantity(totalResponse.getTotalQuantity() + promotionReturnDTO.getQuantity());
                totalResponse.setAllTotal(totalResponse.getAllTotal() + promotionReturnDTO.getPaymentReturn());
            }
        }
        CoverResponse<List<PromotionReturnDTO>, TotalOrderReturnDetail> coverResponse =
                new CoverResponse<>(promotionReturnsDTOList, totalResponse);
        return coverResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public HashMap<String,Object> createOrderReturn(OrderReturnRequest request, Long shopId, String userName) {
        if (request == null)
            throw new ValidateException(ResponseMessage.REQUEST_BODY_NOT_BE_NULL);
        SaleOrder saleOrder = repository.getSaleOrderByNumber(request.getOrderNumber(), shopId);
        if (saleOrder == null)
            throw new ValidateException(ResponseMessage.ORDER_RETURN_DOES_NOT_EXISTS);

        Integer check = repository.checkIsReturn(saleOrder.getId());
        if (check != null && check > 0) throw new ValidateException(ResponseMessage.SALE_ORDER_HAS_ALREADY_RETURNED);
        List<SaleOrderDetail> saleOrderPromotions =
                saleOrderDetailRepository.findSaleOrderDetail(saleOrder.getId(), true);
        if (saleOrder.getIsReturn() != null && !saleOrder.getIsReturn())
            throw new ValidateException(ResponseMessage.SALE_ORDER_CANNOT_RETURN);
        ShopDTO shop = shopClient.getByIdV1(shopId).getData();
        if (shop == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        LocalDateTime returnDate = LocalDateTime.now();
        SaleOrder newOrderReturn = new SaleOrder();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        NewOrderReturnDTO newOrderReturnDTO = modelMapper.map(saleOrder, NewOrderReturnDTO.class);
        newOrderReturn = modelMapper.map(newOrderReturnDTO, SaleOrder.class);

        newOrderReturn.setType(2);
        newOrderReturn.setTotalCustomerPurchase(saleOrder.getTotalCustomerPurchase());
        newOrderReturn.setCustomerPurchase(saleOrder.getCustomerPurchase());
        newOrderReturn.setFromSaleOrderId(saleOrder.getId());
        newOrderReturn.setOriginOrderNumber(saleOrder.getOrderNumber());//Lưu mã của đơn gốc
        newOrderReturn.setReasonId(request.getReasonId());
        newOrderReturn.setReasonDesc(request.getReasonDescription());

        if(saleOrder.getOnlineNumber()!= null) {
            newOrderReturn.setOnlineNumber(saleOrder.getOnlineNumber() + "_TH");
            saleOrder.setOnlineNumber(saleOrder.getOnlineNumber() + "_TH");
        }

        if (saleOrder.getAmount() != null) newOrderReturn.setAmount(saleOrder.getAmount() * -1);
        if (saleOrder.getTotalPromotion() != null) newOrderReturn.setTotalPromotion(saleOrder.getTotalPromotion() * -1);
        if (saleOrder.getTotal() != null) newOrderReturn.setTotal(saleOrder.getTotal() * -1);
        if (saleOrder.getTotalPaid() != null) newOrderReturn.setTotalPaid(saleOrder.getTotalPaid() * -1);
        if (saleOrder.getBalance() != null) newOrderReturn.setBalance(saleOrder.getBalance() * -1);
        if (saleOrder.getMemberCardAmount() != null)
            newOrderReturn.setMemberCardAmount(saleOrder.getMemberCardAmount() * -1);
        if (saleOrder.getTotalVoucher() != null) newOrderReturn.setTotalVoucher(saleOrder.getTotalVoucher() * -1);
        if (saleOrder.getAutoPromotionNotVat() != null)
            newOrderReturn.setAutoPromotionNotVat(saleOrder.getAutoPromotionNotVat() * -1);
        if (saleOrder.getAutoPromotionVat() != null)
            newOrderReturn.setAutoPromotionVat(saleOrder.getAutoPromotionVat() * -1);
        if (saleOrder.getAutoPromotion() != null) newOrderReturn.setAutoPromotion(saleOrder.getAutoPromotion() * -1);
        if (saleOrder.getZmPromotion() != null) newOrderReturn.setZmPromotion(saleOrder.getZmPromotion() * -1);
        if (saleOrder.getTotalPromotionNotVat() != null)
            newOrderReturn.setTotalPromotionNotVat(saleOrder.getTotalPromotionNotVat() * -1);
        if (saleOrder.getCustomerPurchase() != null)
            newOrderReturn.setCustomerPurchase(saleOrder.getCustomerPurchase() * -1);
        if (saleOrder.getDiscountCodeAmount() != null)
            newOrderReturn.setDiscountCodeAmount(saleOrder.getDiscountCodeAmount() * -1);
        if (saleOrder.getTotalCustomerPurchase() != null)
            newOrderReturn.setTotalCustomerPurchase(saleOrder.getTotalCustomerPurchase() * -1);
        if (saleOrder.getTotalPromotionVat() != null)
            newOrderReturn.setTotalPromotionVat(saleOrder.getTotalPromotionVat() * -1);
        if (saleOrder.getZmPromotionVat() != null) newOrderReturn.setZmPromotionVat(saleOrder.getZmPromotionVat() * -1);
        if (saleOrder.getZmPromotionNotVat() != null)
            newOrderReturn.setZmPromotionNotVat(saleOrder.getZmPromotionNotVat() * -1);
        newOrderReturn.setOrderDate(returnDate);

      /*  newOrderReturn.setOrderNumber(saleService.createOrderNumber(shop));
        repository.save(newOrderReturn); //save new orderReturn*/
        try{
        	newOrderReturn = saleService.safeSave(newOrderReturn, shop);
        }catch (Exception ex) {
            throw new ValidateException(ResponseMessage.SAVE_FAIL);
        }
        saleOrder.setIsReturn(false);
        repository.save(saleOrder);
        Map<String, Double> shopMapNeedUpdates = new HashMap<>();
        //new orderReturn detail
        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findSaleOrderDetail(saleOrder.getId(), false);
        if (saleOrderDetails != null) {
            for (SaleOrderDetail saleOrderDetail : saleOrderDetails) {
                NewOrderReturnDetailDTO productReturnDTO = modelMapper.map(saleOrderDetail, NewOrderReturnDetailDTO.class);
                SaleOrderDetail productReturn = modelMapper.map(productReturnDTO, SaleOrderDetail.class);
                productReturn.setSaleOrderId(newOrderReturn.getId());
                productReturn.setAutoPromotionVat(saleOrderDetail.getAutoPromotionVat());
                productReturn.setOrderDate(returnDate);

                productReturn.setQuantity(saleOrderDetail.getQuantity() * -1);
                productReturn.setPrice(saleOrderDetail.getPrice());
                productReturn.setPriceNotVat(saleOrderDetail.getPriceNotVat());
                productReturn.setAmount(saleOrderDetail.getAmount() * -1);
                productReturn.setTotal(saleOrderDetail.getTotal() * -1);

                if (saleOrderDetail.getAutoPromotionNotVat() != null)
                    productReturn.setAutoPromotionNotVat(saleOrderDetail.getAutoPromotionNotVat() * -1);
                if (saleOrderDetail.getAutoPromotionVat() != null)
                    productReturn.setAutoPromotionVat(saleOrderDetail.getAutoPromotionVat() * -1);
                if (saleOrderDetail.getZmPromotionNotVat() != null)
                    productReturn.setZmPromotionNotVat(saleOrderDetail.getZmPromotionNotVat() * -1);
                if (saleOrderDetail.getZmPromotionVat() != null)
                    productReturn.setZmPromotionVat(saleOrderDetail.getZmPromotionVat() * -1);
                if (saleOrderDetail.getAutoPromotion() != null)
                    productReturn.setAutoPromotion(saleOrderDetail.getAutoPromotion() * -1);
                if (saleOrderDetail.getZmPromotion() != null)
                    productReturn.setZmPromotion(saleOrderDetail.getZmPromotion() * -1);
                saleOrderDetailRepository.save(productReturn); //save new orderReturn detail
            }
        }
        //new orderReturn promotion

        for (SaleOrderDetail promotionDetail : saleOrderPromotions) {
            if(!shopMapNeedUpdates.containsKey(promotionDetail.getPromotionCode())) {
                shopMapNeedUpdates.put(promotionDetail.getPromotionCode(), promotionDetail.getQuantity().doubleValue());
            }else {
                double qty = shopMapNeedUpdates.get(promotionDetail.getPromotionCode()) + promotionDetail.getQuantity().doubleValue();
                shopMapNeedUpdates.put(promotionDetail.getPromotionCode(), qty);
            }

            NewOrderReturnDetailDTO promotionReturnDTO = modelMapper.map(promotionDetail, NewOrderReturnDetailDTO.class);
            SaleOrderDetail promotionReturn = modelMapper.map(promotionReturnDTO, SaleOrderDetail.class);
            promotionReturn.setOrderDate(returnDate);
            promotionReturn.setSaleOrderId(newOrderReturn.getId());
            promotionReturn.setPrice(promotionDetail.getPrice());
            promotionReturn.setAmount(0D);
            promotionReturn.setTotal(0D);
            promotionReturn.setQuantity(promotionDetail.getQuantity() * (-1));
            saleOrderDetailRepository.save(promotionReturn);
        }


        //new orderReturn discount
        List<SaleOrderDiscount> orderReturnDiscount = saleDiscount.findAllBySaleOrderId(saleOrder.getId());
        if (orderReturnDiscount.size() > 0) {
            for (SaleOrderDiscount discount : orderReturnDiscount) {
                PromotionProgramDTO programDTO = promotionClient.getByCode(discount.getPromotionCode()).getData();
                if(programDTO!=null) {
                    Double discountValue = discount.getDiscountAmountVat();
                    if(programDTO.getDiscountPriceType() != null && programDTO.getDiscountPriceType() == 0) {
                        discountValue = discount.getDiscountAmountNotVat();
                    }
                    if(!shopMapNeedUpdates.containsKey(discount.getPromotionCode())) {
                        shopMapNeedUpdates.put(discount.getPromotionCode(), discountValue);
                    }else {
                        double qty = shopMapNeedUpdates.get(discount.getPromotionCode()) + discountValue;
                        shopMapNeedUpdates.put(discount.getPromotionCode(), qty);
                    }
                }

                OrderDiscountReturnDTO returnDiscountDTO = modelMapper.map(discount, OrderDiscountReturnDTO.class);
                SaleOrderDiscount returnDiscount = modelMapper.map(returnDiscountDTO, SaleOrderDiscount.class);
                returnDiscount.setDiscountAmount(discount.getDiscountAmount() * -1);
                returnDiscount.setDiscountAmountVat(discount.getDiscountAmountVat() * -1);
                returnDiscount.setDiscountAmountNotVat(discount.getDiscountAmountNotVat() * -1);
                returnDiscount.setMaxDiscountAmount(discount.getMaxDiscountAmount() * -1);
                returnDiscount.setOrderDate(newOrderReturn.getOrderDate());
                returnDiscount.setSaleOrderId(newOrderReturn.getId());
                saleDiscount.save(returnDiscount);
            }
        }

        List<SaleOrderComboDiscount> comboDiscounts = SaleComboDiscount.findAllBySaleOrderId(saleOrder.getId());
        if (comboDiscounts.size() > 0) {
            for (SaleOrderComboDiscount comboDiscount : comboDiscounts) {
                SaleComboDiscountDTO returnComboDiscountDTO = modelMapper.map(comboDiscount, SaleComboDiscountDTO.class);
                SaleOrderComboDiscount returnComboDiscount = modelMapper.map(returnComboDiscountDTO, SaleOrderComboDiscount.class);
                if (comboDiscount.getDiscountAmount() != null)
                    returnComboDiscount.setDiscountAmount(comboDiscount.getDiscountAmount() * -1);
                if (comboDiscount.getDiscountAmountNotVat() != null)
                    returnComboDiscount.setDiscountAmountNotVat(comboDiscount.getDiscountAmountNotVat() * -1);
                if (comboDiscount.getDiscountAmountVat() != null)
                    returnComboDiscount.setDiscountAmountVat(comboDiscount.getDiscountAmountVat() * -1);
                returnComboDiscount.setOrderDate(newOrderReturn.getOrderDate());
                returnComboDiscount.setSaleOrderId(newOrderReturn.getId());
                SaleComboDiscount.save(returnComboDiscount);
            }
        }

        List<SaleOrderComboDetail> comboDetails = SaleComboDetail.findAllBySaleOrderId(saleOrder.getId());
        if (comboDetails.size() > 0) {
            for (SaleOrderComboDetail comboDetail : comboDetails) {
                SaleComboDetailDTO returnComboDetailDTO = modelMapper.map(comboDetail, SaleComboDetailDTO.class);
                SaleOrderComboDetail returnComboDetail = modelMapper.map(returnComboDetailDTO, SaleOrderComboDetail.class);
                returnComboDetail.setComboQuantity(comboDetail.getComboQuantity() * -1);
                returnComboDetail.setQuantity(comboDetail.getQuantity() * -1);
//                    returnComboDetail.setPrice(comboDetail.getPrice() * -1);
                returnComboDetail.setPriceNotVat(comboDetail.getPriceNotVat() * -1);
                returnComboDetail.setAmount(comboDetail.getAmount() * -1);
                returnComboDetail.setTotal(comboDetail.getTotal() * -1);
                if (comboDetail.getAutoPromotion() != null)
                    returnComboDetail.setAutoPromotion(comboDetail.getAutoPromotion() * -1);
                if (comboDetail.getAutoPromotionVat() != null)
                    returnComboDetail.setAutoPromotionVat(comboDetail.getAutoPromotionVat() * -1);
                if (comboDetail.getAutoPromotionNotVat() != null)
                    returnComboDetail.setAutoPromotionNotVat(comboDetail.getAutoPromotionNotVat() * -1);
                if (comboDetail.getZmPromotion() != null)
                    returnComboDetail.setZmPromotion(comboDetail.getZmPromotion() * -1);
                if (comboDetail.getZmPromotionVat() != null)
                    returnComboDetail.setZmPromotionVat(comboDetail.getZmPromotionVat() * -1);
                if (comboDetail.getZmPromotionNotVat() != null)
                    returnComboDetail.setZmPromotionNotVat(comboDetail.getZmPromotionNotVat() * -1);
                returnComboDetail.setSaleOrderId(newOrderReturn.getId());
                returnComboDetail.setOrderDate(newOrderReturn.getOrderDate());
                SaleComboDetail.save(returnComboDetail);
            }
        }
        updateReturn(newOrderReturn.getId(), newOrderReturn.getWareHouseTypeId(), shopId);
        CustomerDTO customer = customerClient.getCustomerByIdV1(saleOrder.getCustomerId()).getData();
        if (customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);

        //Update voucher
        if(saleOrder.getTotalVoucher() != null && saleOrder.getTotalVoucher()> 0){
            try{
                promotionClient.returnVoucher(saleOrder.getId());
            }catch (Exception e) {
                throw new ValidateException(ResponseMessage.UPDATE_VOUCHER_FAILED);
            }
        }

        //update MGG + update số xuất
        List<Long> lstPromotionShopMapIds = new ArrayList<Long>();
        if(saleOrder.getDiscountCodeAmount() != null && saleOrder.getDiscountCodeAmount() > 0) {
            try {
                lstPromotionShopMapIds = promotionClient.returnMGG(saleOrder.getOrderNumber()).getData();
            }catch (Exception e){
                throw new ValidateException(ResponseMessage.UPDATE_PROMOTION_DISCOUNT_CODE_FAILED);
            }
        }

        //update shopmap
        if(!shopMapNeedUpdates.isEmpty()){
            try {
                List<Long> lstPromotionShopMap = promotionClient.returnPromotionShopmap(shopMapNeedUpdates).getData();
                for(Long id :lstPromotionShopMap ) {
                	lstPromotionShopMapIds.add(id);
                }
            }catch (Exception e) {
                throw new ValidateException(ResponseMessage.UPDATE_PROMOTION_SHOP_MAP_FAILED);
            }
        }

        this.updateZV23(customer, saleOrder, saleOrderDetails, orderReturnDiscount);
        Long memberCustomerId = null;
        if (saleOrder.getCustomerPurchase() != null)
            saleService.updateCustomer(newOrderReturn, customer, true);
        if (saleOrder.getMemberCardAmount() != null)
        	memberCustomerId = saleService.updateAccumulatedAmount(-saleOrder.getMemberCardAmount(), customer.getId());
        
        HashMap<String,Object> syncmap = new HashMap<>();
        syncmap.put(JMSType.sale_order , newOrderReturn);
        syncmap.put(JMSType.member_customer, memberCustomerId);
        syncmap.put(JMSType.promotion_shop_map, lstPromotionShopMapIds);
        return syncmap;
    }


    /*
   Nếu cửa hàng không khai báo thì lấy cấu hình theo cấp cha của cửa hàng đó, shop cha và con ko có thì ko dc phép trả hàng
   Nếu kết quả
   --+ VALUE = -1 ; Không được trả hàng
   --+ VALUE = 0 : Chỉ được trả hàng cho ngày hiện tại
   --+ VALUE = n , n >0 thì được trả hàng trong n ngày
   => Kiểm tra trunc(sale_order.order_date) >= trunc(sysdate) - số ngày cấu hình
    */
    public CoverResponse<List<SaleOrderDTO>, TotalOrderChoose> getSaleOrderForReturn(SaleOrderChosenFilter filter, Long shopId) {
        String stringDayReturn = shopClient.dayReturn(shopId).getData();
        if (stringDayReturn == null || stringDayReturn.isEmpty() || stringDayReturn.equals("-1"))
            throw new ValidateException(ResponseMessage.SHOP_DOES_HAVE_DAY_RETURN);
        int dayReturn = Integer.parseInt(stringDayReturn.trim());
        LocalDateTime newFromDate = DateUtils.convertFromDate(LocalDateTime.now().minusDays(dayReturn));
        LocalDateTime fromDate = DateUtils.convertFromDate(filter.getFromDate());
        LocalDateTime toDate = DateUtils.convertToDate(filter.getToDate());
        List<Long> customerIds = null;
        if (filter.getSearchKeyword() != null) {
            customerIds = customerClient.getIdCustomerBySearchKeyWordsV1(filter.getSearchKeyword().trim()).getData();
            if (customerIds == null || customerIds.isEmpty()) customerIds = Arrays.asList(-1L);
        }
        if (filter.getOrderNumber() != null) filter.setOrderNumber(filter.getOrderNumber().trim().toUpperCase());
        if (filter.getProduct() != null) filter.setProduct(filter.getProduct().trim().toUpperCase());
        List<SaleOrder> saleOrders = repository.getSaleOrderForReturn(shopId, filter.getOrderNumber(), customerIds, filter.getProduct(), fromDate, toDate, newFromDate);
        if (saleOrders.size() == 0) throw new ValidateException(ResponseMessage.ORDER_FOR_RETURN_NOT_FOUND);

        List<SaleOrderDTO> list = new ArrayList<>();
        SaleOrderTotalResponse totalResponse = new SaleOrderTotalResponse();
        List<UserDTO> users = userClient.getUserByIdsV1(saleOrders.stream().map(item -> item.getSalemanId()).distinct().filter(Objects::nonNull).collect(Collectors.toList()));
        List<CustomerDTO> customers = customerClient.getCustomerInfoV1(new ArrayList<>(), 1, saleOrders.stream().map(item -> item.getCustomerId()).distinct().filter(Objects::nonNull).collect(Collectors.toList()));
        for (SaleOrder saleOrder : saleOrders) {
            SaleOrderDTO listForChoose = mapSaleOrderDTO(saleOrder, users, customers);
            list.add(listForChoose);
            totalResponse.setTotalAmount(totalResponse.getTotalAmount() + saleOrder.getAmount());
            totalResponse.setAllTotal(totalResponse.getAllTotal() + saleOrder.getTotal());
        }
        CoverResponse coverResponse = new CoverResponse(list, totalResponse);
        return coverResponse;
    }

    private SaleOrderDTO mapSaleOrderDTO(SaleOrder saleOrder, List<UserDTO> users, List<CustomerDTO> customers) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SaleOrderDTO dto = modelMapper.map(saleOrder, SaleOrderDTO.class);
        if (customers != null) {
            for (CustomerDTO customer : customers) {
                if (customer.getId().equals(saleOrder.getCustomerId())) {
                    dto.setCustomerNumber(customer.getCustomerCode());
                    dto.setCustomerName(customer.getFullName());
                    break;
                }
            }
        }
        if (users != null) {
            for (UserDTO user : users) {
                if (user.getId().equals(saleOrder.getSalemanId())) {
                    dto.setSalesManName(user.getFullName());
                    break;
                }
            }
        }
        return dto;
    }

    public OrderReturnDetailDTO getSaleOrderChosen(Long id, Long shopId) {
        SaleOrder orderReturn = repository.findById(id).orElseThrow(() -> new ValidateException(ResponseMessage.SALE_ORDER_NOT_FOUND));

        OrderReturnDetailDTO orderReturnDetailDTO = new OrderReturnDetailDTO();
        orderReturnDetailDTO.setInfos(getInfos(orderReturn));
        orderReturnDetailDTO.setProductReturn(getProductReturn(id));
        orderReturnDetailDTO.setPromotionReturn(getPromotionReturn(id));
        List<ApParamDTO> apParamDTOList = apparamClient.getApParamByTypeV1("SALEMT_MASTER_PAY_ITEM").getData();
        List<ReasonReturnDTO> reasons = new ArrayList<>();
        for (ApParamDTO ap : apParamDTOList) {
            ReasonReturnDTO reasonReturnDTO = new ReasonReturnDTO();
            reasonReturnDTO.setApCode(ap.getApParamCode());
            reasonReturnDTO.setApName(ap.getApParamName());
            reasonReturnDTO.setValue(ap.getValue());
            reasons.add(reasonReturnDTO);
        }
        orderReturnDetailDTO.setReasonReturn(reasons);
        return orderReturnDetailDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateReturn(long id, long wareHouse, long shopId) {
        List<SaleOrderDetail> odReturns = saleOrderDetailRepository.findSaleOrderDetail(id, false);
        if(!odReturns.isEmpty()) {
            List<StockTotal> stockTotals1 = stockTotalRepository.getStockTotal(shopId, wareHouse,
                    odReturns.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()));
            for (SaleOrderDetail sad : odReturns) {
                stockTotalService.validateStockTotal(stockTotals1, sad.getProductId(), -sad.getQuantity());
            }
        }

        List<SaleOrderDetail> promotionReturns = saleOrderDetailRepository.findSaleOrderDetail(id, true);
        if (!promotionReturns.isEmpty()) {
            List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, wareHouse,
                    promotionReturns.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()));
            for (SaleOrderDetail sad : promotionReturns) {
                stockTotalService.validateStockTotal(stockTotals, sad.getProductId(), -sad.getQuantity());
            }
        }

        for (SaleOrderDetail sod : odReturns) {
            if (sod.getQuantity() == null) sod.setQuantity(0);
            stockTotalService.updateWithLock(shopId, wareHouse, sod.getProductId(), sod.getQuantity() * -1);
        }

        for (SaleOrderDetail prd : promotionReturns) {
            if (prd.getQuantity() == null) prd.setQuantity(0);
            stockTotalService.updateWithLock(shopId, wareHouse, prd.getProductId(), prd.getQuantity() * -1);
        }
    }

    private void updateZV23(CustomerDTO customer, SaleOrder saleOrder, List<SaleOrderDetail> saleOrderDetails, List<SaleOrderDiscount> discounts) {
        List<SaleOrderDiscount> discountZV23S = discounts.stream().filter(d -> d.getPromotionType().equalsIgnoreCase("ZV23")).collect(Collectors.toList());
        if(discountZV23S.isEmpty()) return ;

        Map<Long, List<SaleOrderDiscount>> zv23MapProducts = discountZV23S.stream().collect(Collectors.groupingBy(SaleOrderDiscount::getPromotionProgramId, LinkedHashMap::new, Collectors.toList()));

        List<RPT_ZV23DTO> rpt_zv23DTOS = promotionClient.findByProgramIdsV1(zv23MapProducts.keySet(), customer.getId()).getData();

        List<SaleOrderDetail> details = saleOrderDetails.stream().filter(s -> s.getIsFreeItem() == null || !s.getIsFreeItem()).collect(Collectors.toList());

        for(RPT_ZV23DTO zv23: rpt_zv23DTOS) {
            // lấy tổng tiền theo những sản phẩm quy định
            Double amountInTax = 0.0;
            List<Long> productIds = zv23MapProducts.get(zv23.getPromotionProgramId()).stream().map(SaleOrderDiscount::getProductId).collect(Collectors.toList());
            for(SaleOrderDetail detail: details) {
                if(productIds.contains(detail.getProductId())) amountInTax += detail.getAmount();
            }
            Double amount =  zv23.getTotalAmount()!=null?zv23.getTotalAmount():0;
            RPT_ZV23Request zv23Request = new RPT_ZV23Request();
            zv23Request.setTotalAmount(amount - amountInTax);
            zv23Request.setShopId(customer.getShopId());
            promotionClient.updateRPTZV23V1(zv23.getId(), zv23Request);
        }

    }

    private double roundValue(Double value){
        if(value == null) return 0;
        return Math.round(value);
    }
}
