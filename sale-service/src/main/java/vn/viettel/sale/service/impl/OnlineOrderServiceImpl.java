package vn.viettel.sale.service.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.poi.util.IOUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.convert.XStreamTranslator;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.exception.ApplicationException;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.messaging.CustomerOnlRequest;
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
import vn.viettel.sale.service.feign.ApparamClient;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.ShopClient;
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
    @Autowired
    OnlineOrderDetailRepository onlineOrderDetailRepo;

    @Autowired
    ShopClient shopClient;

    @Autowired
    CustomerClient customerClient;

    @Autowired
    ApparamClient apparamClient;

    @Autowired
    CustomerTypeClient customerTypeClient;

    @Autowired
    ProductRepository productRepo;

    @Autowired
    ProductPriceRepository productPriceRepo;

    @Autowired
    StockTotalRepository stockTotalRepo;

    @Autowired
    SaleOrderRepository saleOrderRepository;

    @Value( "${apparam.code.unique.online.order}" )
    private String uniqueParramCode;

    @Value( "${apparam.type.unique.online.order}" )
    private String uniqueParramType;

    @Value("${spring.application.name}")
    public String appName;

    XStreamTranslator xstream = XStreamTranslator.getInstance();

    private Class<?>[] classes = new Class[] { Line.class, DataSet.class, Header.class, NewDataSet.class, NewData.class};

    @Override
    public Page<OnlineOrderDTO> getOnlineOrders(OnlineOrderFilter filter, Pageable pageable) {
        Sort newSort = Sort.by("createDate").descending();
        if(pageable.getSort() != null) {
            for (Sort.Order order : pageable.getSort()) {
                if(!order.getProperty().equals("createdAt"))
                    newSort.and(Sort.by(order.getDirection(), order.getProperty()));
            }
        }
        Pageable newPage = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
        Page<OnlineOrder> onlineOrders = repository.findAll(
                Specification.where(
                        OnlineOrderSpecification.hasOrderNumber(filter.getOrderNumber()))
                        .and(OnlineOrderSpecification.hasShopId(filter.getShopId()))
                        .and(OnlineOrderSpecification.hasSynStatus(filter.getSynStatus()))
                        .and(OnlineOrderSpecification.hasFromDateToDate(filter.getFromDate(), filter.getToDate())
                        ), newPage);
        Page<OnlineOrderDTO> onlineOrderDTOS = onlineOrders.map(this::mapOnlineOrderToOnlineOrderDTO);

        return onlineOrderDTOS;
    }

    @Override
    public OnlineOrderDTO getOnlineOrder(Long id, Long shopId, Long userId) {
        OnlineOrder onlineOrder = repository.getById(id, shopId).orElseThrow(() -> new ValidateException(ResponseMessage.ORDER_ONLINE_NOT_FOUND));

        List<OnlineOrderDetail> orderDetails = onlineOrderDetailRepo.findByOnlineOrderId(id);
        if(orderDetails.isEmpty()) throw new ValidateException(ResponseMessage.ONLINE_ORDER_PRODUCT_IS_EMPTY);

        List<CustomerDTO> customerDTOS = customerClient.getCustomerByMobiPhoneV1(onlineOrder.getCustomerPhone()).getData();
        if(customerDTOS.isEmpty()) {
            CustomerOnlRequest cusRequest = new CustomerOnlRequest();
            this.setCustomerName(cusRequest, onlineOrder);
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
        if(customerDTOS.get(0).getCustomerTypeId() == null) throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);
        CustomerTypeDTO customerType = customerTypeClient.getCusTypeById(customerDTOS.get(0).getCustomerTypeId());

        OnlineOrderDTO onlineOrderDTO = this.mapOnlineOrderToOnlineOrderDTO(onlineOrder);

        List<OrderProductOnlineDTO> orderLines = new ArrayList<>();
        List<Product> products = productRepo.findByProductCodes(orderDetails.stream().map(item -> item.getSku()).distinct().
                filter(Objects::nonNull).collect(Collectors.toList()));
        List<Long> productIds = products.stream().map(item -> item.getId()).collect(Collectors.toList());
        List<Price> prices = productPriceRepo.findProductPriceWithType(productIds, customerType.getId(), DateUtils.convertToDate(LocalDateTime.now()));
        List<StockTotal> stockTotals = stockTotalRepo.getStockTotal(shopId, customerType.getWareHouseTypeId(), productIds);

        /*Cửa hàng có quyền chính sửa đơn thì vẫn load lên SP ko tồn tai || ko có gia || ko có tồn kho */
        boolean isEditable = shopClient.isEditableOnlineOrderV1(shopId).getData();
        for (OnlineOrderDetail detail: orderDetails) {
            Product product = null;
            for(Product p : products){
                if(p.getProductCode().equalsIgnoreCase(detail.getSku())){
                    product = p; break;
                }
            }
            if(product!=null) {
                OrderProductOnlineDTO productOrder = this.mapOnlineOrderDetailToProductDTO(detail, product, prices, stockTotals, isEditable);
                orderLines.add(productOrder);
                onlineOrderDTO.addQuantity(productOrder.getQuantity());
                onlineOrderDTO.addTotalPrice(productOrder.getTotalPrice());
            }else if(product == null && isEditable) {
                OrderProductOnlineDTO productOrder = new OrderProductOnlineDTO();
                productOrder.setProductCode(detail.getSku());
                productOrder.setProductName(detail.getProductName());
                productOrder.setQuantity(detail.getQuantity());
                productOrder.setStatus(0);
                orderLines.add(productOrder);
                onlineOrderDTO.addQuantity(detail.getQuantity());
            }else {
                throw new ValidateException(ResponseMessage.PRODUCT_NOT_EXISTS, detail.getSku());
            }
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
        ApParamDTO apParam = apparamClient.getApParamByCodeTypeV1(uniqueParramCode, uniqueParramType).getData();
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime daysAgo = null;
        if(apParam!=null && apParam.getStatus() == 1 && apParam.getValue() !=null) {
            if(apParam.getValue().equals("0")) daysAgo = DateUtils.convertFromDate(date);
            else daysAgo = DateUtils.convertFromDate(date.minusDays(Integer.valueOf(apParam.getValue())));
        }
        List<SaleOrder> saleOrders = saleOrderRepository.checkOnlineNumber(code, daysAgo);

        if(!saleOrders.isEmpty())
            throw new ValidateException(ResponseMessage.ONLINE_NUMBER_IS_EXISTS);
        return code;
    }


    private void setCustomerName(CustomerOnlRequest request,  OnlineOrder onlineOrder) {
        if(onlineOrder.getCustomerName()==null || onlineOrder.getCustomerName().isEmpty()) return;
        String fullName = onlineOrder.getCustomerName().trim();
        String[] words = fullName.split(" ");
        if(words.length == 1) {
            request.setLastName("KH");
            request.setFirstName(fullName);
        }else {
            int i = fullName.lastIndexOf(' ');
            request.setLastName(fullName.substring(0, i).trim());
            request.setFirstName(fullName.substring(i+1).trim());
        }
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

                String orderNumber = header.getOrderNumber();
                if(orderNumber!=null && orderNumber.startsWith("#")) orderNumber = orderNumber.substring(1);

                //check order number
                OnlineOrder onlineOrder = repository.findByOrderNumber(orderNumber);
                if(onlineOrder != null && onlineOrder.getSynStatus() != 0) continue;
                if(onlineOrder != null && onlineOrder.getSynStatus() == 0) {
                    onlineOrderDetailRepo.deleteByOnlineOrderId(onlineOrder.getId());
                    repository.delete(onlineOrder);
                }

                //check product - có thể trùng sp
                Set<String> productCodes = new HashSet<>();
                for(Line line: lines) {
                    if (!productCodes.contains(line.getSku())) {
                        productCodes.add(line.getSku());
                    }
                }
                List<Product> products = productRepo.findByProductCodes(new ArrayList<>(productCodes));
                if (products.size() < productCodes.size()) continue;

                String adrress = header.getCustomerAddress();
                if(header.getCustomerAddress().isEmpty()) adrress = header.getShippingAddress();

                OnlineOrder newOnlOrder = new OnlineOrder();
                newOnlOrder.setShopId(shopDTO.getId());
                newOnlOrder.setSynStatus(0);
                newOnlOrder.setSourceName(header.getSourceName());
                newOnlOrder.setOrderId(header.getOrderID());
                newOnlOrder.setOrderNumber(orderNumber);
                newOnlOrder.setTotalLineValue(header.getTotalLineValue());
                newOnlOrder.setDiscountCode(header.getDiscountCode());
                newOnlOrder.setDiscountValue(header.getDiscountValue());
                newOnlOrder.setCustomerName(header.getCustomerName());
                newOnlOrder.setCustomerPhone(header.getCustomerPhone());
                newOnlOrder.setCustomerAddress(adrress);
                newOnlOrder.setOrderStatus(header.getShippingAddress());
                newOnlOrder.setCustomerDOB(header.getCustomerBirthday());
                newOnlOrder.setOrderStatus(header.getOrderStatus());
                newOnlOrder.setVnmSynStatus(0);
                newOnlOrder.setNote(header.getNote());
                newOnlOrder.setCreateDate(header.getCreatedAt());
                repository.save(newOnlOrder);

                for(Line line : lines){
                    OnlineOrderDetail newDetail = new OnlineOrderDetail();
                    newDetail.setShopId(shopDTO.getId());
                    newDetail.setOnlineOrderId(newOnlOrder.getId());
                    newDetail.setSku(line.getSku());
                    newDetail.setProductName(line.getProductName());
                    newDetail.setQuantity(line.getQuantity());
                    newDetail.setOriginalPrice(line.getOriginalPrice());
                    newDetail.setRetailsPrice(line.getRetailsPrice());
                    newDetail.setLineValue(line.getLineValue());
                    newDetail.setCharacter1Name(line.getCharacter1Name());
                    newDetail.setCharacter1Value(line.getCharacter1Value());
                    newDetail.setCharacter2Name(line.getCharacter2Name());
                    newDetail.setCharacter2Value(line.getCharacter2Value());
                    newDetail.setCharacter3Name(line.getCharacter3Name());
                    newDetail.setCharacter3Value(line.getCharacter3Value());
                    newDetail.setPromotionName(line.getPromotionName());
                    newDetail.setCreateDate(header.getCreatedAt());

                    onlineOrderDetailRepo.save(newDetail);
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
                                repository.schedulerCancel(-1, orderNumber, LocalDateTime.now(), onlineOrder.getId());
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
                if(onlineOrder.getSaleOrderId()!=null) {
                    SaleOrder saleOrder = saleOrderRepository.findById(onlineOrder.getSaleOrderId()).orElse(null);
                    if(saleOrder != null)
                    {
                        header.setPosOrderNumber(saleOrder.getOrderNumber());
                    }
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
                , destinationMessage = "/home/ftpimt/pos/ordermessage", failName = "VES_ORDERMESSAGE_", shopCodes = "";
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
                if(app.getApParamCode() == null || "FTP_ORDER_SHOP".equalsIgnoreCase(app.getApParamCode().trim())) shopCodes = app.getValue();
            }
        }
        FTPINFO ftpinfo = new FTPINFO(apParamDTOList);
        ConnectFTP connectFTP = new ConnectFTP(ftpinfo.getServer(), ftpinfo.getPortStr(), ftpinfo.getUserName(), ftpinfo.getPassword());

        String[] arrShops = {null};
        if(shopCodes != null && !shopCodes.isEmpty()) {
            arrShops = shopCodes.split(";");
        }

        for(String shopCode : arrShops){
            if(newOrder == null) newOrder = "";
            if(cancelOrder == null) cancelOrder = "";
            if(connectFTP.reConnected(ftpinfo.getServer(), ftpinfo.getPortStr(), ftpinfo.getUserName(), ftpinfo.getPassword())){
                FTPFile[] ftpFiles = connectFTP.getFilesV2(readPath);
                if (ftpFiles != null && ftpFiles.length > 0) {
                    for (FTPFile file : ftpFiles) {
                        if (file.isFile() && file.getName().endsWith(connectFTP.getReadFile()) &&
                                ((shopCode==null || shopCode.isEmpty()) || (shopCode!=null && file.getName().toLowerCase().startsWith(shopCode.trim().toLowerCase())))) {
                            InputStream inputstream = null;
                            try {
                                if(connectFTP.reConnected(ftpinfo.getServer(), ftpinfo.getPortStr(), ftpinfo.getUserName(), ftpinfo.getPassword())){
                                    inputstream = connectFTP.retrieveFile(readPath + "/" + file.getName());
                                    if(file.getName().toLowerCase().contains(newOrder.toLowerCase()) && inputstream != null) {
                                        this.syncXmlOnlineOrder(inputstream);
                                        connectFTP.moveFile(readPath, backupPath, file.getName());
                                    }else if(file.getName().toLowerCase().contains(cancelOrder.toLowerCase()) && inputstream != null) {
                                        this.syncXmlToCancelOnlineOrder(inputstream);
                                        connectFTP.moveFile(readPath, backupPath, file.getName());
                                    }
                                }
                            }catch (Exception ex) {
                                LogFile.logToFile(appName, "schedule", LogLevel.ERROR, null, "FTP read files error: " +  file.getName() + " - " + ex.getMessage());
                            }finally {
                                IOUtils.closeQuietly(inputstream);
                            }
                        }
                    }
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
            FTPINFO ftpinfo = new FTPINFO(apParamDTOList);
            ConnectFTP connectFTP = new ConnectFTP(ftpinfo.getServer(), ftpinfo.getPortStr(), ftpinfo.getUserName(), ftpinfo.getPassword());

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
                        LogFile.logToFile(appName, "schedule", LogLevel.ERROR, null, "Error parse sale order " + shopDTO.getShopCode() + " to file - " + ex.getMessage());
                    }
                }
            }
            connectFTP.disconnectFTPServer();
        }
    }

    /*private ConnectFTP connectFTP(List<ApParamDTO> apParamDTOList){
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
*/
    private OrderProductOnlineDTO mapOnlineOrderDetailToProductDTO(OnlineOrderDetail detail, Product product, List<Price> prices, List<StockTotal> stockTotals, boolean isEditable) {
        double price = 0;
        for(Price p : prices){
            if(p.getProductId().equals(product.getId())){
                price = p.getPrice(); break;
            }
        }
        if(price < 1 && !isEditable) throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED, detail.getSku());

        int stockTotal = 0;
        for(StockTotal p : stockTotals){
            if(p.getProductId().equals(product.getId())){
                stockTotal = p.getQuantity(); break;
            }
        }
        if(stockTotal < 1 && !isEditable) throw new ValidateException(ResponseMessage.PRODUCT_STOCK_TOTAL_NOT_FOUND, detail.getSku());

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderProductOnlineDTO productOrder = modelMapper.map(product, OrderProductOnlineDTO.class);
        productOrder.setProductId(product.getId());
        productOrder.setQuantity(detail.getQuantity());
        productOrder.setPrice(price);
        productOrder.setStockTotal(stockTotal);

        return productOrder;
    }

    private OnlineOrderDTO mapOnlineOrderToOnlineOrderDTO(OnlineOrder order) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OnlineOrderDTO dto = modelMapper.map(order, OnlineOrderDTO.class);
        dto.setOrderInfo(order.getCustomerName() + " - " + order.getCustomerPhone());
        dto.setCreatedAt(order.getCreateDate()); //vì cần tìm theo ngày tạo trên ftp
        return dto;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public class FTPINFO {
       private String server;
       private String portStr;
       private String userName;
       private String password;
        public FTPINFO(List<ApParamDTO> apParamDTOList) {
            if(apParamDTOList != null){
                for(ApParamDTO app : apParamDTOList){
                    if(app.getApParamCode() == null || "FTP_SERVER".equalsIgnoreCase(app.getApParamCode().trim())) server = app.getValue().trim();
                    if(app.getApParamCode() == null || "FTP_USER".equalsIgnoreCase(app.getApParamCode().trim())) userName = app.getValue().trim();
                    if(app.getApParamCode() == null || "FTP_PASS".equalsIgnoreCase(app.getApParamCode().trim())) password = app.getValue().trim();
                    if(app.getApParamCode() == null || "FTP_PORT".equalsIgnoreCase(app.getApParamCode().trim())) portStr = app.getValue().trim();
                }
            }
        }
    }


}
