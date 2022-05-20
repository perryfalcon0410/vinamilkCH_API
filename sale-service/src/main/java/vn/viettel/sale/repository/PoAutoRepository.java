package vn.viettel.sale.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.PoAuto;
import vn.viettel.sale.entities.PoAutoDetail;
import vn.viettel.sale.entities.PoTransDetail;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.SalePlan;
import vn.viettel.sale.service.dto.PoAutoDTO;

@Repository
public interface PoAutoRepository extends BaseRepository<PoAuto>, JpaSpecificationExecutor<PoAuto> {
	
	@Query(value = "select new vn.viettel.sale.service.dto.PoAutoDTO (po.poAutoNumber, po.groupCode, po.status, po.createAt, po.approveDate, po.amount) from PoAuto po where po.shopId = :shopId")
	public Page<PoAutoDTO> findAllPo(Long shopId, Pageable pageable);
	
    @Query(value = "select new vn.viettel.sale.service.dto.PoAutoDTO (po.poAutoNumber, po.groupCode, po.status, po.createAt, po.approveDate, po.amount) from PoAuto po "
    		+ "where (:poAutoNumber is null or po.poAutoNumber like %:poAutoNumber%) "
    		+ "and (:poGroupCode is null or po.groupCode like %:poGroupCode%) "
    		+ "and (:fromCreateDate is null or po.createAt >= :fromCreateDate) "
    		+ "and (:toCreateDate is null or po.createAt <= :toCreateDate) "
    		+ "and ((:fromApproveDate is null or po.approveDate is null) or po.approveDate >= :fromApproveDate) "
    		+ "and ((:toApproveDate is null or po.approveDate is null) or po.approveDate <= :toApproveDate) "
    		+ "and (:poStatus = -1 or po.status = :poStatus) "
    		+ "and (po.shopId = :shopId)")
	public Page<PoAutoDTO> searchPoList (String poAutoNumber, String poGroupCode, LocalDateTime fromCreateDate, 
			LocalDateTime toCreateDate, LocalDateTime fromApproveDate, LocalDateTime toApproveDate, int poStatus, Long shopId, Pageable pageable);
    
    @Query(value = "select po from PoAuto po where po.poAutoNumber = :poAutoNumber and (po.shopId = :shopId)")
    public PoAuto getPoAutoBypoAutoNumber (String poAutoNumber, Long shopId);
    
    @Query(value = "select pd from Product pd inner join PoAutoDetail po "
    		+ "on po.poAutoId = :poAutoId "
    		+ "and po.productId = pd.id ")
    public List<Product> getPoAutoDetailProductById (Long poAutoId);
    
    @Query(value = "select po from PoAutoDetail po where po.poAutoId = :poAutoId")
    public List<PoAutoDetail> getPoAutoDetailById (Long poAutoId);
    
    @Query(value = "select sp from SalePlan sp where sp.shopId = :shopId "
    		+ "and sp.productId = :productId "
    		+ "and sp.month between :fromMonth and :toMonth")
    public SalePlan getSalePlanByShopIdProductIdMonth(String shopId, Long productId, Date fromMonth, Date toMonth);
    
    @Modifying()
    @Query(value = "Update PoAuto SET status = 2, updateAt = :date Where poAutoNumber = :poAutoNumber "
    		+ "and shopId = :shopId")
    public int cancelPo (String poAutoNumber, LocalDateTime date, Long shopId);
    
    @Modifying()
    @Query(value = "Update PoAuto SET status = 1, approveDate = :date, updateAt = :date Where poAutoNumber = :poAutoNumber "
    		+ "and shopId = :shopId")
    public int approvePo (String poAutoNumber, LocalDateTime date, Long shopId);
    
    @Query(value = 
    		"select pd.product_name productName, pd.product_code productCode, pr.price price, st.quantity quantity from products pd "
    		+ "join ( "
    		+ "    select price, product_id from prices where (product_id, id) in ( "
    		+ "        select product_id, min(id) "
    		+ "        from prices where (product_id, from_date) in ( "
    		+ "            select product_id, max(from_date) "
    		+ "            from prices where status = 1 group by product_id) "
    		+ "        group by product_id) "
    		+ ") pr on pd.id = pr.product_id "
    		+ "join ( "
    		+ "    select quantity, product_id from stock_total st "
    		+ "    join warehouse_type wt on st.ware_house_type_id = wt.id "
    		+ "    and wt.warehouse_type_name = 'Cửa hàng' and wt.status = 1 "
    		+ "    where st.status = 1 and st.shop_id = ?1 "
    		+ ") st on st.product_id = pd.id "
    		+ "where pd.status = 1 and ( ?2 is null or pd.product_name like '%?2%') "
    		, nativeQuery = true)
    List<Tuple> getProductByPage(Long shopId, String keyword);
    
    @Query(value = "select po from PoAuto po order by po.id desc")
    public List<PoAuto> getNewestPoAutoNumber();
    
