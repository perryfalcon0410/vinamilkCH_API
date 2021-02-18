package vn.viettel.authorization.component;

import vn.viettel.core.db.entity.Distributor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import vn.viettel.authorization.service.DistributorService;

@Component
public class UserCronJob {
    private static final Logger log = LoggerFactory.getLogger(UserCronJob.class);

    @Autowired
    private DistributorService distributorService;


    @Autowired
    protected ModelMapper modelMapper;


    @Scheduled(cron = "0 0 01 1 * *")
    public void changeDistributorIncentiveRateJob() {
        log.info("====================");
        log.info("> run cronjob update current incentive rate of distributors");
        log.info("====================");
        this.changeDistributorIncentiveRate();
    }

    public void changeDistributorIncentiveRate() {
        List<Distributor> distributors = distributorService.getAll();

        distributors.forEach(distributor -> {
        	distributor.setCurrentPlanIncentiveRate(distributor.getNextPlanIncentiveRate());  
        	distributor.setNextPlatformIncentiveRate(distributor.getNextPlatformIncentiveRate());  
        	distributorService.update(distributor);
        });
    }

}
