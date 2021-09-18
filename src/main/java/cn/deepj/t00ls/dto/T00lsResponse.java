package cn.deepj.t00ls.dto;

import lombok.Data;
import lombok.ToString;

/**
 * t00ls 签到返回值
 *
 * @author qingdong.zhang
 * @version 1.0
 * @since 2021-07-21 9:25
 */
@Data
@ToString
public class T00lsResponse {

    private String status;
    private String message;

    private String formhash;

}
