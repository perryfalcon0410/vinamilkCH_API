package vn.viettel.sale.service.impl;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.common.Price;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.db.entity.sale.RedInvoice;
import vn.viettel.core.db.entity.sale.RedInvoiceDetail;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.db.entity.sale.SaleOrderDetail;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.RedInvoiceDetailService;
import vn.viettel.sale.service.RedInvoiceService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.specification.RedInvoiceSpefication;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class RedInvoiceServiceImpl extends BaseServiceImpl<RedInvoice, RedInvoiceRepository> implements RedInvoiceService {
    @Autowired
    CustomerClient customerClient;

    @Autowired
    ShopClient shopClient;

    @Autowired
    RedInvoiceDetailService redInvoiceDetailService;

    @Autowired
    RedInvoiceRepository redInvoiceRepository;

    @Autowired
    SaleOrderRepository saleOrderRepository;

    @Autowired
    SaleOrderDetailRepository saleOrderDetailRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductPriceRepository productPriceRepository;

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
            redInvoices = repository.findAll(Specification.where(RedInvoiceSpefication.hasFromDateToDate(fromDate, toDate))
                    .and(RedInvoiceSpefication.hasInvoiceNumber(invoiceNumber)), pageable);
        } else {
            if (ids.size() == 0)
                redInvoices = repository.findAll(Specification.where(RedInvoiceSpefication.hasCustomerId(-1L)), pageable);
            else {
                for (Long id : ids) {
                    redInvoices = repository.findAll(Specification.where(RedInvoiceSpefication.hasCustomerId(id))
                            .and(RedInvoiceSpefication.hasFromDateToDate(fromDate, toDate))
                            .and(RedInvoiceSpefication.hasInvoiceNumber(invoiceNumber)), pageable);
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

                    Long idassas = idCus;
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
                    dataDTO.setVat(price.getVat());
                    dataDTO.setTotalMoney(price.getPriceNotVat() * detail.getQuantity());
                    dataDTO.setNote(order.getNote());
                    dataDTO.setNote(order.getNote());
                    dataDTO.setValueAddedTax(((price.getPriceNotVat() * detail.getQuantity()) * price.getVat()) / 100);
                    dtos.add(dataDTO);
                }
            }
            for (int i = 0 ; i < dtos.size() ; i++){
                for (int j = i + 1 ; j < dtos.size(); j++){
                    if (dtos.get(i).equals(dtos.get(j))){
                        dtos.remove(j);
                        j--;
                    }
                }
            }
        }
        return new Response<List<RedInvoiceDataDTO>>().
                withData(dtos);
    }

    private RedInvoiceDataDTO mapRedInvoiceToRedInvoiceResponse(RedInvoice redInvoice) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        RedInvoiceDataDTO dto = modelMapper.map(redInvoice, RedInvoiceDataDTO.class);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<RedInvoiceDataDTO> create(RedInvoiceDataDTO redInvoiceDataDTO, Long userId, Long shopId) {
        Shop shop = shopClient.getById(shopId).getData();

        if (shop == null) {
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        RedInvoice redInvoiceRecord = modelMapper.map(redInvoiceDataDTO, RedInvoice.class);

//        redInvoiceRecord.setInvoiceNumber(this.createRedInvoiceCode(shopId, shop.getShopCode()));
        redInvoiceRecord.setShopId(shopId);
        redInvoiceRecord.setOfficeWorking(redInvoiceDataDTO.getOfficeWorking());
        redInvoiceRecord.setOfficeAddress(redInvoiceDataDTO.getOfficeAddress());
        redInvoiceRecord.setTaxCode(redInvoiceDataDTO.getTaxCode());
        redInvoiceRecord.setTotalQuantity(redInvoiceDataDTO.getTotalQuantity());
        redInvoiceRecord.setTotalMoney(redInvoiceDataDTO.getTotalMoney());
        redInvoiceRecord.setPrintDate(redInvoiceDataDTO.getPrintDate());
        redInvoiceRecord.setNote(redInvoiceDataDTO.getNote());
        redInvoiceRecord.setCustomerId(redInvoiceDataDTO.getCustomerId());
        redInvoiceRecord.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
//        List<String> saleOrderList = saleOrderRepository.findByIdSale(redInvoiceDataDTO.getSaleOrderId());
//        redInvoiceRecord.setOrderNumbers();
        redInvoiceRecord.setCreateUser(redInvoiceDataDTO.getCreateUser());


        RedInvoice redInvoiceResult = redInvoiceRepository.save(redInvoiceRecord);
        RedInvoiceDataDTO invoiceDataDTO = this.mapRedInvoiceToRedInvoiceResponse(redInvoiceResult);
        return new Response<RedInvoiceDataDTO>().withData(invoiceDataDTO);
    }

    @Override
    public Response<List<ProductDetailDTO>> getAllProductByOrderNumber(String orderCode) {

        Long saleOrderId = saleOrderRepository.findSaleOrderIdByOrderCode(orderCode);
        List<Long> productIdtList = saleOrderDetailRepository.findAllBySaleOrderCode(saleOrderId);
        List<ProductDetailDTO> productDetailDTOS = new ArrayList<>();
        ProductDetailDTO dto = new ProductDetailDTO();
        for (Long ids : productIdtList) {
            Product product = productRepository.findProductById(ids);
            dto.setProductCode(product.getProductCode());
            dto.setProductName(product.getProductName());

            SaleOrderDetail saleOrderDetail = saleOrderDetailRepository.findSaleOrderDetailBySaleOrderIdAndProductId(saleOrderId, ids);
            dto.setQuantity(saleOrderDetail.getQuantity());
            dto.setUnitPrice(saleOrderDetail.getPrice());
            dto.setIntoMoney(saleOrderDetail.getQuantity().floatValue() * saleOrderDetail.getPrice());
            productDetailDTOS.add(dto);
        }
        return new Response<List<ProductDetailDTO>>().withData(productDetailDTOS);
    }

//    public String createRedInvoiceCode(Long shopId, String shopCode) {
//        int redInvoiceNumber = redInvoiceRepository.getCustomerNumber(shopId);
//        return "RED." + shopCode + "." + Integer.toString(redInvoiceNumber + 1 + 100000).substring(1);
//    }
}
