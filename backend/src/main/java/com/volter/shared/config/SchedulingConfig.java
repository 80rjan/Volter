package com.volter.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables Spring's {@code @Scheduled} support (e.g. the monthly report generator).
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
