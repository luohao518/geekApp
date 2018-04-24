package xyz.geekweb.util;

/**
 * @author lhao
 */
public interface MailService {

    /**
     * 发送简单邮件
     *
     * @param to
     * @param subject
     * @param content
     */
    void sendSimpleMail(String content);

}
