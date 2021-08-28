package vn.viettel.customer.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.viettel.customer.service.CustomerService;

import java.time.LocalDate;

@Component
public class SchedulerManager {
    @Autowired
    CustomerService customerService;

    //Update per start day 0 0 0 * * ?
    @Scheduled(cron = "0 0 0 * * ?")
    public void schedulerStartDay() throws InterruptedException {
       // System.out.println("[customer] begin schedule - 00:00h  - " + new Date());
        int day = LocalDate.now().getDayOfMonth();
        if(day == 1) {
            customerService.updateCustomerStartMonth();
        }else{
            customerService.updateCustomerStartDay();
        }
      //  System.out.println("[customer] end schedule - " + new Date());
    }

}
