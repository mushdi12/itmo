package com.example.lab1.config;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.logging.Logger;


@ApplicationScoped
public class CacheStatisticsConfig {

    private static final Logger logger = Logger.getLogger(CacheStatisticsConfig.class.getName());
    private static boolean loggingEnabled = true;


    public static void enableLogging() {
        loggingEnabled = true;
        System.setProperty("cache.statistics.logging.enabled", "true");
        logger.info("Cache statistics logging enabled");
    }


    public static void disableLogging() {
        loggingEnabled = false;
        System.setProperty("cache.statistics.logging.enabled", "false");
        logger.info("Cache statistics logging disabled");
    }

    public static boolean isLoggingEnabled() {
        return loggingEnabled;
    }
}

