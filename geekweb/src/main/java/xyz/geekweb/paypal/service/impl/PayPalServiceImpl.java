package xyz.geekweb.paypal.service.impl;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.geekweb.paypal.config.PayPalConfig;
import xyz.geekweb.paypal.config.PayPalPaymentIntentEnum;
import xyz.geekweb.paypal.config.PayPalPaymentMethodEnum;
import xyz.geekweb.paypal.service.PayPalService;

import java.util.*;

/**
 * @author lhao
 */
@Service
public class PayPalServiceImpl implements PayPalService {

    private static Logger logger = LoggerFactory.getLogger(PayPalServiceImpl.class);

    private static String strId =null;

    @Autowired
    private PayPalConfig payPalConfig;

    @Override
    public Payment createPayment(
            Double total,
            String cancelUrl,
            String successUrl,
            String orderNO, String customMsg
    ) throws PayPalRESTException {

        return createPayment(total, "USD", PayPalPaymentMethodEnum.paypal,
                PayPalPaymentIntentEnum.sale,
                "",
                cancelUrl,
                successUrl,
                orderNO, customMsg);

    }

    @Override
    public Payment createPayment(
            Double total,
            String currency,
            PayPalPaymentMethodEnum method,
            PayPalPaymentIntentEnum intent,
            String description,
            String cancelUrl,
            String successUrl,
            String orderNO,
            String customMsg) throws PayPalRESTException {
        logger.info("createPayment():[{}],[{}],[{}],[{}],[{}],[{}],[{}]", total, currency, method, intent, description, cancelUrl, successUrl);

        APIContext apiContext = getApiContext();

        // ###Details
        Details details = new Details();
        details.setShipping("0");
        String strTotal = String.format("%.2f", total);
        details.setSubtotal(strTotal);
        details.setTax("0");

        // ###Amount
        Amount amount = new Amount();
        amount.setCurrency(currency);
        // Total must be equal to sum of shipping, tax and subtotal.
        amount.setTotal(strTotal);
        amount.setDetails(details);

        // ###Transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setCustom(customMsg);
        // ###Transactions
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        // ### Items
        Item item = new Item();
        item.setName(orderNO).setQuantity("1").setCurrency(currency).setPrice(strTotal);
        ItemList itemList = new ItemList();
        List<Item> items = new ArrayList<>();
        items.add(item);
        itemList.setItems(items);

        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setLine1("P.O. Box 7004");
        shippingAddress.setLine2("123 Nowhere Anywhere St., Apt. 542");
        shippingAddress.setCity("Omaha");
        shippingAddress.setState("NE");
        shippingAddress.setPostalCode("68114");
        shippingAddress.setCountryCode("US");

        //itemList.setShippingAddress(shippingAddress);
        transaction.setItemList(itemList);

        // ###Payer
        Payer payer = new Payer();
        PayerInfo payerInfo = new PayerInfo();
        payerInfo.setEmail("janet.doe@nowhere.com");
        payerInfo.setFirstName("Janet M.");
        payerInfo.setLastName("Doe");

        /*Address billingAddress = new Address();
        billingAddress.setLine1("P.O. Box 7004");
        billingAddress.setLine2("123 Nowhere Anywhere St., Apt. 542");
        billingAddress.setCity("Omaha");
        billingAddress.setState("NE");
        billingAddress.setPostalCode("68114");
        billingAddress.setCountryCode("US");*/


        payer.setPayerInfo(payerInfo);
        payer.setPaymentMethod(method.toString());

        // ###Payment
        Payment payment = new Payment();
        payment.setIntent(intent.toString());
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        // ###Redirect URLs
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        payment.setExperienceProfileId(getWebProfile(apiContext));
        return payment.create(apiContext);
    }

    @Override
    public DetailedRefund reFund(String saleId, String amountMoney) throws PayPalRESTException {
        logger.info("reFund():[{}],[{}]", saleId, amountMoney);

        Sale sale = new Sale();
        sale.setId(saleId);

        RefundRequest refund = new RefundRequest();

        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(amountMoney);
        refund.setAmount(amount);
        return sale.refund(getApiContext(), refund);
    }

    @Override
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {

        logger.info("executePayment():[{}],[{}]", paymentId, payerId);

        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);

        return payment.execute(getApiContext(), paymentExecute);

    }

    private APIContext getApiContext() {

        logger.info("getApiContext()");

        Map<String, String> sdkConfig = new HashMap<>(1);
        sdkConfig.put("mode", payPalConfig.mode);
        return new APIContext(payPalConfig.clientId, payPalConfig.clientSecret, payPalConfig.mode, sdkConfig);
    }

    private synchronized static String getWebProfile(APIContext apiContext) throws PayPalRESTException {

        if(strId==null) {
            WebProfile webProfile = new WebProfile();
            InputFields inputField = new InputFields();
            inputField.setNoShipping(1);
            webProfile.setInputFields(inputField);
            String name="WebProfile"+ UUID.randomUUID();
            logger.info("getWebProfile name="+name);
            webProfile.setName(name);
            strId= webProfile.create(apiContext).getId();
        }

        logger.info("webProfile ID:"+strId);
        return strId;
    }
}