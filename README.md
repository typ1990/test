# test
java 高并发处理项目
SpringMVC+Spring+mybatis

项目笔记：


优化方案分析：   原子计数器--(技术实现)--->redis/NoSQL
                    |
                    |
                记录行为消息---(技术实现)-->分布式MQ(阿里RocketMQ,Apache ActiveMQ,LinkIn Kafka)
                    |
                    |
                消费消息并落地实现--(实现)-->Mysql
           成本分析:
                  运维成本：稳定性，NoSQL ,MQ
                  开发成本：数据一致性，回滚方案
                  幂等性难保证：重复秒杀
                  不适合新手




瓶颈分析:          update 减库存
                    |
                  GC|网络延迟
                  insert
                    |
                  GC|网络延迟
              commit/rollback



网络延迟分析:同城机房（0.5~2ms） max(1000qbs)
           -update后JVM-GC(50ms) max(20qps)
           异地机房（北京到上海为例，距离1300公里）1300*2/300000*(2/3)=13ms
           实际情况20ms左右
优化方案： 把客户端放到Mysql服务器，避免GC和网络延迟
如何把客户端放到MySQL服务器：
1，天猫/阿里解决方案：
    定制SQL方案：  update /*+[auto_commit]*/  ,需要修改Mysql源码，成本很高，一般公司没有技术能力、
2，使用存储过程，整个事务放在MySQL完成（银行经常使用）




优化总结：
    前端控制：暴露接口，按钮防止重复点击
    动态数据分离:CDN缓存，后端缓存(memcached,redis)
    事务竞争优化：减少事务锁时间
事务的优化：
    存储过程
        1，存储过程优化，优化事务行级锁持有时间
        2，不要过度依赖存储过程
        3，简单逻辑，可以应用存储过程
        4, qps 一个秒杀6000/qps




         智能DNS解析<-----------流量
               |                |
         Nginx | Nginx          |
               |             CDN 缓存
               |
               |
           逻辑集群----------->缓存集群（redis redis）
               |
               |
               |
           分库分表------------>统计分析
       （tddl淘宝内部技术）


数据层技术：
        数据库设计和实现
        MyBatis理解和使用技巧
        MyBatis整合Spring技巧
业务层：
    业务接口设计和封装
    SpringIoc配置技巧
    Spring声明式事务使用和理解
WEB技术回顾：
    Restful接口运用
    SpringMvc使用技巧
    前端交互过程
    Bootstrap的使用和Js的书写


并发优化：
        系统瓶颈优化
        事务锁，网络延迟的理解
        前端，CDN,缓存的使用
        集群化部署