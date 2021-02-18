package vn.viettel.authorization.controller;

import vn.viettel.core.ResponseMessage;
import vn.viettel.authorization.component.UserCronJob;
import vn.viettel.core.messaging.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CronJobController {

    @Autowired
    UserCronJob cronJob;

    @GetMapping("/run-cron-jobs")
    public Response<String> runCronJobs(@RequestParam("job_name") String jobName) {
        Response<String> response = new Response<>();
        switch (jobName) {
            case "changeDistributorIncentiveRateJob":
                cronJob.changeDistributorIncentiveRateJob();
                break;
            default:
                response.setFailure(ResponseMessage.NOT_EXISTS);
        }
        return response;
    }

}
