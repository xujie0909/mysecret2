package com.xujie.mysecret.web;

import com.xujie.mysecret.service.impl.WeChatServiceImpl;
import com.xujie.mysecret.utils.CheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Slf4j
@RestController
public class WeChatController {

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String index(HttpServletRequest request, HttpServletResponse response) throws Exception {

        WeChatServiceImpl weChatService = new WeChatServiceImpl();

        log.info("----------------开始处理微信发过来的消息------------------");
        // 微信服务器POST消息时用的是UTF-8编码，在接收时也要用同样的编码，否则中文会乱码；
        request.setCharacterEncoding("UTF-8");
        // 在响应消息（回复消息给用户）时，也将编码方式设置为UTF-8，原理同上；
        //response.setCharacterEncoding("UTF-8");
        //response.setHeader("Content-type", "text/html;charset=UTF-8");
        String respXml = weChatService.weChatHandle(request, response);
        if (StringUtils.isBlank(respXml)) {
            log.error("-------------处理微信消息失败-----------------------");
            return null;
        } else {
            log.info("----------返回微信消息处理结果-----------------------:" + respXml);
            return respXml;
        }
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public void connect(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //消息来源可靠性验证
        String signature = request.getParameter("signature");// 微信加密签名
        String timestamp = request.getParameter("timestamp");// 时间戳
        String nonce = request.getParameter("nonce");       // 随机数
        String echostr = request.getParameter("echostr");//成为开发者验证
        //确认此次GET请求来自微信服务器，原样返回echostr参数内容，则接入生效，成为开发者成功，否则接入失败
        PrintWriter out = response.getWriter();
        if (CheckUtil.checkSignature(signature, timestamp, nonce)) {
            System.out.println("=======请求校验成功======" + echostr);
            out.print(echostr);
        }
        out.close();
        out = null;

    }
}
