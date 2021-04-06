package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PoProductReportDTO {

    DecimalFormat formatter = new DecimalFormat("###,###,###");

    private String type; //ngành hàng
    private Integer totalQuantity;
    private String totalPrice;

    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = formatter.format(totalPrice);
    }

    private List<PoReportProductDetailDTO> products = new ArrayList<>();
    private JRBeanCollectionDataSource productsDataSource;

    public JRBeanCollectionDataSource getProductsDataSource() {
        return new JRBeanCollectionDataSource(products, false);
    }

    public PoProductReportDTO addProduct(PoReportProductDetailDTO product) {
        this.products.add(product);
        return this;
    }
}
