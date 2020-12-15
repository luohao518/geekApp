package xyz.geekweb.wxpay;

import com.paypal.api.payments.DetailedRefund;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import xyz.geekweb.paypal.result.ExceptionMsg;
import xyz.geekweb.paypal.result.ResponseData;
import xyz.geekweb.paypal.service.PayPalService;
import xyz.geekweb.util.URLUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author lhao
 */
@Controller
@RequestMapping("/wxpay")
@Slf4j
public class WXPayController {

//    @GetMapping("notify")
//    public String notify2(HttpServletRequest request,HttpServletResponse response) {
//        return "2";
//    }

    @RequestMapping("notify")
    public void payReturn(HttpServletResponse response, HttpServletRequest request) throws Exception{
        // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        Map<String,String> map = doXMLParse(inputStream);
        String out_trade_no = map.get("out_trade_no");
        if (map.get("result_code").equals("SUCCESS")){
           log.info("支付成功");
        }else {
            log.info("支付失败");
        }
    }

    public static Map doXMLParse(InputStream inputStream) {
        Map m = new HashMap();
        try {
            SAXReader reader = new SAXReader();
            org.dom4j.Document doc = reader.read(inputStream);
            // 得到xml根元素
            Element rootElement = doc.getRootElement();
            // 得到根元素的所有子节点
            List<Element> elementList = rootElement.elements();
            for (Iterator<Element> iterator = elementList.iterator(); iterator.hasNext(); ) {
                Element element = iterator.next();
                String k = element.getName();
                String v = element.getText();
                m.put(k, v);
            }
            //关闭流
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }


//    @PostMapping("notify")
//    public String notify(HttpServletRequest request,HttpServletResponse response) {
//        log.info("微信回调接口方法 start");
//        log.info("微信回调接口 操作逻辑 start");
//        String inputLine = "";
//        String notityXml = "";
//        try {
//            while((inputLine = request.getReader().readLine()) != null){
//                notityXml += inputLine;
//            }
//            //关闭流
//            request.getReader().close();
//            log.info("微信回调内容信息："+notityXml);
////            //解析成Map
////            Map<String,String> map = doXMLParse(notityXml);
////            //判断 支付是否成功
////            if("SUCCESS".equals(map.get("result_code"))){
////                logger.info("微信回调返回是否支付成功：是");
////                //获得 返回的商户订单号
////                String outTradeNo = map.get("out_trade_no");
////                logger.info("微信回调返回商户订单号："+outTradeNo);
////                //访问DB
////                WechatAppletGolfPayInfo payInfo = appletGolfPayInfoMapper.selectByPrimaryKey(outTradeNo);
////                logger.info("微信回调 根据订单号查询订单状态："+payInfo.getPayStatus());
////                if("0".equals(payInfo.getPayStatus())){
////                    //修改支付状态
////                    payInfo.setPayStatus("1");
////                    //更新Bean
////                    int sqlRow = appletGolfPayInfoMapper.updateByPrimaryKey(payInfo);
////                    //判断 是否更新成功
////                    if(sqlRow == 1){
////                        logger.info("微信回调  订单号："+outTradeNo +",修改状态成功");
////                        //封装 返回值
////                        StringBuffer buffer = new StringBuffer();
////                        buffer.append("<xml>");
////                        buffer.append("<return_code>SUCCESS</return_code>");
////                        buffer.append("<return_msg>OK</return_msg>");
////                        buffer.append("</xml>");
////
////                        //给微信服务器返回 成功标示 否则会一直询问 咱们服务器 是否回调成功
////                        PrintWriter writer = response.getWriter();
////                        //返回
////                        writer.print(buffer.toString());
////                    }
////                }
////            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }



}
