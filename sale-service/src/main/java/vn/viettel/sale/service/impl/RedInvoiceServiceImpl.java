package vn.viettel.sale.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.sale.entities.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.RedInvoiceDetailService;
import vn.viettel.sale.service.RedInvoiceService;
import vn.viettel.sale.service.dto.RedInvoiceDTO;
import vn.viettel.sale.service.dto.RedInvoiceDataDTO;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.specification.RedInvoiceSpecification;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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

                    Float totalQuantity = saleOrderDetailRepository.getCountQuantity(detail.getSaleOrderId(), detail.getProductId());

                    dataDTO.setTotalQuantity(totalQuantity);
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
            for (int i = 0; i < dtos.size(); i++) {
                for (int j = i + 1; j < dtos.size(); j++) {
                    Long a = dtos.get(i).getProductId();
                    Long b = dtos.get(j).getProductId();
                    if (dtos.get(i).getSaleOrderId().equals(dtos.get(j).getSaleOrderId())) {
                        if (dtos.get(i).getProductId().equals(dtos.get(j).getProductId())) {
                            dtos.remove(j);
                            j--;
                        }
                    }
                }
            }
        }
        return new Response<List<RedInvoiceDataDTO>>().
                withData(dtos);
    }
}
