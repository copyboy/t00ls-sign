package cn.deepj.t00ls.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 工具类
 *
 * @author qingdong.zhang
 * @version 1.0
 * @since 2021-07-20 16:35
 */
@Component
public class RestConfig {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
