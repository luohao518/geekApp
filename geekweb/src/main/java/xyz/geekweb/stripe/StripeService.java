package xyz.geekweb.stripe;

import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.stripe.model.Refund;

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

    Refund refund(String chargeId, long amount) throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException;
}
