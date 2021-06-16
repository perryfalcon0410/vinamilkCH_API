package vn.viettel.sale.service.impl;

import org.joda.time.DateTime;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.viettel.core.convert.XStreamTranslator;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.dto.customer.RptCusMemAmountDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.OnlineOrderFilter;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.dto.OnlineOrderDTO;
import vn.viettel.sale.service.dto.OrderProductOnlineDTO;
import vn.viettel.sale.service.feign.*;
import vn.viettel.sale.specification.OnlineOrderSpecification;
import vn.viettel.sale.xml.*;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
        if (filter.getFromDate() == null || filter.getToDate() == null) {
            LocalDate initial = LocalDate.now();
            Date fromDate = Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            filter.setFromDate(Instant.ofEpochMilli(fromDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
            filter.setToDate(LocalDateTime.now());
        }

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

        CustomerDTO customerDTO = customerClient.getCustomerByMobiPhoneV1(onlineOrder.getCustomerPhone()).getData();

        if(customerDTO==null) {
            CustomerRequest customerRequest = this.createCustomerRequest(onlineOrder);
            try{
                customerDTO = customerClient.createForFeignV1(customerRequest, userId,  shopId).getData();
            }catch (Exception e){

                throw new ValidateException(ResponseMessage.CUSTOMER_CREATE_FAILED);
            }
        }else{
            RptCusMemAmountDTO rptCusMemAmountDTO = memberCustomerClient.findByCustomerIdV1(customerDTO.getId()).getData();
            if(rptCusMemAmountDTO != null && rptCusMemAmountDTO.getScore()!=null) {
                customerDTO.setScoreCumulated(rptCusMemAmountDTO.getScore());
                customerDTO.setAmountCumulated(rptCusMemAmountDTO.getScore()*100D);
            }

        }

        List<OnlineOrderDetail> orderDetails = onlineOrderDetailRepo.findByOnlineOrderId(id);
        OnlineOrderDTO onlineOrderDTO = this.mapOnlineOrderToOnlineOrderDTO(onlineOrder);

        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if(customerTypeDTO == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);

        List<OrderProductOnlineDTO> products = new ArrayList<>();
        for (OnlineOrderDetail detail: orderDetails) {
            OrderProductOnlineDTO productOrder = this.mapOnlineOrderDetailToProductDTO(
                detail, onlineOrderDTO, customerDTO.getCustomerTypeId(), shopId, customerTypeDTO.getWareHouseTypeId());
            products.add(productOrder);
        }
        onlineOrderDTO.setProducts(products);
        onlineOrderDTO.setCustomer(customerDTO);

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
    public DataSet syncXmlOnlineOrder(MultipartFile file) throws IOException {

        xstream.processAnnotations(classes);
        xstream.allowTypes(classes);
        DataSet dataSet = (DataSet) xstream.fromXML(file.getInputStream());

        List<NewDataSet> dataSets = dataSet.getLstNewDataSet();
        dataSets.stream().forEach(data->{
            Header header = data.getHeader();
            List<Line> lines = data.getLstLine();

            //check order number
            OnlineOrder check = repository.findByOrderNumber(header.getOrderNumber());
            if(check != null)
                throw new ValidateException(ResponseMessage.ONLINE_NUMBER_IS_EXISTS);

            //online order
            OnlineOrder onlineOrder = new OnlineOrder();
            ShopDTO shopDTO = shopClient.getByShopCode(header.getStoreID()).getData();
            if(shopDTO != null)
                onlineOrder.setShopId(shopDTO.getId());
            else
                throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
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
            onlineOrder.setNote(header.getNote());
           Long id = repository.save(onlineOrder).getId();

            //online order detail
            for(Line line : lines){
                OnlineOrderDetail detail = new OnlineOrderDetail();
                detail.setOnlineOrderId(id);
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
        });
        return dataSet;
    }

    @Override
    public DataSet syncXmlToCancelOnlineOrder(MultipartFile file) throws IOException {
        xstream.processAnnotations(classes);
        xstream.allowTypes(classes);
        DataSet dataSet = (DataSet) xstream.fromXML(file.getInputStream());

        Integer stt = 0;

        List<NewDataSet> dataSets = dataSet.getLstNewDataSet();
        for(NewDataSet data :  dataSets){
            Header header = data.getHeader();
            if(header != null)
            {
                if(header.getOrderNumber() != null)
                {
                    OnlineOrder onlineOrder = repository.findByOrderNumber(header.getOrderNumber());
                    if(onlineOrder != null)
                    {
                        if(onlineOrder.getSynStatus() == 0)
                        {
                            onlineOrder.setSynStatus(-1);
                            String orderNumber = onlineOrder.getOrderNumber()+"_HUY";
                            onlineOrder.setOrderNumber(orderNumber);
                            repository.save(onlineOrder);
                            stt ++;
                        }
                    }
                }
            }
        }
        dataSet.setStt(stt);
        return dataSet;
    }

    @Override
    public String exportXmlFile(List<Long> ids) {
        xstream.processAnnotations(classes);
        DataSet dataSet = new DataSet();
        List<NewDataSet> dataSets = new ArrayList<>();
        for(Long id : ids)
        {
            SaleOrder saleOrder = saleOrderRepository.findById(id).orElse(null);
            if(saleOrder != null)
            {
                NewDataSet newDataSet = new NewDataSet();
                Header header = new Header();
                if(saleOrder.getOnlineNumber() != null)
                {
                    OnlineOrder onlineOrder = repository.findByOrderNumber(saleOrder.getOnlineNumber());
                    header.setOrderNumber(onlineOrder.getOrderNumber());
                    header.setOrderID(onlineOrder.getOrderId());
                    header.setPosOrderNumber(saleOrder.getOrderNumber());
                    newDataSet.setHeader(header);
                    dataSets.add(newDataSet);
                }
            }
        }
        dataSet.setLstNewDataSet(dataSets);
        try {
            xstream.toXMLFile(dataSet);
            String xml = xstream.toXML(dataSet);
            return xml;
        } catch (IOException e) {
            e.printStackTrace();
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
        customerRequest.setStatus(1L);
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

    private OrderProductOnlineDTO mapOnlineOrderDetailToProductDTO(
            OnlineOrderDetail detail, OnlineOrderDTO onlineOrderDTO, Long customerTypeId, Long shopId, Long warehouseTypeId) {

        Product product = productRepo.getProductByProductCodeAndStatus(detail.getSku(), 1)
                .orElseThrow(() -> new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND));

        Price productPrice = productPriceRepo.getProductPrice(product.getId(), customerTypeId);
        if(productPrice == null)
            throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);

        StockTotal stockTotal = stockTotalRepo.getStockTotal(shopId, warehouseTypeId, product.getId())
                .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND));

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderProductOnlineDTO productOrder = modelMapper.map(product, OrderProductOnlineDTO.class);
        productOrder.setProductId(product.getId());
        productOrder.setQuantity(detail.getQuantity());
        productOrder.setPrice(productPrice.getPrice());
        productOrder.setPrice(productPrice.getPrice());
        productOrder.setStockTotal(stockTotal.getQuantity());

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
