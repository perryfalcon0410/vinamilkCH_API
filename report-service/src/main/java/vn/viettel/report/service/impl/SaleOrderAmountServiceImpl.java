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
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.report.messaging.SaleOrderAmountFilter;
import vn.viettel.report.service.SaleOrderAmountService;
import vn.viettel.report.service.dto.TableDynamicDTO;
import vn.viettel.report.service.excel.SaleOrderAmountExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class SaleOrderAmountServiceImpl implements SaleOrderAmountService {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ShopClient shopClient;

    @Override
    public ByteArrayInputStream exportExcel(SaleOrderAmountFilter filter) throws IOException {
        this.validMonth(filter);

        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        ShopDTO parentShopDTO = shopClient.getShopByIdV1(shopDTO.getParentShopId()).getData();
        TableDynamicDTO  tableDynamicDTO = this.callProcedure(filter);
        SaleOrderAmountExcel excel = new SaleOrderAmountExcel(filter, tableDynamicDTO, shopDTO, parentShopDTO);
        return excel.export();
    }

    @Override
    public TableDynamicDTO findAmounts(SaleOrderAmountFilter filter, Pageable pageable) {
        this.validMonth(filter);

        TableDynamicDTO procedure = this.callProcedure(filter);
        if(procedure.getResponse() == null) return new TableDynamicDTO();

        TableDynamicDTO reponse = new TableDynamicDTO(procedure.getDates(), procedure.getTotals());
        List<Object[]> allDatas = (List<Object[]>) procedure.getResponse();
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allDatas.size());
        Page<Object[]> page = new PageImpl<>( allDatas.subList(start, end), pageable, allDatas.size());
        reponse.setResponse(page);
        return reponse;
    }

    private void validMonth(SaleOrderAmountFilter filter){
        LocalDateTime fromDate = filter.getFromDate().plusDays(1);
        long monthsBetween = ChronoUnit.MONTHS.between(fromDate, filter.getToDate());
        if(monthsBetween >= 12) throw new ValidateException(ResponseMessage.NUMBER_OF_MONTH_LESS_THAN_OR_EQUAL_12);
    }

    public TableDynamicDTO callProcedure(SaleOrderAmountFilter filter){
        String nameOrCodeCustomer = VNCharacterUtils.removeAccent(filter.getNameOrCodeCustomer().toUpperCase(Locale.ROOT));

        Session session = entityManager.unwrap(Session.class);
        TableDynamicDTO tableDynamicDTO = new TableDynamicDTO();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        session.doWork(new Work() {
            @Override
            public void execute(Connection con) throws SQLException {
                try (CallableStatement cs = con.prepareCall("{CALL P_CUSTOMERS_SALE_ORDER_TOTAL(?,?,?,?,?,?,?,?,?,?)}")) {
                    cs.registerOutParameter(1, OracleTypes.CURSOR);
                    cs.registerOutParameter(2, OracleTypes.CURSOR);
                    cs.setLong(3, filter.getShopId());
                    if (filter.getFromDate() != null)
                        cs.setDate(4, new Date(filter.getFromDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
                    else cs.setNull(4, Types.DATE);

                    if (filter.getToDate() != null)
                        cs.setDate(5, new Date(filter.getToDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
                    else cs.setNull(5, Types.DATE);

                    if(filter.getCustomerTypeId() != null) {
                        cs.setLong(6, filter.getCustomerTypeId());
                    }else cs.setNull(6, Types.INTEGER);

                    cs.setString(7, nameOrCodeCustomer);
                    cs.setString(8, filter.getPhoneNumber());

                    if (filter.getFromAmount() != null)
                        cs.setFloat(9, filter.getFromAmount());
                    else cs.setNull(9, Types.INTEGER);

                    if (filter.getToAmount() != null)
                        cs.setFloat(10, filter.getToAmount());
                    else cs.setNull(10, Types.INTEGER);

                    cs.execute();
                    ResultSet rs = (ResultSet) cs.getObject(1);
                    ResultSet rs1 = (ResultSet) cs.getObject(2);

                    List<Object[]> rowData = new ArrayList<>();
                    ResultSetMetaData rsmd = rs.getMetaData();
                    while (rs.next()) {
                        Object[] rowDatas = new Object[rsmd.getColumnCount() + 1];
                        Float total = 0F;
                        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                            rowDatas[i - 1] = rs.getObject(i);
                            if(i > 3 && rs.getObject(i) != null) total += Float.valueOf(rs.getObject(i).toString());
                            if(i == rsmd.getColumnCount()) rowDatas[i] = total;
                        }
                        rowData.add(rowDatas);
                    }

                    if(!rowData.isEmpty()) {
                        tableDynamicDTO.setTotals(rowData.get(rowData.size() - 1));
                        rowData.remove(rowData.size() - 1);
                        tableDynamicDTO.setResponse(rowData);
                    }

                    List<String> dates = new ArrayList<>();
                    while (rs1.next()) {
                        dates.add(dateFormat.format(rs1.getTimestamp(1)));
                    }
                    tableDynamicDTO.setDates(dates);
                }
            }
        });

        return tableDynamicDTO;
    }

}
