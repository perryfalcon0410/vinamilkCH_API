package vn.viettel.sale.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.sale.entities.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.RedInvoiceDetailService;
import vn.viettel.sale.service.RedInvoiceService;
import vn.viettel.sale.service.dto.ProductDetailDTO;
import vn.viettel.sale.service.dto.RedInvoiceDTO;
import vn.viettel.sale.service.dto.RedInvoiceDataDTO;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.specification.RedInvoiceSpecification;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class RedInvoiceServiceImpl extends BaseServiceImpl<RedInvoice, RedInvoiceRepository> implements RedInvoiceService {
    @Autowired
    CustomerClient customerClient;

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


    @Override
    public Response<Page<RedInvoiceDTO>> getAll(String searchKeywords, Date fromDate, Date toDate, String invoiceNumber, Pageable pageable) {

        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);

        if (fromDate == null || toDate == null) {
            LocalDate initial = LocalDate.now();
            fromDate = Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            toDate = Date.from(initial.withDayOfMonth(initial.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        List<Long> ids = customerClient.getIdCustomerBySearchKeyWords(searchKeywords).getData();
        Page<RedInvoice> redInvoices = null;

        if (searchKeywords.equals("")) {
            redInvoices = repository.findAll(Specification.where(RedInvoiceSpecification.hasFromDateToDate(fromDate, toDate))
                    .and(RedInvoiceSpecification.hasInvoiceNumber(invoiceNumber)), pageable);
        } else {
            if (ids.size() == 0)
                redInvoices = repository.findAll(Specification.where(RedInvoiceSpecification.hasCustomerId(-1L)), pageable);
            else {
                for (Long id : ids) {
                    redInvoices = repository.findAll(Specification.where(RedInvoiceSpecification.hasCustomerId(id))
                            .and(RedInvoiceSpecification.hasFromDateToDate(fromDate, toDate))
                            .and(RedInvoiceSpecification.hasInvoiceNumber(invoiceNumber)), pageable);
                }
            }
        }

        Page<RedInvoiceDTO> redInvoiceDTOS = redInvoices.map(red -> modelMapper.map(red, RedInvoiceDTO.class));

        redInvoiceDTOS.forEach(redInvoiceDTO -> {
            RedInvoiceDetail redInvoiceDetail = redInvoiceDetailService.getRedInvoiceDetailByRedInvoiceId(redInvoiceDTO.getId()).getData();
            redInvoiceDTO.setAmountNotVat(redInvoiceDetail.getAmountNotVat());
            redInvoiceDTO.setAmountGTGT(redInvoiceDetail.getAmount() - redInvoiceDetail.getAmountNotVat());
        });

        return new Response<Page<RedInvoiceDTO>>().withData(redInvoiceDTOS);
    }

    @Override
    public Response<List<RedInvoiceDataDTO>> getDataInBillOfSale(List<String> orderCodeList, Long shopId) {
        String customerName, customerCodes, officeWorking, officeAddress, taxCode;
        List<Long> idCustomerList = new ArrayList<>();
        Long customerId;
        CustomerDTO customerDTO;
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
                        break;
                    }
                }
            }
        }
        if (check) {
            for (String saleOrderCode : orderCodeList) {
                SaleOrder saleOrder = saleOrderRepository.findSaleOrderByCustomerIdAndOrderNumber(idCus, saleOrderCode);
                saleOrdersList.add(saleOrder);
            }

            for (SaleOrder saleOrders : saleOrdersList) {
                List<SaleOrderDetail> saleOrderDetailsList = saleOrderDetailRepository.findAllBySaleOrderId(saleOrders.getId());
                for (SaleOrderDetail detail : saleOrderDetailsList) {

                    String saleOrderCode = saleOrderRepository.getSaleOrderCode(detail.getSaleOrderId());
                    Product product = productRepository.findProductById(detail.getProductId());
                    SaleOrder order = saleOrderRepository.findSaleOrderByOrderNumber(saleOrderCode);
                    Date date = productPriceRepository.findByProductIdAndOrderDate(detail.getProductId(), order.getOrderDate());
                    String fromDate = new SimpleDateFormat("yy-MM-dd").format(date);
                    Price price = productPriceRepository.findByFromDate(fromDate);
                    customerDTO = customerClient.getCustomerById(idCus).getData();
                    customerName = customerDTO.getLastName() + " " + customerDTO.getFirstName();
                    customerCodes = customerDTO.getCustomerCode();
                    officeWorking = customerDTO.getWorkingOffice();
                    officeAddress = customerDTO.getOfficeAddress();
                    taxCode = customerDTO.getTaxCode();

                    RedInvoiceDataDTO dataDTO = new RedInvoiceDataDTO();
                    dataDTO.setShopId(shopId);
                    dataDTO.setSaleOrderId(saleOrders.getId());
                    dataDTO.setCustomerId(customerDTO.getId());
                    dataDTO.setCustomerName(customerName);
                    dataDTO.setCustomerCode(customerCodes);
                    dataDTO.setOfficeWorking(officeWorking);
                    dataDTO.setOfficeAddress(officeAddress);
                    dataDTO.setTaxCode(taxCode);
                    dataDTO.setProductId(product.getId());
                    dataDTO.setProductCode(product.getProductCode());
                    dataDTO.setProductName(product.getProductName());
                    dataDTO.setTotalQuantity(detail.getQuantity().floatValue());
                    dataDTO.setUom1(product.getUom1());
                    dataDTO.setUom2(product.getUom2());
                    dataDTO.setPriceNotVat(price.getPriceNotVat());
                    dataDTO.setPrice(price.getPrice());
                    dataDTO.setVat(price.getVat());
                    dataDTO.setAmountNotVat(price.getPriceNotVat() * detail.getQuantity());
                    dataDTO.setAmount(price.getPrice() * detail.getQuantity());
                    dataDTO.setNote(order.getNote());
                    dataDTO.setValueAddedTax(((price.getPriceNotVat() * detail.getQuantity()) * price.getVat()) / 100);
                    dtos.add(dataDTO);
                }
            }
            for (int i = 0; i < dtos.size(); i++) {
                for (int j = i + 1; j < dtos.size(); j++) {
                    if (dtos.get(i).getProductId().equals(dtos.get(j).getProductId())) {
                        Float count = dtos.get(i).getTotalQuantity() + dtos.get(j).getTotalQuantity();
                        dtos.get(i).setTotalQuantity(count);
                        dtos.remove(j);
                        j--;

                    }
                }
            }
        }
        return new Response<List<RedInvoiceDataDTO>>().
                withData(dtos);
    }

    @Override
    public Response<List<ProductDetailDTO>> getAllProductByOrderNumber(String orderCode) {

        Long saleOrderId = saleOrderRepository.findSaleOrderIdByOrderCode(orderCode);
        List<BigDecimal> productIdtList = saleOrderDetailRepository.findAllBySaleOrderCode(saleOrderId);
        List<ProductDetailDTO> productDetailDTOS = new ArrayList<>();
        for (BigDecimal ids : productIdtList) {
            ProductDetailDTO dto = new ProductDetailDTO();
            Product product = productRepository.findProductById(ids.longValue());
            dto.setProductCode(product.getProductCode());
            dto.setProductName(product.getProductName());
            SaleOrderDetail saleOrderDetail = saleOrderDetailRepository.findSaleOrderDetailBySaleOrderIdAndProductId(saleOrderId, ids.longValue());
            dto.setQuantity(saleOrderDetail.getQuantity());
            dto.setUnitPrice(saleOrderDetail.getPrice());
            dto.setIntoMoney(saleOrderDetail.getQuantity().floatValue() * saleOrderDetail.getPrice());
            productDetailDTOS.add(dto);
        }
        return new Response<List<ProductDetailDTO>>().withData(productDetailDTOS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Object> create(RedInvoiceDataDTO redInvoiceDataDTO, Long userId, Long shopId) {
//        ShopDTO shop = shopClient.getById(shopId).getData();
//        if (shop == null) {
//            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
//        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        RedInvoice redInvoiceRecord = modelMapper.map(redInvoiceDataDTO, RedInvoice.class);
        RedInvoiceDetail redInvoiceDetailRecord = modelMapper.map(redInvoiceDataDTO, RedInvoiceDetail.class);

        redInvoiceRecord.setInvoiceNumber(this.createRedInvoiceCode());
        redInvoiceRecord.setShopId(shopId);
        redInvoiceRecord.setOfficeWorking(redInvoiceDataDTO.getOfficeWorking());
        redInvoiceRecord.setOfficeAddress(redInvoiceDataDTO.getOfficeAddress());
        redInvoiceRecord.setTaxCode(redInvoiceDataDTO.getTaxCode());
        redInvoiceRecord.setTotalQuantity(redInvoiceDataDTO.getTotalQuantity());
        redInvoiceRecord.setTotalMoney(redInvoiceDataDTO.getAmount());
        redInvoiceRecord.setPrintDate(redInvoiceDataDTO.getPrintDate());
        redInvoiceRecord.setNote(redInvoiceDataDTO.getNote());
        redInvoiceRecord.setCustomerId(redInvoiceDataDTO.getCustomerId());
        redInvoiceRecord.setPaymentType(redInvoiceDataDTO.getPaymentType());
        String orderNumber = saleOrderRepository.findByIdSale(redInvoiceDataDTO.getSaleOrderId());
        redInvoiceRecord.setOrderNumbers(orderNumber);
        redInvoiceRecord.setCreateUser(redInvoiceDataDTO.getCreateUser());
        redInvoiceRecord.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        redInvoiceRepository.save(redInvoiceRecord);

//        RedInvoiceDetail redInvoiceDetail = new RedInvoiceDetail();
        redInvoiceDetailRecord.setRedInvoiceId(redInvoiceRecord.getId());
        redInvoiceDetailRecord.setShopId(shopId);
        redInvoiceDetailRecord.setPrintDate(redInvoiceDataDTO.getPrintDate());
        redInvoiceDetailRecord.setProductId(redInvoiceDataDTO.getProductId());
        redInvoiceDetailRecord.setQuantity(redInvoiceDataDTO.getTotalQuantity().intValue());
        redInvoiceDetailRecord.setPrice(redInvoiceDataDTO.getPrice());
        redInvoiceDetailRecord.setPriceNotVat(redInvoiceDataDTO.getPriceNotVat());
        redInvoiceDetailRecord.setAmount(redInvoiceDataDTO.getAmount());
        redInvoiceDetailRecord.setAmountNotVat(redInvoiceDataDTO.getAmountNotVat());
        redInvoiceDetailRecord.setNote(redInvoiceDataDTO.getNote());
        redInvoiceDetailRecord.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        redInvoiceDetailRepository.save(redInvoiceDetailRecord);
        return null;
    }

    public String createRedInvoiceCode() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
