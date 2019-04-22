package xyz.geekweb.facebook;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import xyz.geekweb.paypal.service.PayPalService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * @author lhao
 */
@Controller
@RequestMapping("/facebook")
public class FacebookController {

//    public static final String GOOGLEUSERCONTENT_COM = "320502522179-ghvamimn46lr0ode7ocpus9oke104k2f.apps.googleusercontent.com";

    public static final String GOOGLEUSERCONTENT_COM = "320502522179-cpppoag1v7kokag6se35lp96ejtm1aig.apps.googleusercontent.com";


    private Logger logger = LoggerFactory.getLogger(FacebookController.class);

    @Autowired
    private PayPalService payPalService;

    @GetMapping("login")
    public String login() {
        logger.debug("do login()");
        return "facebook/login";
    }

    @RequestMapping(value = "googleVerify", method = RequestMethod.POST)
    public String verifyToken(String idtokenstr) {

        System.out.println(idtokenstr);
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(GOOGLEUSERCONTENT_COM)).build();
        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(idtokenstr);
        } catch (GeneralSecurityException e) {
            System.out.println("验证时出现GeneralSecurityException异常");
        } catch (IOException e) {
            System.err.println(e);
            System.out.println("验证时出现IOException异常");
        }
        if (idToken != null) {
            System.out.println("验证成功.");
            GoogleIdToken.Payload payload = idToken.getPayload();
            String userId = payload.getSubject();
			System.out.println("User ID: " + userId);
			String email = payload.getEmail();
        } else {
            System.out.println("Invalid ID token.");
        }

        return "index";
    }


}
