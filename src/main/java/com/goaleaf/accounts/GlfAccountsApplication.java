package com.goaleaf.accounts;

import com.github.pplociennik.commons.audit.AuditAwareImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing( auditorAwareRef = "auditAwareImpl" )
public class GlfAccountsApplication {

    public static void main( String[] args ) {
        SpringApplication.run( GlfAccountsApplication.class, args );
    }

    @Bean
    AuditorAware< String > auditAwareImpl() {
        return new AuditAwareImpl();
    }

}
