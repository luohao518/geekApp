package xyz.geekweb.stripe.impl;

import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import xyz.geekweb.stripe.StripeService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lhao
 */
@Service
public class StripeServiceImpl implements StripeService {

    public static final String API_KEY = "sk_test_ZMAOBVK5MXbLjYY894Mhr29i";

    private static Logger logger = LoggerFactory.getLogger(StripeServiceImpl.class);

    public static void main(String[] args) throws Exception {
//        new StripeServiceImpl().doPay(10000, "luohao518@yeah.net", "xxxx", "111111");
        new StripeServiceImpl().refund("ch_1DTgnGLiluVmKKa3ZZL84nXS",14);
    }

    /**
     * do pay
     *
     * @param amount
     * @param orderId
     * @param receiptEmail
     * @param token
     * @return
     */
    @Override
    public Charge doPay(long amount, String orderId, String receiptEmail, String token) {
        Stripe.apiKey = API_KEY;

        Map<String, Object> params = new HashMap<>(5);
        params.put("amount", amount);
        params.put("currency", "usd");
        params.put("description", "import-express.com");
        params.put("source", token);

        Map<String, String> metadata = new HashMap<>(3);
        metadata.put("order_id", orderId);

        params.put("metadata", metadata);
        params.put("receipt_email", receiptEmail);
        try {
            Charge charge = Charge.create(params);
            logger.debug("charge=[{}]", charge.toJson());
            logger.info("charge.Status=[{}]", charge.getStatus());
            return charge;
        } catch (AuthenticationException e) {
            logger.error("AuthenticationException:", e);
            throw new RuntimeException(e);
        } catch (InvalidRequestException e) {
            logger.error("InvalidRequestException:", e);
            throw new RuntimeException(e);
        } catch (APIConnectionException e) {
            logger.error("APIConnectionException:", e);
            throw new RuntimeException(e);
        } catch (CardException e) {
            logger.error("CardException:", e);
            throw new RuntimeException(e);
        } catch (APIException e) {
            logger.error("APIException:", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Refund refund(String chargeId, long amount) throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {

        Stripe.apiKey = API_KEY;

        Map<String, Object> params = new HashMap<>();
        params.put("charge", chargeId);
        params.put("amount", amount);
        Refund refund = Refund.create(params);
        logger.info(refund.toJson());
        if("succeeded".equals(refund.getStatus())){
            logger.info("refund succeeded.");
            return refund;
        }else{
            throw new IllegalStateException("refund faild");
        }
    }
}
