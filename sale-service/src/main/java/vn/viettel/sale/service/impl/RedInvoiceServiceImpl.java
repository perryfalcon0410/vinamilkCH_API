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
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.excel.HDDTExcel;
import vn.viettel.sale.excel.HVKHExcel;
import vn.viettel.sale.messaging.RedInvoiceRequest;
import vn.viettel.sale.messaging.TotalRedInvoice;
import vn.viettel.sale.messaging.TotalRedInvoiceResponse;
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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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


    @Override
    public CoverResponse<Page<RedInvoiceDTO>, TotalRedInvoice> getAll(Long shopId, String searchKeywords, Date fromDate, Date toDate, String invoiceNumber, Pageable pageable) {

        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);
        List<Long> ids = customerClient.getIdCustomerBySearchKeyWordsV1(searchKeywords).getData();
        Page<RedInvoice> redInvoices = null;

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
            List<RedInvoiceDetailDTO> redInvoiceDetails = redInvoiceDetailService.getRedInvoiceDetailByRedInvoiceId(redInvoiceDTO.getId()).getData();
            Float amount = 0F;
            Float amountNotVat = 0F;
            for (RedInvoiceDetailDTO detail : redInvoiceDetails) {
                amount += detail.getAmount();
                amountNotVat += detail.getAmountNotVat();
            }

            redInvoiceDTO.setAmountNotVat(amountNotVat);
            redInvoiceDTO.setAmountGTGT(amount - amountNotVat);
            totalRedInvoice.addAmountNotVat(redInvoiceDTO.getAmountNotVat())
                    .addAmountGTGT(redInvoiceDTO.getAmountGTGT())
                    .addTotalQuantity(redInvoiceDTO.getTotalQuantity())
                    .addTotalMoney(redInvoiceDTO.getTotalMoney());
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
            if (check) {
                for (String saleOrderCode : orderCodeList) {
                    SaleOrder saleOrder = saleOrderRepository.findSaleOrderByCustomerIdAndOrderNumberAndType(idCus, saleOrderCode, 1);
                    saleOrdersList.add(saleOrder);
                }
                for (SaleOrder saleOrders : saleOrdersList) {
                    List<SaleOrderDetail> saleOrderDetailsList = saleOrderDetailRepository.findAllBySaleOrderId(saleOrders.getId());
                    for (SaleOrderDetail detail : saleOrderDetailsList) {
                        String saleOrderCode = saleOrderRepository.getSaleOrderCode(detail.getSaleOrderId());
                        Product product = productRepository.findByIdAndStatus(detail.getProductId(), 1);
                        SaleOrder order = saleOrderRepository.findSaleOrderByOrderNumber(saleOrderCode);
                        customerDTO = customerClient.getCustomerByIdV1(idCus).getData();
                        Price price = productPriceRepository.getProductPrice(product.getId(), customerDTO.getCustomerTypeId());

                        RedInvoiceDataDTO dataDTO = new RedInvoiceDataDTO();
                        dataDTO.setSaleOrderId(saleOrders.getId());
                        dataDTO.setProductId(product.getId());
                        dataDTO.setProductCode(product.getProductCode());
                        dataDTO.setProductName(product.getProductName());
                        dataDTO.setQuantity(detail.getQuantity().floatValue());
                        dataDTO.setGroupVat(product.getGroupVat());
                        dataDTO.setUom1(product.getUom1());
                        dataDTO.setUom2(product.getUom2());
                        dataDTO.setPriceNotVat(price.getPriceNotVat());
                        dataDTO.setPrice(price.getPrice());
                        dataDTO.setVat(price.getVat());
                        dataDTO.setAmountNotVat(price.getPriceNotVat() * detail.getQuantity());
                        dataDTO.setAmount(price.getPrice() * detail.getQuantity());
                        dataDTO.setValueAddedTax(((price.getPriceNotVat() * detail.getQuantity()) * price.getVat()) / 100);
                        int integerQuantity = detail.getQuantity() / product.getConvFact();
                        int residuaQuantity = detail.getQuantity() % product.getConvFact();
                        dataDTO.setNote(integerQuantity + "T" + residuaQuantity);
                        dtos.add(dataDTO);
                    }
                }
                for (int i = 0; i < dtos.size(); i++) {
                    for (int j = i + 1; j < dtos.size(); j++) {
                        if (dtos.get(i).getProductId().equals(dtos.get(j).getProductId())) {
                            Float count = dtos.get(i).getQuantity() + dtos.get(j).getQuantity();
                            dtos.get(i).setQuantity(count);
                            dtos.remove(j);
                            j--;
                        }
                    }
                }
            }
            Float totalQuantity = 0F;
            Float totalAmount = 0F;
            Float totalValueAddedTax = 0F;

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

        Long saleOrderId = saleOrderRepository.findSaleOrderIdByOrderCode(orderCode);
        List<BigDecimal> productIdtList = saleOrderDetailRepository.findAllBySaleOrderCode(saleOrderId);
        if (productIdtList.isEmpty()) {
            return new ArrayList<>();
        }
        List<ProductDetailDTO> productDetailDTOS = new ArrayList<>();
        for (BigDecimal ids : productIdtList) {
            Product product = productRepository.findByIdAndStatus(ids.longValue(), 1);
            if (product == null) {
                throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
            }
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            ProductDetailDTO dto = modelMapper.map(product, ProductDetailDTO.class);
            dto.setOrderNumber(orderCode);
            SaleOrderDetail saleOrderDetail = saleOrderDetailRepository.findSaleOrderDetailBySaleOrderIdAndProductIdAndIsFreeItem(saleOrderId, ids.longValue());
            dto.setQuantity(saleOrderDetail.getQuantity());
            dto.setUnitPrice(saleOrderDetail.getPrice());
            dto.setIntoMoney(saleOrderDetail.getQuantity().floatValue() * saleOrderDetail.getPrice());

            productDetailDTOS.add(dto);
        }
        if (productDetailDTOS.size() == 0) {
            return new ArrayList<>();
        }
        return productDetailDTOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage create(RedInvoiceNewDataDTO redInvoiceNewDataDTO, Long userId, Long shopId) {
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

        if (check) {
            UserDTO userDTO = userClient.getUserByIdV1(userId);

            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            RedInvoice redInvoiceRecord = modelMapper.map(redInvoiceNewDataDTO, RedInvoice.class);
            if (redInvoiceNewDataDTO.getRedInvoiceNumber().equals("") || redInvoiceNewDataDTO.getRedInvoiceNumber() == null) {
                String redInvoiceCode = this.createRedInvoiceCode();
                if (this.checkRedInvoiceNumber(redInvoiceCode)) {
                    throw new ValidateException(ResponseMessage.RED_INVOICE_CODE_HAVE_EXISTED);
                }else {
                    redInvoiceRecord.setInvoiceNumber(redInvoiceCode);
                }
            }else {
                if (this.checkRedInvoiceNumber(redInvoiceNewDataDTO.getRedInvoiceNumber())) {
                    throw new ValidateException(ResponseMessage.RED_INVOICE_CODE_HAVE_EXISTED);
                }else {
                    redInvoiceRecord.setInvoiceNumber(redInvoiceNewDataDTO.getRedInvoiceNumber());
                }
            }

            redInvoiceRecord.setShopId(shopId);
            String orderNumber = null;
            if (redInvoiceNewDataDTO.getSaleOrderId().size() > 0) {
                orderNumber = saleOrderRepository.findByIdSale(redInvoiceNewDataDTO.getSaleOrderId().get(0));
                List<Long> idSaleOrderList = new ArrayList<>();
                for (int i = 1; i < redInvoiceNewDataDTO.getSaleOrderId().size(); i++) {
                    orderNumber = orderNumber + "," + saleOrderRepository.findByIdSale(redInvoiceNewDataDTO.getSaleOrderId().get(i));
                }
                for (int j = 0; j < redInvoiceNewDataDTO.getSaleOrderId().size(); j++) {
                    idSaleOrderList.add(redInvoiceNewDataDTO.getSaleOrderId().get(j));
                }
                for (Long idSaleOrder : idSaleOrderList) {
                    SaleOrder saleOrder = saleOrderRepository.findById(idSaleOrder).get();
                    saleOrder.setUsedRedInvoice(true);
                    saleOrder.setId(idSaleOrder);
                    saleOrder.setRedInvoiceCompanyName(redInvoiceNewDataDTO.getOfficeWorking());
                    saleOrder.setRedInvoiceTaxCode(redInvoiceNewDataDTO.getTaxCode());
                    saleOrder.setRedInvoiceCompanyName(redInvoiceNewDataDTO.getOfficeAddress());
                    saleOrder.setRedInvoiceRemark(redInvoiceNewDataDTO.getNote());
                    saleOrderRepository.save(saleOrder);
                }
            }
            redInvoiceRecord.setOrderNumbers(orderNumber);
            redInvoiceRecord.setCreatedBy(userDTO.getLastName() + " " + userDTO.getFirstName());
            redInvoiceRepository.save(redInvoiceRecord);

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
                redInvoiceDetailRecord.setNote(productDataDTO.getNote());
                redInvoiceDetailRepository.save(redInvoiceDetailRecord);
            }
        }

        if (!check) {
            return ResponseMessage.ERROR;
        }
        return ResponseMessage.SUCCESSFUL;
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
            for (Long id : ids) {
                redInvoiceRepository.deleteById(id);
                List<BigDecimal> idRedList = redInvoiceDetailRepository.getAllRedInvoiceIds(id);
                for (BigDecimal idRed : idRedList) {
                    redInvoiceDetailRepository.deleteById(idRed.longValue());
                }
            }
        }
        return ResponseMessage.SUCCESSFUL;
    }

    private List<HDDTExcelDTO> getDataHddtExcel(String ids) {
        List<HddtExcel> hddtExcels = hddtExcelRepository.getDataHddtExcel(ids);
        List<HDDTExcelDTO> HDDTExcelDTOS = null;
        HDDTExcelDTOS = hddtExcels.stream().map(data -> {
            HDDTExcelDTO hddtExcelDTO = modelMapper.map(data, HDDTExcelDTO.class);
            //shop
            if (data.getShopId() != null) {
                ShopDTO shopDTO = shopClient.getByIdV1(data.getShopId()).getData();
                if (shopDTO != null)
                    hddtExcelDTO.setShopCode(shopDTO.getShopCode());
            }
            //customer
            if (data.getCustomerId() != null) {
                CustomerDTO customerDTO = customerClient.getCustomerByIdV1(data.getCustomerId()).getData();
                if (customerDTO != null) {
                    hddtExcelDTO.setCustomerCode(customerDTO.getCustomerCode());
                    hddtExcelDTO.setMobiPhone(customerDTO.getMobiPhone());
                }

            }
            hddtExcelDTO.setTotalAmount(data.getQuantity() * data.getPriceNotVat());
            Float gtgt = (data.getPrice() - data.getPriceNotVat()) / data.getPriceNotVat() * 100;
            gtgt = (float) Math.ceil((gtgt * 1000) / 1000);
            hddtExcelDTO.setGTGT(gtgt);
            return hddtExcelDTO;
        }).collect(Collectors.toList());
        return HDDTExcelDTOS;
    }

    private List<HDDTO> getDataHdDvkh(String ids) {
        List<RedInvoice> redInvoices = redInvoiceRepository.getRedInvoiceByIds(ids);
        List<HDDTO> hddtos = null;
        hddtos = redInvoices.stream().map(data -> {
            HDDTO hddto = modelMapper.map(data, HDDTO.class);
            String fullname = "";
            if (data.getCustomerId() != null) {
                CustomerDTO customerDTO = customerClient.getCustomerByIdV1(data.getCustomerId()).getData();
                if (customerDTO != null) {
                    fullname += customerDTO.getLastName() + " " + customerDTO.getFirstName();
                    hddto.setFullName(fullname);
                }
            }
            if (data.getShopId() != null) {
                ShopDTO shopDTO = shopClient.getByIdV1(data.getShopId()).getData();
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
        ctdtos = ctdvkhs.stream().map(data -> {
            CTDTO ctdto = modelMapper.map(data, CTDTO.class);
            if (data.getShopId() != null) {
                ShopDTO shopDTO = shopClient.getByIdV1(data.getShopId()).getData();
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

    public String createRedInvoiceCode() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
