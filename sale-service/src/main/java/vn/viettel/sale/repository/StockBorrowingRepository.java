package vn.viettel.sale.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.StockBorrowing;
import vn.viettel.sale.service.dto.StockBorrowingDTO;

public interface StockBorrowingRepository extends BaseRepository<StockBorrowing> {

    @Query("SELECT NEW vn.viettel.sale.service.dto.StockBorrowingDTO(sb.id, sb.poBorrowCode,sb.shopId, sb.toShopId," +
            " sb.borrowDate, sb.status,sb.wareHouseTypeId, sb.totalQuantity, sb.totalAmount, sb.note)" +
            " FROM StockBorrowing sb WHERE 1=1 AND sb.toShopId =:shopId AND sb.statusImport = 1 AND sb.status = 1 " +
            " AND sb.borrowDate >= :date1 AND sb.borrowDate <= :date2 " +
            " ORDER BY sb.poBorrowCode desc ")
    List<StockBorrowingDTO> getStockBorrowingImport(Long shopId,LocalDateTime date1,LocalDateTime date2);

    @Query("SELECT NEW vn.viettel.sale.service.dto.StockBorrowingDTO(sb.id, sb.poBorrowCode,sb.shopId, sb.toShopId," +
            " sb.borrowDate, sb.status,sb.wareHouseTypeId, sb.totalQuantity, sb.totalAmount, sb.note,w.wareHouseTypeName)" +
            " FROM StockBorrowing sb" +
            " JOIN WareHouseType w on w.id = sb.wareHouseTypeId " +
            " WHERE 1=1 AND sb.shopId =:shopId AND sb.statusExport = 1 AND sb.status = 1" +
            " AND sb.borrowDate >= :date1 AND sb.borrowDate <= :date2 " +
            " ORDER BY sb.poBorrowCode desc ")
    List<StockBorrowingDTO> getStockBorrowingExport(Long shopId,LocalDateTime date1,LocalDateTime date2);

    @Query("SELECT sb FROM StockBorrowing sb WHERE sb.id = :id AND sb.toShopId =:shopId AND sb.statusImport = 1 AND sb.status = 1 ")
    StockBorrowing getImportById(Long id, Long shopId);

    @Query("SELECT sb FROM StockBorrowing sb WHERE sb.id = :id AND sb.shopId =:shopId AND sb.statusExport = 1 AND sb.status = 1 ")
    StockBorrowing getExportById(Long id, Long shopId);

}
