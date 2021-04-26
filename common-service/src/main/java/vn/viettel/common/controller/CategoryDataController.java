package vn.viettel.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.common.service.CategoryDataService;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.Response;

import java.util.List;

@RestController
public class CategoryDataController extends BaseController {
    @Autowired
    CategoryDataService categoryDataService;
    private final String root = "/commons/categorydata";

    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<CategoryDataDTO> getCategoryDataById(@PathVariable Long id) {
        return categoryDataService.getCategoryDataById(id);
    }

    @GetMapping(value = { V1 + root + "/genders"})
    public Response<List<CategoryDataDTO>> getGenders(){
        return categoryDataService.getGenders();
    }

    @GetMapping(value = { V1 + root + "/get-by-group-code"})
    public List<CategoryDataDTO> getByCategoryGroupCode() {
        return categoryDataService.getByCategoryGroupCode();
    }

    @GetMapping(value = { V1 + root + "/reason/{id}"})
    public CategoryDataDTO getReasonById(@PathVariable Long id) {
        return categoryDataService.getReasonById(id);
    }
}
