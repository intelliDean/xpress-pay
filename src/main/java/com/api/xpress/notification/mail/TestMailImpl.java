package com.api.xpress.notification.mail;

import com.api.xpress.notification.mail.dto.EmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("dev")
public class TestMailImpl implements MailService {
    @Override
    public void sendMail(EmailRequest request) {
        log.info("Mail sent to: %s".formatted(request.getTo().getFirst().email()));
    }
}
