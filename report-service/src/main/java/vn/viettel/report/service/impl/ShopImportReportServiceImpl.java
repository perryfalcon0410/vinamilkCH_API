package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.report.messaging.ShopImportFilter;
import vn.viettel.report.service.ShopImportReportService;
import vn.viettel.report.service.dto.*;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShopImportReportServiceImpl implements ShopImportReportService {
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    ShopClient shopClient;
    @Override
    public CoverResponse<Page<ShopImportDTO>, ShopImportTotalDTO> find(ShopImportFilter filter, Pageable pageable) {
        if(filter.getImportType() !=null){
            if(filter.getImportType().equals("0")) filter.setImportType("0,3");
        }
        List<ShopImportDTO> shopImports =  this.callProcedure(filter).getData();
        ShopImportTotalDTO totalDTO = new ShopImportTotalDTO();
        List<ShopImportDTO> subList = new ArrayList<>();
        if(!shopImports.isEmpty()) {
            ShopImportDTO total = shopImports.get(shopImports.size() -1);
            totalDTO.setTotalQuantity(total.getQuantity());
            totalDTO.setTotalWholeSale(total.getWholesale());
            totalDTO.setTotalRetail(total.getRetail());
            totalDTO.setTotalAmount(total.getAmount());
            totalDTO.setTotal(total.getTotal());
            this.removeDataList(shopImports);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), shopImports.size());
            subList = shopImports.subList(start, end);
        }
        Page<ShopImportDTO> page = new PageImpl<>( subList, pageable, shopImports.size());
        CoverResponse response = new CoverResponse(page, totalDTO);
        return response;
    }
    @Override
    public  Response<List<ShopImportDTO>> callProcedure(ShopImportFilter filter) {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_SHOP_IMPORT", ShopImportDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(5, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(7, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(8, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(9, Long.class, ParameterMode.IN);
        ///////////////////////////////////////////////////////////////////////////////////////////
        storedProcedure.setParameter(2, filter.getFromDate());
        storedProcedure.setParameter(3, filter.getToDate());
        storedProcedure.setParameter(4, filter.getProductCodes());
        storedProcedure.setParameter(5, filter.getImportType());
        storedProcedure.setParameter(6, filter.getInternalNumber());
        storedProcedure.setParameter(7, filter.getFromOrderDate());
        storedProcedure.setParameter(8, filter.getToOrderDate());
        storedProcedure.setParameter(9, filter.getShopId());
        storedProcedure.execute();
        List<ShopImportDTO> response = storedProcedure.getResultList();
        entityManager.close();
        return new Response<List<ShopImportDTO>>().withData(response);
    }

    @Override
    public  Response<CoverResponse<List<ShopImportDTO>, ShopImportTotalDTO>> dataExcel(ShopImportFilter filter) {
        List<ShopImportDTO> shopImports =  this.callProcedure(filter).getData();
        ShopImportTotalDTO totalDTO = new ShopImportTotalDTO();
        if(!shopImports.isEmpty()) {
            ShopImportDTO total = shopImports.get(shopImports.size() -1);
            totalDTO.setTotalQuantity(total.getQuantity());
            totalDTO.setTotalWholeSale(total.getWholesale());
            totalDTO.setTotalRetail(total.getRetail());
            totalDTO.setTotalAmount(total.getAmount());
            totalDTO.setTotal(total.getTotal());
            this.removeDataList(shopImports);
        }
        CoverResponse response = new CoverResponse(shopImports, totalDTO);
        return new Response<CoverResponse<List<ShopImportDTO>, ShopImportTotalDTO>>().withData(response);
    }


    @Override
    public PrintShopImportDTO print(ShopImportFilter filter, Long shopId) {
        ShopDTO shop = shopClient.getShopByIdV1(shopId).getData();
        if(shop == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        List<ShopImportDTO> shopImports =  this.callProcedure(filter).getData();
        this.removeDataList(shopImports);

        PrintShopImportDTO response = new PrintShopImportDTO(shop.getShopName(), shop.getAddress(), shop.getPhone());
            response.setFromDate(filter.getFromDate());
            response.setToDate(filter.getToDate());

        if(shopImports.isEmpty()) return response;

        //PO (nhập)
        response.setImpPO(this.filterData(shopImports.stream().filter(t -> t.getTypess() == 0 && t.getType() != null && t.getType() == 1).collect(Collectors.toList())));
        //Nhập điều chỉnh
        response.setImpAdjust(this.filterData(shopImports.stream().filter(t -> t.getTypess() == 1).collect(Collectors.toList())));
        // Nhập vay mượn
        response.setImpBorrow(this.filterData(shopImports.stream().filter(t -> t.getTypess() == 2).collect(Collectors.toList())));
        // Xuất trả Po lấy ra âm - po trả ko lấy trong in
        //response.setExpPO(this.filterData(shopImports.stream().filter(t -> t.getTypess() == 0 && t.getType() != null && t.getType() == 2).collect(Collectors.toList())));

        Long totalQuantity = 0L;
        Double totalAmount = 0.0;
        Double totalAmountVAT =0.0;
        if(response.getImpPO()!=null){
            totalQuantity += response.getImpPO().getTotalQuantity();
            totalAmount += response.getImpPO().getTotalPriceVat();
            totalAmountVAT += response.getImpPO().getTotalPriceNotVat();
        }
        if(response.getImpAdjust()!=null){
            totalQuantity += response.getImpAdjust().getTotalQuantity();
            totalAmount += response.getImpAdjust().getTotalPriceVat();
            totalAmountVAT += response.getImpAdjust().getTotalPriceNotVat();
        }
        if(response.getImpBorrow()!=null){
            totalQuantity += response.getImpBorrow().getTotalQuantity();
            totalAmount += response.getImpBorrow().getTotalPriceVat();
            totalAmountVAT += response.getImpBorrow().getTotalPriceNotVat();
        }

        response.setTotalQuantity(totalQuantity);
        response.setTotalAmount(totalAmount);
        response.setTotalVat(totalAmount - totalAmountVAT);

        return response;
    }

    private PrintShopImportTotalDTO filterData( List<ShopImportDTO> importDatas) {
        if(importDatas == null || importDatas.isEmpty()) return null;
            //Gộp id đơn, DS SP thuộc ngành và đơn hàng đó
            Map<Long, List<ShopImportDTO>> orderMaps = importDatas.stream().collect(Collectors.groupingBy(ShopImportDTO::getOrderId));
            PrintShopImportTotalDTO shopImport = new PrintShopImportTotalDTO();

            int totalPoQty = 0;
            double totalPoPriceNotVat = 0;
            double totalPoPriceVat = 0;
            for(Map.Entry<Long, List<ShopImportDTO>> entry : orderMaps.entrySet()) {
                List<ShopImportDTO> orderMapValues = entry.getValue();

                OrderImportDTO orderImport = new OrderImportDTO();
                ShopImportDTO orderDf = orderMapValues.get(0);
                orderImport.setOrderDate(orderDf.getOrderDate());
                orderImport.setRedInvoiceNo(orderDf.getRedInvoiceNo());
                orderImport.setPoNumber(orderDf.getPoNumber());
                orderImport.setInternalNumber(orderDf.getInternalNumber());
                orderImport.setTransCode(orderDf.getTransCode());

                int totalOrderQty = 0;
                double totalOrderPriceNotVat = 0;
                double totalOrderPriceVat = 0;

                //Gộp id ngành hàng, DS SP thuộc ngành đó
                Map<Long, List<ShopImportDTO>> catMaps = orderMapValues.stream().collect(Collectors.groupingBy(ShopImportDTO::getCatId));
                List<ShopImportCatDTO> cats = new ArrayList<>();
                for(Map.Entry<Long, List<ShopImportDTO>> entryCat : catMaps.entrySet()) {
                    String catName = "";
                    int totalCatQty = 0;
                    double totalCatPriceNotVat = 0;
                    double totalCatPriceVat = 0;
                    for(ShopImportDTO product: entryCat.getValue()) {
                        catName = product.getProductInfoName();
                        int quantity = product.getQuantity()!=null?product.getQuantity():0;
                        double amountNotVat = product.getAmount()!=null?product.getAmount():0;
                        double amountVat = product.getTotal()!=null?product.getTotal():0;

                        totalCatQty += quantity;
                        totalCatPriceNotVat += amountNotVat;
                        totalCatPriceVat += amountVat;

                        totalOrderQty += quantity;
                        totalOrderPriceNotVat += amountNotVat;
                        totalOrderPriceVat += amountVat;

                        totalPoQty += quantity;
                        totalPoPriceNotVat += amountNotVat;
                        totalPoPriceVat += amountVat;

                    }

                    ShopImportCatDTO cat = new ShopImportCatDTO();
                    cat.setCatName(catName);
                    cat.setTotalQuantity(totalCatQty);
                    cat.setTotalPriceNotVat(totalCatPriceNotVat);
                    cat.setTotalPriceVat(totalCatPriceVat);
                    cat.setProducts(entryCat.getValue());
                    cats.add(cat);

                }
                Collections.sort(cats, Comparator.comparing(ShopImportCatDTO::getCatName));

                orderImport.setCats(cats);
                orderImport.setTotalQuantity(totalOrderQty);
                orderImport.setTotalPriceNotVat(totalOrderPriceNotVat);
                orderImport.setTotalPriceVat(totalOrderPriceVat);
                orderImport.setVat(totalOrderPriceVat - totalOrderPriceNotVat);

                shopImport.addOrderImport(orderImport);
            }
            shopImport.setTotalQuantity(totalPoQty);
            shopImport.setTotalPriceNotVat(totalPoPriceNotVat);
            shopImport.setTotalPriceVat(totalPoPriceVat);

            return shopImport;
    }

    private void removeDataList(List<ShopImportDTO> shopImports) {
        shopImports.remove(shopImports.size()-1);
        shopImports.remove(shopImports.size()-1);
    }
}
