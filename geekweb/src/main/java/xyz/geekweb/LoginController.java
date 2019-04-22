//package xyz.geekweb;
//
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import xyz.geekweb.calcurepayment.CalcuRepaymentService;
//import xyz.geekweb.calcurepayment.model.Detail;
//import xyz.geekweb.calcurepayment.model.FormData;
//import xyz.geekweb.calcurepayment.model.RepaymentScheduleData;
//
//import javax.servlet.http.HttpSession;
//import javax.validation.Valid;
//import java.io.UnsupportedEncodingException;
//import java.util.List;
//
//@Controller
//@RequestMapping("/login")
//@Slf4j
//public class LoginController {
//
//
//    @Autowired
//    public LoginController() {
//
//    }
//
//    @GetMapping
//    public String doGet() {
//        log.info("do get()");
//
//
//        return "login";
//    }
//
//
//}
