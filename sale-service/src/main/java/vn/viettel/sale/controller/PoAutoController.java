package vn.viettel.sale.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.service.PoAutoService;
import vn.viettel.sale.service.dto.PoAutoDTO;
import vn.viettel.sale.service.dto.PoAutoDetailProduct;

@RestController
public class PoAutoController extends BaseController {

	private final String root = "/sales";

	@Autowired
	PoAutoService poAutoService;

	@ApiOperation(value = "Api dùng để lấy tất cả danh sách mua hàng")
	@ApiResponse(code = 200, message = "Success")
	@GetMapping(value = { V1 + root + "/po-list" })
	public Response<List<PoAutoDTO>> getAllPoAuto(HttpServletRequest httpRequest) {

		List<PoAutoDTO> response = poAutoService.getAllPoAuto();

		return new Response<List<PoAutoDTO>>().withData(response);
	}
	
	@ApiOperation(value = "Api dùng để tìm kiếm danh sách mua hàng")
	@ApiResponse(code = 200, message = "Success")
	@GetMapping(value = { V1 + root + "/po-search-list" })
	public Response<List<PoAutoDTO>> getSearchPoAuto(HttpServletRequest httpRequest, 
													@RequestParam String poAutoNumber,
													@RequestParam String poGroupCode,
													@RequestParam(value="fromCreateDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date fromCreateDate,
													@RequestParam(value="toCreateDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date toCreateDate,
													@RequestParam(value="fromApproveDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date fromApproveDate,
													@RequestParam(value="toApproveDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date toApproveDate,
													@RequestParam int poStatus) {

		List<PoAutoDTO> response = poAutoService.getSearchPoAuto(poAutoNumber,
																 poGroupCode, 
																 DateUtils.convertFromDate(fromCreateDate),
																 DateUtils.convertToDate(toCreateDate),
																 DateUtils.convertFromDate(fromApproveDate),
																 DateUtils.convertToDate(toApproveDate),
																 poStatus);

		return new Response<List<PoAutoDTO>>().withData(response);
	}
	
	@ApiOperation(value = "Api dùng để tìm kiếm danh sách mua hàng")
	@ApiResponse(code = 200, message = "Success")
	@GetMapping(value = { V1 + root + "/po-detail-product" })
	public Response<List<PoAutoDetailProduct>> getPoAutoProduct(HttpServletRequest httpRequest, @RequestParam Long poAutoId) {
		
		List<PoAutoDetailProduct> response = poAutoService.getPoAutoDetailProduct(poAutoId);
		
		return new Response<List<PoAutoDetailProduct>>().withData(response);
	}
}
