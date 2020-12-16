package xyz.geekweb.wxpay;

import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;
import com.github.wxpay.sdk.WXPayConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * @author jack.luo
 * @date 2020/12/14
 */
/**
 * @author jack.luo
 * @date 2020/12/14
 */
@Service
public class MyConfig extends WXPayConfig {

    private byte[] certData;

    @Autowired
    private WXPayConfig wxPayConfig;

    public MyConfig() throws Exception {
//        //TODO
//        String certPath = "E:\\lhao\\apiclient_cert.p12";
//        File file = new File(certPath);
//        InputStream certStream = new FileInputStream(file);
//        this.certData = new byte[(int) file.length()];
//        certStream.read(this.certData);
//        certStream.close();
    }

    @Override
    public String getAppID() {

        return wxPayConfig.getAppID();

    }

    @Override
    public String getMchID() {
        return wxPayConfig.getMchID();
    }

    @Override
    public IWXPayDomain getWXPayDomain(){
        IWXPayDomain iwxPayDomain = new IWXPayDomain() {

            public void report(String domain, long elapsedTimeMillis, Exception ex) {

            }

            public DomainInfo getDomain(WXPayConfig config) {
                return new IWXPayDomain.DomainInfo(WXPayConstants.DOMAIN_API, true);
            }
        };
        return iwxPayDomain;
    }


    @Override
    public String getKey() {
        return wxPayConfig.getKey();
        //通过WXPayCreateSignatureExample.java得到
        //return "1f3198769feadbdb6f99f5bf84c94d20";
    }

    @Override
    public InputStream getCertStream() {
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    @Override
    public int getHttpConnectTimeoutMs() {

        return 8000;
    }

    @Override
    public int getHttpReadTimeoutMs() {

        return 10000;
    }
}
