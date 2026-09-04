package com.sih.procurement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProcurementApplication {
    public static void main(String[] args) {
        // Windows JVMs often report the deprecated "Asia/Calcutta" alias
        // for IST instead of "Asia/Kolkata", which PostgreSQL rejects at
        // connection time. Force the correct IANA name before anything
        // (Hikari/Hibernate) reads the default timezone.
        System.setProperty("user.timezone", "Asia/Kolkata");
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Kolkata"));

        SpringApplication.run(ProcurementApplication.class, args);
    }
}
