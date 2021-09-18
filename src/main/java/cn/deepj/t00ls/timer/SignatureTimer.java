package cn.deepj.t00ls.timer;

import cn.deepj.t00ls.service.T00lsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 签到定时器
 *
 * @author qingdong.zhang
 * @version 1.0
 * @since 2021-07-20 16:14
 */
@Component
public class SignatureTimer {

    @Resource
    private T00lsService t00lsService;

    @Scheduled(cron = "15 45 8,17 * * ?")
//    @Scheduled(cron = "0/5 * * * * ?")
    public void signJob() {
        t00lsService.process();
    }

}
