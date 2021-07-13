package vn.viettel.customer.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.viettel.customer.repository.CustomerRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Date;

@Component
public class SchedulerManager {
    @Autowired
    CustomerRepository customerRepo;

    //Update per start day 0 0 0 * * ?
    @Scheduled(cron = "0 0 0 * * ?")
    public void schedulerStartDay() throws InterruptedException {
        int day = LocalDate.now().getDayOfMonth();
        if(day == 1) {
            this.updateCustomerStartMonth();
        }else{
            this.schedulerStartDay();
        }
        System.out.println("[customer] schedule - 00:00h  - " + new Date());
    }

    @Transactional()
    public void updateCustomerStartDay() {
        customerRepo.schedulerUpdateStartDay();
    }

    @Transactional()
    public void updateCustomerStartMonth() {
        customerRepo.schedulerUpdateStartMonth();
    }


}
