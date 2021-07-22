package vn.viettel.sale.repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.SaleOrderComboDiscount;
import vn.viettel.sale.entities.SaleOrderDiscount;

import java.util.List;

public interface SaleOrderComboDiscountRepository extends BaseRepository<SaleOrderComboDiscount> {
    List<SaleOrderComboDiscount> findAllBySaleOrderId(Long saleOrderId);
}
