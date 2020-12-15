package xyz.geekweb.wxpay;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付
 * @author jack.luo
 * @date 2020/12/15
 */
@Slf4j
public class WXPayExample {

    /**
     * 订单查询
     * @throws Exception
     */
    public void orderQuery() throws Exception {

        MyConfig config = new MyConfig();
        WXPay wxpay = new WXPay(config,true,true);

        Map<String, String> data = new HashMap<String, String>();
        data.put("out_trade_no", "2016090910595900000012");

        try {
            Map<String, String> resp = wxpay.orderQuery(data);
            log.info("map={}",resp);
            String xml = WXPayUtil.mapToXml(resp);
            log.info("xml={}",xml);
        } catch (Exception e) {
            log.error("orderQuery",e);
        }
    }

    /**
     * 统一下单
     * @throws Exception
     */
    public void unifiedOrder() throws Exception {

        MyConfig config = new MyConfig();
        WXPay wxpay = new WXPay(config,true,true);

        Map<String, String> data = new HashMap<String, String>();
        data.put("body", "腾讯充值中心-QQ会员充值");
        data.put("out_trade_no", "2016090910595900000012");
        data.put("device_info", "");
        data.put("fee_type", "CNY");
        //金额以分为单位
        data.put("total_fee", "101");
        data.put("spbill_create_ip", "112.74.89.58");
        data.put("notify_url", "http://4c84qs.natappfree.cc/wxpay/notify");
        // 此处指定为扫码支付
        data.put("trade_type", "NATIVE");
        data.put("product_id", "12");

        try {
            Map<String, String> resp = wxpay.unifiedOrder(data);
            log.info("map={}",resp);
            String xml = WXPayUtil.mapToXml(resp);
            log.info("xml={}",xml);
            System.out.println(resp.get("code_url"));
            QRCodeUtil.encodeQRCode(resp.get("code_url"),"d:\\qrcode.png");

        } catch (Exception e) {
            log.error("unifiedOrder",e);
        }
    }
    public static void main(String[] args) throws Exception {
        WXPayExample wxPayExample = new WXPayExample();
        wxPayExample.unifiedOrder();
        wxPayExample.orderQuery();
    }


}
