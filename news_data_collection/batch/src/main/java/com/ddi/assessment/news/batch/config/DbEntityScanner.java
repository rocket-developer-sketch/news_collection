package com.ddi.assessment.news.batch.config;


import jakarta.persistence.Entity;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * 사용 안 함
 * */
public class DbEntityScanner {
    public static  <T extends Annotation>  String[] scanPackagesWithAnnotation(Class<T> annotationClass,
                                                                               String basePackage) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));

        Set<String> packages = new HashSet<>();

        for (var bd : scanner.findCandidateComponents(basePackage)) {
            try {
                Class<?> clazz = Class.forName(bd.getBeanClassName());
                if (clazz.isAnnotationPresent(annotationClass)) {
                    packages.add(clazz.getPackage().getName());
                }
            } catch (ClassNotFoundException ignored) {}
        }

        return packages.toArray(String[]::new);
    }
}
