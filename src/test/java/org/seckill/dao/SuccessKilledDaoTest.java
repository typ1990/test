package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by lisa on 2016/8/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
    @Resource
    private SuccessKilledDao seckillDaoTest;

    @Test
    public void testInsertSuccessKilled() throws Exception {
        long id=1000L;
        long phone=13502181181L;
        int a=  seckillDaoTest.insertSuccessKilled(id,phone);
        System.out.println("00000000000000000000          "+a);
    }

    @Test
    public void testQueryByIdWithSeckill() throws Exception {
        long id=1000L;
        long phone=13502181181L;
        SuccessKilled a= seckillDaoTest.queryByIdWithSeckill(id,phone);
        System.out.println("00000000000000000000          "+a);
        System.out.println("********************          "+a.getSeckill());

    }
}