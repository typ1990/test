package org.seckill.service;

import org.junit.Test;
import org.junit.internal.builders.JUnit4Builder;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by lisa on 2016/8/29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                      "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void testGetSeckillList() throws Exception {
        List<Seckill> list=seckillService.getSeckillList();
        logger.info("list={}",list);

    }

    @Test
    public void testGetById() throws Exception {
        long id=1000;
        Seckill seckill=seckillService.getById(id);
        logger.info("seckill={}",seckill);
    }

    //测试代码完整逻辑，注意可重复执行
    @Test
    public void testExportSeckillUrl() throws Exception {
        long id=1002;
        Exposer exposer=seckillService.exportSeckillUrl(id);
        //exposer=Exposer{exposed=true, md5='45c49ca170b89875b6f08e8bcdcf4040',
        // seckillId=1000, now=0, start=0, end=0}
        if(exposer.isExposed()){
            logger.info("exposer={}",exposer);
            long phone=13502171122L;
            String md5=exposer.getMd5();
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
                logger.info("result={}", seckillExecution);
            }catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }catch (RepeatKillException e){
                logger.error(e.getMessage());
            }
        }else{
            //秒杀为开启
        logger.warn("exposer={}", exposer);
        }
        // result=SeckillExecution{seckillId=1002, state=1,
        // stateInfo='秒杀成功', successKilled=SuccessKilled{seckillId=1002,
        // userPhone=13502171125, state=-1, createTime=Mon Aug 29 17:46:20 CST 2016}}

}

    @Test
    public void testExecuteSeckill() throws Exception {
        long id=1000;
        long phone=13502171125L;
        String md5="45c49ca170b89875b6f08e8bcdcf4040";
        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
            logger.info("result={}", seckillExecution);
        }catch (SeckillCloseException e){
            logger.error(e.getMessage());
        }catch (RepeatKillException e){
            logger.error(e.getMessage());
        }


    }

    @Test
    public void testExecuteSeckillProcedure() throws Exception {
        long seckillId=1001;
        long phone=136801110;
        Exposer exposer=seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
                String md5=exposer.getMd5();
                SeckillExecution seckillExecution= seckillService.executeSeckillProcedure(seckillId
                    , phone, md5);
            logger.info(seckillExecution.getStateInfo());
        }

    }
}