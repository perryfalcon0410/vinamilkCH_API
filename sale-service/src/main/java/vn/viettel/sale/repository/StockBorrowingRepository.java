package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockBorrowing;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.service.dto.StockBorrowingDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface StockBorrowingRepository extends BaseRepository<StockBorrowing> {

    @Query("SELECT NEW vn.viettel.sale.service.dto.StockBorrowingDTO(sb.id, sb.poBorrowCode,sb.shopId, sb.toShopId," +
            " sb.borrowDate, sb.status,sb.wareHouseTypeId, sb.totalQuantity, sb.totalAmount, sb.note)" +
            " FROM StockBorrowing sb WHERE 1=1 AND sb.toShopId =:shopId AND sb.statusImport = 1 AND sb.status = 1 " +
            " AND sb.borrowDate >= :date1 AND sb.borrowDate <= :date2 ")
    List<StockBorrowingDTO> getStockBorrowingImport(Long shopId,LocalDateTime date1,LocalDateTime date2);

    @Query("SELECT NEW vn.viettel.sale.service.dto.StockBorrowingDTO(sb.id, sb.poBorrowCode,sb.shopId, sb.toShopId," +
            " sb.borrowDate, sb.status,sb.wareHouseTypeId, sb.totalQuantity, sb.totalAmount, sb.note)" +
            " FROM StockBorrowing sb WHERE 1=1 AND sb.shopId =:shopId AND sb.statusExport = 1 AND sb.status = 1" +
            " AND sb.borrowDate >= :date1 AND sb.borrowDate <= :date2 ")
    List<StockBorrowingDTO> getStockBorrowingExport(Long shopId,LocalDateTime date1,LocalDateTime date2);

}
