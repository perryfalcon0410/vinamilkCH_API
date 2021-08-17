package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Chi tiết đơn hàng")
public class OrderImportDTO {

    @ApiModelProperty(notes = "Ngày hóa đơn")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;

    @ApiModelProperty(notes = "Số hóa đơn")
    private String redInvoiceNo;

    @ApiModelProperty(notes = "Số PO")
    private String poNumber;

    @ApiModelProperty(notes = "Số nội bộ")
    private String internalNumber;

    @ApiModelProperty(notes = "Mã nhập hàng")
    private String transCode;

    @ApiModelProperty(notes = "Tổng số lượng")
    private Integer totalQuantity;

    @ApiModelProperty(notes = "Tổng giá trước thuế - đối với PO thì lấy giá này")
    private Double totalPriceNotVat;

    @ApiModelProperty(notes = "Tổng giá sau thuế - đối với nhập điều chỉnh và vay mượn lấy giá này")
    private Double totalPriceVat;

    @ApiModelProperty(notes = "Tổng điều chỉnh")
    private Double adjusted;

    @ApiModelProperty(notes = "VAT")
    private Double vat;

    @ApiModelProperty(notes = "Danh sách ngành hàng")
    private List<ShopImportCatDTO> cats;


}
