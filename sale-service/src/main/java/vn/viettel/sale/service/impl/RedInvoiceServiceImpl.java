package vn.viettel.sale.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


    @Override
    public Response<CoverResponse<Page<RedInvoiceDTO>, TotalRedInvoice>> getAll(Long shopId, String searchKeywords, Date fromDate, Date toDate, String invoiceNumber, Pageable pageable) {

        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);

        if (fromDate == null || toDate == null) {
            LocalDate initial = LocalDate.now();
            fromDate = Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            toDate = Date.from(initial.withDayOfMonth(initial.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

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
            List<RedInvoiceDetail> redInvoiceDetails = redInvoiceDetailService.getRedInvoiceDetailByRedInvoiceId(redInvoiceDTO.getId()).getData();
            Float amount = 0F;
            Float amountNotVat = 0F;
            for(RedInvoiceDetail detail : redInvoiceDetails)
            {
                amount +=detail.getAmount();
                amountNotVat+=detail.getAmountNotVat();
            }

            redInvoiceDTO.setAmountNotVat(amountNotVat);
            redInvoiceDTO.setAmountGTGT(amount - amountNotVat);
            totalRedInvoice.addAmountNotVat(redInvoiceDTO.getAmountNotVat())
                    .addAmountGTGT(redInvoiceDTO.getAmountGTGT())
                    .addTotalQuantity(redInvoiceDTO.getTotalQuantity())
                    .addTotalMoney(redInvoiceDTO.getTotalMoney());
        });

        CoverResponse coverResponse = new CoverResponse(redInvoiceDTOS,totalRedInvoice);
        return new Response<CoverResponse<Page<RedInvoiceDTO>, TotalRedInvoice>>().withData(coverResponse);
    }

    @Override
    public Response<CoverResponse<List<RedInvoiceDataDTO> , TotalRedInvoiceResponse>> getDataInBillOfSale(List<String> orderCodeList, Long shopId) {
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
                        throw new ValidateException(ResponseMessage.CUSTOMERS_ARE_NOT_DIFFERENT);
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
                    Price price = productPriceRepository.findByFromDate(fromDate, product.getId());
                    customerDTO = customerClient.getCustomerByIdV1(idCus).getData();
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
                    dataDTO.setQuantity(detail.getQuantity().floatValue());
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
        for (RedInvoiceDataDTO dataDTO : dtos){
            totalQuantity += dataDTO.getQuantity();
            totalAmount += dataDTO.getAmount();
            totalValueAddedTax += dataDTO.getValueAddedTax();
        }
        TotalRedInvoiceResponse totalRedInvoiceResponse = new TotalRedInvoiceResponse(totalQuantity,totalAmount,totalValueAddedTax);
        List<RedInvoiceDataDTO> redInvoiceDataDTOS = new ArrayList<>(dtos);
        CoverResponse<List<RedInvoiceDataDTO>,TotalRedInvoiceResponse> response = new CoverResponse(redInvoiceDataDTOS,totalRedInvoiceResponse);
        return new Response<CoverResponse<List<RedInvoiceDataDTO>, TotalRedInvoiceResponse>>().
                withData(response);
    }

    @Override
    public Response<List<ProductDetailDTO>> getAllProductByOrderNumber(String orderCode) {

        Long saleOrderId = saleOrderRepository.findSaleOrderIdByOrderCode(orderCode);
        List<BigDecimal> productIdtList = saleOrderDetailRepository.findAllBySaleOrderCode(saleOrderId);
        if (productIdtList.isEmpty()) {
            throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
        }
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
    public Response<Object> create(RedInvoiceNewDataDTO redInvoiceNewDataDTO, Long userId, Long shopId) {

        UserDTO userDTO = userClient.getUserByIdV1(userId);
        RedInvoice redInvoiceRecord = new RedInvoice();
        redInvoiceRecord.setInvoiceNumber(this.createRedInvoiceCode());
        redInvoiceRecord.setShopId(shopId);
        redInvoiceRecord.setOfficeWorking(redInvoiceNewDataDTO.getOfficeWorking());
        redInvoiceRecord.setOfficeAddress(redInvoiceNewDataDTO.getOfficeAddress());
        redInvoiceRecord.setTaxCode(redInvoiceNewDataDTO.getTaxCode());
        redInvoiceRecord.setTotalQuantity(redInvoiceNewDataDTO.getTotalQuantity());
        redInvoiceRecord.setTotalMoney(redInvoiceNewDataDTO.getAmountTotal());
        redInvoiceRecord.setPrintDate(redInvoiceNewDataDTO.getPrintDate());
        redInvoiceRecord.setNote(redInvoiceNewDataDTO.getNote());
        redInvoiceRecord.setCustomerId(redInvoiceNewDataDTO.getCustomerId());
        redInvoiceRecord.setPaymentType(redInvoiceNewDataDTO.getPaymentType());
        String orderNumber = saleOrderRepository.findByIdSale(redInvoiceNewDataDTO.getSaleOrderId().get(0));
        for (int i = 1; i < redInvoiceNewDataDTO.getSaleOrderId().size(); i++) {
            orderNumber = orderNumber + "," + saleOrderRepository.findByIdSale(redInvoiceNewDataDTO.getSaleOrderId().get(i));
        }
        redInvoiceRecord.setOrderNumbers(orderNumber);
        redInvoiceRecord.setCreateUser(userDTO.getLastName() + " " + userDTO.getFirstName());
        redInvoiceRecord.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        redInvoiceRecord.setBuyerName(redInvoiceNewDataDTO.getBuyerName());
        redInvoiceRepository.save(redInvoiceRecord);

        for (ProductDataDTO productDataDTO : redInvoiceNewDataDTO.getProductDataDTOS()) {
            RedInvoiceDetail redInvoiceDetailRecord = new RedInvoiceDetail();
            redInvoiceDetailRecord.setRedInvoiceId(redInvoiceRecord.getId());
            redInvoiceDetailRecord.setShopId(shopId);
            redInvoiceDetailRecord.setPrintDate(redInvoiceNewDataDTO.getPrintDate());
            redInvoiceDetailRecord.setProductId(productDataDTO.getProductId());
            redInvoiceDetailRecord.setQuantity(productDataDTO.getQuantity().intValue());
            redInvoiceDetailRecord.setPrice(productDataDTO.getPrice());
            redInvoiceDetailRecord.setPriceNotVat(productDataDTO.getPriceNotVat());
            redInvoiceDetailRecord.setAmount(productDataDTO.getAmount());
            redInvoiceDetailRecord.setAmountNotVat(productDataDTO.getAmountNotVat());
            redInvoiceDetailRecord.setNote(redInvoiceNewDataDTO.getNote());
            redInvoiceDetailRecord.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            redInvoiceDetailRepository.save(redInvoiceDetailRecord);
        }
        return null;
    }

    @Override
    public Response<List<RedInvoiceDTO>> lstRedInvocePrint(List<Long> ids) {
        List<RedInvoiceDTO> redInvoiceDTOS = null;
        if(ids.size()>0)
        {
            ids.forEach(id->{
                RedInvoice redInvoice = repository.findById(id).orElse(null);
                if(redInvoice!=null)
                    redInvoiceDTOS.add(modelMapper.map(redInvoice,RedInvoiceDTO.class));
            });
        }
        return new Response<List<RedInvoiceDTO>>().withData(redInvoiceDTOS);
    }

    public String createRedInvoiceCode() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
