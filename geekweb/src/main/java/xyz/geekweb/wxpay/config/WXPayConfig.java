package xyz.geekweb.wxpay.config;

import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import xyz.geekweb.wxpay.MyConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jack.luo
 * @date 2020/12/15
 */
@Configuration
public class WXPayConfig {

    @Value("${wxpay.sanbox}")
    public boolean sanbox;

    @Value("${wxpay.appid}")
    public String appID;

    @Value("${wxpay.mchid}")
    public String mchID;

    @Value("${wxpay.key}")
    public String key;

}
