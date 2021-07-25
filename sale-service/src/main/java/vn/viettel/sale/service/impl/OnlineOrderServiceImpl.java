package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.convert.XStreamTranslator;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.exception.ApplicationException;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.StringUtils;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.OnlineOrderFilter;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.dto.OnlineOrderDTO;
import vn.viettel.sale.service.dto.OrderProductOnlineDTO;
import vn.viettel.sale.service.feign.*;
import vn.viettel.sale.specification.OnlineOrderSpecification;
import vn.viettel.sale.xml.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class OnlineOrderServiceImpl extends BaseServiceImpl<OnlineOrder, OnlineOrderRepository> implements OnlineOrderService {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Autowired
    OnlineOrderDetailRepository onlineOrderDetailRepo;

    @Autowired
    ShopClient shopClient;

    @Autowired
    CustomerClient customerClient;

    @Autowired
    AreaClient areaClient;

    @Autowired
    ApparamClient apparamClient;

    @Autowired
    CustomerTypeClient customerTypeClient;

    @Autowired
    MemberCustomerClient memberCustomerClient;

    @Autowired
    ProductRepository productRepo;

    @Autowired
    ProductPriceRepository productPriceRepo;

    @Autowired
    StockTotalRepository stockTotalRepo;

    @Autowired
    SaleOrderRepository saleOrderRepository;

    XStreamTranslator xstream = XStreamTranslator.getInstance();

    private Class<?>[] classes = new Class[] { Line.class, DataSet.class, Header.class, NewDataSet.class, NewData.class};

    @Override
    public Page<OnlineOrderDTO> getOnlineOrders(OnlineOrderFilter filter, Pageable pageable) {
        Page<OnlineOrder> onlineOrders = repository.findAll(
                Specification.where(
                        OnlineOrderSpecification.hasOrderNumber(filter.getOrderNumber()))
                        .and(OnlineOrderSpecification.hasShopId(filter.getShopId()))
                        .and(OnlineOrderSpecification.hasSynStatus(filter.getSynStatus()))
                        .and(OnlineOrderSpecification.hasFromDateToDate(filter.getFromDate(), filter.getToDate())
                        ), pageable);
        Page<OnlineOrderDTO> onlineOrderDTOS = onlineOrders.map(this::mapOnlineOrderToOnlineOrderDTO);

        return onlineOrderDTOS;
    }

    @Override
    public OnlineOrderDTO getOnlineOrder(Long id, Long shopId, Long userId) {
        OnlineOrder onlineOrder = repository.findById(id)
                .orElseThrow(() -> new ValidateException(ResponseMessage.ORDER_ONLINE_NOT_FOUND));
        if(onlineOrder.getSynStatus()!=null &&  onlineOrder.getSynStatus()==1)
            throw new ValidateException(ResponseMessage.SALE_ORDER_ALREADY_CREATED);

        List<CustomerDTO> customerDTOS = customerClient.getCustomerByMobiPhoneV1(onlineOrder.getCustomerPhone()).getData();

        if(customerDTOS.isEmpty()) {
            CustomerRequest customerRequest = this.createCustomerRequest(onlineOrder);
            try{
                CustomerDTO customerDTO = customerClient.createForFeignV1(customerRequest, userId,  shopId).getData();
                customerDTOS.add(customerDTO);
            }catch (Exception e){
                throw new ValidateException(ResponseMessage.CUSTOMER_CREATE_FAILED);
            }
        }

        List<OnlineOrderDetail> orderDetails = onlineOrderDetailRepo.findByOnlineOrderId(id);
        OnlineOrderDTO onlineOrderDTO = this.mapOnlineOrderToOnlineOrderDTO(onlineOrder);

        Long warehouseTypeId = customerTypeClient.getWarehouseTypeByShopId(shopId);
        if (warehouseTypeId == null)
            throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);

        List<OrderProductOnlineDTO> products = new ArrayList<>();
        Long customerType = null;
        //TH 1 khách hàng thì load giá SP luôn
        if(customerDTOS.size() == 1) {
            CustomerDTO customer = customerDTOS.get(0);
            customerType = customer.getCustomerTypeId();
        }else{

        }
        for (OnlineOrderDetail detail: orderDetails) {
            OrderProductOnlineDTO productOrder = this.mapOnlineOrderDetailToProductDTO(detail, onlineOrderDTO, customerType, shopId, warehouseTypeId);
            products.add(productOrder);
        }

        onlineOrderDTO.setProducts(products);
        onlineOrderDTO.setCustomers(customerDTOS);

        return onlineOrderDTO;
    }

    @Override
    public String checkOnlineNumber(String code) {
        ApParamDTO apParam = apparamClient.getApParamByCodeV1("NUMDAY_CHECK_ONLNO").getData();
        if(apParam == null) throw new ValidateException(ResponseMessage.AP_PARAM_NOT_EXISTS);
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime daysAgo = date.minusDays(Integer.valueOf(apParam.getValue()));
        List<OnlineOrder> onlineOrders = repository.findAll(Specification.where(OnlineOrderSpecification.equalOrderNumber(code))
                .and(OnlineOrderSpecification.hasFromDateToDate(daysAgo, date)));
        if(!onlineOrders.isEmpty())
            throw new ValidateException(ResponseMessage.ONLINE_NUMBER_IS_EXISTS);
        return code;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncXmlOnlineOrder(InputStream inputStream) throws ApplicationException {

        xstream.processAnnotations(classes);
        xstream.allowTypes(classes);
        DataSet dataSet = (DataSet) xstream.fromXML(inputStream);
        String message = "";

        for(NewDataSet data : dataSet.getLstNewDataSet()){
            try {
                Header header = data.getHeader();
                List<Line> lines = data.getLstLine();

                //check order number
                OnlineOrder check = repository.findByOrderNumber(header.getOrderNumber());
                if(check != null) {
                    message = ResponseMessage.ONLINE_NUMBER_IS_EXISTS.statusCodeValue();
                    continue;
                }

                //online order
                OnlineOrder onlineOrder = new OnlineOrder();
                ShopDTO shopDTO = shopClient.getByShopCode(header.getStoreID()).getData();
                if(shopDTO == null) {
                    message = ResponseMessage.SHOP_NOT_FOUND.statusCodeValue();
                    continue;
                }

                onlineOrder.setShopId(shopDTO.getId());
                onlineOrder.setSynStatus(0);
                onlineOrder.setSourceName(header.getSourceName());
                onlineOrder.setOrderId(header.getOrderID());
                onlineOrder.setOrderNumber(header.getOrderNumber());
                onlineOrder.setCreatedAt(header.getCreatedAt());
                onlineOrder.setTotalLineValue(header.getTotalLineValue());
                onlineOrder.setDiscountCode(header.getDiscountCode());
                onlineOrder.setDiscountValue(header.getDiscountValue());
                onlineOrder.setCustomerName(header.getCustomerName());
                onlineOrder.setCustomerPhone(header.getCustomerPhone());
                if(header.getCustomerAddress().isEmpty())
                    onlineOrder.setCustomerAddress(header.getShippingAddress());
                else
                    onlineOrder.setCustomerAddress(header.getCustomerAddress());
                onlineOrder.setShippingAddress(header.getShippingAddress());
                onlineOrder.setCustomerDOB(header.getCustomerBirthday());
                onlineOrder.setOrderStatus(header.getOrderStatus());
                onlineOrder.setVnmSynStatus(0);
                onlineOrder.setNote(header.getNote());
                Long id = repository.save(onlineOrder).getId();

                //online order detail
                for(Line line : lines){
                    OnlineOrderDetail detail = new OnlineOrderDetail();
                    detail.setOnlineOrderId(100L);
                    detail.setSku(line.getSku());
                    detail.setProductName(line.getProductName());
                    detail.setQuantity(line.getQuantity());
                    detail.setCharacter1Name(line.getCharacter1Name());
                    detail.setCharacter1Value(line.getCharacter1Value());
                    detail.setCharacter2Name(line.getCharacter2Name());
                    detail.setCharacter2Value(line.getCharacter2Value());
                    detail.setCharacter3Name(line.getCharacter3Name());
                    detail.setCharacter3Value(line.getCharacter3Value());
                    detail.setOriginalPrice(line.getOriginalPrice());
                    detail.setRetailsPrice(line.getRetailsPrice());
                    detail.setLineValue(line.getLineValue());
                    detail.setPromotionName(line.getPromotionName());
                    onlineOrderDetailRepo.save(detail);
                }
            }catch (Exception e) {
                message = e.getMessage();
            }
        }
        if(!StringUtils.stringIsNullOrEmpty(message))
            throw new ApplicationException(message);
    }

    @Override
    public void syncXmlToCancelOnlineOrder(InputStream inputStream) throws Exception {
        xstream.processAnnotations(classes);
        xstream.allowTypes(classes);
        DataSet dataSet = (DataSet) xstream.fromXML(inputStream);
        String message = "";

        for(NewDataSet data :  dataSet.getLstNewDataSet()){
            try {
                Header header = data.getHeader();
                if (header != null) {
                    if (header.getOrderNumber() != null) {
                        OnlineOrder onlineOrder = repository.findByOrderNumber(header.getOrderNumber());
                        if (onlineOrder != null) {
                            if (onlineOrder.getSynStatus() == 0) {
                                onlineOrder.setSynStatus(-1);
                                String orderNumber = onlineOrder.getOrderNumber() + "_HUY";
                                onlineOrder.setOrderNumber(orderNumber);
                                repository.save(onlineOrder);
                            }
                        }
                    }
                }
            }catch (Exception e) {
                message = e.getMessage();
            }
        }
        if(!StringUtils.stringIsNullOrEmpty(message))
            throw new ApplicationException(message);
    }

    @Override
    public InputStream exportXmlFile(List<OnlineOrder> onlineOrders) throws Exception {
        xstream.processAnnotations(classes);
        DataSet dataSet = new DataSet();
        List<NewDataSet> newDataSets = new ArrayList<>();
        if(onlineOrders != null)
        {
            for(OnlineOrder onlineOrder : onlineOrders){
                NewDataSet newDataSet = new NewDataSet();
                Header header = new Header();
                header.setOrderNumber(onlineOrder.getOrderNumber());
                header.setOrderID(onlineOrder.getOrderId());

                SaleOrder saleOrder = saleOrderRepository.findById(onlineOrder.getSaleOrderId()).orElse(null);
                if(saleOrder != null)
                {
                    header.setPosOrderNumber(saleOrder.getOrderNumber());
                }
                newDataSet.setHeader(header);
                newDataSets.add(newDataSet);
            }
            dataSet.setLstNewDataSet(newDataSets);
            xstream.toXMLFile(dataSet);
            String xml = xstream.toXML(dataSet);
            return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        }

        return null;
    }

    private CustomerRequest createCustomerRequest(OnlineOrder onlineOrder) {
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCustomerTypeDefaultV1().getData();

        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setFirstName(this.getFirstName(onlineOrder.getCustomerName()));
        customerRequest.setLastName(this.getLastName(onlineOrder.getCustomerName()));
        customerRequest.setMobiPhone(onlineOrder.getCustomerPhone());
        customerRequest.setDob(onlineOrder.getCustomerDOB());
        customerRequest.setCustomerTypeId(customerTypeDTO.getId());
        customerRequest.setStatus(1);
        this.setArea(onlineOrder.getCustomerAddress(), customerRequest);
        return customerRequest;
    }

    private void setArea(String address, CustomerRequest customerRequest ) {
        String[] words = address.split(",");
        int index = words.length -1;
        String provinceName = words[index--].trim();
        String districtName = words[index--].trim();
        String precinctName = words[index--].trim();
        String street = words[index].trim();

        AreaDTO areaDTO = areaClient.getAreaV1(provinceName, districtName, precinctName).getData();
        customerRequest.setAreaId(areaDTO.getId());
        customerRequest.setStreet(street);
    }

    private OrderProductOnlineDTO mapOnlineOrderDetailToProductDTO(OnlineOrderDetail detail, OnlineOrderDTO onlineOrderDTO, Long customerTypeId, Long shopId, Long warehouseTypeId) {
        Product product = productRepo.getProductByProductCodeAndStatus(detail.getSku(), 1)
                .orElseThrow(() -> new ValidateException(ResponseMessage.PRODUCT_NOT_EXISTS, detail.getSku()));

        double price = 0;
        if(customerTypeId != null) {
            List<Price> productPrices = productPriceRepo.findProductPriceWithType(Arrays.asList(product.getId()), customerTypeId, LocalDateTime.now());
            if(productPrices == null || productPrices.isEmpty()) throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);
            price = productPrices.get(0).getPrice();
        }

        List<StockTotal> stockTotals = stockTotalRepo.getStockTotal(shopId, warehouseTypeId, Arrays.asList(product.getId()));
        if(stockTotals == null || stockTotals.isEmpty()) throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderProductOnlineDTO productOrder = modelMapper.map(product, OrderProductOnlineDTO.class);
        productOrder.setProductId(product.getId());
        productOrder.setQuantity(detail.getQuantity());
        productOrder.setPrice(price);
        productOrder.setStockTotal(stockTotals.get(0).getQuantity());

        onlineOrderDTO.addQuantity(productOrder.getQuantity());
        onlineOrderDTO.addTotalPrice(productOrder.getTotalPrice());

        return productOrder;
    }

    private OnlineOrderDTO mapOnlineOrderToOnlineOrderDTO(OnlineOrder order) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OnlineOrderDTO dto = modelMapper.map(order, OnlineOrderDTO.class);
        dto.setOrderInfo(order.getCustomerName() + " - " + order.getCustomerPhone());
        return dto;
    }

    private String getLastName(String fullName) {
        int i = fullName.lastIndexOf(' ');
        return fullName.substring(0, i).trim();
    }

    private String getFirstName(String fullName) {
        int i = fullName.lastIndexOf(' ');
        return fullName.substring(i+1).trim();
    }


}
