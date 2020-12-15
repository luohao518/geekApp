package xyz.geekweb.wxpay;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付-签名生成
 * @author jack.luo
 * @date 2020/12/15
 */
public class WXPayCreateSignatureExample {

    public static void main(String[] args) throws Exception {
        MyConfig myConfig = new MyConfig();
        String strNonce = WXPayUtil.generateNonceStr();
        Map<String, String> map = new HashMap<>();
        map.put("mch_id", myConfig.getMchID());
        map.put("nonce_str", strNonce);
        //微信支付API秘钥
        String signature = WXPayUtil.generateSignature(map, "hZT598UEEBqMCkUBej9yJzQcG9tEMm4Z");
        System.out.println(strNonce);
        System.out.println(signature);

    }

}
