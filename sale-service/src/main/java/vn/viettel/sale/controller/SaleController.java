package vn.viettel.sale.controller;

import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.promotion.PromotionProgramDiscountDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.jms.JMSSender;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.utils.JMSType;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.OrderPromotionRequest;
import vn.viettel.sale.messaging.SaleOrderRequest;
import vn.viettel.sale.messaging.SalePromotionCalculationRequest;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.SalePromotionService;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.dto.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@Api(tags = "Api sử dụng cho tạo đơn bán hàng")
public class SaleController extends BaseController {
    @Autowired
    SaleService service;

    @Autowired
    ProductService productService;

    @Autowired
    SalePromotionService salePromotionService;
    
    @Autowired
    JMSSender jmsSender;

    private final String root = "/sales";

    @ApiOperation(value = "Api dùng để tạo mới đơn bán hàng, đơn hàng online")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 6000, message = "Khách hàng không tìm thấy"),
            @ApiResponse(code = 4007, message = "Tên đăng nhập hoặc mật khẩu không đúng"),
            @ApiResponse(code = 6014, message = "Không tìm thấy cửa hàng"),
            @ApiResponse(code = 6166, message = "Loại đơn hàng không hợp lệ"),
            @ApiResponse(code = 9010, message = "Không tìm thấy đơn online"),
            @ApiResponse(code = 9031, message = "Cửa hàng không có quyền chỉnh sửa đơn Online"),
            @ApiResponse(code = 9032, message = "Cửa hàng không có quyền tạo tay đơn Online"),
            @ApiResponse(code = 9019, message = "Sản phẩm không tồn tại"),
            @ApiResponse(code = 6174, message = "Không tìm thấy giá được áp dụng cho sản phẩm"),
            @ApiResponse(code = 6175, message = "Tạo mới thất bại"),
            @ApiResponse(code = 9015, message = "Đơn hàng đã được tạo"),
            @ApiResponse(code = 6100, message = "Số lượng mua vượt quá tồn kho")
    })
    @PostMapping(value = { V1 + root })
    public Response<HashMap> createSaleOrder(@Valid @ApiParam("Thông tin tạo mới đơn hàng") @RequestBody SaleOrderRequest request) {
        if (request.getProducts().isEmpty()) throw new ValidateException(ResponseMessage.EMPTY_LIST);
        Long id = (Long) service.createSaleOrder(request, this.getUserId(), this.getRoleId(), this.getShopId(), false);
        if (id != null) {
        	sendSynRequest(Arrays.asList(id));
        }
        Response<HashMap> response = new Response<>();
        HashMap<String,Long> map = new HashMap<>();
        map.put("orderId", id);
        return response.withData(map);
    }

    @ApiOperation(value = "Api dùng để lấy danh sách khuyến mãi cho một đơn hàng")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/order-promotions"})
    public Response<SalePromotionCalculationDTO> getOrderPromotions(@Valid @ApiParam("Thông tin mua hàng") @RequestBody OrderPromotionRequest orderRequest) {
        if (orderRequest == null || orderRequest.getProducts() == null || orderRequest.getProducts().size() < 1){
            throw new ValidateException(ResponseMessage.ORDER_ITEM_NOT_NULL);
        }

        SalePromotionCalculationDTO list = salePromotionService.getSaleItemPromotions(orderRequest, this.getShopId(), null, false);
        return new Response<SalePromotionCalculationDTO>().withData(list);
    }

    @ApiOperation(value = "Api dùng để lấy danh sách sản phẩm cho khuyến mãi tay")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/promotion-products"})
    public Response<List<FreeProductDTO>> getPromotionProduct(@Valid @ApiParam("ID chương trình khuyến mãi") @RequestParam Long promotionId,
                                                              @ApiParam("Tìm kiếm theo tên hoặc mã sản phẩm")
                                                              @RequestParam(name = "keyWord", required = false, defaultValue = "") String keyWord,
                                                              @RequestParam(name = "page", required = false) Integer page) {
        if (promotionId == null){
            throw new ValidateException(ResponseMessage.PROMOTION_DOSE_NOT_EXISTS);
        }

        if (page == null)
            page = 0;

        List<FreeProductDTO> response = productService.findFreeProductDTONoOrder(this.getShopId(), null, keyWord.trim(), page);

        return new Response<List<FreeProductDTO>>().withData(response);
    }

    @ApiOperation(value = "Api dùng để tính khuyến mãi")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/promotion-calculation"})
    public Response<SalePromotionCalculationDTO> promotionCalculation(@Valid @ApiParam("Thông tin cần tính") @RequestBody SalePromotionCalculationRequest calculationRequest) {

        SalePromotionCalculationDTO result = salePromotionService.promotionCalculation(calculationRequest, this.getShopId());
        return new Response<SalePromotionCalculationDTO>().withData(result);
    }

    @ApiOperation(value = "Feign lấy giá trị sản phẩm")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/price/{productId}"})
    public Response<PriceDTO> getPriceByPrID(@PathVariable Long productId) {
        return new Response<PriceDTO>().withData(productService.getProductPriceById(getShopId(), productId));
    }

    @ApiOperation(value = "Api dùng để in tạm hóa đơn")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 6000, message = "Khách hàng không tìm thấy"),
            @ApiResponse(code = 4007, message = "Tên đăng nhập hoặc mật khẩu không đúng"),
            @ApiResponse(code = 6014, message = "Không tìm thấy cửa hàng"),
            @ApiResponse(code = 6166, message = "Loại đơn hàng không hợp lệ"),
            @ApiResponse(code = 9010, message = "Không tìm thấy đơn online"),
            @ApiResponse(code = 9031, message = "Cửa hàng không có quyền chỉnh sửa đơn Online"),
            @ApiResponse(code = 9032, message = "Cửa hàng không có quyền tạo tay đơn Online"),
            @ApiResponse(code = 9019, message = "Sản phẩm không tồn tại"),
            @ApiResponse(code = 6174, message = "Không tìm thấy giá được áp dụng cho sản phẩm"),
            @ApiResponse(code = 6175, message = "Tạo mới thất bại"),
            @ApiResponse(code = 9015, message = "Đơn hàng đã được tạo"),
            @ApiResponse(code = 6100, message = "Số lượng mua vượt quá tồn kho")
    })
    @PostMapping(value = { V1 + root + "/printtmp"})
    public Response<PrintSaleOrderDTO> printTempSaleOrder(@Valid @ApiParam("Thông tin đơn hàng") @RequestBody SaleOrderRequest request) {
        Response<PrintSaleOrderDTO> response = new Response<>();
        PrintSaleOrderDTO result = (PrintSaleOrderDTO) service.createSaleOrder(request, this.getUserId(), this.getRoleId(), this.getShopId(), true);
        return response.withData(result);
    }


    @ApiOperation(value = "Api dùng để lấy mã giảm giá")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/discount-code/{code}"})
    public Response<SalePromotionDTO> getDiscountCode(@PathVariable("code") String discountCode, @Valid @ApiParam("Thông tin mua hàng") @RequestBody OrderPromotionRequest orderRequest) {
        if (orderRequest == null || orderRequest.getProducts() == null || orderRequest.getProducts().size() < 1){
            throw new ValidateException(ResponseMessage.ORDER_ITEM_NOT_NULL);
        }

        SalePromotionDTO discount = salePromotionService.getDiscountCode(discountCode, this.getShopId(), orderRequest);
        return new Response<SalePromotionDTO>().withData(discount);
    }
    
    private void sendSynRequest(List<Long> lstIds) {
		try {
			if(!lstIds.isEmpty()) {
				jmsSender.sendMessage(JMSType.sale_order, lstIds);
			}
		} catch (Exception ex) {
			LogFile.logToFile("vn.viettel.sale.service.impl.SaleServiceImpl.sendSynRequest", JMSType.sale_order, LogLevel.ERROR, null, "has error when encode data " + ex.getMessage());
		}
	}

    @ApiOperation(value = "Api dùng để lấy quyền chỉnh sửa đơn online")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/valid/online-order"})
    public Response<OnlineOrderValidDTO> getDiscountCode() {
        OnlineOrderValidDTO response = service.getValidOnlineOrder(this.getShopId());
        return new Response<OnlineOrderValidDTO>().withData(response);
    }

}
