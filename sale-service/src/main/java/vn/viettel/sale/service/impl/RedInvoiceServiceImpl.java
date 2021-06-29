package vn.viettel.sale.service.impl;

import org.apache.commons.lang.StringUtils;
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
import vn.viettel.core.jms.JMSSender;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.utils.JMSType;
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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    JMSSender jmsSender;

    @Override
    public CoverResponse<Page<RedInvoiceDTO>, TotalRedInvoice> getAll(Long shopId, String searchKeywords, Date fromDate, Date toDate, String invoiceNumber, Pageable pageable) {

        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);
        List<Long> ids = customerClient.getIdCustomerBySearchKeyWordsV1(searchKeywords).getData();
        Page<RedInvoice> redInvoices;

        if (searchKeywords.equals("")) {
            redInvoices = repository.findAll(Specification.where(RedInvoiceSpecification.hasFromDateToDate(fromDate, toDate))
                    .and(RedInvoiceSpecification.hasShopId(shopId))
                    .and(RedInvoiceSpecification.hasInvoiceNumber(invoiceNumber)), pageable);
        } else {
            if (ids.size() == 0)
                redInvoices = repository.findAll(Specification.where(RedInvoiceSpecification.hasInvoiceNumber("-1")), pageable);
            else {
                redInvoices = repository.findAll(Specification.where(RedInvoiceSpecification.hasCustomerId(ids))
                        .and(RedInvoiceSpecification.hasShopId(shopId))
                        .and(RedInvoiceSpecification.hasFromDateToDate(fromDate, toDate))
                        .and(RedInvoiceSpecification.hasInvoiceNumber(invoiceNumber)), pageable);
            }
        }

        Page<RedInvoiceDTO> redInvoiceDTOS = redInvoices.map(red -> modelMapper.map(red, RedInvoiceDTO.class));
        TotalRedInvoice totalRedInvoice = new TotalRedInvoice();

        redInvoiceDTOS.stream().forEach(redInvoiceDTO -> {
            List<RedInvoiceDetailDTO> redInvoiceDetails = redInvoiceDetailService.getRedInvoiceDetailByRedInvoiceId(redInvoiceDTO.getId());
            Double amount = 0D;
            Double amountNotVat = 0D;
            Double totalMoney = 0D;
            for (RedInvoiceDetailDTO detail : redInvoiceDetails) {
                amount += detail.getAmount();
                amountNotVat += detail.getAmountNotVat();
            }

            totalMoney = (double)Math.round(redInvoiceDTO.getTotalMoney());
            redInvoiceDTO.setTotalMoney(totalMoney);
            redInvoiceDTO.setAmountNotVat((double)Math.round(amountNotVat));
            redInvoiceDTO.setAmountGTGT((double)Math.round(amount - amountNotVat));
            totalRedInvoice.addAmountNotVat(redInvoiceDTO.getAmountNotVat())
                    .addAmountGTGT(redInvoiceDTO.getAmountGTGT())
                    .addTotalQuantity(redInvoiceDTO.getTotalQuantity())
                    .addTotalMoney((double)Math.round(redInvoiceDTO.getTotalMoney()));
        });

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

            List<Long> idCustomerList = new ArrayList<>();
            Long customerId;
            CustomerDTO customerDTO = null;
            List<RedInvoiceDataDTO> dtos = new ArrayList<>();
            List<SaleOrder> saleOrdersList = new ArrayList<>();
            Long idCus = null;
            for (String ids : orderCodeList) {
                customerId = saleOrderRepository.getCustomerCode(ids);
                idCustomerList.add(customerId);
            }
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
            List<Price> priceList = productPriceRepository.findAllByStatusAndPriceType(1, 1);
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
                                    dataDTO.setValueAddedTax(((price.getPriceNotVat() * detail.getQuantity()) * price.getVat()) / 100);
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
                totalQuantity += dataDTO.getQuantity();
                totalAmount += dataDTO.getAmount();
                totalValueAddedTax += dataDTO.getValueAddedTax();

            }
            TotalRedInvoiceResponse totalRedInvoiceResponse = new TotalRedInvoiceResponse(
                    totalQuantity, totalAmount, totalValueAddedTax, shopId, customerIds, customerCodes, customerName, null, null, officeWorking, officeAddress
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
        boolean check = false;
        if (redInvoiceNewDataDTO.getProductDataDTOS().size() > 1) {
            for (int i = 0; i < redInvoiceNewDataDTO.getProductDataDTOS().size(); i++) {
                for (int j = 1; j < redInvoiceNewDataDTO.getProductDataDTOS().size(); j++) {
                    if (redInvoiceNewDataDTO.getProductDataDTOS().get(i).getGroupVat().equals(redInvoiceNewDataDTO.getProductDataDTOS().get(j).getGroupVat())) {
                        check = true;
                    } else {
                        throw new ValidateException(ResponseMessage.INDUSTRY_ARE_NOT_DIFFERENT);
                    }
                }
            }
        } else {
            check = true;
        }
        RedInvoiceDTO redInvoiceDTO = null;
        List<Long> lstSaleOrderIds = new ArrayList<Long>();
        if (check) {
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

            if (redInvoiceNewDataDTO.getNoteRedInvoice().length() > 4000)
                throw new ValidateException(ResponseMessage.MAX_LENGTH_STRING);

            String orderNumber = null;
            if (redInvoiceNewDataDTO.getSaleOrderId().size() > 0) {
                orderNumber = saleOrderRepository.findByIdSale(redInvoiceNewDataDTO.getSaleOrderId().get(0));
                List<Long> idSaleOrderList = new ArrayList<>();
                for (int i = 1; i < redInvoiceNewDataDTO.getSaleOrderId().size(); i++) {
                    orderNumber = orderNumber + "," + saleOrderRepository.findByIdSale(redInvoiceNewDataDTO.getSaleOrderId().get(i));
                }
                CustomerDTO customer = customerClient.getCustomerByIdV1(redInvoiceNewDataDTO.getCustomerId()).getData();
                CustomerRequest request = modelMapper.map(customer, CustomerRequest.class);
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                request.setId(redInvoiceNewDataDTO.getCustomerId());
                request.setWorkingOffice(redInvoiceNewDataDTO.getOfficeWorking());
                request.setTaxCode(redInvoiceNewDataDTO.getTaxCode());
                request.setOfficeAddress(redInvoiceNewDataDTO.getOfficeAddress());
                customerClient.updateFeignV1(redInvoiceNewDataDTO.getCustomerId(), request);

                ////////////////////////////////////////////////////////////////
                for (int j = 0; j < redInvoiceNewDataDTO.getSaleOrderId().size(); j++) {
                    idSaleOrderList.add(redInvoiceNewDataDTO.getSaleOrderId().get(j));
                }

                ////////////////////////////////////////////////////////////////
                for (Long idSaleOrder : idSaleOrderList) {
                    SaleOrder saleOrder = saleOrderRepository.findById(idSaleOrder).get();
                    saleOrder.setUsedRedInvoice(true);
                    saleOrder.setId(idSaleOrder);
                    saleOrder.setRedInvoiceCompanyName(redInvoiceNewDataDTO.getOfficeWorking());
                    saleOrder.setRedInvoiceTaxCode(redInvoiceNewDataDTO.getTaxCode());
                    saleOrder.setRedInvoiceCompanyName(redInvoiceNewDataDTO.getOfficeAddress());
                    saleOrder.setRedInvoiceRemark(redInvoiceNewDataDTO.getNoteRedInvoice());
                    saleOrderRepository.save(saleOrder);
                    lstSaleOrderIds.add(saleOrder.getId());
                }
            }
            Float totalMoney = 0F;
            for (ProductDataDTO productDataDTO : redInvoiceNewDataDTO.getProductDataDTOS()) {
                totalMoney += ((((productDataDTO.getPriceNotVat() * productDataDTO.getQuantity()) * productDataDTO.getVat()) / 100) + (productDataDTO.getPriceNotVat() * productDataDTO.getQuantity()));
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
                redInvoiceDetailRecord.setPrice(((productDataDTO.getPriceNotVat() * productDataDTO.getVat()) / 100) + productDataDTO.getPriceNotVat());
                redInvoiceDetailRecord.setPriceNotVat(productDataDTO.getPriceNotVat());
                redInvoiceDetailRecord.setAmount((((productDataDTO.getPriceNotVat() * productDataDTO.getQuantity()) * productDataDTO.getVat()) / 100) + (productDataDTO.getPriceNotVat() * productDataDTO.getQuantity()));
                redInvoiceDetailRecord.setAmountNotVat(productDataDTO.getPriceNotVat() * productDataDTO.getQuantity());
                redInvoiceDetailRecord.setCreatedBy(userDTO.getLastName() + " " + userDTO.getFirstName());
                if (productDataDTO.getNoteRedInvoiceDetail().length() > 4000) {
                    throw new ValidateException(ResponseMessage.MAX_LENGTH_STRING);
                }
                redInvoiceDetailRecord.setNote(productDataDTO.getNoteRedInvoiceDetail());
                redInvoiceDetailRepository.save(redInvoiceDetailRecord);
            }
        }
        
        sendSynRequest(lstSaleOrderIds);
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
        if (ids.isEmpty()) {
            throw new ValidateException(ResponseMessage.RED_INVOICE_ID_IS_NULL);
        } else {
        	List<Long> lstSaleOrderIds = new ArrayList<Long>();
            for (Long id : ids) {
                String saleOrderNumber = redInvoiceRepository.getIdSaleOrder(id);
                if (saleOrderNumber.isEmpty() || saleOrderNumber == null)
                    throw new ValidateException(ResponseMessage.SALE_ORDER_NUMBER_NOT_FOUND);
                String[] orderNumber = saleOrderNumber.split(",", -1);
                List<Long> idsSaleOrder = new ArrayList<>();
                for (String order : orderNumber) {
                    Long idSale = saleOrderRepository.findSaleOrderIdByOrderCode(order);
                    idsSaleOrder.add(idSale);
                }
                for (Long idSaleOrder : idsSaleOrder) {
                    SaleOrder saleOrder = saleOrderRepository.findById(idSaleOrder).get();
                    saleOrder.setId(idSaleOrder);
                    saleOrder.setUsedRedInvoice(false);
                    saleOrderRepository.save(saleOrder);
                    lstSaleOrderIds.add(idSaleOrder);
                }
                redInvoiceRepository.deleteById(id);
                List<BigDecimal> idRedList = redInvoiceDetailRepository.getAllRedInvoiceIds(id);
                for (BigDecimal idRed : idRedList) {
                    redInvoiceDetailRepository.deleteById(idRed.longValue());
                }

            }
            
          sendSynRequest(lstSaleOrderIds);
        }
        return ResponseMessage.DELETE_SUCCESSFUL;
    }

    private List<HDDTExcelDTO> getDataHddtExcel(String ids) {
        List<HddtExcel> hddtExcels = hddtExcelRepository.getDataHddtExcel(ids);
        List<HDDTExcelDTO> HDDTExcelDTOS = null;
        Map<Integer, CustomerDTO> lstCustomer = customerClient.getAllCustomerToRedInvocieV1().getData();
        Map<Integer, ShopDTO> shopDTOS = shopClient.getAllShopToRedInvoiceV1().getData();
        HDDTExcelDTOS = hddtExcels.stream().map(data -> {
            HDDTExcelDTO hddtExcelDTO = modelMapper.map(data, HDDTExcelDTO.class);
            //shop
            if (data.getShopId() != null) {
                ShopDTO shopDTO = shopDTOS.get(Math.toIntExact(data.getShopId()));
                if (shopDTO != null)
                    hddtExcelDTO.setShopCode(shopDTO.getShopCode());
            }
            //customer
            if (data.getCustomerId() != null) {
                CustomerDTO customerDTO = lstCustomer.get(Math.toIntExact(data.getCustomerId()));
                if (customerDTO != null) {
                    hddtExcelDTO.setCustomerCode(customerDTO.getCustomerCode());
                    hddtExcelDTO.setMobiPhone(customerDTO.getMobiPhone());
                }
            }
            hddtExcelDTO.setTotalAmount(data.getQuantity() * data.getPriceNotVat());
            Double gtgt = (data.getPrice() - data.getPriceNotVat()) / data.getPriceNotVat() * 100;
            gtgt = Math.ceil((gtgt * 1000) / 1000);
            hddtExcelDTO.setGTGT(gtgt);
            return hddtExcelDTO;
        }).collect(Collectors.toList());
        return HDDTExcelDTOS;
    }

    private List<HDDTO> getDataHdDvkh(String ids) {
        List<RedInvoice> redInvoices = redInvoiceRepository.getRedInvoiceByIds(ids);
        List<HDDTO> hddtos = null;
        Map<Integer, CustomerDTO> lstCustomer = customerClient.getAllCustomerToRedInvocieV1().getData();
        Map<Integer, ShopDTO> shopDTOS = shopClient.getAllShopToRedInvoiceV1().getData();
        hddtos = redInvoices.stream().map(data -> {
            HDDTO hddto = modelMapper.map(data, HDDTO.class);
            String fullname = "";
            if (data.getCustomerId() != null) {
                CustomerDTO customerDTO = lstCustomer.get(Math.toIntExact(data.getCustomerId()));
                if (customerDTO != null) {
                    fullname += customerDTO.getLastName() + " " + customerDTO.getFirstName();
                    hddto.setFullName(fullname);
                }
            }
            if (data.getShopId() != null) {
                ShopDTO shopDTO = shopDTOS.get(Math.toIntExact(data.getShopId()));
                if (shopDTO != null)
                    hddto.setShopCode(shopDTO.getShopCode());
            }
            return hddto;
        }).collect(Collectors.toList());
        return hddtos;
    }

    private List<CTDTO> getDataCTDvkh(String ids) {
        List<CTDVKH> ctdvkhs = ctdvkhRepository.getCTDVKHByIds(ids);
        List<CTDTO> ctdtos = null;
        Map<Integer, ShopDTO> shopDTOS = shopClient.getAllShopToRedInvoiceV1().getData();
        ctdtos = ctdvkhs.stream().map(data -> {
            CTDTO ctdto = modelMapper.map(data, CTDTO.class);
            if (data.getShopId() != null) {
                ShopDTO shopDTO = shopDTOS.get(Math.toIntExact(data.getShopId()));
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
        Float amountNotVat = 0F;
        Float amount = 0F;
        ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
        RedInvoice redInvoice = redInvoiceRepository.findById(idRedInvoice).get();
        List<Product> productList = productRepository.findAll();
        List<String> redInvoiceNumberList = redInvoiceRepository.findRedInvoiceNumberById();
        if (redInvoice == null)
            throw new ValidateException(ResponseMessage.RED_INVOICE_NOT_FOUND);
        List<RedInvoiceDetail> redInvoiceDetailDTOS = redInvoiceDetailRepository.getAllByRedInvoiceId(idRedInvoice);
        if (redInvoiceDetailDTOS.size() == 0)
            throw new ValidateException(ResponseMessage.RED_INVOICE_DETAIL_NOT_EXISTS);
        CustomerDTO customerDTO = customerClient.getCustomerByIdV1(redInvoice.getCustomerId()).getData();
        if (customerDTO == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
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
            productDTO.setPrice(detailDTO.getPrice());
            productDTO.setQuantity(detailDTO.getQuantity());
            productDTO.setIntoMoney(detailDTO.getAmount());
            productDTO.setNote(detailDTO.getNote());
            productDTOS.add(productDTO);
            amountNotVat += detailDTO.getAmountNotVat();
            amount += detailDTO.getAmount();
        }
        if (redInvoiceNumberList.size() > 0){
            for (String s : redInvoiceNumberList) {
                if (redInvoice.getInvoiceNumber().equals(s)) {
                    response.setRedInvoiceNumber(s);
                }
            }
        }
        response.setDatePrint(redInvoice.getPrintDate());
        response.setShopName(shopDTO.getShopName());
        response.setShopAddress(shopDTO.getAddress());
        response.setShopTel(shopDTO.getPhone());
        response.setCustomerName(customerDTO.getLastName() + " " + customerDTO.getFirstName());
        response.setCustomerAddress(customerDTO.getAddress());
        response.setCustomerPhone(customerDTO.getMobiPhone());
        response.setTotalAmountNumber(amount);
        response.setAmount(amountNotVat);
        response.setValueAddedTax(amount - amountNotVat);
        response.setTotalAmountString(convert(amount.intValue()));

        return new CoverResponse<>(productDTOS, response);
    }

    public String createRedInvoiceCode() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
    
	private void sendSynRequest(List<Long> lstIds) {
		try {
			if(!lstIds.isEmpty()) {
				jmsSender.sendMessage(JMSType.sale_order, lstIds);
			}
		} catch (Exception ex) {
			LogFile.logToFile("vn.viettel.sale.service.impl.RedInvoiceServiceImpl.sendSynRequest", JMSType.sale_order, LogLevel.ERROR, null, "has error when encode data " + ex.getMessage());
		}
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
            " bốm",
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
                tradHundredThousands = "một ngàn đồng ";
                break;
            default:
                tradHundredThousands = convertLessThanOneThousand(hundredThousands)
                        + " nghìn đồng ";
        }
        result = result + tradHundredThousands;

        String tradThousand;
        tradThousand = convertLessThanOneThousand(thousands);
        result = result + tradThousand;

        // remove extra spaces!
        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
    }

}
