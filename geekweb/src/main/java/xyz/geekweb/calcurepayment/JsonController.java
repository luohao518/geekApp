package xyz.geekweb.calcurepayment;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.geekweb.calcurepayment.model.Detail;
import xyz.geekweb.calcurepayment.model.RepaymentScheduleData;

import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * @author lhao
 */
@RestController
public class JsonController {

    @SuppressWarnings("unchecked")
    @RequestMapping("/download")
    public RepaymentScheduleData<List<Detail>> repayment(HttpSession session) {

        return (RepaymentScheduleData<List<Detail>>) session.getAttribute("result");
    }
}
