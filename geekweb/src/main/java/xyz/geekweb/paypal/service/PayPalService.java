package xyz.geekweb.paypal.service;

import com.paypal.api.payments.DetailedRefund;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import xyz.geekweb.paypal.config.PayPalPaymentIntentEnum;
import xyz.geekweb.paypal.config.PayPalPaymentMethodEnum;

/**
 * @author lhao
 */
public interface PayPalService {

    /**
     * 创建交易
     *
     * @param total
     * @param cancelUrl
     * @param successUrl
     * @param orderNO
     * @param customMsg
     * @return
     * @throws PayPalRESTException
     */
    Payment createPayment(Double total,
                          String cancelUrl,
                          String successUrl,
                          String orderNO,String customMsg) throws PayPalRESTException;

    /**
     * 创建交易
     *
     * @param total
     * @param currency
     * @param method
     * @param intent
     * @param description
     * @param cancelUrl
     * @param successUrl
     * @param orderNO
     * @param customMsg
     * @return
     * @throws PayPalRESTException
     */
    Payment createPayment(
            Double total,
            String currency,
            PayPalPaymentMethodEnum method,
            PayPalPaymentIntentEnum intent,
            String description,
            String cancelUrl,
            String successUrl,
            String orderNO,String customMsg) throws PayPalRESTException;

    /**
     * 退款
     *
     * @param saleId
     * @param amountMoney
     * @return
     * @throws PayPalRESTException
     */
    DetailedRefund reFund(String saleId, String amountMoney) throws PayPalRESTException;

    /**
     * 执行交易
     *
     * @param paymentId
     * @param payerId
     * @return
     * @throws PayPalRESTException
     */
    Payment executePayment(String paymentId, String payerId) throws PayPalRESTException;
}
