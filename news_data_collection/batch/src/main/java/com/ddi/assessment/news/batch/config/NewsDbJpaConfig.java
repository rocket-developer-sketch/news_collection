package com.ddi.assessment.news.batch.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
        basePackages = "com.ddi.assessment.news.domain", // 뉴스 도메인 패키지
        entityManagerFactoryRef = "newsEntityManagerFactory",
        transactionManagerRef = "newsTransactionManager"
)
public class NewsDbJpaConfig {

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
        jpaProperties.put("hibernate.hbm2ddl.auto", "none");
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        emf.setJpaPropertyMap(jpaProperties);

        return emf;
    }

    @Bean(name = "newsTransactionManager")
    public PlatformTransactionManager newsTransactionManager(
            @Qualifier("newsEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    @ConfigurationProperties("spring.datasource.news")
    public DataSourceProperties newsDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean(name = "newsDataSource")
    @ConfigurationProperties("spring.datasource.news.hikari")
    public HikariDataSource newsDataSource() {
        return newsDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

}
