package vn.viettel.report.service.impl;
import oracle.jdbc.OracleTypes;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.service.dto.*;
import vn.viettel.report.service.ExchangeTransReportService;
import vn.viettel.report.service.excel.ExchangeTransExcel;
import vn.viettel.report.service.feign.CommonClient;
import vn.viettel.report.service.feign.ShopClient;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExchangeTransReportServiceImpl implements ExchangeTransReportService {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ShopClient shopClient;

    @Autowired
    CommonClient commonClient;

    @Override
    public ByteArrayInputStream exportExcel(ExchangeTransFilter filter) throws IOException {
        this.validMonth(filter);
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        ShopDTO parentShopDTO = shopClient.getShopByIdV1(shopDTO.getParentShopId()).getData();
        TableDynamicDTO tableDynamicDTO = this.callProcedure(filter);
        ExchangeTransExcel excel = new ExchangeTransExcel(filter,shopDTO,tableDynamicDTO,parentShopDTO);
        return excel.export();
    }

    private ExchangeTransReportFullDTO callStoreProcedure(ExchangeTransFilter filter) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_EXCHANGE_TRANS", ExchangeTransReportDTO.class);
        query.registerStoredProcedureParameter("EXCHANGE_TRANS", void.class,  ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("SALES", Integer.class, ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("transCode", String.class,  ParameterMode.IN);
        query.registerStoredProcedureParameter("fromDate", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toDate", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("reason", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("productKW", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("shopId", Integer.class, ParameterMode.IN);


        query.setParameter("transCode",filter.getTransCode());
        query.setParameter("fromDate", DateUtils.convertFromDate(filter.getFromDate()));
        query.setParameter("toDate", DateUtils.convertToDate(filter.getToDate()));
        query.setParameter("reason", filter.getReason());
        query.setParameter("productKW", filter.getProductKW());
        query.setParameter("shopId", Integer.valueOf(filter.getShopId().toString()));
        query.execute();
        List<ExchangeTransReportDTO> reportDTOS = query.getResultList();
        List<ExchangeTransReportRateDTO> reportDTOS1 = new ArrayList<>();
        if(query.hasMoreResults())
            reportDTOS1 = query.getResultList();
        ExchangeTransReportFullDTO reportFullDTOS = new ExchangeTransReportFullDTO();
        reportFullDTOS.setListData(reportDTOS);
        reportFullDTOS.setSales(reportDTOS1);
        return reportFullDTOS;
    }

    @Override
    public TableDynamicDTO callProcedure(ExchangeTransFilter filter){
        Session session = entityManager.unwrap(Session.class);
        TableDynamicDTO tableDynamicDTO = new TableDynamicDTO();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        session.doWork(new Work() {
            @Override
            public void execute(Connection con) throws SQLException {
                try (CallableStatement cs = con.prepareCall("{CALL P_EXCHANGE_TRANS(?,?,?,?,?,?,?,?)}")) {
                    cs.registerOutParameter(1, OracleTypes.CURSOR);
                    cs.registerOutParameter(2, OracleTypes.CURSOR);
                    if(filter.getTransCode() != null) {
                        cs.setString(3, filter.getTransCode());
                    }else cs.setNull(3, Types.INTEGER);

                    if (filter.getFromDate() != null)
                        cs.setDate(4, java.sql.Date.valueOf(filter.getFromDate()));
                    else cs.setNull(4, Types.DATE);

                    if (filter.getToDate() != null)
                        cs.setDate(5, java.sql.Date.valueOf(filter.getToDate()));
                    else cs.setNull(5, Types.DATE);

                    if(filter.getReason() != null) {
                        cs.setString(6, filter.getReason());
                    }else cs.setNull(6, Types.INTEGER);

                    if(filter.getProductKW() != null) {
                        cs.setString(7, filter.getProductKW());
                    }else cs.setNull(7, Types.INTEGER);

                    cs.setLong(8, filter.getShopId());

                    cs.execute();
                    ResultSet rs = (ResultSet) cs.getObject(1);
                    ResultSet rs1 = (ResultSet) cs.getObject(2);

                    List<Object[]> rowData = new ArrayList<>();
                    ResultSetMetaData rsmd = rs.getMetaData();
                    while (rs.next()) {
                        Object[] rowDatas = new Object[rsmd.getColumnCount() + 1];
//                        Float total = 0F;
                        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                            rowDatas[i - 1] = rs.getObject(i);
//                            if(i > 3 && rs.getObject(i) != null) total += Float.valueOf(rs.getObject(i).toString());
//                            if(i == rsmd.getColumnCount()) rowDatas[i] = total;
                        }
                        rowData.add(rowDatas);
                    }
                    if(!rowData.isEmpty()) {
                        tableDynamicDTO.setTotals(rowData.get(rowData.size() - 1));
                        rowData.remove(rowData.size() - 1);
                        tableDynamicDTO.setResponse(rowData);
                    }
                    List<String> dates = new ArrayList<>();
                    while (rs.next()) {
                        dates.add(dateFormat.format(rs.getTimestamp(1)));
                    }
                    tableDynamicDTO.setDates(dates);
                }
            }
        });

        return tableDynamicDTO;
    }

    @Override
    public TableDynamicDTO getExchangeTransReport(ExchangeTransFilter filter, Pageable pageable) {
        this.validMonth(filter);

        TableDynamicDTO procedure = this.callProcedure(filter);
        if(procedure.getResponse() == null) return null;

        TableDynamicDTO reponse = new TableDynamicDTO(procedure.getDates(), procedure.getTotals());
        List<Object[]> allDatas = (List<Object[]>) procedure.getResponse();
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allDatas.size());
        Page<Object[]> page = new PageImpl<>( allDatas.subList(start, end), pageable, allDatas.size());
        reponse.setResponse(page);
        return reponse;
    }

    private void validMonth(ExchangeTransFilter filter){
        LocalDate fromDate = filter.getFromDate().plusDays(1);
        long monthsBetween = ChronoUnit.MONTHS.between(fromDate, filter.getToDate());
        if(monthsBetween >= 12) throw new ValidateException(ResponseMessage.NUMBER_OF_MONTH_LESS_THAN_OR_EQUAL_12);
    }

    public CoverResponse<Page<ExchangeTransReportDTO>, ExchangeTransTotalDTO> getExchangeTransReport1(ExchangeTransFilter filter, Pageable pageable) {
        ExchangeTransReportFullDTO exchangeTransFull = this.callStoreProcedure(filter);
        List<ExchangeTransReportDTO> exchangeTransList = exchangeTransFull.getListData();
        ExchangeTransTotalDTO totalDTO = new ExchangeTransTotalDTO();
        List<ExchangeTransReportDTO> subList = new ArrayList<>();
        if(!exchangeTransList.isEmpty()) {
            ExchangeTransReportDTO total = exchangeTransList.get(exchangeTransList.size()-1);
            totalDTO.setTotalQuantity(total.getQuantity());
            totalDTO.setTotalAmount(total.getAmount());

            this.removeDataList(exchangeTransList);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), exchangeTransList.size());
            subList = exchangeTransList.subList(start, end);
        }
        Page<ExchangeTransReportDTO> page = new PageImpl<>( subList, pageable, exchangeTransList.size());
        CoverResponse response = new CoverResponse(page, totalDTO);
        return response;
    }

    public List<CategoryDataDTO> listReasonExchange() {
        List<CategoryDataDTO> reasons = commonClient.getReasonExchangeV1().getData();
        return reasons;
    }

    private void removeDataList(List<ExchangeTransReportDTO> exchangeTrans) {
        exchangeTrans.remove(exchangeTrans.size()-1);
        exchangeTrans.remove(exchangeTrans.size()-1);
    }
}
