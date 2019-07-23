package com.email.service;

/**
 * @author: Jerry Yi
 * @date: 2019/7/23 21:52
 * @description:
 */
public interface MailService {
    /***发送String字符内容邮件
     * 收件人，主题，内容
     * @param to
     * @param subject
     * @param content
     * @return
     */
    public String sendSimpleMail(String to, String subject, String content);

    /**
     * 编码实现HTML发送
     * @param to
     * @param subject
     * @param content
     * @return
     */
    public String sendHtmlMail(String to, String subject, String content);

    /**
     * 编码实现发送附件
     * @param to
     * @param subject
     * @param content
     * @param path
     */
    public String sendAttachmentsMail(String to, String subject, String content,String path);
}
