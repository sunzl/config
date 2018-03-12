package com.namibank.df.fl.util;

import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;


/**
 * 邮件发送工具
 *
 * @author Clive
 * @date Jan 2, 2017 9:22:09 PM
 */
public class EmailUtil {

    private static Logger logger = LoggerFactory.getLogger(EmailUtil.class);

    private EmailUtil() {
    }

    /**
     * 发送邮件
     *
     * @param addressee 收件人邮箱地址
     * @param subject   主题
     * @param content   内容
     * @throws Exception
     */
    public static void send(String addressee, String subject, String content) throws Exception {
//        protocol.email.host=smtp.qiye.163.com
//        protocol.email.account=bdca@namibank.com
//                protocol.email.password=Namibank2017
        execute(addressee, subject, content, "smtp.qiye.163.com", "bdca@namibank.com", "Namibank2017");
    }

    public static void main(String[] args) {
        try {
            send("yushiwei@namibank.com", "余世炜测试邮件", "阿凯");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送带附件的邮件
     *
     * @param attachments 附件文件
     * @param recipients  收件人列表
     * @param carbonCopys 抄送人列表
     * @param subject     主题
     * @param content     内容
     * @throws Exception
     */
    public static void sendEmailWithAttachments(List<File> attachments, List<String> recipients,
                                                List<String> carbonCopys, String subject, String content, String host, String account, String password) throws Exception {
        sendBySSL(attachments, recipients, carbonCopys, subject, content, host, account, password);
    }

    private static void execute(String addressee, String subject, String content, String host, String account, String password) throws Exception {

        try {
            Properties props = new Properties();
            // 发信的主机
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.auth", "true");
            Session session = Session.getInstance(props);
            // 调试开关
            session.setDebug(false);
            MimeMessage message = new MimeMessage(session);
            // 给消息对象设置发件人/收件人/主题/发信时间
            InternetAddress from = new InternetAddress(account);
            message.setFrom(from);
            InternetAddress to = new InternetAddress(addressee);
            message.setRecipient(Message.RecipientType.TO, to);
            message.setSubject(subject);
            message.setSentDate(new Date());
            // 给消息对象设置内容
            BodyPart mdp = new MimeBodyPart();// 新建一个存放信件内容的BodyPart对象
            mdp.setContent(content, "text/html;charset=utf-8");// 给BodyPart对象设置内容和格式/编码方式
            Multipart mm = new MimeMultipart();// 新建一个MimeMultipart对象用来存放BodyPart对
            // 象(事实上可以存放多个)
            mm.addBodyPart(mdp);// 将BodyPart加入到MimeMultipart对象中(可以加入多个BodyPart)
            message.setContent(mm);// 把mm作为消息对象的内容
            message.saveChanges();
            Transport transport = session.getTransport("smtp");
            try {
                transport.connect(host, account, password);
            } catch (Exception e1) {
                logger.error("", e1);
                throw new Exception("发送邮件账户与密码不匹配");
            }
            try {
                transport.sendMessage(message, message.getAllRecipients());
            } catch (Exception e2) {
                logger.error("", e2);
                throw new Exception("收件人地址无效");
            }
            transport.close();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        logger.info("邮件发送成功, to:{}, suject:{}, content:{}", addressee, subject, content);
    }

    public void send(List<File> attachments, List<String> recipients, List<String> carbonCopys, String subject,
                     String content,String host, String account, String password) {
        try {
            Assert.isTrue(StringUtils.isNotBlank(host), "邮件发送的email.host未配置");
            Assert.isTrue(StringUtils.isNotBlank(account), "邮件发送的email.account未配置");
            Assert.isTrue(StringUtils.isNotBlank(password), "邮件发送的email.password未配置");

            Assert.notEmpty(recipients, "收件人不能为空");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (StringUtils.isBlank(subject)) {
                subject = "每日对账文件_" + sdf.format(DateUtil.addDays(new Date(), -1));
            }
            Properties props = new Properties();
            // 发信的主机
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.auth", "true");
            Session session = Session.getInstance(props);
            // 调试开关
            session.setDebug(false);
            MimeMessage message = new MimeMessage(session);

            // 给消息对象设置发件人/收件人/主题/发信时间
            InternetAddress from = new InternetAddress(account);
            message.setFrom(from);

            // 收件人
            for (String recipient : recipients) {
                if (StringUtils.isNotBlank(recipient)) {
                    InternetAddress to = new InternetAddress(recipient);
                    message.addRecipient(Message.RecipientType.TO, to);
                }
            }

            // 抄送
            if (carbonCopys != null) {
                for (String carbonCopy : carbonCopys) {
                    if (StringUtils.isNotBlank(carbonCopy)) {
                        message.addRecipient(Message.RecipientType.CC, new InternetAddress(carbonCopy));
                    }
                }
            }

            message.setSubject(subject);
            message.setSentDate(new Date());
            // 给消息对象设置内容
            BodyPart mbp = new MimeBodyPart();// 新建一个存放信件内容的BodyPart对象
            mbp.setContent(content, "text/html;charset=utf-8");// 给BodyPart对象设置内容和格式/编码方式
            Multipart mm = new MimeMultipart();// 新建一个MimeMultipart对象用来存放BodyPart对
            // 象(事实上可以存放多个)
            mm.addBodyPart(mbp);// 将BodyPart加入到MimeMultipart对象中(可以加入多个BodyPart)
            message.setContent(mm);// 把mm作为消息对象的内容

            // 附件
            if (attachments != null && !attachments.isEmpty()) {
                for (File attachment : attachments) {
                    mbp = new MimeBodyPart();
                    DataSource source = new FileDataSource(attachment);
                    mbp.setDataHandler(new DataHandler(source));
                    mbp.setFileName(MimeUtility.encodeWord(attachment.getName()));
                    mm.addBodyPart(mbp);
                }
            }
            message.saveChanges();
            Transport transport = session.getTransport("smtp");
            try {
                transport.connect(host, account, password);
            } catch (Exception e1) {
                logger.error("", e1);
                throw new Exception("发送邮件账户与密码不匹配");
            }
            try {
                transport.sendMessage(message, message.getAllRecipients());
            } catch (Exception e2) {
                logger.error("", e2);
                throw new Exception("收件人地址无效");
            }
            transport.close();
        } catch (Exception e) {
            logger.error("send", e);
        }
    }

    @SuppressWarnings("restriction")
    public static void sendBySSL(List<File> attachments, List<String> recipients, List<String> carbonCopys,
                                 String subject, String content, String host, String account, String password) throws Exception {
        try {

            Assert.isTrue(StringUtils.isNotBlank(host), "邮件发送的email.host未配置");
            Assert.isTrue(StringUtils.isNotBlank(account), "邮件发送的email.account未配置");
            Assert.isTrue(StringUtils.isNotBlank(password), "邮件发送的email.password未配置");

            Assert.notEmpty(recipients, "收件人不能为空");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (StringUtils.isBlank(subject)) {
                subject = "默认_" + sdf.format(DateUtil.addDays(new Date(), -1));
            }
            // 设置SSL连接、邮件环境
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
            Properties props = System.getProperties();
            props.setProperty("mail.smtp.host", host);
            props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.port", "465");
            props.setProperty("mail.smtp.socketFactory.port", "465");
            props.setProperty("mail.smtp.auth", "true");
            //发送邮件的代码中添加证书信任配置
            MailSSLSocketFactory mailSSLSocketFactory = new MailSSLSocketFactory();
            mailSSLSocketFactory.setTrustAllHosts(true);
            props.put("mail.smtp.ssl.trust", "*");
            props.put("mail.smtp.ssl.socketFactory", mailSSLSocketFactory);
            // 建立邮件会话
            Session session = Session.getDefaultInstance(props, new Authenticator() {
                // 身份认证
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(account, password);
                }
            });
            // 建立邮件对象
            MimeMessage message = new MimeMessage(session);
            // 设置邮件的发件人、收件人、主题
            message.setFrom(new InternetAddress(account));

            // 收件人
            for (String recipient : recipients) {
                InternetAddress to = new InternetAddress(recipient);
                message.addRecipient(Message.RecipientType.TO, to);
            }

            // 抄送
            if (carbonCopys != null) {
                for (String carbonCopy : carbonCopys) {
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(carbonCopy));
                }
            }
            message.setSentDate(new Date());
            message.setSubject(subject);

            // 给消息对象设置内容
            BodyPart mbp = new MimeBodyPart();// 新建一个存放信件内容的BodyPart对象
            mbp.setContent(content, "text/html;charset=utf-8");// 给BodyPart对象设置内容和格式/编码方式
            Multipart mm = new MimeMultipart();// 新建一个MimeMultipart对象用来存放BodyPart对
            // 象(事实上可以存放多个)
            mm.addBodyPart(mbp);// 将BodyPart加入到MimeMultipart对象中(可以加入多个BodyPart)
            message.setContent(mm);// 把mm作为消息对象的内容

            // 附件
            if (attachments != null && !attachments.isEmpty()) {
                for (File attachment : attachments) {
                    mbp = new MimeBodyPart();
                    DataSource source = new FileDataSource(attachment);
                    mbp.setDataHandler(new DataHandler(source));
                    mbp.setFileName(MimeUtility.encodeWord(attachment.getName()));
                    mm.addBodyPart(mbp);
                }
            }
            message.saveChanges();
            // 发送邮件
            Transport.send(message);
        } catch (Exception e) {
            logger.error("sendBySSL", e);
            throw e;
        }
    }

}
