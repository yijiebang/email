package com.email.controler;

import com.email.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;

/**
 * @author: Jerry Yi
 * @date: 2019/7/23 21:57
 * @description:
 */
@RestController
@RequestMapping("/mail")
public class MailController {
    private static final Logger logger = LoggerFactory.getLogger(MailController.class);

    @Autowired
    MailService mailService;

    //使用模板，发送邮件
    @Autowired
    private TemplateEngine templateEngine;

    @PostMapping(value = "/test")
    @ResponseBody
    public String mail(@RequestParam("to") String to,@RequestParam("subject") String subject,@RequestParam("content") String content){
        logger.debug("收件人"+to);
        logger.debug("标题"+subject);
        logger.debug("内容"+content);
       return mailService.sendSimpleMail(to, subject, content);
    }

    @PostMapping(value = "/temp")
    @ResponseBody
    public void tempMail(@RequestParam("to") String to,@RequestParam("subject") String subject,@RequestParam("content") String content){
        logger.debug("收件人"+to);
        logger.debug("标题"+subject);
        logger.debug("内容"+content);

        Context contexts = new Context();
        contexts.setVariable("id","110");
        String contents = templateEngine.process("index",contexts);
        mailService.sendHtmlMail(to,subject,contents);
    }


    @RequestMapping("/upload")
    @ResponseBody
    public String handleFileUpload(@RequestParam("file")MultipartFile file){
        if(!file.isEmpty()){
            try {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(file.getOriginalFilename())));
                out.write(file.getBytes());
                out.flush();
                out.close();
            }catch(FileNotFoundException e) {
                e.printStackTrace();
                return "上传失败,"+e.getMessage();
            }catch (IOException e) {
                e.printStackTrace();
                return "上传失败,"+e.getMessage();
            }

            return "上传成功";

        }else{

            return "上传失败，因为文件是空的.";

        }
    }

    @Value("${mail.loca.file.path}")
    private String loca_file_path;

    @PostMapping(value = "/file")
    @ResponseBody
    public String fileMail(@RequestParam("to") String to,@RequestParam("subject") String subject,@RequestParam("content") String content,@RequestParam(value = "file")MultipartFile file){
        logger.debug("收件人"+to);
        logger.debug("标题"+subject);
        logger.debug("内容"+content);

        String fileName = file.getOriginalFilename();
        File uploadDir = new File(loca_file_path);
        // 创建一个目录 （它的路径名由当前 File 对象指定，包括任一必须的父路径。）
        if (!uploadDir.exists())
            uploadDir.mkdirs();
        String path = loca_file_path + fileName;
        // 新建一个文件
        File tempFile = new File(path);
        try {
            // 将上传的文件写入新建的文件中
            file.transferTo(tempFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return   mailService.sendAttachmentsMail(to,subject,content,path);
    }

}
