package vn.viettel.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.common.service.CategoryDataService;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.Response;

import java.util.List;

@RestController
@RequestMapping("/api/sale/categorydata")
public class CategoryDataController{
    @Autowired
    CategoryDataService categoryDataService;

    @GetMapping("/{id}")
    public Response<CategoryDataDTO> getCategoryDataById(@PathVariable Long id) {
        return categoryDataService.getCategoryDataById(id);
    }

    @GetMapping("/genders")
    public Response<List<CategoryDataDTO>> getGenders(){
        return categoryDataService.getGenders();
    }

    @GetMapping("/get-by-group-code")
    public List<CategoryDataDTO> getByCategoryGroupCode() {
        return categoryDataService.getByCategoryGroupCode();
    }

    @GetMapping("/reason/{id}")
    public CategoryDataDTO getReasonById(@PathVariable Long id) {
        return categoryDataService.getReasonById(id);
    }
}
