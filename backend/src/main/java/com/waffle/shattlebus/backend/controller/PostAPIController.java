package com.waffle.shattlebus.backend.controller;


import com.waffle.shattlebus.backend.model.Feedback;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@RestController
@RequestMapping("/api/v1")
public class PostAPIController {
    static int emailId = 0;

    @PostMapping(path = "/feedback/")
    public Feedback postRequest(@RequestBody Feedback feedback) {
        gmailSend(feedback.getContent());
        return feedback;
    }

    private void gmailSend(String content) {

        String user = "waffleshattlebus@gmail.com"; // 네이버일 경우 네이버 계정, gmail경우 gmail 계정
        String password = "shattlebus";   // 패스워드

        // SMTP 서버 정보를 설정한다.
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", 465);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress("waffleshattlebus@gmail.com"));

            String id = String.valueOf(emailId++);

            message.setSubject(id);

            message.setText(content);

            // send the message
            Transport.send(message);

        } catch (AddressException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}

