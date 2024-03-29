package vn.viettel.report.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.viettel.core.dto.report.ReportStockAggregatedDTO;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.report.entity.ReportStockAggregated;

@Repository
public interface ReportStockAggregatedRepository extends BaseRepository<ReportStockAggregated>, JpaSpecificationExecutor<ReportStockAggregated>{
	
	@Query(value = "select "
			+ "(rsa.endingQuantity) "
			+ "from ReportStockAggregated rsa "
			+ "where rsa.shopId = :shopId "
			+ "and rsa.productId = :productId "
			+ "and rsa.rptDate = :rptDate")
	public List<Long> getStockAggregated(Long shopId, Long productId, Date rptDate);
	
	@Query(value = "select "
			+ "(SUM(rsa.impQuantity + rsa.impAdjustmentQuantity + rsa.impBorrowingQuantity + rsa.impReturnQuantity + rsa.impComboQuantity)) "
			+ "from ReportStockAggregated rsa "
			+ "where rsa.shopId = :shopId "
			+ "and rsa.productId = :productId "
			+ "and rsa.rptDate between :rptBegDate and :rptEndDate")
	public Long getImport(Long shopId, Long productId, Date rptBegDate, Date rptEndDate);

	@Query(value = "select "
			+ "(SUM(rsa.expSalesQuantity + rsa.expPromotionQuantity + rsa.expExchangeQuantity "
			+ "- rsa.impReturnQuantity)) "
			+ "from ReportStockAggregated rsa "
			+ "where rsa.shopId = :shopId "
			+ "and rsa.productId = :productId "
			+ "and rsa.rptDate between :rptBegDate and :rptEndDate")
	public Long getExport(Long shopId, Long productId, Date rptBegDate, Date rptEndDate);

	@Query(value = "select "
			+ "(SUM(rsa.expSalesQuantity + rsa.expExchangeQuantity "
			+ "- rsa.impReturnQuantity)) "
			+ "from ReportStockAggregated rsa "
			+ "where rsa.shopId = :shopId "
			+ "and rsa.productId = :productId "
			+ "and rsa.rptDate between :rptBegDate and :rptEndDate")
	public Long getCumulativeConsumption(Long shopId, Long productId, Date rptBegDate, Date rptEndDate);
}
