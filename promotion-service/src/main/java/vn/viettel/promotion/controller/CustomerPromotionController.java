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
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.promotion.service.CustomerPromotionService;
import java.util.List;

@RestController
@Api(tags = "Api sử dụng để lấy khuyến mãi của khách hàng")
public class CustomerPromotionController extends BaseController {
    @Autowired
    CustomerPromotionService customerPromotionService;
    private final String root = "/cus-promotion";

    @RoleFeign
    @GetMapping(value = { V1 + root + "/cus-type/{programId}"})
    @ApiOperation(value = "Tìm kiếm loại khách hàng được áp dụng khuyến mãi")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<Long>> findCusPromotion(@PathVariable Long programId) {
        List<Long> customerTypes = customerPromotionService.getListCusType(programId);
        return new Response<List<Long>>().withData(customerTypes);
    }
    @RoleFeign
    @GetMapping(value = { V1 + root + "/member-card/{programId}"})
    @ApiOperation(value = "Tìm kiếm loại khách hàng thành viên được áp dụng khuyến mãi")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<Long>> findMemberCardPromotion(@PathVariable Long programId) {
        List<Long> memberCard = customerPromotionService.getListMemberCard(programId);
        return new Response<List<Long>>().withData(memberCard);
    }
    @RoleFeign
    @GetMapping(value = { V1 + root + "/cus-loyal/{programId}"})
    @ApiOperation(value = "Tìm kiếm khách hàng thân thiết được áp dụng khuyến mãi")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<Long>> findCusLoyalPromotion(@PathVariable Long programId) {
        List<Long> cusLoyal = customerPromotionService.getListCusLoyal(programId);
        return new Response<List<Long>>().withData(cusLoyal);
    }
    @RoleFeign
    @GetMapping(value = { V1 + root + "/cus-card/{programId}"})
    @ApiOperation(value = "Tìm kiếm thẻ thuộc tính khách hàng được áp dụng khuyến mãi")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<Long>> findCusCardPromotion(@PathVariable Long programId) {
        List<Long> cusCard = customerPromotionService.getListCusCard(programId);
        return new Response<List<Long>>().withData(cusCard);
    }
}
