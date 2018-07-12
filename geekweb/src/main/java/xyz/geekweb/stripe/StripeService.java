package xyz.geekweb.stripe;

import com.stripe.model.Charge;

public interface StripeService {

    /**
     * do pay
     *
     * @param amount
     * @param orderId
     * @param receiptEmail
     * @param token
     * @return
     */
    Charge doPay(long amount, String orderId, String receiptEmail, String token);
}
