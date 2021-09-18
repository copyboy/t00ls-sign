# 使用说明



## 修改 配置

> application.properties 配置文件

```properties
t00ls.username=xxxx
# password 是md5后的密码, 可以在登录页面F12抓取该值
t00ls.password=xxxx
# questionid
# 1 母亲的名字
# 2 爷爷的名字
# 3 父亲出生的城市
# 4 您其中一位老师的名字
# 5 您个人计算机的型号
# 6 您最喜欢的餐馆名称
# 7 驾驶执照的最后四位数字
t00ls.questionid=xxxx
t00ls.answer=xxxx
# 用户ID 可在 T00LS » 我的资料 页面查询
t00ls.uid=xxxx

# 签到成功是否发送微信通知, true-发送,false-不发送，签到失败必定发送
# 暂时没做钉钉通知,没有有需要,可以提,到时加上
wechat.successNotify=true
wechat.notifyUrl=xxxxx


```



## 启动程序

如果具有开发能力，可以使用 maven 方式打包 

`mvn clean package -DskipTests`

在启动脚本

`nohup java -jar t00ls-0.0.1-SNAPSHOT.jar  > t00l.log  2>&1 &`



如果不具有开发能力，或者没有开发工具，

可以尝试使用 realese 提供的jar 包 ，使用以下 -D 参数 命令启动

```bash
# 展开说明
nohup java 
-Dt00ls.username=xxxx 
-Dt00ls.password=xxxx 
-Dt00ls.questionid=xxxx 
-Dt00ls.answer=xxxx 
-Dt00ls.uid=xxxx 
-Dwechat.successNotify=xxxx 
-Dwechat.notifyUrl="xxxx" 
-jar t00ls-0.0.1-SNAPSHOT.jar > t00l.log  2>&1 &


# 替换执行脚本
nohup java -Dt00ls.username=xxxx -Dt00ls.password=xxxx -Dt00ls.questionid=xxxx -Dt00ls.answer=xxxx -Dt00ls.uid=xxxx -Dwechat.successNotify=xxxx -Dwechat.notifyUrl="xxxx" -jar t00ls-0.0.1-SNAPSHOT.jar  > t00l.log  2>&1 &
```



## 修改定时器



我每天定时两次去执行脚本, 分别是 早上`8:45:15` 和 下午 `17:45:15` ，是避免早上出现网络波动,可以有重试的机会，

已经尝试了2个多月，出现过1次网络波动，早上没打上，晚上打的情况

```java
cn.deepj.t00ls.timer.SignatureTimer

@Scheduled(cron = "15 45 8,17 * * ?")
// @Scheduled(cron = "0/5 * * * * ?")
    
```