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
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.RedInvoicePrint;
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
        return new Response<CoverResponse<Page<RedInvoiceDTO>, TotalRedInvoice>>().withData(coverResponse);
    }

    @Override
    public CoverResponse<List<RedInvoiceDataDTO>, TotalRedInvoiceResponse> getDataInBillOfSale(List<String> orderCodeList, Long shopId) {
        String customerName, customerCodes, officeWorking, officeAddress, taxCode;
        Long customerIds;
        if (orderCodeList.isEmpty()){
            throw new ValidateException(ResponseMessage.EMPTY_LIST);
        }else {

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
                    SaleOrder saleOrder = saleOrderRepository.findSaleOrderByCustomerIdAndOrderNumber(idCus, saleOrderCode);
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
//                        dataDTO.setShopId(shopId);
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
                    totalQuantity, totalAmount, totalValueAddedTax, shopId , customerIds,customerCodes,customerName,null,null,officeWorking,officeAddress
                    ,taxCode,null,null);
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
            throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
        }
        List<ProductDetailDTO> productDetailDTOS = new ArrayList<>();
        for (BigDecimal ids : productIdtList) {
            Product product = productRepository.findByIdAndStatus(ids.longValue(), 1);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            ProductDetailDTO dto = modelMapper.map(product, ProductDetailDTO.class);
            dto.setOrderNumber(orderCode);
            SaleOrderDetail saleOrderDetail = saleOrderDetailRepository.findSaleOrderDetailBySaleOrderIdAndProductId(saleOrderId, ids.longValue());
            dto.setQuantity(saleOrderDetail.getQuantity());
            dto.setUnitPrice(saleOrderDetail.getPrice());
            dto.setIntoMoney(saleOrderDetail.getQuantity().floatValue() * saleOrderDetail.getPrice());
            productDetailDTOS.add(dto);
        }
        return productDetailDTOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(RedInvoiceNewDataDTO redInvoiceNewDataDTO, Long userId, Long shopId) {
        boolean check = false;
        for (int i = 0; i < redInvoiceNewDataDTO.getProductDataDTOS().size(); i++) {
            for (int j = 1; j < redInvoiceNewDataDTO.getProductDataDTOS().size(); j++) {
                if (redInvoiceNewDataDTO.getProductDataDTOS().get(i).getGroupVat().equals(redInvoiceNewDataDTO.getProductDataDTOS().get(j).getGroupVat())) {
                    check = true;
                } else {
                    throw new ValidateException(ResponseMessage.INDUSTRY_ARE_NOT_DIFFERENT);
                }
            }
        }
        String redInvoiceCode = this.createRedInvoiceCode();
        String checkRedInvoice = redInvoiceRepository.checkRedInvoice(redInvoiceCode);
        if (!(checkRedInvoice == null)) {
            throw new ValidateException(ResponseMessage.RED_INVOICE_CODE_HAVE_EXISTED);
        } else {
            if (check) {
                UserDTO userDTO = userClient.getUserByIdV1(userId);
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                RedInvoice redInvoiceRecord = modelMapper.map(redInvoiceNewDataDTO , RedInvoice.class);
                redInvoiceRecord.setInvoiceNumber(redInvoiceCode);
                redInvoiceRecord.setShopId(shopId);
                String orderNumber = saleOrderRepository.findByIdSale(redInvoiceNewDataDTO.getSaleOrderId().get(0));
                for (int i = 1; i < redInvoiceNewDataDTO.getSaleOrderId().size(); i++) {
                    orderNumber = orderNumber + "," + saleOrderRepository.findByIdSale(redInvoiceNewDataDTO.getSaleOrderId().get(i));
                }
                redInvoiceRecord.setOrderNumbers(orderNumber);
                redInvoiceRecord.setCreatedBy(userDTO.getLastName() + " " + userDTO.getFirstName());
                redInvoiceRecord.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                redInvoiceRepository.save(redInvoiceRecord);

                for (ProductDataDTO productDataDTO : redInvoiceNewDataDTO.getProductDataDTOS()) {
                    RedInvoiceDetail redInvoiceDetailRecord = modelMapper.map(redInvoiceNewDataDTO , RedInvoiceDetail.class);
                    redInvoiceDetailRecord.setRedInvoiceId(redInvoiceRecord.getId());
                    redInvoiceDetailRecord.setShopId(shopId);
                    redInvoiceDetailRecord.setProductId(productDataDTO.getProductId());
                    redInvoiceDetailRecord.setQuantity(productDataDTO.getQuantity().intValue());
                    redInvoiceDetailRecord.setPrice(((productDataDTO.getPriceNotVat() * productDataDTO.getVat() )/100) + productDataDTO.getPriceNotVat() );
                    redInvoiceDetailRecord.setPriceNotVat(productDataDTO.getPriceNotVat());
                    redInvoiceDetailRecord.setAmount((((productDataDTO.getPriceNotVat() * productDataDTO.getQuantity()) * productDataDTO.getVat()) / 100)+ (productDataDTO.getPriceNotVat() * productDataDTO.getQuantity()));
                    redInvoiceDetailRecord.setAmountNotVat(productDataDTO.getPriceNotVat() * productDataDTO.getQuantity());
                    redInvoiceDetailRecord.setCreatedBy(userDTO.getLastName() + " " + userDTO.getFirstName());
                    redInvoiceDetailRecord.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    redInvoiceDetailRepository.save(redInvoiceDetailRecord);
                }
            }
        }
        String message = "Thêm thành công";
        return message;
    }

    @Override
    public Response<List<RedInvoicePrint>> lstRedInvoicePrint(List<Long> ids) {
        List<RedInvoicePrint> redInvoicePrints = new ArrayList<>();
        if (ids.size() > 0) {
            ids.forEach(id -> {
                RedInvoice redInvoice = repository.findById(id).orElse(null);
                if (redInvoice != null) {
                    RedInvoicePrint redInvoicePrint = modelMapper.map(redInvoice, RedInvoicePrint.class);

                    //shop
                    ShopDTO shopDTO = shopClient.getByIdV1(redInvoice.getShopId()).getData();
                    if (shopDTO != null)
                        redInvoicePrint.setShopCode(shopDTO.getShopCode());

                    //customer
                    CustomerDTO customerDTO = customerClient.getCustomerByIdV1(redInvoice.getCustomerId()).getData();
                    if (customerDTO != null) {
                        redInvoicePrint.setCustomerCode(customerDTO.getCustomerCode());
                        redInvoicePrint.setLastName(customerDTO.getLastName());
                        redInvoicePrint.setFirstName(customerDTO.getFirstName());
                        redInvoicePrint.setMobiPhone(customerDTO.getMobiPhone());
                    }

                    //red invoice detail
                    RedInvoiceDetail redInvoiceDetail = redInvoiceDetailRepository.findById(redInvoice.getId()).orElse(null);
                    if (redInvoiceDetail != null) {
                        redInvoicePrint.setQuantity(redInvoiceDetail.getQuantity());
                        redInvoicePrint.setPriceNotVat(redInvoiceDetail.getPriceNotVat());
                        redInvoicePrint.setTotalAmount(redInvoiceDetail.getQuantity() * redInvoiceDetail.getPriceNotVat());
                        Float gtgt = (redInvoiceDetail.getPrice() - redInvoiceDetail.getPriceNotVat()) / redInvoiceDetail.getPriceNotVat() * 100;
                        gtgt = (float) Math.ceil((gtgt * 1000) / 1000);
                        redInvoicePrint.setGTGT(gtgt);

                        //product
                        Product product = productRepository.findById(redInvoiceDetail.getProductId()).orElse(null);
                        if (product != null) {
                            redInvoicePrint.setProductCode(product.getProductCode());
                            redInvoicePrint.setProductName(product.getProductName());
                            redInvoicePrint.setUom1(product.getUom1());
                        }
                    }
                    redInvoicePrints.add(redInvoicePrint);
                }
            });
        }
        return new Response<List<RedInvoicePrint>>().withData(redInvoicePrints);
    }

    @Override
    public String deleteByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            throw new ValidateException(ResponseMessage.RED_INVOICE_ID_IS_NULL);
        } else {
            for (Long id : ids) {
                redInvoiceRepository.deleteById(id);
              List<BigDecimal> idRedList = redInvoiceDetailRepository.getAllRedInvoiceIds(id);
              for (BigDecimal idRed : idRedList){
                  redInvoiceDetailRepository.deleteById(idRed.longValue());
              }
            }
        }
        String message = "Xóa thành công";
        return message;
    }

    public String createRedInvoiceCode() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
