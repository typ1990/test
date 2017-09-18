package org.seckill.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seckill.dao.hbase.HBaseUtilDao;
import org.seckill.dto.SeckillResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by typ on 2017/9/18.
 */
@Controller//@Service @Compent
@RequestMapping("/hbase")//url:/模块/资源/{id}/细分
public class HBaseController {
    Logger loggerDis =  LogManager.getLogger(HBaseController.class);
    @Autowired
    private HBaseUtilDao hBaseUtilDao;

    @ResponseBody
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public SeckillResult<Long> createTable(@RequestParam(value = "tableName", required = true) final String tableName){
        long aa=System.currentTimeMillis();
        loggerDis.info("进来了");
        return new SeckillResult<Long>(true,aa);
    }


}
