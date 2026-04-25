package com.computershop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Database configuration for distributed mode only.
 * Single mode uses Spring Boot auto-configuration (no custom beans needed).
 *
 * Distributed mode (2 databases):
 * - DB1 (primary): Users, Products, Categories, Images, Roles
 * - DB2 (orders): Orders, OrderDetails, Carts, CartItems, PaymentTransactions
 *
 * Activate with: --spring.profiles.active=distributed
 */
@Configuration
@Profile("distributed")
@EnableTransactionManagement
public class DatabaseConfig {

    @Value("${spring.datasource.primary.url}")
    private String primaryDbUrl;

    @Value("${spring.datasource.primary.username}")
    private String primaryDbUsername;

    @Value("${spring.datasource.primary.password}")
    private String primaryDbPassword;

    @Value("${spring.datasource.orders.url}")
    private String ordersDbUrl;

    @Value("${spring.datasource.orders.username}")
    private String ordersDbUsername;

    @Value("${spring.datasource.orders.password}")
    private String ordersDbPassword;

    @Value("${spring.jpa.hibernate.ddl-auto:update}")
    private String hibernateDdlAuto;

    @Value("${spring.jpa.show-sql:false}")
    private boolean showSql;

    /**
     * Primary datasource for DB1 (Users, Products, Categories).
     */
    @Bean(name = "primaryDataSource")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create()
                .url(primaryDbUrl)
                .username(primaryDbUsername)
                .password(primaryDbPassword)
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    /**
     * Orders datasource for DB2 (Orders, Carts).
     */
    @Bean(name = "ordersDataSource")
    public DataSource ordersDataSource() {
        return DataSourceBuilder.create()
                .url(ordersDbUrl)
                .username(ordersDbUsername)
                .password(ordersDbPassword)
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    /**
     * Primary entity manager factory for DB1.
     */
    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(primaryDataSource());
        em.setPackagesToScan("com.computershop.main.entities");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(showSql);
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());

        return em;
    }

    /**
     * Orders entity manager factory for DB2.
     */
    @Bean(name = "ordersEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean ordersEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(ordersDataSource());
        em.setPackagesToScan("com.computershop.main.entities");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(showSql);
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());

        return em;
    }

    /**
     * Primary transaction manager.
     */
    @Bean(name = "transactionManager")
    public PlatformTransactionManager primaryTransactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(primaryEntityManagerFactory().getObject());
        return tm;
    }

    /**
     * Orders transaction manager.
     */
    @Bean(name = "ordersTransactionManager")
    public PlatformTransactionManager ordersTransactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(ordersEntityManagerFactory().getObject());
        return tm;
    }

    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.hbm2ddl.auto", hibernateDdlAuto);
        props.setProperty("hibernate.show_sql", String.valueOf(showSql));
        return props;
    }
}
