package vn.viettel.report.service.impl;

import oracle.jdbc.OracleTypes;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Service;
import vn.viettel.report.messaging.SaleOrderAmountFilter;
import vn.viettel.report.service.SaleOrderAmountService;
import vn.viettel.report.service.dto.TableDynamicDTO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SaleOrderAmountServiceImpl implements SaleOrderAmountService {

    @PersistenceContext
    EntityManager entityManager;


    @Override
    public TableDynamicDTO findAmounts(SaleOrderAmountFilter filter) {
        return this.callProcedure(filter);
//        return null;
    }

    public TableDynamicDTO callProcedure(SaleOrderAmountFilter filter) {
        Session session = entityManager.unwrap(Session.class);
        TableDynamicDTO tableDynamicDTO = new TableDynamicDTO();
        session.doWork(new Work() {
            @Override
            public void execute(Connection con) throws SQLException {
                try (CallableStatement cs = con.prepareCall("{CALL P_CUSTOMERS_SALE_ORDER_TOTAL(?,?,?,?,?,?,?,?,?,?)}")) {
                    cs.registerOutParameter(1, OracleTypes.CURSOR);
                    cs.registerOutParameter(2, OracleTypes.CURSOR);
                    cs.setLong(3, filter.getShopId());
                    if (filter.getFromDate() != null)
                        cs.setDate(4, new java.sql.Date(filter.getFromDate().getTime()));
                    else cs.setNull(4, Types.DATE);

                    if (filter.getToDate() != null)
                        cs.setDate(5, new java.sql.Date(filter.getToDate().getTime()));
                    else cs.setNull(5, Types.DATE);

                    cs.setLong(6, filter.getCustomerTypeId());
                    cs.setString(7, filter.getNameOrCodeCustomer());
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
                        Object[] rowDatas = new Object[rsmd.getColumnCount()];
                        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                            rowDatas[i - 1] = rs.getObject(i);
                        }
                        rowData.add(rowDatas);
                    }
                    tableDynamicDTO.setTotals(rowData.get(rowData.size() - 1));
                    rowData.remove(rowData.size() - 1);
                    tableDynamicDTO.setDataset(rowData);

                    Set<String> headers = new HashSet<>();
                    while (rs1.next()) {
                        String b = rs1.getString(1);
                        headers.add(b);
                    }
                    tableDynamicDTO.setHeaders(headers);
                }
            }
        });

        return tableDynamicDTO;
    }

}
