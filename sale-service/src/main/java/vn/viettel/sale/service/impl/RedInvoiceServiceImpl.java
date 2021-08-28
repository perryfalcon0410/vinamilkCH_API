package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.excel.HDDTExcel;
import vn.viettel.sale.excel.HVKHExcel;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.RedInvoiceDetailService;
import vn.viettel.sale.service.RedInvoiceService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.RedInvoiceSpecification;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RedInvoiceServiceImpl extends BaseServiceImpl<RedInvoice, RedInvoiceRepository> implements RedInvoiceService {
    @Autowired
    CustomerClient customerClient;

    @Autowired
    UserClient userClient;

    @Autowired
    RedInvoiceDetailService redInvoiceDetailService;

    @Autowired
    SaleOrderRepository saleOrderRepository;

    @Autowired
    RedInvoiceRepository redInvoiceRepository;

    @Autowired
    RedInvoiceDetailRepository redInvoiceDetailRepository;

    @Autowired
    SaleOrderDetailRepository saleOrderDetailRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductPriceRepository productPriceRepository;

    @Autowired
    ShopClient shopClient;

    @Autowired
    HDDTExcelRepository hddtExcelRepository;

    @Autowired
    CTDVKHRepository ctdvkhRepository;

    @Override
    public CoverResponse<Page<RedInvoiceDTO>, TotalRedInvoice> getAll(Long shopId, String searchKeywords, Date fromDate, Date toDate, String invoiceNumber, Pageable pageable) {
        List<Long> ids = null;
        if(searchKeywords != null){
            ids = customerClient.getIdCustomerBySearchKeyWordsV1(searchKeywords.trim()).getData();
            if(ids == null || ids.isEmpty()) ids = Arrays.asList(-1L);
        }

        if (invoiceNumber!=null) invoiceNumber = invoiceNumber.trim().toUpperCase();
        LocalDateTime tsFromDate = DateUtils.convertFromDate(fromDate);
        LocalDateTime tsToDate = DateUtils.convertToDate(toDate);
        if (tsFromDate == null) tsFromDate = LocalDateTime.of(2015,1,1,0,0);
        if (tsToDate == null) tsToDate = LocalDateTime.now();
        tsFromDate = DateUtils.convertFromDate(tsFromDate);
        tsToDate = DateUtils.convertToDate(tsToDate);

        Page<RedInvoice> redInvoices = repository.findAll(Specification.where(RedInvoiceSpecification.hasCustomerId(ids))
                .and(RedInvoiceSpecification.hasShopId(shopId))
                .and(RedInvoiceSpecification.hasFromDateToDate(fromDate, toDate))
                .and(RedInvoiceSpecification.hasInvoiceNumber(invoiceNumber)), pageable);;

        Page<RedInvoiceDTO> redInvoiceDTOS = redInvoices.map(red -> modelMapper.map(red, RedInvoiceDTO.class));
        TotalRedInvoice totalRedInvoice = repository.getTotalRedInvoice1(shopId, ids, invoiceNumber, tsFromDate, tsToDate);
        TotalRedInvoice totalRedInvoice1 = repository.getTotalRedInvoice2(shopId, ids, invoiceNumber, tsFromDate, tsToDate);
        if(totalRedInvoice1.getSumAmountNotVat() > 0) totalRedInvoice.setSumAmountNotVat(totalRedInvoice1.getSumAmountNotVat());
        else totalRedInvoice.setSumAmountNotVat(totalRedInvoice1.getSumTotalQuantity());
        if(totalRedInvoice1.getSumAmountGTGT() > 0) totalRedInvoice.setSumAmountGTGT(totalRedInvoice1.getSumAmountGTGT());
        else totalRedInvoice.setSumAmountGTGT(totalRedInvoice1.getSumTotalMoney());

        CoverResponse coverResponse = new CoverResponse(redInvoiceDTOS, totalRedInvoice);
        return coverResponse;
    }

    @Override
    public CoverResponse<List<RedInvoiceDataDTO>, TotalRedInvoiceResponse> getDataInBillOfSale(List<String> orderCodeList, Long shopId) {
        String customerName, customerCodes, officeWorking, officeAddress, taxCode;
        Long customerIds;
        if (orderCodeList == null || orderCodeList.isEmpty()) {
            throw new ValidateException(ResponseMessage.INVOICE_NUMBER_EMPTY);
        } else {
            CustomerDTO customerDTO = null;
            List<RedInvoiceDataDTO> dtos = new ArrayList<>();
            List<SaleOrder> saleOrdersList = new ArrayList<>();
            List<Long> idCustomerList = saleOrderRepository.getCustomerCode(orderCodeList);
            Long idCus = null;
            boolean check = false;
            if (idCustomerList.size() == 1) {
                check = true;
                idCus = idCustomerList.get(0);
            } else {
                for (int i = 0; i < idCustomerList.size(); i++) {
                    for (int j = i + 1; j < idCustomerList.size(); j++) {
                        if (idCustomerList.get(i).equals(idCustomerList.get(j))) {
                            idCus = idCustomerList.get(i);
                            check = true;
                        } else {
                            check = false;
                            throw new ValidateException(ResponseMessage.CUSTOMERS_ARE_NOT_DIFFERENT);
                        }
                    }
                }
            }
            List<Product> productList = productRepository.findAllByStatus(1);
            List<Price> priceList = productPriceRepository.findProductPrice(null, 1, LocalDateTime.now());
            if (check) {
                for (String saleOrderCode : orderCodeList) {
                    SaleOrder saleOrder = saleOrderRepository.findSaleOrderByCustomerIdAndOrderNumberAndType(idCus, saleOrderCode, 1);
                    saleOrdersList.add(saleOrder);
                }
                customerDTO = customerClient.getCustomerByIdV1(idCus).getData();
                for (SaleOrder saleOrders : saleOrdersList) {
                    List<SaleOrderDetail> saleOrderDetailsList = saleOrderDetailRepository.findSaleOrderDetail(saleOrders.getId(), false);
                    for (SaleOrderDetail detail : saleOrderDetailsList) {
                        RedInvoiceDataDTO dataDTO = new RedInvoiceDataDTO();
                        if (productList.size() > 0) {
                            for (Product product : productList) {
                                if (product.getId().equals(detail.getProductId())) {
                                    dataDTO.setProductId(product.getId());
                                    dataDTO.setProductCode(product.getProductCode());
                                    dataDTO.setProductName(product.getProductName());
                                    dataDTO.setGroupVat(product.getGroupVat());
                                    dataDTO.setUom1(product.getUom1());
                                    dataDTO.setUom2(product.getUom2());
                                    dataDTO.setConvFact(product.getConvFact());
                                    int integerQuantity = detail.getQuantity() / product.getConvFact();
                                    int residuaQuantity = detail.getQuantity() % product.getConvFact();
                                    dataDTO.setNote(integerQuantity + "T" + residuaQuantity);
                                }
                            }
                        }

                        if (priceList.size() > 0) {
                            for (Price price : priceList) {
                                if (price.getProductId().equals(detail.getProductId())) {
                                    dataDTO.setPriceNotVat(price.getPriceNotVat());
                                    dataDTO.setPrice(price.getPrice());
                                    dataDTO.setVat(price.getVat());
                                    dataDTO.setAmountNotVat(price.getPriceNotVat() * detail.getQuantity());
                                    dataDTO.setAmount(price.getPrice() * detail.getQuantity());
                                    dataDTO.setValueAddedTax(roundValue(((price.getPriceNotVat() * detail.getQuantity()) * price.getVat()) / 100));
                                }
                            }
                        }
                        dataDTO.setSaleOrderId(saleOrders.getId());
                        dataDTO.setQuantity(detail.getQuantity());
                        dtos.add(dataDTO);
                    }
                }
                //               dtos = saleOrderDetailRepository.findRedInvoiceDataDTO(idCus, orderCodeList);
                for (int i = 0; i < dtos.size(); i++) {
                    for (int j = i + 1; j < dtos.size(); j++) {
                        if (dtos.get(i).getProductId().equals(dtos.get(j).getProductId())) {
                            Integer count = dtos.get(i).getQuantity() + dtos.get(j).getQuantity();
                            dtos.get(i).setQuantity(count);
                            dtos.remove(j);
                            j--;
                        }
                    }
                }
            }
            Float totalQuantity = 0F;
            Double totalAmount = 0D;
            Double totalValueAddedTax = 0D;

            customerName = customerDTO.getLastName() + " " + customerDTO.getFirstName();
            customerCodes = customerDTO.getCustomerCode();
            officeWorking = customerDTO.getWorkingOffice();
            officeAddress = customerDTO.getOfficeAddress();
            taxCode = customerDTO.getTaxCode();
            customerIds = customerDTO.getId();

            for (RedInvoiceDataDTO dataDTO : dtos) {
                totalQuantity += (dataDTO.getQuantity()==null?0:dataDTO.getQuantity());
                totalAmount += (dataDTO.getAmount()==null?0D:dataDTO.getAmount());
                totalValueAddedTax += (dataDTO.getValueAddedTax()==null?0D:dataDTO.getValueAddedTax());

            }
            TotalRedInvoiceResponse totalRedInvoiceResponse = new TotalRedInvoiceResponse(
                    totalQuantity, totalAmount, roundValue(totalValueAddedTax), shopId, customerIds, customerCodes, customerName, null, null, officeWorking, officeAddress
                    , taxCode, null, null);
            List<RedInvoiceDataDTO> redInvoiceDataDTOS = new ArrayList<>(dtos);
            CoverResponse<List<RedInvoiceDataDTO>, TotalRedInvoiceResponse> response = new CoverResponse(redInvoiceDataDTOS, totalRedInvoiceResponse);
            return response;
        }
    }

    @Override
    public List<ProductDetailDTO> getAllProductByOrderNumber(String orderCode) {
        if (orderCode == null) {
            return new ArrayList<>();
        }
        return productRepository.findProductDetailDTO(orderCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RedInvoiceDTO create(RedInvoiceNewDataDTO redInvoiceNewDataDTO, Long userId, Long shopId) {

        for (int i = 0; i < redInvoiceNewDataDTO.getProductDataDTOS().size(); i++) {
            if (redInvoiceNewDataDTO.getProductDataDTOS().get(i).getPriceNotVat() == null) {
                Long id = redInvoiceNewDataDTO.getProductDataDTOS().get(i).getProductId();
                Product product = productRepository.getById(id);
                throw new ValidateException(ResponseMessage.PRODUCT_PRICE_RED_INVOICE_NOT_FOUND, product.getProductCode() + " - " + product.getProductName());
            }

            if (redInvoiceNewDataDTO.getProductDataDTOS().get(i).getVat() == null) {
                Long id = redInvoiceNewDataDTO.getProductDataDTOS().get(i).getProductId();
                Product product = productRepository.getById(id);
                throw new ValidateException(ResponseMessage.PRODUCT_VAT_RED_INVOICE_NOT_FOUND, product.getProductCode() + " - " + product.getProductName());
            }

            for (int j = 1; j < redInvoiceNewDataDTO.getProductDataDTOS().size(); j++) {
                if(redInvoiceNewDataDTO.getProductDataDTOS().get(i).getGroupVat() == null && redInvoiceNewDataDTO.getProductDataDTOS().get(j).getGroupVat() == null)
                    continue;

                if((redInvoiceNewDataDTO.getProductDataDTOS().get(i).getGroupVat() != null && redInvoiceNewDataDTO.getProductDataDTOS().get(j).getGroupVat() == null)
                    ||
                    (redInvoiceNewDataDTO.getProductDataDTOS().get(i).getGroupVat() == null && redInvoiceNewDataDTO.getProductDataDTOS().get(j).getGroupVat() != null)
                )
                    throw new ValidateException(ResponseMessage.INDUSTRY_ARE_NOT_DIFFERENT);

                if (!redInvoiceNewDataDTO.getProductDataDTOS().get(i).getGroupVat().equals(redInvoiceNewDataDTO.getProductDataDTOS().get(j).getGroupVat()))
                    throw new ValidateException(ResponseMessage.INDUSTRY_ARE_NOT_DIFFERENT);

            }
        }

        RedInvoiceDTO redInvoiceDTO = null;

        UserDTO userDTO = userClient.getUserByIdV1(userId);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        RedInvoice redInvoiceRecord = modelMapper.map(redInvoiceNewDataDTO, RedInvoice.class);

        if ((redInvoiceNewDataDTO.getRedInvoiceNumber() == null) || (redInvoiceNewDataDTO.getRedInvoiceNumber().equals(""))) {
            String redInvoiceCode = this.createRedInvoiceCode();
            if (this.checkRedInvoiceNumber(redInvoiceCode)) {
                throw new ValidateException(ResponseMessage.RED_INVOICE_CODE_HAVE_EXISTED);
            } else {
                redInvoiceRecord.setInvoiceNumber(redInvoiceCode);
            }
        } else {
            if (this.checkRedInvoiceNumber(redInvoiceNewDataDTO.getRedInvoiceNumber())) {
                throw new ValidateException(ResponseMessage.RED_INVOICE_CODE_HAVE_EXISTED);
            } else {
                redInvoiceRecord.setInvoiceNumber(redInvoiceNewDataDTO.getRedInvoiceNumber());
            }
        }

        String orderNumber = null;
        if (redInvoiceNewDataDTO.getSaleOrderId().size() > 0) {
            redInvoiceRecord.setCustomerId(redInvoiceNewDataDTO.getCustomerId());
            ////////////////////////////////////////////////////////////////
            List<SaleOrder> saleOrders = saleOrderRepository.findAllById(redInvoiceNewDataDTO.getSaleOrderId());
            for (SaleOrder saleOrder : saleOrders) {
                if(orderNumber == null) orderNumber = saleOrder.getOrderNumber();
                else orderNumber = orderNumber + "," + saleOrder.getOrderNumber();
                saleOrder.setUsedRedInvoice(true);
                saleOrder.setRedInvoiceCompanyName(redInvoiceNewDataDTO.getOfficeWorking());
                saleOrder.setRedInvoiceTaxCode(redInvoiceNewDataDTO.getTaxCode());
                saleOrder.setRedInvoiceAddress(redInvoiceNewDataDTO.getOfficeAddress());
                saleOrder.setRedInvoiceRemark(redInvoiceNewDataDTO.getNoteRedInvoice());
                saleOrderRepository.save(saleOrder);
            }
        }
        Float totalMoney = 0F;
        for (ProductDataDTO productDataDTO : redInvoiceNewDataDTO.getProductDataDTOS()) {
            totalMoney += Math.round((((productDataDTO.getPriceNotVat() * productDataDTO.getQuantity()) * productDataDTO.getVat()) / 100) + (productDataDTO.getPriceNotVat() * productDataDTO.getQuantity()));
        }
        redInvoiceRecord.setShopId(shopId);
        redInvoiceRecord.setTotalMoney(totalMoney);
        redInvoiceRecord.setNote(redInvoiceNewDataDTO.getNoteRedInvoice());
        redInvoiceRecord.setOrderNumbers(orderNumber);
        RedInvoice redInvoice = redInvoiceRepository.save(redInvoiceRecord);
        redInvoiceDTO = modelMapper.map(redInvoice, RedInvoiceDTO.class);
        for (ProductDataDTO productDataDTO : redInvoiceNewDataDTO.getProductDataDTOS()) {
            RedInvoiceDetail redInvoiceDetailRecord = modelMapper.map(redInvoiceNewDataDTO, RedInvoiceDetail.class);
            redInvoiceDetailRecord.setRedInvoiceId(redInvoiceRecord.getId());
            redInvoiceDetailRecord.setShopId(shopId);
            redInvoiceDetailRecord.setProductId(productDataDTO.getProductId());
            redInvoiceDetailRecord.setQuantity(productDataDTO.getQuantity().intValue());
            redInvoiceDetailRecord.setPrice((float) Math.round(((productDataDTO.getPriceNotVat() * productDataDTO.getVat()) / 100) + productDataDTO.getPriceNotVat()));
            redInvoiceDetailRecord.setPriceNotVat(productDataDTO.getPriceNotVat());
            redInvoiceDetailRecord.setAmount((float) Math.round((((productDataDTO.getPriceNotVat() * productDataDTO.getQuantity()) * productDataDTO.getVat()) / 100) + (productDataDTO.getPriceNotVat() * productDataDTO.getQuantity())));
            redInvoiceDetailRecord.setAmountNotVat((float) Math.round(productDataDTO.getPriceNotVat() * productDataDTO.getQuantity()));
            redInvoiceDetailRecord.setCreatedBy(userDTO.getLastName() + " " + userDTO.getFirstName());
            redInvoiceDetailRecord.setNote(productDataDTO.getNoteRedInvoiceDetail());
            redInvoiceDetailRepository.save(redInvoiceDetailRecord);
        }

        if(redInvoiceNewDataDTO.getCustomerId()!=null) {
            CustomerDTO customer = customerClient.getCustomerByIdV1(redInvoiceNewDataDTO.getCustomerId()).getData();
            CustomerRequest request = modelMapper.map(customer, CustomerRequest.class);
            request.setId(redInvoiceNewDataDTO.getCustomerId());
            request.setWorkingOffice(redInvoiceNewDataDTO.getOfficeWorking());
            request.setTaxCode(redInvoiceNewDataDTO.getTaxCode());
            request.setOfficeAddress(redInvoiceNewDataDTO.getOfficeAddress());
            customerClient.updateFeignV1(redInvoiceNewDataDTO.getCustomerId(), request);
        }

        return redInvoiceDTO;
    }

    public Boolean checkRedInvoiceNumber(String redInvoiceNumber) {
        String checkRedInvoice = redInvoiceRepository.checkRedInvoice(redInvoiceNumber);
        if (!(checkRedInvoice == null)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ResponseMessage deleteByIds(List<Long> ids) {
        if (ids !=null && !ids.isEmpty()) {
            for (Long id : ids) {
                String saleOrderNumber = redInvoiceRepository.getIdSaleOrder(id);
                if(saleOrderNumber != null && !saleOrderNumber.trim().equals("")) {
                    List<SaleOrder> saleOrders = saleOrderRepository.findSaleOrderByOrderCode(Arrays.asList(saleOrderNumber.split(",", -1)));
                    for (SaleOrder saleOrder : saleOrders) {
                        saleOrder.setUsedRedInvoice(false);
                        saleOrderRepository.save(saleOrder);
                    }
                }
                try {
                    redInvoiceRepository.deleteById(id);
                    List<Long> idRedList = redInvoiceDetailRepository.getAllRedInvoiceIds(id);
                    for (Long idRed : idRedList) {
                        redInvoiceDetailRepository.deleteById(idRed);
                    }
                }catch (Exception exception){
                    System.out.println(exception.getMessage());
                    LogFile.logToFile("", "", LogLevel.INFO, null, exception.getMessage());
                }
            }
        }
        return ResponseMessage.DELETE_SUCCESSFUL;
    }

    private List<HDDTExcelDTO> getDataHddtExcel(String ids) {
        List<Long> list = null;
        if(ids != null)
            list = Stream.of(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<HddtExcel> hddtExcels = hddtExcelRepository.getDataHddtExcel(list);
        List<HDDTExcelDTO> HDDTExcelDTOS = new ArrayList<>();
        if(hddtExcels == null || hddtExcels.isEmpty()) return HDDTExcelDTOS;

        List<CustomerDTO> customers = null;
        List<Long> customerIds = hddtExcels.stream().map(HddtExcel::getCustomerId).collect(Collectors.toList());
        if(!customerIds.isEmpty()) customers = customerClient.getCustomerInfoV1( new ArrayList<>(), null, customerIds);
        Map<Long, CustomerDTO> lstCustomer = new HashMap<>();
        if(customers!=null) {
            for(CustomerDTO customer: customers) {
                if(!lstCustomer.containsKey(customer.getId())) lstCustomer.put(customer.getId(), customer);
            }
        }

        List<ShopDTO> shops = shopClient.getAllShopToRedInvoiceV1( hddtExcels.stream().map(HddtExcel::getShopId).collect(Collectors.toList())).getData();
        Map<Long, ShopDTO> shopDTOS = new HashMap<>();
        for(ShopDTO shop: shops) {
            if(!shopDTOS.containsKey(shop.getId())) shopDTOS.put(shop.getId(), shop);
        }

        HDDTExcelDTOS = hddtExcels.stream().map(data -> {
            HDDTExcelDTO hddtExcelDTO = modelMapper.map(data, HDDTExcelDTO.class);
            //shop
            if (data.getShopId() != null) {
                ShopDTO shopDTO = shopDTOS.get(data.getShopId());
                if (shopDTO != null)
                    hddtExcelDTO.setShopCode(shopDTO.getShopCode());
            }
            //customer
            if (data.getCustomerId() != null) {
                CustomerDTO customerDTO = lstCustomer.get(data.getCustomerId());
                if (customerDTO != null) {
                    hddtExcelDTO.setCustomerCode(customerDTO.getCustomerCode());
                    hddtExcelDTO.setMobiPhone(customerDTO.getMobiPhone());
                }
            }
            hddtExcelDTO.setTotalAmount(data.getQuantity() * data.getPriceNotVat());
            if(data.getPrice()!=null && data.getPrice() > 0) {
            Double gtgt = (data.getPrice() - data.getPriceNotVat()) / data.getPriceNotVat() * 100;
            gtgt = (double) Math.round((gtgt));
            if(gtgt != null) hddtExcelDTO.setGTGT(gtgt);
            }
            return hddtExcelDTO;
        }).collect(Collectors.toList());
        Collections.sort(HDDTExcelDTOS, Comparator.comparing(HDDTExcelDTO::getInvoiceNumber, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(HDDTExcelDTO::getProductCode, Comparator.nullsLast(Comparator.naturalOrder())));

        return HDDTExcelDTOS;
    }

    private List<HDDTO> getDataHdDvkh(String ids) {
        List<Long> redIds = Arrays.stream(ids.split(",")).map(item -> {Long rs = 0L; try{rs = Long.parseLong(item);}catch(Exception e){} return rs; }).distinct().collect(Collectors.toList());
        List<RedInvoice> redInvoices = redInvoiceRepository.findAllById(redIds);

        List<HDDTO> hddtos = new ArrayList<>();
        if(redInvoices == null || redInvoices.isEmpty()) return hddtos;

        List<ShopDTO> shops = shopClient.getAllShopToRedInvoiceV1( redInvoices.stream().map(RedInvoice::getShopId).collect(Collectors.toList())).getData();
        Map<Long, ShopDTO> shopDTOS = new HashMap<>();
        for(ShopDTO shop: shops) {
            if(!shopDTOS.containsKey(shop.getId())) shopDTOS.put(shop.getId(), shop);
        }
        for(RedInvoice redInvoice:redInvoices) {
            HDDTO hddto = modelMapper.map(redInvoice, HDDTO.class);
            hddto.setFullName(redInvoice.getOfficeWorking());
            if (redInvoice.getShopId() != null) {
                ShopDTO shopDTO = shopDTOS.get(redInvoice.getShopId());
                if (shopDTO != null)
                    hddto.setShopCode(shopDTO.getShopCode());
            }
            hddtos.add(hddto);
        }
        return hddtos;
    }

    private List<CTDTO> getDataCTDvkh(String ids) {
        List<Long> list = null;
        if(ids != null)
            list = Stream.of(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<CTDVKH> ctdvkhs = ctdvkhRepository.getCTDVKHByIds(list);
        List<CTDTO> ctdtos = new ArrayList<>();
        if(ctdvkhs == null || ctdvkhs.isEmpty()) return ctdtos;

        List<ShopDTO> shops = shopClient.getAllShopToRedInvoiceV1( ctdvkhs.stream().map(CTDVKH::getShopId).collect(Collectors.toList())).getData();
        Map<Long, ShopDTO> shopDTOS = new HashMap<>();
        for(ShopDTO shop: shops) {
            if(!shopDTOS.containsKey(shop.getId())) shopDTOS.put(shop.getId(), shop);
        }

        ctdtos = ctdvkhs.stream().map(data -> {
            CTDTO ctdto = modelMapper.map(data, CTDTO.class);
            if (data.getShopId() != null) {
                ShopDTO shopDTO = shopDTOS.get(data.getShopId());
                if (shopDTO != null)
                    ctdto.setShopCode(shopDTO.getShopCode());
            }
            return ctdto;
        }).collect(Collectors.toList());
        return ctdtos;
    }

    @Override
    public ByteArrayInputStream exportExcel(String ids, Integer type) throws IOException {
        if (ids == null || (ids != null && ids.trim().equals(""))) {
            throw new ValidateException(ResponseMessage.RED_INVOICE_NUMBER_NOT_FOUND);
        }

        if (type == 1) {
            List<HDDTO> hddtos = this.getDataHdDvkh(ids);
            List<CTDTO> ctdtos = this.getDataCTDvkh(ids);
            HVKHExcel hvkhExcel = new HVKHExcel(hddtos, ctdtos);
            return hvkhExcel.export();
        } else {
            List<HDDTExcelDTO> hddtExcelDTOS = this.getDataHddtExcel(ids);
            HDDTExcel hddtExcel = new HDDTExcel(hddtExcelDTOS);

            return hddtExcel.export();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage updateRed(List<RedInvoiceRequest> redInvoiceRequests, Long userId) {
        UserDTO userDTO = userClient.getUserByIdV1(userId);
        String userName = userDTO.getLastName() + " " + userDTO.getFirstName();
        if (redInvoiceRequests.isEmpty()) {
            throw new ValidateException(ResponseMessage.RED_INVOICE_NUMBER_NOT_FOUND);
        }

        for (int i = 0; i < redInvoiceRequests.size(); i++) {
            if (redInvoiceRequests.get(i).getId() == null) {
                throw new ValidateException(ResponseMessage.RED_INVOICE_ID_IS_NULL);
            }
            if (redInvoiceRequests.get(i).getInvoiceNumber().equals("") || redInvoiceRequests.get(i).getInvoiceNumber() == null) {
                throw new ValidateException(ResponseMessage.RED_INVOICE_NUMBER_IS_NULL);
            }
            String checkRedInvoice = redInvoiceRepository.checkRedInvoice(redInvoiceRequests.get(i).getInvoiceNumber());
            if (!(checkRedInvoice == null)) {
                throw new ValidateException(ResponseMessage.RED_INVOICE_CODE_HAVE_EXISTED, checkRedInvoice);
            } else {
                RedInvoice redInvoice = redInvoiceRepository.findRedInvoiceById(redInvoiceRequests.get(i).getId());
                redInvoice.setId(redInvoiceRequests.get(i).getId());
                redInvoice.setInvoiceNumber(redInvoiceRequests.get(i).getInvoiceNumber());
                redInvoice.setUpdatedBy(userName);
                redInvoiceRepository.save(redInvoice);
            }
        }
        return ResponseMessage.CREATED;
    }

    @Override
    public CoverResponse<List<ProductDataResponse>, PrintDataRedInvoiceResponse> getDataPrint(Long idRedInvoice, Long shopId) {
        if (idRedInvoice == null)
            throw new ValidateException(ResponseMessage.RED_INVOICE_NUMBER_IS_NULL);
        List<ProductDataResponse> productDTOS = new ArrayList<>();
        PrintDataRedInvoiceResponse response = new PrintDataRedInvoiceResponse();
        Double amountNotVat = 0.0;
        Double amount = 0.0;
        ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
        RedInvoice redInvoice = redInvoiceRepository.findById(idRedInvoice).get();
        List<Product> productList = productRepository.findAll();
        if (redInvoice == null)
            throw new ValidateException(ResponseMessage.RED_INVOICE_NOT_FOUND);
        List<RedInvoiceDetail> redInvoiceDetailDTOS = redInvoiceDetailRepository.getAllByRedInvoiceId(idRedInvoice);
        if (redInvoiceDetailDTOS.size() == 0)
            throw new ValidateException(ResponseMessage.RED_INVOICE_DETAIL_NOT_EXISTS);

        //Khách hàng có thể có hoặc không
        if(redInvoice.getCustomerId()!=null) {
            CustomerDTO customerDTO = customerClient.getCustomerByIdV1(redInvoice.getCustomerId()).getData();
            if (customerDTO == null)
                throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
            response.setCustomerName(customerDTO.getLastName() + " " + customerDTO.getFirstName());
            response.setCustomerAddress(customerDTO.getAddress());
            response.setCustomerPhone(customerDTO.getMobiPhone());
        }

        for (RedInvoiceDetail detailDTO : redInvoiceDetailDTOS) {
            ProductDataResponse productDTO = new ProductDataResponse();
            if (productList.size() > 0) {
                for (Product product : productList) {
                    if (product.getId().equals(detailDTO.getProductId())) {
                        if (product == null)
                            throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
                        productDTO.setProductName(product.getProductName());
                        productDTO.setProductCode(product.getProductCode());
                        productDTO.setUom1(product.getUom1());
                    }
                }
            }
            productDTO.setPrice(roundValue(detailDTO.getPrice().doubleValue()));
            productDTO.setQuantity(detailDTO.getQuantity());
            productDTO.setIntoMoney(roundValue(detailDTO.getAmount().doubleValue()));
            productDTO.setNote(detailDTO.getNote());
            productDTOS.add(productDTO);
            amountNotVat += roundValue(detailDTO.getAmountNotVat().doubleValue());
            amount += productDTO.getIntoMoney();
        }
        response.setRedInvoiceNumber(redInvoice.getInvoiceNumber());
        response.setDatePrint(redInvoice.getPrintDate());
        response.setShopName(shopDTO.getShopName());
        response.setShopAddress(shopDTO.getAddress());
        response.setShopTel(shopDTO.getPhone());
        response.setTotalAmountNumber(amount);
        response.setAmount(amountNotVat);
        response.setValueAddedTax(amount - amountNotVat);
        response.setTotalAmountString(convert(amount.intValue()));

        return new CoverResponse<>(productDTOS, response);
    }

    public String createRedInvoiceCode() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private static final String[] tensNames = {
            "",
            " mười",
            " hai mươi",
            " ba mươi",
            " bốn mươi",
            " năm mươi",
            " sáu mươi",
            " bảy mươi",
            " tám mươi",
            " chín mươi"
    };

    private static final String[] numNames = {
            "",
            " một",
            " hai",
            " ba",
            " bốn",
            " năm",
            " sáu",
            " bảy",
            " tám",
            " chín",
            " mười",
            " mười một",
            " mười hai",
            " mười ba",
            " mười bốn",
            " mười lăm",
            " mười sáu",
            " mười bảy",
            " mười tám",
            " mười chín"
    };

    private static String convertLessThanOneThousand(int number) {
        String soFar;

        if (number % 100 < 20) {
            soFar = numNames[number % 100];
            number /= 100;
        } else {
            soFar = numNames[number % 10];
            number /= 10;

            soFar = tensNames[number % 10] + soFar;
            number /= 10;
        }
        if (number == 0) return soFar;
        return numNames[number] + " trăm" + soFar;
    }


    public static String convert(int number) {
        // 0 to 999 999 999 999
        if (number == 0) {
            return "không";
        }

        String snumber = Float.toString(number);

        // pad with "0"
        String mask = "000000000000";
        DecimalFormat df = new DecimalFormat(mask);
        snumber = df.format(number);

        // XXXnnnnnnnnn
        int billions = Integer.parseInt(snumber.substring(0, 3));
        // nnnXXXnnnnnn
        int millions = Integer.parseInt(snumber.substring(3, 6));
        // nnnnnnXXXnnn
        int hundredThousands = Integer.parseInt(snumber.substring(6, 9));
        // nnnnnnnnnXXX
        int thousands = Integer.parseInt(snumber.substring(9, 12));

        String tradBillions;
        switch (billions) {
            case 0:
                tradBillions = "";
                break;
            case 1:
                tradBillions = convertLessThanOneThousand(billions)
                        + " tỷ ";
                break;
            default:
                tradBillions = convertLessThanOneThousand(billions)
                        + " tỷ ";
        }
        String result = tradBillions;

        String tradMillions;
        switch (millions) {
            case 0:
                tradMillions = "";
                break;
            case 1:
                tradMillions = convertLessThanOneThousand(millions)
                        + " triệu ";
                break;
            default:
                tradMillions = convertLessThanOneThousand(millions)
                        + " triệu ";
        }
        result = result + tradMillions;

        String tradHundredThousands;
        switch (hundredThousands) {
            case 0:
                tradHundredThousands = "";
                break;
            case 1:
                tradHundredThousands = "một ngàn ";
                break;
            default:
                tradHundredThousands = convertLessThanOneThousand(hundredThousands)
                        + " nghìn ";
        }
        result = result + tradHundredThousands;

        String tradThousand;
        tradThousand = convertLessThanOneThousand(thousands);
        result = result + tradThousand;

        // remove extra spaces!
        String text = result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");;
        String firstLetStr = text.substring(0, 1);
        firstLetStr = firstLetStr.toUpperCase();

        String remLetStr = text.substring(1);

        return firstLetStr + remLetStr + " đồng.";
    }

    private double roundValue(Double value){
        if(value == null) return 0;
        return Math.round(value);
    }
}
