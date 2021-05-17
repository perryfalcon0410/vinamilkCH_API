package vn.viettel.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeTransReportFullDTO {
    List<ExchangeTransReportDTO> listData;
    List<ExchangeTransReportRate> sales;
}
