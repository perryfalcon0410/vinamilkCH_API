package vn.viettel.common.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PRINTER_CONFIG")
public class PrinterConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "CLIENT_IP")
    private String clientIp;

    @Column(name = "BILL_PRINTER_NAME")
    private String billPrinterName;

    @Column(name = "REPORT_PRINTER_NAME")
    private String reportPrinterName;

    @Column(name = "DEFAULT_PRINTER_NAME")
    private String defaultPrinterName;

    @Column(name = "REMOVE_ACCENT", columnDefinition = "boolean default false")
    private Boolean removeAccent;

}
