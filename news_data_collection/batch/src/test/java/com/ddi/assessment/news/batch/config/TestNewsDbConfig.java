package com.ddi.assessment.news.batch.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@TestConfiguration
@Profile("test")
@EnableJpaRepositories(
        basePackages =  "com.ddi.assessment.news.domain",
        entityManagerFactoryRef = "newsEntityManagerFactory",
        transactionManagerRef = "newsTransactionManager"
)
public class TestNewsDbConfig {

    @Primary
    @Bean(name = "newsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean newsEntityManagerFactory(
            @Qualifier("newsDataSource") DataSource newsDataSource) {

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(newsDataSource);
        emf.setPackagesToScan("com.ddi.assessment.news.domain"); // 실제 엔티티 경로
//        emf.setPackagesToScan(DbEntityScanner.scanPackagesWithAnnotation(NewsDb.class, "com.ddi.assessment.news.batch.domain"));

        emf.setPersistenceUnitName("news");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.hbm2ddl.auto", "create-drop");
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

        emf.setJpaPropertyMap(jpaProperties);

        return emf;
    }

    @Primary
    @Bean(name = "newsTransactionManager")
    public PlatformTransactionManager newsTransactionManager(
            @Qualifier("newsEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
