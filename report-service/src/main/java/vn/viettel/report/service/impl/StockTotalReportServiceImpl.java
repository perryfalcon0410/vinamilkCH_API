package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.DateUtils;
import vn.viettel.report.service.StockTotalReportService;
import vn.viettel.report.service.dto.StockTotalCatDTO;
import vn.viettel.report.service.dto.StockTotalInfoDTO;
import vn.viettel.report.service.dto.StockTotalReportDTO;
import vn.viettel.report.service.dto.StockTotalReportPrintDTO;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockTotalReportServiceImpl implements StockTotalReportService {
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    ShopClient shopClient;

    @Override
    public StockTotalReportPrintDTO print(Date stockDate, String productCodes, Long shopId) {
        ShopDTO shopDTO = shopClient.getShopByIdV1(shopId).getData();
        StockTotalReportPrintDTO printDTO = new StockTotalReportPrintDTO();
        List<StockTotalReportDTO> lists =  callProcedure(stockDate, productCodes, shopId);
        StockTotalReportDTO totalInfo = lists.get(lists.size() - 1);
        List<StockTotalReportDTO> listResults = lists.subList(0, lists.size() - 2);
        Map<Long, List<StockTotalReportDTO>> cats = listResults.stream().collect(Collectors.groupingBy(StockTotalReportDTO::getCatId));
        List<StockTotalCatDTO> dataByCat = new ArrayList<>();
        for (Map.Entry<Long, List<StockTotalReportDTO>> entry : cats.entrySet()) {
             StockTotalCatDTO cat = new StockTotalCatDTO();
             Integer quantity = 0;
             Double amount = 0.0;
             for(StockTotalReportDTO stock : entry.getValue()) {
                 cat.setCategory(stock.getProductCategory());
                 if(stock.getStockQuantity()!=null)
                 quantity += stock.getStockQuantity();
                 if(stock.getTotalAmount()!=null)
                 amount += stock.getTotalAmount();
             }
             cat.setTotalQuantity(quantity);
             cat.setTotalAmount(amount);
             cat.setData(cats.get(entry.getKey()));
             dataByCat.add(cat);
        }
        printDTO.setShopName(shopDTO.getShopName());
        printDTO.setShopTel(shopDTO.getMobiPhone());
        printDTO.setAddress(shopDTO.getAddress());
        printDTO.setDate(DateUtils.convertDateToLocalDateTime(stockDate));
        printDTO.setPrintDate(DateUtils.convertDateToLocalDateTime(new Date()));
        printDTO.setTotalInfo(totalInfo);
        printDTO.setDataByCat(dataByCat);
        return printDTO;
    }

    @Override
    public CoverResponse<Page<StockTotalReportDTO>, StockTotalInfoDTO> getStockTotalReport(Date stockDate, String productCodes, Long shopId, Pageable pageable) {
        List<StockTotalReportDTO> listResult =  callProcedure(stockDate, productCodes, shopId);
        if (listResult.isEmpty())
            return new CoverResponse<>(new PageImpl<>(listResult), null);
        StockTotalReportDTO totalInfo = listResult.get(listResult.size() - 1);
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), listResult.size() - 2);
        List<StockTotalReportDTO> subList = listResult.subList(start, end);
        return new CoverResponse<>(new PageImpl<>(subList),
                new StockTotalInfoDTO(totalInfo.getStockQuantity(), totalInfo.getPacketQuantity(), totalInfo.getUnitQuantity(), totalInfo.getTotalAmount()));
    }

   private List<StockTotalReportDTO> callProcedure(Date stockDate, String productCodes, Long shopId) {
       StoredProcedureQuery storedProcedure =
               entityManager.createStoredProcedureQuery("P_STOCK_COUNTING", StockTotalReportDTO.class);
       storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
       storedProcedure.registerStoredProcedureParameter(2, Date.class, ParameterMode.IN);
       storedProcedure.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
       storedProcedure.registerStoredProcedureParameter(4, Long.class, ParameterMode.IN);

       storedProcedure.setParameter(2, stockDate);
       storedProcedure.setParameter(3, productCodes);
       storedProcedure.setParameter(4, shopId);

       List<StockTotalReportDTO> listResult = storedProcedure.getResultList();
       return listResult;
   }
}
