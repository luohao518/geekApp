package xyz.geekweb.stripe;

import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.net.APIResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.geekweb.util.URLUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lhao
 */
@Controller
@RequestMapping("/stripe")
public class StripeController {

    private Logger logger = LoggerFactory.getLogger(StripeController.class);

    @Autowired
    private StripeService stripeService;

    @GetMapping("")
    public String index() {
        logger.debug("do index()");
        return "stripe/index";
    }

    @GetMapping("alipay")
    public String index2() {
        logger.debug("do index()");
        return "stripe/index-alipay";
    }

    @PostMapping("pay")
    public String pay(HttpServletRequest request) {
        String cancelUrl = URLUtils.getBaseURl(request) + "/stripe/cancel";
        String successUrl = URLUtils.getBaseURl(request) + "/stripe/success";
        String token = request.getParameter("stripeToken");
        System.out.println(token);
        logger.debug("do pay() start");
        Charge charge = stripeService.doPay(
                6600, "11111111", "luohao518@yeah.net", token);
        if ("succeeded".equals(charge.getStatus())) {
            return "redirect:" + successUrl;
        } else {
            return "redirect:" + cancelUrl;
        }
    }

    @GetMapping("cancel")
    public String cancelPay() {
        logger.debug("do cancel");
        return "paypal/cancel";

    }

    @GetMapping("success")
    public String successPay() {
        logger.debug("do success");
        return "paypal/success";

    }

    @GetMapping("webhook")
    public void webhook(@RequestBody String body, HttpServletResponse response) {
        // Retrieve the request's body and parse it as JSON:
        logger.info("RequestBody[{}]", body);
        Event eventJson = APIResource.GSON.fromJson(body, Event.class);

        logger.info("eventJson[{}]", eventJson);
        // Do something with eventJson

        response.setStatus(200);
    }
}
