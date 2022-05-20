package vn.viettel.sale.service.feign;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import vn.viettel.core.dto.report.ReportStockAggregatedDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "report-service")
public interface ReportStockClient {
    @GetMapping("api/v1/reports/stock-total/rpt-stock")
    Response<Long> getStockAggregated( 
			@RequestParam Long shopId,
            @RequestParam Long productId,
            @RequestParam(value="rptDate", required = false) LocalDate rptDate);
    
    @GetMapping("api/v1/reports/stock-total/rpt-stock-import")
    public Response<Long> getImport( 
    		@RequestParam Long shopId,
    		@RequestParam Long productId,
    		@RequestParam(value="rptDate", required = false) LocalDate rptBegDate,
    		@RequestParam(value="rptDate", required = false) LocalDate rptEndDate);

    @GetMapping("api/v1/reports/stock-total/rpt-stock-export")
    public Response<Long> getExport( 
    		@RequestParam Long shopId,
    		@RequestParam Long productId,
    		@RequestParam(value="rptDate", required = false) LocalDate rptBegDate,
    		@RequestParam(value="rptDate", required = false) LocalDate rptEndDate);
    
    @GetMapping("api/v1/reports/stock-total/rpt-stock-consumption")
    public Response<Long> getCumulativeConsumption( 
    		@RequestParam Long shopId,
    		@RequestParam Long productId,
    		@RequestParam(value="rptDate", required = false) LocalDate rptBegDate,
    		@RequestParam(value="rptDate", required = false) LocalDate rptEndDate);
}
