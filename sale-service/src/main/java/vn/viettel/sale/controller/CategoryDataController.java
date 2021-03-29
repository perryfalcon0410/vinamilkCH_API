package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.sale.service.CategoryDataService;

@RestController
@RequestMapping("/api/sale")
public class CategoryDataController{
    @Autowired
    CategoryDataService categoryDataService;

    @GetMapping("/findById/{id}")
    public CategoryData getCategoryDataById(@PathVariable long id) {
        return categoryDataService.getCategoryDataById(id).get();
    }
}
