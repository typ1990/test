--数据库初始化脚本
--创建数据库
CREATE DATABASE seckill;
--使用数据库
use seckill;
--创建表 秒杀库存表
CREATE  TABLE seckill(
`seckill_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
`name` varchar(120) NOT NULL COMMENT '商品名称',
`number` int NOT NULL COMMENT '库存数量',
`start_time` TIMESTAMP  not null comment '秒杀开始时间',
`end_time` TIMESTAMP  not null comment '秒杀结束时间',
`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP  comment '创建时间',
PRIMARY KEY (seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT  CHARSET=utf8 COMMENT='秒杀库存表';

CREATE TABLE seckill (
`seckill_id`  bigint NOT NULL AUTO_INCREMENT ,
`name`  varchar(120) NOT NULL ,
`number`  int NOT NULL ,
`start_time`  timestamp NOT NULL ,
`end_time`  timestamp NOT NULL ,
`create_time`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
PRIMARY KEY (`seckill_id`),
INDEX `idx_start_time` (`start_time`) ,
INDEX `idx_end_time` (`end_time`) ,
INDEX `idx_create_time` (`create_time`)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARACTER SET=utf8;


CREATE TABLE `seckill` (
  `seckill_id` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`seckill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 初始化数据
insert into seckill(name,number,start_time,end_time) values ('1000元秒杀ipone6',100,'2016-11-01 00:00:00','2016-11-02 00:00:00'),
('500元秒杀ipad2',200,'2016-11-01 00:00:00','2016-11-02 00:00:00'),
('300元秒杀小米4',300,'2016-11-01 00:00:00','2016-11-02 00:00:00'),
('200元秒杀红米note',400,'2016-11-01 00:00:00','2016-11-02 00:00:00')



--秒杀成功明细表
--用户登录认证的相关信息
create table success_killed(
`seckill_id` bigint not null comment '秒杀商品id',
`user_phone` bigint not null comment '用户手机号',
`state` tinyint not null default -1 comment '状态标识：-1 无效 0：成功 1： 已付款',
`create_time` timestamp not null comment '创建时间',
primary key (seckill_id,user_phone),/*联合主键*/
key idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT  CHARSET=utf8 COMMENT='秒杀成功明细表';


--连接数据库控制台
