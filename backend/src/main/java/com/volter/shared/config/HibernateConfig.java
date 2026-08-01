package com.volter.shared.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.volter.shared.multitenancy.SchemaConnectionProvider;
import com.volter.shared.multitenancy.TenantIdentifierResolver;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
import org.hibernate.type.format.jackson.JacksonJsonFormatMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.volter",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class HibernateConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            SchemaConnectionProvider connectionProvider,
            TenantIdentifierResolver tenantResolver) {

        LocalContainerEntityManagerFactoryBean em =
                new LocalContainerEntityManagerFactoryBean();

        em.setDataSource(dataSource);
        em.setPackagesToScan("com.volter");           // scans all entities across contexts
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> props = new HashMap<>();
        props.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
        props.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);
        props.put(Environment.HBM2DDL_AUTO, "none");
        props.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        props.put(Environment.PHYSICAL_NAMING_STRATEGY, PhysicalNamingStrategySnakeCaseImpl.class.getName());
        // jsonb columns (e.g. the report payload) may hold java.time values such as session
        // dates. Hibernate's default JSON mapper uses a bare ObjectMapper that can't serialize
        // those, so give it one with the JavaTime module (dates as ISO strings, not arrays).
        // Built here rather than injecting the Spring ObjectMapper bean, which isn't yet
        // available when this JPA config initializes.
        ObjectMapper jsonbMapper = new ObjectMapper()
                .findAndRegisterModules()  // picks up the JavaTime module (jsr310) from the runtime classpath
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        props.put(AvailableSettings.JSON_FORMAT_MAPPER, new JacksonJsonFormatMapper(jsonbMapper));
        em.setJpaPropertyMap(props);

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            LocalContainerEntityManagerFactoryBean emf) {
        return new JpaTransactionManager(emf.getObject());
    }
}
