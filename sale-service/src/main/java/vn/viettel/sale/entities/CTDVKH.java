package vn.viettel.sale.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CTDVKH {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "INVOICE_NUMBER")
    private String invoiceNumber;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "UOM1")
    private String uom1;
    @Column(name = "QUANTITY")
    private Integer quantity;
}
