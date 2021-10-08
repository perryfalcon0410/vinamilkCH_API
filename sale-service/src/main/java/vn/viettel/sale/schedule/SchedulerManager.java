package vn.viettel.sale.schedule;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.viettel.sale.service.OnlineOrderService;

@Component
public class SchedulerManager{

	@Autowired
	OnlineOrderService onlineOrderService;

	@Scheduled(cron = "* */5 * * * *")
	@SchedulerLock(name = "getOnlineOrder")
	public void getOnlineOrder() throws InterruptedException {
		onlineOrderService.getOnlineOrderSchedule();
	}

	@Scheduled(cron = "* */15 * * * *")
	@SchedulerLock(name = "uploadOnlineOrder")
	public void uploadOnlineOrder() throws InterruptedException {
		onlineOrderService.uploadOnlineOrderSchedule();
	}

}
