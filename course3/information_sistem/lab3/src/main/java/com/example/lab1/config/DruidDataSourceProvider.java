package com.example.lab1.config;

import com.alibaba.druid.pool.DruidDataSource;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.logging.Logger;

@ApplicationScoped
public class DruidDataSourceProvider {

    private static final Logger logger = Logger.getLogger(DruidDataSourceProvider.class.getName());
    private DruidDataSource dataSource;

    @PostConstruct
    public void init() {
        dataSource = new DruidDataSource();
        
        // Базовые настройки подключения
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5433/studs");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");
        
        // Настройки пула соединений
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(5);
        dataSource.setMaxActive(20);
        dataSource.setMaxWait(60000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        
        try {
            dataSource.setFilters("stat,wall");
        } catch (SQLException e) {
            logger.warning("Failed to set Druid filters: " + e.getMessage());
        }
        
        dataSource.setConnectionProperties("druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000");
        
        logger.info("Druid DataSource initialized");
    }

    @PreDestroy
    public void destroy() {
        if (dataSource != null) {
            dataSource.close();
            logger.info("Druid DataSource closed");
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}

