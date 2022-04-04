package vn.viettel.sale.repository;

import java.math.BigDecimal;
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
    		"select pd.product_name productName, pd.product_code productCode, pr.price price, st.quantity quantity from products pd join ( "
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
    
    @Query(value = 
    		"SELECT "
    		+ "    pog.po_auto_group_id groupId, "
    		+ "    pogd.object_type objectType, "
    		+ "    pogd.object_id objectId "
    		+ "FROM "
    		+ "    po_auto_group pog "
    		+ "    JOIN po_auto_group_detail   pogd ON pogd.po_auto_group_id = pog.po_auto_group_id "
    		+ "    LEFT JOIN po_auto_group_shop_map pogsm ON pogsm.shop_id = ?1 "
    		+ "WHERE "
    		+ "    ( pog.po_auto_group_shop_map_id IS NULL "
    		+ "      AND pogsm.po_auto_group_shop_map_id IS NULL ) "
    		+ "    OR ( pog.po_auto_group_shop_map_id = pogsm.po_auto_group_shop_map_id "
    		+ "         AND pogsm.po_auto_group_shop_map_id IS NOT NULL )"
    		, nativeQuery = true)
    List<Tuple> getSplitPO(Long shopId);
    
    @Query(value = 
    		"SELECT "
    		+ "    product_id "
    		+ "FROM "
    		+ "    pallet_shop_product "
    		+ "    WHERE shop_id = ?1"
    		, nativeQuery = true)
    List<Tuple> getPalletSplit(Long shopId);
    
    @Query(value = "select po from PoAuto po order by po.id")
    public List<PoAuto> getNewestPoAutoNumber();
    
    @Query(value = "select id from prices where (product_id, id) in ( "
    		+ "    select product_id, min(id) "
    		+ "    from prices where (product_id, from_date) in ( "
    		+ "        select product_id, max(from_date) "
    		+ "        from prices where status = 1 group by product_id) "
    		+ "    group by product_id) and product_id = ?1"
    		, nativeQuery = true)
    BigDecimal getNewPriceIdOfProduct(Long productId);
    
    @Query(value = "select price from prices where (product_id, id) in ( "
    		+ "    select product_id, min(id) "
    		+ "    from prices where (product_id, from_date) in ( "
    		+ "        select product_id, max(from_date) "
    		+ "        from prices where status = 1 group by product_id) "
    		+ "    group by product_id) and product_id = ?1"
    		, nativeQuery = true)
    BigDecimal getNewPriceOfProduct(Long productId);
}
