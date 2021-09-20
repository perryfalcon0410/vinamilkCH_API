package vn.viettel.sale.controller;

import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.mapstruct.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.promotion.PromotionProgramDiscountDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.ResponseMessage;
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
import java.security.Principal;
import java.time.LocalDateTime;
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
    public Response<HashMap> createSaleOrder(HttpServletRequest httpRequest, @Valid @ApiParam("Thông tin tạo mới đơn hàng") @RequestBody SaleOrderRequest request) {
        Long id = (Long) service.createSaleOrder(request, this.getUserId(httpRequest), this.getShopId(httpRequest), false);
        //Logs theo dõi nhầm shop
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.ERROR, httpRequest, "[CHECK-PAYMENT - " + LocalDateTime.now() + "]" +
                "[clientIp: " + this.getClientIp(httpRequest)   + ", customer: " + request.getCustomerId() + ", shop_id : " +  this.getShopId(httpRequest)+ "-"+this.getShopId(httpRequest)+ ", username: " +this.getUsername(httpRequest) +"-"+ this.getUsername(httpRequest)+"][sale_order_id: " + id +"]");
        Response<HashMap> response = new Response<>();
        HashMap<String,Long> map = new HashMap<>();
        map.put("orderId", id);
        return response.withData(map);
    }

    @ApiOperation(value = "Api dùng để lấy danh sách khuyến mãi cho một đơn hàng")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/order-promotions"})
    public Response<SalePromotionCalculationDTO> getOrderPromotions(HttpServletRequest httpRequest, @Valid @ApiParam("Thông tin mua hàng") @RequestBody OrderPromotionRequest orderRequest) {
        SalePromotionCalculationDTO list = salePromotionService.getSaleItemPromotions(orderRequest, this.getShopId(httpRequest), null, false);
        return new Response<SalePromotionCalculationDTO>().withData(list);
    }

    @ApiOperation(value = "Api dùng để lấy danh sách sản phẩm cho khuyến mãi tay")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/promotion-products"})
    public Response<List<FreeProductDTO>> getPromotionProduct(HttpServletRequest httpRequest, @Valid @ApiParam("ID chương trình khuyến mãi") @RequestParam Long promotionId,
                                                              @ApiParam("ID khách hàng") @RequestParam Long customerId,
                                                              @ApiParam("Tìm kiếm theo tên hoặc mã sản phẩm")
                                                              @RequestParam(name = "keyWord", required = false, defaultValue = "") String keyWord,
                                                              @RequestParam(name = "page", required = false) Integer page) {
        if (customerId == null){
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        }

        if (page == null)
            page = 0;

        List<FreeProductDTO> response = productService.findFreeProductDTONoOrder(this.getShopId(httpRequest), customerId, keyWord.trim(), page);

        return new Response<List<FreeProductDTO>>().withData(response);
    }

    @ApiOperation(value = "Api dùng để tính khuyến mãi")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/promotion-calculation"})
    public Response<SalePromotionCalculationDTO> promotionCalculation(HttpServletRequest httpRequest, @Valid @ApiParam("Thông tin cần tính") @RequestBody SalePromotionCalculationRequest calculationRequest) {

        SalePromotionCalculationDTO result = salePromotionService.promotionCalculation(calculationRequest, this.getShopId(httpRequest));
        return new Response<SalePromotionCalculationDTO>().withData(result);
    }

    @ApiOperation(value = "Feign lấy giá trị sản phẩm")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/price/{productId}"})
    public Response<PriceDTO> getPriceByPrID(HttpServletRequest httpRequest, @PathVariable Long productId) {
        return new Response<PriceDTO>().withData(productService.getProductPriceById(getShopId(httpRequest), productId));
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
    public Response<PrintSaleOrderDTO> printTempSaleOrder(HttpServletRequest httpRequest, @Valid @ApiParam("Thông tin đơn hàng") @RequestBody SaleOrderRequest request) {
        Response<PrintSaleOrderDTO> response = new Response<>();
        PrintSaleOrderDTO result = (PrintSaleOrderDTO) service.createSaleOrder(request, this.getUserId(httpRequest), this.getShopId(httpRequest), true);
        return response.withData(result);
    }


    @ApiOperation(value = "Api dùng để lấy mã giảm giá")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/discount-code/{code}"})
    public Response<SalePromotionDTO> getDiscountCode(HttpServletRequest httpRequest, @PathVariable("code") String discountCode, @Valid @ApiParam("Thông tin mua hàng") @RequestBody OrderPromotionRequest orderRequest) {
        if (orderRequest == null || orderRequest.getProducts() == null || orderRequest.getProducts().size() < 1){
            throw new ValidateException(ResponseMessage.ORDER_ITEM_NOT_NULL);
        }

        SalePromotionDTO discount = salePromotionService.getDiscountCode(discountCode, this.getShopId(httpRequest), orderRequest);
        return new Response<SalePromotionDTO>().withData(discount);
    }

    @ApiOperation(value = "Api dùng để lấy quyền chỉnh sửa đơn online")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/valid/online-order"})
    public Response<OnlineOrderValidDTO> getDiscountCode(HttpServletRequest httpRequest) {
        OnlineOrderValidDTO response = service.getValidOnlineOrder(this.getShopId(httpRequest));
        return new Response<OnlineOrderValidDTO>().withData(response);
    }


    private final String LOCALHOST_IPV4 = "127.0.0.1";
    private final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR" };
    public String getClientIp(@Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        if(LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
            try {
                InetAddress inetAddress = InetAddress.getLocalHost();
                ipAddress = inetAddress.getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                ipAddress += "-" + ip;
            }
        }

        if(!StringUtils.isEmpty(ipAddress)
                && ipAddress.length() > 15
                && ipAddress.indexOf(",") > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }

        return ipAddress;
    }
}
