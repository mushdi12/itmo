package com.example.lab1.config;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.util.logging.Logger;

// Инициализатор, который устанавливает Druid DataSource в SessionCustomizer
@ApplicationScoped
public class DruidDataSourceInitializer {

    private static final Logger logger = Logger.getLogger(DruidDataSourceInitializer.class.getName());
    
    @Inject
    private DruidDataSourceProvider druidDataSourceProvider;

    @PostConstruct
    public void init() {
        DataSource dataSource = druidDataSourceProvider.getDataSource();
        DruidSessionCustomizer.setDataSource(dataSource);
        logger.info("Druid DataSource set in SessionCustomizer");
    }
}

