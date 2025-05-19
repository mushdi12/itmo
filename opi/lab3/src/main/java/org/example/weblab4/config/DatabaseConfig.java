package org.example.weblab4.config;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    public DSLContext dslContext(DataSource dataSource) {
        DefaultConfiguration config = new DefaultConfiguration();
        config.setDataSource(dataSource);
        config.setSQLDialect(SQLDialect.POSTGRES);
        return DSL.using(config);
    }
} 