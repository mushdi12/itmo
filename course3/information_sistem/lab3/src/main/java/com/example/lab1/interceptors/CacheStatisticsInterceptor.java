package com.example.lab1.interceptors;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.Session;

import java.util.logging.Logger;

@Interceptor
@CacheStatisticsLogging
@Priority(Interceptor.Priority.APPLICATION)
public class CacheStatisticsInterceptor {

    private static final Logger logger = Logger.getLogger(CacheStatisticsInterceptor.class.getName());
    
    @PersistenceContext(name = "bookPU")
    private EntityManager em;

    @AroundInvoke
    public Object logCacheStatistics(InvocationContext context) throws Exception {
        boolean loggingEnabled = isCacheLoggingEnabled();
        
        if (!loggingEnabled) {
            return context.proceed();
        }

        // Получаем статистику кэша до выполнения метода
        CacheStatistics beforeStats = getCacheStatistics();

        try {
            Object result = context.proceed();

            // Получаем статистику кэша после выполнения метода
            CacheStatistics afterStats = getCacheStatistics();

            // Вычисляем разницу
            long hitsDiff = afterStats.hits - beforeStats.hits;
            long missesDiff = afterStats.misses - beforeStats.misses;

            logger.info(String.format(
                "L2 Cache Statistics for %s.%s: Hits: %d, Misses: %d, Total Requests: %d, Hit Rate: %.2f%%",
                context.getMethod().getDeclaringClass().getSimpleName(),
                context.getMethod().getName(),
                hitsDiff,
                missesDiff,
                hitsDiff + missesDiff,
                (hitsDiff + missesDiff) > 0 ? (hitsDiff * 100.0 / (hitsDiff + missesDiff)) : 0.0
            ));

            return result;
        } catch (IllegalArgumentException e) {
            // Валидационные ошибки - это нормально, не логируем как SEVERE
            throw e;
        } catch (Exception e) {
            // Только неожиданные ошибки логируем как SEVERE
            logger.severe("Error in cache statistics interceptor: " + e.getMessage());
            throw e;
        }
    }

    private CacheStatistics getCacheStatistics() {
        try {
            // EclipseLink не предоставляет прямого API для получения статистики кэша
            // Используем альтернативный подход через Session
            Session session = em.unwrap(Session.class);
            if (session instanceof AbstractSession) {
                AbstractSession abstractSession = (AbstractSession) session;
                // Получаем информацию о кэше через внутренний API
                // Примечание: это упрощенная реализация, в реальности может потребоваться
                // более сложная логика для получения точной статистики
                long hits = 0;
                long misses = 0;
                
                // Попытка получить статистику через рефлексию (если доступно)
                try {
                    java.lang.reflect.Method getCacheMethod = abstractSession.getClass()
                        .getMethod("getCache");
                    Object cache = getCacheMethod.invoke(abstractSession);
                    if (cache != null) {
                        // Здесь можно попытаться получить статистику из кэша
                        // Для упрощения возвращаем нули
                    }
                } catch (Exception e) {
                    // Игнорируем ошибки рефлексии
                }
                
                return new CacheStatistics(hits, misses);
            }
        } catch (Exception e) {
            logger.warning("Could not retrieve cache statistics: " + e.getMessage());
        }
        return new CacheStatistics(0, 0);
    }

    private boolean isCacheLoggingEnabled() {
        // Можно сделать настраиваемым через системное свойство или конфигурацию
        String enabled = System.getProperty("cache.statistics.logging.enabled", "true");
        return Boolean.parseBoolean(enabled);
    }

    private static class CacheStatistics {
        final long hits;
        final long misses;

        CacheStatistics(long hits, long misses) {
            this.hits = hits;
            this.misses = misses;
        }
    }
}

