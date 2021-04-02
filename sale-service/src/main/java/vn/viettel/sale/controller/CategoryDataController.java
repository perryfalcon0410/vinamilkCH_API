package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.common.ApParam;
import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.CategoryDataService;

import java.util.List;

@RestController
@RequestMapping("/api/sale")
public class CategoryDataController{
    @Autowired
    CategoryDataService categoryDataService;

    @GetMapping("/categorydata/findById/{id}")
    public Response<CategoryData> getCategoryDataById(@PathVariable Long id) {
        return categoryDataService.getCategoryDataById(id);
    }

    @GetMapping("/categorydata/findAllByCategoryGroupCode")
    public Response<List<CategoryData>> getAllByCategoryGroupCode(@RequestParam(value = "categoryGroupCode") String categoryGroupCode) {
        return categoryDataService.getAllByCategoryGroupCode(categoryGroupCode);
    }
}
