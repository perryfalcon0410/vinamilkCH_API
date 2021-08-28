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
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.report.messaging.CustomerTradeFilter;
import vn.viettel.report.service.CustomerNotTradeService;
import vn.viettel.report.service.dto.CustomerNotTradePrintDTO;
import vn.viettel.report.service.dto.CustomerReportDTO;
import vn.viettel.report.service.dto.CustomerTradeDTO;
import vn.viettel.report.service.dto.CustomerTradeTotalDTO;
import vn.viettel.report.service.excel.CustomerTradeExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class CustomerNotTradeServiceImpl implements CustomerNotTradeService {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ShopClient shopClient;

    @Override
    public Object index(Date fromDate, Date toDate, Boolean isPaging, Pageable pageable, Long shopId) {
        List<CustomerReportDTO> result = this.customerNotTradeProcedures(fromDate, toDate, shopId);
        if (result.isEmpty())
            return new Response<Page<CustomerReportDTO>>()
                    .withData(new PageImpl<>(new ArrayList<>()));

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), result.size());
        List<CustomerReportDTO> subList = result.subList(start, end);

        if (isPaging)
            return new Response<Page<CustomerReportDTO>>().withData(new PageImpl<>(subList,pageable,result.size()));
        else
            return new Response<List<CustomerReportDTO>>().withData(result);
    }

    public CustomerNotTradePrintDTO printCustomerNotTrade(Date fromDate, Date toDate, Long shopId) {
        List<CustomerReportDTO> result = this.customerNotTradeProcedures(fromDate, toDate, shopId);
        CustomerNotTradePrintDTO printDTO = new CustomerNotTradePrintDTO();
        if(!result.isEmpty()) {
            ShopDTO shopDTO = shopClient.getShopByIdV1(shopId).getData();
            if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
            printDTO.setShopName(shopDTO.getShopName());
            printDTO.setAddress(shopDTO.getAddress());
            printDTO.setShopTel(shopDTO.getMobiPhone());
            printDTO.setFromDate(DateUtils.convertDateToLocalDateTime(fromDate));
            printDTO.setToDate(DateUtils.convertDateToLocalDateTime(toDate));
            printDTO.setPrintDate(DateUtils.convertDateToLocalDateTime(new Date()));
            printDTO.setData(result);
            return printDTO;
        }
        return printDTO;
    }

    private List<CustomerReportDTO> customerNotTradeProcedures(Date fromDate, Date toDate, Long ShopId) {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_CUSTOMER_NOT_TRADE", CustomerReportDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(4, Long.class, ParameterMode.IN);

        storedProcedure.setParameter(2, fromDate);
        storedProcedure.setParameter(3, toDate);
        storedProcedure.setParameter(4, ShopId);
        List<CustomerReportDTO> result = storedProcedure.getResultList();
        entityManager.close();
        return result;
    }

//    @Override
//    public Page<CustomerTradeDTO>  findCustomerTrades(CustomerTradeFilter filter, Pageable pageable) {
//        List<CustomerTradeDTO> customers = this.callProcedure(filter);
//        List<CustomerTradeDTO> subList = new ArrayList<>();
//        if(!customers.isEmpty()) {
//            int start = (int)pageable.getOffset();
//            int end = Math.min((start + pageable.getPageSize()), customers.size());
//            subList = customers.subList(start, end);
//        }
//        return new PageImpl<>( subList, pageable, customers.size());
//    }

    @Override
    public CoverResponse<Page<CustomerTradeDTO>, CustomerTradeTotalDTO> findCustomerTrades(CustomerTradeFilter filter, Pageable pageable) {
        List<CustomerTradeDTO> customers = this.callProcedure(filter);
        CustomerTradeTotalDTO totalDTO = new CustomerTradeTotalDTO();
        List<CustomerTradeDTO> subList = new ArrayList<>();

        if(!customers.isEmpty()) {
            CustomerTradeDTO total = customers.get(customers.size() -1);
            totalDTO.setTotalSaleAmount(total.getSaleAmount());
            this.removeDataList(customers);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), customers.size());
            subList = customers.subList(start, end);
        }
        Page<CustomerTradeDTO> page = new PageImpl<>( subList, pageable, customers.size());
        CoverResponse response = new CoverResponse(page, totalDTO);

        return response;
    }

    @Override
    public ByteArrayInputStream customerTradesExportExcel(CustomerTradeFilter filter) throws IOException {
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        List<CustomerTradeDTO> customers = this.callProcedure(filter);
        this.removeDataList(customers);
        CustomerTradeExcel excel = new CustomerTradeExcel(shopDTO, shopDTO.getParentShop(), customers);
        return excel.export();
    }

    private List<CustomerTradeDTO> callProcedure(CustomerTradeFilter filter) {
        String keySearchUpper = VNCharacterUtils.removeAccent(filter.getKeySearch().toUpperCase(Locale.ROOT));

        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("P_CUSTOMER_TRADE", CustomerTradeDTO.class );
        query.registerStoredProcedureParameter("results", void.class, ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("shopId", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("customerSearch", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("areaCode", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("customerType", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("customerStatus", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("customerPhone", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromCreateDate", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toCreateDate", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromPurchaseDate", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toPurchaseDate", LocalDateTime.class, ParameterMode.IN);

        query.setParameter("shopId", filter.getShopId());
        query.setParameter("customerSearch", keySearchUpper);
        query.setParameter("areaCode", filter.getAreaCode());
        query.setParameter("customerType", filter.getCustomerType());
        query.setParameter("customerStatus", filter.getCustomerStatus());
        query.setParameter("customerPhone", filter.getCustomerPhone());
        query.setParameter("fromCreateDate", filter.getFromCreateDate());
        query.setParameter("toCreateDate", filter.getToCreateDate());
        query.setParameter("fromPurchaseDate", filter.getFromPurchaseDate());
        query.setParameter("toPurchaseDate", filter.getToPurchaseDate());

        List<CustomerTradeDTO> result = query.getResultList();
        entityManager.close();

        return result;
    }

    private void removeDataList(List<CustomerTradeDTO> customerTrades) {
        customerTrades.remove(customerTrades.size() - 1);
        customerTrades.remove(customerTrades.size() - 1);
    }


}
