package com.api.xpress.notification.mail;

import com.api.xpress.notification.mail.dto.EmailRequest;

public interface MailService {
    // String sendMail(EmailRequest emailRequest);
    void sendMail(EmailRequest request);

}

