package com.api.xpress.airtime.data.models;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BillerConfig {

    @Value("${biller.unique-code.mtn}")
    private String mtnUniqueCode;

    @Value("${biller.unique-code.glo}")
    private String gloUniqueCode;

    @Value("${biller.unique-code.airtel}")
    private String airtelUniqueCode;

    @Value("${biller.unique-code.9mobile}")
    private String etisalatUniqueCode;

    @PostConstruct
    public void initializeBillerCodes() {
        Biller.MTN.setUniqueCode(mtnUniqueCode);

        Biller.GLO.setUniqueCode(gloUniqueCode);

        Biller.AIRTEL.setUniqueCode(airtelUniqueCode);

        Biller.ETISALAT.setUniqueCode(etisalatUniqueCode);

        log.info("Biller unique codes initialized successfully");
    }
}
