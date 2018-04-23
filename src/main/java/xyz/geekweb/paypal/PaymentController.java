package xyz.geekweb.paypal;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.geekweb.paypal.config.PayPalPaymentIntentEnum;
import xyz.geekweb.paypal.config.PayPalPaymentMethodEnum;
import xyz.geekweb.util.URLUtils;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/paypal")
public class PaymentController {

    private Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PayPalService paypalService;

    @GetMapping("")
    public String index() {
        logger.debug("do index()");
        return "paypal/index";
    }

    @PostMapping("pay")
    public String pay(HttpServletRequest request) {
        String cancelUrl = URLUtils.getBaseURl(request) + "/paypal/cancel";
        String successUrl = URLUtils.getBaseURl(request) + "/paypal/success";
        try {
            logger.debug("do pay() start");
            Payment payment = paypalService.createPayment(
                    50.00,
                    "USD",
                    PayPalPaymentMethodEnum.paypal,
                    PayPalPaymentIntentEnum.sale,
                    "payment description",
                    cancelUrl,
                    successUrl);
            logger.debug("do pay() end");
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return "redirect:" + links.getHref();
                }
            }
        } catch (PayPalRESTException e) {
            logger.error("pay error",e);
        }
        return "redirect:/";
    }

    @GetMapping("cancel")
    public String cancelPay() {
        logger.debug("do cancel");
        return "paypal/cancel";
    }

    @GetMapping("success")
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            logger.debug("do success");
            Payment payment = paypalService.executePayment(paymentId, payerId);
            logger.debug("do execute finished!!![{}]", payment.toJSON());
            if (payment.getState().equals("approved")) {
                return "paypal/success";
            }
        } catch (PayPalRESTException e) {
            logger.error(e.getMessage());
        }
        return "redirect:/";
    }

}
