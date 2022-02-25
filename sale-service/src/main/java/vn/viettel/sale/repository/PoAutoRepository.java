package vn.viettel.sale.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.PoAuto;
import vn.viettel.sale.entities.PoAutoDetail;
import vn.viettel.sale.entities.Product;

@Repository
public interface PoAutoRepository extends BaseRepository<PoAuto>, JpaSpecificationExecutor<PoAuto> {
	
    @Query(value = "select po from PoAuto po where (:poAutoNumber = null or po.poAutoNumber like %:poAutoNumber%) "
    		+ "and (:poGroupCode = null or po.groupCode like %:poGroupCode%) "
    		+ "and (po.createAt between :fromCreateDate and :toCreateDate) "
    		+ "and (po.approveDate between :fromApproveDate and :toApproveDate) "
    		+ "and (:poStatus < 0 or po.status = :poStatus)")
	public List<PoAuto> searchPoList (String poAutoNumber, String poGroupCode, LocalDateTime fromCreateDate, 
			LocalDateTime toCreateDate, LocalDateTime fromApproveDate, LocalDateTime toApproveDate, int poStatus);
    
    @Query(value = "select po from PoAuto po where po.poAutoNumber = :poAutoNumber")
    public PoAuto getPoAutoBypoAutoNumber (String poAutoNumber);
    
    @Query(value = "select pd from Product pd inner join PoAutoDetail po "
    		+ "on po.poAutoId = :poAutoId "
    		+ "and po.productId = pd.id")
    public List<Product> getPoAutoDetailProductById (Long poAutoId);
    
    @Query(value = "select po from PoAutoDetail po where po.poAutoId = :poAutoId")
    public List<PoAutoDetail> getPoAutoDetailById (Long poAutoId);
    
    @Modifying()
    @Query(value = "Update PoAuto SET status = 2, updateAt = :date Where poAutoNumber = :poAutoNumber")
    public int cancelPo (String poAutoNumber, LocalDateTime date);
    
    @Modifying()
    @Query(value = "Update PoAuto SET status = 1, approveDate = :date, updateAt = :date Where poAutoNumber = :poAutoNumber")
    public int approvePo (String poAutoNumber, LocalDateTime date);
}
