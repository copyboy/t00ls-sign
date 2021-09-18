package cn.deepj.t00ls.service;

import cn.deepj.t00ls.dto.T00lsResponse;
import cn.deepj.t00ls.utils.Jacksons;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * t00ls 签到服务
 *
 * @author qingdong.zhang
 * @version 1.0
 * @since 2021-07-20 16:29
 */
@Service
@Slf4j
public class T00lsService {


    private List<String> cookies;

    @Value("${t00ls.username}")
    private String username;
    @Value("${t00ls.password}")
    private String password;
    @Value("${t00ls.questionid}")
    private String questionid;
    @Value("${t00ls.answer}")
    private String answer;
    @Value("${t00ls.uid}")
    private String uid;
    @Value("${wechat.notifyUrl}")
    private String notifyUrl;
    @Value("${wechat.successNotify}")
    private Boolean successNotify;

    private String profileUrl = "https://www.t00ls.cc/members-profile-uid.html";
    private String loginUrl = "https://www.t00ls.cc/login.json";
    private String signUrl = "https://www.t00ls.cc/ajax-sign.json";
    private String checkUrl = "https://www.t00ls.cc/checklogin.html";

    private String host = "www.t00ls.cc";
    private String www = "https://www.t00ls.cc";
    private String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    private String defaultFormHash;

    @Resource
    private RestTemplate restTemplate;

    public void process() {

        try {
            login();
            // login 得到的 formhash 参数无效
            getFormHash();
            sign();
        } catch (Exception e) {
            log.error("发生异常, " , e);
            T00lsResponse response = new T00lsResponse();
            response.setStatus("fail");
            response.setMessage(e.getMessage());
            wechatNotify(response);
        }

    }

    private void login() {
        log.info("Login Begin...");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        headers.add("Host", host);
        headers.add("Origin", www);
        headers.add("Referer", loginUrl);
        headers.add("User-Agent", ua);

        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("action", "login");
        param.add("username", username);
        param.add("password", password);
        param.add("questionid", questionid);
        param.add("answer", answer);

        HttpEntity<Map> entity = new HttpEntity<>(param, headers);
        log.info("Login params [{}]", entity);

        ResponseEntity<String> resp;
        try {
            resp = restTemplate.postForEntity(loginUrl, entity, String.class);
            log.info("Login resp code[{}], resp [{}]", resp.getStatusCode(), resp.getBody());
        } catch (Exception e) {
            throw new RuntimeException("login 发生异常", e);
        }
        cookies = resp.getHeaders().get("Set-Cookie");
        log.info("Login saveCookie, cookies [{}]",  cookies);
        log.info("Login End...");

    }


    public void sign() {
        log.info("Sign Begin...");
        HttpHeaders header = buildHeaders();

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("formhash", defaultFormHash);
        params.add("signsubmit", "apply");
        params.add("submitcheck", false);

        HttpEntity<Map> entity = new HttpEntity<>(params, header);
        log.info("Sign exec, params [{}]", entity);

        ResponseEntity<String> resp;
        try {
            resp = restTemplate.postForEntity(signUrl, entity, String.class);
            log.info("Sign resp code[{}], resp [{}]", resp.getStatusCode(), resp.getBody());
        } catch (Exception e) {
            throw new RuntimeException("sign 发生异常", e);
        }
        T00lsResponse response = Jacksons.readJson(resp.getBody(), T00lsResponse.class);
        wechatNotify(response);

    }


    /**
     * 通知hook
     */
    private void wechatNotify(T00lsResponse resp) {

        log.info("WechatNotify Begin...");

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_TYPE, "application/json");

        JSONObject params = new JSONObject();
        params.put("msgtype", "text");
        JSONObject content = new JSONObject();

        String result = "";
        if ("success".equals(resp.getStatus())) {

            if (!successNotify) {
                return;
            }

            result = "成功";
        } else if ("fail".equals(resp.getStatus())) {
            result = "失败";
        }
        content.put("content", " T00ls签到[" + result + "]，时间[" + LocalDateTime.now() + "]，原因 [" + "" + resp.getMessage() + "]");
        params.put("text", content);

        HttpEntity<String> entity = new HttpEntity<>(params.toJSONString(), header);
        log.info("WechatNotify exec, params [{}]", entity);
        ResponseEntity<String> response = restTemplate.exchange(notifyUrl, HttpMethod.POST,
                entity, String.class);
        log.info("WechatNotify End..., resp[{}]", response.getBody());
    }
    private void getFormHash() {

        log.info("GetFormHash Begin...");
        HttpHeaders header = buildHeaders();

        HttpEntity<String> entity = new HttpEntity<>(header);
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(checkUrl, HttpMethod.GET,
                    entity, String.class);
            log.info("GetFormHash >>> resp [{}]", response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("getFormHash 发生异常", e);
        }

        String formHash = "formhash=\\w{8}";
        Pattern pattern = Pattern.compile(formHash);
        Matcher m = pattern.matcher(response.getBody());
        if (m.find()) {
            String[] group = m.group().split("=");
            defaultFormHash = group[1];
        }
        log.info("GetFormHash End... , value [{}]", defaultFormHash);
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders header = new HttpHeaders();
        header.put(HttpHeaders.COOKIE, cookies);
        header.add("Referer", profileUrl.replace("uid", uid));
        return header;
    }

}
