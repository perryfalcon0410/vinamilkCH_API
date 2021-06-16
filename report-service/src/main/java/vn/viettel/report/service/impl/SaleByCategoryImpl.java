package vn.viettel.report.service.impl;

import oracle.jdbc.OracleTypes;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.report.messaging.SaleCategoryFilter;
import vn.viettel.report.service.SaleByCategoryReportService;
import vn.viettel.report.service.dto.SalesByCategoryReportDTO;
import vn.viettel.report.service.feign.CustomerTypeClient;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class SaleByCategoryImpl implements SaleByCategoryReportService {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    CustomerTypeClient customerTypeClient;

    public SalesByCategoryReportDTO callProcedure(SaleCategoryFilter filter){
        Session session = entityManager.unwrap(Session.class);
        SalesByCategoryReportDTO tableDynamicDTO = new SalesByCategoryReportDTO();
        session.doWork(new Work() {
            @Override
            public void execute(Connection con) throws SQLException {
                try (CallableStatement cs = con.prepareCall("{CALL P_SALES_BY_CATEGORY(?,?,?,?,?,?,?,?)}")) {
                    cs.registerOutParameter(1, OracleTypes.CURSOR);
                    cs.registerOutParameter(2, OracleTypes.CURSOR);
                    if(filter.getCustomerKW() != null) {
                        cs.setString(3, VNCharacterUtils.removeAccent(filter.getCustomerKW()).trim().toUpperCase(Locale.ROOT));
                    }else cs.setNull(3, Types.INTEGER);

                    if(filter.getCustomerPhone() != null) {
                        cs.setString(4, filter.getCustomerPhone().trim());
                    }else cs.setNull(4, Types.INTEGER);

                    if (filter.getFromDate() != null)
                        cs.setDate(5, java.sql.Date.valueOf(filter.getFromDate()));
                    else cs.setNull(5, Types.DATE);

                    if (filter.getToDate() != null)
                        cs.setDate(6, java.sql.Date.valueOf(filter.getToDate()));
                    else cs.setNull(6, Types.DATE);

                    if(filter.getCustomerType() != null) {
                        cs.setLong(7, filter.getCustomerType());
                    }else cs.setNull(7, Types.INTEGER);

                    cs.setLong(8, filter.getShopId());

                    cs.execute();
                    ResultSet rs = (ResultSet) cs.getObject(1);
                    ResultSet rs1 = (ResultSet) cs.getObject(2);

                    List<Object[]> rowData = new ArrayList<>();
                    ResultSetMetaData rsmd = rs1.getMetaData();
                    while (rs1.next()) {
                        Object[] rowDatas = new Object[rsmd.getColumnCount()];
                        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                            rowDatas[i - 1] = rs1.getObject(i);
                        }
                        rowData.add(rowDatas);
                    }
                    if(!rowData.isEmpty()) {
                        tableDynamicDTO.setTotals(rowData.get(rowData.size() - 1));
                        rowData.remove(rowData.size() - 1);
                        tableDynamicDTO.setResponse(rowData);
                    }
                }
            }
        });
        return tableDynamicDTO;
    }

    @Override
    public SalesByCategoryReportDTO getSaleByCategoryReport(SaleCategoryFilter filter, Pageable pageable) {
        this.validMonth(filter);

        SalesByCategoryReportDTO procedure = this.callProcedure(filter);
        if(procedure.getResponse() == null) return null;

        SalesByCategoryReportDTO reponse = new SalesByCategoryReportDTO(procedure.getTotals());
        List<Object[]> allDatas = (List<Object[]>) procedure.getResponse();
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allDatas.size());
        Page<Object[]> page = new PageImpl<>( allDatas.subList(start, end), pageable, allDatas.size());
        reponse.setResponse(page);
        return reponse;
    }

    private void validMonth(SaleCategoryFilter filter){
        LocalDate fromDate = filter.getFromDate().plusDays(1);
        long monthsBetween = ChronoUnit.MONTHS.between(fromDate, filter.getToDate());
        if(monthsBetween >= 12) throw new ValidateException(ResponseMessage.NUMBER_OF_MONTH_LESS_THAN_OR_EQUAL_12);
    }

    List<CustomerTypeDTO> getCustomerTypes(){
        List<CustomerTypeDTO> list = customerTypeClient.getCusTypesV1();
        return list;
    }
}
