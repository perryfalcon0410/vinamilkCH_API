package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.service.ComboProductService;
import vn.viettel.sale.service.dto.ComboProductDTO;

import java.util.List;

@RestController
@RequestMapping("/api/sale/combo-products")
public class ComboProductController extends BaseController {

    @Autowired
    ComboProductService comboProductService;

    @RoleAdmin
    @GetMapping()
    public Response<List<ComboProductDTO>> findComboProducts(@RequestParam(name = "keyWord", required = false, defaultValue = "") String keyWord,
                                                             @RequestParam(name = "status", required = false) Integer status,
                                                             Pageable pageable) {
        return comboProductService.findComboProducts(keyWord, status);
    }

    @RoleAdmin
    @GetMapping("/{id}")
    public Response<ComboProductDTO> findComboProducts(@PathVariable Long id) {
        return comboProductService.getComboProduct(id);
    }


}