package com.email.service.impl;

import com.email.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @author: Jerry Yi
 * @date: 2019/7/23 21:52
 * @description:
 */
@Service
public class MailServiceImpl implements MailService {
    private final Logger logger = (Logger)  LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.fromMail.addr}")
    private String from;

    @Override
    public String sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        try {
            mailSender.send(message);
            logger.info("简单邮件已经发送。");
        }catch (Exception e) {
            logger.error("发送简单邮件时发生异常！", e);
            return "error";
        }
        return "success";
    }

    //HTML的发送需要借助MimeMessage，
    // MimeMessageHelper的setTest方法提供是否开启html的重装方法。
    @Override
    public String sendHtmlMail(String to, String subject, String content) {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);//是否开启html的重装方法

            mailSender.send(message);
            System.out.println("发送成功~");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("发送失败~");
            return "error";
        }
        return "success";
    }

   //可以通过多个addAttachment方法发送多个附件,
   // File.separator是用来分隔同一个路径字符串中的目录
    @Override
    public String sendAttachmentsMail(String to, String subject, String content, String path) {
                MimeMessage message = mailSender.createMimeMessage();
            try {
                MimeMessageHelper helper = new MimeMessageHelper(message,true);
                helper.setFrom(from);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(content, true);
                FileSystemResource fileSystemResource = new FileSystemResource(new File(path));

                String fileName= path.substring(path.lastIndexOf(File.separator));
                helper.addAttachment(fileName,fileSystemResource);

                mailSender.send(message);
                System.out.println("发送成功！");
                return "success";
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("发送失败！");
                return "error";
            }
    }

}
