package xyz.geekweb.wxpay;

import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付-沙箱key生成
 * @author jack.luo
 * @date 2020/12/15
 */
public class WXPayCreateSandboxKeyExample {

    /**
     * 沙箱key生成
     * @return
     */
    public String getSignKey() {
        URI uri;
        try {
            //获取仿真测试环境验签秘钥API
            uri = new URI("https://api.mch.weixin.qq.com/sandboxnew/pay/getsignkey");
            HttpHeaders headers = new HttpHeaders();
            //媒体类型`application/xml
            headers.setContentType(MediaType.APPLICATION_XML);
            MyConfig myConfig = new MyConfig();
            Map<String, String> map = new HashMap<String, String>();
            //商户号
            map.put("mch_id", myConfig.getMchID());
            //随机字符串
            map.put("nonce_str", WXPayUtil.generateNonceStr());
            //用生产环境的KEY对mch_id、nonce_str 请求参数签名
            map.put("sign", WXPayUtil.generateSignature(map, myConfig.getKey()));
            //其实请求的是xml格式，非key-value格式
            HttpEntity<String> entity = new HttpEntity<String>(WXPayUtil.mapToXml(map), headers);
            RestTemplate rt = new RestTemplate();
            String responseXML = rt.postForObject(uri, entity, String.class);
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(responseXML);
            String sandbox_signkey = xmlToMap.get("sandbox_signkey");
            //这个就是沙箱签名key
            return sandbox_signkey;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new WXPayCreateSandboxKeyExample().getSignKey());

    }

}
