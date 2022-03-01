package vn.viettel.sale.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.service.PoAutoService;
import vn.viettel.sale.service.dto.PoAutoDTO;
import vn.viettel.sale.service.dto.PoAutoDetailProduct;
import vn.viettel.sale.service.dto.PoAutoNumberList;

@RestController
public class PoAutoController extends BaseController {

	private final String root = "/sales/po-auto";

	@Autowired
	PoAutoService poAutoService;

	@ApiOperation(value = "Api dùng để lấy tất cả danh sách mua hàng")
	@ApiResponse(code = 200, message = "Success")
	@GetMapping(value = { V1 + root + "/po-list" })
	public Response<List<PoAutoDTO>> getAllPoAuto(HttpServletRequest httpRequest, @RequestParam(name = "page", required = false) Integer page) {

		if(page == null) page = 0;
		
		List<PoAutoDTO> response = poAutoService.getAllPoAuto(this.getShopId(httpRequest), page);

		return new Response<List<PoAutoDTO>>().withData(response);
	}
	
	@ApiOperation(value = "Api dùng để tìm kiếm danh sách mua hàng")
	@ApiResponse(code = 200, message = "Success")
	@GetMapping(value = { V1 + root + "/po-search-list" })
	public Response<List<PoAutoDTO>> getSearchPoAuto(HttpServletRequest httpRequest, 
													@RequestParam(name = "poAutoNumber", required = false) String poAutoNumber,
													@RequestParam(name = "poGroupCode", required = false) String poGroupCode,
													@RequestParam(value="fromCreateDate") @DateTimeFormat(iso=ISO.DATE) Date fromCreateDate,
													@RequestParam(value="toCreateDate") @DateTimeFormat(iso=ISO.DATE) Date toCreateDate,
													@RequestParam(value="fromApproveDate") @DateTimeFormat(iso=ISO.DATE) Date fromApproveDate,
													@RequestParam(value="toApproveDate") @DateTimeFormat(iso=ISO.DATE) Date toApproveDate,
													@RequestParam int poStatus) {

		List<PoAutoDTO> response = poAutoService.getSearchPoAuto(poAutoNumber,
																 poGroupCode, 
																 DateUtils.convertFromDate(fromCreateDate),
																 DateUtils.convertToDate(toCreateDate),
																 DateUtils.convertFromDate(fromApproveDate),
																 DateUtils.convertToDate(toApproveDate),
																 poStatus,
																 this.getShopId(httpRequest));

		return new Response<List<PoAutoDTO>>().withData(response);
	}
	
	@ApiOperation(value = "Api dùng để lấy chi tiết của đơn hàng")
	@ApiResponse(code = 200, message = "Success")
	@GetMapping(value = { V1 + root + "/po-detail-product" })
	public Response<List<PoAutoDetailProduct>> getPoAutoProduct(HttpServletRequest httpRequest, @RequestParam String poAutoNumber) {
		
		List<PoAutoDetailProduct> response = poAutoService.getPoAutoDetailProduct(poAutoNumber, this.getShopId(httpRequest));
		
		return new Response<List<PoAutoDetailProduct>>().withData(response);
	}
	
	@PostMapping(value = {V1 + root + "/approve-po"})
    @ApiOperation(value = "Duyệt đơn PO")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Integer> approvePoAuto(HttpServletRequest request,
                                                         @ApiParam("PO auto number need approve")
                                                         @RequestBody PoAutoNumberList poAutoNumberList ) {
    	
    	int response = poAutoService.approvePoAuto(poAutoNumberList.getPoAutoNumberList(), this.getShopId(request));
        
        return new Response<Integer>().withData(response);
    }
	
    @PostMapping(value = {V1 + root + "/cancel-po"})
    @ApiOperation(value = "Hủy đơn PO")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Integer> cancelPoAuto(HttpServletRequest request,
                                                         @ApiParam("PO auto number need cancel")
                                                         @RequestBody PoAutoNumberList poAutoNumberList ) {
    	
    	int response = poAutoService.cancelPoAuto(poAutoNumberList.getPoAutoNumberList(), this.getShopId(request));
        
        return new Response<Integer>().withData(response);
    }
}
