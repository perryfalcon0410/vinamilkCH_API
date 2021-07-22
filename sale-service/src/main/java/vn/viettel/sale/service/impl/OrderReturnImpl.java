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
        List<Long> customerIds = customerClient.getIdCustomerByV1(saleOrderFilter.getSearchKeyword(), saleOrderFilter.getCustomerPhone()).getData();
        if(customerIds.size() == 0) {
            return new CoverResponse<>(new PageImpl<>(new ArrayList<>()), new SaleOrderTotalResponse());
        }
        if(saleOrderFilter.getOrderNumber() != null) saleOrderFilter.setOrderNumber(saleOrderFilter.getOrderNumber().trim().toUpperCase());
        Page<SaleOrder> findAll = repository.getSaleOrderReturn(shopId,saleOrderFilter.getOrderNumber(),
                customerIds, saleOrderFilter.getFromDate(), saleOrderFilter.getToDate(), pageable);
        SaleOrderTotalResponse totalResponse = repository.getSumSaleOrderReturn(shopId,saleOrderFilter.getOrderNumber(),
                customerIds, saleOrderFilter.getFromDate(), saleOrderFilter.getToDate());
        List<UserDTO> users = userClient.getUserByIdsV1(findAll.getContent().stream().map(item -> item.getSalemanId()).distinct()
        .filter(Objects::nonNull).collect(Collectors.toList()));
        List<CustomerDTO> customers = customerClient.getCustomerInfoV1(null, customerIds);
        List<SaleOrder> saleOrders = repository.findAllById(findAll.getContent().stream().map(item -> item.getFromSaleOrderId()).collect(Collectors.toList()));
        Page<OrderReturnDTO> orderReturnDTOS = findAll.map(item ->mapOrderReturnDTO(item, users, customers, saleOrders));
        CoverResponse coverResponse = new CoverResponse(orderReturnDTOS, totalResponse);
        return coverResponse;
    }

    private OrderReturnDTO mapOrderReturnDTO(SaleOrder orderReturn, List<UserDTO> users, List<CustomerDTO> customers, List<SaleOrder> saleOrders) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderReturnDTO dto = modelMapper.map(orderReturn, OrderReturnDTO.class);
        if(saleOrders != null){
            for(SaleOrder saleOrder : saleOrders){
                if(saleOrder.getId().equals(orderReturn.getFromSaleOrderId())){
                    dto.setOrderNumberRef(saleOrder.getOrderNumber());
                    break;
                }
            }
        }
        if(users != null){
            for(UserDTO user : users){
                if(user.getId().equals(orderReturn.getSalemanId())){
                    dto.setUserName(user.getFullName());
                    break;
                }
            }
        }
        if(customers != null){
            for(CustomerDTO customer : customers){
                if(customer.getId().equals(orderReturn.getCustomerId())){
                    dto.setCustomerNumber(customer.getCustomerCode());
                    dto.setCustomerName(customer.getFullName());
                    break;
                }
            }
        }
        if(orderReturn.getTotalPromotion() >= 0){
            dto.setTotalPromotion(orderReturn.getTotalPromotion());
        }else dto.setTotalPromotion(orderReturn.getTotalPromotion() * -1);

        dto.setAmount(orderReturn.getAmount() * -1);
        dto.setTotal(orderReturn.getTotal() * -1);
        dto.setDateReturn(orderReturn.getOrderDate());
        return dto;
    }

    @Override
    public OrderReturnDetailDTO getOrderReturnDetail(Long orderReturnId) {
        OrderReturnDetailDTO orderReturnDetailDTO = new OrderReturnDetailDTO();
        orderReturnDetailDTO.setInfos(getInfos(orderReturnId));
        orderReturnDetailDTO.setProductReturn(getProductReturn(orderReturnId));
        orderReturnDetailDTO.setPromotionReturn(getPromotionReturn(orderReturnId));
        return orderReturnDetailDTO;
    }

    private InfosReturnDetailDTO getInfos(long orderReturnId){
        InfosReturnDetailDTO infosReturnDetailDTO = new InfosReturnDetailDTO();
        SaleOrder orderReturn = repository.findById(orderReturnId).get();
        if (orderReturn.getFromSaleOrderId() != null) {
            SaleOrder saleOrder = repository.findById(orderReturn.getFromSaleOrderId()).get();
            infosReturnDetailDTO.setOrderDate(saleOrder.getOrderDate()); //order date
        }
        CustomerDTO customer =
                customerClient.getCustomerByIdV1(orderReturn.getCustomerId()).getData();
        infosReturnDetailDTO.setCustomerName(customer.getLastName()+" "+customer.getFirstName());
        ApParamDTO apParamDTO = new ApParamDTO();
        if(orderReturn.getReasonId()!= null) {
             apParamDTO = apparamClient.getApParamByCodeV1(orderReturn.getReasonId()).getData();
        }
        infosReturnDetailDTO.setReason(apParamDTO.getApParamName());
        infosReturnDetailDTO.setReasonDesc(orderReturn.getReasonDesc());
        infosReturnDetailDTO.setReturnDate(orderReturn.getOrderDate()); //order return
        infosReturnDetailDTO.setReturnNumber(orderReturn.getOrderNumber());
        UserDTO user = userClient.getUserByIdV1(orderReturn.getSalemanId());
        infosReturnDetailDTO.setUserName(user.getLastName()+" "+user.getFirstName());
        return  infosReturnDetailDTO;
    }

    private CoverResponse<List<ProductReturnDTO>,TotalOrderReturnDetail> getProductReturn(long orderReturnId) {
        List<SaleOrderDetail> productReturns = saleOrderDetailRepository.findSaleOrderDetail(orderReturnId, false);
        if(productReturns.size() == 0) return null;
        List<ProductReturnDTO> productReturnDTOList = new ArrayList<>();
        List<Product> products  = productRepository.getProducts(productReturns.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()), null);
        TotalOrderReturnDetail totalResponse = new TotalOrderReturnDetail();
        for (SaleOrderDetail productReturn:productReturns ) {
            ProductReturnDTO productReturnDTO = new ProductReturnDTO();
            if(products != null){
                for(Product product : products){
                    if(product.getId().equals(productReturn.getProductId())){
                        productReturnDTO.setProductCode(product.getProductCode());
                        productReturnDTO.setProductName(product.getProductName());
                        productReturnDTO.setUnit(product.getUom1());
                        break;
                    }
                }
            }
            productReturnDTO.setPricePerUnit(productReturn.getPrice());
            if(productReturn.getAutoPromotion() == null && productReturn.getZmPromotion() == null){
                productReturnDTO.setDiscount(0D);
            }
            else if(productReturn.getAutoPromotion() == null || productReturn.getZmPromotion() == null) {
                if(productReturn.getAutoPromotion() == null)
                    productReturnDTO.setDiscount(productReturn.getZmPromotion());
                if(productReturn.getZmPromotion() == null)
                    productReturnDTO.setDiscount(productReturn.getAutoPromotion());
            }else {
                double discount = productReturn.getAutoPromotion() + productReturn.getZmPromotion();
                productReturnDTO.setDiscount(discount);
            }
            if(productReturn.getQuantity() < 0) {
                productReturnDTO.setTotalPrice(productReturn.getAmount() * -1);
                productReturnDTO.setQuantity(productReturn.getQuantity() * -1);
                productReturnDTO.setPaymentReturn(productReturn.getTotal() * -1);
                productReturnDTO.setPricePerUnit(productReturn.getPrice() * -1);
            } else {
                productReturnDTO.setTotalPrice(productReturn.getAmount());
                productReturnDTO.setQuantity(productReturn.getQuantity());
                productReturnDTO.setPaymentReturn(productReturn.getTotal());
                productReturnDTO.setPricePerUnit(productReturn.getPrice());
            }
            productReturnDTOList.add(productReturnDTO);
            totalResponse.setTotalQuantity(totalResponse.getTotalQuantity() + productReturnDTO.getQuantity());
            totalResponse.setTotalAmount(totalResponse.getTotalAmount() + productReturnDTO.getTotalPrice());
            totalResponse.setTotalDiscount(totalResponse.getTotalDiscount() + productReturnDTO.getDiscount());
            totalResponse.setAllTotal(totalResponse.getAllTotal() + productReturnDTO.getPaymentReturn());
        }

        CoverResponse<List<ProductReturnDTO>,TotalOrderReturnDetail> coverResponse =
                new CoverResponse<>(productReturnDTOList,totalResponse);
        return coverResponse;
    }

    private CoverResponse<List<PromotionReturnDTO>,TotalOrderReturnDetail> getPromotionReturn(long orderReturnId) {
        List<SaleOrderDetail> promotionReturns = saleOrderDetailRepository.findSaleOrderDetail(orderReturnId, true);
        List<PromotionReturnDTO> promotionReturnsDTOList = new ArrayList<>();
        TotalOrderReturnDetail totalResponse = new TotalOrderReturnDetail();
        if(!promotionReturns.isEmpty()) {
            List<Product> products  = productRepository.getProducts(promotionReturns.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()), null);
            for (SaleOrderDetail promotionReturn:promotionReturns) {
                PromotionReturnDTO promotionReturnDTO = new PromotionReturnDTO();
                if(products != null){
                    for(Product product : products){
                        if(product.getId().equals(promotionReturn.getProductId())){
                            promotionReturnDTO.setProductCode(product.getProductCode());
                            promotionReturnDTO.setProductName(product.getProductName());
                            promotionReturnDTO.setUnit(product.getUom1());
                            break;
                        }
                    }
                }
                if(promotionReturn.getQuantity() < 0){
                    promotionReturnDTO.setQuantity(promotionReturn.getQuantity() * (-1));
                }else {
                    promotionReturnDTO.setQuantity(promotionReturn.getQuantity());
                }
                promotionReturnDTO.setPricePerUnit(0D);
                promotionReturnDTO.setPaymentReturn(0D);
                promotionReturnsDTOList.add(promotionReturnDTO);
                totalResponse.setTotalQuantity(totalResponse.getTotalQuantity() + promotionReturnDTO.getQuantity());
                totalResponse.setAllTotal(totalResponse.getAllTotal() + promotionReturnDTO.getPaymentReturn());
            }
        }
        CoverResponse<List<PromotionReturnDTO>,TotalOrderReturnDetail> coverResponse =
                new CoverResponse<>(promotionReturnsDTOList,totalResponse);
        return coverResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SaleOrder createOrderReturn(OrderReturnRequest request, Long shopId, String userName) {
        if (request == null)
            throw new ValidateException(ResponseMessage.REQUEST_BODY_NOT_BE_NULL);
        SaleOrder saleOrder = repository.getSaleOrderByNumber(request.getOrderNumber());
        if(saleOrder == null)
            throw new ValidateException(ResponseMessage.ORDER_RETURN_DOES_NOT_EXISTS);
        CustomerDTO customer = customerClient.getCustomerByIdV1(saleOrder.getCustomerId()).getData();
        if(customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        Integer check = repository.checkIsReturn(saleOrder.getId());
        if(check != null && check > 0) throw new ValidateException(ResponseMessage.SALE_ORDER_HAS_ALREADY_RETURNED);
        List<SaleOrderDetail> saleOrderPromotions =
                saleOrderDetailRepository.findSaleOrderDetail(saleOrder.getId(), true);
        if(saleOrder.getIsReturn() != null && !saleOrder.getIsReturn()) throw new ValidateException(ResponseMessage.SALE_ORDER_CANNOT_RETURN);

        LocalDateTime orderDate = DateUtils.convertToDate(saleOrder.getOrderDate());
        LocalDateTime returnDate = DateUtils.convertToDate(new Date());
        Duration dur = Duration.between(orderDate, returnDate);
        double diff = dur.toMillis();
//        double diff = returnDate.getTime() - orderDate.getTime();
        double diffDays = diff / (24 * 60 * 60 * 1000);
        /*
        Nếu cửa hàng không khai báo thì lấy cấu hình theo cấp cha của cửa hàng đó
            huonglx2: Ngoài ra nếu tất cả các cấp đơn vị đều không khai báo tham số   thì fix là 1 ngày.
         */
        String dayString = shopClient.dayReturn(shopId).getData();
        int dayReturn = Integer.parseInt(dayString);
        SaleOrder newOrderReturn = new SaleOrder();
        if(diffDays <= dayReturn) {
            int day = returnDate.getDayOfMonth();
            int month = returnDate.getMonthValue();
            String  year = Integer.toString(returnDate.getYear()).substring(2);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            NewOrderReturnDTO newOrderReturnDTO = modelMapper.map(saleOrder, NewOrderReturnDTO.class);
            newOrderReturn =  modelMapper.map(newOrderReturnDTO, SaleOrder.class);
            String orderNumber = createOrderReturnNumber(saleOrder.getShopId(), day, month, year);
            newOrderReturn.setOrderNumber(orderNumber); // important
            newOrderReturn.setType(2);
            newOrderReturn.setTotalCustomerPurchase(saleOrder.getTotalCustomerPurchase());
            newOrderReturn.setCustomerPurchase(saleOrder.getCustomerPurchase());
            newOrderReturn.setFromSaleOrderId(saleOrder.getId());
            newOrderReturn.setReasonId(request.getReasonId());
            newOrderReturn.setReasonDesc(request.getReasonDescription());

            if(saleOrder.getAmount()!=null) newOrderReturn.setAmount(saleOrder.getAmount() * -1);
            if(saleOrder.getTotalPromotion()!=null) newOrderReturn.setTotalPromotion(saleOrder.getTotalPromotion() * -1);
            if(saleOrder.getTotal()!=null) newOrderReturn.setTotal(saleOrder.getTotal() * -1);
            if(saleOrder.getTotalPaid()!=null) newOrderReturn.setTotalPaid(saleOrder.getTotalPaid() * -1);
            if(saleOrder.getBalance()!=null) newOrderReturn.setBalance(saleOrder.getBalance() * -1);
            if(saleOrder.getMemberCardAmount()!=null) newOrderReturn.setMemberCardAmount(saleOrder.getMemberCardAmount() * -1);
            if(saleOrder.getTotalVoucher()!=null) newOrderReturn.setTotalVoucher(saleOrder.getTotalVoucher() * -1);
            if(saleOrder.getAutoPromotionNotVat()!=null) newOrderReturn.setAutoPromotionNotVat(saleOrder.getAutoPromotionNotVat() * -1);
            if(saleOrder.getAutoPromotionVat()!=null) newOrderReturn.setAutoPromotionVat(saleOrder.getAutoPromotionVat() * -1);
            if(saleOrder.getAutoPromotion()!=null) newOrderReturn.setAutoPromotion(saleOrder.getAutoPromotion() * -1);
            if(saleOrder.getZmPromotion()!=null) newOrderReturn.setZmPromotion(saleOrder.getZmPromotion() * -1);
            if(saleOrder.getTotalPromotionNotVat()!=null) newOrderReturn.setTotalPromotionNotVat(saleOrder.getTotalPromotionNotVat() * -1);
            if(saleOrder.getCustomerPurchase()!=null) newOrderReturn.setCustomerPurchase(saleOrder.getCustomerPurchase() * -1);
            if(saleOrder.getDiscountCodeAmount()!=null) newOrderReturn.setDiscountCodeAmount(saleOrder.getDiscountCodeAmount() * -1);
            if(saleOrder.getTotalCustomerPurchase()!=null) newOrderReturn.setTotalCustomerPurchase(saleOrder.getTotalCustomerPurchase() * -1);
            if(saleOrder.getTotalPromotionVat()!=null) newOrderReturn.setTotalPromotionVat(saleOrder.getTotalPromotionVat() * -1);
            if(saleOrder.getZmPromotionVat()!=null) newOrderReturn.setZmPromotionVat(saleOrder.getZmPromotionVat() * -1);
            if(saleOrder.getZmPromotionNotVat()!=null) newOrderReturn.setZmPromotionNotVat(saleOrder.getZmPromotionNotVat() * -1);
            newOrderReturn.setCreatedAt(LocalDateTime.now());
            newOrderReturn.setOrderDate(LocalDateTime.now());
            repository.save(newOrderReturn); //save new orderReturn
            saleOrder.setIsReturn(false);
            repository.save(saleOrder);

            //new orderReturn detail
            List<SaleOrderDetail> saleOrderDetails =
                    saleOrderDetailRepository.findSaleOrderDetail(saleOrder.getId(), false);
//            if(saleOrderDetails.size() <= 0) throw new ValidateException(ResponseMessage.SALE_ORDER_DOES_NOT_HAVE_PRODUCT);
            SaleOrder orderReturn = repository.getOrderReturnByNumber(newOrderReturn.getOrderNumber());
            if(saleOrderDetails != null)
            for(SaleOrderDetail saleOrderDetail:saleOrderDetails) {
                NewOrderReturnDetailDTO productReturnDTO = modelMapper.map(saleOrderDetail, NewOrderReturnDetailDTO.class);
                SaleOrderDetail productReturn = modelMapper.map(productReturnDTO, SaleOrderDetail.class);
                productReturn.setSaleOrderId(orderReturn.getId());
                productReturn.setAutoPromotionVat(saleOrderDetail.getAutoPromotionVat());
                productReturn.setOrderDate(LocalDateTime.now());

                productReturn.setQuantity(saleOrderDetail.getQuantity() * -1);
//                productReturn.setPrice(saleOrderDetail.getPrice() * -1);
                productReturn.setAmount(saleOrderDetail.getAmount() * -1);
                productReturn.setTotal(saleOrderDetail.getTotal() * -1);

                if(saleOrderDetail.getPriceNotVat()!=null) productReturn.setPriceNotVat(saleOrderDetail.getPriceNotVat() * -1);
                if(saleOrderDetail.getAutoPromotionNotVat()!=null) productReturn.setAutoPromotionNotVat(saleOrderDetail.getAutoPromotionNotVat() * -1);
                if(saleOrderDetail.getAutoPromotionVat()!=null) productReturn.setAutoPromotionVat(saleOrderDetail.getAutoPromotionVat() * -1);
                if(saleOrderDetail.getZmPromotionNotVat()!=null) productReturn.setZmPromotionNotVat(saleOrderDetail.getZmPromotionNotVat() * -1);
                if(saleOrderDetail.getZmPromotionVat()!=null) productReturn.setZmPromotionVat(saleOrderDetail.getZmPromotionVat() * -1);
                if(saleOrderDetail.getAutoPromotion()!=null) productReturn.setAutoPromotion(saleOrderDetail.getAutoPromotion() * -1);
                if(saleOrderDetail.getZmPromotion()!=null) productReturn.setZmPromotion(saleOrderDetail.getZmPromotion() * -1);
                saleOrderDetailRepository.save(productReturn); //save new orderReturn detail
            }

            //new orderReturn promotion
            for(SaleOrderDetail promotionDetail:saleOrderPromotions) {
                NewOrderReturnDetailDTO promotionReturnDTO = modelMapper.map(promotionDetail, NewOrderReturnDetailDTO.class);
                SaleOrderDetail promotionReturn = modelMapper.map(promotionReturnDTO, SaleOrderDetail.class);
                promotionReturn.setSaleOrderId(orderReturn.getId());
                promotionReturn.setPrice(0D);
                promotionReturn.setAmount(0D);
                promotionReturn.setTotal(0D);
                promotionReturn.setQuantity(promotionDetail.getQuantity() * (-1));
                saleOrderDetailRepository.save(promotionReturn);
            }
            updateZV23(saleOrder.getCustomerId(),shopId, saleOrderPromotions, saleOrder.getId());

            //new orderReturn discount
            List<SaleOrderDiscount> orderReturnDiscount = saleDiscount.findAllBySaleOrderId(saleOrder.getId());
            if(orderReturnDiscount.size() > 0) {
                for(SaleOrderDiscount discount:orderReturnDiscount){
                    OrderDiscountReturnDTO returnDiscountDTO = modelMapper.map(discount, OrderDiscountReturnDTO.class);
                    SaleOrderDiscount returnDiscount = modelMapper.map(returnDiscountDTO, SaleOrderDiscount.class);
                    returnDiscount.setDiscountAmount(discount.getDiscountAmount() * -1);
                    returnDiscount.setDiscountAmountVat(discount.getDiscountAmountVat() * -1);
                    returnDiscount.setDiscountAmountNotVat(discount.getDiscountAmountNotVat() * -1);
                    returnDiscount.setMaxDiscountAmount(discount.getMaxDiscountAmount() * -1);
                    returnDiscount.setOrderDate(newOrderReturn.getCreatedAt());
                    returnDiscount.setSaleOrderId(newOrderReturn.getId());
                    saleDiscount.save(returnDiscount);
                }
            }

            List<SaleOrderComboDiscount> comboDiscounts = SaleComboDiscount.findAllBySaleOrderId(saleOrder.getId());
            if(comboDiscounts.size() > 0) {
                for(SaleOrderComboDiscount comboDiscount:comboDiscounts) {
                    SaleComboDiscountDTO returnComboDiscountDTO = modelMapper.map(comboDiscount, SaleComboDiscountDTO.class);
                    SaleOrderComboDiscount returnComboDiscount = modelMapper.map(returnComboDiscountDTO, SaleOrderComboDiscount.class);
                    if(comboDiscount.getDiscountAmount()!= null) returnComboDiscount.setDiscountAmount(comboDiscount.getDiscountAmount() * -1);
                    if(comboDiscount.getDiscountAmountNotVat()!= null) returnComboDiscount.setDiscountAmountNotVat(comboDiscount.getDiscountAmountNotVat() * -1);
                    if(comboDiscount.getDiscountAmountVat()!= null) returnComboDiscount.setDiscountAmountVat(comboDiscount.getDiscountAmountVat() * -1);
                    returnComboDiscount.setOrderDate(newOrderReturn.getCreatedAt());
                    returnComboDiscount.setSaleOrderId(newOrderReturn.getId());
                    SaleComboDiscount.save(returnComboDiscount);
                }
            }

            List<SaleOrderComboDetail> comboDetails = SaleComboDetail.findAllBySaleOrderId(saleOrder.getId());
            if(comboDiscounts.size() > 0) {
                for(SaleOrderComboDetail comboDetail:comboDetails) {
                    SaleComboDetailDTO returnComboDetailDTO = modelMapper.map(comboDetail, SaleComboDetailDTO.class);
                    SaleOrderComboDetail returnComboDetail = modelMapper.map(returnComboDetailDTO, SaleOrderComboDetail.class);
                    returnComboDetail.setComboQuantity(comboDetail.getComboQuantity() * -1);
                    returnComboDetail.setQuantity(comboDetail.getQuantity() * -1);
//                    returnComboDetail.setPrice(comboDetail.getPrice() * -1);
                    returnComboDetail.setPriceNotVat(comboDetail.getPriceNotVat() * -1);
                    returnComboDetail.setAmount(comboDetail.getAmount() * -1);
                    returnComboDetail.setTotal(comboDetail.getTotal() * -1);
                    if(comboDetail.getAutoPromotion()!=null) returnComboDetail.setAutoPromotion(comboDetail.getAutoPromotion() * -1);
                    if(comboDetail.getAutoPromotionVat()!=null) returnComboDetail.setAutoPromotionVat(comboDetail.getAutoPromotionVat() * -1);
                    if(comboDetail.getAutoPromotionNotVat()!=null) returnComboDetail.setAutoPromotionNotVat(comboDetail.getAutoPromotionNotVat() * -1);
                    if(comboDetail.getZmPromotion()!=null) returnComboDetail.setZmPromotion(comboDetail.getZmPromotion() * -1);
                    if(comboDetail.getZmPromotionVat()!=null) returnComboDetail.setZmPromotionVat(comboDetail.getZmPromotionVat() * -1);
                    if(comboDetail.getZmPromotionNotVat()!=null) returnComboDetail.setZmPromotionNotVat(comboDetail.getZmPromotionNotVat() * -1);
                    returnComboDetail.setSaleOrderId(newOrderReturn.getId());
                    returnComboDetail.setOrderDate(newOrderReturn.getCreatedAt());
                    SaleComboDetail.save(returnComboDetail);
                }
            }
            updateReturn(newOrderReturn.getId(), newOrderReturn.getWareHouseTypeId(),shopId);
            if(saleOrder.getCustomerPurchase() != null)
                saleService.updateCustomer(newOrderReturn, customer, true);
            if(saleOrder.getMemberCardAmount() != null)
                saleService.updateAccumulatedAmount(-saleOrder.getMemberCardAmount(), customer.getId());
        }else {
            throw new ValidateException(ResponseMessage.ORDER_EXPIRED_FOR_RETURN);
        }
        return newOrderReturn;
    }

    public CoverResponse<List<SaleOrderDTO>,TotalOrderChoose> getSaleOrderForReturn(SaleOrderChosenFilter filter, Long shopId) {
        String orderNumber = StringUtils.defaultIfBlank(filter.getOrderNumber(), StringUtils.EMPTY);
        String upperCaseON = VNCharacterUtils.removeAccent(orderNumber.toUpperCase(Locale.ROOT));
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        String stringDayReturn = shopClient.dayReturn(shopId).getData();
        if(stringDayReturn == null) throw new ValidateException(ResponseMessage.SHOP_DOES_HAVE_DAY_RETURN);
        int dayReturn = Integer.parseInt(shopClient.dayReturn(shopId).getData());
        LocalDateTime newFromDate = DateUtils.convertFromDate(LocalDateTime.now().minusDays(dayReturn));
        LocalDateTime fromDate = DateUtils.convertFromDate(filter.getFromDate());
        LocalDateTime toDate = DateUtils.convertToDate(filter.getToDate());
        String keyUpper = "";
        if (filter.getProduct() != null){
            keyUpper = VNCharacterUtils.removeAccent(filter.getProduct().trim()).toUpperCase(Locale.ROOT);
        }
        List<Long> customerIds = null;
        if(filter.getSearchKeyword() != null) {
            customerIds = customerClient.getIdCustomerBySearchKeyWordsV1(filter.getSearchKeyword().trim()).getData();
            if(customerIds == null || customerIds.isEmpty()) customerIds = Arrays.asList(-1L);
        }
        List<SaleOrder> saleOrders = repository.getSaleOrderForReturn(shopId,upperCaseON, customerIds, keyUpper, fromDate,toDate,newFromDate);
        if(saleOrders.size() == 0) throw new ValidateException(ResponseMessage.ORDER_FOR_RETURN_NOT_FOUND);
        Collections.sort(saleOrders, Comparator.comparing(SaleOrder::getOrderDate, Comparator.reverseOrder())
                .thenComparing(SaleOrder::getOrderNumber));

        List<SaleOrderDTO> list = new ArrayList<>();
        SaleOrderTotalResponse totalResponse = new SaleOrderTotalResponse();
        List<UserDTO> users = userClient.getUserByIdsV1(saleOrders.stream().map(item -> item.getSalemanId()).distinct().filter(Objects::nonNull).collect(Collectors.toList()));
        List<CustomerDTO> customers = customerClient.getCustomerInfoV1(1, saleOrders.stream().map(item -> item.getCustomerId()).distinct().filter(Objects::nonNull).collect(Collectors.toList()));
        for(SaleOrder saleOrder:saleOrders) {
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
        if(customers != null){
            for(CustomerDTO customer : customers){
                if(customer.getId().equals(saleOrder.getCustomerId())){
                    dto.setCustomerNumber(customer.getCustomerCode());
                    dto.setCustomerName(customer.getFullName());
                    break;
                }
            }
        }
        if(users != null){
            for(UserDTO user : users){
                if(user.getId().equals(saleOrder.getSalemanId())){
                    dto.setSalesManName(user.getFullName());
                    break;
                }
            }
        }
        return dto;
    }

    public OrderReturnDetailDTO getSaleOrderChosen(long id) {
        OrderReturnDetailDTO orderReturnDetailDTO = new OrderReturnDetailDTO();
        orderReturnDetailDTO.setInfos(getInfos(id));
        orderReturnDetailDTO.setProductReturn(getProductReturn(id));
        orderReturnDetailDTO.setPromotionReturn(getPromotionReturn(id));
        List<ApParamDTO> apParamDTOList = apparamClient.getApParamByTypeV1("SALEMT_MASTER_PAY_ITEM").getData();
        List<ReasonReturnDTO> reasons = new ArrayList<>();
        for(ApParamDTO ap:apParamDTOList) {
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
    public void updateReturn(long id, long wareHouse, long shopId){
        List<SaleOrderDetail> odReturns = saleOrderDetailRepository.findSaleOrderDetail(id, false);

        for(SaleOrderDetail sod:odReturns) {
            if(sod.getQuantity() == null) sod.setQuantity(0);
            stockTotalService.updateWithLock(shopId, wareHouse, sod.getProductId(), sod.getQuantity() * -1);
        }
        List<SaleOrderDetail> promotionReturns = saleOrderDetailRepository.findSaleOrderDetail(id, true);
        for(SaleOrderDetail prd:promotionReturns) {
            if(prd.getQuantity() == null) prd.setQuantity(0);
            stockTotalService.updateWithLock(shopId, wareHouse, prd.getProductId(), prd.getQuantity() * -1);
        }
    }

    private void updateZV23(Long customerId, Long shopId, List<SaleOrderDetail> products, Long saleOderId){
        List<Long> idsProduct = new ArrayList<>();
        Map<Long,List<SaleOrderDetail>> groupIds = products.stream().collect(Collectors.groupingBy(SaleOrderDetail::getProductId));
        for(Map.Entry<Long, List<SaleOrderDetail>> entry : groupIds.entrySet()) {
            idsProduct.add(entry.getKey());
        }
        List<Long> idsRejected = new ArrayList<>();
        List<PromotionProgramProductDTO> productRejected =  promotionClient.findByPromotionIdsV1(idsProduct).getData();
        if(productRejected != null && !productRejected.isEmpty()) {
            for(PromotionProgramProductDTO ids:productRejected){
                idsRejected.add(ids.getProductId());
            }
            List<SaleOrderDetail> promotionProduct = saleOrderDetailRepository.detailNotHaveRejected(saleOderId,true,idsRejected);
            RPT_ZV23Request zv23Request = new RPT_ZV23Request();
            for(SaleOrderDetail detail:promotionProduct){
                RPT_ZV23DTO rpt_zv23DTO = promotionClient.checkZV23RequireV1(detail.getPromotionCode(),customerId,shopId).getData();
                if(rpt_zv23DTO != null) {
                    rpt_zv23DTO.setTotalAmount(rpt_zv23DTO.getTotalAmount() - detail.getAmount());
                    promotionClient.updateRPTZV23V1(rpt_zv23DTO.getId(), zv23Request);
                }
            }
        }
    }

//    @Transactional(rollbackFor = Exception.class)
//    public void stockIn(StockTotal stockTotal, int quantity) {
//        stockTotal.setQuantity(stockTotal.getQuantity() + (quantity * (-1)));
//        stockTotalRepository.save(stockTotal);
//    }

    private String createOrderReturnNumber(Long shopId, int day, int month, String year) {
        ShopDTO shop = shopClient.getByIdV1(shopId).getData();
        String shopCode = shop.getShopCode();
        LocalDateTime now = DateUtils.convertDateToLocalDateTime(new Date());
        LocalDateTime start =  DateUtils.convertFromDate(now);
        LocalDateTime end =  DateUtils.convertToDate(now);
        int STT = repository.countSaleOrder(start,end,shopId) + 1;
        return  "SAL." +  shopCode + year + Integer.toString(month + 100).substring(1)  + Integer.toString(day + 100).substring(1) + Integer.toString(STT + 10000).substring(1);
    }
}
