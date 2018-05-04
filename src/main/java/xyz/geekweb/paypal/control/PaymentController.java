package xyz.geekweb.paypal.control;

import com.paypal.api.payments.DetailedRefund;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
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

/**
 * @author lhao
 */
@Controller
@RequestMapping("/paypal")
public class PaymentController {

    private Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PayPalService payPalService;

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
            Payment payment = payPalService.createPayment(
                    50.00,
                    cancelUrl,
                    successUrl,"O1111111");
            logger.debug("do pay() end");
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return "redirect:" + links.getHref();
                }
            }
        } catch (PayPalRESTException e) {
            logger.error("pay error", e);
        }
        return "redirect:/";
    }

    @GetMapping("cancel")
    public String cancelPay() {
        logger.debug("do cancel");
        return "paypal/cancel";

    }

    @GetMapping("success")
    public ResponseData successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            logger.debug("do success");
            Payment payment = payPalService.executePayment(paymentId, payerId);
            logger.debug("do execute finished!!![{}]", payment.toJSON());
            if (payment.getState().equals("approved")) {
                return new ResponseData(ExceptionMsg.SUCCESS);
            } else {
                return new ResponseData(ExceptionMsg.FAILED);
            }
        } catch (PayPalRESTException e) {
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
            detailedRefund = payPalService.reFund(saleId, amountMoney);
            return new ResponseData(ExceptionMsg.SUCCESS, detailedRefund.toJSON());
        } catch (PayPalRESTException e) {
            logger.error("reFund", e);
            return new ResponseData(ExceptionMsg.FAILED, e.getMessage());
        }


    }
}
