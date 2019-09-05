package com.modcreater.tmutils;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/8/27 10:16
 */
public class SendMsgUtil {
    // 设置服务器
    private static String KEY_SMTP = "mail.host";
    private static String VALUE_SMTP = "smtp.qq.com";

    private static String SMTP_PORT_KEY = "mail.smtp.port";
    private static String SMTP_PORT_VALUE = "465";
    // 服务器验证
    private static String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static String VALUE_AUTH = "true";
    // 发件人用户名、密码
    private static String SEND_USER = "306878615@qq.com";
    private static String SEND_UNAME = "306878615";
    private static String SEND_PWD = "facdjmtfuwnqbgda";
    // 建立会话
    private static MimeMessage message;
    private static Session s;
    //核心线程数为1，最大线程数为10,超时时间为5秒
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 10, 5, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    /*
     * 初始化方法
     */
    static {
        Properties props = System.getProperties();
        props.setProperty(KEY_SMTP, VALUE_SMTP);
        props.setProperty(MAIL_SMTP_AUTH, VALUE_AUTH);
        props.setProperty(SMTP_PORT_KEY,SMTP_PORT_VALUE );
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", SMTP_PORT_VALUE);
//        props.setProperty("mail.debug", "true");
        s =  Session.getDefaultInstance(props, new Authenticator(){
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SEND_UNAME, SEND_PWD);
            }});
        s.setDebug(true);
        message = new MimeMessage(s);
    }

    /**
     * 同步发送邮件
     *
     * @param title
     *            邮件头文件名
     * @param content
     *            邮件内容
     * @param receiveEmail
     *            收件人地址
     */
    public static void sendEmail(String title, String content,String receiveEmail) {
        System.out.println("邮箱："+receiveEmail);
        try {
            // 发件人
            InternetAddress from = new InternetAddress(SEND_USER);
            message.setFrom(from);
            // 收件人
            InternetAddress to = new InternetAddress(receiveEmail);
            message.setRecipient(Message.RecipientType.TO, to);
            // 邮件标题
            message.setSubject(title);
            // 邮件内容,也可以使纯文本"text/plain"
            message.setContent(content, "text/html;charset=GBK");
            message.saveChanges();
            Transport transport = s.getTransport("smtp");
            // smtp验证，就是你用来发邮件的邮箱用户名密码
            transport.connect(VALUE_SMTP, SEND_UNAME, SEND_PWD);
            // 发送
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("send success!");
        } catch (AddressException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    public static void asynSendEmail(String title, String content, List<String> emails){
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                for (String receiveEmail:emails) {
                    sendEmail(title,content,receiveEmail);
                }
            }
        };
        executor.execute(runnable);
    }
}
