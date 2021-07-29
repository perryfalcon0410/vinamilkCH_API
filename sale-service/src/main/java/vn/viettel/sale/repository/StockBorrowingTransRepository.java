package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.PoTrans;
import vn.viettel.sale.entities.StockBorrowingTrans;
import vn.viettel.core.repository.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StockBorrowingTransRepository extends BaseRepository<StockBorrowingTrans>, JpaSpecificationExecutor<StockBorrowingTrans> {

    @Query(value = "SELECT p FROM StockBorrowingTrans p WHERE p.createdAt>= :startDate And p.type =:type " +
            " AND p.id = (SELECT MAX (po.id) FROM StockBorrowingTrans po WHERE po.createdAt >= :startDate And po.type =:type ) " +
            " ORDER BY p.id desc, p.createdAt desc ")
    List<StockBorrowingTrans> getLastBorrowTrans(Integer type, LocalDateTime startDate);

}