    @Query(value = "select ptd from PoTransDetail ptd "
    		+ "join PoTrans pt on pt.id = ptd.transId "
    		+ "and (pt.transDate = :transDate or 1 = 1)"
    		+ "and pt.shopId = :shopId "
    		+ "and pt.status = 1")
    List<PoTransDetail> getInputQuantity(Long shopId, Date transDate);
    
    @Query(value = "select SUM(potd.quantity + satd.quantity + sbtd.quantity) "
    		+ "from PoTrans pot "
    		+ "join PoTransDetail potd on potd.transId = pot.id "
    		+ "join StockAdjustmentTrans sat on pot.shopId = sat.shopId and sat.type = 1 and sat.status = 1 and (sat.adjustmentDate = :date or 1=1) "
    		+ "join StockAdjustmentTransDetail satd on satd.transId = sat.id "
    		+ "join StockBorrowingTrans sbt on pot.shopId = sbt.toShopId and sbt.type = 1 and sbt.status = 1 and (sbt.borrowDate = :date or 1=1) "
    		+ "join StockBorrowingTransDetail sbtd on sbt.id = sbtd.transId "
    		+ "where pot.status = 1 and pot.type = 1 and pot.transDate = :locDate "
    		+ "and pot.shopId = :shopId")
    Long getImportQuantity1(Long shopId, LocalDate date, LocalDateTime locDate);
    
    @Query(value = "select SUM(sod.quantity + cptd.quantity) "
    		+ "from SaleOrder so "
    		+ "join SaleOrderDetail sod on so.id = sod.saleOrderId "
    		+ "join ComboProductTrans cpt on so.shopId = cpt.shopId and cpt.transType = 1 and (cpt.transDate = :date or 1=1) "
    		+ "join ComboProductTransDetail cptd on cpt.id = cptd.transId and cptd.isCombo = 1 "
    		+ "where so.shopId = :shopId and so.type = 2 and so.orderDate = :date ")
    Long getImportQuantity2(Long shopId, LocalDate date);
    
    @Query(value = "select SUM(potd.quantity + satd.quantity + sbtd.quantity) "
    		+ "from PoTrans pot "
    		+ "join PoTransDetail potd on potd.transId = pot.id "
    		+ "join StockAdjustmentTrans sat on pot.shopId = sat.shopId and sat.type = 2 and sat.status = 3 and (sat.adjustmentDate = :date or 1=1) "
    		+ "join StockAdjustmentTransDetail satd on satd.transId = sat.id "
    		+ "join StockBorrowingTrans sbt on pot.shopId = sbt.shopId and sbt.type = 2 and sbt.status = 2 and (sbt.borrowDate = :date or 1=1) "
    		+ "join StockBorrowingTransDetail sbtd on sbt.id = sbtd.transId "
    		+ "where pot.status = 1 and pot.type = 2 and pot.transDate = :locDate "
    		+ "and pot.shopId = :shopId")
    Long getExportQuantity1(Long shopId, LocalDate date, LocalDateTime locDate);
    
    @Query(value = "select SUM(sod.quantity + cptd.quantity + etd.quantity - sod2.quantity) "
    		+ "from SaleOrder so "
    		+ "join SaleOrderDetail sod on so.id = sod.saleOrderId "
    		+ "join SaleOrder so2 on so.shopId = so2.shopId and so2.type = 2 and (so2.orderDate = :date or 1=1) "
    		+ "join SaleOrderDetail sod2 on so2.id = sod2.saleOrderId "
    		+ "join ComboProductTrans cpt on so.shopId = cpt.shopId and cpt.transType = 2 and (cpt.transDate = :date or 1=1) "
    		+ "join ComboProductTransDetail cptd on cpt.id = cptd.transId and cptd.isCombo = 1 "
    		+ "join ExchangeTrans et on so.shopId = et.shopId and (et.transDate = :date or 1=1) "
    		+ "join ExchangeTransDetail etd on et.id = etd.transId "
    		+ "where so.shopId = :shopId and so.type = 1 and so.orderDate = :date ")
    Long getExportQuantity2(Long shopId, LocalDate date);
    
    @Query(value = "select SUM(sod.quantity + etd.quantity - sod2.quantity) "
    		+ "from SaleOrder so "
    		+ "join SaleOrderDetail sod on so.id = sod.saleOrderId "
    		+ "join SaleOrder so2 on so.shopId = so2.shopId and so2.type = 2 and so2.orderDate = :date "
    		+ "join SaleOrderDetail sod2 on so2.id = sod2.saleOrderId "
    		+ "join ExchangeTrans et on so.shopId = et.shopId and et.transDate = :date "
    		+ "join ExchangeTransDetail etd on et.id = etd.transId "
    		+ "where so.shopId = :shopId and so.type = 1 and so.orderDate = :date ")
    Long getComsumptionQuantity(Long shopId, LocalDate date);

}
