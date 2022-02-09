package vn.viettel.sale.repository;

import java.util.List;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.SaleOrderComboDetail;

public interface SaleOrderComboDetailRepository extends BaseRepository<SaleOrderComboDetail> {
    List<SaleOrderComboDetail> findAllBySaleOrderId(Long saleOrderId);
}
