package com.waffle.shattlebus.backend.Controller;


import com.waffle.shattlebus.backend.Model.Feedback;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static com.waffle.shattlebus.backend.keys.*;


@RestController
@RequestMapping("/api/v1")
public class PostAPIController {
    static int emailId = 0;

    @PostMapping(path = "/feedback")
    public Feedback postRequest(@RequestBody Feedback feedback) {
        gmailSend(feedback.getContent());
        return feedback;
    }

    private void gmailSend(String content) {
        String user = getEmailId;
        String password = getEmailPw;
        // SMTP 서버 정보를 설정한다.
        Properties prop = new Properties();
	    prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
       // prop.put("mail.smtp.host", "smtp.gmail.com");
       // prop.put("mail.smtp.port", 465);
       // prop.put("mail.smtp.auth", "true");
       // prop.put("mail.smtp.ssl.enable", "true");
       // prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(user));

            String id = String.valueOf(emailId++);

            message.setSubject(id);

            message.setText(content);

            // send the message
            Transport.send(message);

        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}

