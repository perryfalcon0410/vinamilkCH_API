package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.report.messaging.ShopExportFilter;
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.ReportExportGoodsService;
import vn.viettel.report.service.dto.*;
import vn.viettel.report.service.excel.ShopExportExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportExportGoodsServiceImpl implements ReportExportGoodsService {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ShopClient shopClient;

    @Override
    public CoverResponse<Page<ShopExportDTO>, TotalReport> index(ShopExportFilter filter, Pageable pageable) {
        List<ShopExportDTO> shopExports = this.callProcedure(filter);
        TotalReport totalDTO = new TotalReport();
        List<ShopExportDTO> subList = new ArrayList<>();
        if (!shopExports.isEmpty()) {
            ShopExportDTO total = shopExports.get(shopExports.size() - 1);
            totalDTO.setTotalQuantity(total.getQuantity());
            totalDTO.setTotalPacketQuantity(total.getWholesale());
            totalDTO.setTotalUnitQuantity(total.getRetail());
            totalDTO.setTotalAmountNotVat(total.getTotalPriceNotVat());
            totalDTO.setTotalAmountVat(total.getTotalPriceVat());

            this.removeDataList(shopExports);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), shopExports.size());
            subList = shopExports.subList(start, end);
        }
        Page<ShopExportDTO> page = new PageImpl<>(subList, pageable, shopExports.size());
        CoverResponse response = new CoverResponse(page, totalDTO);
        return response;
    }

    @Override
    public ByteArrayInputStream exportExcel(ShopExportFilter filter) throws IOException {
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        List<ShopExportDTO> shopExports = this.callProcedure(filter);
        TotalReport totalReport = new TotalReport();

        if (!shopExports.isEmpty()) {
            ShopExportDTO total = shopExports.get(shopExports.size() - 1);
            totalReport.setTotalQuantity(total.getQuantity());
            totalReport.setTotalPacketQuantity(total.getWholesale());
            totalReport.setTotalUnitQuantity(total.getRetail());
            totalReport.setTotalAmountNotVat(total.getTotalPriceNotVat());
            totalReport.setTotalAmountVat(total.getTotalPriceVat());

            this.removeDataList(shopExports);
        }

        ShopExportExcel excel = new ShopExportExcel(shopDTO, shopDTO.getParentShop(), shopExports, totalReport);
        excel.setFromDate(filter.getFromDate());
        excel.setToDate(filter.getToDate());

        return excel.export();

    }

    @Override
    public PrintShopExportDTO getDataToPrint(ShopExportFilter filter) {
        ShopDTO shop = shopClient.getShopByIdV1(filter.getShopId()).getData();
        if (shop == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        List<ShopExportDTO> shopExports = this.callProcedure(filter);
        this.removeDataList(shopExports);

        PrintShopExportDTO response = new PrintShopExportDTO(shop.getShopName(), shop.getAddress(), shop.getPhone());
        response.setFromDate(filter.getFromDate());
        response.setToDate(filter.getToDate());

        if (shopExports.isEmpty()) return response;

        // xuất trả PO
        response.setExpPO(this.filterData(shopExports.stream().filter(t -> t.getTypess() == 0).collect(Collectors.toList())));
        //Nhập điều chỉnh
        response.setExpAdjust(this.filterData(shopExports.stream().filter(t -> t.getTypess() == 1).collect(Collectors.toList())));
        // Nhập vay mượn
        response.setExpBorrow(this.filterData(shopExports.stream().filter(t -> t.getTypess() == 2).collect(Collectors.toList())));

        Long totalQuantity = 0L;
        Double totalAmount = 0.0;
        if (response.getExpPO() != null) {
            totalQuantity += response.getExpPO().getTotalQuantity();
            totalAmount += response.getExpPO().getTotalPriceNotVat();
        }
        if (response.getExpAdjust() != null) {
            totalQuantity += response.getExpAdjust().getTotalQuantity();
            totalAmount += response.getExpAdjust().getTotalPriceVat();
        }
        if (response.getExpBorrow() != null) {
            totalQuantity += response.getExpBorrow().getTotalQuantity();
            totalAmount += response.getExpBorrow().getTotalPriceVat();
        }

        response.setTotalQuantity(totalQuantity);
        response.setTotalAmount(totalAmount);

        return response;

    }

    private PrintShopExportTotalDTO filterData(List<ShopExportDTO> exportDatas) {
        if (exportDatas == null || exportDatas.isEmpty()) return null;
        //Gộp id đơn, DS SP thuộc ngành và đơn hàng đó
        Map<Long, List<ShopExportDTO>> orderMaps = exportDatas.stream().collect(Collectors.groupingBy(ShopExportDTO::getOrderId));
        PrintShopExportTotalDTO shopImport = new PrintShopExportTotalDTO();

        int totalPoQty = 0;
        double totalPoPriceNotVat = 0;
        double totalPoPriceVat = 0;
        for (Map.Entry<Long, List<ShopExportDTO>> entry : orderMaps.entrySet()) {
            List<ShopExportDTO> orderMapValues = entry.getValue();

            OrderExportDTO orderExp = new OrderExportDTO();
            ShopExportDTO orderDf = orderMapValues.get(0);
            orderExp.setOrderDate(orderDf.getOrderDate());
            orderExp.setRedInvoiceNo(orderDf.getRedInvoiceNo());
            orderExp.setPoNumber(orderDf.getPoNumber());
            orderExp.setInternalNumber(orderDf.getInternalNumber());
            orderExp.setTransCode(orderDf.getTransCode());

            int totalOrderQty = 0;
            double totalOrderPriceNotVat = 0;
            double totalOrderPriceVat = 0;

            //Gộp id ngành hàng, DS SP thuộc ngành đó
            Map<Long, List<ShopExportDTO>> catMaps = orderMapValues.stream().collect(Collectors.groupingBy(ShopExportDTO::getCatId));
            List<ShopExportCatDTO> cats = new ArrayList<>();
            for (Map.Entry<Long, List<ShopExportDTO>> entryCat : catMaps.entrySet()) {
                String catName = "";
                int totalCatQty = 0;
                double totalCatPriceNotVat = 0;
                double totalCatPriceVat = 0;
                for (ShopExportDTO product : entryCat.getValue()) {
                    catName = product.getProductInfoName();
                    int quantity = product.getQuantity() != null ? product.getQuantity() : 0;
                    double amountNotVat = product.getTotalPriceNotVat() != null ? product.getTotalPriceNotVat() : 0;
                    double amountVat = product.getTotalPriceVat() != null ? product.getTotalPriceVat() : 0;

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

                ShopExportCatDTO cat = new ShopExportCatDTO();
                cat.setCatName(catName);
                cat.setTotalQuantity(totalCatQty);
                cat.setTotalPriceNotVat(totalCatPriceNotVat);
                cat.setTotalPriceVat(totalCatPriceVat);
                cat.setProducts(entryCat.getValue());

                cats.add(cat);
            }

            Collections.sort(cats, Comparator.comparing(ShopExportCatDTO::getCatName));
            orderExp.setCats(cats);
            orderExp.setTotalQuantity(totalOrderQty);
            orderExp.setTotalPriceNotVat(totalOrderPriceNotVat);
            orderExp.setTotalPriceVat(totalOrderPriceVat);

            shopImport.addOrderImport(orderExp);
        }

        shopImport.setTotalQuantity(totalPoQty);
        shopImport.setTotalPriceNotVat(totalPoPriceNotVat);
        shopImport.setTotalPriceVat(totalPoPriceVat);

        return shopImport;
    }


    private List<ShopExportDTO> callProcedure(ShopExportFilter filter) {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_SHOP_EXPORT", ShopExportDTO.class);
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
        storedProcedure.setParameter(6, filter.getSearchKeywords());
        storedProcedure.setParameter(7, filter.getFromOrderDate());
        storedProcedure.setParameter(8, filter.getToOrderDate());
        storedProcedure.setParameter(9, filter.getShopId());
        storedProcedure.execute();
        List<ShopExportDTO> response =  storedProcedure.getResultList();
        entityManager.close();
        return response;
    }

    private void removeDataList(List<ShopExportDTO> shopExports) {
        shopExports.remove(shopExports.size() - 1);
        shopExports.remove(shopExports.size() - 1);
    }

}