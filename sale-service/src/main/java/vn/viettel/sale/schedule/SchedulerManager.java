package vn.viettel.sale.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerManager {

	@Scheduled(cron = "* */10 * * * *")
	public void getOnlineOrder() throws InterruptedException {
		System.out.println("Hi, I am a schedule");
	}
}
