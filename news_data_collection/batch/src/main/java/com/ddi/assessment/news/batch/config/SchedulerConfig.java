package com.ddi.assessment.news.batch.config;

import org.quartz.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class SchedulerConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(new AutoWiringSpringBeanJobFactory());
        factory.setWaitForJobsToCompleteOnShutdown(true);
        factory.setOverwriteExistingJobs(true);

        return factory;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factoryBean) {
        return factoryBean.getScheduler();
    }
}
