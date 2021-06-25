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
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.service.dto.*;
import vn.viettel.report.service.ExchangeTransReportService;
import vn.viettel.report.service.excel.ExchangeTransExcel;
import vn.viettel.report.service.feign.CommonClient;
import vn.viettel.report.service.feign.ShopClient;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        ExchangeTransReportDTO tableDynamicDTO = this.callProcedure(filter);
        ExchangeTransExcel excel = new ExchangeTransExcel(filter,shopDTO,tableDynamicDTO,parentShopDTO);
        return excel.export();
    }

    @Override
    public ExchangeTransReportDTO callProcedure(ExchangeTransFilter filter){
        Session session = entityManager.unwrap(Session.class);
        ExchangeTransReportDTO tableDynamicDTO = new ExchangeTransReportDTO();
        session.doWork(new Work() {
            @Override
            public void execute(Connection con) throws SQLException {
                try (CallableStatement cs = con.prepareCall("{CALL P_EXCHANGE_TRANS(?,?,?,?,?,?,?,?)}")) {
                    cs.registerOutParameter(1, OracleTypes.CURSOR);
                    cs.registerOutParameter(2, OracleTypes.CURSOR);
                    if(filter.getTransCode() != null) {
                        cs.setString(3, VNCharacterUtils.removeAccent(filter.getTransCode()).trim().toUpperCase(Locale.ROOT));
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
                        cs.setString(7, VNCharacterUtils.removeAccent(filter.getProductKW()).trim().toUpperCase(Locale.ROOT));
                    }else cs.setNull(7, Types.INTEGER);

                    cs.setLong(8, filter.getShopId());

                    cs.execute();
                    ResultSet rs = (ResultSet) cs.getObject(1);
                    ResultSet rs1 = (ResultSet) cs.getObject(2);

                    List<Object[]> rowData = new ArrayList<>();
                    ResultSetMetaData rsmd = rs.getMetaData();
                    while (rs.next()) {
                        Object[] rowDatas = new Object[rsmd.getColumnCount()];
                        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                            rowDatas[i - 1] = rs.getObject(i);
                        }
                        rowData.add(rowDatas);
                    }
                    if(!rowData.isEmpty()) {
                        tableDynamicDTO.setTotals(rowData.get(rowData.size() - 1));
                        rowData.remove(rowData.size() - 1);
                        tableDynamicDTO.setResponse(rowData);
                    }
                    List<Object[]> rowData1 = new ArrayList<>();
                    ResultSetMetaData rsmd1 = rs1.getMetaData();
                    while (rs1.next()) {
                        Object[] rowDatas = new Object[rsmd1.getColumnCount()];
                        for (int i = 1; i <= rsmd1.getColumnCount(); i++) {
                            rowDatas[i-1] = rs1.getObject(i);
                        }
                        rowData1.add(rowDatas);
                    }
                    if(!rowData.isEmpty()) {
                        tableDynamicDTO.setExchangeRate(rowData1);
                    }
                }
            }
        });
        return tableDynamicDTO;
    }

    @Override
    public ExchangeTransReportDTO getExchangeTransReport(ExchangeTransFilter filter, Pageable pageable) {
        this.validMonth(filter);

        ExchangeTransReportDTO procedure = this.callProcedure(filter);
        if(procedure.getResponse() == null) return null;

        ExchangeTransReportDTO reponse = new ExchangeTransReportDTO(procedure.getTotals());
        List<Object[]> allDatas = (List<Object[]>) procedure.getResponse();
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allDatas.size()-1);
        Page<Object[]> page = new PageImpl<>( allDatas.subList(start, end), pageable, allDatas.size());
        reponse.setResponse(page);
        return reponse;
    }

    private void validMonth(ExchangeTransFilter filter){
        LocalDate fromDate = filter.getFromDate().plusDays(1);
        long monthsBetween = ChronoUnit.MONTHS.between(fromDate, filter.getToDate());
        if(monthsBetween >= 12) throw new ValidateException(ResponseMessage.NUMBER_OF_MONTH_LESS_THAN_OR_EQUAL_12);
    }

    public List<CategoryDataDTO> listReasonExchange() {
        List<CategoryDataDTO> reasons = commonClient.getReasonExchangeV1().getData();
        return reasons;
    }
}
