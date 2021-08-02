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
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.messaging.CustomerOnlRequest;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
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
import vn.viettel.sale.util.ConnectFTP;
import vn.viettel.sale.xml.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        OnlineOrder onlineOrder = repository.findById(id).orElseThrow(() -> new ValidateException(ResponseMessage.ORDER_ONLINE_NOT_FOUND));
        if(onlineOrder.getSynStatus()!=null &&  onlineOrder.getSynStatus()==1)
            throw new ValidateException(ResponseMessage.SALE_ORDER_ALREADY_CREATED);

        List<CustomerDTO> customerDTOS = customerClient.getCustomerByMobiPhoneV1(onlineOrder.getCustomerPhone()).getData();

        if(customerDTOS.isEmpty()) {
            CustomerOnlRequest cusRequest = new CustomerOnlRequest();
            cusRequest.setFirstName(this.getFirstName(onlineOrder.getCustomerName()));
            cusRequest.setLastName(this.getLastName(onlineOrder.getCustomerName()));
            cusRequest.setMobiPhone(onlineOrder.getCustomerPhone());
            cusRequest.setDob(onlineOrder.getCustomerDOB());
            cusRequest.setAddress(onlineOrder.getCustomerAddress());
            cusRequest.setStatus(1);

            try{
                CustomerDTO customerDTO = customerClient.createForFeignV1(cusRequest, shopId).getData();
                customerDTOS.add(customerDTO);
            }catch (Exception e){
                throw new ValidateException(ResponseMessage.CUSTOMER_CREATE_FAILED);
            }
        }

        List<OnlineOrderDetail> orderDetails = onlineOrderDetailRepo.findByOnlineOrderId(id);
        if(orderDetails.isEmpty()) throw new ValidateException(ResponseMessage.EMPTY_LIST);
        OnlineOrderDTO onlineOrderDTO = this.mapOnlineOrderToOnlineOrderDTO(onlineOrder);

        Long warehouseTypeId = customerTypeClient.getWarehouseTypeByShopId(shopId);
        if (warehouseTypeId == null)
            throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);

        List<OrderProductOnlineDTO> orderLines = new ArrayList<>();
        Long customerType = customerDTOS.get(0).getCustomerTypeId();
        List<Product> products = productRepo.findByProductCodes(orderDetails.stream().map(item -> item.getSku()).distinct().
                filter(Objects::nonNull).collect(Collectors.toList()));
        List<Long> productIds = products.stream().map(item -> item.getId()).collect(Collectors.toList());
        List<Price> prices = productPriceRepo.findProductPriceWithType(productIds, customerType, DateUtils.convertToDate(LocalDateTime.now()));
        List<StockTotal> stockTotals = stockTotalRepo.getStockTotal(shopId, warehouseTypeId, productIds);

        for (OnlineOrderDetail detail: orderDetails) {
            OrderProductOnlineDTO productOrder = this.mapOnlineOrderDetailToProductDTO(detail, products, prices, stockTotals);
            orderLines.add(productOrder);
            onlineOrderDTO.addQuantity(productOrder.getQuantity());
            onlineOrderDTO.addTotalPrice(productOrder.getTotalPrice());
        }

        onlineOrderDTO.setProducts(orderLines);
        onlineOrderDTO.setCustomers(customerDTOS);

        //Set order type
        ApParamDTO apParam = apparamClient.getApParamOnlineOrderV1(onlineOrder.getSourceName()).getData();
        onlineOrderDTO.setType(apParam);

        return onlineOrderDTO;
    }

    @Override
    public String checkOnlineNumber(String code) {
        ApParamDTO apParam = apparamClient.getApParamByCodeV1("NUMDAY_CHECK_ONLNO").getData();
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime daysAgo = null;
        if(apParam!=null && apParam.getStatus() == 1 && apParam.getValue() !=null) {
            daysAgo = DateUtils.convertFromDate(date.minusDays(Integer.valueOf(apParam.getValue())));
        }
        List<SaleOrder> saleOrders = saleOrderRepository.checkOnlineNumber(code, daysAgo);

        if(!saleOrders.isEmpty())
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

                //online order
                ShopDTO shopDTO = shopClient.getByShopCode(header.getStoreID()).getData();
                if(shopDTO == null) {
                    message = ResponseMessage.SHOP_NOT_FOUND.statusCodeValue();
                    continue;
                }

                //check order number
                OnlineOrder onlineOrder = repository.findByOrderNumber(header.getOrderNumber());
                if(onlineOrder != null && onlineOrder.getSynStatus() != 0) continue;
                if(onlineOrder != null && onlineOrder.getSynStatus() == 0) {
                    onlineOrderDetailRepo.deleteByOnlineOrderId(onlineOrder.getId());
                    repository.delete(onlineOrder);
                }

                //check product
                List<String> productCodes = new ArrayList<>();
                for(Line line: lines) {
                    if (!productCodes.contains(line.getSku())) {
                        productCodes.add(line.getSku());
                    }
                }
                List<Product> products = productRepo.findByProductCodes(productCodes);
                if (products.size() != lines.size()) continue;

                String adrress = header.getCustomerAddress();
                if(header.getCustomerAddress().isEmpty()) adrress = header.getShippingAddress();

                if (header.getCustomerBirthday()!=null) {
                    repository.schedulerInsert(shopDTO.getId(), 0, header.getSourceName(), header.getOrderID(), header.getOrderNumber(),
                            header.getTotalLineValue(), header.getDiscountCode(), header.getDiscountValue(), header.getCustomerName(), header.getCustomerPhone(), adrress, header.getShippingAddress(),
                            header.getCustomerBirthday(), header.getOrderStatus(), 0 , header.getNote(), LocalDateTime.now());
                }else {
                    repository.schedulerInsertNoDOB(shopDTO.getId(), 0, header.getSourceName(), header.getOrderID(), header.getOrderNumber(),
                            header.getTotalLineValue(), header.getDiscountCode(), header.getDiscountValue(), header.getCustomerName(), header.getCustomerPhone(), adrress, header.getShippingAddress(),
                            header.getOrderStatus(), 0 , header.getNote(), LocalDateTime.now());
                }

                OnlineOrder onlineOrderDB = repository.findFirstByOrderByIdDesc();

                for(Line line : lines){
                    onlineOrderDetailRepo.schedulerInsert(shopDTO.getId(), onlineOrderDB.getId(), line.getSku(), line.getProductName(), line.getQuantity(), line.getOriginalPrice(),
                            line.getRetailsPrice(), line.getLineValue(), line.getCharacter1Name(), line.getCharacter1Value(), line.getCharacter2Name(), line.getCharacter2Value(),
                            line.getCharacter3Name(), line.getCharacter3Value(), line.getPromotionName(),  LocalDateTime.now());
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
                                String orderNumber = onlineOrder.getOrderNumber() + "_HUY";
                                repository.schedulerCancel(-1, orderNumber, onlineOrder.getId());
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
    @Transactional(rollbackFor = Exception.class)
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
                repository.schedulerUpdate(1, LocalDateTime.now(), onlineOrder.getId());
            }
            dataSet.setLstNewDataSet(newDataSets);
            xstream.toXMLFile(dataSet);
            String xml = xstream.toXML(dataSet);
            return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void getOnlineOrderSchedule() {
        List<ApParamDTO> apParamDTOList = apparamClient.getApParamByTypeV1("FTP").getData();
        String readPath = "/home/ftpimt/pos/neworder", backupPath = "/home/ftpimt/pos/backup", newOrder = "_VES_", cancelOrder = "_CANORDERPOS_"
                , destinationMessage = "/home/ftpimt/pos/ordermessage", failName = "VES_ORDERMESSAGE_";
        if(apParamDTOList != null){
            for(ApParamDTO app : apParamDTOList){
                if(app.getApParamCode() == null || "FTP_ORDER".equalsIgnoreCase(app.getApParamCode().trim())) readPath = app.getValue().trim();
                if(app.getApParamCode() == null || "FTP_ONLINE_BACKUP".equalsIgnoreCase(app.getApParamCode().trim())) backupPath = app.getValue().trim();
                if(app.getApParamCode() == null || "FTP_FILE_NEW".equalsIgnoreCase(app.getApParamCode().trim())) newOrder = app.getValue().trim();
                if(app.getApParamCode() == null || "FTP_FILE_CANCEL".equalsIgnoreCase(app.getApParamCode().trim())) cancelOrder = app.getValue().trim();
                if (app.getApParamCode() == null || "FTP_MESSAGE".equalsIgnoreCase(app.getApParamCode().trim()))
                    destinationMessage = app.getValue().trim();
                if (app.getApParamCode() == null || "FTP_FILE_FAIL".equalsIgnoreCase(app.getApParamCode().trim()))
                    failName = app.getValue().trim();
            }
        }
        ConnectFTP connectFTP = connectFTP(apParamDTOList);
        //read new order
        HashMap<String, InputStream> newOrders = connectFTP.getFiles(readPath, newOrder, null);
        if(newOrders != null){
            for (Map.Entry<String, InputStream> entry : newOrders.entrySet()){
                try {
                    this.syncXmlOnlineOrder(entry.getValue());
                    connectFTP.moveFile(readPath, backupPath, entry.getKey());
                    entry.getValue().close();
                }catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println(ex);
                    LogFile.logToFile("", "", LogLevel.ERROR, null, "Error while read file " + entry.getKey() + " - " + ex.getMessage());
                }
            }
        }

        //read cancel order
        HashMap<String, InputStream> cancelOrders = connectFTP.getFiles(readPath, cancelOrder, null);
        if(cancelOrders != null){
            for (Map.Entry<String, InputStream> entry : cancelOrders.entrySet()){
                try {
                    this.syncXmlToCancelOnlineOrder(entry.getValue());
                    connectFTP.moveFile(readPath, backupPath, entry.getKey());
                    entry.getValue().close();
                }catch (Exception ex) {
                    System.out.println(ex);
                    LogFile.logToFile("", "", LogLevel.ERROR, null, "Error while read file " + entry.getKey() + " - " + ex.getMessage());
                }
            }
        }
        connectFTP.disconnectFTPServer();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadOnlineOrderSchedule() {
        List<Long> shops = repository.findALLShopId();
        if(shops.size() > 0) {

            //set ap param value
            List<ApParamDTO> apParamDTOList = apparamClient.getApParamByTypeV1("FTP").getData();

            String uploadDestination = "/home/ftpimt/pos/downorderpos", successName = "ORDERPOS_";
            if (apParamDTOList != null) {
                for (ApParamDTO app : apParamDTOList) {
                    if (app.getApParamCode() == null || "FTP_UPLOAD".equalsIgnoreCase(app.getApParamCode().trim()))
                        uploadDestination = app.getValue().trim();
                    if (app.getApParamCode() == null || "FTP_FILE_SUC".equalsIgnoreCase(app.getApParamCode().trim()))
                        successName = app.getValue().trim();
                }
            }
            ConnectFTP connectFTP = connectFTP(apParamDTOList);
            for (Long shopId : shops) {
                List<OnlineOrder> onlineOrders = repository.findOnlineOrderExportXml(shopId);
                ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
                if (onlineOrders != null && !onlineOrders.isEmpty()) {
                    try {
                        String fileName = successName + StringUtils.createXmlFileName(shopDTO.getShopCode());
                        InputStream inputStream = this.exportXmlFile(onlineOrders);
                        if (inputStream != null)
                            connectFTP.uploadFile(inputStream, fileName, uploadDestination);
                    } catch (Exception ex) {
                        LogFile.logToFile("", "", LogLevel.ERROR, null, "Error parse sale order " + shopDTO.getShopCode() + " to file - " + ex.getMessage());
                    }
                }
            }
            connectFTP.disconnectFTPServer();
        }
    }

    private ConnectFTP connectFTP(List<ApParamDTO> apParamDTOList){
        String server = "192.168.100.112", portStr = "21", userName = "ftpimt", password = "Viett3l$Pr0ject";
        if(apParamDTOList != null){
            for(ApParamDTO app : apParamDTOList){
                if(app.getApParamCode() == null || "FTP_SERVER".equalsIgnoreCase(app.getApParamCode().trim())) server = app.getValue().trim();
                if(app.getApParamCode() == null || "FTP_USER".equalsIgnoreCase(app.getApParamCode().trim())) userName = app.getValue().trim();
                if(app.getApParamCode() == null || "FTP_PASS".equalsIgnoreCase(app.getApParamCode().trim())) password = app.getValue().trim();
                if(app.getApParamCode() == null || "FTP_PORT".equalsIgnoreCase(app.getApParamCode().trim())) portStr = app.getValue().trim();
            }
        }
        return new ConnectFTP(server, portStr, userName, password);
    }

    private OrderProductOnlineDTO mapOnlineOrderDetailToProductDTO(OnlineOrderDetail detail, List<Product> products, List<Price> prices, List<StockTotal> stockTotals) {
        Product product = null;
        for(Product p : products){
            if(p.getProductCode().equalsIgnoreCase(detail.getSku())){
                product = p; break;
            }
        }
        if(product == null) throw new ValidateException(ResponseMessage.PRODUCT_NOT_EXISTS, detail.getSku());

        double price = 0;
        for(Price p : prices){
            if(p.getProductId().equals(product.getId())){
                price = p.getPrice(); break;
            }
        }
        if(price < 1) throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED, detail.getSku());

        StockTotal stockTotal = null;
        for(StockTotal p : stockTotals){
            if(p.getProductId().equals(product.getId())){
                stockTotal = p; break;
            }
        }
        if(stockTotal == null) throw new ValidateException(ResponseMessage.PRODUCT_STOCK_TOTAL_NOT_FOUND, detail.getSku());

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderProductOnlineDTO productOrder = modelMapper.map(product, OrderProductOnlineDTO.class);
        productOrder.setProductId(product.getId());
        productOrder.setQuantity(detail.getQuantity());
        productOrder.setPrice(price);
        productOrder.setStockTotal(stockTotal.getQuantity());

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
