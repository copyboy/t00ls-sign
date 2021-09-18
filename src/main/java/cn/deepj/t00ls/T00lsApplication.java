package cn.deepj.t00ls;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author ronhan
 */
@SpringBootApplication
@EnableScheduling
public class T00lsApplication {

    public static void main(String[] args) {
        SpringApplication.run(T00lsApplication.class, args);
    }

}
