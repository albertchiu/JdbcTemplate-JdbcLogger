package idv.ccw.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import idv.ccw.service.UserService;

@Component
public class ScheduledTasks {
    @Autowired
    private UserService userService;

    //@Scheduled(fixedRate = 5000)
    @Scheduled(cron="0/5 * 8-18 ? * MON-FRI")
    public void printAllUsers() {
        System.out.println(userService.findAll());
    }
}