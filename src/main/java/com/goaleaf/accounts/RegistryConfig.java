package com.goaleaf.accounts;

import com.github.pplociennik.commons.system.registry.CollectingSystemRegistry;
import com.github.pplociennik.commons.system.registry.impl.SynchronizedHashSetBasedSystemRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class responsible for defining system registry beans.
 * This class provides the necessary configuration for collecting and managing system-wide registries.
 *
 * @author Created by: Pplociennik at 27.05.2025 17:32
 */
@Configuration
class RegistryConfig {

    /**
     * Creates and configures a synchronized registry for token validation filters.
     * This bean provides thread-safe storage and management of token validation components.
     *
     * @return A synchronized registry instance for storing String-based token validation filters
     */
    @Bean( name = "tokenValidationFilterRegistry" )
    CollectingSystemRegistry< String > tokenValidationFilterRegistry() {
        return new SynchronizedHashSetBasedSystemRegistry<>();
    }
}
