package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.service.BaseReportServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.report.messaging.StockTotalFilter;
import vn.viettel.report.service.StockTotalReportService;
import vn.viettel.report.service.dto.*;
import vn.viettel.report.service.excel.StockTotalReportExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockTotalReportServiceImpl extends BaseReportServiceImpl implements StockTotalReportService {

    @Autowired
    ShopClient shopClient;

    @Override
    public StockTotalReportPrintDTO print(StockTotalFilter filter) {
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        StockTotalReportPrintDTO printDTO = new StockTotalReportPrintDTO();
        List<StockTotalReportDTO> lists =  callProcedure(filter);
        StockTotalReportDTO totalInfo = lists.get(lists.size() - 1);
        List<StockTotalReportDTO> listResults = lists.subList(0, lists.size() - 2);
        /*
            LinkedHashMap::new danh sách lấy lên đã sort - chống sort lại của map đảm bảo đúng thứ tự ngành hàng đã sort trước đó
         */
        Map<Long, List<StockTotalReportDTO>> cats = listResults.stream().collect(Collectors.groupingBy(StockTotalReportDTO::getCatId, LinkedHashMap::new, Collectors.toList()));
        List<StockTotalCatDTO> dataByCat = new ArrayList<>();
        for (Map.Entry<Long, List<StockTotalReportDTO>> entry : cats.entrySet()) {
             StockTotalCatDTO cat = new StockTotalCatDTO();
             Long quantity = 0L;
             Double amount = 0.0;
             List<StockTotalReportDTO> stocks =  entry.getValue();
             for(StockTotalReportDTO stock : stocks) {
                 cat.setCategory(stock.getProductCategory());
                 if(stock.getStockQuantity()!=null)
                 quantity += stock.getStockQuantity();
                 if(stock.getTotalAmount()!=null)
                 amount += stock.getTotalAmount();
             }
             Collections.sort(stocks, Comparator.comparing(StockTotalReportDTO::getProductCode));

             cat.setTotalQuantity(quantity);
             cat.setTotalAmount(amount);
             cat.setData(stocks);
             dataByCat.add(cat);
        }

        printDTO.setShopName(shopDTO.getShopName());
        printDTO.setShopTel(shopDTO.getPhone());
        printDTO.setAddress(shopDTO.getAddress());
        printDTO.setDate(DateUtils.convertDateToLocalDateTime(filter.getStockDate()));
        printDTO.setPrintDate(DateUtils.convertDateToLocalDateTime(new Date()));
        printDTO.setTotalInfo(totalInfo);
        printDTO.setDataByCat(dataByCat);
        return printDTO;
    }

    @Override
    public ByteArrayInputStream exportExcel(StockTotalFilter filter) throws IOException {
        ShopDTO shop = shopClient.getShopByIdV1(filter.getShopId()).getData();
        if(shop == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        List<StockTotalReportDTO> listResult =  callProcedure(filter);
        StockTotalReportDTO totalInfo = new StockTotalReportDTO();
        List<StockTotalReportDTO> listResults = new ArrayList<>();
        if(listResult !=null && !listResult.isEmpty()) {
            totalInfo = listResult.get(listResult.size() - 1);
            listResults = listResult.subList(0, listResult.size() - 2);
        }

        StockTotalExcelRequest input = new StockTotalExcelRequest(listResults,
                new StockTotalInfoDTO(totalInfo.getStockQuantity(), totalInfo.getPacketQuantity(), totalInfo.getUnitQuantity(), totalInfo.getTotalAmount()));
        StockTotalReportExcel exportExcel = new StockTotalReportExcel(input, shop, shop.getParentShop(), DateUtils.convert2Local(filter.getStockDate()));

       return exportExcel.export();
    }

    @Override
    public CoverResponse<Page<StockTotalReportDTO>, StockTotalInfoDTO> getStockTotalReport(StockTotalFilter filter, Pageable pageable) {
        List<StockTotalReportDTO> listResult =  callProcedure(filter);
        if (listResult.isEmpty())
            return new CoverResponse<>(new PageImpl<>(listResult), null);
        StockTotalReportDTO totalInfo = listResult.get(listResult.size() - 1);
        List<StockTotalReportDTO> listResults = listResult.subList(0, listResult.size() - 2);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), listResults.size());
        List<StockTotalReportDTO> subList = listResults.subList(start, end);

        Page<StockTotalReportDTO> page = new PageImpl<>(subList, pageable, listResults.size());
        CoverResponse response = new CoverResponse(page,
                new StockTotalInfoDTO(totalInfo.getStockQuantity(), totalInfo.getPacketQuantity(), totalInfo.getUnitQuantity(), totalInfo.getTotalAmount()));

        return response;
    }

   private List<StockTotalReportDTO> callProcedure(StockTotalFilter filter) {

       StoredProcedureQuery storedProcedure =
               entityManager.createStoredProcedureQuery("P_STOCK_COUNTING", StockTotalReportDTO.class);
       storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
       storedProcedure.registerStoredProcedureParameter(2, LocalDateTime.class, ParameterMode.IN);
       storedProcedure.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
       storedProcedure.registerStoredProcedureParameter(4, Long.class, ParameterMode.IN);
       storedProcedure.registerStoredProcedureParameter(5, Long.class, ParameterMode.IN);

       storedProcedure.setParameter(2, DateUtils.convertFromDate(filter.getStockDate()));
       storedProcedure.setParameter(3, filter.getProductCodes());
       storedProcedure.setParameter(4, filter.getShopId());
       storedProcedure.setParameter(5, filter.getWarehouseTypeId());

       List<StockTotalReportDTO> listResult = storedProcedure.getResultList();
       entityManager.close();
       return listResult;
   }
}
