package com.goaleaf.accounts;

import com.github.pplociennik.commons.audit.AuditAwareImpl;
import com.github.pplociennik.modinfo.config.ModuleInfoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Instant;

@SpringBootApplication
@EnableJpaAuditing( auditorAwareRef = "auditAwareImpl" )
@Import( value = { ModuleInfoConfiguration.class } )
public class GlfAccountsApplication {

    public static void main( String[] args ) {
        SpringApplication.run( GlfAccountsApplication.class, args );
    }

    @Bean
    AuditorAware< Instant > auditAwareImpl() {
        return new AuditAwareImpl();
    }

}
