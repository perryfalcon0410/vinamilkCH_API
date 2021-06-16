package vn.viettel.sale.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.convert.DTZConverter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@XStreamAlias("Header")
public class Header {
    @XStreamAlias("OrderID")
    private Long orderID;
    @XStreamAlias("OrderNumber")
    private String orderNumber;
    @XStreamAlias("CreatedAt")
    @XStreamConverter(DTZConverter.class)
    private LocalDateTime createdAt;
    @XStreamAlias("SourceName")
    private String sourceName;
    @XStreamAlias("StoreID")
    private String storeID;
    @XStreamAlias("TotalLineValue")
    private Float totalLineValue;
    @XStreamAlias("DiscountCode")
    private String discountCode;
    @XStreamAlias("DiscountValue")
    private Float discountValue;
    @XStreamAlias("CustomerName")
    private String customerName;
    @XStreamAlias("CustomerEmail")
    private String customerEmail;
    @XStreamAlias("CustomerPhone")
    private String customerPhone;
    @XStreamAlias("CustomerAddress")
    private String customerAddress;
    @XStreamAlias("CustomerDistrictCode")
    private String customerDistrictCode;
    @XStreamAlias("CustomerProvinceCode")
    private String customerProvinceCode;
    @XStreamAlias("CustomerIdNumber")
    private String customerIdNumber;
    @XStreamAlias("CustomerIdNumberPlace")
    private String customerIdNumberPlace;
    @XStreamAlias("CustomerIdNumberDate")
    @XStreamConverter(DTZConverter.class)
    private LocalDateTime customerIdNumberDate;
    @XStreamAlias("CustomerBirthday")
    @XStreamConverter(DTZConverter.class)
    private LocalDateTime customerBirthday;
    @XStreamAlias("CustomerGender")
    private String customerGender;
    @XStreamAlias("CustomerGroup")
    private String customerGroup;
    @XStreamAlias("CustomerStaffId")
    private String customerStaffId;
    @XStreamAlias("VatInvoice")
    private String vatInvoice;
    @XStreamAlias("CompanyName")
    private String companyName;
    @XStreamAlias("CompanyAddress")
    private String companyAddress;
    @XStreamAlias("CompanyTaxCode")
    private String companyTaxCode;
    @XStreamAlias("ShippingName")
    private String shippingName;
    @XStreamAlias("ShippingAddress")
    private String shippingAddress;
    @XStreamAlias("ShippingEmail")
    private String shippingEmail;
    @XStreamAlias("ShippingPhone")
    private String shippingPhone;
    @XStreamAlias("ShippingDistrictCode")
    private String shippingDistrictCode;
    @XStreamAlias("ShippingProvinceCode")
    private String shippingProvinceCode;
    @XStreamAlias("ShippingFee")
    private String shippingFee;
    @XStreamAlias("CarrierName")
    private String carrierName;
    @XStreamAlias("ShippingMethod")
    private String shippingMethod;
    @XStreamAlias("TotalOrderValue")
    private String totalOrderValue;
    @XStreamAlias("OrderStatus")
    private String orderStatus;
    @XStreamAlias("ShippingStatus")
    private String shippingStatus;
    @XStreamAlias("FinancialStatus")
    private String financialStatus;
    @XStreamAlias("Gateway")
    private String gateway;
    @XStreamAlias("CodStatus")
    private String codStatus;
    @XStreamAlias("Note")
    private String note;
    @XStreamAlias("OrderSource")
    private String orderSource;
    @XStreamAlias("OrderType")
    private String orderType;
    @XStreamAlias("PosOrderNumber")
    private String posOrderNumber;


}
