package vn.viettel.sale.repository;

import java.util.List;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.SaleOrderComboDiscount;

public interface SaleOrderComboDiscountRepository extends BaseRepository<SaleOrderComboDiscount> {
    List<SaleOrderComboDiscount> findAllBySaleOrderId(Long saleOrderId);
}
