package com.api.xpress.notification.mail.dto;

import lombok.*;

@Builder
public record MailInfo (

    String name,

    String email
){}
