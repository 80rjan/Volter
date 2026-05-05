package com.volter.shared.config;

import com.volter.shared.multitenancy.SchemaConnectionProvider;
import com.volter.shared.multitenancy.TenantIdentifierResolver;
import org.hibernate.cfg.Environment;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
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
        em.setJpaPropertyMap(props);

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            LocalContainerEntityManagerFactoryBean emf) {
        return new JpaTransactionManager(emf.getObject());
    }
}
