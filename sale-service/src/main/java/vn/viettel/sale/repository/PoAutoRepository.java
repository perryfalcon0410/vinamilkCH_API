package vn.viettel.sale.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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

@Repository
public interface PoAutoRepository extends BaseRepository<PoAuto>, JpaSpecificationExecutor<PoAuto> {
	
	@Query(value = "select po from PoAuto po where po.shopId = :shopId")
	public List<PoAuto> findAllPo(Long shopId, Pageable pageable);
	
    @Query(value = "select po from PoAuto po where (:poAutoNumber is null or po.poAutoNumber like %:poAutoNumber%) "
    		+ "and (:poGroupCode is null or po.groupCode like %:poGroupCode%) "
    		+ "and (po.createAt between :fromCreateDate and :toCreateDate) "
    		+ "and (po.approveDate between :fromApproveDate and :toApproveDate) "
    		+ "and (:poStatus = -1 or po.status = :poStatus) "
    		+ "and (po.shopId = :shopId)")
	public List<PoAuto> searchPoList (String poAutoNumber, String poGroupCode, LocalDateTime fromCreateDate, 
			LocalDateTime toCreateDate, LocalDateTime fromApproveDate, LocalDateTime toApproveDate, int poStatus, Long shopId);
    
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
}
