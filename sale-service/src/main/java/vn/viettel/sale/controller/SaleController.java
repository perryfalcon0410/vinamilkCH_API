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

    @RequestMapping(value = V1 + root + "/processing", method = RequestMethod.GET)
    public void processData(HttpServletRequest request) {

        System.out.println(request.getRemoteAddr());
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        System.out.println("ipAddress1 - " + ipAddress);
        System.out.println("ipAddress2 - " + getClientIpAddressIfServletRequestExist());
        System.out.println("ipAddress3 - " + getClientIp(request));

        // some other code
    }

    private final String[] IP_HEADER_CANDIDATES = {
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
            "REMOTE_ADDR"
    };

    public String getClientIpAddressIfServletRequestExist() {

        if (RequestContextHolder.getRequestAttributes() == null) {
            return "0.0.0.0";
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        for (String header: IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
                String ip = ipList.split(",")[0];
                return ip;
            }
        }

        return request.getRemoteAddr();
    }
    private final String LOCALHOST_IPV4 = "127.0.0.1";
    private final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    public String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if(StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }

        if(StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if(StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if(LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    ipAddress = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }

        if(!StringUtils.isEmpty(ipAddress)
                && ipAddress.length() > 15
                && ipAddress.indexOf(",") > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }

        return ipAddress;
    }

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

    @ApiOperation(value = "Api dùng để lấy quyền chỉnh sửa đơn online")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/valid/online-order"})
    public Response<OnlineOrderValidDTO> getDiscountCode() {
        OnlineOrderValidDTO response = service.getValidOnlineOrder(this.getShopId());
        return new Response<OnlineOrderValidDTO>().withData(response);
    }

}
