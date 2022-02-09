package vn.viettel.sale.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.StockCountingDetail;
import vn.viettel.sale.service.dto.StockCountingExcel;
import vn.viettel.sale.service.dto.StockCountingExcelDTO;

public interface StockCountingDetailRepository extends BaseRepository<StockCountingDetail>, JpaSpecificationExecutor<StockCountingDetail> {

    List<StockCountingDetail> findByStockCountingId(Long id);

    @Query(value = "SELECT new vn.viettel.sale.service.dto.StockCountingExcel(p.id, p.productCode, p.productName, gcat.productInfoName, " +
            " cat.productInfoName, cat.productInfoCode, dtl.price, dtl.stockQuantity, p.uom1, p.uom2, coalesce(p.convFact, 1), dtl.quantity) " +
            " FROM StockCountingDetail dtl JOIN Product p ON p.id = dtl.productId " +
            " LEFT JOIN ProductInfo gcat ON p.groupCatId = gcat.id and gcat.type = 6 " +
            " LEFT JOIN ProductInfo cat ON p.catId = cat.id and cat.type = 1 " +
            " WHERE dtl.stockCountingId = :stockCountingId " +
            " ORDER BY cat.productInfoCode asc, p.productCode asc ")
    List<StockCountingExcel> getStockCountingExcel(Long stockCountingId);

    @Query(value = "SELECT new vn.viettel.sale.service.dto.StockCountingExcelDTO(p.id, p.productCode, p.productName, gcat.productInfoName, " +
            " cat.productInfoName, cat.productInfoCode, dtl.price, dtl.stockQuantity, p.uom1, p.uom2, coalesce(p.convFact, 1), dtl.quantity, dtl.packageQuantity) " +
            " FROM StockCountingDetail dtl JOIN Product p ON p.id = dtl.productId " +
            " LEFT JOIN ProductInfo gcat ON p.groupCatId = gcat.id and gcat.type = 6 " +
            " LEFT JOIN ProductInfo cat ON p.catId = cat.id and cat.type = 1 " +
            " WHERE dtl.stockCountingId = :stockCountingId " +
            " ORDER BY cat.productInfoCode asc, p.productCode asc ")
    List<StockCountingExcelDTO> getStockCountingDetail(Long stockCountingId);


/*    *//* ko lấy những detail ko có kiểm kê *//*
    @Query(value = "SELECT new vn.viettel.sale.service.dto.StockCountingExcel(p.id, p.productCode, p.productName, gcat.productInfoName, " +
            " cat.productInfoName, cat.productInfoCode, dtl.price, dtl.stockQuantity, p.uom1, p.uom2, coalesce(p.convFact, 1), dtl.quantity) " +
            " FROM StockCountingDetail dtl JOIN Product p ON p.id = dtl.productId " +
            " LEFT JOIN ProductInfo gcat ON p.groupCatId = gcat.id and gcat.type = 6 " +
            " LEFT JOIN ProductInfo cat ON p.catId = cat.id and cat.type = 1 " +
            " WHERE dtl.stockCountingId = :stockCountingId AND ( dtl.quantity > 0 OR dtl.stockQuantity > 0 )" +
            " ORDER BY cat.productInfoCode asc, p.productCode asc ")
    List<StockCountingExcel> getStockCountingExportExcel(Long stockCountingId);*/


    @Query(value = "SELECT new vn.viettel.sale.service.dto.StockCountingExcelDTO(p.id, p.productCode, p.productName, gcat.productInfoName, " +
            " cat.productInfoName, cat.productInfoCode, dtl.price, dtl.stockQuantity, p.uom1, p.uom2, coalesce(p.convFact, 1), dtl.quantity, dtl.packageQuantity) " +
            " FROM StockCountingDetail dtl JOIN Product p ON p.id = dtl.productId " +
            " LEFT JOIN ProductInfo gcat ON p.groupCatId = gcat.id and gcat.type = 6 " +
            " LEFT JOIN ProductInfo cat ON p.catId = cat.id and cat.type = 1 " +
            " WHERE dtl.stockCountingId = :stockCountingId AND ( dtl.quantity > 0 OR dtl.stockQuantity > 0 )" +
            " ORDER BY cat.productInfoCode asc, p.productCode asc ")
    List<StockCountingExcelDTO> getStockCountingExportExcel(Long stockCountingId);
}
