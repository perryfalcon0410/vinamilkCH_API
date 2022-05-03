package vn.viettel.sale.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import vn.viettel.sale.service.dto.ProductQuantityListDTO;
import vn.viettel.sale.service.dto.ProductStockDTO;

@RestController
public class PoAutoController extends BaseController {

	private final String root = "/sales/po-auto";

	@Autowired
	PoAutoService poAutoService;

	@ApiOperation(value = "Api dùng để lấy tất cả danh sách mua hàng")
	@ApiResponse(code = 200, message = "Success")
	@GetMapping(value = { V1 + root + "/po-list" })
	public Response<Page<PoAutoDTO>> getAllPoAuto(HttpServletRequest httpRequest, @RequestParam(name = "page", required = false) Integer page) {

		if(page == null) page = 0;
		
		Page<PoAutoDTO> response = poAutoService.getAllPoAuto(this.getShopId(httpRequest), page);

		return new Response<Page<PoAutoDTO>>().withData(response);
	}
	
	@ApiOperation(value = "Api dùng để tìm kiếm danh sách mua hàng")
	@ApiResponse(code = 200, message = "Success")
	@GetMapping(value = { V1 + root + "/po-search-list" })
	public Response<Page<PoAutoDTO>> getSearchPoAuto(HttpServletRequest httpRequest, 
													@RequestParam(name = "poAutoNumber", required = false) String poAutoNumber,
													@RequestParam(name = "poGroupCode", required = false) String poGroupCode,
													@RequestParam(value="fromCreateDate", required = false) Date fromCreateDate,
													@RequestParam(value="toCreateDate", required = false) Date toCreateDate,
													@RequestParam(value="fromApproveDate", required = false) Date fromApproveDate,
													@RequestParam(value="toApproveDate", required = false) Date toApproveDate,
													@RequestParam(name = "page", required = false) Integer page,
													@RequestParam(name = "poStatus", required = false) Integer poStatus) {

		if(poStatus == null) poStatus = -1;
		
		Page<PoAutoDTO> response = poAutoService.getSearchPoAuto(poAutoNumber,
																 poGroupCode, 
																 DateUtils.convertFromDate(fromCreateDate),
																 DateUtils.convertToDate(toCreateDate),
																 DateUtils.convertFromDate(fromApproveDate),
																 DateUtils.convertToDate(toApproveDate),
																 poStatus,
																 this.getShopId(httpRequest),
																 page);

		return new Response<Page<PoAutoDTO>>().withData(response);
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
    public Response<String> approvePoAuto(HttpServletRequest request,
                                                         @ApiParam("PO auto number need approve")
                                                         @RequestBody PoAutoNumberList poAutoNumberList ) {
    	
		String response = poAutoService.approvePoAuto(poAutoNumberList.getPoAutoNumberList(), this.getShopId(request));
        
        return new Response<String>().withData(response);
    }
	
    @PostMapping(value = {V1 + root + "/cancel-po"})
    @ApiOperation(value = "Hủy đơn PO")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<String> cancelPoAuto(HttpServletRequest request,
                                                         @ApiParam("PO auto number need cancel")
                                                         @RequestBody PoAutoNumberList poAutoNumberList ) {
    	
    	String response = poAutoService.cancelPoAuto(poAutoNumberList.getPoAutoNumberList(), this.getShopId(request));
        
        return new Response<String>().withData(response);
    }
    
	@ApiOperation(value = "Api dùng để lấy tất cả danh sách mua hàng")
	@ApiResponse(code = 200, message = "Success")
	@GetMapping(value = { V1 + root + "/product-list" })
	public Response<Page<ProductStockDTO>> getAllProductByPage(HttpServletRequest httpRequest, 
														@RequestParam(name = "keyword", required = false) String keyword,
														Pageable pageable) {
		Page<ProductStockDTO> response = poAutoService.getProductByPage(pageable, this.getShopId(httpRequest), keyword);
		
		return new Response<Page<ProductStockDTO>>().withData(response);
	}
	
    @PostMapping(value = {V1 + root + "/save-po"})
    @ApiOperation(value = "Lưu đơn PO")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<String> splitPO(HttpServletRequest request,
                                                         @ApiParam("PO auto list need save")
                                                         @RequestBody ProductQuantityListDTO productQuantityListDTO ) {
    	
    	String response = poAutoService.spiltPO(productQuantityListDTO, this.getShopId(request));
        
        return new Response<String>().withData(response);
    }
}
