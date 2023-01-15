package com.jivan.taskscheduler;

import com.jivan.taskscheduler.service.SchedulerJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final SchedulerJobService scheduleJobService;

    @RequestMapping(value = "/create", method = { RequestMethod.GET, RequestMethod.POST })
    public Object saveOrUpdate(SchedulerJobInfo job) {
        log.info("params, job = {}", job);
        try {
            scheduleJobService.scheduleNewJob(job);
        } catch (Exception e) {
            log.error("updateCron ex:", e);
        }
        return "saved";
    }
}
