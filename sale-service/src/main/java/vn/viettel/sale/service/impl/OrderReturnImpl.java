package vn.viettel.sale.service.impl;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.promotion.PromotionProgramProductDTO;
import vn.viettel.core.dto.promotion.RPT_ZV23DTO;
import vn.viettel.core.messaging.RPT_ZV23Request;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.sale.entities.*;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.StockTotalService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.*;

import java.time.*;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

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
        if (saleOrderFilter.getSearchKeyword() != null || saleOrderFilter.getCustomerPhone() != null) {
            customerIds = customerClient.getIdCustomerByV1(saleOrderFilter.getSearchKeyword(), saleOrderFilter.getCustomerPhone()).getData();
            if (customerIds == null || customerIds.isEmpty()) customerIds = Arrays.asList(-1L);
        }
        if (saleOrderFilter.getOrderNumber() != null)
            saleOrderFilter.setOrderNumber(saleOrderFilter.getOrderNumber().trim().toUpperCase());
        Page<SaleOrder> findAll = repository.getSaleOrderReturn(shopId, saleOrderFilter.getOrderNumber(),
                customerIds, saleOrderFilter.getFromDate(), saleOrderFilter.getToDate(), pageable);
        SaleOrderTotalResponse totalResponse = repository.getSumSaleOrderReturn(shopId, saleOrderFilter.getOrderNumber(),
                customerIds, saleOrderFilter.getFromDate(), saleOrderFilter.getToDate());
        List<UserDTO> users = userClient.getUserByIdsV1(findAll.getContent().stream().map(item -> item.getSalemanId()).distinct()
                .filter(Objects::nonNull).collect(Collectors.toList()));
        List<CustomerDTO> customers = customerClient.getCustomerInfoV1(null, findAll.getContent().stream().map(item -> item.getCustomerId()).distinct()
                .filter(Objects::nonNull).collect(Collectors.toList()));
        List<SaleOrder> saleOrders = repository.findAllById(findAll.getContent().stream().map(item -> item.getFromSaleOrderId()).collect(Collectors.toList()));
        Page<OrderReturnDTO> orderReturnDTOS = findAll.map(item -> mapOrderReturnDTO(item, users, customers, saleOrders));
        CoverResponse coverResponse = new CoverResponse(orderReturnDTOS, totalResponse);
        return coverResponse;
    }

    private OrderReturnDTO mapOrderReturnDTO(SaleOrder orderReturn, List<UserDTO> users, List<CustomerDTO> customers, List<SaleOrder> saleOrders) {
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
        if (users != null) {
            for (UserDTO user : users) {
                if (user.getId().equals(orderReturn.getSalemanId())) {
                    dto.setUserName(user.getFullName());
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
        if (orderReturn.getTotalPromotion() >= 0) {
            dto.setTotalPromotion(orderReturn.getTotalPromotion());
        } else dto.setTotalPromotion(orderReturn.getTotalPromotion() * -1);

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
            if (productReturn.getAutoPromotion() == null && productReturn.getZmPromotion() == null) {
                discount = 0;
            } else if (productReturn.getAutoPromotion() == null || productReturn.getZmPromotion() == null) {
                if (productReturn.getAutoPromotion() == null)
                    discount = productReturn.getZmPromotion();
                if (productReturn.getZmPromotion() == null)
                    discount = productReturn.getAutoPromotion();
            } else {
                discount = productReturn.getAutoPromotion() + productReturn.getZmPromotion();
            }
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
                promotionReturnDTO.setPricePerUnit(0D);
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
    public SaleOrder createOrderReturn(OrderReturnRequest request, Long shopId, String userName) {
        if (request == null)
            throw new ValidateException(ResponseMessage.REQUEST_BODY_NOT_BE_NULL);
        SaleOrder saleOrder = repository.getSaleOrderByNumber(request.getOrderNumber());
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

        newOrderReturn.setOrderNumber(saleService.createOrderNumber(shop)); // important
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
        repository.save(newOrderReturn); //save new orderReturn
        saleOrder.setIsReturn(false);
        repository.save(saleOrder);

        //new orderReturn detail
        List<SaleOrderDetail> saleOrderDetails =
                saleOrderDetailRepository.findSaleOrderDetail(saleOrder.getId(), false);
        SaleOrder orderReturn = repository.getOrderReturnByNumber(newOrderReturn.getOrderNumber());
        if (saleOrderDetails != null) {
            for (SaleOrderDetail saleOrderDetail : saleOrderDetails) {
                NewOrderReturnDetailDTO productReturnDTO = modelMapper.map(saleOrderDetail, NewOrderReturnDetailDTO.class);
                SaleOrderDetail productReturn = modelMapper.map(productReturnDTO, SaleOrderDetail.class);
                productReturn.setSaleOrderId(orderReturn.getId());
                productReturn.setAutoPromotionVat(saleOrderDetail.getAutoPromotionVat());
                productReturn.setOrderDate(returnDate);

                productReturn.setQuantity(saleOrderDetail.getQuantity() * -1);
//                productReturn.setPrice(saleOrderDetail.getPrice() * -1);
                productReturn.setAmount(saleOrderDetail.getAmount() * -1);
                productReturn.setTotal(saleOrderDetail.getTotal() * -1);

                if (saleOrderDetail.getPriceNotVat() != null)
                    productReturn.setPriceNotVat(saleOrderDetail.getPriceNotVat() * -1);
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
            NewOrderReturnDetailDTO promotionReturnDTO = modelMapper.map(promotionDetail, NewOrderReturnDetailDTO.class);
            SaleOrderDetail promotionReturn = modelMapper.map(promotionReturnDTO, SaleOrderDetail.class);
            promotionReturn.setSaleOrderId(orderReturn.getId());
            promotionReturn.setPrice(0D);
            promotionReturn.setAmount(0D);
            promotionReturn.setTotal(0D);
            promotionReturn.setQuantity(promotionDetail.getQuantity() * (-1));
            saleOrderDetailRepository.save(promotionReturn);
        }


        //new orderReturn discount
        List<SaleOrderDiscount> orderReturnDiscount = saleDiscount.findAllBySaleOrderId(saleOrder.getId());
        if (orderReturnDiscount.size() > 0) {
            for (SaleOrderDiscount discount : orderReturnDiscount) {
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
        if (comboDiscounts.size() > 0) {
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

        this.updateZV23(customer, saleOrder, orderReturnDiscount);
        if (saleOrder.getCustomerPurchase() != null)
            saleService.updateCustomer(newOrderReturn, customer, true);
        if (saleOrder.getMemberCardAmount() != null)
            saleService.updateAccumulatedAmount(-saleOrder.getMemberCardAmount(), customer.getId());

        return newOrderReturn;
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
        List<CustomerDTO> customers = customerClient.getCustomerInfoV1(1, saleOrders.stream().map(item -> item.getCustomerId()).distinct().filter(Objects::nonNull).collect(Collectors.toList()));
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

        List<StockTotal> stockTotals1 = stockTotalRepository.getStockTotal(shopId, wareHouse,
                odReturns.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()));
        for (SaleOrderDetail sad : odReturns) {
            stockTotalService.validateStockTotal(stockTotals1, sad.getProductId(), -sad.getQuantity());
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

    private void updateZV23(CustomerDTO customer, SaleOrder saleOrder, List<SaleOrderDiscount> discounts) {
        Set<Long> zV23Ids = new HashSet<>();
        for (SaleOrderDiscount discount: discounts) {
            if (discount.getPromotionType().equalsIgnoreCase("ZV23")) zV23Ids.add(discount.getPromotionProgramId());
        }
        if (zV23Ids.isEmpty()) return;
        List<RPT_ZV23DTO> rpt_zv23DTOS = promotionClient.findByProgramIdsV1(zV23Ids, customer.getId()).getData();
        for(RPT_ZV23DTO zv23: rpt_zv23DTOS) {
            Double amount =  zv23.getTotalAmount()!=null?zv23.getTotalAmount():0;
            Double cusPurchase = saleOrder.getCustomerPurchase()!=null?saleOrder.getCustomerPurchase():0;
            RPT_ZV23Request zv23Request = new RPT_ZV23Request();
            zv23Request.setTotalAmount(amount - cusPurchase);
            promotionClient.updateRPTZV23V1(zv23.getId(), zv23Request);
        }
    }

    private double roundValue(Double value){
        if(value == null) return 0;
        return Math.round(value);
    }
}
