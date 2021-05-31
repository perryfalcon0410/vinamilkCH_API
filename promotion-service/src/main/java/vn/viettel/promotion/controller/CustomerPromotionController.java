package vn.viettel.promotion.controller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.promotion.service.CustomerPromotionService;
import java.util.List;

@RestController
@Api(tags = "Api sử dụng để lấy khuyến mãi của khách hàng")
public class CustomerPromotionController extends BaseController {
    @Autowired
    CustomerPromotionService customerPromotionService;
    private final String root = "/cus-promotion";
    @GetMapping(value = { V1 + root + "/cus-type/{promotionId}"})
    @ApiOperation(value = "Tìm kiếm loại khách hàng được áp dụng khuyến mãi")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<Long>> findCusPromotion(@PathVariable Long promotionId) {
        List<Long> CustomerTypes = customerPromotionService.getListCusType(promotionId);
        return new Response<List<Long>>().withData(CustomerTypes);
    }
}
