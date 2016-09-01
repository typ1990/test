package org.seckill.web;

import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by lisa on 2016/8/30.
 */
@Controller//@Service @Compent
@RequestMapping("/seckill")//url:/模块/资源/{id}/细分
public class SeckillController {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        //list.jsp+model=ModelAndView
        List<Seckill> seckills = seckillService.getSeckillList();
        model.addAttribute("list", seckills);
        return "list";
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String datail(@PathVariable("seckillId") Long seckillId, Model model) {

        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }


    //ajax json
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"}
    )
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable Long seckillId) {
        SeckillResult<Exposer> result;

        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }

        return result;
    }


    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charser=UTF-8 "}
    )
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(
            @PathVariable("seckillId") Long seckillId,
            @PathVariable("md5") String md5,
            @CookieValue(value = "killPhone", required = false) Long phone) {
        //springmvc valid
        if(phone==null){
            return  new SeckillResult<SeckillExecution>(false,"未注册");
        }

        SeckillResult<SeckillExecution> result=null;
        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId,
                    phone, md5);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        } catch (RepeatKillException e) {
            SeckillExecution execution=new SeckillExecution(seckillId,
                    SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true,execution);
        }catch (SeckillCloseException e) {
            SeckillExecution execution=new SeckillExecution(seckillId,
                    SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(true,execution);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            SeckillExecution execution=new SeckillExecution(seckillId,
                    SeckillStatEnum.INNER_ORROR);
            return new SeckillResult<SeckillExecution>(true,execution);
        }

    }

    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date date=new Date();
        return new SeckillResult<Long>(true,date.getTime());

    }

}
