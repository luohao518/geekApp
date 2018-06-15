package xyz.geekweb.stripe;

import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lhao
 */
@Service
public class StripeService {

   public void doPay(int amount, String receiptEmail,String token,String orderId){
       // Set your secret key: remember to change this to your live secret key in production
        // See your keys here: https://dashboard.stripe.com/account/apikeys
       Stripe.apiKey = "sk_test_gHqlPlfQXXcnhj6kjmYPUWur";


       Map<String, Object> params = new HashMap<>();
       params.put("amount", amount);
       params.put("currency", "usd");
       params.put("description", orderId);
       params.put("source", token);
       Map<String, String> metadata = new HashMap<>();
       metadata.put("order_id", orderId);
       params.put("metadata", metadata);
       params.put("receipt_email", receiptEmail);
       try {
           Charge charge = Charge.create(params);
           System.out.println(charge.getStatus());
           //return charge;
       } catch (AuthenticationException e) {
           e.printStackTrace();
       } catch (InvalidRequestException e) {
           e.printStackTrace();
       } catch (APIConnectionException e) {
           e.printStackTrace();
       } catch (CardException e) {
           e.printStackTrace();
       } catch (APIException e) {
           e.printStackTrace();
       }
   }

   public static void main(String[] args){
      new StripeService().doPay(10000,"luohao518@yeah.net","xxxx","111111");
   }
}
