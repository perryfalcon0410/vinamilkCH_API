package vn.viettel.sale.schedule;

//import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.SaleService;

import java.time.LocalDateTime;

@Component
public class SchedulerManager{

	@Autowired
	OnlineOrderService onlineOrderService;

	@Autowired
	SaleService saleService;


	@Scheduled(cron = "* */10 * * * *")
	@SchedulerLock(name = "getOnlineOrder")
	public void getOnlineOrder() throws InterruptedException {
		onlineOrderService.getOnlineOrderSchedule();
	}

	@Scheduled(cron = "* */15 * * * *")
	@SchedulerLock(name = "uploadOnlineOrder")
	public void uploadOnlineOrder() throws InterruptedException {
		onlineOrderService.uploadOnlineOrderSchedule();
	}


	@Scheduled(cron = "*/1 * * * * *")
	@Transactional
	public void test() throws InterruptedException {
		SaleOrder saleOrder = new SaleOrder();
		saleOrder.setShopId(1L);
		saleOrder.setNote("test1");
		saleOrder.setOrderDate(LocalDateTime.now());
		ShopDTO shopDTO = new ShopDTO();
		shopDTO.setId(1L);
		shopDTO.setShopCode("SHOP1");
		saleOrder.setOrderNumber(saleService.createOrderNumber(shopDTO));
		saleService.safeSave(saleOrder, shopDTO);
	}

/*	@Transactional
	public SaleOrder safeSave(SaleOrder saleOrder, ShopDTO shopDTO){
		try {
			saleOrder.setOrderNumber(saleService.createOrderNumber(shopDTO));
			saleOrderRepository.save(saleOrder);
		}catch (DataIntegrityViolationException | ConstraintViolationException ex){
			saleOrder.setOrderNumber(saleService.createOrderNumber(shopDTO));
			saleOrderRepository.save(saleOrder);
		}

		return saleOrder;
	}*/

	@Scheduled(cron = "*/1 * * * * *")
	@Transactional
	public void tye() throws InterruptedException {
		SaleOrder saleOrder = new SaleOrder();
		saleOrder.setShopId(1L);
		saleOrder.setNote("test2");
		saleOrder.setOrderDate(LocalDateTime.now());
		ShopDTO shopDTO = new ShopDTO();
		shopDTO.setId(1L);
		shopDTO.setShopCode("SHOP1");
		saleOrder.setOrderNumber(saleService.createOrderNumber(shopDTO));
		saleService.safeSave(saleOrder, shopDTO);
	}

}
