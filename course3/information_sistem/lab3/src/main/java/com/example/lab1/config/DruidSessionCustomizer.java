package com.example.lab1.config;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;

import javax.sql.DataSource;
import java.util.logging.Logger;

//SessionCustomizer для EclipseLink, который настраивает использование Druid DataSource
public class DruidSessionCustomizer implements SessionCustomizer {

    private static final Logger logger = Logger.getLogger(DruidSessionCustomizer.class.getName());
    
    private static DataSource druidDataSource;

    public static void setDataSource(DataSource dataSource) {
        druidDataSource = dataSource;
    }

    @Override
    public void customize(Session session) throws Exception {
        if (druidDataSource != null) {
            DatabaseLogin login = (DatabaseLogin) session.getDatasourceLogin();
            // Используем Connector для установки DataSource
            login.setConnector(new org.eclipse.persistence.sessions.Connector() {
                @Override
                public Object clone() {
                    return this;
                }
                
                @Override
                public java.sql.Connection connect(java.util.Properties properties, Session session) {
                    try {
                        return druidDataSource.getConnection();
                    } catch (java.sql.SQLException e) {
                        throw new RuntimeException("Failed to get connection from Druid DataSource", e);
                    }
                }
                
                @Override
                public void toString(java.io.PrintWriter writer) {
                    writer.print("DruidDataSourceConnector");
                }
                
                @Override
                public String getConnectionDetails() {
                    return "Druid DataSource Connection";
                }
            });
            logger.info("Druid DataSource configured in EclipseLink session");
        } else {
            logger.warning("Druid DataSource is null, using default JDBC connection");
        }
    }
}

