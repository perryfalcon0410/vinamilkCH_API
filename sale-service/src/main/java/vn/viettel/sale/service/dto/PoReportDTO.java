package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PoReportDTO {

    private String type; //type of bill;
    private String transCode;
    private String poNumber;
    private String invoiceNumber;
    private String transDate; //need fortmat dd/MM/yyyy hh:mm:ss
    private String internalNumber;
    private Date invoiceDate;

    private int quantity;
    private String totalPrice;

    private String note;

    private JRBeanCollectionDataSource groupProductsDataSource;

    public Map<String, Object> getParameters() {
        Map<String,Object> parameters = new HashMap<>();//param
        parameters.put("type", getType());
        parameters.put("transCode", getTransCode());
        parameters.put("poNumber", getPoNumber());
        parameters.put("invoiceNumber", getInvoiceNumber());
        parameters.put("transDate", getTransDate());
        parameters.put("internalNumber", getInternalNumber());
        parameters.put("invoiceDate", getInvoiceDate());

        parameters.put("quantity", getQuantity());
        parameters.put("totalPrice", getTotalPrice());

        parameters.put("not", getNote());

        return parameters;
    };

    public Map<String, Object> getDataSources() {
        Map<String,Object> dataSources = new HashMap<>();//field
        dataSources.put("groupProductsDataSource", groupProductsDataSource);

        return dataSources;
    }

}
