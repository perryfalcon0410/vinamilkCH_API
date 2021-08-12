package vn.viettel.sale.repository;

import vn.viettel.sale.entities.SaleOrderComboDetail;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.SaleOrderComboDiscount;

import java.util.List;

public interface SaleOrderComboDetailRepository extends BaseRepository<SaleOrderComboDetail> {
    List<SaleOrderComboDetail> findAllBySaleOrderId(Long saleOrderId);
}
