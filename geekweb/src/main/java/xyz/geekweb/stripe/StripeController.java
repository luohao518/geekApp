package xyz.geekweb.stripe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.geekweb.util.URLUtils;

import javax.servlet.http.HttpServletRequest;

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
        String cancelUrl = URLUtils.getBaseURl(request) + "/paypal/cancel";
        String successUrl = URLUtils.getBaseURl(request) + "/paypal/success";
        String token = request.getParameter("stripeToken");
        System.out.println(token);
        logger.debug("do pay() start");
        stripeService.doPay(
                6600,"luohao518@yeah.net",token,"ordreId:11111111");
        logger.debug("do pay() end");


        return "redirect:/";
    }

    @GetMapping("cancel")
    public String cancelPay() {
        logger.debug("do cancel");
        return "paypal/cancel";

    }
/*
    @GetMapping("success")
    public ResponseData successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            logger.debug("do success");
            Payment payment = stripeService.executePayment(paymentId, payerId);
            logger.debug("do execute finished!!![{}]", payment.toJSON());
            if (payment.getState().equals("approved")) {
                return new ResponseData(ExceptionMsg.SUCCESS);
            } else {
                return new ResponseData(ExceptionMsg.FAILED);
            }
        } catch (StripeRESTException e) {
            logger.error("successPay", e);
            return new ResponseData(ExceptionMsg.FAILED, e.getMessage());
        }
    }

    @GetMapping("refund")
    public @ResponseBody
    ResponseData reFund(String saleId, String amountMoney) {
        logger.debug("do refund");

        DetailedRefund detailedRefund = null;
        try {
            detailedRefund = stripeService.reFund(saleId, amountMoney);
            return new ResponseData(ExceptionMsg.SUCCESS, detailedRefund.toJSON());
        } catch (StripeRESTException e) {
            logger.error("reFund", e);
            return new ResponseData(ExceptionMsg.FAILED, e.getMessage());
        }


    }*/
}
